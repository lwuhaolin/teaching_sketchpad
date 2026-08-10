package com.geometry.animation.interpolation;

/**
 * Phase 09 - Interpolator for smooth animation transitions.
 *
 * Provides mapping from normalized time [0, 1] to eased output [0, 1].
 * Used to make animations feel natural rather than mechanical.
 *
 * Implementations:
 *   - LinearInterpolator: constant speed
 *   - EaseInterpolator: cubic ease-in/out for smooth start/end
 */
public interface Interpolator {

    /**
     * Map normalized input time to eased output value.
     *
     * @param t normalized time in [0, 1]
     * @return eased value in [0, 1]
     */
    float interpolate(float t);
}
