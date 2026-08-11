package com.geometry.interaction.constraint;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/** Unrestricted 3D rule set. */
public final class Free3DConstraint implements GeometryConstraint {
    @Override public Transform constrainTransform(Transform transform) {
        if (transform == null) throw new IllegalArgumentException("Transform cannot be null");
        return transform;
    }
    @Override public Vec3 constrainTranslation(Vec3 delta) {
        if (delta == null) throw new IllegalArgumentException("Translation cannot be null");
        return delta;
    }
    @Override public Vec3 constrainRotation(Vec3 delta) {
        if (delta == null) throw new IllegalArgumentException("Rotation cannot be null");
        return delta;
    }
    @Override public boolean isMeshCompatible(GeometryObject geometry) { return geometry != null; }
}
