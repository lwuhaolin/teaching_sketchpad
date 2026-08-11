package com.geometry.ui.theme;

import com.geometry.ui.ToolbarSize;
import com.geometry.ui.input.InputMode;

import java.awt.*;

/**
 * Phase 14 - Modern education-themed colour palette.
 *
 * Design principles:
 *   - Soft, warm background colours (not clinical white)
 *   - Primary blue: #2B6CB0 — trust, knowledge
 *   - Accent colours for tools: clearly differentiated
 *   - High contrast for text on projectors / whiteboards
 *   - Rounded, friendly aesthetic appropriate for K-12 education
 *
 * All colours chosen for readability in classroom settings.
 */
public class EducationTheme {

    // ── Core palette ──────────────────────────────────────────────────

    /** App window background — soft blue-grey, not harsh white. */
    private final Color backgroundColor;

    /** Main panel / card background — clean white for content areas. */
    private final Color panelBackgroundColor;

    /** Header bar background — deep blue for brand identity. */
    private final Color headerColor;

    /** Header text colour — white for contrast on deep blue. */
    private final Color headerTextColor;

    /** Sidebar background — slightly darker than panel for depth. */
    private final Color sidebarBackgroundColor;

    /** Sidebar text colour — dark blue-grey. */
    private final Color sidebarTextColor;

    /** Sidebar active item background — light blue highlight. */
    private final Color sidebarActiveColor;

    /** Sidebar active item text — deep blue. */
    private final Color sidebarActiveTextColor;

    /** Accent / primary action colour — bright blue. */
    private final Color primaryColor;

    /** Primary text colour — dark blue-grey. */
    private final Color textColor;

    /** Secondary text colour — medium grey. */
    private final Color textSecondaryColor;

    /** Canvas / whiteboard background — very light blue tint. */
    private final Color canvasBackgroundColor;

    /** Canvas border colour — soft blue. */
    private final Color canvasBorderColor;

    /** Selection highlight colour — coral red. */
    private final Color selectionColor;

    /** Success / completed step colour — green. */
    private final Color successColor;

    /** Tool accent colours (index corresponds to tool order). */
    private static final Color[] TOOL_ACCENT_COLORS = {
            new Color(0x3B, 0x82, 0xF6), // select  — blue
            new Color(0x10, 0xB9, 0x81), // move    — green
            new Color(0xF5, 0x9E, 0x0B), // rotate  — amber
            new Color(0x8B, 0x5C, 0xF6), // scale   — purple
            new Color(0xEF, 0x44, 0x44), // cut     — red
            new Color(0x06, 0xB6, 0xD4), // unfold  — cyan
            new Color(0xF9, 0x73, 0x16), // measure — orange
            new Color(0xEC, 0x48, 0x99), // animation — pink
    };

    /**
     * Create the default education theme.
     */
    public EducationTheme() {
        this.backgroundColor      = new Color(0xF5, 0xF7, 0xFA);
        this.panelBackgroundColor = Color.WHITE;
        this.headerColor          = new Color(0xFF, 0xFF, 0xFF);
        this.headerTextColor      = new Color(0x1F, 0x29, 0x3B);
        this.sidebarBackgroundColor = new Color(0xF8, 0xFA, 0xFE);
        this.sidebarTextColor     = new Color(0x47, 0x55, 0x69);
        this.sidebarActiveColor   = new Color(0xDE, 0xEB, 0xF7);
        this.sidebarActiveTextColor = new Color(0x1E, 0x3A, 0x8A);
        this.primaryColor         = new Color(0x3A, 0x7A, 0xFE);
        this.textColor            = new Color(0x1E, 0x29, 0x3B);
        this.textSecondaryColor   = new Color(0x64, 0x74, 0x8B);
        this.canvasBackgroundColor = new Color(0xF8, 0xFA, 0xFC);
        this.canvasBorderColor    = new Color(0xE2, 0xE8, 0xF0);
        this.selectionColor       = new Color(0x2B, 0x6C, 0xB0);
        this.successColor         = new Color(0x10, 0xB9, 0x81);
    }

