package com.geometry.ui;

/**
 * Phase 11 - UI interaction mode.
 *
 * Determines the size and behavior of UI controls:
 *   - WHITEBOARD: large touch-friendly controls (buttons >= 60px)
 *   - DESKTOP: standard mouse-driven controls (buttons ~24px)
 *
 * This mode affects:
 *   - Toolbar button sizes
 *   - Panel layout density
 *   - Touch target sizing
 *
 * Does NOT affect:
 *   - Core interaction logic (that is handled by InteractionMode)
 *   - Rendering pipeline
 *
 * Not thread-safe.
 */
public enum UIInteractionMode {

    /**
     * Desktop mode: mouse and keyboard primary input.
     * Compact UI layout with small controls.
     */
    DESKTOP,

    /**
     * Whiteboard mode: touch and pen primary input.
     * Large controls for finger/pen interaction.
     */
    WHITEBOARD
}
