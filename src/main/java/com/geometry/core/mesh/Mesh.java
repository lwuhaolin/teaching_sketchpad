package com.geometry.core.mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 01 - Mesh data structure.
 *
 * The core geometric primitive that holds vertices, edges, and faces.
 * All geometry in this engine — 2D and 3D — is ultimately represented as a Mesh.
 *
 * The mesh owns its data; callers should use the provided mutation methods
 * rather than directly accessing internal collections.
 *
 * Not thread-safe.
 */
public class Mesh {

    private final List<Vertex> vertices;
    private final List<Edge> edges;
    private final List<Face> faces;

    public Mesh() {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    /**
     * Add a vertex and return its index in the vertex list.
     */
    public int addVertex(Vertex vertex) {
        vertices.add(vertex);
        return vertices.size() - 1;
    }

    /**
     * Add an edge between two existing vertex indices.
     * @throws IllegalArgumentException if indices are out of range.
     */
    public void addEdge(int vertexA, int vertexB) {
        validateVertexIndex(vertexA);
        validateVertexIndex(vertexB);
        edges.add(new Edge(vertexA, vertexB));
    }

    /**
     * Add a triangular face from three vertex indices.
     * @throws IllegalArgumentException if any index is out of range.
     */
    public void addFace(int v0, int v1, int v2) {
        validateVertexIndex(v0);
        validateVertexIndex(v1);
        validateVertexIndex(v2);
        faces.add(new Face(v0, v1, v2));
    }

    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(vertices);
    }

    public List<Edge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Face> getFaces() {
        return Collections.unmodifiableList(faces);
    }

    public int getVertexCount() {
        return vertices.size();
    }

    public int getEdgeCount() {
        return edges.size();
    }

    public int getFaceCount() {
        return faces.size();
    }

    public Vertex getVertex(int index) {
        return vertices.get(index);
    }

    public Edge getEdge(int index) {
        return edges.get(index);
    }

    public Face getFace(int index) {
        return faces.get(index);
    }

    /**
     * Check whether this mesh has any faces.
     */
    public boolean isEmpty() {
        return vertices.isEmpty() && faces.isEmpty();
    }

    private void validateVertexIndex(int index) {
        if (index < 0 || index >= vertices.size()) {
            throw new IndexOutOfBoundsException(
                    "Vertex index " + index + " out of range [0, " + (vertices.size() - 1) + "]"
            );
        }
    }

    @Override
    public String toString() {
        return "Mesh{vertices=" + vertices.size()
                + ", edges=" + edges.size()
                + ", faces=" + faces.size() + "}";
    }
}
