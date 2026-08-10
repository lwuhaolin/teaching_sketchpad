package com.geometry.geometry.analysis;

import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.geometry.cutting.Plane;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 08 - Represents a cross-section (cut surface) produced by a plane cut.
 *
 * The section is a polygon defined by a list of 3D points in order,
 * lying on the cutting plane. These points form the boundary of the
 * newly exposed surface after cutting.
 */
public class Section {

    private final List<Vec3> points;
    private final Plane cuttingPlane;

    public Section(List<Vec3> points, Plane cuttingPlane) {
        if (points == null) {
            throw new IllegalArgumentException("Points list cannot be null");
        }
        if (cuttingPlane == null) {
            throw new IllegalArgumentException("Cutting plane cannot be null");
        }
        this.points = new ArrayList<>(points);
        this.cuttingPlane = cuttingPlane;
    }

    /**
     * Return the ordered list of section points.
     */
    public List<Vec3> getPoints() {
        return points;
    }

    /**
     * Return the number of points in the section.
     */
    public int pointCount() {
        return points.size();
    }

    /**
     * Return the cutting plane that produced this section.
     */
    public Plane getCuttingPlane() {
        return cuttingPlane;
    }

    /**
     * Check if the section is empty.
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }

    /**
     * Compute the approximate area of the section polygon.
     * Uses the projected 2D area on the plane's local coordinate system.
     */
    public float computeArea() {
        if (points.size() < 3) {
            return 0f;
        }
        // Project points onto the plane's local 2D coordinate system
        Vec3 normal = cuttingPlane.normal;
        // Find two orthogonal vectors on the plane
        Vec3 up = Vec3.UNIT_Y;
        if (Math.abs(normal.dot(up)) > 0.999f) {
            up = Vec3.UNIT_X;
        }
        Vec3 right = normal.cross(up).normalize();
        Vec3 forward = right.cross(normal).normalize();

        // Project to 2D
        double[] xs = new double[points.size()];
        double[] ys = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            xs[i] = points.get(i).dot(right);
            ys[i] = points.get(i).dot(forward);
        }

        // Shoelace formula
        double area = 0;
        int n = points.size();
        for (int i = 0; i < n; i++) {
            int j = (i + 1) % n;
            area += xs[i] * ys[j];
            area -= xs[j] * ys[i];
        }
        return (float) (Math.abs(area) / 2.0);
    }

    @Override
    public String toString() {
        return "Section{points=" + points.size() + ", plane=" + cuttingPlane + "}";
    }
}
