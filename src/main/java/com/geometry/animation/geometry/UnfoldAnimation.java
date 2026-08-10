package com.geometry.animation.geometry;

import com.geometry.animation.AnimationState;
import com.geometry.animation.face.FaceAnimationState;
import com.geometry.animation.face.FaceAnimator;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;

/**
 * Phase 09 - Cube unfold animation.
 *
 * Unfolds a cube mesh into its 2D net (6-square cross pattern).
 * Each face rotates around its shared edge with the base face,
 * creating the classic cross-shaped展开图.
 *
 * Face mapping for a cube:
 *   0,1 - Front
 *   2,3 - Back
 *   4,5 - Top
 *   6,7 - Bottom
 *   8,9 - Right
 *  10,11 - Left
 */
public class UnfoldAnimation extends GeometryAnimation {

    private final UnfoldType unfoldType;

    /**
     * Types of unfold animations.
     */
    public enum UnfoldType {
        /** Unfold cube faces into a cross pattern. */
        CUBE,
        /** Unfold cylinder side into a rectangle (caps stay attached). */
        CYLINDER,
        /** Unfold cone side into a sector. */
        CONE
    }

    private float unfoldProgress;

    /**
     * Create a Cube unfold animation.
     *
     * @param mesh         the cube mesh to unfold
     * @param duration     animation duration in seconds
     * @param unfoldType   type of unfold (CUBE, CYLINDER, or CONE)
     * @param interpolator easing function
     */
    public UnfoldAnimation(Mesh mesh, float duration, UnfoldType unfoldType,
                           Interpolator interpolator) {
        super(mesh, duration, interpolator);
        this.unfoldType = unfoldType;
        this.unfoldProgress = 0f;
    }

    /**
     * Create a Cube unfold animation with linear interpolation.
     */
    public UnfoldAnimation(Mesh mesh, float duration, UnfoldType unfoldType) {
        this(mesh, duration, unfoldType, null);
    }

    @Override
    protected void resetAnimation() {
        this.unfoldProgress = 0f;
        faceAnimator = new FaceAnimator(sourceMesh);
    }

    @Override
    public void onAnimate(float easedProgress) {
        this.unfoldProgress = easedProgress;
        faceAnimator = new FaceAnimator(sourceMesh);

        if (unfoldType == UnfoldType.CUBE) {
            setupCubeUnfold(easedProgress);
        } else if (unfoldType == UnfoldType.CYLINDER) {
            setupCylinderUnfold(easedProgress);
        } else if (unfoldType == UnfoldType.CONE) {
            setupConeUnfold(easedProgress);
        }
    }

    // ------------------------------------------------------------------
    // Cube unfold logic
    // ------------------------------------------------------------------

    private void setupCubeUnfold(float progress) {
        // Cube has 12 faces (2 triangles per side, 6 sides)
        // Group by side: each side has 2 triangles
        // Use face pairs to represent each cube face
        int facePairs = sourceMesh.getFaceCount() / 2;

        // Cube side offsets for unfolding into cross pattern
        // Base: front face stays in place
        // Top, Bottom, Left, Right unfold outward
        // Back unfolds from top
        Vec3[] basePositions = computeBasePositions();

        // Define unfold directions for each face pair
        // Face pair indices: 0=front,1=back,2=top,3=bottom,4=right,5=left
        Vec3[] unfoldDirections = {
                Vec3.ZERO,                              // front (base)
                new Vec3(0f, 2f, 0f),                  // back (unfold up from top)
                new Vec3(0f, 1f, 0f),                  // top
                new Vec3(0f, -1f, 0f),                 // bottom
                new Vec3(1f, 0f, 0f),                  // right
                new Vec3(-1f, 0f, 0f)                  // left
        };

        for (int pair = 0; pair < facePairs; pair++) {
            int baseFaceIdx = pair * 2;
            if (baseFaceIdx + 1 >= sourceMesh.getFaceCount()) {
                break;
            }

            Face face0 = sourceMesh.getFace(baseFaceIdx);
            Face face1 = sourceMesh.getFace(baseFaceIdx + 1);

            Vec3[] startVerts0 = getFaceVertices(face0);
            Vec3[] startVerts1 = getFaceVertices(face1);

            // Compute target positions with unfold
            Vec3 unfoldOffset = unfoldDirections[Math.min(pair, unfoldDirections.length - 1)]
                    .multiply(progress);

            Vec3[] endVerts0 = new Vec3[3];
            Vec3[] endVerts1 = new Vec3[3];
            for (int v = 0; v < 3; v++) {
                endVerts0[v] = startVerts0[v].add(unfoldOffset);
                endVerts1[v] = startVerts1[v].add(unfoldOffset);
            }

            faceAnimator.addFaceState(new FaceAnimationState(baseFaceIdx, startVerts0, endVerts0));
            faceAnimator.addFaceState(new FaceAnimationState(baseFaceIdx + 1, startVerts1, endVerts1));
        }
    }

