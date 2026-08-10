package com.geometry.tools;

/**
 * Phase 06 - Interface for all tools in the Geometry Teaching Engine.
 *
 * All user operations must go through tools — no if(action) branching
 * in business logic. Each tool implements a single user intention:
 * move, rotate, scale, draw, measure, delete, cut.
 *
 * Tools receive Actions (not raw mouse/touch events), making them
 * input-device agnostic. Both mouse drag and touch drag produce
 * the same MoveAction → MoveTool.
 *
 * Lifecycle:
 *   activate()  → used via handle() × N → deactivate()
 *
 * Extension points (future phases):
 *   execute()  / undo()  — Undo/Redo infrastructure (Phase 10)
 */
public interface Tool {

    /**
     * Get the unique name of this tool.
     */
    String getName();

    /**
     * Called when this tool becomes the active tool.
     * Subclasses should initialise any per-tool state here.
     */
    void activate();

    /**
     * Called when another tool takes over.
     * Subclasses should clean up any per-tool state here.
     */
    void deactivate();

    /**
     * Handle an incoming Action.
     *
     * The tool should cast the Action to the expected type and perform
     * the corresponding operation. Unknown action types are ignored.
     *
     * @param action the action to handle
     */
    void handle(com.geometry.interaction.action.Action action);

    /**
     * Per-frame update. Called by ToolManager each frame while this tool
     * is active. Useful for tools that need continuous processing
     * (e.g. DrawTool tracking stroke progress).
     */
    void update();
}
