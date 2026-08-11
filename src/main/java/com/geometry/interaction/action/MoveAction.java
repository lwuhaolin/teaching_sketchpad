package com.geometry.interaction.action;

import com.geometry.scene.SceneObject;

/**
 * Phase 05 - Action to move (translate) a SceneObject.
 *
 * Created by the InteractionManager when a DRAG gesture is recognised on a
 * selected SceneObject. The movement is applied to the object's override
 * transform position.
 */
public class MoveAction implements Action {

    private final SceneObject target;
    /** Movement delta in world space units. */
    private final float deltaX;
    private final float deltaY;
    private final float deltaZ;

    public MoveAction(SceneObject target, float deltaX, float deltaY) {
        this(target, deltaX, deltaY, 0f);
    }

    public MoveAction(SceneObject target, float deltaX, float deltaY, float deltaZ) {
        if (target == null) {
            throw new IllegalArgumentException("Target SceneObject cannot be null");
        }
        this.target = target;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
    }

    @Override
    public void execute() {
        com.geometry.core.math.Vec3 delta =
                new com.geometry.core.math.Vec3(deltaX, deltaY, deltaZ);
        com.geometry.core.transform.Transform current = target.getOverrideTransform();
        if (current == null) {
            current = target.getGeometry().getTransform();
        }
        com.geometry.core.transform.Transform updated = current.translate(delta);
        target.setOverrideTransform(updated);
    }

    @Override
    public String getDescription() {
        return "MoveAction{target=" + target.getId()
                + ", delta=(" + deltaX + ", " + deltaY + ")}";
    }

    public SceneObject getTarget() {
        return target;
    }

    public float getDeltaX() {
        return deltaX;
    }

    public float getDeltaY() {
        return deltaY;
    }

    public float getDeltaZ() { return deltaZ; }

    public com.geometry.core.math.Vec3 getDelta() {
        return new com.geometry.core.math.Vec3(deltaX, deltaY, deltaZ);
    }
}
