package com.geometry.interaction.constraint;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.core.mesh.Vertex;

/** XY-plane rule set: z position and X/Y rotations are always removed. */
public final class Planar2DConstraint implements GeometryConstraint {
    private static final float EPSILON = 0.0001f;

    @Override
    public Transform constrainTransform(Transform transform) {
        if (transform == null) {
            throw new IllegalArgumentException("Transform cannot be null");
        }
        Vec3 p = transform.getPosition();
        Vec3 r = transform.getRotation();
        Vec3 s = transform.getScale();
        return new Transform(new Vec3(p.x, p.y, 0f), new Vec3(0f, 0f, r.z),
                new Vec3(s.x, s.y, s.z));
    }

    @Override
    public Vec3 constrainTranslation(Vec3 delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Translation cannot be null");
        }
        return new Vec3(delta.x, delta.y, 0f);
    }

    @Override
    public Vec3 constrainRotation(Vec3 delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Rotation cannot be null");
        }
        return new Vec3(0f, 0f, delta.z);
    }

    @Override
    public boolean isMeshCompatible(GeometryObject geometry) {
        if (geometry == null || geometry.getMesh() == null) {
            return false;
        }
        for (Vertex vertex : geometry.getMesh().getVertices()) {
            if (Math.abs(vertex.getPosition().z) > EPSILON) {
                return false;
            }
        }
        return true;
    }
}
