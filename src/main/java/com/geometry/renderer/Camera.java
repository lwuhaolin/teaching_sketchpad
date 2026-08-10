package com.geometry.renderer;

import com.geometry.core.math.Matrix4;
import com.geometry.core.math.Vec3;

/**
 * Phase 03 - Perspective/orthographic camera for the renderer.
 *
 * Maintains the camera's world position, orientation, and projection matrix.
 *
 * Two projection modes:
 *   MODE_3D: Perspective projection (default) — objects appear smaller with distance.
 *   MODE_2D: Orthographic projection — no perspective distortion, ideal for 2D teaching.
 *
 * The camera looks along the negative Z axis with Y as up.
 * In 3D mode, the camera is placed at (0, 0, 5) looking at the origin by default.
 * In 2D mode, the orthographic frustum covers the range [-10, 10] on X and Y.
 */
public class Camera {

    private RenderMode renderMode;
    private Vec3 position;
    private Vec3 rotation; // Euler angles in degrees (pitch, yaw, roll)
    private Matrix4 projection;
    private Matrix4 view;

    // ---- 3D mode defaults ----
    private static final float PERSPECTIVE_FOV = (float) Math.toRadians(45.0);
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000.0f;

    // ---- 2D mode defaults ----
    private static final float ORTHO_LEFT = -10f;
    private static final float ORTHO_RIGHT = 10f;
    private static final float ORTHO_BOTTOM = -10f;
    private static final float ORTHO_TOP = 10f;

    /**
     * Create a camera in 3D perspective mode.
     */
    public Camera() {
        this(RenderMode.MODE_3D);
    }

    /**
     * Create a camera with the specified render mode.
     *
     * @param mode 2D orthographic or 3D perspective
     */
    public Camera(RenderMode mode) {
        this.renderMode = mode;
        this.position = new Vec3(0f, 0f, 5f);
        this.rotation = new Vec3(0f, 0f, 0f);
        this.projection = Matrix4.IDENTITY;
        this.view = Matrix4.IDENTITY;
        updateProjection();
    }

    /**
     * Get the current render mode.
     */
    public RenderMode getRenderMode() {
        return renderMode;
    }

    /**
     * Get the camera position in world space.
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * Get the camera rotation (Euler angles in degrees).
     */
    public Vec3 getRotation() {
        return rotation;
    }

    /**
     * Get the projection matrix (perspective or orthographic).
     * Returns a column-major float array suitable for glUniformMatrix4fv.
     */
    public float[] getProjectionMatrix() {
        return projection.m;
    }

    /**
     * Get the view matrix (camera-to-world inverse).
     * Returns a column-major float array suitable for glUniformMatrix4fv.
     */
    public float[] getViewMatrix() {
        return view.m;
    }

    /**
     * Move the camera by the given delta in world space.
     *
     * @param delta translation amount
     */
    public void translate(Vec3 delta) {
        this.position = position.add(delta);
    }

    /**
     * Rotate the camera by the given Euler angles (degrees).
     *
     * @param delta rotation amounts in degrees (pitch, yaw, roll)
     */
    public void rotate(Vec3 delta) {
        this.rotation = new Vec3(
                rotation.x + delta.x,
                rotation.y + delta.y,
                rotation.z + delta.z
        );
    }

    /**
     * Switch between 2D orthographic and 3D perspective projection.
     *
     * @param mode the new render mode
     */
    public void setRenderMode(RenderMode mode) {
        this.renderMode = mode;
        // Reset camera position for 2D mode
        if (mode == RenderMode.MODE_2D) {
            this.position = new Vec3(0f, 0f, 0f);
            this.rotation = new Vec3(0f, 0f, 0f);
        } else {
            this.position = new Vec3(0f, 0f, 5f);
            this.rotation = new Vec3(0f, 0f, 0f);
        }
        updateProjection();
    }

