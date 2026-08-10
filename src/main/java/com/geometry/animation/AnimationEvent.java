package com.geometry.animation;

/**
 * Phase 09 - Event fired by animations during their lifecycle.
 *
 * Carries information about animation events such as start,
 * update progress, and completion.
 */
public class AnimationEvent {

    /** Type of animation event. */
    public enum EventType {
        /** Animation started playing. */
        START,

        /** Animation updated (progress changed). */
        UPDATE,

        /** Animation completed naturally. */
        COMPLETE,

        /** Animation was stopped. */
        STOP
    }

    private final EventType type;
    private final Animation source;
    private final float progress;

    /**
     * Create an AnimationEvent.
     *
     * @param type     the event type
     * @param source   the animation that generated this event
     * @param progress current progress [0.0, 1.0]
     */
    public AnimationEvent(EventType type, Animation source, float progress) {
        this.type = type;
        this.source = source;
        this.progress = progress;
    }

    /**
     * Get the event type.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Get the animation that generated this event.
     */
    public Animation getSource() {
        return source;
    }

    /**
     * Get the progress at the time of this event.
     */
    public float getProgress() {
        return progress;
    }
}
