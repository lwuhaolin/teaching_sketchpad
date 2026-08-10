package com.geometry.tools;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Rectangle;
import com.geometry.interaction.action.DeleteAction;
import com.geometry.interaction.action.DrawAction;
import com.geometry.interaction.action.MeasureAction;
import com.geometry.interaction.action.MoveAction;
import com.geometry.interaction.action.RotateAction;
import com.geometry.interaction.action.ScaleAction;
import com.geometry.interaction.event.Vec2;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.scene.SelectionManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 06 - Tests for the Tool System.
 *
 * Tests:
 *   - Tool interface and lifecycle (activate/deactivate)
 *   - ToolManager: register, switch, dispatch, update
 *   - Action-to-Tool flow: MoveAction -> MoveTool, RotateAction -> RotateTool, etc.
 *   - ToolContext: Scene, SelectionManager, RenderMode access
 *   - DrawTool: creates geometry objects
 *   - MeasureTool: computes distance and angle
 *   - CutTool: stub, does not throw
 *   - DeleteTool: removes objects
 *   - Mode independence: same Action produces same Tool behaviour
 */
public class ToolTest {

    private Scene scene;
    private SelectionManager selectionManager;
    private ToolContext toolContext;
    private ToolManager toolManager;

    @Before
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
    // Tool interface tests
    // ------------------------------------------------------------------

    @Test
    public void testToolGetName() {
        com.geometry.tools.move.MoveTool moveTool = new com.geometry.tools.move.MoveTool(toolContext);
        assertEquals("move", moveTool.getName());
    }

    @Test
    public void testToolLifecycle() {
        com.geometry.tools.rotate.RotateTool rotateTool = new com.geometry.tools.rotate.RotateTool(toolContext);
        rotateTool.activate();
        rotateTool.deactivate();
        // No exception -- lifecycle completes
    }

    // ------------------------------------------------------------------
    // ToolManager tests
    // ------------------------------------------------------------------

