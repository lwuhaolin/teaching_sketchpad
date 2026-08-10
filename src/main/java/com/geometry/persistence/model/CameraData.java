package com.geometry.persistence.model;

/**
 * Phase 10 - Data model for camera state in a saved project.
 *
 * Stores camera position, target look-at point, up vector, and FOV.
 * These parameters allow the camera to be restored to the exact state
 * when the project was saved.
 *
 * Not thread-safe.
 */
public class CameraData {

    private float[] position;
    private float[] target;
    private float[] up;
    private float fov;

    /**
     * Create a CameraData with default values.
     */
    public CameraData() {
        this.position = new float[]{0f, 0f, 10f};
        this.target = new float[]{0f, 0f, 0f};
        this.up = new float[]{0f, 1f, 0f};
        this.fov = 45f;
    }

    /**
     * Create a CameraData with explicit values.
     *
     * @param position camera position [x, y, z]
     * @param target   look-at target [x, y, z]
     * @param up       up vector [x, y, z]
     * @param fov      field of view in degrees
     */
    public CameraData(float[] position, float[] target, float[] up, float fov) {
        this.position = position != null ? position.clone() : new float[]{0f, 0f, 10f};
        this.target = target != null ? target.clone() : new float[]{0f, 0f, 0f};
        this.up = up != null ? up.clone() : new float[]{0f, 1f, 0f};
        this.fov = fov > 0 ? fov : 45f;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public float[] getPosition() {
        return position.clone();
    }

    public void setPosition(float[] position) {
        if (position == null || position.length != 3) {
            throw new IllegalArgumentException("Position must be a 3-float array");
        }
        this.position = position.clone();
    }

    public float[] getTarget() {
        return target.clone();
    }

    public void setTarget(float[] target) {
        if (target == null || target.length != 3) {
            throw new IllegalArgumentException("Target must be a 3-float array");
        }
        this.target = target.clone();
    }

    public float[] getUp() {
        return up.clone();
    }

    public void setUp(float[] up) {
        if (up == null || up.length != 3) {
            throw new IllegalArgumentException("Up must be a 3-float array");
        }
        this.up = up.clone();
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov > 0 ? fov : 45f;
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CameraData that = (CameraData) o;
        return Float.compare(that.fov, fov) == 0
                && java.util.Arrays.equals(position, that.position)
                && java.util.Arrays.equals(target, that.target)
                && java.util.Arrays.equals(up, that.up);
    }

    @Override
    public int hashCode() {
        int result = java.util.Arrays.hashCode(position);
        result = 31 * result + java.util.Arrays.hashCode(target);
        result = 31 * result + java.util.Arrays.hashCode(up);
        result = 31 * result + Float.floatToIntBits(fov);
        return result;
    }

    @Override
    public String toString() {
        return "CameraData{pos=" + java.util.Arrays.toString(position)
                + ", target=" + java.util.Arrays.toString(target)
                + ", up=" + java.util.Arrays.toString(up)
                + ", fov=" + fov + "}";
    }
}
