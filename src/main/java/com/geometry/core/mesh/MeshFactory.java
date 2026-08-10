package com.geometry.core.mesh;

import com.geometry.core.math.Vec3;

/**
 * Phase 02 - Mesh generation factory.
 *
 * Centralised mesh creation for all geometry primitives. Keeps geometry
 * classes thin (parameter-only) while encapsulating the mesh-generation
 * algorithms here.
 *
 * Usage: call the static factory methods to build Mesh instances.
 */
public final class MeshFactory {

    private MeshFactory() {
        // Utility class — prevent instantiation
    }

    // ---- 2D primitives (z = 0) ----

    /**
     * Create a rectangular mesh in the XY plane (z=0), centred at origin.
     * Width along X, height along Y.
     * Produces 4 vertices and 2 triangular faces.
     */
    public static Mesh createRectangle(float width, float height) {
        Mesh mesh = new Mesh();
        float hw = width / 2f;
        float hh = height / 2f;

        int v0 = mesh.addVertex(new Vertex(new Vec3(-hw, -hh, 0f)));
        int v1 = mesh.addVertex(new Vertex(new Vec3(hw, -hh, 0f)));
        int v2 = mesh.addVertex(new Vertex(new Vec3(hw, hh, 0f)));
        int v3 = mesh.addVertex(new Vertex(new Vec3(-hw, hh, 0f)));

        mesh.addFace(v0, v1, v2);
        mesh.addFace(v0, v2, v3);
        return mesh;
    }

    /**
     * Create a circular disc mesh in the XY plane (z=0), centred at origin.
     * Uses a polygon approximation with the given number of segments.
     * Produces (segments + 1) vertices and segments triangular faces.
     */
    public static Mesh createCircle(float radius, int segments) {
        Mesh mesh = new Mesh();
        int center = mesh.addVertex(new Vertex(new Vec3(0f, 0f, 0f)));

        float angleStep = 2f * (float) Math.PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float y = radius * (float) Math.sin(angle);
            int v = mesh.addVertex(new Vertex(new Vec3(x, y, 0f)));

            // Triangle from center to edge segment
            int next = (i + 1) % segments;
            int vNext = mesh.getVertexCount() - 1;
            // Re-fetch because we just added; simpler: store indices
            // Actually we need to track them. Let me fix this below.
        }

        // Re-implement cleanly with index tracking
        mesh = new Mesh();
        center = mesh.addVertex(new Vertex(new Vec3(0f, 0f, 0f)));

