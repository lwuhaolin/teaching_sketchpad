package com.geometry.ui;

import com.geometry.animation.AnimationManager;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.Lesson;
import com.geometry.teaching.Step;
import com.geometry.teaching.TeachingManager;
import com.geometry.tools.ToolContext;
import com.geometry.tools.ToolManager;
import com.geometry.tools.draw.DrawTool;
import com.geometry.tools.cut.CutTool;
import com.geometry.tools.delete.DeleteTool;
import com.geometry.tools.measure.MeasureTool;
import com.geometry.tools.move.MoveTool;
import com.geometry.tools.rotate.RotateTool;
import com.geometry.tools.scale.ScaleTool;
import com.geometry.tools.select.SelectTool;
import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.canvas.CanvasInteractionLayer;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.ui.toolbar.QuickToolBar;
import com.geometry.ui.toolbar.ToolBar;

/**
 * Phase 11 - UI system demo.
 *
 * Demonstrates the complete UI workspace system:
 *   - Creates a Scene with Cube and Rectangle
 *   - Builds the full Workspace with all panels
 *   - Shows toolbar tool switching
 *   - Shows scene tree population
 *   - Shows property panel updates on selection
 *   - Shows teaching panel state
 *
 * This demo runs headlessly (no OS window required).
 */
public class UIDemo {

