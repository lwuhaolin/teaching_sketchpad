package com.geometry.ui.touch;

import com.geometry.ui.UIInteractionMode;
import com.geometry.ui.ToolbarSize;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.ui.toolbar.QuickToolBar;
import com.geometry.ui.toolbar.ToolBar;

/**
 * Phase 11 - Touch-optimized layout adapter.
 *
 * Adjusts UI component sizes and positions for touch interaction.
 * When in WHITEBOARD mode, panels use larger touch targets and
 * simplified layouts.
 *
 * This class provides helper methods for determining touch-friendly
 * dimensions. It does NOT create UI components — it only provides
 * sizing hints.
 *
 * Not thread-safe.
 */
public class TouchLayout {

    /** The current interaction mode. */
    private final UIInteractionMode mode;

    /**
     * Create a TouchLayout for the given mode.
     *
     * @param mode the UI interaction mode
     */
    public TouchLayout(UIInteractionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("UIInteractionMode cannot be null");
        }
        this.mode = mode;
    }

    /**
     * Get the minimum touch target size in pixels.
     * Windows accessibility guidelines recommend 44x44 for touch.
     * Whiteboard targets 60x60 for visibility at arm's length.
     */
    public int getMinTouchTargetSize() {
        return mode == UIInteractionMode.WHITEBOARD ? 60 : 44;
    }

    /**
     * Get the toolbar button size for the current mode.
     */
    public int getToolBarButtonSize() {
        return mode == UIInteractionMode.WHITEBOARD
                ? QuickToolBar.QUICK_BUTTON_WIDTH
                : ToolBar.TOOL_BUTTON_WIDTH;
    }

    /**
     * Get the panel header height for the current mode.
     */
    public int getPanelHeaderHeight() {
        return mode == UIInteractionMode.WHITEBOARD ? 40 : 28;
    }

    /**
     * Get the panel row height for tree items.
     */
    public int getPanelRowHeight() {
        return mode == UIInteractionMode.WHITEBOARD ? 36 : 24;
    }

    /**
     * Get the panel control button height.
     */
    public int getControlButtonHeight() {
        return mode == UIInteractionMode.WHITEBOARD ? 48 : 36;
    }

    /**
     * Check if touch-optimized layout should be used.
     */
    public boolean isTouchOptimized() {
        return mode == UIInteractionMode.WHITEBOARD;
    }

    /**
     * Get the recommended panel width for whiteboard mode.
     */
    public int getPanelWidth() {
        return mode == UIInteractionMode.WHITEBOARD ? 260 : 220;
    }

    /**
     * Get the recommended canvas padding in pixels.
     * Whiteboard mode has larger padding to avoid accidental touches.
     */
    public int getCanvasPadding() {
        return mode == UIInteractionMode.WHITEBOARD ? 16 : 8;
    }
}
