package com.geometry.animation.face;

import com.geometry.core.math.Vec3;

/**
 * Phase 09 - Represents a transformed face during geometry animation.
 *
 * Stores the position of each vertex after applying the face-level
 * transformation (e.g. during unfold or explode animations).
 * The original mesh is never modified — this creates a separate
 * target mesh for rendering the animated state.
 *
 * Used by FaceAnimator to build animated mesh snapshots.
 */
public class FaceTransform {

    private final int faceIndex;
    private final Vec3[] displacedVertices;

    /**
     * Create a FaceTransform.
     *
     * @param faceIndex        index of the face in the original mesh
     * @param displacedVertices transformed vertex positions (same count as face vertices)
     */
    public FaceTransform(int faceIndex, Vec3[] displacedVertices) {
        if (displacedVertices == null || displacedVertices.length != 3) {
            throw new IllegalArgumentException("FaceTransform requires exactly 3 displaced vertices");
        }
        this.faceIndex = faceIndex;
        this.displacedVertices = displacedVertices.clone();
    }

    /**
     * Get the face index in the original mesh.
     */
    public int getFaceIndex() {
        return faceIndex;
    }

    /**
     * Get the displaced vertex positions.
     */
    public Vec3[] getDisplacedVertices() {
        return displacedVertices.clone();
    }

    /**
     * Get a specific displaced vertex.
     *
     * @param i vertex index within the face (0, 1, or 2)
     */
    public Vec3 getDisplacedVertex(int i) {
        return displacedVertices[i];
    }

    @Override
    public String toString() {
        return "FaceTransform{face=" + faceIndex
                + ", v0=" + displacedVertices[0]
                + ", v1=" + displacedVertices[1]
                + ", v2=" + displacedVertices[2] + "}";
    }
}
