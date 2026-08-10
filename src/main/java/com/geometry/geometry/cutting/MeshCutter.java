package com.geometry.geometry.cutting;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;
import com.geometry.geometry.operation.GeometryOperation;
import com.geometry.geometry.operation.OperationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 08 - Core mesh cutting algorithm.
 *
 * Splits a triangle mesh by a plane into two separate meshes (positive side
 * and negative side). The original mesh is never modified.
 *
 * <h3>Cutting Algorithm</h3>
 * <ol>
 *   <li>For each face (triangle), classify its 3 vertices as POSITIVE, NEGATIVE, or ON_PLANE.</li>
 *   <li>Based on classification, determine which side(s) the face belongs to.</li>
 *   <li>For crossing faces, compute edge-plane intersection points and generate new triangles.</li>
 *   <li>Build two new meshes from the classified face lists.</li>
 * </ol>
 *
 * <h3>Face Classification Rules</h3>
 * <table>
 *   <tr><th>Vertices</th><th>Action</th></tr>
 *   <tr><td>All 3 positive</td><td>Keep face in positive mesh</td></tr>
 *   <tr><td>All 3 negative</td><td>Keep face in negative mesh</td></tr>
 *   <tr><td>All 3 on plane</td><td>Keep face in positive mesh (degenerate)</td></tr>
 *   <tr><td>2 positive, 1 negative</td><td>Split: positive triangle + negative triangle</td></tr>
 *   <tr><td>1 positive, 2 negative</td><td>Split: positive triangle + negative triangle</td></tr>
 *   <tr><td>1 on plane, 2 positive</td><td>Keep in positive mesh</td></tr>
 *   <tr><td>1 on plane, 2 negative</td><td>Keep in negative mesh</td></tr>
 *   <tr><td>2 on plane, 1 positive</td><td>Keep in positive mesh</td></tr>
 *   <tr><td>2 on plane, 1 negative</td><td>Keep in negative mesh</td></tr>
 * </table>
 */
public class MeshCutter {

    /** Vertex side classification. */
    enum Side { POSITIVE, NEGATIVE, ON_PLANE }

    /**
     * Cut a mesh with the given plane.
     *
     * @param mesh  the mesh to cut (not modified)
     * @param plane the cutting plane
     * @return OperationResult containing two meshes (positive side, negative side)
     * @throws IllegalArgumentException if mesh or plane is null
     */
    public static OperationResult cut(Mesh mesh, Plane plane) {
        if (mesh == null) {
            throw new IllegalArgumentException("Mesh cannot be null");
        }
        if (plane == null) {
            throw new IllegalArgumentException("Plane cannot be null");
        }

        List<Vertex> vertices = mesh.getVertices();
        List<Face> faces = mesh.getFaces();

        // Use position-based faces to handle intersection points cleanly
        List<Vec3[]> positiveFacePositions = new ArrayList<>();
        List<Vec3[]> negativeFacePositions = new ArrayList<>();

        // Cache for edge-plane intersections to avoid recomputation
        Map<Long, Vec3> intersectionCache = new HashMap<>();

        for (Face face : faces) {
            int[] faceIndices = face.getVertexIndices();
            if (faceIndices.length != 3) {
                continue;
            }

            Side[] sides = new Side[3];
            for (int i = 0; i < 3; i++) {
                Vec3 pos = vertices.get(faceIndices[i]).getPosition();
                float dist = plane.distanceToPoint(pos);
                if (dist > 1e-6f) {
                    sides[i] = Side.POSITIVE;
                } else if (dist < -1e-6f) {
                    sides[i] = Side.NEGATIVE;
                } else {
                    sides[i] = Side.ON_PLANE;
                }
            }

            processFace(sides, faceIndices, vertices, plane,
                    positiveFacePositions, negativeFacePositions, intersectionCache);
        }

        // Build output meshes from position triples
        Mesh positiveMesh = buildMeshFromPositions(positiveFacePositions);
        Mesh negativeMesh = buildMeshFromPositions(negativeFacePositions);

        // If one side is empty, return just the other
        if (positiveMesh.isEmpty() && negativeMesh.isEmpty()) {
            return OperationResult.empty();
        }
        if (positiveMesh.isEmpty()) {
            return OperationResult.success(negativeMesh);
        }
        if (negativeMesh.isEmpty()) {
            return OperationResult.success(positiveMesh);
        }

        return OperationResult.success(positiveMesh, negativeMesh);
    }

