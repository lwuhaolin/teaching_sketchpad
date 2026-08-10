package com.geometry.animation.face;

import com.geometry.core.math.Vec3;

/**
 * Phase 09 - Animation state for a single mesh face.
 *
 * Tracks the start and end transform parameters for a face
 * during unfold/explode animations. The animation system uses
 * these to compute intermediate face positions.
 *
 * Design: Face-level animation is necessary because unfold
 * animations cannot be represented by a single Transform on
 * the whole Mesh — each face moves independently.
 */
public class FaceAnimationState {

    private final int faceIndex;
    private final Vec3 startPosition;
    private final Vec3 endPosition;
    private final Vec3 startNormal;
    private final Vec3 endNormal;
    private final Vec3[] startVertices;
    private final Vec3[] endVertices;

    /**
     * Create a FaceAnimationState.
     *
     * @param faceIndex index of the face
     * @param startVertices starting vertex positions (3 vertices)
     * @param endVertices   ending vertex positions (3 vertices)
     */
    public FaceAnimationState(int faceIndex, Vec3[] startVertices, Vec3[] endVertices) {
        if (startVertices == null || endVertices == null
                || startVertices.length != 3 || endVertices.length != 3) {
            throw new IllegalArgumentException("FaceAnimationState requires 3 start and 3 end vertices");
        }
        this.faceIndex = faceIndex;
        this.startVertices = startVertices.clone();
        this.endVertices = endVertices.clone();
        this.startPosition = computeCentroid(startVertices);
        this.endPosition = computeCentroid(endVertices);
        this.startNormal = computeNormal(startVertices);
        this.endNormal = computeNormal(endVertices);
    }

    /**
     * Get the face index.
     */
    public int getFaceIndex() {
        return faceIndex;
    }

    /**
     * Get the start centroid position.
     */
    public Vec3 getStartPosition() {
        return startPosition;
    }

    /**
     * Get the end centroid position.
     */
    public Vec3 getEndPosition() {
        return endPosition;
    }

    /**
     * Get the start normal vector.
     */
    public Vec3 getStartNormal() {
        return startNormal;
    }

    /**
     * Get the end normal vector.
     */
    public Vec3 getEndNormal() {
        return endNormal;
    }

    /**
     * Get the starting vertex positions.
     */
    public Vec3[] getStartVertices() {
        return startVertices.clone();
    }

    /**
     * Get the ending vertex positions.
     */
    public Vec3[] getEndVertices() {
        return endVertices.clone();
    }

    /**
     * Interpolate vertex positions at the given progress [0, 1].
     *
     * @param progress normalized progress
     * @return array of 3 interpolated vertex positions
     */
    public Vec3[] interpolateVertices(float progress) {
        Vec3[] result = new Vec3[3];
        for (int i = 0; i < 3; i++) {
            result[i] = lerp(startVertices[i], endVertices[i], progress);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private static Vec3 computeCentroid(Vec3[] vertices) {
        float cx = 0f, cy = 0f, cz = 0f;
        for (Vec3 v : vertices) {
            cx += v.x;
            cy += v.y;
            cz += v.z;
        }
        return new Vec3(cx / 3f, cy / 3f, cz / 3f);
    }

    private static Vec3 computeNormal(Vec3[] vertices) {
        Vec3 edge1 = vertices[1].subtract(vertices[0]);
        Vec3 edge2 = vertices[2].subtract(vertices[0]);
        return edge1.cross(edge2).normalize();
    }

    private static Vec3 lerp(Vec3 a, Vec3 b, float t) {
        return new Vec3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }
}
