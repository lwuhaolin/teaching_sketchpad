package com.geometry.ui.input;

/**
 * Phase 13 - Input mode representing the interaction style.
 *
 * Three modes are supported:
 *   - DESKTOP: mouse and keyboard (pointer precision)
 *   - WHITEBOARD: large touch targets, no precision (finger / projector)
 *   - TABLET: hybrid touch + pen (stylus precision)
 *
 * Switching between modes changes layout sizing, touch tolerance,
 * and which input handlers are active.
 *
 * Not thread-safe.
 */
public enum InputMode {

    /**
     * Desktop mode — mouse + keyboard.
     * Small touch targets, precise pointer, full tool set.
     */
    DESKTOP,

    /**
     * Whiteboard mode — finger touch.
     * Large touch targets, high tolerance, simplified tool set.
     */
    WHITEBOARD,

    /**
     * Tablet mode — pen + touch hybrid.
     * Medium-large targets, precision pen input available.
     */
    TABLET
}
