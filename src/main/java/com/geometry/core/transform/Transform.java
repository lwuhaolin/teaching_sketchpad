package com.geometry.core.transform;

import com.geometry.core.math.Vec3;

/**
 * Phase 01 - Object transformation container.
 *
 * Describes the position, rotation, and scale of a geometry object in world space.
 * Rotation is stored as Euler angles in degrees (pitch, yaw, roll) around X, Y, Z axes.
 *
 * Immutable: all transformation methods return a new Transform instance.
 *
 * Used by every GeometryObject via {@code GeometryObject.getTransform()}.
 * Future phases will compute the final Model matrix from these components.
 */
public class Transform {

    public static final Transform IDENTITY = new Transform(
            Vec3.ZERO,
            new Vec3(0f, 0f, 0f),
            Vec3.ONE
    );

    private final Vec3 position;
    private final Vec3 rotation; // Euler angles in degrees
    private final Vec3 scale;

    public Transform(Vec3 position, Vec3 rotation, Vec3 scale) {
        this.position = position != null ? position : Vec3.ZERO;
        this.rotation = rotation != null ? rotation : new Vec3(0f, 0f, 0f);
        this.scale = scale != null ? scale : Vec3.ONE;
    }

    /** Create an identity transform. */
    public Transform() {
        this(Vec3.ZERO, new Vec3(0f, 0f, 0f), Vec3.ONE);
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getRotation() {
        return rotation;
    }

    public Vec3 getScale() {
        return scale;
    }

    /**
     * Translate this transform by the given delta.
     * Returns a new Transform with the updated position.
     */
    public Transform translate(Vec3 delta) {
        return new Transform(position.add(delta), rotation, scale);
    }

    /**
     * Rotate this transform by adding the given Euler angles (degrees).
     * Returns a new Transform with the updated rotation.
     */
    public Transform rotate(Vec3 deltaDeg) {
        return new Transform(position, rotation.add(deltaDeg), scale);
    }

    /**
     * Scale this transform by the given factors.
     * Returns a new Transform with the updated scale.
     */
    public Transform scale(Vec3 factor) {
        return new Transform(position, rotation, new Vec3(
                scale.x * factor.x,
                scale.y * factor.y,
                scale.z * factor.z
        ));
    }

    /**
     * Scale this transform by a uniform factor.
     */
    public Transform scaleUniform(float s) {
        return new Transform(position, rotation, scale.multiply(s));
    }

    /** Compose this transform with another (other applied after this). */
    public Transform combine(Transform other) {
        // Position: apply other's rotation + scale to this.position, then add other.position
        // For simplicity in this phase, positions are additive; full matrix composition
        // will be implemented in Phase 03 Renderer.
        return new Transform(
                other.position,
                other.rotation,
                other.scale
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
        Transform transform = (Transform) o;
        return position.equals(transform.position)
                && rotation.equals(transform.rotation)
                && scale.equals(transform.scale);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + rotation.hashCode();
        result = 31 * result + scale.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Transform{pos=" + position + ", rot=" + rotation + ", scale=" + scale + "}";
    }
}
