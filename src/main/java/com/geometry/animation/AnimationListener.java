package com.geometry.animation;

/**
 * Phase 09 - Listener for animation lifecycle events.
 *
 * Implement this interface to receive callbacks when animations
 * start, update, complete, or stop.
 *
 * Used by:
 *   - TeachingManager (to show annotations after animation completes)
 *   - UI layer (to update progress bar)
 *   - InteractiveAnimation (to handle user input during animation)
 */
public interface AnimationListener {

    /**
     * Called when an animation starts playing.
     *
     * @param event the animation event
     */
    default void onAnimationStart(AnimationEvent event) {
    }

    /**
     * Called when an animation updates each frame.
     *
     * @param event the animation event
     */
    default void onAnimationUpdate(AnimationEvent event) {
    }

    /**
     * Called when an animation completes naturally.
     *
     * @param event the animation event
     */
    default void onAnimationComplete(AnimationEvent event) {
    }

    /**
     * Called when an animation is stopped.
     *
     * @param event the animation event
     */
    default void onAnimationStop(AnimationEvent event) {
    }
}
