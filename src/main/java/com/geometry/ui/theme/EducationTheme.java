package com.geometry.ui.theme;

import com.geometry.ui.ToolbarSize;
import com.geometry.ui.input.InputMode;

import java.awt.*;

/**
 * Phase 13 - Education-themed colour palette.
 *
 * Provides a consistent colour scheme for all UI components.
 * Colours are chosen for readability on projectors and whiteboards.
 *
 * Not thread-safe.
 */
public class EducationTheme {

    /** Background colour. */
    private final Color backgroundColor;

    /** Panel background colour. */
    private final Color panelBackgroundColor;

    /** Toolbar / header background. */
    private final Color toolbarColor;

    /** Toolbar text / accent. */
    private final Color toolbarTextColor;

    /** Active tool highlight. */
    private final Color activeToolColor;

    /** Canvas border. */
    private final Color canvasBorderColor;

    /** Selection colour. */
    private final Color selectionColor;

    /** Text colour. */
    private final Color textColor;

    /** Secondary text colour. */
    private final Color textSecondaryColor;

    /** Canvas drawing area colour. */
    private final Color canvasDrawColor;

    /**
     * Create the default education theme.
     */
    public EducationTheme() {
        this.backgroundColor       = new Color(0xF5, 0xF5, 0xF0);
        this.panelBackgroundColor  = new Color(0xFA, 0xFA, 0xF8);
        this.toolbarColor          = new Color(0x2C, 0x3E, 0x50);
        this.toolbarTextColor      = new Color(0xEC, 0xF0, 0xF1);
        this.activeToolColor       = new Color(0x34, 0x98, 0xDB);
        this.canvasBorderColor     = new Color(0xBD, 0xC3, 0xC7);
        this.selectionColor        = new Color(0xE7, 0x4C, 0x3C);
        this.textColor             = new Color(0x2C, 0x3E, 0x50);
        this.textSecondaryColor    = new Color(0x7F, 0x8C, 0x8D);
        this.canvasDrawColor       = Color.WHITE;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public Color getBackgroundColor()       { return backgroundColor; }
    public Color getPanelBackgroundColor()  { return panelBackgroundColor; }
    public Color getToolbarColor()          { return toolbarColor; }
    public Color getToolbarTextColor()      { return toolbarTextColor; }
    public Color getActiveToolColor()       { return activeToolColor; }
    public Color getCanvasBorderColor()     { return canvasBorderColor; }
    public Color getSelectionColor()        { return selectionColor; }
    public Color getTextColour()            { return textColor; }
    public Color getTextSecondaryColour()   { return textSecondaryColor; }
    public Color getCanvasDrawColor()       { return canvasDrawColor; }

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
}
