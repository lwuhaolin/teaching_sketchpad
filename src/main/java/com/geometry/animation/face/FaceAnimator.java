package com.geometry.animation.face;

import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 09 - Controls face-level animation for geometry transformations.
 *
 * Manages a list of FaceAnimationState objects and produces a
 * new Mesh at any animation progress value by interpolating
 * face vertex positions.
 *
 * The original mesh is NEVER modified.
 *
 * Usage:
 *   1. Create FaceAnimator with a source mesh
 *   2. Add FaceAnimationState entries for each animated face
 *   3. Call getAnimatedMesh(progress) to get the interpolated mesh
 */
public class FaceAnimator {

    private final Mesh sourceMesh;
    private final List<FaceAnimationState> faceStates;
    private final List<FaceTransform> currentTransforms;
    private AnimationState state;

    /**
     * Create a FaceAnimator for the given source mesh.
     *
     * @param sourceMesh the original mesh to animate (not modified)
     */
    public FaceAnimator(Mesh sourceMesh) {
        if (sourceMesh == null) {
            throw new IllegalArgumentException("Source mesh cannot be null");
        }
        this.sourceMesh = sourceMesh;
        this.faceStates = new ArrayList<>();
        this.currentTransforms = new ArrayList<>();
        this.state = AnimationState.READY;
    }

    /**
     * Add a face animation state.
     *
     * @param state the face animation state
     */
    public void addFaceState(FaceAnimationState state) {
        if (state == null) {
            throw new IllegalArgumentException("FaceAnimationState cannot be null");
        }
        this.faceStates.add(state);
    }

    /**
     * Get all face animation states.
     */
    public List<FaceAnimationState> getFaceStates() {
        return new ArrayList<>(faceStates);
    }

    /**
     * Get the number of animated faces.
     */
    public int getFaceCount() {
        return faceStates.size();
    }

    /**
     * Build an animated mesh at the given progress [0, 1].
     * At progress=0 returns the original mesh.
     * At progress=1 returns the fully transformed mesh.
     *
     * @param progress normalized animation progress
     * @return a new Mesh with interpolated vertex positions
     */
    public Mesh getAnimatedMesh(float progress) {
        if (progress <= 0f) {
            return copyMesh(sourceMesh);
        }
        if (progress >= 1f) {
            return buildFinalMesh();
        }

        Mesh result = new Mesh();
        // Build a map from original vertex index to new vertex index
        int[] vertexMap = buildVertexMap(progress, result);

        // Copy all faces, remapping vertex indices
        for (int i = 0; i < sourceMesh.getFaceCount(); i++) {
            Face face = sourceMesh.getFace(i);
            int v0 = vertexMap[face.getVertexIndex(0)];
            int v1 = vertexMap[face.getVertexIndex(1)];
            int v2 = vertexMap[face.getVertexIndex(2)];
            result.addFace(v0, v1, v2);
        }

        return result;
    }

