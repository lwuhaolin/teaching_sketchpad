package com.geometry.ui.input.gesture;

/**
 * Phase 13 - Simple gesture recognizer for whiteboard/tablet modes.
 *
 * Tracks touch down / move / up sequences and detects simple
 * gestures: tap, drag, double-tap.  Intentionally lightweight
 * — no complex gesture library dependency.
 *
 * Not thread-safe.
 */
public class GestureRecognizer {

    /** State of the current gesture. */
    /** State of the current gesture. */
    public enum State { IDLE, DRAGGING, TAPPING }

    private State state;

    /** Coordinates of the down event. */
    private int downX;
    private int downY;

    /** Time of the last tap in milliseconds. */
    private long lastTapTime;

    /** Number of consecutive taps (for double-tap detection). */
    private int tapCount;

    /** Threshold for double-tap (350 ms between taps). */
    private static final long DOUBLE_TAP_THRESHOLD = 350;

    /** Maximum distance for a tap vs drag (10 px). */
    private static final int TAP_MAX_DISTANCE = 10;

    public GestureRecognizer() {
        this.state = State.IDLE;
        this.downX = 0;
        this.downY = 0;
        this.lastTapTime = 0;
        this.tapCount = 0;
    }

    /**
     * Called when a touch/mouse button goes down.
     *
     * @param x screen x
     * @param y screen y
     */
    public void processDown(int x, int y) {
        this.downX = x;
        this.downY = y;

        long now = System.currentTimeMillis();
        if (now - lastTapTime < DOUBLE_TAP_THRESHOLD) {
            tapCount++;
        } else {
            tapCount = 1;
        }
        lastTapTime = now;

        state = State.TAPPING;
    }

    /**
     * Called when the pointer moves.
     *
     * @param x screen x
     * @param y screen y
     */
    public void processMove(int x, int y) {
        if (state == State.TAPPING) {
            int dx = Math.abs(x - downX);
            int dy = Math.abs(y - downY);
            if (dx > TAP_MAX_DISTANCE || dy > TAP_MAX_DISTANCE) {
                state = State.DRAGGING;
            }
        }
    }

    /**
     * Called when the touch/mouse is released.
     *
     * @param x screen x
     * @param y screen y
     * @return true if a tap was detected
     */
    public boolean processUp(int x, int y) {
        boolean result;
        if (state == State.TAPPING) {
            int dx = Math.abs(x - downX);
            int dy = Math.abs(y - downY);
            if (dx <= TAP_MAX_DISTANCE && dy <= TAP_MAX_DISTANCE) {
                result = true; // tap detected
            } else {
                result = false; // too far to be a tap
            }
        } else {
            result = false;
        }
        state = State.IDLE;
        return result;
    }

    /**
     * Cancel the current gesture (e.g. on ESC).
     */
    public void cancel() {
        state = State.IDLE;
    }

    /**
     * Check if a double-tap was detected on the last up event.
     * (Consumed after call.)
     */
    public boolean isDoubleTap() {
        boolean result = tapCount >= 2;
        tapCount = 0;
        return result;
    }

    /**
     * Get the down x coordinate (for drag start).
     */
    public int getDownX() {
        return downX;
    }

    /**
     * Get the down y coordinate (for drag start).
     */
    public int getDownY() {
        return downY;
    }

    /**
     * Get the current gesture state.
     */
    public State getState() {
        return state;
    }
}
