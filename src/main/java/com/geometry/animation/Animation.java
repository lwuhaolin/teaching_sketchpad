package com.geometry.animation;

/**
 * Phase 09 - Core animation interface.
 *
 * All animations in the engine must implement this interface.
 * Animations control time-based transitions without modifying
 * the original geometry mesh data.
 *
 * Lifecycle:
 *   READY -> start() -> RUNNING -> update(dt) -> FINISHED
 *   RUNNING -> pause() -> PAUSED -> resume (back to RUNNING)
 *   Any -> stop() -> STOPPED / READY
 *
 * Sub-interfaces for specific animation types:
 *   - TransformAnimation: movement, rotation, scaling
 *   - GeometryAnimation: unfold, explode, cut, section
 *
 * Used by AnimationManager for lifecycle management.
 * Connected to Teaching system via AnimationSequence.
 */
public interface Animation {

    /**
     * Start or resume the animation from its current state.
     * Transitions: READY -> RUNNING, PAUSED -> RUNNING
     */
    void start();

    /**
     * Update the animation state by the given delta time.
     * Must be called every frame during rendering.
     *
     * @param deltaTime time in seconds since last update
     */
    void update(float deltaTime);

    /**
     * Pause the animation, preserving current progress.
     * Transitions: RUNNING -> PAUSED
     */
    void pause();

    /**
     * Stop the animation and reset to initial state.
     * Transitions: any -> STOPPED
     */
    void stop();

    /**
     * Check if the animation has completed.
     *
     * @return true if animation is in FINISHED state
     */
    boolean isFinished();

    /**
     * Get the current animation state.
     *
     * @return current AnimationState
     */
    AnimationState getState();

    /**
     * Get the current progress of the animation [0.0, 1.0].
     * 0.0 = start, 1.0 = end.
     *
     * @return progress value
     */
    float getProgress();

    /**
     * Set the animation progress directly [0.0, 1.0].
     * Used for interactive scrubbing (e.g., timeline dragging).
     *
     * @param progress progress value in [0.0, 1.0]
     */
    void setProgress(float progress);

    /**
     * Get the animation duration in seconds.
     *
     * @return duration in seconds, 0 if indefinite
     */
    float getDuration();
}
