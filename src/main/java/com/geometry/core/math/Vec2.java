package com.geometry.core.math;

/**
 * Phase 01 - Two-dimensional vector.
 *
 * Represents a 2D point or direction with x and y components.
 * Immutable: all operations return new Vec2 instances.
 */
public class Vec2 {

    public final float x;
    public final float y;

    public static final Vec2 ZERO = new Vec2(0f, 0f);
    public static final Vec2 ONE = new Vec2(1f, 1f);
    public static final Vec2 UNIT_X = new Vec2(1f, 0f);
    public static final Vec2 UNIT_Y = new Vec2(0f, 1f);

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Return a new Vec2 whose components are the sums of this and v. */
    public Vec2 add(Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    /** Return a new Vec2 whose components are this minus v. */
    public Vec2 subtract(Vec2 v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }

    /** Return a new Vec2 scaled by the given scalar. */
    public Vec2 multiply(float s) {
        return new Vec2(this.x * s, this.y * s);
    }

    /** Euclidean length (magnitude) of this vector. */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Return a unit vector in the same direction, or ZERO if length is 0. */
    public Vec2 normalize() {
        float len = length();
        if (len == 0f) {
            return ZERO;
        }
        return new Vec2(x / len, y / len);
    }

    /** Dot product with v. */
    public float dot(Vec2 v) {
        return x * v.x + y * v.y;
    }

    /**
     * 2D cross product (scalar result).
     * Positive when v is counter-clockwise from this vector.
     */
    public float cross(Vec2 v) {
        return x * v.y - y * v.x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vec2 vec2 = (Vec2) o;
        return Float.compare(vec2.x, x) == 0 && Float.compare(vec2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
