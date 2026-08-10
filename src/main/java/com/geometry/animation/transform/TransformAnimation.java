package com.geometry.animation.transform;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationEvent;
import com.geometry.animation.AnimationListener;
import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/**
 * Phase 09 - Animation that interpolates a Transform on a GeometryObject.
 *
 * Blends between a start Transform and an end Transform over time.
 * Does NOT modify the original mesh — only updates the object's
 * Transform, which the Renderer reads.
 *
 * Interpolation is done component-wise:
 *   - Position: lerp between start and end positions
 *   - Rotation: lerp between start and end Euler angles
 *   - Scale: lerp between start and end scale factors
 *
 * Used as the base class for MoveAnimation, RotateAnimation, and ScaleAnimation.
 */
public abstract class TransformAnimation implements Animation {

    private final GeometryObject target;
    private final Transform startTransform;
    private final Transform endTransform;
    private final float duration;
    private final Interpolator interpolator;

    private AnimationState state;
    private float elapsed;
    private float progress;
    private boolean started;

    /**
     * Create a TransformAnimation.
     *
     * @param target         the geometry object to animate
     * @param startTransform starting transform (captured at animation start)
     * @param endTransform   target transform at animation end
     * @param duration       animation duration in seconds
     * @param interpolator   easing function (use LinearInterpolator for constant speed)
     */
    public TransformAnimation(GeometryObject target,
                              Transform startTransform,
                              Transform endTransform,
                              float duration,
                              Interpolator interpolator) {
        if (target == null) {
            throw new IllegalArgumentException("Target geometry cannot be null");
        }
        if (startTransform == null || endTransform == null) {
            throw new IllegalArgumentException("Transforms cannot be null");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive, got " + duration);
        }
        this.target = target;
        this.startTransform = startTransform;
        this.endTransform = endTransform;
        this.duration = duration;
        this.interpolator = interpolator != null ? interpolator : com.geometry.animation.interpolation.LinearInterpolator.getInstance();
        this.state = AnimationState.READY;
        this.elapsed = 0f;
        this.progress = 0f;
        this.started = false;
    }

    @Override
    public void start() {
        if (state == AnimationState.FINISHED) {
            // Restart from beginning
            this.elapsed = 0f;
            this.progress = 0f;
            target.setTransform(startTransform);
        }
        if (state == AnimationState.PAUSED || state == AnimationState.READY || state == AnimationState.STOPPED) {
            if (state == AnimationState.READY || state == AnimationState.STOPPED) {
                target.setTransform(startTransform);
                this.elapsed = 0f;
                this.progress = 0f;
            }
            this.state = AnimationState.RUNNING;
            this.started = true;
            fireEvent(new AnimationEvent(AnimationEvent.EventType.START, this, 0f));
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
        applyInterpolatedTransform(easedProgress);
        fireEvent(new AnimationEvent(AnimationEvent.EventType.UPDATE, this, progress));
        if (progress >= 1f) {
            this.state = AnimationState.FINISHED;
            fireEvent(new AnimationEvent(AnimationEvent.EventType.COMPLETE, this, 1f));
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
        target.setTransform(startTransform);
        this.state = AnimationState.STOPPED;
        this.elapsed = 0f;
        this.progress = 0f;
        fireEvent(new AnimationEvent(AnimationEvent.EventType.STOP, this, 0f));
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
        applyInterpolatedTransform(easedProgress);
    }

    @Override
    public float getDuration() {
        return duration;
    }

    /**
     * Apply the interpolated transform at the given eased progress [0, 1].
     * Subclasses may override for custom interpolation logic.
     *
     * @param easedProgress normalized progress after easing
     */
    protected void applyInterpolatedTransform(float easedProgress) {
        Vec3 newPos = lerpVec3(startTransform.getPosition(), endTransform.getPosition(), easedProgress);
        Vec3 newRot = lerpVec3(startTransform.getRotation(), endTransform.getRotation(), easedProgress);
        Vec3 newScale = lerpVec3(startTransform.getScale(), endTransform.getScale(), easedProgress);
        target.setTransform(new Transform(newPos, newRot, newScale));
    }

    /**
     * Get the target geometry object.
     */
    public GeometryObject getTarget() {
        return target;
    }

    /**
     * Get the start transform.
     */
    public Transform getStartTransform() {
        return startTransform;
    }

    /**
     * Get the end transform.
     */
    public Transform getEndTransform() {
        return endTransform;
    }

    // ------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------

    protected Vec3 lerpVec3(Vec3 start, Vec3 end, float t) {
        return new Vec3(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t,
                start.z + (end.z - start.z) * t
        );
    }

    private void fireEvent(AnimationEvent event) {
        // No listener list here; the AnimationManager handles events
    }
}
