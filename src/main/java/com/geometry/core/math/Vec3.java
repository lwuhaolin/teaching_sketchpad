package com.geometry.core.math;

/**
 * Phase 01 - Three-dimensional vector.
 *
 * Represents a 3D point or direction with x, y, and z components.
 * Immutable: all operations return new Vec3 instances.
 *
 * All geometry in this engine — including 2D shapes — uses Vec3,
 * with 2D shapes expressed at z = 0.
 */
public class Vec3 {

    public final float x;
    public final float y;
    public final float z;

    public static final Vec3 ZERO = new Vec3(0f, 0f, 0f);
    public static final Vec3 ONE = new Vec3(1f, 1f, 1f);
    public static final Vec3 UNIT_X = new Vec3(1f, 0f, 0f);
    public static final Vec3 UNIT_Y = new Vec3(0f, 1f, 0f);
    public static final Vec3 UNIT_Z = new Vec3(0f, 0f, 1f);

    public Vec3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Return a new Vec3 whose components are the sums of this and v. */
    public Vec3 add(Vec3 v) {
        return new Vec3(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /** Return a new Vec3 whose components are this minus v. */
    public Vec3 subtract(Vec3 v) {
        return new Vec3(this.x - v.x, this.y - v.y, this.z - v.z);
    }

    /** Return a new Vec3 scaled by the given scalar. */
    public Vec3 multiply(float s) {
        return new Vec3(this.x * s, this.y * s, this.z * s);
    }

    /** Euclidean length (magnitude) of this vector. */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    /** Return a unit vector in the same direction, or ZERO if length is 0. */
    public Vec3 normalize() {
        float len = length();
        if (len == 0f) {
            return ZERO;
        }
        return new Vec3(x / len, y / len, z / len);
    }

    /** Dot product with v. */
    public float dot(Vec3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /** Cross product with v. Result is perpendicular to both this and v. */
    public Vec3 cross(Vec3 v) {
        return new Vec3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vec3 vec3 = (Vec3) o;
        return Float.compare(vec3.x, x) == 0
                && Float.compare(vec3.y, y) == 0
                && Float.compare(vec3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
