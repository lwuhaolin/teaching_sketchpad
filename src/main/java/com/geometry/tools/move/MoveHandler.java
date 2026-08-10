package com.geometry.tools.move;

import com.geometry.interaction.action.MoveAction;
import com.geometry.scene.SceneObject;
import com.geometry.core.transform.Transform;

/**
 * Phase 06 - Handler for MoveAction execution logic.
 *
 * Decouples MoveTool from the raw transform manipulation.
 * MoveTool creates and delegates to this handler.
 *
 * This class is package-private; it is only used by MoveTool.
 */
class MoveHandler {

    /**
     * Apply a MoveAction to the given SceneObject.
     *
     * @param target the selected SceneObject
     * @param action the move action containing the delta
     */
    static void applyMove(SceneObject target, MoveAction action) {
        if (target == null || action == null) {
            return;
        }
        action.execute();
    }

    /**
     * Create a MoveAction for the given target and delta.
     *
     * @param target  the SceneObject to move
     * @param deltaX  world-space X delta
     * @param deltaY  world-space Y delta
     * @return the new MoveAction
     */
    static MoveAction createMoveAction(SceneObject target, float deltaX, float deltaY) {
        return new MoveAction(target, deltaX, deltaY);
    }
}
