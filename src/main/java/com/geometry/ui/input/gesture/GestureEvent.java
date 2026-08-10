package com.geometry.ui.input.gesture;

/**
 * Phase 13 - Event fired by the GestureRecognizer.
 *
 * Carries gesture type and screen coordinates for consumption
 * by the canvas interaction layer or tool system.
 *
 * Not thread-safe.
 */
public class GestureEvent {

    /** The type of gesture detected. */
    public enum GestureType {
        /** Single tap (click). */
        TAP,
        /** Drag gesture (touch down then move then up). */
        DRAG,
        /** Double tap. */
        DOUBLE_TAP,
        /** Long press (precision mode entry). */
        LONG_PRESS
    }

    private final GestureType type;
    private final int x;
    private final int y;

    public GestureEvent(GestureType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public GestureType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "GestureEvent{type=" + type + ", x=" + x + ", y=" + y + "}";
    }
}