    /**
     * Process a single face based on vertex side classifications.
     */
    private static void processFace(Side[] sides, int[] indices, List<Vertex> vertices,
                                    Plane plane, List<Vec3[]> positiveFaces,
                                    List<Vec3[]> negativeFaces,
                                    Map<Long, Vec3> intersectionCache) {
        // All same side
        if (sides[0] == sides[1] && sides[1] == sides[2]) {
            if (sides[0] == Side.POSITIVE || sides[0] == Side.ON_PLANE) {
                positiveFaces.add(toPositionArray(indices, vertices));
            } else {
                negativeFaces.add(toPositionArray(indices, vertices));
            }
            return;
        }

        // Count sides
        int posCount = 0, negCount = 0, onCount = 0;
        for (Side s : sides) {
            if (s == Side.POSITIVE) posCount++;
            else if (s == Side.NEGATIVE) negCount++;
            else onCount++;
        }

        // 2 positive, 1 negative
        if (posCount == 2 && negCount == 1) {
            splitTwoPositiveOneNegative(sides, indices, vertices, plane, positiveFaces, negativeFaces, intersectionCache);
            return;
        }

        // 1 positive, 2 negative
        if (posCount == 1 && negCount == 2) {
            splitOnePositiveTwoNegative(sides, indices, vertices, plane, positiveFaces, negativeFaces, intersectionCache);
            return;
        }

        // 1 on-plane mixed with positives or negatives
        if (onCount == 1) {
            // One vertex on plane, two off-plane
            int offPosIdx = -1, offNegIdx = -1;
            for (int i = 0; i < 3; i++) {
                if (sides[i] == Side.POSITIVE) offPosIdx = i;
                else if (sides[i] == Side.NEGATIVE) offNegIdx = i;
            }
            if (offPosIdx >= 0) {
                // 1 on-plane, 2 positive → keep in positive
                positiveFaces.add(toPositionArray(indices, vertices));
            } else if (offNegIdx >= 0) {
                // 1 on-plane, 2 negative → keep in negative
                negativeFaces.add(toPositionArray(indices, vertices));
            }
            return;
        }

        // 2 on-plane, 1 off-plane
        if (onCount == 2) {
            for (int i = 0; i < 3; i++) {
                if (sides[i] != Side.ON_PLANE) {
                    if (sides[i] == Side.POSITIVE) {
                        positiveFaces.add(toPositionArray(indices, vertices));
                    } else {
                        negativeFaces.add(toPositionArray(indices, vertices));
                    }
                    return;
                }
            }
        }
    }

