package com.geometry.ui;

/**
 * Phase 11 - Constants for toolbar button sizing.
 *
 * Centralized size constants used by ToolBar and QuickToolBar,
 * as well as LayoutManager and TouchLayout.
 *
 * Not thread-safe (constants are immutable).
 */
public class ToolbarSize {

    /** Desktop toolbar button width in pixels. */
    public static final int DESKTOP_BUTTON_WIDTH = 48;

    /** Desktop toolbar button height in pixels. */
    public static final int DESKTOP_BUTTON_HEIGHT = 48;

    /** Whiteboard toolbar button width in pixels (>= 60px). */
    public static final int WHITEBOARD_BUTTON_WIDTH = 72;

    /** Whiteboard toolbar button height in pixels (>= 60px). */
    public static final int WHITEBOARD_BUTTON_HEIGHT = 72;

    /** Minimum touch target size recommended by accessibility guidelines. */
    public static final int MIN_TOUCH_TARGET = 44;

    /** Whiteboard minimum touch target (larger for arm's length use). */
    public static final int WHITEBOARD_MIN_TOUCH_TARGET = 60;

    /** Default panel width in desktop mode. */
    public static final int DESKTOP_PANEL_WIDTH = 220;

    /** Default panel width in whiteboard mode. */
    public static final int WHITEBOARD_PANEL_WIDTH = 260;

    /** Default toolbar height in desktop mode. */
    public static final int DESKTOP_TOOLBAR_HEIGHT = 52;

    /** Default toolbar height in whiteboard mode. */
    public static final int WHITEBOARD_TOOLBAR_HEIGHT = 80;

    /** Tablet button height in pixels. */
    public static final int TABLET_BUTTON_HEIGHT = 52;

    private ToolbarSize() {
        // Utility class — prevent instantiation
    }
}
