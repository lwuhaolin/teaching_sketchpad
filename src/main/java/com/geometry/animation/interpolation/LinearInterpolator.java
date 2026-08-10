package com.geometry.animation.interpolation;

/**
 * Phase 09 - Linear interpolation (constant speed).
 *
 * Output equals input: f(t) = t
 * No easing applied.
 */
public class LinearInterpolator implements Interpolator {

    private static final LinearInterpolator INSTANCE = new LinearInterpolator();

    private LinearInterpolator() {
        // Singleton
    }

    /**
     * Get the singleton instance.
     */
    public static LinearInterpolator getInstance() {
        return INSTANCE;
    }

    @Override
    public float interpolate(float t) {
        return t;
    }
}
