package com.geometry.geometry.analysis;

import com.geometry.core.math.Vec3;
import com.geometry.geometry.analysis.Section;
import com.geometry.geometry.cutting.Plane;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 08 - Analyzes a cross-section to determine its geometric type.
 *
 * Based on the number of points and their arrangement, classifies the section
 * as a polygon, approximate circle, approximate ellipse, etc.
 *
 * Note: This is a basic analyzer. Full geometric fitting (circle, ellipse)
 * is deferred to future phases.
 */
public class SectionAnalyzer {

    /** Section shape types. */
    public enum ShapeType {
        TRIANGLE,    // 3 points
        QUADRILATERAL, // 4 points
        POLYGON,     // 5+ points
        APPROX_CIRCLE, // many points, roughly circular
        UNKNOWN
    }

    /**
     * Analyze a section and determine its shape type.
     *
     * @param section the cross-section to analyze
     * @return the classified shape type
     */
    public static ShapeType analyze(Section section) {
        if (section == null || section.isEmpty()) {
            return ShapeType.UNKNOWN;
        }

        int count = section.pointCount();
        if (count < 3) {
            return ShapeType.UNKNOWN;
        }

        if (count == 3) {
            return ShapeType.TRIANGLE;
        }

        if (count == 4) {
            return ShapeType.QUADRILATERAL;
        }

        if (count >= 5 && count <= 8) {
            return ShapeType.POLYGON;
        }

        // For many points, check if approximately circular
        if (count > 8) {
            return isApproximatelyCircular(section) ? ShapeType.APPROX_CIRCLE : ShapeType.POLYGON;
        }

        return ShapeType.POLYGON;
    }

    /**
     * Check if the section points form an approximately circular shape.
     * Uses the ratio of min/max distance from centroid.
     */
    private static boolean isApproximatelyCircular(Section section) {
        List<Vec3> points = section.getPoints();
        if (points.size() < 6) {
            return false;
        }

        // Compute centroid
        float cx = 0, cy = 0, cz = 0;
        for (Vec3 p : points) {
            cx += p.x;
            cy += p.y;
            cz += p.z;
        }
        cx /= points.size();
        cy /= points.size();
        cz /= points.size();
        Vec3 centroid = new Vec3(cx, cy, cz);

        // Compute distances from centroid
        float minDist = Float.MAX_VALUE;
        float maxDist = Float.MIN_VALUE;
        for (Vec3 p : points) {
            float d = p.subtract(centroid).length();
            if (d < minDist) minDist = d;
            if (d > maxDist) maxDist = d;
        }

        // If ratio is close to 1, it's approximately circular
        if (maxDist < 1e-6f) {
            return false;
        }
        float ratio = minDist / maxDist;
        return ratio > 0.85f;
    }

    /**
     * Generate a Section from a list of intersection points.
     * Points should already lie on the cutting plane.
     */
    public static Section fromPoints(List<Vec3> points, Plane plane) {
        if (points == null || plane == null) {
            return new Section(new ArrayList<>(), plane);
        }
        return new Section(new ArrayList<>(points), plane);
    }

    /**
     * Extract section points from the intersection points computed during cutting.
     *
     * @param intersectionPoints list of intersection points from the cutter
     * @param plane the cutting plane
     * @return a Section, or null if no points
     */
    public static Section extractSection(List<Vec3> intersectionPoints, Plane plane) {
        if (intersectionPoints == null || intersectionPoints.isEmpty()) {
            return null;
        }
        return new Section(new ArrayList<>(intersectionPoints), plane);
    }
}
