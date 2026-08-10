package com.geometry.interaction.event;

/**
 * Phase 05 - Unified pointer event across all input devices.
 *
 * Normalises mouse, touch, and pen input into a single event type so that
 * the gesture system and tools never need to distinguish input sources.
 *
 * Event flow:
 *   Input Device → PointerEvent → GestureRecognizer → GestureEvent
 *                                                       → Action
 */
public class PointerEvent extends InputEvent {

    /** Which pointer caused this event. */
    public enum PointerType {
        MOUSE,
        TOUCH,
        PEN
    }

    /** The nature of this pointer event. */
    public enum EventType {
        DOWN,
        UP,
        MOVE,
        CLICK
    }

    /** Unique identifier for the pointer (mouse button / touch point ID). */
    private final int pointerId;

    /** Type of pointer that generated this event. */
    private final PointerType pointerType;

    /** Screen-space position in pixels (origin top-left). */
    private final Vec2 position;

    /** Delta since the previous move event, in pixels. */
    private final Vec2 delta;

    /** The type of this event. */
    private final EventType eventType;

    /** Pressure value in [0, 1], only meaningful for pen input. */
    private final float pressure;

    public PointerEvent(int pointerId, PointerType pointerType,
                        Vec2 position, Vec2 delta, EventType eventType) {
        this(pointerId, pointerType, position, delta, eventType, 0f);
    }

    public PointerEvent(int pointerId, PointerType pointerType,
                        Vec2 position, Vec2 delta, EventType eventType, float pressure) {
        super();
        this.pointerId = pointerId;
        this.pointerType = pointerType;
        this.position = position != null ? position : Vec2.ZERO;
        this.delta = delta != null ? delta : Vec2.ZERO;
        this.eventType = eventType;
        this.pressure = Math.max(0f, Math.min(1f, pressure));
    }

    public int getPointerId() {
        return pointerId;
    }

    public PointerType getPointerType() {
        return pointerType;
    }

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getDelta() {
        return delta;
    }

    public EventType getEventType() {
        return eventType;
    }

    public float getPressure() {
        return pressure;
    }

    public boolean isDown() {
        return eventType == EventType.DOWN;
    }

    public boolean isUp() {
        return eventType == EventType.UP;
    }

    public boolean isMove() {
        return eventType == EventType.MOVE;
    }

    public boolean isClick() {
        return eventType == EventType.CLICK;
    }

    @Override
    public String toString() {
        return "PointerEvent{type=" + pointerType + ", event=" + eventType
                + ", pos=" + position + ", delta=" + delta
                + ", pressure=" + pressure + "}";
    }
}
