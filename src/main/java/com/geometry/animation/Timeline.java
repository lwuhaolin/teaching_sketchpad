package com.geometry.animation;

/**
 * Phase 09 - Timeline for animation timing control.
 *
 * Provides absolute time tracking for animations.
 * Supports stepping and time scaling (slow motion, fast forward).
 *
 * Integration:
 *   Used by AnimationSequence to control timing of step-based animations.
 */
public class Timeline {

    private float currentTime;
    private float timeScale;
    private boolean paused;

    /**
     * Create a Timeline starting at 0 with normal speed.
     */
    public Timeline() {
        this.currentTime = 0f;
        this.timeScale = 1f;
        this.paused = false;
    }

    /**
     * Create a Timeline with a specific starting time.
     *
     * @param startTime initial time in seconds
     */
    public Timeline(float startTime) {
        this.currentTime = startTime;
        this.timeScale = 1f;
        this.paused = false;
    }

    /**
     * Update the timeline by delta time.
     *
     * @param deltaTime time in seconds since last update
     */
    public void update(float deltaTime) {
        if (paused) {
            return;
        }
        this.currentTime += deltaTime * timeScale;
    }

    // ------------------------------------------------------------------
    // Time control
    // ------------------------------------------------------------------

    /**
     * Get the current elapsed time.
     *
     * @return time in seconds
     */
    public float getCurrentTime() {
        return currentTime;
    }

    /**
     * Set the current time.
     *
     * @param time time in seconds
     */
    public void setCurrentTime(float time) {
        this.currentTime = Math.max(0f, time);
    }

    /**
     * Reset the timeline to zero.
     */
    public void reset() {
        this.currentTime = 0f;
    }

    /**
     * Set the time scale (speed multiplier).
     * Values: < 1 = slow motion, > 1 = fast forward, 1 = normal
     *
     * @param scale speed multiplier
     */
    public void setTimeScale(float scale) {
        this.timeScale = Math.max(0.01f, scale);
    }

    /**
     * Get the current time scale.
     *
     * @return time scale factor
     */
    public float getTimeScale() {
        return timeScale;
    }

    /**
     * Pause the timeline.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Resume the timeline.
     */
    public void resume() {
        this.paused = false;
    }

    /**
     * Check if the timeline is paused.
     *
     * @return true if paused
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Check if the timeline has reached the end time.
     *
     * @param endTime the end time to check against
     * @return true if currentTime >= endTime
     */
    public boolean isFinished(float endTime) {
        return currentTime >= endTime;
    }
}
