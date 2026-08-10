package com.geometry.ui.input;

/**
 * Phase 13 - Abstract input handler interface.
 *
 * Each concrete implementation handles a specific input mode
 * (Desktop, Whiteboard, Tablet).  Handlers receive raw screen
 * coordinates and translate them into UI actions through the
 * canvas interaction layer.
 *
 * All methods use screen coordinates (origin top-left).
 *
 * Not thread-safe.
 */
public interface InputHandler {

    /**
     * Check if this handler supports the given input mode.
     *
     * @param mode the mode to check
     * @return true if this handler is active for the mode
     */
    boolean supports(InputMode mode);

    /**
     * Called when the mouse / touch moves.
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     */
    void onMouseMove(int x, int y);

    /**
     * Called when a mouse / touch button is pressed down.
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     */
    void onMouseDown(int x, int y);

    /**
     * Called when a mouse / touch button is released.
     *
     * @param x screen x coordinate
     * @param y screen y coordinate
     */
    void onMouseUp(int x, int y);

    /**
     * Called when a key is pressed.
     *
     * @param keyCode the AWT key code
     */
    void onKeyPress(int keyCode);

    /**
     * Called when a key is released.
     *
     * @param keyCode the AWT key code
     */
    void onKeyRelease(int keyCode);

    /**
     * Called when the mouse wheel is scrolled.
     *
     * @param delta the scroll delta (positive = up)
     */
    void onScroll(int delta);

    /**
     * Called when a pen / stylus is used.
     *
     * @param x        screen x
     * @param y        screen y
     * @param pressure pressure in [0, 1]
     */
    default void onPenDown(int x, int y, float pressure) {
        // default: no-op
    }

    default void onPenMove(int x, int y, float pressure) {
        // default: no-op
    }

    default void onPenUp(int x, int y, float pressure) {
        // default: no-op
    }
}
