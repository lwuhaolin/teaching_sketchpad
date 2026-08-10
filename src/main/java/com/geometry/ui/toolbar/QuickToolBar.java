package com.geometry.ui.toolbar;

import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 11 - Quick toolbar for whiteboard/touch interaction.
 *
 * A simplified toolbar with larger buttons designed for touch
 * and whiteboard use. Fewer tools than the full ToolBar, but
 * with bigger hit targets.
 *
 * Quick tools:
 *   - move, rotate, measure, play, next
 *
 * The toolbar does NOT hold a reference to the core engine.
 * It only communicates via UIEventBridge.
 *
 * Not thread-safe.
 */
public class QuickToolBar {

    /** Default quick tool names for whiteboard mode. */
    public static final List<String> DEFAULT_QUICK_TOOL_NAMES = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("move");
                add("rotate");
                add("measure");
                add("play");
                add("next");
            }}
    );

    /** The current active tool name. */
    private String activeTool;

    /** The list of quick tool names available. */
    private final List<String> toolNames;

    /** The event bridge to forward clicks to. */
    private final UIEventBridge bridge;

    /**
     * Create a QuickToolBar with default tools and an event bridge.
     *
     * @param bridge the UIEventBridge to forward events to (may be null in tests)
     */
    public QuickToolBar(UIEventBridge bridge) {
        this(DEFAULT_QUICK_TOOL_NAMES, bridge);
    }

    /**
     * Create a QuickToolBar with a custom set of tools and an event bridge.
     *
     * @param toolNames the list of tool names to show
     * @param bridge    the UIEventBridge to forward events to (may be null in tests)
     */
    public QuickToolBar(List<String> toolNames, UIEventBridge bridge) {
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
     * Switch to the quick tool with the given name.
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
     * Get all available quick tool names in order.
     */
    public List<String> getToolNames() {
        return toolNames;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    /**
     * Get the width of the quick toolbar in pixels.
     */
    public int getWidth() {
        return toolNames.size() * QUICK_BUTTON_WIDTH;
    }

    /**
     * Get the height of quick toolbar buttons in pixels.
     */
    public int getHeight() {
        return QUICK_BUTTON_HEIGHT;
    }

    /**
     * Check if a point (x, y) falls within a quick tool button.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the tool name if clicked, or null
     */
    public String getToolAtPosition(int x, int y) {
        if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight()) {
            return null;
        }
        int index = x / QUICK_BUTTON_WIDTH;
        if (index >= 0 && index < toolNames.size()) {
            return toolNames.get(index);
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------------

    /** Width of each quick button in pixels (whiteboard mode, >= 60px). */
    public static final int QUICK_BUTTON_WIDTH = 72;
    /** Height of each quick button in pixels (whiteboard mode, >= 60px). */
    public static final int QUICK_BUTTON_HEIGHT = 72;
}