    // ------------------------------------------------------------------
    // Cylinder unfold logic
    // ------------------------------------------------------------------

    private void setupCylinderUnfold(float progress) {
        // Cylinder: separate side panels and unfold them
        // Side panels are pairs of triangles between bottom and top rings
        int totalFaces = sourceMesh.getFaceCount();
        // Bottom cap: segments faces, Top cap: segments faces, Side: 2*segments faces
        int segCount = (totalFaces - 2) / 3; // approximate
        if (segCount < 3) segCount = 16; // default for standard cylinder

        int bottomCapFaces = segCount;
        int topCapFaces = segCount;
        int sideStart = bottomCapFaces;
        int sideEnd = sideStart + 2 * segCount;

        // Unfold side panels: each pair of triangles forms a rectangular panel
        for (int i = sideStart; i < sideEnd; i += 2) {
            if (i + 1 >= totalFaces) break;

            Face face0 = sourceMesh.getFace(i);
            Face face1 = sourceMesh.getFace(i + 1);

            Vec3[] startVerts0 = getFaceVertices(face0);
            Vec3[] startVerts1 = getFaceVertices(face1);

            // Calculate unfold position: spread panels horizontally
            int panelIndex = (i - sideStart) / 2;
            float angleStep = 2f * (float) Math.PI / segCount;
            float unfoldAngle = angleStep * panelIndex * progress;

            // Displace each vertex based on unfold angle
            Vec3[] endVerts0 = new Vec3[3];
            Vec3[] endVerts1 = new Vec3[3];
            for (int v = 0; v < 3; v++) {
                Vec3 origPos = startVerts0[v];
                // Unfold: rotate around bottom edge
                float x = origPos.x * (float) Math.cos(unfoldAngle)
                        - origPos.z * (float) Math.sin(unfoldAngle);
                float z = origPos.x * (float) Math.sin(unfoldAngle)
                        + origPos.z * (float) Math.cos(unfoldAngle);
                endVerts0[v] = new Vec3(x, origPos.y, z);
                endVerts1[v] = endVerts0[v];
            }

            faceAnimator.addFaceState(new FaceAnimationState(i, startVerts0, endVerts0));
            faceAnimator.addFaceState(new FaceAnimationState(i + 1, startVerts1, endVerts1));
        }
    }

    // ------------------------------------------------------------------
    // Cone unfold logic
    // ------------------------------------------------------------------

    private void setupConeUnfold(float progress) {
        // Cone: unfold side triangles into a sector
        int totalFaces = sourceMesh.getFaceCount();
        int sideFaces = totalFaces / 2; // approximate
        if (sideFaces < 3) sideFaces = 16;

        // Unfold cone side triangles around apex
        for (int i = 0; i < sideFaces; i++) {
            Face face = sourceMesh.getFace(i);
            Vec3[] startVerts = getFaceVertices(face);

            // Calculate unfold angle for this triangle
            float angleStep = 2f * (float) Math.PI / sourceMesh.getVertexCount();
            float unfoldAngle = angleStep * i * progress;

            Vec3[] endVerts = new Vec3[3];
            for (int v = 0; v < 3; v++) {
                Vec3 origPos = startVerts[v];
                if (v == 0) {
                    // Apex stays fixed
                    endVerts[v] = origPos;
                } else {
                    // Base vertices rotate outward
                    float x = origPos.x * (float) Math.cos(unfoldAngle)
                            - origPos.z * (float) Math.sin(unfoldAngle);
                    float z = origPos.x * (float) Math.sin(unfoldAngle)
                            + origPos.z * (float) Math.cos(unfoldAngle);
                    endVerts[v] = new Vec3(x, origPos.y, z);
                }
            }

            faceAnimator.addFaceState(new FaceAnimationState(i, startVerts, endVerts));
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Vec3[] getFaceVertices(Face face) {
        Vec3[] verts = new Vec3[3];
        for (int i = 0; i < 3; i++) {
            verts[i] = sourceMesh.getVertex(face.getVertexIndex(i)).getPosition();
        }
        return verts;
    }

    private Vec3[] computeBasePositions() {
        // Compute centroid positions for all faces
        Vec3[] centroids = new Vec3[sourceMesh.getFaceCount()];
        for (int i = 0; i < sourceMesh.getFaceCount(); i++) {
            Face face = sourceMesh.getFace(i);
            float cx = 0, cy = 0, cz = 0;
            for (int v = 0; v < 3; v++) {
                Vec3 pos = sourceMesh.getVertex(face.getVertexIndex(v)).getPosition();
                cx += pos.x;
                cy += pos.y;
                cz += pos.z;
            }
            centroids[i] = new Vec3(cx / 3f, cy / 3f, cz / 3f);
        }
        return centroids;
    }
}
