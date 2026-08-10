package com.geometry.core.math;

/**
 * Phase 01 - 4x4 column-major matrix.
 *
 * Stored as a 1D float array in column-major order (compatible with OpenGL).
 * Index mapping: column * 4 + row  (e.g. m[0] is m00, m[4] is m10).
 *
 * Immutable: all operations return new Matrix4 instances.
 */
public class Matrix4 {

    /** Column-major storage: indices 0-3 = col 0, 4-7 = col 1, etc. */
    public final float[] m;

    public static final Matrix4 IDENTITY = new Matrix4(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    );

    public Matrix4(float... values) {
        if (values == null || values.length != 16) {
            throw new IllegalArgumentException("Matrix4 requires exactly 16 float values");
        }
        this.m = values.clone();
    }

    /** Return a new Matrix4 initialized to zeros. */
    public static Matrix4 zero() {
        return new Matrix4(
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f,
                0f, 0f, 0f, 0f
        );
    }

    /** Get element at row r, column c (0-indexed). */
    public float get(int row, int col) {
        return m[col * 4 + row];
    }

    /** Set element at row r, column c (0-indexed). Returns this for chaining. */
    public Matrix4 set(int row, int col, float value) {
        m[col * 4 + row] = value;
        return this;
    }

    /** Return a new matrix that is the product of this and right. */
    public Matrix4 multiply(Matrix4 right) {
        float[] a = this.m;
        float[] b = right.m;
        float[] result = new float[16];
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                float sum = 0f;
                for (int k = 0; k < 4; k++) {
                    sum += a[k * 4 + row] * b[col * 4 + k];
                }
                result[col * 4 + row] = sum;
            }
        }
        return new Matrix4(result);
    }

    /**
     * Return a new matrix multiplied by vector v (treated as a column vector with w=1).
     * The result w-component is returned separately.
     */
    public Vec3 transformPoint(Vec3 v) {
        float[] a = this.m;
        float x = v.x;
        float y = v.y;
        float z = v.z;
        float w = a[3] * x + a[7] * y + a[11] * z + a[15];
        if (w != 0f && w != 1f) {
            x = x / w;
            y = y / w;
            z = z / w;
        }
        return new Vec3(
                a[0] * x + a[4] * y + a[8] * z + a[12],
                a[1] * x + a[5] * y + a[9] * z + a[13],
                a[2] * x + a[6] * y + a[10] * z + a[14]
        );
    }

    /**
     * Return a new matrix multiplied by direction vector v (w=0, no translation).
     */
    public Vec3 transformDir(Vec3 v) {
        float[] a = this.m;
        return new Vec3(
                a[0] * v.x + a[4] * v.y + a[8] * v.z,
                a[1] * v.x + a[5] * v.y + a[9] * v.z,
                a[2] * v.x + a[6] * v.y + a[10] * v.z
        );
    }

    /**
     * Return a new matrix that is the product of a scalar and this matrix.
     */
    public Matrix4 scale(float s) {
        float[] result = new float[16];
        for (int i = 0; i < 16; i++) {
            result[i] = this.m[i] * s;
        }
        return new Matrix4(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Matrix4 matrix4 = (Matrix4) o;
        for (int i = 0; i < 16; i++) {
            if (Float.compare(matrix4.m[i], m[i]) != 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (float v : m) {
            result = 31 * result + Float.floatToIntBits(v);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 4; row++) {
            if (row > 0) {
                sb.append("\n");
            }
            for (int col = 0; col < 4; col++) {
                if (col > 0) {
                    sb.append("  ");
                }
                sb.append(String.format("%8.4f", m[col * 4 + row]));
            }
        }
        return sb.toString();
    }
}
