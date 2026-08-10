package com.geometry.animation;

/**
 * Phase 09 - Animation lifecycle state.
 *
 * Represents the current state of an Animation instance.
 * State transitions:
 *   READY -> RUNNING -> PAUSED -> FINISHED
 *   Any state -> READY (via stop)
 */
public enum AnimationState {

    /** Animation has been created but not yet started. */
    READY,

    /** Animation is currently playing. */
    RUNNING,

    /** Animation is paused (preserves current progress). */
    PAUSED,

    /** Animation has reached completion. */
    FINISHED,

    /** Animation has been stopped and reset to initial state. */
    STOPPED
}
