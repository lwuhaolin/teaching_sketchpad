package com.geometry.tools.select;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.SelectAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Select tool implementation.
 *
 * Handles SelectAction to select/deselect SceneObjects.
 * This is the default tool — clicking an object selects it.
 */
public class SelectTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public SelectTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "select";
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
        if (action instanceof SelectAction) {
            SelectAction selectAction = (SelectAction) action;
            selectAction.execute();
        }
    }

    @Override
    public void update() {
        // SelectTool is event-driven.
    }
}
