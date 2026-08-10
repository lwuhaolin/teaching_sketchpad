package com.geometry.core.geometry;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 02 - Arbitrary 2D polygon geometry object.
 *
 * Represents a polygon in the XY plane (z=0), defined by an ordered list
 * of vertices. Supports dynamic modification of the vertex list.
 * Default z-coordinate is 0 for all vertices.
 */
public class Polygon implements GeometryObject {

    private final List<Vec3> points;
    private Mesh mesh;
    private Transform transform;

    public Polygon(Vec3... points) {
        if (points.length < 3) {
            throw new IllegalArgumentException(
                    "Polygon requires at least 3 vertices, got " + points.length);
        }
        this.points = new ArrayList<>();
        Collections.addAll(this.points, points);
        this.transform = new Transform();
        updateMesh();
    }

    public List<Vec3> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public void setPoint(int index, Vec3 point) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException(
                    "Point index " + index + " out of range [0, " + (points.size() - 1) + "]");
        }
        points.set(index, point);
        updateMesh();
    }

    public void addPoint(Vec3 point) {
        points.add(point);
        updateMesh();
    }

    public void removePoint(int index) {
        if (points.size() <= 3) {
            throw new IllegalStateException(
                    "Cannot remove point: polygon would have fewer than 3 vertices");
        }
        points.remove(index);
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
        Vec3[] pts = points.toArray(new Vec3[0]);
        mesh = com.geometry.core.mesh.MeshFactory.createPolygon(pts);
    }

    @Override
    public String toString() {
        return "Polygon{points=" + points.size()
                + ", transform=" + transform + "}";
    }
}
