package com.geometry.interaction.constraint;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;

/** Rules applied by interaction tools; geometry objects remain mode agnostic. */
public interface GeometryConstraint {
    Transform constrainTransform(Transform transform);
    Vec3 constrainTranslation(Vec3 delta);
    Vec3 constrainRotation(Vec3 delta);
    boolean isMeshCompatible(GeometryObject geometry);
}
