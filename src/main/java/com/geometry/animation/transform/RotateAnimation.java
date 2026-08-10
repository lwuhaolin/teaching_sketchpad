package com.geometry.animation.transform;

import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/**
 * Phase 09 - Rotate animation.
 *
 * Animates a GeometryObject rotating from start rotation to end rotation
 * (Euler angles in degrees) over the specified duration.
 *
 * Rotation is interpolated component-wise around X, Y, Z axes.
 *
 * Example: Rotate a cube 90 degrees around Y axis in 1 second.
 */
public class RotateAnimation extends TransformAnimation {

    /**
     * Create a RotateAnimation.
     *
     * @param target         the geometry object to rotate
     * @param startPosition  starting position (preserved throughout)
     * @param startRotation  starting Euler rotation in degrees
     * @param endRotation    ending Euler rotation in degrees
     * @param duration       animation duration in seconds
     * @param interpolator   easing function (null for linear)
     */
    public RotateAnimation(GeometryObject target, Vec3 startPosition,
                           Vec3 startRotation, Vec3 endRotation,
                           float duration, Interpolator interpolator) {
        super(target,
                new Transform(startPosition, startRotation, Vec3.ONE),
                new Transform(startPosition, endRotation, Vec3.ONE),
                duration, interpolator);
    }

    /**
     * Create a RotateAnimation with linear interpolation.
     *
     * @param target        the geometry object to rotate
     * @param startPosition starting position
     * @param startRotation starting Euler rotation in degrees
     * @param endRotation   ending Euler rotation in degrees
     * @param duration      animation duration in seconds
     */
    public RotateAnimation(GeometryObject target, Vec3 startPosition,
                           Vec3 startRotation, Vec3 endRotation, float duration) {
        this(target, startPosition, startRotation, endRotation, duration, null);
    }

    /**
     * Convenience: rotate around Y axis from 0 to specified angle.
     *
     * @param target     the geometry object
     * @param position   starting position
     * @param yawDeg     target Y-axis rotation in degrees
     * @param duration   animation duration in seconds
     */
    public RotateAnimation(GeometryObject target, Vec3 position, float yawDeg, float duration) {
        this(target, position,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, yawDeg, 0f),
                duration, null);
    }
}
