package com.geometry.animation.interpolation;

/**
 * Phase 09 - Cubic ease-in-out interpolation.
 *
 * Provides smooth acceleration and deceleration:
 *   - Starts slowly, speeds up in the middle, slows down at the end
 *   - Formula: f(t) = 0.5 * sin(π * t - π/2) + 0.5
 *
 * Commonly used for teaching animations to give a natural feel.
 */
public class EaseInterpolator implements Interpolator {

    private static final EaseInterpolator INSTANCE = new EaseInterpolator();

    private EaseInterpolator() {
        // Singleton
    }

    /**
     * Get the singleton instance.
     */
    public static EaseInterpolator getInstance() {
        return INSTANCE;
    }

    @Override
    public float interpolate(float t) {
        // Clamp input to [0, 1] to avoid numerical issues
        t = Math.max(0f, Math.min(1f, t));
        // Smooth sine-based easing: sin(π*t - π/2) gives -1 to 1 over [0,1]
        // Then normalize to [0, 1]
        return (float) (0.5f * Math.sin(Math.PI * t - Math.PI / 2.0) + 0.5f);
    }
}
