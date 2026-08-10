package com.geometry.teaching.construction;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.MeshFactory;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 07 - Constructs a Point geometry object at a given position.
 *
 * A Point is represented as a small sphere mesh (radius 0.1) at the
 * specified world position. Points are used as:
 *   - Geometric reference markers
 *   - Construction inputs (e.g. endpoints of a line)
 *   - Label targets for text annotations
 *
 * The constructed point is immutable after creation — to move it,
 * create a new PointConstruction with a different position.
 *
 * Not thread-safe.
 */
public class PointConstruction implements Construction {

    private final Vec3 position;
    private final float radius;

    // Default point radius
    private static final float DEFAULT_RADIUS = 0.1f;

    /**
     * Create a PointConstruction at the given position with default radius.
     *
     * @param position world-space position (typically z=0 for 2D)
     * @throws IllegalArgumentException if position is null
     */
    public PointConstruction(Vec3 position) {
        this(position, DEFAULT_RADIUS);
    }

    /**
     * Create a PointConstruction at the given position with custom radius.
     *
     * @param position world-space position
     * @param radius   point sphere radius (must be positive)
     * @throws IllegalArgumentException if position is null or radius <= 0
     */
    public PointConstruction(Vec3 position, float radius) {
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        this.position = position;
        this.radius = radius;
    }

    // ------------------------------------------------------------------
    // Construction interface
    // ------------------------------------------------------------------

    /**
     * Build a Point-like GeometryObject (small sphere) at the construction position.
     *
     * @return a Sphere geometry object positioned at this point
     */
    @Override
    public GeometryObject build() {
        Mesh mesh = MeshFactory.createSphere(radius, 8, 6);
        Transform transform = new Transform(position, new com.geometry.core.math.Vec3(0f, 0f, 0f), Vec3.ONE);

        // Use a simple wrapper that holds the mesh and transform
        // We create a minimal Sphere with the given radius at the construction position
        // Since Sphere takes center position as parameter, we construct it and then
        // set the transform to move it to our target position
        com.geometry.core.geometry.Sphere point = new com.geometry.core.geometry.Sphere(radius, 8, 6);
        point.setTransform(transform);
        return point;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the construction position.
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * Get the point radius.
     */
    public float getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "PointConstruction{position=" + position + ", radius=" + radius + "}";
    }
}