    @Test
    public void testToolManagerRegisterAndSwitch() {
        assertNull(toolManager.getCurrentTool());
        toolManager.switchTool("move");
        assertNotNull(toolManager.getCurrentTool());
        assertEquals("move", toolManager.getCurrentToolName());

        toolManager.switchTool("rotate");
        assertEquals("rotate", toolManager.getCurrentToolName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolManagerSwitchNullTool() {
        toolManager.switchTool(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolManagerSwitchUnknownTool() {
        toolManager.switchTool("nonexistent");
    }

    @Test
    public void testToolManagerDispatchToActiveTool() {
        toolManager.switchTool("move");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube", cube);
        selectionManager.select(so);

        MoveAction move = new MoveAction(so, 3f, 4f);
        toolManager.dispatchAction(move);

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(3f, t.getPosition().x, 0.001f);
        assertEquals(4f, t.getPosition().y, 0.001f);
    }

    @Test
    public void testToolManagerDispatchWhenNoTool() {
        // No tool active -- dispatch should not throw even with a valid action
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_no_tool", cube);
        selectionManager.select(so);
        MoveAction move = new MoveAction(so, 1f, 1f);
        // Should not throw even with no tool active
        toolManager.dispatchAction(move);
    }

    @Test
    public void testToolManagerGetToolNames() {
        java.util.List<String> names = toolManager.getToolNames();
        assertTrue(names.contains("move"));
        assertTrue(names.contains("rotate"));
        assertTrue(names.contains("scale"));
        assertTrue(names.contains("draw"));
        assertTrue(names.contains("measure"));
        assertTrue(names.contains("cut"));
        assertTrue(names.contains("delete"));
        assertEquals(7, names.size());
    }

    @Test
    public void testToolManagerUpdate() {
        toolManager.switchTool("move");
        // update() should not throw
        toolManager.update();
    }

    @Test
    public void testToolManagerReplaceTool() {
        int initialSize = toolManager.getToolNames().size();

        // Replace with a new instance
        com.geometry.tools.move.MoveTool newMove = new com.geometry.tools.move.MoveTool(toolContext);
        toolManager.registerTool("move", newMove);
        assertEquals(initialSize, toolManager.getToolNames().size());
        // If it was the current tool, it should be re-activated
        toolManager.switchTool("move");
        assertNotNull(toolManager.getCurrentTool());
    }

    // ------------------------------------------------------------------
    // MoveTool tests
    // ------------------------------------------------------------------

    @Test
    public void testMoveToolHandlesMoveAction() {
        toolManager.switchTool("move");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_move", cube);
        selectionManager.select(so);

        MoveAction move = new MoveAction(so, 5f, 0f);
        toolManager.dispatchAction(move);

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(5f, t.getPosition().x, 0.001f);
        assertEquals(0f, t.getPosition().y, 0.001f);
    }

    @Test
    public void testMoveToolIgnoresWrongAction() {
        toolManager.switchTool("move");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_ignore", cube);
        selectionManager.select(so);

        // Send a RotateAction to MoveTool -- should be ignored
        RotateAction rotate = new RotateAction(so, 90f);
        toolManager.dispatchAction(rotate);

        // Position should not change
        assertNull(so.getOverrideTransform());
    }

    @Test
    public void testMoveToolInactiveDoesNothing() {
        com.geometry.tools.move.MoveTool moveTool = new com.geometry.tools.move.MoveTool(toolContext);
        // Not activated

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_inactive", cube);
        selectionManager.select(so);

        MoveAction move = new MoveAction(so, 5f, 0f);
        moveTool.handle(move);

        // Should not have moved
        assertNull(so.getOverrideTransform());
    }

    // ------------------------------------------------------------------
    // RotateTool tests
    // ------------------------------------------------------------------

    @Test
    public void testRotateToolHandlesRotateAction() {
        toolManager.switchTool("rotate");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_rotate", cube);
        selectionManager.select(so);

        RotateAction rotate = new RotateAction(so, 90f);
        toolManager.dispatchAction(rotate);

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(90f, t.getRotation().z, 0.001f);
    }

    @Test
    public void testRotateToolIgnoresWrongAction() {
        toolManager.switchTool("rotate");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_rotate_ignore", cube);
        selectionManager.select(so);

        MoveAction move = new MoveAction(so, 1f, 1f);
        toolManager.dispatchAction(move);

        assertNull(so.getOverrideTransform());
    }

    // ------------------------------------------------------------------
    // ScaleTool tests
    // ------------------------------------------------------------------

    @Test
    public void testScaleToolHandlesScaleAction() {
        toolManager.switchTool("scale");

        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        SceneObject so = scene.addObject("cyl_scale", cylinder);
        selectionManager.select(so);

        ScaleAction scale = new ScaleAction(so, 2.0f);
        toolManager.dispatchAction(scale);

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(2f, t.getScale().x, 0.001f);
        assertEquals(2f, t.getScale().y, 0.001f);
        assertEquals(2f, t.getScale().z, 0.001f);
    }

    @Test
    public void testScaleToolIgnoresWrongAction() {
        toolManager.switchTool("scale");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_scale_ignore", cube);
        selectionManager.select(so);

        RotateAction rotate = new RotateAction(so, 45f);
        toolManager.dispatchAction(rotate);

        assertNull(so.getOverrideTransform());
    }

    // ------------------------------------------------------------------
    // DrawTool tests
    // ------------------------------------------------------------------

    @Test
    public void testDrawToolCreatesRectangle() {
        toolManager.switchTool("draw");

        int initialCount = scene.getObjectCount();

        DrawAction draw = new DrawAction(
                DrawAction.DrawType.RECTANGLE,
                new Vec2(300f, 250f),
                new Vec2(500f, 350f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(draw);

        assertEquals(initialCount + 1, scene.getObjectCount());
    }

    @Test
    public void testDrawToolCreatesCircle() {
        toolManager.switchTool("draw");

        int initialCount = scene.getObjectCount();

        DrawAction draw = new DrawAction(
                DrawAction.DrawType.CIRCLE,
                new Vec2(400f, 300f),
                new Vec2(450f, 300f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(draw);

        assertEquals(initialCount + 1, scene.getObjectCount());
    }

    @Test
    public void testDrawToolCreatesPoint() {
        toolManager.switchTool("draw");

        int initialCount = scene.getObjectCount();

        DrawAction draw = new DrawAction(
                DrawAction.DrawType.POINT,
                new Vec2(400f, 300f),
                new Vec2(400f, 300f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(draw);

        assertEquals(initialCount + 1, scene.getObjectCount());
    }

    @Test
    public void testDrawToolCreatesLine() {
        toolManager.switchTool("draw");

        int initialCount = scene.getObjectCount();

        DrawAction draw = new DrawAction(
                DrawAction.DrawType.LINE,
                new Vec2(300f, 300f),
                new Vec2(500f, 300f),
                java.util.Collections.emptyList()
        );
        toolManager.dispatchAction(draw);

        assertEquals(initialCount + 1, scene.getObjectCount());
    }

    @Test
    public void testDrawToolInactiveDoesNothing() {
        com.geometry.tools.draw.DrawTool drawTool = new com.geometry.tools.draw.DrawTool(toolContext);
        // Not activated

        int initialCount = scene.getObjectCount();

        DrawAction draw = new DrawAction(
                DrawAction.DrawType.RECTANGLE,
                new Vec2(300f, 250f),
                new Vec2(500f, 350f),
                java.util.Collections.emptyList()
        );
        drawTool.handle(draw);

        assertEquals(initialCount, scene.getObjectCount());
    }

    // ------------------------------------------------------------------
    // MeasureTool tests
    // ------------------------------------------------------------------

    @Test
    public void testMeasureToolDistance() {
        toolManager.switchTool("measure");

        com.geometry.tools.measure.MeasureTool measureTool =
                (com.geometry.tools.measure.MeasureTool) toolManager.getCurrentTool();

        MeasureAction measure = new MeasureAction(
                new com.geometry.core.math.Vec2(0f, 0f),
                new com.geometry.core.math.Vec2(3f, 4f)
        );
        measureTool.handle(measure);

        assertEquals(5f, measureTool.getLastResult(), 0.001f);
    }

    @Test
    public void testMeasureToolAngle() {
        toolManager.switchTool("measure");

        com.geometry.tools.measure.MeasureTool measureTool =
                (com.geometry.tools.measure.MeasureTool) toolManager.getCurrentTool();

        // Measure angle: p1=(0,0), p2=(1,0), p3=(1,1) — angle at p2 is 90°
        MeasureAction measure = new MeasureAction(
                new com.geometry.core.math.Vec2(0f, 0f),
                new com.geometry.core.math.Vec2(1f, 0f),
                new com.geometry.core.math.Vec2(1f, 1f)
        );
        measureTool.handle(measure);

        assertEquals(90f, measureTool.getLastResult(), 0.5f);
    }

    @Test
    public void testMeasureToolInactiveDoesNothing() {
        com.geometry.tools.measure.MeasureTool measureTool = new com.geometry.tools.measure.MeasureTool(toolContext);
        // Not activated

        MeasureAction measure = new MeasureAction(
                new com.geometry.core.math.Vec2(0f, 0f),
                new com.geometry.core.math.Vec2(3f, 4f)
        );
        measureTool.handle(measure);

        assertEquals(0f, measureTool.getLastResult(), 0.001f);
    }

    // ------------------------------------------------------------------
    // CutTool tests (stub)
    // ------------------------------------------------------------------

    @Test
    public void testCutToolDoesNotThrow() {
        toolManager.switchTool("cut");

        com.geometry.tools.cut.CutTool cutTool =
                (com.geometry.tools.cut.CutTool) toolManager.getCurrentTool();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube_cut", cube);
        selectionManager.select(so);

        // Should not throw -- stub
        cutTool.executeCut(so, new com.geometry.core.math.Vec3(0f, 0f, 1f), 0f);
    }

    @Test
    public void testCutToolIgnoreActions() {
        toolManager.switchTool("cut");

        // CutTool should ignore non-cut actions (MoveAction)
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_cut_ignore", cube);
        selectionManager.select(so);
        MoveAction move = new MoveAction(so, 1f, 1f);
        toolManager.dispatchAction(move);
        // No exception — MoveTool should not affect cut tool state
    }

    // ------------------------------------------------------------------
    // DeleteTool tests
    // ------------------------------------------------------------------

    @Test
    public void testDeleteToolRemovesObject() {
        toolManager.switchTool("delete");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_delete", cube);
        selectionManager.select(so);
        assertEquals(1, scene.getObjectCount());

        DeleteAction delete = new DeleteAction(scene, so);
        toolManager.dispatchAction(delete);

        assertEquals(0, scene.getObjectCount());
    }

    @Test
    public void testDeleteToolNullTargetDoesNothing() {
        toolManager.switchTool("delete");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_delete2", cube);
        selectionManager.select(so);
        assertEquals(1, scene.getObjectCount());

        // Use DeleteAction with null target
        DeleteAction deleteNull = new DeleteAction(scene, null);
        toolManager.dispatchAction(deleteNull);

        // Null target does nothing
        assertEquals(1, scene.getObjectCount());
    }

    // ------------------------------------------------------------------
    // ToolContext tests
    // ------------------------------------------------------------------

    @Test
    public void testToolContextSceneAccess() {
        assertEquals(scene, toolContext.getScene());
    }

    @Test
    public void testToolContextSelectionAccess() {
        assertNotNull(toolContext.getSelectionManager());
    }

    @Test
    public void testToolContextRenderMode() {
        assertTrue(toolContext.is2DMode());
        assertFalse(toolContext.is3DMode());

        toolContext.setRenderMode(RenderMode.MODE_3D);
        assertTrue(toolContext.is3DMode());
        assertFalse(toolContext.is2DMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolContextNullScene() {
        new ToolContext(null, selectionManager, null, RenderMode.MODE_2D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolContextNullSelectionManager() {
        new ToolContext(scene, null, null, RenderMode.MODE_2D);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolContextNullRenderMode() {
        toolContext.setRenderMode(null);
    }

    @Test
    public void testToolContextGetSelectedObject() {
        assertNull(toolContext.getSelectedObject());

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_ctx", cube);
        selectionManager.select(so);

        assertNotNull(toolContext.getSelectedObject());
        assertEquals(so, toolContext.getSelectedObject());
    }

    // ------------------------------------------------------------------
    // Mode independence tests
    // ------------------------------------------------------------------

    @Test
    public void testSameActionDifferentTools() {
        // MoveAction should work with MoveTool regardless of what other tools exist
        toolManager.switchTool("move");

        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube_move_action", cube);
        selectionManager.select(so);

        MoveAction move = new MoveAction(so, 2f, 3f);
        toolManager.dispatchAction(move);

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(2f, t.getPosition().x, 0.001f);
        assertEquals(3f, t.getPosition().y, 0.001f);
    }

    @Test
    public void testToolDoesNotDependOnInputDevice() {
        // Both mouse and touch produce the same MoveAction
        // The tool should not care which input device was used

        toolManager.switchTool("move");

        // Simulate mouse drag
        Cube mouseCube = new Cube(1f, 1f, 1f);
        SceneObject mouseSo = scene.addObject("mouse_cube_tool", mouseCube);
        selectionManager.select(mouseSo);
        toolManager.dispatchAction(new MoveAction(mouseSo, 5f, 0f));

        // Simulate touch drag (same action)
        Cube touchCube = new Cube(1f, 1f, 1f);
        SceneObject touchSo = scene.addObject("touch_cube_tool", touchCube);
        selectionManager.select(touchSo);
        toolManager.dispatchAction(new MoveAction(touchSo, 5f, 0f));

        // Both should have moved the same amount
        com.geometry.core.transform.Transform mouseT = mouseSo.getOverrideTransform();
        com.geometry.core.transform.Transform touchT = touchSo.getOverrideTransform();
        assertNotNull(mouseT);
        assertNotNull(touchT);
        assertEquals(mouseT.getPosition().x, touchT.getPosition().x, 0.001f);
    }

    // ------------------------------------------------------------------
    // Tool name uniqueness
    // ------------------------------------------------------------------

    @Test
    public void testAllToolNamesAreUnique() {
        java.util.List<String> names = toolManager.getToolNames();
        assertEquals(7, names.size());
        // All names should be unique
        long distinct = names.stream().distinct().count();
        assertEquals(names.size(), distinct);
    }
}