        int[] edgeVertices = new int[segments];
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float y = radius * (float) Math.sin(angle);
            edgeVertices[i] = mesh.addVertex(new Vertex(new Vec3(x, y, 0f)));
        }

        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(center, edgeVertices[i], edgeVertices[next]);
        }

        return mesh;
    }

    /**
     * Create a polygon mesh from an ordered list of 2D points (z=0).
     * Uses fan triangulation from the first vertex.
     * Requires at least 3 points.
     *
     * @param points ordered list of vertices (copied, not referenced)
     */
    public static Mesh createPolygon(Vec3... points) {
        if (points.length < 3) {
            throw new IllegalArgumentException(
                    "Polygon requires at least 3 vertices, got " + points.length);
        }
        Mesh mesh = new Mesh();
        int center = mesh.addVertex(new Vertex(points[0]));

        int[] edgeVertices = new int[points.length - 1];
        for (int i = 1; i < points.length; i++) {
            edgeVertices[i - 1] = mesh.addVertex(new Vertex(points[i]));
        }

        for (int i = 0; i < edgeVertices.length - 1; i++) {
            mesh.addFace(center, edgeVertices[i], edgeVertices[i + 1]);
        }
        return mesh;
    }

    // ---- 3D primitives ----

    /**
     * Create a cube mesh with the given dimensions, centred at origin.
     * Produces 8 vertices and 12 triangular faces (6 faces × 2 triangles).
     */
    public static Mesh createCube(float width, float height, float depth) {
        Mesh mesh = new Mesh();
        float hw = width / 2f, hh = height / 2f, hd = depth / 2f;

        // 8 vertices, ordered: -Y face (0-3), +Y face (4-7)
        // Bottom face (z = -hd)
        int v0 = mesh.addVertex(new Vertex(new Vec3(-hw, -hh, -hd)));
        int v1 = mesh.addVertex(new Vertex(new Vec3(hw, -hh, -hd)));
        int v2 = mesh.addVertex(new Vertex(new Vec3(hw, hh, -hd)));
        int v3 = mesh.addVertex(new Vertex(new Vec3(-hw, hh, -hd)));
        // Top face (z = +hd)
        int v4 = mesh.addVertex(new Vertex(new Vec3(-hw, -hh, hd)));
        int v5 = mesh.addVertex(new Vertex(new Vec3(hw, -hh, hd)));
        int v6 = mesh.addVertex(new Vertex(new Vec3(hw, hh, hd)));
        int v7 = mesh.addVertex(new Vertex(new Vec3(-hw, hh, hd)));

        // 6 faces, each 2 triangles
        // Front (z = -hd)
        mesh.addFace(v0, v1, v2); mesh.addFace(v0, v2, v3);
        // Back (z = +hd)
        mesh.addFace(v5, v4, v7); mesh.addFace(v5, v7, v6);
        // Top (y = +hh)
        mesh.addFace(v3, v2, v6); mesh.addFace(v3, v6, v7);
        // Bottom (y = -hh)
        mesh.addFace(v0, v3, v7); mesh.addFace(v0, v7, v1);
        // Right (x = +hw)
        mesh.addFace(v1, v7, v6); mesh.addFace(v1, v6, v2);
        // Left (x = -hw)
        mesh.addFace(v0, v4, v3); mesh.addFace(v3, v4, v7);

        return mesh;
    }

    /**
     * Create a cylinder mesh with the given radius, height, and segment count.
     * Cylinder is centred at origin along Y axis, from z = -height/2 to z = +height/2.
     *
     * Produces:
     * - Side: 2 * segments vertices, 2 * segments triangles
     * - Top & bottom caps: segments triangles each (fan from centre)
     * Total: 2 * segments + 2 vertices, 2 * segments * 2 + segments * 2 triangles
     */
    public static Mesh createCylinder(float radius, float height, int segments) {
        if (segments < 3) {
            throw new IllegalArgumentException("Cylinder requires at least 3 segments, got " + segments);
        }
        Mesh mesh = new Mesh();
        float halfH = height / 2f;

        // Bottom circle vertices (z = -halfH)
        int[] bottom = new int[segments];
        float angleStep = 2f * (float) Math.PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            bottom[i] = mesh.addVertex(new Vertex(new Vec3(x, -halfH, z)));
        }

        // Top circle vertices (z = +halfH)
        int[] top = new int[segments];
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            top[i] = mesh.addVertex(new Vertex(new Vec3(x, halfH, z)));
        }

        // Bottom cap (fan from centre)
        int bottomCenter = mesh.addVertex(new Vertex(new Vec3(0f, -halfH, 0f)));
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(bottomCenter, bottom[i], bottom[next]);
        }

        // Top cap (fan from centre)
        int topCenter = mesh.addVertex(new Vertex(new Vec3(0f, halfH, 0f)));
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(topCenter, top[next], top[i]);
        }

        // Side panels
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(bottom[i], top[i], bottom[next]);
            mesh.addFace(bottom[next], top[i], top[next]);
        }

        return mesh;
    }

    /**
     * Create a cone mesh with the given base radius, height, and segment count.
     * Cone apex at (0, +height/2, 0), base circle at z = -height/2.
     *
     * Produces:
     * - Side: 2 * segments triangles
     * - Base cap: segments triangles (fan from centre)
     */
    public static Mesh createCone(float radius, float height, int segments) {
        if (segments < 3) {
            throw new IllegalArgumentException("Cone requires at least 3 segments, got " + segments);
        }
        Mesh mesh = new Mesh();
        float halfH = height / 2f;

        // Apex
        int apex = mesh.addVertex(new Vertex(new Vec3(0f, halfH, 0f)));

        // Base circle vertices (z = -halfH)
        int[] base = new int[segments];
        float angleStep = 2f * (float) Math.PI / segments;
        for (int i = 0; i < segments; i++) {
            float angle = i * angleStep;
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            base[i] = mesh.addVertex(new Vertex(new Vec3(x, -halfH, z)));
        }

        // Side triangles (apex to each base edge)
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(apex, base[i], base[next]);
        }

        // Base cap (fan from centre)
        int baseCenter = mesh.addVertex(new Vertex(new Vec3(0f, -halfH, 0f)));
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            mesh.addFace(baseCenter, base[i], base[next]);
        }

        return mesh;
    }

    /**
     * Create a sphere mesh using latitude-longitude (UV) parameterisation.
     *
     * @param radius  sphere radius
     * @param segments longitudinal segments (meridians), ≥ 3
     * @param rings    latitudinal segments (parallels), ≥ 2
     */
    public static Mesh createSphere(float radius, int segments, int rings) {
        if (segments < 3) {
            throw new IllegalArgumentException("Sphere requires at least 3 segments, got " + segments);
        }
        if (rings < 2) {
            throw new IllegalArgumentException("Sphere requires at least 2 rings, got " + rings);
        }
        Mesh mesh = new Mesh();

        float angleStep = 2f * (float) Math.PI / segments;
        float ringStep = (float) Math.PI / rings;

        // Create vertices row by row (rings + 2 rows: north pole, rings, south pole)
        int[][] ringVertices = new int[rings + 2][];
        int[][] vertexIndices = new int[rings + 2][segments];

        // North pole
        vertexIndices[0] = new int[]{mesh.addVertex(new Vertex(new Vec3(0f, radius, 0f)))};

        // Middle rings
        for (int r = 1; r <= rings; r++) {
            float phi = r * ringStep; // 0 to PI
            float y = radius * (float) Math.cos(phi);
            float rXY = radius * (float) Math.sin(phi);
            ringVertices[r] = new int[segments];
            for (int s = 0; s < segments; s++) {
                float theta = s * angleStep;
                float x = rXY * (float) Math.cos(theta);
                float z = rXY * (float) Math.sin(theta);
                ringVertices[r][s] = mesh.addVertex(new Vertex(new Vec3(x, y, z)));
            }
        }

        // South pole
        vertexIndices[rings + 1] = new int[]{mesh.addVertex(new Vertex(new Vec3(0f, -radius, 0f)))};

        // Generate quads as two triangles per cell
        // Between ring r and r+1
        for (int r = 0; r < rings + 1; r++) {
            if (r == 0) {
                // North pole: fan of triangles from north pole to first ring
                for (int s = 0; s < segments; s++) {
                    int next = (s + 1) % segments;
                    mesh.addFace(vertexIndices[r][0], vertexIndices[r + 1][s], vertexIndices[r + 1][next]);
                }
            } else if (r == rings) {
                // South pole: fan of triangles from last ring to south pole
                for (int s = 0; s < segments; s++) {
                    int next = (s + 1) % segments;
                    mesh.addFace(vertexIndices[r][s], vertexIndices[r + 1][0], vertexIndices[r][next]);
                }
            } else {
                // Middle band: two triangles per quad cell
                for (int s = 0; s < segments; s++) {
                    int next = (s + 1) % segments;
                    int a = vertexIndices[r][s];
                    int b = vertexIndices[r][next];
                    int c = vertexIndices[r + 1][s];
                    int d = vertexIndices[r + 1][next];
                    mesh.addFace(a, b, c);
                    mesh.addFace(b, d, c);
                }
            }
        }

        return mesh;
    }
}
