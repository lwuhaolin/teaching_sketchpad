package com.geometry.ui;

import com.geometry.animation.AnimationManager;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.TeachingManager;
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
import com.geometry.ui.component.BottomActionBar;
import com.geometry.ui.component.FloatingToolBar;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.component.LessonStatusBar;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.input.InputModeManager;
import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.workspace.TeachingWorkspace;

/**
 * Phase 13 - Real Swing teaching workspace demo.
 *
 * Launches a real window with:
 *   - Geometry canvas showing Cube and Rectangle
 *   - Toolbar for tool selection
 *   - Lesson/status area
 *   - Mode switching (Desktop / Whiteboard / Tablet)
 *
 * Run with:
 *   mvn exec:java -Dexec.mainClass="com.geometry.ui.TeachingWorkspaceDemo"
 */
public class TeachingWorkspaceDemo {

    public static void main(String[] args) {
        System.out.println("=== Geometry Teaching Engine - Teaching Workspace Demo ===\n");

        // 1. Create core engine components
        Scene scene = new Scene();
        ToolManager toolManager = new ToolManager();
        InteractionManager interactionManager = new InteractionManager(scene);
        TeachingManager teachingManager = new TeachingManager(scene, null);
        AnimationManager animationManager = new AnimationManager();

        // 2. Register tools
        ToolContext toolContext = new ToolContext(scene, new com.geometry.scene.SelectionManager(), null, null);
        toolManager.registerTool("select", new SelectTool(toolContext));
        toolManager.registerTool("move", new MoveTool(toolContext));
        toolManager.registerTool("rotate", new RotateTool(toolContext));
        toolManager.registerTool("scale", new ScaleTool(toolContext));
        toolManager.registerTool("draw", new DrawTool(toolContext));
        toolManager.registerTool("measure", new MeasureTool(toolContext));
        toolManager.registerTool("cut", new CutTool(toolContext));
        toolManager.registerTool("delete", new DeleteTool(toolContext));

        // 3. Add geometry objects
        Cube cube = new Cube(2f, 2f, 2f);
        cube.setTransform(new Transform(new Vec3(0, 0, 0), new Vec3(0, 45, 0), new Vec3(1, 1, 1)));
        scene.addObject("cube_001", cube);

        Rectangle rect = new Rectangle(4f, 2f);
        rect.setTransform(new Transform(new Vec3(0, 0, 0), new Vec3(0, 0, 0), new Vec3(1, 1, 1)));
        scene.addObject("rect_001", rect);

        System.out.println("Added " + scene.getObjectCount() + " objects to scene");

        // 4. Create the teaching workspace
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                teachingManager, animationManager);

        // 5. Demo: Input mode management
        InputModeManager inputMgr = ws.getInputModeManager();
        System.out.println("\n--- Input Mode Demo ---");
        System.out.println("Initial mode: " + inputMgr.getMode());
        System.out.println("Desktop: " + inputMgr.isDesktop());
        System.out.println("Touch tolerance: " + inputMgr.getTouchTolerance() + "px");
        System.out.println("Snap radius: " + inputMgr.getObjectSnapRadius() + "px");

        // Switch to whiteboard
        ws.setInputMode(InputMode.WHITEBOARD);
        System.out.println("\nAfter switching to WHITEBOARD:");
        System.out.println("Mode: " + inputMgr.getMode());
        System.out.println("Touch tolerance: " + inputMgr.getTouchTolerance() + "px");
        System.out.println("Snap radius: " + inputMgr.getObjectSnapRadius() + "px");
        System.out.println("Floating toolbar visible: " + ws.getFloatingToolBar().isVisibleNow());

        // Switch to tablet
        ws.setInputMode(InputMode.TABLET);
        System.out.println("\nAfter switching to TABLET:");
        System.out.println("Mode: " + inputMgr.getMode());
        System.out.println("Touch tolerance: " + inputMgr.getTouchTolerance() + "px");
        System.out.println("Snap radius: " + inputMgr.getObjectSnapRadius() + "px");

        // Cycle modes
        inputMgr.cycleMode();
        System.out.println("\nAfter cycle(): " + inputMgr.getMode());
        inputMgr.cycleMode();
        System.out.println("After cycle() again: " + inputMgr.getMode());
        inputMgr.cycleMode();
        System.out.println("After cycle() third time: " + inputMgr.getMode());

