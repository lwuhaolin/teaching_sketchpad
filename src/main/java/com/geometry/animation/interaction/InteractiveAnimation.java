package com.geometry.animation.interaction;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationManager;
import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;

/**
 * Phase 09 - Interactive animation with manual progress control.
 *
 * Allows the user (teacher/student) to scrub through an animation
 * by setting progress directly. Used for:
 *   - Whiteboard drag gestures
 *   - Timeline scrubbing
 *   - Step-by-step teaching control
 *
 * The animation state is preserved when scrubbing, and can be
 * played normally after manual interaction.
 */
public class InteractiveAnimation implements Animation {

    private final Animation delegate;
    private final AnimationManager manager;
    private boolean interactiveMode;

    /**
     * Create an InteractiveAnimation wrapper.
     *
     * @param delegate the animation to wrap
     * @param manager  the animation manager that owns this animation
     */
    public InteractiveAnimation(Animation delegate, AnimationManager manager) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate animation cannot be null");
        }
        this.delegate = delegate;
        this.manager = manager;
        this.interactiveMode = false;
    }

    /**
     * Set the animation progress directly.
     *
     * @param progress normalized progress [0.0, 1.0]
     */
    public void setProgress(float progress) {
        this.delegate.setProgress(progress);
        this.interactiveMode = true;
    }

    /**
     * Get the current progress.
     */
    public float getProgress() {
        return delegate.getProgress();
    }

    /**
     * Check if currently in interactive (scrubbing) mode.
     */
    public boolean isInteractiveMode() {
        return interactiveMode;
    }

    /**
     * Exit interactive mode and resume normal animation.
     */
    public void resume() {
        this.interactiveMode = false;
        delegate.start();
    }

    @Override
    public void start() {
        interactiveMode = false;
        delegate.start();
    }

    @Override
    public void update(float deltaTime) {
        if (!interactiveMode) {
            delegate.update(deltaTime);
        }
    }

    @Override
    public void pause() {
        delegate.pause();
    }

    @Override
    public void stop() {
        delegate.stop();
        interactiveMode = false;
    }

    @Override
    public boolean isFinished() {
        return delegate.isFinished();
    }

    @Override
    public AnimationState getState() {
        return delegate.getState();
    }

    @Override
    public float getDuration() {
        return delegate.getDuration();
    }
}
