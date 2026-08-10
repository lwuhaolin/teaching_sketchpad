package com.geometry.core.mesh;

/**
 * Phase 01 - Mesh face (triangle).
 *
 * Represents a triangular face by storing indices into the parent Mesh's vertex list.
 * Future phases may extend this to support quad or n-gon faces.
 */
public class Face {

    private final int[] vertexIndices;

    public Face(int v0, int v1, int v2) {
        this.vertexIndices = new int[]{v0, v1, v2};
    }

    public int[] getVertexIndices() {
        return vertexIndices;
    }

    public int getVertexIndex(int i) {
        return vertexIndices[i];
    }

    public int getVertexCount() {
        return vertexIndices.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Face face = (Face) o;
        return java.util.Arrays.equals(vertexIndices, face.vertexIndices);
    }

    @Override
    public int hashCode() {
        return java.util.Arrays.hashCode(vertexIndices);
    }

    @Override
    public String toString() {
        return "Face[" + vertexIndices[0] + "," + vertexIndices[1] + "," + vertexIndices[2] + "]";
    }
}
