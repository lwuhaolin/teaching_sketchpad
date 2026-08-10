package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 02 - 3D cube geometry object.
 *
 * A cube (or rectangular prism / cuboid) with given width, height, and depth.
 * Centred at the origin.
 */
public class Cube implements GeometryObject {

    private float width;
    private float height;
    private float depth;
    private Mesh mesh;
    private Transform transform;

    public Cube(float width, float height, float depth) {
        if (width <= 0 || height <= 0 || depth <= 0) {
            throw new IllegalArgumentException("Dimensions must be positive, got " + width + "x" + height + "x" + depth);
        }
        this.width = width;
        this.height = height;
        this.depth = depth;
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

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("Depth must be positive, got " + depth);
        }
        this.depth = depth;
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
        mesh = com.geometry.core.mesh.MeshFactory.createCube(width, height, depth);
    }

    @Override
    public String toString() {
        return "Cube{width=" + width + ", height=" + height + ", depth=" + depth
                + ", transform=" + transform + "}";
    }
}
