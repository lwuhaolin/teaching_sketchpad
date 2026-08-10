package com.geometry.ui.canvas;

import com.geometry.scene.SceneObject;

/**
 * Phase 11 - Overlay renderer for canvas annotations.
 *
 * Draws visual overlays on top of the OpenGL canvas without modifying
 * the scene geometry. Used for:
 *   - Selection rectangles
 *   - Gesture hints (e.g. "drag to move")
 *   - Touch focus indicators
 *
 * This class is UI-only and does not depend on OpenGL or any rendering
 * backend. It provides layout information for overlay drawing.
 *
 * Not thread-safe.
 */
public class OverlayRenderer {

    /**
     * Types of overlays that can be rendered.
     */
    public enum OverlayType {
        /** Selection rectangle around a picked object. */
        SELECTION_BOX,
        /** Hint text for a gesture (e.g. "drag to move"). */
        GESTURE_HINT,
        /** Touch/focus indicator dot. */
        TOUCH_INDICATOR,
        /** Crosshair cursor. */
        CROSSHAIR
    }

    /** The overlay type currently being displayed. */
    private OverlayType currentOverlay;

    /** Overlay position in screen pixels (origin top-left). */
    private int overlayX;
    private int overlayY;

    /** Overlay width and height in pixels. */
    private int overlayWidth;
    private int overlayHeight;

    /** Whether the overlay is currently visible. */
    private boolean visible;

    /**
     * Create an OverlayRenderer.
     */
    public OverlayRenderer() {
        this.currentOverlay = null;
        this.overlayX = 0;
        this.overlayY = 0;
        this.overlayWidth = 0;
        this.overlayHeight = 0;
        this.visible = false;
    }

    // ------------------------------------------------------------------
    // Overlay control
    // ------------------------------------------------------------------

    /**
     * Show a selection box overlay.
     *
     * @param x      left edge in screen pixels
     * @param y      top edge in screen pixels
     * @param width  width in pixels
     * @param height height in pixels
     */
    public void showSelectionBox(int x, int y, int width, int height) {
        this.currentOverlay = OverlayType.SELECTION_BOX;
        this.overlayX = x;
        this.overlayY = y;
        this.overlayWidth = width;
        this.overlayHeight = height;
        this.visible = true;
    }

    /**
     * Show a gesture hint overlay.
     *
     * @param x    x position in screen pixels
     * @param y    y position in screen pixels
     * @param text hint text to display
     */
    public void showGestureHint(int x, int y, String text) {
        this.currentOverlay = OverlayType.GESTURE_HINT;
        this.overlayX = x;
        this.overlayY = y;
        this.visible = true;
    }

    /**
     * Show a touch indicator overlay.
     *
     * @param x center x in screen pixels
     * @param y center y in screen pixels
     */
    public void showTouchIndicator(int x, int y) {
        this.currentOverlay = OverlayType.TOUCH_INDICATOR;
        this.overlayX = x;
        this.overlayY = y;
        this.overlayWidth = 20;
        this.overlayHeight = 20;
        this.visible = true;
    }

    /**
     * Show a crosshair overlay.
     *
     * @param x center x in screen pixels
     * @param y center y in screen pixels
     */
    public void showCrosshair(int x, int y) {
        this.currentOverlay = OverlayType.CROSSHAIR;
        this.overlayX = x;
        this.overlayY = y;
        this.visible = true;
    }

    /**
     * Hide the current overlay.
     */
    public void hide() {
        this.visible = false;
        this.currentOverlay = null;
    }

    /**
     * Check if an overlay is currently visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get the current overlay type.
     */
    public OverlayType getCurrentOverlay() {
        return currentOverlay;
    }

    /**
     * Get the overlay x position.
     */
    public int getOverlayX() {
        return overlayX;
    }

    /**
     * Get the overlay y position.
     */
    public int getOverlayY() {
        return overlayY;
    }

    /**
     * Get the overlay width.
     */
    public int getOverlayWidth() {
        return overlayWidth;
    }

    /**
     * Get the overlay height.
     */
    public int getOverlayHeight() {
        return overlayHeight;
    }

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    /** Default touch indicator radius in pixels. */
    public static final int TOUCH_INDICATOR_RADIUS = 10;
    /** Default selection box stroke width in pixels. */
    public static final int SELECTION_BOX_WIDTH = 2;
    /** Default gesture hint font size in pixels. */
    public static final int GESTURE_HINT_FONT_SIZE = 16;
}
