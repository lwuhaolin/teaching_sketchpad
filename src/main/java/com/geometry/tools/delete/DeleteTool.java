package com.geometry.tools.delete;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.DeleteAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Delete tool implementation.
 *
 * Handles DeleteAction to remove the selected SceneObject from the Scene.
 * Also handles keyboard DELETE key via InteractionManager.
 */
public class DeleteTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public DeleteTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "delete";
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
        if (action instanceof DeleteAction) {
            DeleteAction deleteAction = (DeleteAction) action;
            deleteAction.execute();
        }
    }

    @Override
    public void update() {
        // DeleteTool is event-driven.
    }
}
