package com.geometry.interaction;

/**
 * Phase 05 - Interaction mode for the engine.
 *
 * Determines how user input is interpreted:
 *   - DESKTOP: traditional mouse/keyboard controls
 *   - WHITEBOARD: touch/pencil-oriented controls for smart boards
 *
 * Switching mode affects gesture interpretation and default action mapping.
 * Tools receive Actions regardless of mode; they do not know the input source.
 */
public enum InteractionMode {

    /**
     * Desktop mode: mouse left-click selects, drag moves, scroll zooms,
     * right-click rotates camera.
     */
    DESKTOP,

    /**
     * Whiteboard mode: single-finger tap selects and drags, two-finger
     * gestures control camera scale/rotation, pen for drawing.
     */
    WHITEBOARD
}
