package com.geometry.ui.bridge;

import com.geometry.scene.Scene;
import com.geometry.scene.SelectionManager;
import com.geometry.tools.ToolContext;
import com.geometry.tools.ToolManager;
import com.geometry.tools.cut.CutTool;
import com.geometry.tools.delete.DeleteTool;
import com.geometry.tools.draw.DrawTool;
import com.geometry.tools.measure.MeasureTool;
import com.geometry.tools.move.MoveTool;
import com.geometry.tools.rotate.RotateTool;
import com.geometry.tools.scale.ScaleTool;
import com.geometry.tools.select.SelectTool;

import java.util.Map;
import java.util.WeakHashMap;

/** Registers the standard teaching tools when the host application has none. */
public final class ToolBootstrapper {

    private static final Map<ToolManager, SelectionManager> SELECTIONS =
            new WeakHashMap<ToolManager, SelectionManager>();
    private static final Map<ToolManager, ToolContext> CONTEXTS =
            new WeakHashMap<ToolManager, ToolContext>();

    private ToolBootstrapper() {
    }

    public static void registerMissingTools(ToolManager toolManager, Scene scene) {
        if (toolManager == null || scene == null) {
            return;
        }
        SelectionManager selectionManager = SELECTIONS.get(toolManager);
        if (selectionManager == null) {
            selectionManager = new SelectionManager();
            SELECTIONS.put(toolManager, selectionManager);
        }
        ToolContext context = CONTEXTS.get(toolManager);
        if (context == null) {
            context = new ToolContext(scene, selectionManager, null, null);
            CONTEXTS.put(toolManager, context);
        }
        register(toolManager, "select", new SelectTool(context));
        register(toolManager, "move", new MoveTool(context));
        register(toolManager, "rotate", new RotateTool(context));
        register(toolManager, "scale", new ScaleTool(context));
        register(toolManager, "measure", new MeasureTool(context));
        register(toolManager, "cut", new CutTool(context));
        register(toolManager, "draw", new DrawTool(context));
        register(toolManager, "delete", new DeleteTool(context));
        if (toolManager.getCurrentTool() == null) {
            toolManager.switchTool("select");
        }
    }

    /** Returns the selection state shared by the canvas and registered tools. */
    public static SelectionManager getSelectionManager(ToolManager toolManager) {
        return toolManager == null ? null : SELECTIONS.get(toolManager);
    }

    /** Returns the shared context so the application can apply view constraints. */
    public static ToolContext getToolContext(ToolManager toolManager) {
        return toolManager == null ? null : CONTEXTS.get(toolManager);
    }

    private static void register(ToolManager manager, String name, com.geometry.tools.Tool tool) {
        if (!manager.getToolNames().contains(name)) {
            manager.registerTool(name, tool);
        }
    }
}
