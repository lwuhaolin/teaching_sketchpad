package com.geometry.tools;

import com.geometry.interaction.action.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Phase 06 - Manages the set of registered tools and the currently active tool.
 *
 * Responsibilities:
 *   - Register tools by name
 *   - Switch the active tool
 *   - Dispatch Actions to the active tool
 *   - Update all tools each frame
 *
 * Design:
 *   Tools are looked up by name (String key). The active tool receives all
 *   Actions. This keeps the dispatch logic in one place and makes it easy
 *   to add new tools without touching existing code.
 *
 * Not thread-safe.
 */
public class ToolManager {

    private final Map<String, Tool> tools;
    private Tool currentTool;

    /**
     * Create a ToolManager.
     */
    public ToolManager() {
        this.tools = new HashMap<>();
        this.currentTool = null;
    }

    // ------------------------------------------------------------------
    // Tool registration
    // ------------------------------------------------------------------

    /**
     * Register a tool under the given name.
     * If a tool with the same name already exists, it is replaced.
     *
     * @param name   unique tool name (e.g. "move", "rotate")
     * @param tool   the tool to register
     */
    public void registerTool(String name, Tool tool) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        if (tool == null) {
            throw new IllegalArgumentException("Tool cannot be null");
        }
        // Deactivate old tool with same name if any
        if (tools.containsKey(name) && tools.get(name) == currentTool) {
            currentTool.deactivate();
        }
        tools.put(name, tool);
    }

    /**
     * Get all registered tool names.
     */
    public List<String> getToolNames() {
        return Collections.unmodifiableList(new ArrayList<>(tools.keySet()));
    }

    // ------------------------------------------------------------------
    // Tool switching
    // ------------------------------------------------------------------

    /**
     * Switch to the tool with the given name.
     * Deactivates the current tool, then activates the new one.
     *
     * @param name the name of the tool to switch to
     * @throws IllegalArgumentException if the tool is not registered
     */
    public void switchTool(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        Tool newTool = tools.get(name);
        if (newTool == null) {
            throw new IllegalArgumentException("Tool not found: " + name);
        }
        if (currentTool != null) {
            currentTool.deactivate();
        }
        currentTool = newTool;
        currentTool.activate();
    }

    /**
     * Get the currently active tool, or null if no tool is active.
     */
    public Tool getCurrentTool() {
        return currentTool;
    }

    /**
     * Get the name of the currently active tool, or null.
     */
    public String getCurrentToolName() {
        return currentTool != null ? currentTool.getName() : null;
    }

    // ------------------------------------------------------------------
    // Action dispatch
    // ------------------------------------------------------------------

    /**
     * Dispatch an Action to the currently active tool.
     *
     * If no tool is active, the action is logged and discarded.
     *
     * @param action the action to dispatch
     */
    public void dispatchAction(Action action) {
        if (action == null) {
            return;
        }
        if (currentTool == null) {
            // No tool active — log a warning
            System.out.println("[ToolManager] No active tool, discarding action: "
                    + action.getDescription());
            return;
        }
        currentTool.handle(action);
    }

    // ------------------------------------------------------------------
    // Frame update
    // ------------------------------------------------------------------

    /**
     * Update the current tool. Called once per frame.
     */
    public void update() {
        if (currentTool != null) {
            currentTool.update();
        }
    }
}
