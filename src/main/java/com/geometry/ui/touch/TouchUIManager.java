package com.geometry.ui.touch;

import com.geometry.ui.UIInteractionMode;
import com.geometry.ui.ViewMode;
import com.geometry.ui.bridge.UIEventBridge;

/**
 * Phase 11 - Touch-optimized UI manager.
 *
 * Manages touch-specific UI behavior:
 *   - Mode switching (whiteboard/desktop)
 *   - Touch gesture handling
 *   - UI size adjustments for touch targets
 *
 * This manager adjusts UI parameters based on the interaction mode
 * but does not handle raw touch input (that is handled by
 * CanvasInteractionLayer).
 *
 * Not thread-safe.
 */
public class TouchUIManager {

    /** The current UI interaction mode. */
    private UIInteractionMode mode;

    /** The UI event bridge. */
    private final UIEventBridge bridge;

    /** The touch layout configuration. */
    private TouchLayout touchLayout;

    /**
     * Create a TouchUIManager.
     *
     * @param bridge the UI event bridge (may be null in tests)
     */
    public TouchUIManager(UIEventBridge bridge) {
        this.mode = UIInteractionMode.DESKTOP;
        this.bridge = bridge;
        this.touchLayout = new TouchLayout(mode);
    }

    // ------------------------------------------------------------------
    // Mode switching
    // ------------------------------------------------------------------

    /**
     * Get the current interaction mode.
     */
    public UIInteractionMode getMode() {
        return mode;
    }

    /**
     * Set the interaction mode.
     *
     * @param mode the new mode
     */
    public void setMode(UIInteractionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("UIInteractionMode cannot be null");
        }
        this.mode = mode;
        this.touchLayout = new TouchLayout(mode);
        if (bridge != null) {
            bridge.submit(com.geometry.ui.UIEvent.interactionModeChange(mode));
        }
    }

    /**
     * Switch to whiteboard mode.
     */
    public void enableWhiteboard() {
        setMode(UIInteractionMode.WHITEBOARD);
    }

    /**
     * Switch to desktop mode.
     */
    public void enableDesktop() {
        setMode(UIInteractionMode.DESKTOP);
    }

    /**
     * Check if currently in whiteboard mode.
     */
    public boolean isWhiteboard() {
        return mode == UIInteractionMode.WHITEBOARD;
    }

    /**
     * Check if currently in desktop mode.
     */
    public boolean isDesktop() {
        return mode == UIInteractionMode.DESKTOP;
    }

    // ------------------------------------------------------------------
    // Touch layout
    // ------------------------------------------------------------------

    /**
     * Get the touch layout configuration.
     */
    public TouchLayout getTouchLayout() {
        return touchLayout;
    }

    /**
     * Get the minimum touch target size.
     */
    public int getMinTouchTargetSize() {
        return touchLayout.getMinTouchTargetSize();
    }

    /**
     * Get the toolbar button size.
     */
    public int getToolBarButtonSize() {
        return touchLayout.getToolBarButtonSize();
    }

    /**
     * Get the panel row height.
     */
    public int getPanelRowHeight() {
        return touchLayout.getPanelRowHeight();
    }

    /**
     * Get the canvas padding.
     */
    public int getCanvasPadding() {
        return touchLayout.getCanvasPadding();
    }
}
