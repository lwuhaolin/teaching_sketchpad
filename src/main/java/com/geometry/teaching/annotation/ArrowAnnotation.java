package com.geometry.teaching.annotation;

import com.geometry.core.math.Vec3;
import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Arrow annotation for pointing at geometry features.
 *
 * Renders a directional arrow from a start point to an end point.
 * Used for:
 *   - Pointing to vertices, edges, or faces
 *   - Indicating direction (vectors, normals)
 *   - Showing measurement ranges
 *
 * The arrow consists of:
 *   - A line segment from start to end
 *   - An arrowhead at the end point
 *
 * Both points are in world space (z = 0 for 2D mode).
 *
 * Not thread-safe.
 */
public class ArrowAnnotation implements Annotation {

    private final Vec3 start;
    private final Vec3 end;
    private final float arrowSize;
    private final int colorR;
    private final int colorG;
    private final int colorB;

    // Default arrowhead size (relative to distance)
    private static final float DEFAULT_ARROW_SIZE = 0.3f;
    private static final int DEFAULT_COLOR_R = 255;
    private static final int DEFAULT_COLOR_G = 255;
    private static final int DEFAULT_COLOR_B = 0; // yellow by default for visibility

    /**
     * Create an arrow annotation.
     *
     * @param start world-space start point
     * @param end   world-space end point (arrow tip)
     */
    public ArrowAnnotation(Vec3 start, Vec3 end) {
        this(start, end, DEFAULT_ARROW_SIZE,
                DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B);
    }

    /**
     * Create an arrow annotation with custom color.
     *
     * @param start    world-space start point
     * @param end      world-space end point (arrow tip)
     * @param r        red component [0, 255]
     * @param g        green component [0, 255]
     * @param b        blue component [0, 255]
     * @throws IllegalArgumentException if start or end is null
     */
    public ArrowAnnotation(Vec3 start, Vec3 end, int r, int g, int b) {
        this(start, end, DEFAULT_ARROW_SIZE, r, g, b);
    }

    /**
     * Create an arrow annotation with custom size and color.
     *
     * @param start      world-space start point
     * @param end        world-space end point (arrow tip)
     * @param arrowSize  arrowhead size in world units
     * @param r          red component [0, 255]
     * @param g          green component [0, 255]
     * @param b          blue component [0, 255]
     * @throws IllegalArgumentException if start or end is null, or arrowSize <= 0
     */
    public ArrowAnnotation(Vec3 start, Vec3 end, float arrowSize, int r, int g, int b) {
        if (start == null) {
            throw new IllegalArgumentException("Start point cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("End point cannot be null");
        }
        if (arrowSize <= 0) {
            throw new IllegalArgumentException("Arrow size must be positive, got " + arrowSize);
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be in [0, 255]");
        }
        this.start = start;
        this.end = end;
        this.arrowSize = arrowSize;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Render this arrow annotation.
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

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    @Override
    public String getDescription() {
        return "ArrowAnnotation{start=" + start + ", end=" + end + "}";
    }

    /**
     * Get the start point.
     */
    public Vec3 getStart() {
        return start;
    }

    /**
     * Get the end point (arrow tip).
     */
    public Vec3 getEnd() {
        return end;
    }

    /**
     * Get the arrowhead size.
     */
    public float getArrowSize() {
        return arrowSize;
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
        ArrowAnnotation that = (ArrowAnnotation) o;
        return Float.compare(that.arrowSize, arrowSize) == 0
                && colorR == that.colorR
                && colorG == that.colorG
                && colorB == that.colorB
                && start.equals(that.start)
                && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + Float.floatToIntBits(arrowSize);
        result = 31 * result + colorR;
        result = 31 * result + colorG;
        result = 31 * result + colorB;
        return result;
    }

    @Override
    public String toString() {
        return "ArrowAnnotation{start=" + start + ", end=" + end
                + ", arrowSize=" + arrowSize
                + ", color=(" + colorR + "," + colorG + "," + colorB + ")}";
    }
}
