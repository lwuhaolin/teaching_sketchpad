package com.geometry.tools.rotate;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.RotateAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Rotate tool implementation.
 *
 * Handles RotateAction to rotate the selected SceneObject around its centre.
 *
 * Mode adaptation:
 *   - 2D mode: rotation is applied to the Z axis only (planar rotation)
 *   - 3D mode: full 3D rotation via the action's angle (Z axis in current impl)
 *
 * The RotateAction already encodes the angle; this tool simply validates
 * and forwards the action.
 */
public class RotateTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public RotateTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "rotate";
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
        if (action instanceof RotateAction) {
            RotateAction rotateAction = (RotateAction) action;
            com.geometry.scene.SceneObject target = rotateAction.getTarget();
            if (target != null && context.getSelectionManager().isSelected(target)) {
                // In 2D mode, constrain to Z-axis rotation (already the default)
                // In 3D mode, allow full rotation
                rotateAction.execute();
            }
        }
    }

    @Override
    public void update() {
        // RotateTool is event-driven; no per-frame state.
    }
}
