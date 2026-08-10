package com.geometry.core.math;

/**
 * Phase 01 - Common mathematical utility methods.
 *
 * Provides constants and helpers used across the geometry core.
 */
public final class MathUtil {

    /** Default epsilon for floating point comparisons. */
    public static final float EPSILON = 1e-6f;

    /** π (pi) */
    public static final float PI = (float) Math.PI;

    /** 2π */
    public static final float TWO_PI = 2f * PI;

    /** π / 2 */
    public static final float PI_OVER_2 = PI / 2f;

    /** Degree-to-radian conversion factor: π / 180 */
    public static final float DEG_TO_RAD = PI / 180f;

    /** Radian-to-degree conversion factor: 180 / π */
    public static final float RAD_TO_DEG = 180f / PI;

    private MathUtil() {
        // Utility class — prevent instantiation
    }

    /** Convert degrees to radians. */
    public static float degreeToRadian(float degrees) {
        return degrees * DEG_TO_RAD;
    }

    /** Convert radians to degrees. */
    public static float radianToDegree(float radians) {
        return radians * RAD_TO_DEG;
    }

    /** Clamp value to [min, max]. */
    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /** Clamp int value to [min, max]. */
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Smoothstep: interpolate between edge0 and edge1 using x.
     * x should be in [0, 1] for the standard S-curve.
     */
    public static float smoothstep(float edge0, float edge1, float x) {
        float t = clamp((x - edge0) / (edge1 - edge0), 0f, 1f);
        return t * t * (3f - 2f * t);
    }

    /**
     * Linear interpolation between a and b by amount t.
     * t=0 → a, t=1 → b.
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    /**
     * Check if two floats are approximately equal using epsilon.
     */
    public static boolean approxEqual(float a, float b, float epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    /** Check if two floats are approximately equal using a default epsilon. */
    public static boolean approxEqual(float a, float b) {
        return approxEqual(a, b, 1e-6f);
    }
}
