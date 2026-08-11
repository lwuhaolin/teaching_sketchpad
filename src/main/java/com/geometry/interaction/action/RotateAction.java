package com.geometry.interaction.action;

import com.geometry.scene.SceneObject;

/**
 * Phase 05 - Action to rotate a SceneObject around its centre.
 *
 * Created by the InteractionManager when a ROTATE gesture is recognised on a
 * selected SceneObject. The rotation is applied as Euler angle delta to the
 * object's override transform.
 */
public class RotateAction implements Action {

    private final SceneObject target;
    /** Rotation delta in degrees (around Z axis for 2D, or full Euler for 3D). */
    private final float angleDegrees;
    private final com.geometry.core.math.Vec3 delta;

    public RotateAction(SceneObject target, float angleDegrees) {
        this(target, new com.geometry.core.math.Vec3(0f, 0f, angleDegrees));
    }

    public RotateAction(SceneObject target, com.geometry.core.math.Vec3 delta) {
        if (target == null) {
            throw new IllegalArgumentException("Target SceneObject cannot be null");
        }
        this.target = target;
        if (delta == null) {
            throw new IllegalArgumentException("Rotation delta cannot be null");
        }
        this.delta = delta;
        this.angleDegrees = delta.z;
    }

    @Override
    public void execute() {
        com.geometry.core.transform.Transform current = target.getOverrideTransform();
        if (current == null) {
            current = target.getGeometry().getTransform();
        }
        com.geometry.core.transform.Transform updated = current.rotate(delta);
        target.setOverrideTransform(updated);
    }

    @Override
    public String getDescription() {
        return "RotateAction{target=" + target.getId() + ", angle=" + angleDegrees + "}";
    }

    public SceneObject getTarget() {
        return target;
    }

    public float getAngleDegrees() {
        return angleDegrees;
    }

    public com.geometry.core.math.Vec3 getDelta() { return delta; }
}