    /**
     * Check if all faces have been assigned animation states.
     */
    public boolean isComplete() {
        return faceStates.size() == sourceMesh.getFaceCount();
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private int[] buildVertexMap(float progress, Mesh result) {
        int vertexCount = sourceMesh.getVertexCount();
        int[] vertexMap = new int[vertexCount];

        // Start with identity mapping (unaffected vertices stay the same)
        for (int i = 0; i < vertexCount; i++) {
            vertexMap[i] = i;
        }

        // Track which original vertices are affected by animation
        boolean[] affected = new boolean[vertexCount];

        // For each animated face, compute displaced vertices
        // and accumulate displacement for shared vertices
        Vec3[] displacementSum = new Vec3[vertexCount];
        int[] displacementCount = new int[vertexCount];

        for (FaceAnimationState faceState : faceStates) {
            Vec3[] displaced = faceState.interpolateVertices(progress);

            Face face = sourceMesh.getFace(faceState.getFaceIndex());
            for (int vi = 0; vi < 3; vi++) {
                int origVertexIdx = face.getVertexIndex(vi);
                Vec3 origPos = sourceMesh.getVertex(origVertexIdx).getPosition();
                Vec3 disp = displaced[vi].subtract(origPos);
                if (displacementSum[origVertexIdx] == null) {
                    displacementSum[origVertexIdx] = Vec3.ZERO;
                    displacementCount[origVertexIdx] = 0;
                }
                displacementSum[origVertexIdx] = displacementSum[origVertexIdx].add(disp);
                displacementCount[origVertexIdx]++;
                affected[origVertexIdx] = true;
            }
        }

        // Apply averaged displacement to affected vertices, add unaffeced ones too
        for (int i = 0; i < vertexCount; i++) {
            if (affected[i] && displacementCount[i] > 0 && displacementSum[i] != null) {
                Vec3 avgDisp = displacementSum[i].multiply(1f / displacementCount[i]);
                Vec3 newPos = sourceMesh.getVertex(i).getPosition().add(avgDisp);
                int newIndex = result.addVertex(new Vertex(newPos));
                vertexMap[i] = newIndex;
            } else {
                // Unaffected vertex — copy as-is
                int newIndex = result.addVertex(new Vertex(sourceMesh.getVertex(i).getPosition()));
                vertexMap[i] = newIndex;
            }
        }

        return vertexMap;
    }

    private Mesh buildFinalMesh() {
        if (faceStates.isEmpty()) {
            return copyMesh(sourceMesh);
        }

        // Build a map from original vertex index to new (potentially displaced) vertex index
        Mesh result = new Mesh();
        int vertexCount = sourceMesh.getVertexCount();
        int[] vertexMap = new int[vertexCount];

        // Track which vertices are affected
        boolean[] affected = new boolean[vertexCount];
        Vec3[] displacementSum = new Vec3[vertexCount];
        int[] displacementCount = new int[vertexCount];

        for (FaceAnimationState faceState : faceStates) {
            Vec3[] endVerts = faceState.getEndVertices();
            Face face = sourceMesh.getFace(faceState.getFaceIndex());
            for (int vi = 0; vi < 3; vi++) {
                int origVertexIdx = face.getVertexIndex(vi);
                Vec3 origPos = sourceMesh.getVertex(origVertexIdx).getPosition();
                Vec3 disp = endVerts[vi].subtract(origPos);
                if (displacementSum[origVertexIdx] == null) {
                    displacementSum[origVertexIdx] = Vec3.ZERO;
                    displacementCount[origVertexIdx] = 0;
                }
                displacementSum[origVertexIdx] = displacementSum[origVertexIdx].add(disp);
                displacementCount[origVertexIdx]++;
                affected[origVertexIdx] = true;
            }
        }

        // Map each vertex: affected ones get displaced, others keep original
        for (int i = 0; i < vertexCount; i++) {
            if (affected[i] && displacementCount[i] > 0) {
                Vec3 avgDisp = displacementSum[i].multiply(1f / displacementCount[i]);
                Vec3 newPos = sourceMesh.getVertex(i).getPosition().add(avgDisp);
                vertexMap[i] = result.addVertex(new Vertex(newPos));
            } else {
                vertexMap[i] = result.addVertex(new Vertex(sourceMesh.getVertex(i).getPosition()));
            }
        }

        // Copy all faces with remapped indices
        for (int i = 0; i < sourceMesh.getFaceCount(); i++) {
            Face face = sourceMesh.getFace(i);
            result.addFace(vertexMap[face.getVertexIndex(0)],
                    vertexMap[face.getVertexIndex(1)],
                    vertexMap[face.getVertexIndex(2)]);
        }
        return result;
    }

    private Mesh copyMesh(Mesh source) {
        Mesh result = new Mesh();
        for (Vertex v : source.getVertices()) {
            result.addVertex(new Vertex(v.getPosition(), v.getNormal(), v.getUv()));
        }
        for (Face f : source.getFaces()) {
            result.addFace(f.getVertexIndex(0), f.getVertexIndex(1), f.getVertexIndex(2));
        }
        return result;
    }
}
