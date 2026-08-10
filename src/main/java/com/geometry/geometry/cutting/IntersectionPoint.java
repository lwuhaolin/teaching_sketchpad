package com.geometry.geometry.cutting;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Edge;

/**
 * Phase 08 - Represents the intersection of an edge with a cutting plane.
 *
 * Stores the 3D position of the intersection and a reference to the
 * edge that produced it. Used during mesh cutting to build new geometry.
 */
public class IntersectionPoint {

    private final Vec3 position;
    private final Edge edge;

    public IntersectionPoint(Vec3 position, Edge edge) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        this.position = position;
        this.edge = edge;
    }

    public Vec3 getPosition() {
        return position;
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public String toString() {
        return "IntersectionPoint{pos=" + position + ", edge=" + edge + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntersectionPoint that = (IntersectionPoint) o;
        return position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}
