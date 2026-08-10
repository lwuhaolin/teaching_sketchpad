package com.geometry.interaction.action;

import com.geometry.scene.SceneObject;

/**
 * Phase 05 - Action to scale a SceneObject.
 *
 * Created by the InteractionManager when a PINCH gesture is recognised on a
 * selected SceneObject. The scale factor is applied uniformly.
 */
public class ScaleAction implements Action {

    private final SceneObject target;
    /** Uniform scale factor (e.g. 1.5 = 150% of original size). */
    private final float scaleFactor;

    public ScaleAction(SceneObject target, float scaleFactor) {
        if (target == null) {
            throw new IllegalArgumentException("Target SceneObject cannot be null");
        }
        if (scaleFactor <= 0f) {
            throw new IllegalArgumentException("Scale factor must be positive, got " + scaleFactor);
        }
        this.target = target;
        this.scaleFactor = scaleFactor;
    }

    @Override
    public void execute() {
        com.geometry.core.transform.Transform current = target.getOverrideTransform();
        if (current == null) {
            current = target.getGeometry().getTransform();
        }
        com.geometry.core.transform.Transform updated = current.scaleUniform(scaleFactor);
        target.setOverrideTransform(updated);
    }

    @Override
    public String getDescription() {
        return "ScaleAction{target=" + target.getId() + ", factor=" + scaleFactor + "}";
    }

    public SceneObject getTarget() {
        return target;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
}
