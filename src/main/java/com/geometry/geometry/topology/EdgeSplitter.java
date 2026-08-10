package com.geometry.geometry.topology;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Edge;
import com.geometry.core.mesh.Vertex;
import com.geometry.geometry.cutting.IntersectionPoint;
import com.geometry.geometry.cutting.Plane;

/**
 * Phase 08 - Splits a mesh edge by a cutting plane.
 *
 * Computes the intersection point of an edge (defined by two vertex positions)
 * with a plane, and returns an IntersectionPoint containing the world position
 * and the original edge reference.
 *
 * If the edge does not intersect the plane (both endpoints on the same side),
 * returns null.
 */
public class EdgeSplitter {

    private static final float EPSILON = 1e-6f;

    /**
     * Split the given edge by the cutting plane.
     *
     * @param edge    the edge to split (contains vertex indices, not positions)
     * @param vertices the vertex list from the parent mesh (for position lookup)
     * @param plane   the cutting plane
     * @return IntersectionPoint if the edge crosses the plane, null otherwise
     */
    public static IntersectionPoint split(Edge edge, java.util.List<Vertex> vertices, Plane plane) {
        if (edge == null || vertices == null || plane == null) {
            return null;
        }
        int idxA = edge.getVertexA();
        int idxB = edge.getVertexB();
        if (idxA < 0 || idxA >= vertices.size() || idxB < 0 || idxB >= vertices.size()) {
            return null;
        }

        Vec3 posA = vertices.get(idxA).getPosition();
        Vec3 posB = vertices.get(idxB).getPosition();

        float distA = plane.distanceToPoint(posA);
        float distB = plane.distanceToPoint(posB);

        // Both on the same side (or on the plane) — no intersection
        if (Math.signum(distA) == Math.signum(distB)) {
            // Special case: one point is essentially on the plane
            if (Math.abs(distA) < EPSILON || Math.abs(distB) < EPSILON) {
                return null;
            }
            return null;
        }

        // Edge crosses the plane — compute intersection via linear interpolation
        float totalDist = Math.abs(distA - distB);
        if (totalDist < EPSILON) {
            return null;
        }

        float t = distA / totalDist;
        Vec3 intersect = interpolate(posA, posB, t);

        return new IntersectionPoint(intersect, edge);
    }

    /**
     * Linearly interpolate between a and b by factor t.
     * t=0 → a, t=1 → b.
     */
    static Vec3 interpolate(Vec3 a, Vec3 b, float t) {
        return new Vec3(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t
        );
    }
}
