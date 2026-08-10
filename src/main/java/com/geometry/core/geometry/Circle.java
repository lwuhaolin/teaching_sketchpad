package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 02 - 2D circle geometry object.
 *
 * A circular disc in the XY plane (z=0), approximated by a polygon with
 * the given number of segments. Centred at the origin.
 */
public class Circle implements GeometryObject {

    private float radius;
    private int segments;
    private Mesh mesh;
    private Transform transform;

    public Circle(float radius, int segments) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        if (segments < 3) {
            throw new IllegalArgumentException("Segments must be >= 3, got " + segments);
        }
        this.radius = radius;
        this.segments = segments;
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
        mesh = com.geometry.core.mesh.MeshFactory.createCircle(radius, segments);
    }

    @Override
    public String toString() {
        return "Circle{radius=" + radius + ", segments=" + segments
                + ", transform=" + transform + "}";
    }
}