    /**
     * Two vertices positive (p0, p1), one negative (n2).
     * The edges from n2 to each positive vertex cross the plane.
     *
     * Positive part: triangle (p0, p1, i1) where i1 is intersection on edge (p1, n2)
     * Negative part: triangle (p1, n2, i1) — but we need a second triangle too.
     *   Actually: negative = (n2, p0, i0) + (n2, i0, i1) ... no.
     *
     * Let me use the standard approach:
     *   Vertices: 0=POS, 1=POS, 2=NEG
     *   i0 = intersection on edge (0,2), i1 = intersection on edge (1,2)
     *   Positive triangle: (0, 1, i1)  -- wait, 0→1 is both positive, so this edge stays
     *   Actually the positive region is the quadrilateral cut into triangles.
     *
     *   For vertices [0=POS, 1=POS, 2=NEG]:
     *   - Edge 0-1: both POS, fully in positive
     *   - Edge 1-2: crosses → intersection i1
     *   - Edge 2-0: crosses → intersection i0
     *   Positive region: triangle (0, 1, i1)  ... no, (0, 1, i1) doesn't connect right.
     *
     *   Think of it as: the positive part is a triangle with vertices 0, 1, and i0 (on edge 0-2)
     *   Wait, let me draw it:
     *   Triangle 0-1-2, with 0 and 1 positive, 2 negative.
     *   The cut line goes from i0 (on edge 0-2) to i1 (on edge 1-2).
     *   Positive part: quadrilateral 0-1-i1-i0 → triangulated as (0,1,i1) + (0,i1,i0)
     *   Negative part: triangle (2, i0, i1)
     *
     *   Actually no. The triangle has vertices 0,1,2. The cut crosses edges 0-2 and 1-2.
     *   Positive: vertices 0, 1, and the two intersection points.
     *   The positive region is a quadrilateral (0, 1, i1, i0) split into two triangles.
     *   The negative region is a triangle (2, i0, i1).
     *
     *   Hmm, but (0, 1, i1, i0) as a quadrilateral: edges are 0→1, 1→i1, i1→i0, i0→0.
     *   Triangulation: (0, 1, i1) and (0, i1, i0).
     */
    private static void splitTwoPositiveOneNegative(Side[] sides, int[] indices,
                                                     List<Vertex> vertices, Plane plane,
                                                     List<Vec3[]> positiveFaces,
                                                     List<Vec3[]> negativeFaces,
                                                     Map<Long, Vec3> intersectionCache) {
        // Find the negative vertex index
        int negIdx = -1;
        for (int i = 0; i < 3; i++) {
            if (sides[i] == Side.NEGATIVE) { negIdx = i; break; }
        }

        int p0 = (negIdx + 1) % 3;
        int p1 = (negIdx + 2) % 3;

        // Intersection on edge (negIdx, p0) and (negIdx, p1)
        Vec3 i0 = getIntersection(indices[negIdx], indices[p0], vertices, plane, intersectionCache);
        Vec3 i1 = getIntersection(indices[negIdx], indices[p1], vertices, plane, intersectionCache);

        Vec3 posP0 = vertices.get(indices[p0]).getPosition();
        Vec3 posP1 = vertices.get(indices[p1]).getPosition();
        Vec3 posNeg = vertices.get(indices[negIdx]).getPosition();

        // Positive: two triangles (p0, p1, i1) and (p0, i1, i0)
        // Wait — need to check winding consistency. Original is (p0, p1, neg).
        // Positive region should have same winding (counter-clockwise when viewed from positive side).
        // Triangles: (p0, p1, i1) and (p0, i1, i0)
        positiveFaces.add(new Vec3[]{posP0, posP1, i1});
        positiveFaces.add(new Vec3[]{posP0, i1, i0});

        // Negative: one triangle (neg, i0, i1)
        negativeFaces.add(new Vec3[]{posNeg, i0, i1});
    }

    /**
     * One vertex positive (p0), two negative (n1, n2).
     * Edges from p0 to n1 and p0 to n2 cross the plane.
     *
     * Positive region: triangle (p0, i0, i1)
     * Negative region: quadrilateral (n1, n2, i1, i0) → (n1, n2, i1) + (n1, i1, i0)
     */
    private static void splitOnePositiveTwoNegative(Side[] sides, int[] indices,
                                                     List<Vertex> vertices, Plane plane,
                                                     List<Vec3[]> positiveFaces,
                                                     List<Vec3[]> negativeFaces,
                                                     Map<Long, Vec3> intersectionCache) {
        // Find the positive vertex index
        int posIdx = -1;
        for (int i = 0; i < 3; i++) {
            if (sides[i] == Side.POSITIVE) { posIdx = i; break; }
        }

        int n0 = (posIdx + 1) % 3;
        int n1 = (posIdx + 2) % 3;

        // Intersection on edge (posIdx, n0) and (posIdx, n1)
        Vec3 i0 = getIntersection(indices[posIdx], indices[n0], vertices, plane, intersectionCache);
        Vec3 i1 = getIntersection(indices[posIdx], indices[n1], vertices, plane, intersectionCache);

        Vec3 posPos = vertices.get(indices[posIdx]).getPosition();
        Vec3 negN0 = vertices.get(indices[n0]).getPosition();
        Vec3 negN1 = vertices.get(indices[n1]).getPosition();

        // Positive: one triangle (pos, i0, i1)
        positiveFaces.add(new Vec3[]{posPos, i0, i1});

        // Negative: two triangles (n0, n1, i1) and (n0, i1, i0)
        negativeFaces.add(new Vec3[]{negN0, negN1, i1});
        negativeFaces.add(new Vec3[]{negN0, i1, i0});
    }

