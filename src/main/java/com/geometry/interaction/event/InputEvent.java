package com.geometry.interaction.event;

/**
 * Phase 05 - Base class for all input events in the interaction system.
 *
 * Subclasses:
 *   PointerEvent  — raw pointer (mouse/touch/pen) data
 *   GestureEvent  — recognised gesture (drag, pinch, rotate)
 *
 * The interaction pipeline:
 *   InputDevice.update() → List&lt;InputEvent&gt; → InteractionManager
 *     → GestureRecognizer → List&lt;GestureEvent&gt;
 *     → Action execution on SceneObject
 */
public abstract class InputEvent {

    /** Timestamp in milliseconds since epoch. */
    private final long timestamp;

    public InputEvent() {
        this.timestamp = System.currentTimeMillis();
    }

    public InputEvent(long timestamp) {
        this.timestamp = timestamp;
    }

    /** Get the timestamp when this event was generated. */
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{ts=" + timestamp + "}";
    }
}
