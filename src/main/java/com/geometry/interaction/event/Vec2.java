package com.geometry.interaction.event;

/**
 * Phase 05 - Lightweight 2D vector for screen-space interaction coordinates.
 *
 * Shared between PointerEvent and gesture classes. Structurally identical
 * to core.math.Vec2 but kept in the interaction package to avoid a
 * circular dependency between interaction and core.
 */
public class Vec2 {

    public final float x;
    public final float y;

    public static final Vec2 ZERO = new Vec2(0f, 0f);
    public static final Vec2 ONE = new Vec2(1f, 1f);

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 add(Vec2 v) {
        return new Vec2(this.x + v.x, this.y + v.y);
    }

    public Vec2 subtract(Vec2 v) {
        return new Vec2(this.x - v.x, this.y - v.y);
    }

    public Vec2 multiply(float s) {
        return new Vec2(this.x * s, this.y * s);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public Vec2 normalize() {
        float len = length();
        if (len == 0f) {
            return ZERO;
        }
        return new Vec2(x / len, y / len);
    }

    public float dot(Vec2 v) {
        return x * v.x + y * v.y;
    }

    /** 2D cross product (scalar). Positive when v is CCW from this. */
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
