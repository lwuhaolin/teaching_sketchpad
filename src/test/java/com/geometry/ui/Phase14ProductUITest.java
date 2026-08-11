package com.geometry.ui;

import com.geometry.animation.AnimationManager;
import com.geometry.core.geometry.Cube;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.teaching.TeachingManager;
import com.geometry.tools.ToolManager;
import com.geometry.ui.bridge.ToolBootstrapper;
import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.interaction.TeachingInteractionController;
import com.geometry.ui.resource.UiStrings;
import com.geometry.ui.workspace.TeachingWorkspace;
import org.junit.Test;

import java.awt.event.MouseEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Phase 14 tests for the product UI behaviour that does not require a visible window. */
public class Phase14ProductUITest {

    @Test
    public void chineseResourcesAreAvailable() {
        assertEquals("几何实验室", UiStrings.text("app.title"));
        assertEquals("教师模式", UiStrings.text("role.teacher"));
        assertEquals("展开", UiStrings.text("tool.unfold"));
    }

    @Test
    public void productWorkspaceRegistersAndSwitchesTeachingTools() {
        Scene scene = new Scene();
        ToolManager tools = new ToolManager();
        ToolBootstrapper.registerMissingTools(tools, scene);

        assertTrue(tools.getToolNames().contains("move"));
        assertTrue(tools.getToolNames().contains("rotate"));
        assertTrue(tools.getToolNames().contains("scale"));
        assertTrue(tools.getToolNames().contains("cut"));
        assertEquals("select", tools.getCurrentToolName());

        tools.switchTool("move");
        assertEquals("move", tools.getCurrentToolName());
    }

    @Test
    public void unfoldCommandCreatesAnimationWithoutChangingTheMesh() {
        Scene scene = new Scene();
        Cube cube = new Cube(2f, 2f, 2f);
        int originalVertices = cube.getMesh().getVertexCount();
        scene.addObject("cube", cube);
        AnimationManager animations = new AnimationManager();
        TeachingInteractionController controller = new TeachingInteractionController(
                scene, new TeachingManager(scene, null), animations);
        UIEventBridge bridge = new UIEventBridge(new ToolManager(), scene,
                new InteractionManager(scene), controller);

        bridge.submit(UIEvent.animationControl("unfold"));
        bridge.dispatchAll();

        assertEquals(1, animations.getAnimationCount());
        assertTrue(animations.isAnyRunning());
        assertEquals(originalVertices, cube.getMesh().getVertexCount());
    }

    @Test
    public void teachingRoleAndInputModeChangeTheProductWorkspaceState() {
        Scene scene = new Scene();
        ToolManager tools = new ToolManager();
        TeachingWorkspace workspace = new TeachingWorkspace(scene, tools,
                new InteractionManager(scene), new TeachingManager(scene, null),
                new AnimationManager());

        workspace.setTeachingMode(UITeachingMode.STUDENT);
        workspace.setInputMode(InputMode.TABLET);

        assertEquals(UITeachingMode.STUDENT, workspace.getTeachingMode());
        assertEquals(InputMode.TABLET, workspace.getInputModeManager().getMode());
        assertFalse(workspace.getFloatingToolBar().isVisibleNow() &&
                workspace.getInputModeManager().isDesktop());
        workspace.dispose();
    }

    @Test
    public void canvasSelectionAndMoveGestureChangeTheSelectedObject() {
        Scene scene = new Scene();
        scene.addObject("cube", new Cube(2f, 2f, 2f));
        ToolManager tools = new ToolManager();
        TeachingWorkspace workspace = new TeachingWorkspace(scene, tools,
                new InteractionManager(scene), new TeachingManager(scene, null),
                new AnimationManager());
        workspace.setViewMode(ViewMode.MODE_3D);
        workspace.getCanvasView().setSize(800, 600);
        workspace.switchTool("move");

        workspace.getCanvasView().dispatchEvent(new MouseEvent(workspace.getCanvasView(),
                MouseEvent.MOUSE_PRESSED, 0L, 0, 400, 300, 1, false));
        workspace.getCanvasView().dispatchEvent(new MouseEvent(workspace.getCanvasView(),
                MouseEvent.MOUSE_DRAGGED, 1L, 0, 492, 300, 0, false));

        assertEquals(0, workspace.getCanvasView().getSelectedIndex());
        assertEquals("move", tools.getCurrentToolName());
        assertTrue(scene.getSelected() != null);
        assertTrue(scene.getSelected().getEffectiveTransform().getPosition().x > 0.9f);
        workspace.dispose();
    }
}
