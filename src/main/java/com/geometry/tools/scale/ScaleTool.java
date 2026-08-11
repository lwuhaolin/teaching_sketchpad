package com.geometry.tools.scale;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.ScaleAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Scale tool implementation.
 *
 * Handles ScaleAction to uniformly scale the selected SceneObject.
 *
 * Future extension:
 *   - XYZ independent scale axes
 *   - Scale from a pivot point
 */
public class ScaleTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public ScaleTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "scale";
    }

    @Override
    public void activate() {
        this.active = true;
    }

    @Override
    public void deactivate() {
        this.active = false;
    }

    @Override
    public void handle(Action action) {
        if (!active) {
            return;
        }
        if (action instanceof ScaleAction) {
            ScaleAction scaleAction = (ScaleAction) action;
            com.geometry.scene.SceneObject target = scaleAction.getTarget();
            if (target != null && context.getSelectionManager().isSelected(target)) {
                com.geometry.core.transform.Transform current = target.getEffectiveTransform();
                target.setOverrideTransform(context.getConstraint()
                        .constrainTransform(current.scaleUniform(scaleAction.getScaleFactor())));
            }
        }
    }

    @Override
    public void update() {
        // ScaleTool is event-driven; no per-frame state.
    }
}
