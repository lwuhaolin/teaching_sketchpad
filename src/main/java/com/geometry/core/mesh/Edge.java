package com.geometry.core.mesh;

/**
 * Phase 01 - Mesh edge.
 *
 * Represents an edge connecting two vertices by their indices in the parent Mesh.
 * Vertex indices reference positions in the owning Mesh's vertex list.
 */
public class Edge {

    private final int vertexA;
    private final int vertexB;

    public Edge(int vertexA, int vertexB) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
    }

    public int getVertexA() {
        return vertexA;
    }

    public int getVertexB() {
        return vertexB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Edge edge = (Edge) o;
        return vertexA == edge.vertexA && vertexB == edge.vertexB;
    }

    @Override
    public int hashCode() {
        return 31 * vertexA + vertexB;
    }

    @Override
    public String toString() {
        return "Edge{" + vertexA + "—" + vertexB + "}";
    }
}
