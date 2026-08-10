package com.geometry.animation.geometry;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationState;
import com.geometry.animation.face.FaceAnimator;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.mesh.Mesh;

/**
 * Phase 09 - Base class for geometry-structure animations.
 *
 * These animations modify the mesh structure itself (face positions,
 * topology) rather than just applying a Transform.
 *
 * Supports face-level animation via {@link FaceAnimator} for operations
 * like unfold and explode where individual faces move independently.
 *
 * Does NOT modify the original mesh — produces a new Mesh at each
 * progress value.
 */
public abstract class GeometryAnimation implements Animation {

    protected final Mesh sourceMesh;
    protected FaceAnimator faceAnimator;
    protected final float duration;
    protected final Interpolator interpolator;

    protected AnimationState state;
    protected float elapsed;
    protected float progress;

    /**
     * Create a GeometryAnimation.
     *
     * @param sourceMesh  the original mesh to animate from
     * @param duration    animation duration in seconds
     * @param interpolator easing function
     */
    protected GeometryAnimation(Mesh sourceMesh, float duration, Interpolator interpolator) {
        if (sourceMesh == null) {
            throw new IllegalArgumentException("Source mesh cannot be null");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        this.sourceMesh = sourceMesh;
        this.faceAnimator = new FaceAnimator(sourceMesh);
        this.duration = duration;
        this.interpolator = interpolator != null ? interpolator
                : com.geometry.animation.interpolation.LinearInterpolator.getInstance();
        this.state = AnimationState.READY;
        this.elapsed = 0f;
        this.progress = 0f;
    }

    @Override
    public void start() {
        if (state == AnimationState.FINISHED || state == AnimationState.STOPPED) {
            this.elapsed = 0f;
            this.progress = 0f;
            resetAnimation();
        }
        if (state == AnimationState.PAUSED || state == AnimationState.READY
                || state == AnimationState.STOPPED) {
            if (state == AnimationState.READY || state == AnimationState.STOPPED) {
                this.elapsed = 0f;
                this.progress = 0f;
                resetAnimation();
            }
            this.state = AnimationState.RUNNING;
        }
    }

    @Override
    public void update(float deltaTime) {
        if (state != AnimationState.RUNNING) {
            return;
        }
        this.elapsed += deltaTime;
        this.progress = Math.min(1f, elapsed / duration);
        float easedProgress = interpolator.interpolate(progress);
        onAnimate(easedProgress);
        if (progress >= 1f) {
            this.state = AnimationState.FINISHED;
        }
    }

    @Override
    public void pause() {
        if (state == AnimationState.RUNNING) {
            this.state = AnimationState.PAUSED;
        }
    }

    @Override
    public void stop() {
        this.state = AnimationState.STOPPED;
        this.elapsed = 0f;
        this.progress = 0f;
        resetAnimation();
    }

    @Override
    public boolean isFinished() {
        return state == AnimationState.FINISHED;
    }

    @Override
    public AnimationState getState() {
        return state;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(float progress) {
        float clamped = Math.max(0f, Math.min(1f, progress));
        this.progress = clamped;
        this.elapsed = clamped * duration;
        float easedProgress = interpolator.interpolate(clamped);
        onAnimate(easedProgress);
    }

    @Override
    public float getDuration() {
        return duration;
    }

    /**
     * Get the source mesh (original, unmodified).
     */
    public Mesh getSourceMesh() {
        return sourceMesh;
    }

    /**
     * Get the face animator for configuring face-level animation.
     */
    public FaceAnimator getFaceAnimator() {
        return faceAnimator;
    }

    /**
     * Reset animation state (called on start/stop/restart).
     * Subclasses should override to reset their specific state.
     */
    protected void resetAnimation() {
        // Default: nothing to reset
    }

    /**
     * Apply animation at the given eased progress [0, 1].
     * Subclasses must implement this.
     *
     * @param easedProgress normalized progress after easing
     */
    protected abstract void onAnimate(float easedProgress);
}
