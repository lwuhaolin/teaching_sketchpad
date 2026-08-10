package com.geometry.animation.transform;

import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/**
 * Phase 09 - Scale animation.
 *
 * Animates a GeometryObject scaling from start scale to end scale
 * over the specified duration. Position and rotation remain constant.
 *
 * Example: Scale a cube from (1,1,1) to (2,2,2) in 1 second.
 */
public class ScaleAnimation extends TransformAnimation {

    /**
     * Create a ScaleAnimation.
     *
     * @param target         the geometry object to scale
     * @param position       position (preserved throughout)
     * @param startScale     starting scale factors
     * @param endScale       ending scale factors
     * @param duration       animation duration in seconds
     * @param interpolator   easing function (null for linear)
     */
    public ScaleAnimation(GeometryObject target, Vec3 position,
                          Vec3 startScale, Vec3 endScale,
                          float duration, Interpolator interpolator) {
        super(target,
                new Transform(position, new Vec3(0f, 0f, 0f), startScale),
                new Transform(position, new Vec3(0f, 0f, 0f), endScale),
                duration, interpolator);
    }

    /**
     * Create a ScaleAnimation with linear interpolation.
     */
    public ScaleAnimation(GeometryObject target, Vec3 position,
                          Vec3 startScale, Vec3 endScale, float duration) {
        this(target, position, startScale, endScale, duration, null);
    }

    /**
     * Convenience: uniform scale from 1.0 to given scale factor.
     *
     * @param target     the geometry object
     * @param position   starting position
     * @param targetScale target uniform scale factor
     * @param duration   animation duration in seconds
     */
    public ScaleAnimation(GeometryObject target, Vec3 position, float targetScale, float duration) {
        this(target, position, Vec3.ONE, new Vec3(targetScale, targetScale, targetScale), duration, null);
    }
}
