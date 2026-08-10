package com.geometry.teaching.annotation;

import com.geometry.renderer.Renderer;
import com.geometry.scene.SceneObject;

/**
 * Phase 07 - Highlight annotation for emphasising geometry features.
 *
 * Renders a visual highlight (colored overlay or outline) around
 * a target {@link SceneObject}. Used for:
 *   - Drawing attention to a specific face, edge, or vertex
 *   - Showing selection state during teaching
 *   - Indicating measurement targets
 *
 * The highlight is rendered as a separate pass after the main geometry,
 * using a slightly expanded mesh or a different color.
 *
 * HighlightState controls the visual appearance:
 *   NORMAL  — no highlight (disabled)
 *   OUTLINE — wireframe outline in highlight color
 *   GLOW    — semi-transparent colored overlay
 *
 * Not thread-safe.
 */
public class HighlightAnnotation implements Annotation {

    /**
     * Highlight visual style.
     */
    public enum HighlightState {
        /** No highlight applied. */
        NORMAL,
        /** Wireframe outline only. */
        OUTLINE,
        /** Semi-transparent colored overlay. */
        GLOW
    }

    private final SceneObject target;
    private HighlightState state;
    private final int colorR;
    private final int colorG;
    private final int colorB;
    private final float alpha;

    // Default highlight color (bright red, high visibility)
    private static final int DEFAULT_COLOR_R = 255;
    private static final int DEFAULT_COLOR_G = 50;
    private static final int DEFAULT_COLOR_B = 50;
    private static final float DEFAULT_ALPHA = 0.4f;

    /**
     * Create a highlight annotation with default color and state.
     *
     * @param target the scene object to highlight
     * @throws IllegalArgumentException if target is null
     */
    public HighlightAnnotation(SceneObject target) {
        this(target, HighlightState.GLOW,
                DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B,
                DEFAULT_ALPHA);
    }

    /**
     * Create a highlight annotation with custom state and color.
     *
     * @param target the scene object to highlight
     * @param state  the highlight visual style
     * @param r      red component [0, 255]
     * @param g      green component [0, 255]
     * @param b      blue component [0, 255]
     * @param alpha  overlay alpha [0.0f, 1.0f] (only used for GLOW state)
     * @throws IllegalArgumentException if target is null
     */
    public HighlightAnnotation(SceneObject target, HighlightState state,
                                int r, int g, int b, float alpha) {
        if (target == null) {
            throw new IllegalArgumentException("Target SceneObject cannot be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("HighlightState cannot be null");
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be in [0, 255]");
        }
        if (alpha < 0f || alpha > 1f) {
            throw new IllegalArgumentException("Alpha must be in [0, 1], got " + alpha);
        }
        this.target = target;
        this.state = state;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.alpha = alpha;
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Render the highlight overlay.
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
        return "HighlightAnnotation{target=" + target.getId()
                + ", state=" + state + "}";
    }

    /**
     * Get the target SceneObject.
     */
    public SceneObject getTarget() {
        return target;
    }

    /**
     * Get the current highlight state.
     */
    public HighlightState getState() {
        return state;
    }

    /**
     * Set the highlight state.
     *
     * @param state new highlight state (cannot be null)
     */
    public void setState(HighlightState state) {
        if (state == null) {
            throw new IllegalArgumentException("HighlightState cannot be null");
        }
        this.state = state;
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
     * Get the overlay alpha [0.0f, 1.0f].
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Check if highlighting is active (state != NORMAL).
     */
    public boolean isHighlightActive() {
        return state != HighlightState.NORMAL;
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
        HighlightAnnotation that = (HighlightAnnotation) o;
        return Float.compare(that.alpha, alpha) == 0
                && colorR == that.colorR
                && colorG == that.colorG
                && colorB == that.colorB
                && state == that.state
                && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + colorR;
        result = 31 * result + colorG;
        result = 31 * result + colorB;
        result = 31 * result + Float.floatToIntBits(alpha);
        return result;
    }

    @Override
    public String toString() {
        return "HighlightAnnotation{target='" + target.getId() + "'"
                + ", state=" + state
                + ", color=(" + colorR + "," + colorG + "," + colorB + ")"
                + ", alpha=" + alpha + "}";
    }
}
