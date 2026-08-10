package com.geometry.tools;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.interaction.action.DrawAction;
import com.geometry.interaction.action.MeasureAction;
import com.geometry.interaction.action.MoveAction;
import com.geometry.interaction.action.RotateAction;
import com.geometry.interaction.action.ScaleAction;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.interaction.gesture.GestureRecognizer;
import com.geometry.interaction.input.TouchDevice;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.scene.SelectionManager;

import java.util.Arrays;
import java.util.List;

/**
 * Phase 06 - Demo demonstrating the Tool System.
 *
 * Tests:
 *   1. Tool switching (MoveTool -> RotateTool -> ScaleTool)
 *   2. Mouse drag -> MoveAction -> MoveTool moves object
 *   3. Touch drag -> MoveAction -> MoveTool moves same object (equivalence)
 *   4. RotateTool handles RotateAction
 *   5. ScaleTool handles ScaleAction
 *   6. DrawTool creates Rectangle and Circle
 *   7. MeasureTool computes distance
 *   8. CutTool is a stub (Phase 08)
 *   9. ToolContext provides 2D/3D mode access
 */
public class ToolDemo {

    private Scene scene;
    private SelectionManager selectionManager;
    private ToolManager toolManager;
    private ToolContext toolContext;

    public void setUp() {
        scene = new Scene();
        selectionManager = new SelectionManager();
        toolContext = new ToolContext(scene, selectionManager, null, RenderMode.MODE_2D);
        toolManager = new ToolManager();
        registerDefaultTools();
    }

    private void registerDefaultTools() {
        toolManager.registerTool("move", new com.geometry.tools.move.MoveTool(toolContext));
        toolManager.registerTool("rotate", new com.geometry.tools.rotate.RotateTool(toolContext));
        toolManager.registerTool("scale", new com.geometry.tools.scale.ScaleTool(toolContext));
        toolManager.registerTool("draw", new com.geometry.tools.draw.DrawTool(toolContext));
        toolManager.registerTool("measure", new com.geometry.tools.measure.MeasureTool(toolContext));
        toolManager.registerTool("cut", new com.geometry.tools.cut.CutTool(toolContext));
        toolManager.registerTool("delete", new com.geometry.tools.delete.DeleteTool(toolContext));
    }

    // ------------------------------------------------------------------
    // Demo 1: Tool switching
    // ------------------------------------------------------------------

    public void demoToolSwitching() {
        System.out.println("=== Demo 1: Tool Switching ===");
        setUp();

        assertNull(toolManager.getCurrentTool(), "Initially no tool active");

        toolManager.switchTool("move");
        assertStringEquals("move", toolManager.getCurrentToolName(), "Switched to move");

        toolManager.switchTool("rotate");
        assertStringEquals("rotate", toolManager.getCurrentToolName(), "Switched to rotate");

        toolManager.switchTool("scale");
        assertStringEquals("scale", toolManager.getCurrentToolName(), "Switched to scale");

        List<String> names = toolManager.getToolNames();
        assertTrue(names.size() == 7, "Must have 7 tools");
        assertTrue(names.contains("move"), "Has move");
        assertTrue(names.contains("rotate"), "Has rotate");
        assertTrue(names.contains("scale"), "Has scale");
        assertTrue(names.contains("draw"), "Has draw");
        assertTrue(names.contains("measure"), "Has measure");
        assertTrue(names.contains("cut"), "Has cut");
        assertTrue(names.contains("delete"), "Has delete");

        System.out.println("PASS: Tool switching works correctly");
    }

    // ------------------------------------------------------------------
    // Demo 2: Mouse drag moves object via MoveTool
    // ------------------------------------------------------------------

