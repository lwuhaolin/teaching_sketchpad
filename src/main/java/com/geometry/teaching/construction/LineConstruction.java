package com.geometry.teaching.construction;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.MeshFactory;
import com.geometry.core.transform.Transform;

/**
 * Phase 07 - Constructs a Line geometry object between two points.
 *
 * A Line is represented as a thin rectangular mesh (cylinder-like)
 * connecting point A to point B. The line has a small radius and
 * spans the distance between the two endpoints.
 *
 * Usage:
 *   Construction line = new LineConstruction(pointA, pointB);
 *   GeometryObject line = line.build();
 *
 * Not thread-safe.
 */
public class LineConstruction implements Construction {

    private final Vec3 pointA;
    private final Vec3 pointB;
    private final float radius;

    // Default line radius (thin line)
    private static final float DEFAULT_RADIUS = 0.05f;

    /**
     * Create a LineConstruction between two points with default radius.
     *
     * @param pointA start point
     * @param pointB end point
     * @throws IllegalArgumentException if either point is null
     */
    public LineConstruction(Vec3 pointA, Vec3 pointB) {
        this(pointA, pointB, DEFAULT_RADIUS);
    }

    /**
     * Create a LineConstruction between two points with custom radius.
     *
     * @param pointA start point
     * @param pointB end point
     * @param radius line thickness radius (must be positive)
     * @throws IllegalArgumentException if either point is null or radius <= 0
     */
    public LineConstruction(Vec3 pointA, Vec3 pointB, float radius) {
        if (pointA == null) {
            throw new IllegalArgumentException("Point A cannot be null");
        }
        if (pointB == null) {
            throw new IllegalArgumentException("Point B cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        this.pointA = pointA;
        this.pointB = pointB;
        this.radius = radius;
    }

    // ------------------------------------------------------------------
    // Construction interface
    // ------------------------------------------------------------------

    /**
     * Build a Line geometry object as a thin cylinder between the two points.
     *
     * The cylinder is oriented along the line direction with:
     *   - Center at the midpoint of A and B
     *   - Height equal to the distance between A and B
     *   - Radius as specified
     *
     * @return a Cylinder geometry object positioned along the line
     */
    @Override
    public GeometryObject build() {
        float distance = pointA.subtract(pointB).length();
        if (distance < 0.0001f) {
            // Points are too close — return a point instead
            return new PointConstruction(pointA).build();
        }

        // Create a cylinder with height = distance, radius as specified
        com.geometry.core.geometry.Cylinder line = new com.geometry.core.geometry.Cylinder(radius, distance, 8);

        // Position at midpoint
        Vec3 midPoint = pointA.add(pointB).multiply(0.5f);
        Transform transform = new Transform(midPoint, new Vec3(0f, 0f, 0f), Vec3.ONE);
        line.setTransform(transform);

        // Orient the cylinder to point from A to B
        // The cylinder is built along Y axis; we need to rotate it
        Vec3 direction = pointB.subtract(pointA).normalize();
        Vec3 up = Vec3.UNIT_Y;
        float dot = up.dot(direction);

        if (Math.abs(dot - 1.0f) < 0.0001f) {
            // Direction is exactly up — no rotation needed
        } else if (Math.abs(dot + 1.0f) < 0.0001f) {
            // Direction is exactly down — rotate 180° around X
            transform = new Transform(midPoint, new Vec3(180f, 0f, 0f), Vec3.ONE);
        } else {
            // Compute rotation axis (cross product) and angle
            Vec3 axis = up.cross(direction).normalize();
            float angle = (float) Math.toDegrees(Math.acos(dot));
            // Apply rotation around the computed axis
            // For simplicity, decompose into pitch and yaw
            float pitch = (float) Math.toDegrees(Math.atan2(direction.x, direction.z));
            float yaw = (float) Math.toDegrees(Math.asin(-direction.y));
            transform = new Transform(midPoint, new Vec3(yaw, pitch, 0f), Vec3.ONE);
        }
        line.setTransform(transform);

        return line;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the start point.
     */
    public Vec3 getPointA() {
        return pointA;
    }

    /**
     * Get the end point.
     */
    public Vec3 getPointB() {
        return pointB;
    }

    /**
     * Get the line radius.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Get the length of the line (distance between endpoints).
     */
    public float getLength() {
        return pointA.subtract(pointB).length();
    }

    @Override
    public String toString() {
        return "LineConstruction{A=" + pointA + ", B=" + pointB + ", radius=" + radius + "}";
    }
}