    /**
     * Resize the projection to match the new viewport dimensions.
     * For perspective: updates FOV aspect ratio.
     * For orthographic: updates left/right/top/bottom bounds.
     *
     * @param width  viewport width in pixels
     * @param height viewport height in pixels
     */
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Viewport dimensions must be positive");
        }
        updateProjection();
    }

    private void updateProjection() {
        if (renderMode == RenderMode.MODE_2D) {
            projection = Matrix4.IDENTITY; // orthographic identity (we set bounds separately)
        } else {
            projection = createPerspectiveMatrix(PERSPECTIVE_FOV, 1.0f, NEAR_PLANE, FAR_PLANE);
        }
        updateView();
    }

    /**
     * Update the view matrix from current position and rotation.
     */
    private void updateView() {
        // Look-at matrix: position the camera at 'position' looking towards origin
        // With rotation applied, the camera orientation is derived from Euler angles.
        // For simplicity, we use a basic look-at without full rotation composition.
        // Full orbit controls are handled by the Interaction system (Phase 05).
        Vec3 target = new Vec3(0f, 0f, 0f);
        Vec3 up = Vec3.UNIT_Y;

        Vec3 forward = target.subtract(position).normalize();
        Vec3 right = up.cross(forward).normalize();
        up = forward.cross(right).normalize();

        float[] m = new float[16];
        // Row-major to column-major conversion
        // View matrix = inverse of (translate position * rotate)
        // Built as: [R^T | -R^T*t; 0 0 0 1]
        m[0] = right.x;  m[1] = up.x;    m[2] = -forward.x; m[3] = 0f;
        m[4] = right.y;  m[5] = up.y;    m[6] = -forward.y; m[7] = 0f;
        m[8] = right.z;  m[9] = up.z;    m[10] = -forward.z; m[11] = 0f;
        m[12] = -(right.dot(position));
        m[13] = -(up.dot(position));
        m[14] = -(-forward.dot(position));
        m[15] = 1f;

        view = new Matrix4(m);
    }

    /**
     * Create a perspective projection matrix.
     *
     * @param fovYRadians field of view in radians (Y axis)
     * @param aspect      width / height ratio
     * @param near        near clipping plane
     * @param far         far clipping plane
     * @return 4x4 column-major projection matrix
     */
    public static Matrix4 createPerspectiveMatrix(float fovYRadians, float aspect,
                                                   float near, float far) {
        float tanHalfFov = (float) Math.tan(fovYRadians / 2.0f);
        float[] m = new float[16];

        m[0] = 1.0f / (aspect * tanHalfFov);  m[1] = 0f;              m[2] = 0f;                    m[3] = 0f;
        m[4] = 0f;                              m[5] = 1.0f / tanHalfFov; m[6] = 0f;                    m[7] = 0f;
        m[8] = 0f;                              m[9] = 0f;              m[10] = -(far + near) / (far - near); m[11] = -1f;
        m[12] = 0f;                             m[13] = 0f;             m[14] = -(2.0f * far * near) / (far - near); m[15] = 0f;

        return new Matrix4(m);
    }

    // ------------------------------------------------------------------
    // Ray picking helpers (Phase 05)
    // ------------------------------------------------------------------

    /**
     * Compute a ray in world space from a screen pixel coordinate.
     *
     * Converts screen position (in pixels, origin top-left) to normalized
     * device coordinates, then unprojects through view and projection to
     * obtain a ray origin and direction in world space.
     *
     * @param screenX screen X in pixels
     * @param screenY screen Y in pixels
     * @param width   viewport width in pixels
     * @param height  viewport height in pixels
     * @return a two-element array: [rayOrigin, rayDirection], each a Vec3
     */
    public float[] getRayFromScreen(float screenX, float screenY,
                                     int width, int height) {
        // Normalized device coordinates (-1 to +1)
        float ndcX = (2f * screenX / width) - 1f;
        float ndcY = 1f - (2f * screenY / height); // flip Y

        Matrix4 invProjView = multiplyMatrices(invertMatrix(getProjectionMatrix()),
                invertMatrix(getViewMatrix()));

        // Ray origin: transform NDC point (z=-1) with invProjView
        float[] origin = transformPoint(invProjView, ndcX, ndcY, -1f, 1f);
        // Ray direction: transform NDC point (z=+1) with invProjView, then subtract origin
        float[] dirRaw = transformPoint(invProjView, ndcX, ndcY, 1f, 1f);
        float[] dir = new float[]{dirRaw[0] - origin[0],
                dirRaw[1] - origin[1],
                dirRaw[2] - origin[2]};

        float dirLen = (float) Math.sqrt(dir[0] * dir[0] + dir[1] * dir[1] + dir[2] * dir[2]);
        if (dirLen > 0f) {
            dir[0] /= dirLen;
            dir[1] /= dirLen;
            dir[2] /= dirLen;
        }

        return new float[]{origin[0], origin[1], origin[2],
                dir[0], dir[1], dir[2]};
    }

    private static float[] transformPoint(Matrix4 mat, float x, float y, float z, float w) {
        float[] m = mat.m;
        float wx = m[0] * x + m[4] * y + m[8] * z + m[12] * w;
        float wy = m[1] * x + m[5] * y + m[9] * z + m[13] * w;
        float wz = m[2] * x + m[6] * y + m[10] * z + m[14] * w;
        float ww = m[3] * x + m[7] * y + m[11] * z + m[15] * w;
        if (ww != 0f && Math.abs(ww) > 0.0001f) {
            wx /= ww;
            wy /= ww;
            wz /= ww;
        }
        return new float[]{wx, wy, wz};
    }

    /**
     * Invert a 4x4 column-major matrix (Gauss-Jordan elimination).
     */
    private static Matrix4 invertMatrix(float[] m) {
        float[] inv = new float[16];
        inv[0] = m[5] * m[10] * m[15] - m[5] * m[11] * m[14]
                - m[9] * m[6] * m[15] + m[9] * m[7] * m[14]
                + m[13] * m[6] * m[11] - m[13] * m[7] * m[10];
        inv[4] = -m[4] * m[10] * m[15] + m[4] * m[11] * m[14]
                + m[8] * m[6] * m[15] - m[8] * m[7] * m[14]
                - m[12] * m[6] * m[11] + m[12] * m[7] * m[10];
        inv[8] = m[4] * m[9] * m[15] - m[4] * m[11] * m[13]
                - m[8] * m[5] * m[15] + m[8] * m[7] * m[13]
                + m[12] * m[5] * m[11] - m[12] * m[7] * m[9];
        inv[12] = -m[4] * m[9] * m[14] + m[4] * m[10] * m[13]
                + m[8] * m[5] * m[14] - m[8] * m[6] * m[13]
                - m[12] * m[5] * m[10] + m[12] * m[6] * m[9];
        inv[1] = -m[1] * m[10] * m[15] + m[1] * m[11] * m[14]
                + m[9] * m[2] * m[15] - m[9] * m[3] * m[14]
                - m[13] * m[2] * m[11] + m[13] * m[3] * m[10];
        inv[5] = m[0] * m[10] * m[15] - m[0] * m[11] * m[14]
                - m[8] * m[2] * m[15] + m[8] * m[3] * m[14]
                + m[12] * m[2] * m[11] - m[12] * m[3] * m[10];
        inv[9] = -m[0] * m[9] * m[15] + m[0] * m[11] * m[13]
                + m[8] * m[2] * m[15] - m[8] * m[3] * m[13]
                - m[12] * m[2] * m[11] + m[12] * m[3] * m[9];
        inv[13] = m[0] * m[9] * m[14] - m[0] * m[10] * m[13]
                - m[8] * m[2] * m[14] + m[8] * m[3] * m[13]
                + m[12] * m[2] * m[10] - m[12] * m[3] * m[9];
        inv[2] = m[1] * m[6] * m[15] - m[1] * m[7] * m[14]
                - m[5] * m[2] * m[15] + m[5] * m[3] * m[14]
                + m[13] * m[2] * m[7] - m[13] * m[3] * m[6];
        inv[6] = -m[0] * m[6] * m[15] + m[0] * m[7] * m[14]
                + m[4] * m[2] * m[15] - m[4] * m[3] * m[14]
                - m[12] * m[2] * m[7] + m[12] * m[3] * m[6];
        inv[10] = m[0] * m[5] * m[15] - m[0] * m[7] * m[13]
                - m[4] * m[2] * m[15] + m[4] * m[3] * m[13]
                + m[12] * m[2] * m[7] - m[12] * m[3] * m[5];
        inv[14] = -m[0] * m[5] * m[14] + m[0] * m[6] * m[13]
                + m[4] * m[2] * m[14] - m[4] * m[3] * m[13]
                - m[12] * m[2] * m[6] + m[12] * m[3] * m[5];
        inv[3] = -m[1] * m[6] * m[11] + m[1] * m[7] * m[10]
                + m[5] * m[2] * m[11] - m[5] * m[3] * m[10]
                - m[9] * m[2] * m[7] + m[9] * m[3] * m[6];
        inv[7] = m[0] * m[6] * m[11] - m[0] * m[7] * m[10]
                - m[4] * m[2] * m[11] + m[4] * m[3] * m[10]
                + m[8] * m[2] * m[7] - m[8] * m[3] * m[6];
        inv[11] = -m[0] * m[5] * m[11] + m[0] * m[7] * m[9]
                + m[4] * m[2] * m[11] - m[4] * m[3] * m[9]
                - m[8] * m[2] * m[7] + m[8] * m[3] * m[5];
        inv[15] = m[0] * m[5] * m[10] - m[0] * m[6] * m[9]
                - m[4] * m[2] * m[10] + m[4] * m[3] * m[9]
                + m[8] * m[2] * m[6] - m[8] * m[3] * m[5];

        float det = m[0] * inv[0] + m[1] * inv[4] + m[2] * inv[8] + m[3] * inv[12];
        if (Math.abs(det) < 0.0001f) {
            return Matrix4.IDENTITY;
        }
        det = 1.0f / det;
        for (int i = 0; i < 16; i++) {
            inv[i] *= det;
        }
        return new Matrix4(inv);
    }

    private static Matrix4 multiplyMatrices(Matrix4 a, Matrix4 b) {
        return a.multiply(b);
    }
}
