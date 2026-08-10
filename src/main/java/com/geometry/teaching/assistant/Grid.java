package com.geometry.teaching.assistant;

import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Grid helper for 2D teaching mode.
 *
 * Renders a reference grid in the background to aid spatial reasoning.
 * The grid consists of:
 *   - Major lines at integer coordinates
 *   - Minor lines at half-integer coordinates
 *   - Number labels at major grid intersections
 *
 * The grid is rendered as a set of helper line annotations and is
 * only active in 2D orthographic mode.
 *
 * Not thread-safe.
 */
public class Grid implements com.geometry.teaching.annotation.Annotation {

    /**
     * Grid density level.
     */
    public enum Density {
        /** Sparse grid — major lines only (spacing = 2 units). */
        SPARSE,
        /** Medium grid — major + minor lines (spacing = 1 / 0.5 units). */
        MEDIUM,
        /** Dense grid — fine subdivisions (spacing = 0.5 / 0.25 units). */
        DENSE
    }

    private final Density density;
    private final int gridSize; // total grid extent (±gridSize from origin)
    private final int colorR;
    private final int colorG;
    private final int colorB;
    private final float alpha;
    private boolean visible;

    // Default settings
    private static final int DEFAULT_GRID_SIZE = 10;
    private static final int DEFAULT_COLOR_R = 80;
    private static final int DEFAULT_COLOR_G = 80;
    private static final int DEFAULT_COLOR_B = 80;
    private static final float DEFAULT_ALPHA = 0.3f;

    /**
     * Create a grid with default settings (MEDIUM density, 10x10 units).
     */
    public Grid() {
        this(Density.MEDIUM, DEFAULT_GRID_SIZE,
                DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B,
                DEFAULT_ALPHA);
    }

    /**
     * Create a grid with custom settings.
     *
     * @param density   grid density level
     * @param gridSize  total extent in each direction (e.g. 10 = from -10 to +10)
     * @param r         red component [0, 255]
     * @param g         green component [0, 255]
     * @param b         blue component [0, 255]
     * @param alpha     opacity [0.0, 1.0]
     * @throws IllegalArgumentException if density or gridSize is invalid
     */
    public Grid(Density density, int gridSize, int r, int g, int b, float alpha) {
        if (density == null) {
            throw new IllegalArgumentException("Density cannot be null");
        }
        if (gridSize <= 0) {
            throw new IllegalArgumentException("Grid size must be positive, got " + gridSize);
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be in [0, 255]");
        }
        if (alpha < 0f || alpha > 1f) {
            throw new IllegalArgumentException("Alpha must be in [0, 1], got " + alpha);
        }
        this.density = density;
        this.gridSize = gridSize;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.alpha = alpha;
        this.visible = true;
    }

    // ------------------------------------------------------------------
    // Annotation interface
    // ------------------------------------------------------------------

    /**
     * Render the grid as annotation overlays.
     *
     * For headless/mock renderers this is a no-op.
     *
     * @param renderer the active renderer
     */
    @Override
    public void render(Renderer renderer) {
        if (renderer == null || !visible) {
            return;
        }
        renderer.renderAnnotation(this);
    }

    @Override
    public String getDescription() {
        return "Grid{density=" + density + ", size=" + gridSize + ", visible=" + visible + "}";
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the grid density.
     */
    public Density getDensity() {
        return density;
    }

    /**
     * Get the grid extent (±this value from origin).
     */
    public int getGridSize() {
        return gridSize;
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

    /**
     * Check if the grid is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set grid visibility.
     *
     * @param visible true to show, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the spacing between major grid lines in world units.
     */
    public float getMajorSpacing() {
        switch (density) {
            case SPARSE: return 2.0f;
            case MEDIUM: return 1.0f;
            case DENSE:  return 0.5f;
            default:     return 1.0f;
        }
    }

    /**
     * Get the spacing between minor grid lines in world units.
     */
    public float getMinorSpacing() {
        switch (density) {
            case SPARSE: return 2.0f; // no minor lines
            case MEDIUM: return 0.5f;
            case DENSE:  return 0.25f;
            default:     return 0.5f;
        }
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
        Grid grid = (Grid) o;
        return gridSize == grid.gridSize
                && colorR == grid.colorR
                && colorG == grid.colorG
                && colorB == grid.colorB
                && Float.compare(grid.alpha, alpha) == 0
                && visible == grid.visible
                && density == grid.density;
    }

    @Override
    public int hashCode() {
        int result = density.hashCode();
        result = 31 * result + gridSize;
        result = 31 * result + colorR;
        result = 31 * result + colorG;
        result = 31 * result + colorB;
        result = 31 * result + Float.floatToIntBits(alpha);
        result = 31 * result + (visible ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Grid{density=" + density + ", size=" + gridSize
                + ", color=(" + colorR + "," + colorG + "," + colorB + ")"
                + ", alpha=" + alpha + ", visible=" + visible + "}";
    }
}