    /**
     * Get or compute the intersection of edge (a, b) with the plane.
     * Cached to avoid recomputation.
     */
    private static Vec3 getIntersection(int idxA, int idxB, List<Vertex> vertices,
                                         Plane plane, Map<Long, Vec3> cache) {
        Long key = edgeKey(idxA, idxB);
        Vec3 cached = cache.get(key);
        if (cached != null) {
            return cached;
        }
        Vec3 posA = vertices.get(idxA).getPosition();
        Vec3 posB = vertices.get(idxB).getPosition();
        Vec3 result = computeEdgePlaneIntersection(posA, posB, plane);
        cache.put(key, result);
        cache.put(edgeKey(idxB, idxA), result);
        return result;
    }

    /**
     * Compute the intersection point of edge (a, b) with the plane.
     */
    private static Vec3 computeEdgePlaneIntersection(Vec3 a, Vec3 b, Plane plane) {
        float distA = plane.distanceToPoint(a);
        float distB = plane.distanceToPoint(b);
        float totalDist = Math.abs(distA - distB);
        if (totalDist < 1e-8f) {
            return new Vec3((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f);
        }
        float t = distA / totalDist;
        return new Vec3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }

    /**
     * Convert face vertex indices to position array.
     */
    private static Vec3[] toPositionArray(int[] indices, List<Vertex> vertices) {
        Vec3[] positions = new Vec3[3];
        for (int i = 0; i < 3; i++) {
            positions[i] = vertices.get(indices[i]).getPosition();
        }
        return positions;
    }

    /**
     * Build a Mesh from a list of triangle position triples.
     * Deduplicates vertices by exact position match.
     */
    private static Mesh buildMeshFromPositions(List<Vec3[]> facePositions) {
        Mesh mesh = new Mesh();
        if (facePositions == null || facePositions.isEmpty()) {
            return mesh;
        }

        // First pass: collect all unique positions
        List<Vec3> uniquePositions = new ArrayList<>();
        Map<Vec3, Integer> posToIndex = new HashMap<>();

        for (Vec3[] tri : facePositions) {
            for (Vec3 pos : tri) {
                if (pos == null) continue;
                Integer idx = posToIndex.get(pos);
                if (idx == null) {
                    idx = mesh.addVertex(new Vertex(pos));
                    posToIndex.put(pos, idx);
                    uniquePositions.add(pos);
                }
            }
        }

        // Second pass: add faces with mapped indices
        for (Vec3[] tri : facePositions) {
            if (tri.length != 3) continue;
            Integer i0 = posToIndex.get(tri[0]);
            Integer i1 = posToIndex.get(tri[1]);
            Integer i2 = posToIndex.get(tri[2]);
            if (i0 != null && i1 != null && i2 != null) {
                mesh.addFace(i0, i1, i2);
            }
        }

        return mesh;
    }

    /**
     * Create a unique hash key for an edge (order-independent).
     */
    private static Long edgeKey(int a, int b) {
        long longA = ((long) a) << 32;
        long longB = (long) b;
        return longA ^ longB;
    }
}
