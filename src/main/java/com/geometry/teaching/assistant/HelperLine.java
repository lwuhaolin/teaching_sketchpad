package com.geometry.teaching.assistant;

import com.geometry.core.math.Vec3;
import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Helper line for geometric constructions.
 *
 * Helper lines are non-permanent visual aids used in teaching:
 *   - Perpendicular lines (dropped from a point to a line)
 *   - Extension lines (extended beyond endpoints)
 *   - Center lines (showing axes of symmetry)
 *   - Parallel lines
 *
 * Unlike Construction objects, HelperLine objects are purely visual
 * and do not produce GeometryObjects. They are rendered as overlay
 * annotations.
 *
 * Not thread-safe.
 */
public class HelperLine implements com.geometry.teaching.annotation.Annotation {

    private final Vec3 start;
    private final Vec3 end;
    private final HelperLineType type;
    private final int colorR;
    private final int colorG;
    private final int colorB;
    private final float alpha;

    // Default colors per type
    private static final int DEFAULT_COLOR_R = 150;
    private static final int DEFAULT_COLOR_G = 150;
    private static final int DEFAULT_COLOR_B = 150;
    private static final float DEFAULT_ALPHA = 0.6f;

    /**
     * Helper line visual type.
     */
    public enum HelperLineType {
        /** Solid line — used for construction guides. */
        SOLID,
        /** Dashed line — used for extension lines. */
        DASHED,
        /** Center line — alternating long/short segments. */
        CENTER
    }

    /**
     * Create a helper line with default styling.
     *
     * @param start start point in world space
     * @param end   end point in world space
     */
    public HelperLine(Vec3 start, Vec3 end) {
        this(start, end, HelperLineType.SOLID,
                DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B,
                DEFAULT_ALPHA);
    }

    /**
     * Create a helper line with custom styling.
     *
     * @param start    start point
     * @param end      end point
     * @param type     visual type (SOLID, DASHED, or CENTER)
     * @param r        red [0, 255]
     * @param g        green [0, 255]
     * @param b        blue [0, 255]
     * @param alpha    opacity [0.0, 1.0]
     * @throws IllegalArgumentException if start or end is null
     */
    public HelperLine(Vec3 start, Vec3 end, HelperLineType type,
                       int r, int g, int b, float alpha) {
        if (start == null) {
            throw new IllegalArgumentException("Start point cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End point cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("HelperLineType cannot be null");
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be in [0, 255]");
        }
        if (alpha < 0f || alpha > 1f) {
            throw new IllegalArgumentException("Alpha must be in [0, 1], got " + alpha);
        }
        this.start = start;
        this.end = end;
        this.type = type;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.alpha = alpha;
    }

    // ------------------------------------------------------------------
    // Annotation interface
    // ------------------------------------------------------------------

    /**
     * Render this helper line as an annotation overlay.
     *
     * @param renderer the active renderer
     */
    @Override
    public void render(Renderer renderer) {
        if (renderer == null) {
            return;
        }
        renderer.renderAnnotation(this);
    }

    @Override
    public String getDescription() {
        return "HelperLine{type=" + type + ", start=" + start + ", end=" + end + "}";
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the start point.
     */
    public Vec3 getStart() {
        return start;
    }

    /**
     * Get the end point.
     */
    public Vec3 getEnd() {
        return end;
    }

    /**
     * Get the line type.
     */
    public HelperLineType getType() {
        return type;
    }

    /**
     * Get the red component [0, 255].
     */
    public int getColorR() {
        return colorR;
    }

    /**
     * Get the green component [0, 255].
     */
    public int getColorG() {
        return colorG;
    }

    /**
     * Get the blue component [0, 255].
     */
    public int getColorB() {
        return colorB;
    }

    /**
     * Get the alpha [0.0, 1.0].
     */
    public float getAlpha() {
        return alpha;
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HelperLine that = (HelperLine) o;
        return Float.compare(that.alpha, alpha) == 0
                && colorR == that.colorR
                && colorG == that.colorG
                && colorB == that.colorB
                && type == that.type
                && start.equals(that.start)
                && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + colorR;
        result = 31 * result + colorG;
        result = 31 * result + colorB;
        result = 31 * result + Float.floatToIntBits(alpha);
        return result;
    }

    @Override
    public String toString() {
        return "HelperLine{start=" + start + ", end=" + end
                + ", type=" + type
                + ", color=(" + colorR + "," + colorG + "," + colorB + ")"
                + ", alpha=" + alpha + "}";
    }
}
