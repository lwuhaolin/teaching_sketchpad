package com.geometry.animation.transform;

import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/**
 * Phase 09 - Move (translate) animation.
 *
 * Animates a GeometryObject from start position to end position
 * over the specified duration. Only the position component changes;
 * rotation and scale remain constant.
 *
 * Example: Move a cube from origin to (5, 0, 0) in 2 seconds.
 */
public class MoveAnimation extends TransformAnimation {

    /**
     * Create a MoveAnimation.
     *
     * @param target         the geometry object to move
     * @param startPosition  starting position
     * @param endPosition    ending position
     * @param duration       animation duration in seconds
     * @param interpolator   easing function (null for linear)
     */
    public MoveAnimation(GeometryObject target, Vec3 startPosition, Vec3 endPosition,
                         float duration, Interpolator interpolator) {
        super(target,
                new Transform(startPosition, new Vec3(0f, 0f, 0f), Vec3.ONE),
                new Transform(endPosition, new Vec3(0f, 0f, 0f), Vec3.ONE),
                duration, interpolator);
    }

    /**
     * Create a MoveAnimation with linear interpolation.
     *
     * @param target      the geometry object to move
     * @param startPosition  starting position
     * @param endPosition    ending position
     * @param duration       animation duration in seconds
     */
    public MoveAnimation(GeometryObject target, Vec3 startPosition, Vec3 endPosition, float duration) {
        this(target, startPosition, endPosition, duration, null);
    }

    @Override
    public void stop() {
        // Restore original transform from geometry
        Transform original = getTarget().getTransform();
        // Re-store start as the original state
        super.stop();
    }
}