        // Switch back to desktop
        ws.setInputMode(InputMode.DESKTOP);
        System.out.println("\nBack to DESKTOP mode");
        System.out.println("Floating toolbar visible: " + ws.getFloatingToolBar().isVisibleNow());

        // 6. Demo: View mode switching
        System.out.println("\n--- View Mode Demo ---");
        ws.setViewMode(ViewMode.MODE_2D);
        System.out.println("Canvas view mode: " + ws.getCanvasView().getViewMode());

        ws.setViewMode(ViewMode.MODE_3D);
        System.out.println("Canvas view mode: " + ws.getCanvasView().getViewMode());

        ws.setViewMode(ViewMode.MODE_2D);

        // 7. Demo: Canvas interaction
        System.out.println("\n--- Canvas Interaction Demo ---");
        GeometryCanvasView canvas = ws.getCanvasView();
        canvas.onTouchDown(400, 300);
        System.out.println("Selected index after touch: " + canvas.getSelectedIndex());

        // 8. Demo: Tool switching via workspace
        System.out.println("\n--- Tool Switching Demo ---");
        ws.switchTool("move");
        System.out.println("Active tool: " + toolManager.getCurrentToolName());
        ws.switchTool("rotate");
        System.out.println("Active tool: " + toolManager.getCurrentToolName());
        ws.switchTool("measure");
        System.out.println("Active tool: " + toolManager.getCurrentToolName());

        // 9. Demo: Status bar
        System.out.println("\n--- Status Bar Demo ---");
        ws.updateStatusBar("Triangle Properties", "Step 2 / 5");
        System.out.println("Status bar updated");

        // 10. Demo: Theme
        System.out.println("\n--- Theme Demo ---");
        EducationTheme theme = new EducationTheme();
        System.out.println("Background: #" + Integer.toHexString(theme.getBackgroundColor().getRGB()).toUpperCase());
        System.out.println("Active tool: #" + Integer.toHexString(theme.getActiveToolColor().getRGB()).toUpperCase());
        System.out.println("Selection: #" + Integer.toHexString(theme.getSelectionColor().getRGB()).toUpperCase());
        System.out.println("Font (desktop): " + theme.getFont(InputMode.DESKTOP));
        System.out.println("Font (whiteboard): " + theme.getFont(InputMode.WHITEBOARD));

        // 11. Demo: Floating toolbar
        System.out.println("\n--- Floating Toolbar Demo ---");
        FloatingToolBar ft = ws.getFloatingToolBar();
        ft.showDefault();
        System.out.println("Floating toolbar shown: " + ft.isVisibleNow());
        ft.hide();
        System.out.println("Floating toolbar hidden: " + !ft.isVisibleNow());

        // 12. Demo: Bottom action bar
        System.out.println("\n--- Bottom Action Bar Demo ---");
        BottomActionBar bar = ws.getBottomActionBar();
        System.out.println("Button count: " + bar.getButtonCount());
        System.out.println("Bar height: " + bar.getBarHeight());

        // 13. Demo: Input mode manager values
        System.out.println("\n--- Input Mode Manager Values ---");
        ws.setInputMode(InputMode.DESKTOP);
        System.out.println("Desktop min button: " + inputMgr.getMinButtonSize());
        System.out.println("Desktop floating toolbar width: " + inputMgr.getFloatingToolBarWidth());
        ws.setInputMode(InputMode.WHITEBOARD);
        System.out.println("Whiteboard min button: " + inputMgr.getMinButtonSize());
        System.out.println("Whiteboard touch target: " + inputMgr.getTouchTargetSize());
        ws.setInputMode(InputMode.TABLET);
        System.out.println("Tablet min button: " + inputMgr.getMinButtonSize());
        System.out.println("Tablet touch target: " + inputMgr.getTouchTargetSize());

        // 14. Show the window
        System.out.println("\n=== Launching Window ===");
        ws.showWorkspace();
        System.out.println("Window title: " + ws.getTitle());
        System.out.println("Window size: " + ws.getSize());
        System.out.println("Workspace created: " + ws.getWorkspace() != null);
        System.out.println("Input mode: " + ws.getInputModeManager().getMode());
        System.out.println("\nDemo complete. Close the window to exit.");
    }
}
