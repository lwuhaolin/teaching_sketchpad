package com.geometry.ui.input;

import com.geometry.ui.input.gesture.GestureRecognizer;
import com.geometry.ui.input.gesture.GestureEvent;

/**
 * Phase 13 - Whiteboard input handler.
 *
 * Handles touch and pen input for whiteboard mode.
 * Uses large touch tolerance (20 px) and auto-snapping.
 * Long-press enters precision mode.
 *
 * Gestures supported:
 *   - Tap: select
 *   - Drag: move / draw
 *   - Pinch: zoom
 *   - Double-tap: context menu
 */
public class WhiteboardInputMode implements InputHandler {

    /** The gesture recognizer for whiteboard gestures. */
    private final GestureRecognizer gestureRecognizer;

    /** Whether precision mode is currently active (long press). */
    private boolean precisionMode;

    /** Touch down time in milliseconds (for long-press detection). */
    private long touchDownTime;

    /** Threshold in ms to enter precision mode (700 ms). */
    private static final long PRECISION_MODE_THRESHOLD = 700;

    public WhiteboardInputMode() {
        this.gestureRecognizer = new GestureRecognizer();
        this.precisionMode = false;
        this.touchDownTime = 0;
    }

    @Override
    public boolean supports(InputMode mode) {
        return mode == InputMode.WHITEBOARD;
    }

    @Override
    public void onMouseMove(int x, int y) {
        gestureRecognizer.processMove(x, y);
    }

    @Override
    public void onMouseDown(int x, int y) {
        this.touchDownTime = System.currentTimeMillis();
        this.precisionMode = false;
        gestureRecognizer.processDown(x, y);
    }

    @Override
    public void onMouseUp(int x, int y) {
        long duration = System.currentTimeMillis() - touchDownTime;
        if (duration >= PRECISION_MODE_THRESHOLD && !precisionMode) {
            this.precisionMode = true;
        }
        gestureRecognizer.processUp(x, y);
        this.precisionMode = false;
        this.touchDownTime = 0;
    }

    @Override
    public void onKeyPress(int keyCode) {
        // Whiteboard mode: keyboard is mostly disabled.
        // Exception: Escape cancels current gesture.
        if (keyCode == 27) { // ESC
            gestureRecognizer.cancel();
        }
    }

    @Override
    public void onKeyRelease(int keyCode) {
        // no-op
    }

    @Override
    public void onScroll(int delta) {
        // Scroll handled via pinch gesture in whiteboard mode.
    }

    @Override
    public void onPenDown(int x, int y, float pressure) {
        gestureRecognizer.processDown(x, y);
    }

    @Override
    public void onPenMove(int x, int y, float pressure) {
        gestureRecognizer.processMove(x, y);
    }

    @Override
    public void onPenUp(int x, int y, float pressure) {
        gestureRecognizer.processUp(x, y);
    }

    /**
     * Get the current precision mode state.
     */
    public boolean isPrecisionMode() {
        return precisionMode;
    }

    /**
     * Get the gesture recognizer for advanced use.
     */
    public GestureRecognizer getGestureRecognizer() {
        return gestureRecognizer;
    }
}
