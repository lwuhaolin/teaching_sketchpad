package com.geometry.animation;

import com.geometry.animation.AnimationEvent.EventType;
import com.geometry.core.transform.Transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 09 - Manages the lifecycle of all animations.
 *
 * Responsibilities:
 *   - Add/remove animations
 *   - Update all active animations each frame
 *   - Provide play/pause/stop control
 *   - Notify listeners of animation events
 *
 * Integration:
 *   Connected to Scene via animation override transforms.
 *   Called by Teaching system through AnimationSequence.
 *
 * Not thread-safe.
 */
public class AnimationManager {

    private final List<Animation> animations;
    private final List<AnimationListener> listeners;
    private boolean paused;

    /**
     * Create an empty AnimationManager.
     */
    public AnimationManager() {
        this.animations = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.paused = false;
    }

    // ------------------------------------------------------------------
    // Animation management
    // ------------------------------------------------------------------

    /**
     * Add an animation to be managed.
     *
     * @param animation the animation to add
     */
    public void addAnimation(Animation animation) {
        if (animation != null && !animations.contains(animation)) {
            animations.add(animation);
        }
    }

    /**
     * Remove an animation from management.
     *
     * @param animation the animation to remove
     */
    public void removeAnimation(Animation animation) {
        animations.remove(animation);
    }

    /**
     * Remove all animations.
     */
    public void clearAnimations() {
        animations.clear();
    }

    /**
     * Get all managed animations.
     */
    public List<Animation> getAnimations() {
        return Collections.unmodifiableList(animations);
    }

    /**
     * Get the number of managed animations.
     */
    public int getAnimationCount() {
        return animations.size();
    }

    // ------------------------------------------------------------------
    // Playback control
    // ------------------------------------------------------------------

    /**
     * Start all animations that are in READY or STOPPED state.
     */
    public void play() {
        for (Animation anim : animations) {
            AnimationState state = anim.getState();
            if (state == AnimationState.READY || state == AnimationState.STOPPED) {
                anim.start();
                fireEvent(anim, AnimationEvent.EventType.START, anim.getProgress());
            }
        }
        this.paused = false;
    }

    /**
     * Pause all running animations.
     */
    public void pause() {
        if (this.paused) {
            return;
        }
        this.paused = true;
        for (Animation anim : animations) {
            if (anim.getState() == AnimationState.RUNNING) {
                anim.pause();
            }
        }
    }

    /**
     * Resume all paused animations.
     */
    public void resume() {
        this.paused = false;
        for (Animation anim : animations) {
            if (anim.getState() == AnimationState.PAUSED) {
                anim.start();
            }
        }
    }

    /**
     * Stop all animations and reset them.
     */
    public void stop() {
        for (Animation anim : animations) {
            anim.stop();
        }
        this.paused = false;
    }

    /**
     * Update all animations by the given delta time.
     * Called once per frame.
     *
     * @param deltaTime time in seconds since last frame
     */
    public void update(float deltaTime) {
        if (paused) {
            return;
        }
        for (int i = animations.size() - 1; i >= 0; i--) {
            Animation anim = animations.get(i);
            anim.update(deltaTime);
            fireEvent(anim, AnimationEvent.EventType.UPDATE, anim.getProgress());
            if (anim.isFinished()) {
                onAnimFinished(anim);
            }
        }
    }

    // ------------------------------------------------------------------
    // Listener management
    // ------------------------------------------------------------------

    /**
     * Add a listener for animation events.
     *
     * @param listener the listener to add
     */
    public void addListener(AnimationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(AnimationListener listener) {
        listeners.remove(listener);
    }

    // ------------------------------------------------------------------
    // Internal event handling
    // ------------------------------------------------------------------

    private void onAnimFinished(Animation animation) {
        AnimationEvent event = new AnimationEvent(AnimationEvent.EventType.COMPLETE, animation, animation.getProgress());
        for (AnimationListener listener : listeners) {
            listener.onAnimationComplete(event);
        }
    }

    private void fireEvent(Animation animation, AnimationEvent.EventType type, float progress) {
        AnimationEvent event = new AnimationEvent(type, animation, progress);
        for (AnimationListener listener : listeners) {
            switch (type) {
                case START: listener.onAnimationStart(event); break;
                case UPDATE: listener.onAnimationUpdate(event); break;
                case COMPLETE: listener.onAnimationComplete(event); break;
                case STOP: listener.onAnimationStop(event); break;
            }
        }
    }

    /**
     * Check if any animation is currently running.
     *
     * @return true if at least one animation is in RUNNING state
     */
    public boolean isAnyRunning() {
        for (Animation anim : animations) {
            if (anim.getState() == AnimationState.RUNNING) {
                return true;
            }
        }
        return false;
    }
}
