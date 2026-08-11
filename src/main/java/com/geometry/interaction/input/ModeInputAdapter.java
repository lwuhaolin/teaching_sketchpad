package com.geometry.interaction.input;

import com.geometry.core.math.Vec3;
import com.geometry.interaction.constraint.GeometryConstraint;

/** Converts device-independent deltas into constrained world-space deltas. */
public final class ModeInputAdapter {
    private final GeometryConstraint constraint;
    public ModeInputAdapter(GeometryConstraint constraint) {
        if (constraint == null) throw new IllegalArgumentException("Constraint cannot be null");
        this.constraint = constraint;
    }
    public Vec3 movement(float x, float y, float z) {
        return constraint.constrainTranslation(new Vec3(x, y, z));
    }
    public Vec3 rotation(float x, float y, float z) {
        return constraint.constrainRotation(new Vec3(x, y, z));
    }
}
