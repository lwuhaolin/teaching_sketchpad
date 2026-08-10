package com.geometry.teaching.construction;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.MeshFactory;

/**
 * Phase 07 - Constructs a Circle geometry object.
 *
 * Supports two construction modes:
 *   1. Center + Radius: circle centered at a point with given radius
 *   2. Three Points: circle passing through three non-collinear points
 *
 * The circle is represented as a disc mesh (flat cylinder) in the XY plane.
 *
 * Not thread-safe.
 */
public class CircleConstruction implements Construction {

    /**
     * Construction mode for circles.
     */
    public enum CircleMode {
        /** Center and radius defined explicitly. */
        CENTER_RADIUS,
        /** Circle passing through three points. */
        THREE_POINTS
    }

    private final CircleMode mode;
    private final Vec3 center;
    private final float radius;
    private final Vec3 pointA;
    private final Vec3 pointB;
    private final Vec3 pointC;
    private final int segments;

    // Default circle segments for smooth appearance
    private static final int DEFAULT_SEGMENTS = 32;

    /**
     * Create a CircleConstruction with center and radius.
     *
     * @param center circle center (z should be 0 for 2D mode)
     * @param radius circle radius (must be positive)
     * @throws IllegalArgumentException if center is null or radius <= 0
     */
    public CircleConstruction(Vec3 center, float radius) {
        this(center, radius, DEFAULT_SEGMENTS);
    }

    /**
     * Create a CircleConstruction with center, radius, and segment count.
     *
     * @param center   circle center
     * @param radius   circle radius (must be positive)
     * @param segments number of segments for the approximation (>= 3)
     * @throws IllegalArgumentException if center is null, radius <= 0, or segments < 3
     */
    public CircleConstruction(Vec3 center, float radius, int segments) {
        if (center == null) {
            throw new IllegalArgumentException("Center cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        if (segments < 3) {
            throw new IllegalArgumentException("Segments must be >= 3, got " + segments);
        }
        this.mode = CircleMode.CENTER_RADIUS;
        this.center = center;
        this.radius = radius;
        this.segments = segments;
        this.pointA = null;
        this.pointB = null;
        this.pointC = null;
    }

    /**
     * Create a CircleConstruction through three points.
     *
     * The three points must not be collinear. The circle is the unique
     * circumscircle passing through all three points.
     *
     * @param a first point on the circle
     * @param b second point on the circle
     * @param c third point on the circle
     * @throws IllegalArgumentException if any point is null or points are collinear
     */
    public CircleConstruction(Vec3 a, Vec3 b, Vec3 c) {
        if (a == null || b == null || c == null) {
            throw new IllegalArgumentException("All three points must be non-null");
        }
        this.mode = CircleMode.THREE_POINTS;
        this.pointA = a;
        this.pointB = b;
        this.pointC = c;
        this.center = null;
        this.radius = 0f;
        this.segments = DEFAULT_SEGMENTS;

        // Validate non-collinearity
        Vec3 ab = b.subtract(a);
        Vec3 ac = c.subtract(a);
        float cross = ab.x * ac.y - ab.y * ac.x;
        if (Math.abs(cross) < 0.0001f) {
            throw new IllegalArgumentException("Points are collinear, cannot construct a circle");
        }
    }

    // ------------------------------------------------------------------
    // Construction interface
    // ------------------------------------------------------------------

    /**
     * Build a Circle geometry object.
     *
     * For CENTER_RADIUS mode: creates a circle with the given center and radius.
     * For THREE_POINTS mode: computes the circumscircle and creates it.
     *
     * @return a Circle geometry object
     */
    @Override
    public GeometryObject build() {
        float cX, cY, r;
        if (mode == CircleMode.CENTER_RADIUS) {
            cX = center.x;
            cY = center.y;
            r = radius;
        } else {
            // Compute circumscircle center and radius
            float[] result = computeCircumscircle(pointA, pointB, pointC);
            cX = result[0];
            cY = result[1];
            r = result[2];
        }

        com.geometry.core.geometry.Circle circle = new com.geometry.core.geometry.Circle(r, segments);
        Vec3 circleCenter = new Vec3(cX, cY, 0f);
        circle.setTransform(new com.geometry.core.transform.Transform(circleCenter, new Vec3(0f, 0f, 0f), Vec3.ONE));
        return circle;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the construction mode.
     */
    public CircleMode getMode() {
        return mode;
    }

    /**
     * Get the center point (for CENTER_RADIUS mode, or computed for THREE_POINTS).
     */
    public Vec3 getCenter() {
        if (mode == CircleMode.CENTER_RADIUS) {
            return center;
        }
        float[] result = computeCircumscircle(pointA, pointB, pointC);
        return new Vec3(result[0], result[1], 0f);
    }

    /**
     * Get the radius.
     */
    public float getRadius() {
        if (mode == CircleMode.CENTER_RADIUS) {
            return radius;
        }
        float[] result = computeCircumscircle(pointA, pointB, pointC);
        return result[2];
    }

    /**
     * Get the segment count.
     */
    public int getSegments() {
        return segments;
    }

    /**
     * Get the three points (for THREE_POINTS mode).
     */
    public Vec3 getPointA() {
        return pointA;
    }

    public Vec3 getPointB() {
        return pointB;
    }

    public Vec3 getPointC() {
        return pointC;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Compute the circumscircle of three 2D points (z ignored).
     *
     * Returns float[3] = {centerX, centerY, radius}.
     *
     * @param a first point
     * @param b second point
     * @param c third point
     * @return array of [cx, cy, radius]
     */
    private static float[] computeCircumscircle(Vec3 a, Vec3 b, Vec3 c) {
        float ax = a.x, ay = a.y;
        float bx = b.x, by = b.y;
        float cx = c.x, cy = c.y;

        float D = 2f * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
        if (Math.abs(D) < 0.0001f) {
            // Degenerate case — return average as center with arbitrary radius
            float cxCalc = (ax + bx + cx) / 3f;
            float cyCalc = (ay + by + cy) / 3f;
            float r = (float) Math.sqrt((ax - cxCalc) * (ax - cxCalc) + (ay - cyCalc) * (ay - cyCalc));
            return new float[]{cxCalc, cyCalc, Math.max(r, 0.1f)};
        }

        float cxCalc = ((ax * ax + ay * ay) * (by - cy) +
                        (bx * bx + by * by) * (cy - ay) +
                        (cx * cx + cy * cy) * (ay - by)) / D;
        float cyCalc = ((ax * ax + ay * ay) * (cx - bx) +
                        (bx * bx + by * by) * (ax - cx) +
                        (cx * cx + cy * cy) * (bx - ax)) / D;

        float r = (float) Math.sqrt((ax - cxCalc) * (ax - cxCalc) + (ay - cyCalc) * (ay - cyCalc));
        return new float[]{cxCalc, cyCalc, r};
    }

    @Override
    public String toString() {
        return "CircleConstruction{mode=" + mode + ", center=" + getCenter()
                + ", radius=" + getRadius() + ", segments=" + segments + "}";
    }
}
