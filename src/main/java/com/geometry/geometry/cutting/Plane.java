package com.geometry.geometry.cutting;

import com.geometry.core.math.Vec3;
import com.geometry.core.math.MathUtil;

/**
 * Phase 08 - Cutting plane in Hesse normal form.
 *
 * Represents the plane equation: ax + by + cz + d = 0
 * where (a, b, c) is the unit normal vector and d is the signed distance
 * from the origin to the plane.
 *
 * The normal points toward the "positive" side. A point P has:
 *   - positive distance: P is on the positive side of the plane
 *   - negative distance: P is on the negative side of the plane
 *   - zero distance:     P lies on the plane
 */
public class Plane {

    public final Vec3 normal;
    public final float distance; // signed distance from origin

    /**
     * Create a plane from a normal vector and signed distance.
     * The normal will be normalized automatically.
     *
     * @param normal the plane normal (must not be zero)
     * @param distance signed distance from origin to plane
     */
    public Plane(Vec3 normal, float distance) {
        if (normal == null) {
            throw new IllegalArgumentException("Normal cannot be null");
        }
        float len = normal.length();
        if (len < MathUtil.EPSILON) {
            throw new IllegalArgumentException("Normal vector must not be zero");
        }
        this.normal = normal.normalize();
        this.distance = distance;
    }

    /**
     * Create a plane from three points. Normal is (B-A) x (C-A) normalized.
     *
     * @param a first point on the plane
     * @param b second point on the plane
     * @param c third point on the plane
     */
    public Plane(Vec3 a, Vec3 b, Vec3 c) {
        if (a == null || b == null || c == null) {
            throw new IllegalArgumentException("Points cannot be null");
        }
        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        Vec3 n = ab.cross(ac);
        float len = n.length();
        if (len < MathUtil.EPSILON) {
            throw new IllegalArgumentException("Points are collinear, cannot define a plane");
        }
        this.normal = n.normalize();
        // d = -(n·a) after normalization
        this.distance = -(this.normal.dot(a));
    }

    /**
     * Distance from a point to this plane.
     * Positive → point is on the positive (normal) side.
     * Negative → point is on the negative side.
     * Zero     → point lies on the plane.
     *
     * @param point the point to test
     * @return signed distance
     */
    public float distanceToPoint(Vec3 point) {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        return normal.x * point.x + normal.y * point.y + normal.z * point.z + distance;
    }

    /**
     * Check if a point is on the positive side of the plane.
     */
    public boolean isPositiveSide(Vec3 point) {
        return distanceToPoint(point) > MathUtil.EPSILON;
    }

    /**
     * Check if a point is on the negative side of the plane.
     */
    public boolean isNegativeSide(Vec3 point) {
        return distanceToPoint(point) < -MathUtil.EPSILON;
    }

    /**
     * Check if a point lies on the plane (within epsilon).
     */
    public boolean isOnPlane(Vec3 point) {
        return Math.abs(distanceToPoint(point)) <= MathUtil.EPSILON;
    }

    /**
     * Check if a point is strictly on the positive side (not on plane).
     */
    public boolean isStrictlyPositive(Vec3 point) {
        return distanceToPoint(point) > MathUtil.EPSILON;
    }

    /**
     * Check if a point is strictly on the negative side (not on plane).
     */
    public boolean isStrictlyNegative(Vec3 point) {
        return distanceToPoint(point) < -MathUtil.EPSILON;
    }

    @Override
    public String toString() {
        return "Plane{normal=" + normal + ", distance=" + distance + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Plane plane = (Plane) o;
        return Float.compare(plane.distance, distance) == 0
                && normal.equals(plane.normal);
    }

    @Override
    public int hashCode() {
        int result = normal.hashCode();
        result = 31 * result + Float.floatToIntBits(distance);
        return result;
    }
}
