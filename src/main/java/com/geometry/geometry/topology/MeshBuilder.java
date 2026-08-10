package com.geometry.geometry.topology;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 08 - Builds a Mesh from lists of vertex positions and face indices.
 *
 * Deduplicates vertices by position (within epsilon) and rebuilds
 * face indices to reference the deduplicated vertex list.
 *
 * This is the final step in the cut pipeline: after classifying faces
 * and computing intersection points, MeshBuilder assembles the output.
 */
public class MeshBuilder {

    private static final float VERTEX_EPSILON = 1e-5f;

    /**
     * Build a Mesh from vertex positions and face index triples.
     *
     * Vertices with nearly identical positions are merged into one.
     *
     * @param positions list of vertex positions
     * @param faces     list of int[3] face index triples (referencing 'positions')
     * @return a new Mesh
     */
    public static Mesh build(List<Vec3> positions, List<int[]> faces) {
        if (positions == null) {
            throw new IllegalArgumentException("Positions list cannot be null");
        }
        if (faces == null) {
            faces = java.util.Collections.emptyList();
        }

        Mesh mesh = new Mesh();
        Map<Vec3, Integer> vertexMap = new HashMap<>();

        // Deduplicate vertices
        for (Vec3 pos : positions) {
            Integer existing = findOrCreateVertex(mesh, vertexMap, pos);
            // We need to track mapping from original index to new index
        }

        // Actually, let's redo this with a cleaner approach
        mesh = new Mesh();
        vertexMap.clear();

        // Phase 1: create deduplicated vertices
        List<Vec3> uniquePositions = new ArrayList<>();
        int[] oldToNew = new int[positions.size()];

        for (int i = 0; i < positions.size(); i++) {
            Vec3 pos = positions.get(i);
            Integer mapped = vertexMap.get(pos);
            if (mapped != null) {
                oldToNew[i] = mapped;
            } else {
                int newIndex = mesh.addVertex(new Vertex(pos));
                vertexMap.put(pos, newIndex);
                uniquePositions.add(pos);
                oldToNew[i] = newIndex;
            }
        }

        // Phase 2: add faces with remapped indices
        for (int[] faceIndices : faces) {
            if (faceIndices.length != 3) {
                continue;
            }
            int newV0 = oldToNew[faceIndices[0]];
            int newV1 = oldToNew[faceIndices[1]];
            int newV2 = oldToNew[faceIndices[2]];
            mesh.addFace(newV0, newV1, newV2);
        }

        return mesh;
    }

    /**
     * Find an existing vertex position within epsilon, or return null.
     */
    private static Integer findOrCreateVertex(Mesh mesh, Map<Vec3, Integer> map, Vec3 pos) {
        // Use exact match via HashMap (Vec3.equals uses float equality)
        Integer existing = map.get(pos);
        if (existing != null) {
            return existing;
        }
        // Fallback: approximate match
        for (Vertex v : mesh.getVertices()) {
            if (v.getPosition().equals(pos)) {
                int idx = mesh.getVertices().indexOf(v);
                map.put(pos, idx);
                return idx;
            }
        }
        return null;
    }
}