    // ── Accessors ─────────────────────────────────────────────────────

    public Color getBackgroundColor()          { return backgroundColor; }
    public Color getPanelBackgroundColor()     { return panelBackgroundColor; }
    public Color getHeaderColor()              { return headerColor; }
    public Color getHeaderTextColor()          { return headerTextColor; }
    public Color getSidebarBackgroundColor()   { return sidebarBackgroundColor; }
    public Color getSidebarTextColor()         { return sidebarTextColor; }
    public Color getSidebarActiveColor()       { return sidebarActiveColor; }
    public Color getSidebarActiveTextColor()   { return sidebarActiveTextColor; }
    public Color getPrimaryColor()             { return primaryColor; }
    public Color getTextColour()               { return textColor; }
    public Color getTextSecondaryColour()      { return textSecondaryColor; }
    public Color getCanvasBackgroundColor()    { return canvasBackgroundColor; }
    public Color getCanvasBorderColor()        { return canvasBorderColor; }
    public Color getSelectionColor()           { return selectionColor; }
    public Color getSuccessColor()             { return successColor; }

    /**
     * Get the accent colour for a tool at the given index.
     *
     * @param toolIndex 0-based index (0=select, 1=move, …)
     * @return the accent colour, or the default blue if out of range
     */
    public Color getToolAccentColor(int toolIndex) {
        if (toolIndex >= 0 && toolIndex < TOOL_ACCENT_COLORS.length) {
            return TOOL_ACCENT_COLORS[toolIndex];
        }
        return primaryColor;
    }

    /**
     * Get the recommended font for the given mode.
     *
     * @param mode the current input mode
     * @return the Font instance
     */
    public Font getFont(InputMode mode) {
        int size;
        switch (mode) {
            case WHITEBOARD: size = 20; break;
            case TABLET:     size = 18; break;
            default:         size = 14; break;
        }
        return new Font("Microsoft YaHei", Font.PLAIN, size);
    }

    /**
     * Get the recommended font for labels.
     */
    public Font getLabelFont(InputMode mode) {
        int size = mode == InputMode.WHITEBOARD ? 16 : 14;
        return new Font("Microsoft YaHei", Font.PLAIN, size);
    }

    // ── Backward-compatibility accessors ──────────────────────────────
    // These methods existed in Phase 11-13 and are kept for test/legacy
    // compatibility. Prefer the new typed accessors above.

    /** @deprecated Use {@link #getBackgroundColor()} instead. */
    @Deprecated
    public Color getToolbarColor() {
        return headerColor;
    }

    /** @deprecated Use {@link #getPrimaryColor()} instead. */
    @Deprecated
    public Color getActiveToolColor() {
        return primaryColor;
    }

    /** @deprecated Use {@link #getHeaderTextColor()} instead. */
    @Deprecated
    public Color getToolbarTextColor() {
        return headerTextColor;
    }

    /** @deprecated Use {@link #getCanvasBackgroundColor()} instead. */
    @Deprecated
    public Color getCanvasDrawColor() {
        return canvasBackgroundColor;
    }

    /**
     * Get the recommended font for buttons.
     */
    public Font getButtonFont(InputMode mode) {
        int size;
        if (mode == InputMode.WHITEBOARD) {
            size = 18;
        } else if (mode == InputMode.TABLET) {
            size = 16;
        } else {
            size = 14;
        }
        return new Font("Microsoft YaHei", Font.BOLD, size);
    }

    /**
     * Get the button height for the current mode.
     *
     * @param mode the input mode
     * @return button height in pixels
     */
    public int getButtonHeight(InputMode mode) {
        switch (mode) {
            case WHITEBOARD: return ToolbarSize.WHITEBOARD_BUTTON_HEIGHT;
            case TABLET:     return ToolbarSize.TABLET_BUTTON_HEIGHT;
            default:         return ToolbarSize.DESKTOP_BUTTON_HEIGHT;
        }
    }
}
