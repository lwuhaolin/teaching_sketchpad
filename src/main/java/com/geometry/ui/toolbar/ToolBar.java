package com.geometry.ui.toolbar;

import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 11 - Toolbar for tool selection.
 *
 * Displays the primary set of editing tools as clickable buttons.
 * Each button generates a UIEvent when clicked, which is forwarded
 * to the UIEventBridge for dispatch to the ToolManager.
 *
 * Tool buttons:
 *   - select, move, rotate, scale, draw, measure, cut
 *
 * The toolbar does NOT hold a reference to the core engine.
 * It only communicates via UIEventBridge.
 *
 * Not thread-safe.
 */
public class ToolBar {

    /** Default tool names available in the toolbar. */
    public static final List<String> DEFAULT_TOOL_NAMES = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("select");
                add("move");
                add("rotate");
                add("scale");
                add("draw");
                add("measure");
                add("cut");
            }}
    );

    /** The current active tool name. */
    private String activeTool;

    /** The list of tool names available in this toolbar. */
    private final List<String> toolNames;

    /** The event bridge to forward clicks to. */
    private final UIEventBridge bridge;

    /**
     * Create a ToolBar with default tools and an event bridge.
     *
     * @param bridge the UIEventBridge to forward events to (may be null in tests)
     */
    public ToolBar(UIEventBridge bridge) {
        this(DEFAULT_TOOL_NAMES, bridge);
    }

    /**
     * Create a ToolBar with a custom set of tools and an event bridge.
     *
     * @param toolNames the list of tool names to show
     * @param bridge    the UIEventBridge to forward events to (may be null in tests)
     */
    public ToolBar(List<String> toolNames, UIEventBridge bridge) {
        if (toolNames == null || toolNames.isEmpty()) {
            throw new IllegalArgumentException("toolNames cannot be null or empty");
        }
        this.toolNames = Collections.unmodifiableList(new ArrayList<>(toolNames));
        this.bridge = bridge;
        this.activeTool = toolNames.get(0);
    }

    // ------------------------------------------------------------------
    // Tool switching
    // ------------------------------------------------------------------

    /**
     * Switch to the tool with the given name.
     *
     * @param toolName the name of the tool
     * @return true if the tool was found and switched
     */
    public boolean switchTool(String toolName) {
        if (toolName == null || toolName.isEmpty()) {
            return false;
        }
        if (!toolNames.contains(toolName)) {
            return false;
        }
        this.activeTool = toolName;
        if (bridge != null) {
            bridge.submit(UIEvent.toolSwitch(toolName));
        }
        return true;
    }

    /**
     * Get the currently active tool name.
     */
    public String getActiveTool() {
        return activeTool;
    }

    /**
     * Get all available tool names in order.
     */
    public List<String> getToolNames() {
        return toolNames;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    /**
     * Get the width of the toolbar in pixels.
     * Each button takes TOOL_BUTTON_WIDTH pixels.
     */
    public int getWidth() {
        return toolNames.size() * TOOL_BUTTON_WIDTH;
    }

    /**
     * Get the height of toolbar buttons in pixels.
     */
    public int getHeight() {
        return TOOL_BUTTON_HEIGHT;
    }

    /**
     * Check if a point (x, y) falls within a tool button.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tool name if clicked, or null
     */
    public String getToolAtPosition(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return null;
        }
        int index = x / TOOL_BUTTON_WIDTH;
        if (index >= 0 && index < toolNames.size()) {
            return toolNames.get(index);
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    /** Width of each tool button in pixels (desktop mode). */
    public static final int TOOL_BUTTON_WIDTH = 48;
    /** Height of each tool button in pixels (desktop mode). */
    public static final int TOOL_BUTTON_HEIGHT = 48;
}
