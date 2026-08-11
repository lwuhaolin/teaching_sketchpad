package com.geometry.ui.theme;

import java.awt.*;

/**
 * Phase 14 - Shared visual style constants for the product-level education theme.
 *
 * All numeric values are chosen for readability on projectors,
 * whiteboards, tablets, and desktop screens.
 *
 * Not thread-safe (constants are immutable).
 */
public class UIStyle {

    private UIStyle() {
        // Utility class — prevent instantiation
    }

    // ------------------------------------------------------------------
    // Colours
    // ------------------------------------------------------------------

    /** Main background colour (soft blue-grey). */
    public static final int COLOR_BACKGROUND = 0xF0F4FA;

    /** Panel / card background colour (white). */
    public static final int COLOR_PANEL_BG = 0xFFFFFF;

    /** Header / title bar background (deep blue). */
    public static final int COLOR_HEADER_BG = 0x1E3A8A;

    /** Header text colour (white). */
    public static final int COLOR_HEADER_TEXT = 0xFFFFFF;

    /** Sidebar background (very light blue). */
    public static final int COLOR_SIDEBAR_BG = 0xF8FAFE;

    /** Sidebar text colour (medium blue-grey). */
    public static final int COLOR_SIDEBAR_TEXT = 0x475569;

    /** Sidebar active item background (light blue). */
    public static final int COLOR_SIDEBAR_ACTIVE = 0xDEF6FF;

    /** Sidebar active item text (deep blue). */
    public static final int COLOR_SIDEBAR_ACTIVE_TEXT = 0x1E3A8A;

    /** Primary action colour (blue). */
    public static final int COLOR_PRIMARY = 0x2B6CB0;

    /** Active tool highlight colour. */
    public static final int COLOR_ACTIVE_TOOL = 0x3B82F6;

    /** Canvas border colour (soft blue). */
    public static final int COLOR_CANVAS_BORDER = 0xBFDCFF;

    /** Canvas / whiteboard background (very light blue tint). */
    public static final int COLOR_CANVAS_DRAW = 0xFAFBFF;

    /** Selection box colour (blue). */
    public static final int COLOR_SELECTION = 0x2B6CB0;

    /** Success / completed step colour (green). */
    public static final int COLOR_SUCCESS = 0x10B981;

    /** Text colour for primary labels. */
    public static final int COLOR_TEXT = 0x1E293B;

    /** Text colour for secondary info. */
    public static final int COLOR_TEXT_SECONDARY = 0x64748B;

    /** Tool accent colours (in order: select, move, rotate, scale, cut, unfold, measure, animation). */
    public static final int[] TOOL_ACCENT_COLORS = {
            0x3B82F6, // select  — blue
            0x10B981, // move    — green
            0xF59E0B, // rotate  — amber
            0x8B5CF6, // scale   — purple
            0xEF4444, // cut     — red
            0x06B6D4, // unfold  — cyan
            0xF97316, // measure — orange
            0xEC4899, // animation — pink
    };

    // ------------------------------------------------------------------
    // Font sizes
    // ------------------------------------------------------------------

    /** Default font size for labels (14 px). */
    public static final int FONT_SIZE_LABEL = 14;

    /** Font size for toolbar buttons (desktop). */
    public static final int FONT_SIZE_BUTTON = 13;

    /** Font size for panel headers (15 px). */
    public static final int FONT_SIZE_HEADER = 15;

    /** Font size for gesture hints (13 px). */
    public static final int FONT_SIZE_HINT = 13;

    /** Font size for header title (18 px). */
    public static final int FONT_SIZE_TITLE = 18;

    /** Font size for status bar (13 px). */
    public static final int FONT_SIZE_STATUS = 13;

    // ------------------------------------------------------------------
    // Spacing & sizing
    // ------------------------------------------------------------------

    /** Default padding between panels (8 px). */
    public static final int PADDING_DEFAULT = 8;

    /** Padding around canvas (12 px). */
    public static final int PADDING_CANVAS = 12;

    /** Border radius for rounded panels (8 px). */
    public static final int BORDER_RADIUS = 8;

    /** Border radius for buttons (6 px). */
    public static final int BUTTON_RADIUS = 6;

    /** Sidebar width (desktop). */
    public static final int SIDEBAR_WIDTH = 58;

    /** Header height. */
    public static final int HEADER_HEIGHT = 52;

    /** Bottom toolbar height (desktop). */
    public static final int BOTTOM_TOOLBAR_HEIGHT = 68;

    // ------------------------------------------------------------------
    // Button sizes
    // ------------------------------------------------------------------

    /** Desktop button height. */
    public static final int DESKTOP_BUTTON_HEIGHT = 44;

    /** Tablet button height. */
    public static final int TABLET_BUTTON_HEIGHT = 52;

    /** Whiteboard button height. */
    public static final int WHITEBOARD_BUTTON_HEIGHT = 64;

    /** Desktop tool button width. */
    public static final int DESKTOP_BUTTON_WIDTH = 60;

    /** Whiteboard tool button width. */
    public static final int WHITEBOARD_BUTTON_WIDTH = 80;

    /** Touch target size (desktop). */
    public static final int TOUCH_TARGET_WIDTH_DESKTOP = 44;

    /** Touch target size (tablet). */
    public static final int TOUCH_TARGET_WIDTH_TABLET = 56;

    /** Touch target size (whiteboard). */
    public static final int TOUCH_TARGET_WIDTH_WHITEBOARD = 72;
}