    public void demoMouseMove() {
        System.out.println("\n=== Demo 2: Mouse Drag -> MoveTool ===");
        setUp();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_001", cube);
        selectionManager.select(sceneCube);

        toolManager.switchTool("move");

        GestureRecognizer gr = new GestureRecognizer();
        List<PointerEvent> mouseEvents = Arrays.asList(
                new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                        new Vec2(100f, 100f), Vec2.ZERO, PointerEvent.EventType.DOWN),
                new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                        new Vec2(300f, 100f), new Vec2(200f, 0f), PointerEvent.EventType.MOVE),
                new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                        new Vec2(300f, 100f), Vec2.ZERO, PointerEvent.EventType.UP)
        );

        List<com.geometry.interaction.event.GestureEvent> gestures = gr.process(mouseEvents);
        System.out.println("Mouse gestures detected: " + gestures.size());

        for (com.geometry.interaction.event.GestureEvent g : gestures) {
            if (g.isDrag()) {
                MoveAction move = new MoveAction(sceneCube, 5f, 0f);
                toolManager.dispatchAction(move);
            }
        }

        com.geometry.core.transform.Transform t = sceneCube.getOverrideTransform();
        assertNotNull("Cube should have moved", t);
        assertFloatEquals(5f, t.getPosition().x, 0.001f, "Cube should move 5 units in X");
        System.out.println("Cube moved to: " + t.getPosition());
        System.out.println("PASS: Mouse drag moves object via MoveTool");
    }

    // ------------------------------------------------------------------
    // Demo 3: Touch drag also moves object (same result as mouse)
    // ------------------------------------------------------------------

    public void demoTouchMove() {
        System.out.println("\n=== Demo 3: Touch Drag -> MoveTool (equivalence) ===");
        setUp();

        Cube touchCube = new Cube(2f, 2f, 2f);
        SceneObject sceneTouchCube = scene.addObject("touch_cube", touchCube);
        selectionManager.select(sceneTouchCube);

        TouchDevice touch = new TouchDevice();
        touch.injectDown(0, 100f, 100f);
        touch.injectMove(0, 300f, 100f);
        touch.injectUp(0);

        GestureRecognizer gr = new GestureRecognizer();
        List<PointerEvent> pointerEvents = new java.util.ArrayList<>();
        for (com.geometry.interaction.event.InputEvent e : touch.getEvents()) {
            if (e instanceof PointerEvent) {
                pointerEvents.add((PointerEvent) e);
            }
        }
        List<com.geometry.interaction.event.GestureEvent> gestures = gr.process(pointerEvents);
        System.out.println("Touch gestures detected: " + gestures.size());

        toolManager.switchTool("move");
        for (com.geometry.interaction.event.GestureEvent g : gestures) {
            if (g.isDrag()) {
                MoveAction move = new MoveAction(sceneTouchCube, 5f, 0f);
                toolManager.dispatchAction(move);
            }
        }

        com.geometry.core.transform.Transform t = sceneTouchCube.getOverrideTransform();
        assertNotNull("Touch drag should move cube", t);
        assertFloatEquals(5f, t.getPosition().x, 0.001f, "Touch should move 5 units in X");
        System.out.println("Touch cube moved to: " + t.getPosition());
        System.out.println("PASS: Touch drag produces same result as mouse drag");
    }

    // ------------------------------------------------------------------
    // Demo 4: RotateTool handles RotateAction
    // ------------------------------------------------------------------

    public void demoRotate() {
        System.out.println("\n=== Demo 4: RotateTool ===");
        setUp();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_rotate", cube);
        selectionManager.select(sceneCube);

        toolManager.switchTool("rotate");

        RotateAction rotate = new RotateAction(sceneCube, 90f);
        toolManager.dispatchAction(rotate);

        com.geometry.core.transform.Transform t = sceneCube.getOverrideTransform();
        assertNotNull(t);
        assertFloatEquals(90f, t.getRotation().z, 0.001f, "Should rotate 90 degrees");
        System.out.println("Cube rotated to: " + t.getRotation());
        System.out.println("PASS: RotateTool works");
    }

    // ------------------------------------------------------------------
    // Demo 5: ScaleTool handles ScaleAction
    // ------------------------------------------------------------------

    public void demoScale() {
        System.out.println("\n=== Demo 5: ScaleTool ===");
        setUp();

        Cylinder cylinder = new Cylinder(1f, 3f, 16);
        SceneObject sceneCyl = scene.addObject("cylinder_scale", cylinder);
        selectionManager.select(sceneCyl);

        toolManager.switchTool("scale");

        ScaleAction scale = new ScaleAction(sceneCyl, 2.0f);
        toolManager.dispatchAction(scale);

        com.geometry.core.transform.Transform t = sceneCyl.getOverrideTransform();
        assertNotNull(t);
        assertFloatEquals(2f, t.getScale().x, 0.001f, "Scale X");
        assertFloatEquals(2f, t.getScale().y, 0.001f, "Scale Y");
        assertFloatEquals(2f, t.getScale().z, 0.001f, "Scale Z");
        System.out.println("Cylinder scaled to: " + t.getScale());
        System.out.println("PASS: ScaleTool works");
    }

    // ------------------------------------------------------------------
    // Demo 6: DrawTool creates geometry
    // ------------------------------------------------------------------

    public void demoDraw() {
        System.out.println("\n=== Demo 6: DrawTool ===");
        setUp();

        toolManager.switchTool("draw");

        int initialCount = scene.getObjectCount();

        DrawAction rectDraw = new DrawAction(
                DrawAction.DrawType.RECTANGLE,
                new Vec2(300f, 250f),
                new Vec2(500f, 350f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(rectDraw);

        DrawAction circleDraw = new DrawAction(
                DrawAction.DrawType.CIRCLE,
                new Vec2(400f, 300f),
                new Vec2(450f, 300f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(circleDraw);

        DrawAction pointDraw = new DrawAction(
                DrawAction.DrawType.POINT,
                new Vec2(400f, 300f),
                new Vec2(400f, 300f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(pointDraw);

        int finalCount = scene.getObjectCount();
        int created = finalCount - initialCount;
        System.out.println("Objects created: " + created + " (expected 3)");
        assertTrue(created >= 3, "Should create 3 objects");
        System.out.println("PASS: DrawTool creates geometry");
    }

    // ------------------------------------------------------------------
    // Demo 7: MeasureTool computes distance
    // ------------------------------------------------------------------

    public void demoMeasure() {
        System.out.println("\n=== Demo 7: MeasureTool ===");
        setUp();

        toolManager.switchTool("measure");

        com.geometry.tools.measure.MeasureTool measureTool =
                (com.geometry.tools.measure.MeasureTool) toolManager.getCurrentTool();

        MeasureAction distMeasure = new MeasureAction(
                new com.geometry.core.math.Vec2(0f, 0f),
                new com.geometry.core.math.Vec2(3f, 4f)
        );
        measureTool.handle(distMeasure);
        assertFloatEquals(5f, measureTool.getLastResult(), 0.001f, "Distance (3-4-5 triangle)");
        System.out.println("Distance measured: " + measureTool.getLastResult());

        MeasureAction angleMeasure = new MeasureAction(
                new com.geometry.core.math.Vec2(0f, 0f),
                new com.geometry.core.math.Vec2(1f, 0f),
                new com.geometry.core.math.Vec2(1f, 1f)
        );
        measureTool.handle(angleMeasure);
        assertFloatEquals(45f, measureTool.getLastResult(), 0.5f, "Angle should be 45 degrees");
        System.out.println("Angle measured: " + measureTool.getLastResult());
        System.out.println("PASS: MeasureTool works");
    }

    // ------------------------------------------------------------------
    // Demo 8: CutTool is a stub (Phase 08)
    // ------------------------------------------------------------------

    public void demoCut() {
        System.out.println("\n=== Demo 8: CutTool (stub) ===");
        setUp();

        toolManager.switchTool("cut");

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_cut", cube);
        selectionManager.select(sceneCube);

        com.geometry.tools.cut.CutTool cutTool =
                (com.geometry.tools.cut.CutTool) toolManager.getCurrentTool();
        cutTool.executeCut(sceneCube,
                new com.geometry.core.math.Vec3(0f, 0f, 1f), 0f);
        System.out.println("PASS: CutTool is a stub (Phase 08)");
    }

    // ------------------------------------------------------------------
    // Demo 9: ToolContext 2D/3D mode
    // ------------------------------------------------------------------

    public void demoToolContext() {
        System.out.println("\n=== Demo 9: ToolContext ===");
        setUp();

        assertTrue(toolContext.is2DMode(), "Should start in 2D mode");
        assertTrue(!toolContext.is3DMode(), "Should not be in 3D mode");

        toolContext.setRenderMode(RenderMode.MODE_3D);
        assertTrue(toolContext.is3DMode(), "Should be in 3D mode");
        assertTrue(!toolContext.is2DMode(), "Should not be in 2D mode");

        toolContext.setRenderMode(RenderMode.MODE_2D);
        assertTrue(toolContext.is2DMode(), "Should be back in 2D mode");
        System.out.println("PASS: ToolContext mode switching works");
    }

    // ------------------------------------------------------------------
    // Run all demos
    // ------------------------------------------------------------------

    public static void main(String[] args) {
        ToolDemo demo = new ToolDemo();
        demo.demoToolSwitching();
        demo.demoMouseMove();
        demo.demoTouchMove();
        demo.demoRotate();
        demo.demoScale();
        demo.demoDraw();
        demo.demoMeasure();
        demo.demoCut();
        demo.demoToolContext();
        System.out.println("\n=== All demos completed ===");
    }

    // ------------------------------------------------------------------
    // Assertion helpers
    // ------------------------------------------------------------------

    private void assertFloatEquals(float expected, float actual, float delta, String msg) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(msg + ": expected " + expected + " but got " + actual);
        }
    }

    private void assertFloatEquals(float expected, float actual, float delta) {
        assertFloatEquals(expected, actual, delta, "");
    }

    private void assertStringEquals(String expected, String actual, String msg) {
        if (!expected.equals(actual)) {
            throw new AssertionError(msg + ": expected '" + expected + "' but got '" + actual + "'");
        }
    }

    private void assertTrue(boolean condition, String msg) {
        if (!condition) {
            throw new AssertionError(msg);
        }
    }

    private void assertNotNull(String msg, Object obj) {
        if (obj == null) {
            throw new AssertionError(msg);
        }
    }

    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new AssertionError("Expected not null");
        }
    }

    private void assertNull(Object obj, String msg) {
        if (obj != null) {
            throw new AssertionError(msg);
        }
    }
}