    public static void main(String[] args) {
        System.out.println("=== Geometry Teaching Engine - UI Demo ===\n");

        // 1. Create core engine components
        Scene scene = new Scene();
        ToolManager toolManager = new ToolManager();
        InteractionManager interactionManager = new InteractionManager(scene);
        TeachingManager teachingManager = new TeachingManager(scene, null);
        AnimationManager animationManager = new AnimationManager();

        // 2. Register tools with ToolContext
        ToolContext toolContext = new ToolContext(scene, new com.geometry.scene.SelectionManager(), null, null);
        registerTools(toolManager, toolContext);

        // 3. Add geometry objects to scene
        addObjects(scene);

        // 4. Create UI event bridge
        UIEventBridge bridge = new UIEventBridge(toolManager, scene, interactionManager);

        // 5. Create layout manager
        LayoutManager layoutManager = new LayoutManager(
                UIInteractionMode.DESKTOP, 1024, 768);

        // 6. Create panels
        SceneTreePanel sceneTreePanel = new SceneTreePanel(scene, bridge);
        PropertyPanel propertyPanel = new PropertyPanel();
        TeachingPanel teachingPanel = new TeachingPanel(teachingManager, bridge);
        AnimationPanel animationPanel = new AnimationPanel(animationManager, bridge);

        // 7. Create workspace
        Workspace workspace = new Workspace(
                layoutManager, bridge,
                sceneTreePanel, propertyPanel,
                teachingPanel, animationPanel);

        // 8. Set up canvas interaction
        CanvasInteractionLayer canvasLayer = new CanvasInteractionLayer(scene, interactionManager);
        workspace.setCanvasInteractionLayer(canvasLayer);

        // 9. Set view mode
        workspace.setViewMode(ViewMode.MODE_3D);

        // 10. Demo: Toolbar
        System.out.println("--- Toolbar Demo ---");
        ToolBar toolbar = workspace.getToolBar();
        System.out.println("Active tool: " + toolbar.getActiveTool());
        System.out.println("Available tools: " + toolbar.getToolNames());
        System.out.println("Toolbar size: " + toolbar.getWidth() + "x" + toolbar.getHeight());

        // Switch tools
        toolbar.switchTool("move");
        System.out.println("After switching to move: " + toolbar.getActiveTool());

        // 11. Demo: Quick Toolbar (whiteboard mode)
        System.out.println("\n--- QuickToolBar Demo ---");
        QuickToolBar quickToolbar = workspace.getQuickToolBar();
        System.out.println("Quick toolbar size: " + quickToolbar.getWidth() + "x" + quickToolbar.getHeight());
        System.out.println("Quick toolbar button size: " + QuickToolBar.QUICK_BUTTON_WIDTH + "x" + QuickToolBar.QUICK_BUTTON_HEIGHT);

        // 12. Demo: Scene Tree
        System.out.println("\n--- SceneTreePanel Demo ---");
        System.out.println("Objects in scene: " + sceneTreePanel.getObjectCount());
        for (int i = 0; i < sceneTreePanel.getObjectCount(); i++) {
            System.out.println("  [" + i + "] " + sceneTreePanel.getLabelText(i));
        }

        // Select first object
        sceneTreePanel.selectByIndex(0);
        System.out.println("Selected object: " + sceneTreePanel.getSelectedObjectId());
        System.out.println("Queued events: " + bridge.getQueuedEventCount());

        // 13. Demo: Property Panel
        System.out.println("\n--- PropertyPanel Demo ---");
        SceneObject selected = scene.getSelected();
        propertyPanel.selectObject(selected);
        System.out.println("Selected object type: " + (selected != null ? selected.getGeometry().getClass().getSimpleName() : "none"));
        System.out.println("Property rows: " + propertyPanel.getPropertyRowCount());
        for (int i = 0; i < propertyPanel.getPropertyRowCount(); i++) {
            System.out.println("  " + propertyPanel.getPropertyLabel(i));
        }

        // 14. Demo: Teaching Panel
        System.out.println("\n--- TeachingPanel Demo ---");
        System.out.println("Lesson: " + teachingPanel.getLessonName());
        System.out.println("Step: " + teachingPanel.getStepDisplay());

        // 15. Demo: Layout Manager
        System.out.println("\n--- LayoutManager Demo ---");
        System.out.println("Window size: " + workspace.getWidth() + "x" + workspace.getHeight());
        int[] toolbarLayout = layoutManager.getToolbarLayout();
        System.out.println("Toolbar: x=" + toolbarLayout[0] + " y=" + toolbarLayout[1]
                + " w=" + toolbarLayout[2] + " h=" + toolbarLayout[3]);
        int[] canvasLayout = layoutManager.getCanvasLayout();
        System.out.println("Canvas: x=" + canvasLayout[0] + " y=" + canvasLayout[1]
                + " w=" + canvasLayout[2] + " h=" + canvasLayout[3]);
        int[] sceneTreeLayout = layoutManager.getSceneTreeLayout();
        System.out.println("SceneTree: x=" + sceneTreeLayout[0] + " y=" + sceneTreeLayout[1]
                + " w=" + sceneTreeLayout[2] + " h=" + sceneTreeLayout[3]);

        // Switch to whiteboard mode
        System.out.println("\n--- Whiteboard Mode ---");
        workspace.setInteractionMode(UIInteractionMode.WHITEBOARD);
        System.out.println("Mode: " + workspace.getInteractionMode());
        System.out.println("Button size: " + workspace.getLayoutManager().getButtonSize() + "px");
        toolbarLayout = layoutManager.getToolbarLayout();
        System.out.println("Toolbar height: " + toolbarLayout[3]);
        sceneTreeLayout = layoutManager.getSceneTreeLayout();
        System.out.println("SceneTree visible in whiteboard: " + (sceneTreeLayout != null));

        // 16. Demo: UIEventBridge
        System.out.println("\n--- UIEventBridge Demo ---");
        System.out.println("Queue empty before dispatch: " + bridge.isQueueEmpty());
        bridge.dispatchAll();
        System.out.println("Active tool after dispatch: " + toolManager.getCurrentToolName());

        // 17. Demo: ApplicationWindow
        System.out.println("\n--- ApplicationWindow Demo ---");
        ApplicationWindow appWindow = new ApplicationWindow(
                scene, toolManager, interactionManager,
                teachingManager, animationManager);
        appWindow.create();
        System.out.println("Window created: " + appWindow.isCreated());
        System.out.println("Workspace mode: " + appWindow.getWorkspace().getInteractionMode());

        System.out.println("\n=== UI Demo Complete ===");
    }

    /**
     * Register all standard tools with the tool manager.
     */
    private static void registerTools(ToolManager toolManager, ToolContext context) {
        toolManager.registerTool("select", new SelectTool(context));
        toolManager.registerTool("move", new MoveTool(context));
        toolManager.registerTool("rotate", new RotateTool(context));
        toolManager.registerTool("scale", new ScaleTool(context));
        toolManager.registerTool("draw", new DrawTool(context));
        toolManager.registerTool("measure", new MeasureTool(context));
        toolManager.registerTool("cut", new CutTool(context));
        toolManager.registerTool("delete", new DeleteTool(context));
    }

    /**
     * Add demo geometry objects to the scene.
     */
    private static void addObjects(Scene scene) {
        // Add a cube
        Cube cube = new Cube(2f, 2f, 2f);
        cube.setTransform(new Transform(new Vec3(0, 0, 0), new Vec3(0, 45, 0), new Vec3(1, 1, 1)));
        scene.addObject("cube_001", cube);

        // Add a rectangle (2D)
        Rectangle rect = new Rectangle(4f, 2f);
        rect.setTransform(new Transform(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));
        scene.addObject("rect_001", rect);

        System.out.println("Added " + scene.getObjectCount() + " objects to scene");
    }
}
