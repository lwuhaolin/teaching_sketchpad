package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 02 - 3D sphere geometry object.
 *
 * A sphere approximated by latitude-longitude parameterisation.
 *
 * @param radius    sphere radius
 * @param segments  longitudinal segments (meridians), must be >= 3
 * @param rings     latitudinal segments (parallels), must be >= 2
 */
public class Sphere implements GeometryObject {

    private float radius;
    private int segments;
    private int rings;
    private Mesh mesh;
    private Transform transform;

    public Sphere(float radius, int segments, int rings) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        if (segments < 3) {
            throw new IllegalArgumentException("Segments must be >= 3, got " + segments);
        }
        if (rings < 2) {
            throw new IllegalArgumentException("Rings must be >= 2, got " + rings);
        }
        this.radius = radius;
        this.segments = segments;
        this.rings = rings;
        this.transform = new Transform();
        updateMesh();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        this.radius = radius;
        updateMesh();
    }

    public int getSegments() {
        return segments;
    }

    public void setSegments(int segments) {
        if (segments < 3) {
            throw new IllegalArgumentException("Segments must be >= 3, got " + segments);
        }
        this.segments = segments;
        updateMesh();
    }

    public int getRings() {
        return rings;
    }

    public void setRings(int rings) {
        if (rings < 2) {
            throw new IllegalArgumentException("Rings must be >= 2, got " + rings);
        }
        this.rings = rings;
        updateMesh();
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

    @Override
    public void setTransform(Transform transform) {
        if (transform == null) {
            throw new IllegalArgumentException("Transform cannot be null");
        }
        this.transform = transform;
        updateMesh();
    }

    @Override
    public void updateMesh() {
        mesh = com.geometry.core.mesh.MeshFactory.createSphere(radius, segments, rings);
    }

    @Override
    public String toString() {
        return "Sphere{radius=" + radius + ", segments=" + segments
                + ", rings=" + rings + ", transform=" + transform + "}";
    }
}
