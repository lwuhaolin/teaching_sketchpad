package com.geometry.ui.input;

/**
 * Phase 13 - Desktop input handler.
 *
 * Handles mouse and keyboard input for desktop mode.
 * Uses standard precision (small tolerance, no snapping).
 *
 * Keyboard shortcuts:
 *   - Space: play/pause animation
 *   - Ctrl+Z: undo
 *   - Delete: delete selected
 *   - Arrow keys: step through lesson
 */
public class DesktopInputMode implements InputHandler {

    /** Screen position of the last known mouse. */
    private int lastX;
    private int lastY;

    @Override
    public boolean supports(InputMode mode) {
        return mode == InputMode.DESKTOP;
    }

    @Override
    public void onMouseMove(int x, int y) {
        this.lastX = x;
        this.lastY = y;
    }

    @Override
    public void onMouseDown(int x, int y) {
        this.lastX = x;
        this.lastY = y;
    }

    @Override
    public void onMouseUp(int x, int y) {
        this.lastX = x;
        this.lastY = y;
    }

    @Override
    public void onKeyPress(int keyCode) {
        // Desktop keyboard shortcuts are handled by the
        // ApplicationWindow key listener.  This handler
        // merely records that a key was pressed.
    }

    @Override
    public void onKeyRelease(int keyCode) {
        // no-op
    }

    @Override
    public void onScroll(int delta) {
        // Scroll handling is delegated to the OpenGL camera
        // through the CanvasInteractionLayer.
    }

    /**
     * Get the last known mouse x coordinate.
     */
    public int getLastX() {
        return lastX;
    }

    /**
     * Get the last known mouse y coordinate.
     */
    public int getLastY() {
        return lastY;
    }
}
