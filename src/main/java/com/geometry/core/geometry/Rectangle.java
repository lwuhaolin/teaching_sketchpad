package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 02 - 2D rectangle geometry object.
 *
 * A rectangle in the XY plane (z=0), centred at the origin.
 * Width along X axis, height along Y axis.
 */
public class Rectangle implements GeometryObject {

    private float width;
    private float height;
    private Mesh mesh;
    private Transform transform;

    public Rectangle(float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive, got " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
        this.transform = new Transform();
        updateMesh();
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive, got " + width);
        }
        this.width = width;
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
        mesh = com.geometry.core.mesh.MeshFactory.createRectangle(width, height);
    }

    @Override
    public String toString() {
        return "Rectangle{width=" + width + ", height=" + height
                + ", transform=" + transform + "}";
    }
}
