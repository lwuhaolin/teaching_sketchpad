package com.geometry.animation.geometry;

import com.geometry.animation.AnimationState;
import com.geometry.animation.face.FaceAnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;

/**
 * Phase 09 - Explode animation for geometry objects.
 *
 * Separates each face of a mesh outward from its centroid,
 * creating a "deconstructed" view useful for teaching
 * geometry structure.
 *
 * Example: Explode a cube to show its six faces separated.
 */
public class ExplodeAnimation extends GeometryAnimation {

    private final Vec3[] originalCentroids;
    private final Vec3[] faceNormals;
    private final float explodeFactor;

    /**
     * Create an ExplodeAnimation.
     *
     * @param mesh         the mesh to explode
     * @param duration     animation duration in seconds
     * @param explodeFactor distance each face moves from center (default 1.0f)
     * @param interpolator easing function
     */
    public ExplodeAnimation(Mesh mesh, float duration, float explodeFactor,
                            Interpolator interpolator) {
        super(mesh, duration, interpolator);
        this.explodeFactor = explodeFactor;
        this.originalCentroids = new Vec3[mesh.getFaceCount()];
        this.faceNormals = new Vec3[mesh.getFaceCount()];
        computeFaceProperties();
    }

    /**
     * Create an ExplodeAnimation with default explode factor.
     */
    public ExplodeAnimation(Mesh mesh, float duration, Interpolator interpolator) {
        this(mesh, duration, 1.0f, interpolator);
    }

    /**
     * Create an ExplodeAnimation with linear interpolation.
     */
    public ExplodeAnimation(Mesh mesh, float duration) {
        this(mesh, duration, 1.0f, null);
    }

    @Override
    protected void resetAnimation() {
        // Nothing extra to reset
    }

    @Override
    public void onAnimate(float easedProgress) {
        // Compute exploded positions for each face
        for (int i = 0; i < sourceMesh.getFaceCount(); i++) {
            Face face = sourceMesh.getFace(i);
            Vec3 centroid = originalCentroids[i];
            Vec3 normal = faceNormals[i];

            // End positions: each vertex displaced outward
            Vec3[] endVertices = new Vec3[3];
            for (int v = 0; v < 3; v++) {
                Vec3 vertexPos = sourceMesh.getVertex(face.getVertexIndex(v)).getPosition();
                Vec3 direction = vertexPos.subtract(centroid).normalize();
                float dist = vertexPos.subtract(centroid).length();
                float explodeDist = dist + explodeFactor * easedProgress;
                endVertices[v] = centroid.add(direction.multiply(explodeDist));
            }

            // Create or update FaceAnimationState for interpolation
            Vec3[] startVertices = new Vec3[3];
            for (int v = 0; v < 3; v++) {
                startVertices[v] = sourceMesh.getVertex(face.getVertexIndex(v)).getPosition();
            }

            FaceAnimationState state = new FaceAnimationState(i, startVertices, endVertices);
            faceAnimator.addFaceState(state);
        }
    }

    private void computeFaceProperties() {
        for (int i = 0; i < sourceMesh.getFaceCount(); i++) {
            Face face = sourceMesh.getFace(i);
            Vec3[] verts = new Vec3[3];
            float cx = 0f, cy = 0f, cz = 0f;
            for (int v = 0; v < 3; v++) {
                Vec3 pos = sourceMesh.getVertex(face.getVertexIndex(v)).getPosition();
                verts[v] = pos;
                cx += pos.x;
                cy += pos.y;
                cz += pos.z;
            }
            originalCentroids[i] = new Vec3(cx / 3f, cy / 3f, cz / 3f);
            Vec3 edge1 = verts[1].subtract(verts[0]);
            Vec3 edge2 = verts[2].subtract(verts[0]);
            faceNormals[i] = edge1.cross(edge2).normalize();
        }
    }
}
