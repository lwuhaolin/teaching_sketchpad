package com.geometry.tools.move;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.MoveAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Move tool implementation.
 *
 * Handles MoveAction to translate the selected SceneObject.
 * In 2D mode, movement is constrained to the z=0 plane.
 * In 3D mode, full 3D translation is supported (via delta Z).
 *
 * Design:
 *   The Action carries the delta; the tool simply executes it.
 *   This keeps MoveTool thin and reusable across input modes.
 */
public class MoveTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public MoveTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "move";
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
        if (action instanceof MoveAction) {
            MoveAction moveAction = (MoveAction) action;
            // Verify the target is still in the scene and selected
            com.geometry.scene.SceneObject target = moveAction.getTarget();
            if (target != null && context.getSelectionManager().isSelected(target)) {
                com.geometry.core.transform.Transform current = target.getEffectiveTransform();
                com.geometry.core.math.Vec3 delta = context.getConstraint()
                        .constrainTranslation(moveAction.getDelta());
                target.setOverrideTransform(context.getConstraint()
                        .constrainTransform(current.translate(delta)));
            }
        }
        // Other action types are ignored by this tool
    }

    @Override
    public void update() {
        // MoveTool is event-driven; no per-frame state to update.
    }
}
