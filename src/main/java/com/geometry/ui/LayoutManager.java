package com.geometry.ui;

/**
 * Phase 11 - Layout manager for the UI workspace.
 *
 * Determines the positions and sizes of all UI panels based on
 * the current UIInteractionMode (WHITEBOARD or DESKTOP).
 *
 * Layout is a simple grid:
 *
 *   +------------------+  +------------------+
 *   |  Toolbar         |  |  TeachingPanel   |
 *   +------------------+  +------------------+
 *   |                  |  |                  |
 *   |  OpenGL Canvas   |  |  PropertyPanel   |
 *   |                  |  |                  |
 *   +------------------+  +------------------+
 *   |  SceneTreePanel  |  |  AnimationPanel  |
 *   +------------------+  +------------------+
 *
 * Not thread-safe.
 */
public class LayoutManager {

    /** The current UI interaction mode. */
    private UIInteractionMode mode;

    /** Total window width in pixels. */
    private int windowWidth;

    /** Total window height in pixels. */
    private int windowHeight;

    /**
     * Create a LayoutManager with desktop mode.
     */
    public LayoutManager() {
        this.mode = UIInteractionMode.DESKTOP;
        this.windowWidth = 1024;
        this.windowHeight = 768;
    }

    /**
     * Create a LayoutManager with the given mode and window size.
     *
     * @param mode the UI interaction mode
     * @param width  the window width
     * @param height the window height
     */
    public LayoutManager(UIInteractionMode mode, int width, int height) {
        if (mode == null) {
            throw new IllegalArgumentException("UIInteractionMode cannot be null");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be positive");
        }
        this.mode = mode;
        this.windowWidth = width;
        this.windowHeight = height;
    }

    // ------------------------------------------------------------------
    // Mode
    // ------------------------------------------------------------------

    /**
     * Get the current UI interaction mode.
     */
    public UIInteractionMode getMode() {
        return mode;
    }

    /**
     * Set the UI interaction mode.
     *
     * @param mode the new mode
     */
    public void setMode(UIInteractionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("UIInteractionMode cannot be null");
        }
        this.mode = mode;
    }

    // ------------------------------------------------------------------
    // Window size
    // ------------------------------------------------------------------

    /**
     * Get the window width.
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * Get the window height.
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * Set the window size.
     *
     * @param width  the new width
     * @param height the new height
     */
    public void setWindowSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Window dimensions must be positive");
        }
        this.windowWidth = width;
        this.windowHeight = height;
    }

    // ------------------------------------------------------------------
    // Panel layout
    // ------------------------------------------------------------------

    /**
     * Get the toolbar layout.
     *
     * @return array of [x, y, width, height]
     */
    public int[] getToolbarLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            // Whiteboard: toolbar at top, full width, taller buttons
            int toolbarHeight = 80;
            return new int[]{0, 0, windowWidth, toolbarHeight};
        } else {
            // Desktop: toolbar at top, full width, standard height
            int toolbarHeight = 52;
            return new int[]{0, 0, windowWidth, toolbarHeight};
        }
    }

    /**
     * Get the canvas layout.
     *
     * @return array of [x, y, width, height]
     */
    public int[] getCanvasLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            int toolbarHeight = 80;
            int rightPanelWidth = 260;
            int bottomPanelHeight = 80;
            return new int[]{
                    0, toolbarHeight,
                    windowWidth - rightPanelWidth,
                    windowHeight - toolbarHeight - bottomPanelHeight
            };
        } else {
            int toolbarHeight = 52;
            int leftPanelWidth = 220;
            int rightPanelWidth = 240;
            int bottomPanelHeight = 100;
            return new int[]{
                    leftPanelWidth, toolbarHeight,
                    windowWidth - leftPanelWidth - rightPanelWidth,
                    windowHeight - toolbarHeight - bottomPanelHeight
            };
        }
    }

    /**
     * Get the scene tree panel layout.
     *
     * @return array of [x, y, width, height] or null if not shown in this mode
     */
    public int[] getSceneTreeLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            // Not shown in whiteboard mode
            return null;
        }
        int toolbarHeight = 52;
        return new int[]{
                0, toolbarHeight,
                220, windowHeight - toolbarHeight - 100
        };
    }

    /**
     * Get the property panel layout.
     *
     * @return array of [x, y, width, height] or null if not shown in this mode
     */
    public int[] getPropertyPanelLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            return null;
        }
        int toolbarHeight = 52;
        return new int[]{
                windowWidth - 240, toolbarHeight,
                240, windowHeight - toolbarHeight - 100
        };
    }

    /**
     * Get the teaching panel layout.
     *
     * @return array of [x, y, width, height] or null if not shown in this mode
     */
    public int[] getTeachingPanelLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            int toolbarHeight = 80;
            return new int[]{
                    windowWidth - 260, toolbarHeight,
                    260, windowHeight - toolbarHeight - 80
            };
        }
        int toolbarHeight = 52;
        return new int[]{
                windowWidth - 240, toolbarHeight,
                240, windowHeight - toolbarHeight - 100
        };
    }

    /**
     * Get the animation panel layout.
     *
     * @return array of [x, y, width, height] or null if not shown in this mode
     */
    public int[] getAnimationPanelLayout() {
        if (mode == UIInteractionMode.WHITEBOARD) {
            int toolbarHeight = 80;
            return new int[]{
                    0, windowHeight - 80,
                    windowWidth, 80
            };
        }
        int toolbarHeight = 52;
        return new int[]{
                0, windowHeight - 100,
                windowWidth, 100
        };
    }

    /**
     * Get the quick toolbar layout (whiteboard mode only).
     *
     * @return array of [x, y, width, height] or null in desktop mode
     */
    public int[] getQuickToolBarLayout() {
        if (mode == UIInteractionMode.DESKTOP) {
            return null;
        }
        int toolbarHeight = 80;
        return new int[]{0, toolbarHeight, windowWidth, 60};
    }

    // ------------------------------------------------------------------
    // Convenience
    // ------------------------------------------------------------------

    /**
     * Get the toolbar height for the current mode.
     */
    public int getToolbarHeight() {
        return mode == UIInteractionMode.WHITEBOARD ? 80 : 52;
    }

    /**
     * Get the bottom panel height for the current mode.
     */
    public int getBottomPanelHeight() {
        return mode == UIInteractionMode.WHITEBOARD ? 80 : 100;
    }

    /**
     * Get the button size for the current mode.
     *
     * @return button size in pixels (square)
     */
    public int getButtonSize() {
        return mode == UIInteractionMode.WHITEBOARD ? 60 : 48;
    }
}
