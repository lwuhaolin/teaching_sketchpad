package com.geometry.ui.input;

import com.geometry.ui.input.gesture.GestureRecognizer;

/**
 * Phase 13 - Tablet input handler.
 *
 * Handles hybrid touch + pen input for tablet mode.
 * Medium touch tolerance (12 px).  Pen pressure is used
 * for drawing tools; touch is used for navigation.
 */
public class TabletInputMode implements InputHandler {

    /** The gesture recognizer for tablet gestures. */
    private final GestureRecognizer gestureRecognizer;

    /** Last known pen pressure. */
    private float lastPressure;

    public TabletInputMode() {
        this.gestureRecognizer = new GestureRecognizer();
        this.lastPressure = 0f;
    }

    @Override
    public boolean supports(InputMode mode) {
        return mode == InputMode.TABLET;
    }

    @Override
    public void onMouseMove(int x, int y) {
        gestureRecognizer.processMove(x, y);
    }

    @Override
    public void onMouseDown(int x, int y) {
        gestureRecognizer.processDown(x, y);
    }

    @Override
    public void onMouseUp(int x, int y) {
        gestureRecognizer.processUp(x, y);
    }

    @Override
    public void onKeyPress(int keyCode) {
        // Tablet may support a modifier key for precision.
    }

    @Override
    public void onKeyRelease(int keyCode) {
        // no-op
    }

    @Override
    public void onScroll(int delta) {
        // Tablet: use pinch gesture or tilt wheel.
    }

    @Override
    public void onPenDown(int x, int y, float pressure) {
        this.lastPressure = Math.max(0f, Math.min(1f, pressure));
        gestureRecognizer.processDown(x, y);
    }

    @Override
    public void onPenMove(int x, int y, float pressure) {
        this.lastPressure = Math.max(0f, Math.min(1f, pressure));
        gestureRecognizer.processMove(x, y);
    }

    @Override
    public void onPenUp(int x, int y, float pressure) {
        this.lastPressure = 0f;
        gestureRecognizer.processUp(x, y);
    }

    /**
     * Get the last known pen pressure.
     */
    public float getLastPressure() {
        return lastPressure;
    }

    /**
     * Get the gesture recognizer.
     */
    public GestureRecognizer getGestureRecognizer() {
        return gestureRecognizer;
    }
}
