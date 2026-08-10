package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 02 - 3D cylinder geometry object.
 *
 * A right circular cylinder with the given radius, height, and segment count.
 * Axis aligned along Y, centred at origin from y = -height/2 to y = +height/2.
 *
 * Parameter update triggers automatic mesh regeneration.
 */
public class Cylinder implements GeometryObject {

    private float radius;
    private float height;
    private int segments;
    private Mesh mesh;
    private Transform transform;

    public Cylinder(float radius, float height, int segments) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive, got " + radius);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive, got " + height);
        }
        if (segments < 3) {
            throw new IllegalArgumentException("Segments must be >= 3, got " + segments);
        }
        this.radius = radius;
        this.height = height;
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

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive, got " + height);
        }
        this.height = height;
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
        mesh = com.geometry.core.mesh.MeshFactory.createCylinder(radius, height, segments);
    }

    @Override
    public String toString() {
        return "Cylinder{radius=" + radius + ", height=" + height
                + ", segments=" + segments + ", transform=" + transform + "}";
    }
}
