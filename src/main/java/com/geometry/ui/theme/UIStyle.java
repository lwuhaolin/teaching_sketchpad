package com.geometry.ui.theme;

/**
 * Phase 13 - Shared visual style constants for the education theme.
 *
 * All numeric values are chosen for readability on projectors,
 * whiteboards, and tablets.
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

    /** Main background colour (light education theme). */
    public static final int COLOR_BACKGROUND = 0xF5F5F0;

    /** Panel background colour. */
    public static final int COLOR_PANEL_BG = 0xFAFAF8;

    /** Toolbar background colour. */
    public static final int COLOR_TOOLBAR_BG = 0x2C3E50;

    /** Toolbar text / accent colour. */
    public static final int COLOR_TOOLBAR_TEXT = 0xECF0F1;

    /** Active tool highlight colour. */
    public static final int COLOR_ACTIVE_TOOL = 0x3498DB;

    /** Canvas border colour. */
    public static final int COLOR_CANVAS_BORDER = 0xBDC3C7;

    /** Selection box colour. */
    public static final int COLOR_SELECTION = 0xE74C3C;

    /** Text colour for labels. */
    public static final int COLOR_TEXT = 0x2C3E50;

    /** Text colour for secondary info. */
    public static final int COLOR_TEXT_SECONDARY = 0x7F8C8D;

    /** Canvas drawing area colour. */
    public static final int COLOR_CANVAS_DRAW = 0xFFFFFF;

    // ------------------------------------------------------------------
    // Font sizes
    // ------------------------------------------------------------------

    /** Default font size for labels (14 px). */
    public static final int FONT_SIZE_LABEL = 14;

    /** Font size for toolbar buttons (16 px, whiteboard: 20). */
    public static final int FONT_SIZE_BUTTON = 16;

    /** Font size for panel headers (15 px). */
    public static final int FONT_SIZE_HEADER = 15;

    /** Font size for gesture hints (16 px). */
    public static final int FONT_SIZE_HINT = 16;

    // ------------------------------------------------------------------
    // Spacing
    // ------------------------------------------------------------------

    /** Default padding between panels (8 px). */
    public static final int PADDING_DEFAULT = 8;

    /** Padding around canvas (16 px). */
    public static final int PADDING_CANVAS = 16;

    /** Border radius for rounded panels (4 px). */
    public static final int BORDER_RADIUS = 4;

    // ------------------------------------------------------------------
    // Touch targets
    // ------------------------------------------------------------------

    /** Minimum touch target width (whiteboard). */
    public static final int TOUCH_TARGET_WIDTH = 72;

    /** Minimum touch target height (whiteboard). */
    public static final int TOUCH_TARGET_HEIGHT = 72;

    /** Minimum touch target width (desktop). */
    public static final int TOUCH_TARGET_WIDTH_DESKTOP = 48;

    /** Minimum touch target height (desktop). */
    public static final int TOUCH_TARGET_HEIGHT_DESKTOP = 48;
}
