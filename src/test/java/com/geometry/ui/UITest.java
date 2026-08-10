package com.geometry.ui;

import com.geometry.animation.AnimationManager;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Circle;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Polygon;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.geometry.Sphere;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.interaction.InteractionManager;
import com.geometry.interaction.InteractionMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
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
import com.geometry.ui.canvas.OverlayRenderer;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.ui.toolbar.QuickToolBar;
import com.geometry.ui.toolbar.ToolBar;
import com.geometry.ui.touch.TouchLayout;
import com.geometry.ui.touch.TouchUIManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 11 - Tests for the UI system.
 *
 * Tests:
 *   - UI mode enums (ViewMode, UIInteractionMode, UITeachingMode)
 *   - UIEvent creation and data access
 *   - UIEventBridge event dispatch
 *   - ToolBar tool switching
 *   - QuickToolBar whiteboard layout
 *   - SceneTreePanel display and selection
 *   - PropertyPanel property display
 *   - TeachingPanel control events
 *   - AnimationPanel control events
 *   - LayoutManager desktop/whiteboard layouts
 *   - Workspace integration
 *   - ApplicationWindow creation
 *   - CanvasInteractionLayer touch/pen events
 *   - OverlayRenderer overlay control
 *   - TouchUIManager mode switching
 */
public class UITest {

    private Scene scene;
    private ToolManager toolManager;
    private InteractionManager interactionManager;
    private UIEventBridge bridge;
    private Workspace workspace;
    private ToolBar toolbar;
    private QuickToolBar quickToolBar;

    @Before
    public void setUp() {
        scene = new Scene();
        toolManager = new ToolManager();
        interactionManager = new InteractionManager(scene);

        // Register tools with ToolContext
        ToolContext toolContext = new ToolContext(scene, new com.geometry.scene.SelectionManager(), null, null);
        toolManager.registerTool("select", new SelectTool(toolContext));
        toolManager.registerTool("move", new MoveTool(toolContext));
        toolManager.registerTool("rotate", new RotateTool(toolContext));
        toolManager.registerTool("scale", new ScaleTool(toolContext));
        toolManager.registerTool("draw", new DrawTool(toolContext));
        toolManager.registerTool("measure", new MeasureTool(toolContext));
        toolManager.registerTool("cut", new CutTool(toolContext));
        toolManager.registerTool("delete", new DeleteTool(toolContext));

        bridge = new UIEventBridge(toolManager, scene, interactionManager);

        LayoutManager layoutManager = new LayoutManager(
                UIInteractionMode.DESKTOP, 1024, 768);
        SceneTreePanel sceneTreePanel = new SceneTreePanel(scene, bridge);
        PropertyPanel propertyPanel = new PropertyPanel();
        TeachingPanel teachingPanel = new TeachingPanel(null, bridge);
        AnimationPanel animationPanel = new AnimationPanel(null, bridge);

        workspace = new Workspace(
                layoutManager, bridge,
                sceneTreePanel, propertyPanel,
                teachingPanel, animationPanel);

        toolbar = workspace.getToolBar();
        quickToolBar = workspace.getQuickToolBar();
    }

    // ------------------------------------------------------------------
    // Mode enums
    // ------------------------------------------------------------------

    @Test
    public void testViewModeValues() {
        assertEquals(2, ViewMode.values().length);
        assertNotNull(ViewMode.MODE_2D);
        assertNotNull(ViewMode.MODE_3D);
    }

    @Test
    public void testUIInteractionModeValues() {
        assertEquals(2, UIInteractionMode.values().length);
        assertNotNull(UIInteractionMode.DESKTOP);
        assertNotNull(UIInteractionMode.WHITEBOARD);
    }

    @Test
    public void testUITeachingModeValues() {
        assertEquals(4, UITeachingMode.values().length);
        assertNotNull(UITeachingMode.TEACHER);
        assertNotNull(UITeachingMode.STUDENT);
        assertNotNull(UITeachingMode.EXAM);
        assertNotNull(UITeachingMode.FREE);
    }

    // ------------------------------------------------------------------
    // UIEvent
    // ------------------------------------------------------------------

    @Test
    public void testUIEventToolSwitch() {
        UIEvent event = UIEvent.toolSwitch("move");
        assertEquals(UIEvent.EventType.TOOL_SWITCH, event.getType());
        assertEquals("move", event.getStringData());
        assertNull(event.getViewMode());
    }

    @Test
    public void testUIEventSelectObject() {
        UIEvent event = UIEvent.selectObject("cube_001");
        assertEquals(UIEvent.EventType.SELECT_OBJECT, event.getType());
        assertEquals("cube_001", event.getStringData());
    }

    @Test
    public void testUIEventViewModeChange() {
        UIEvent event = UIEvent.viewModeChange(ViewMode.MODE_3D);
        assertEquals(UIEvent.EventType.VIEW_MODE_CHANGE, event.getType());
        assertEquals(ViewMode.MODE_3D, event.getViewMode());
    }

    @Test
    public void testUIEventInteractionModeChange() {
        UIEvent event = UIEvent.interactionModeChange(UIInteractionMode.WHITEBOARD);
        assertEquals(UIEvent.EventType.INTERACTION_MODE_CHANGE, event.getType());
        assertEquals(UIInteractionMode.WHITEBOARD, event.getUIInteractionMode());
    }

    @Test
    public void testUIEventTeachingControl() {
        UIEvent event = UIEvent.teachingControl("next");
        assertEquals(UIEvent.EventType.TEACHING_CONTROL, event.getType());
        assertEquals("next", event.getStringData());
    }

    @Test
    public void testUIEventAnimationControl() {
        UIEvent event = UIEvent.animationControl("play");
        assertEquals(UIEvent.EventType.ANIMATION_CONTROL, event.getType());
        assertEquals("play", event.getStringData());
    }

    @Test
    public void testUIEventToggleVisibility() {
        UIEvent event = UIEvent.toggleVisibility("cube_001");
        assertEquals(UIEvent.EventType.TOGGLE_VISIBILITY, event.getType());
        assertEquals("cube_001", event.getStringData());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUIEventNullType() {
        new UIEvent(null, "data");
    }

    // ------------------------------------------------------------------
    // UIEventBridge
    // ------------------------------------------------------------------

    @Test
    public void testBridgeSubmitAndDispatchToolSwitch() {
        bridge.submit(UIEvent.toolSwitch("move"));
        assertEquals(1, bridge.getQueuedEventCount());
        bridge.dispatchAll();
        assertEquals("move", toolManager.getCurrentToolName());
    }

    @Test
    public void testBridgeSubmitAndDispatchSelectObject() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject obj = scene.addObject("cube_001", cube);
        scene.clearSelection();

        bridge.submit(UIEvent.selectObject("cube_001"));
        bridge.dispatchAll();

        assertEquals(obj, scene.getSelected());
    }

    @Test
    public void testBridgeSubmitAndDispatchNonExistentObject() {
        bridge.submit(UIEvent.selectObject("nonexistent"));
        bridge.dispatchAll();
        assertNull(scene.getSelected());
    }

    @Test
    public void testBridgeToggleVisibility() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject obj = scene.addObject("cube_001", cube);
        assertTrue(obj.isVisible());

        bridge.submit(UIEvent.toggleVisibility("cube_001"));
        bridge.dispatchAll();

        assertFalse(obj.isVisible());
    }

    @Test
    public void testBridgeInteractionModeChange() {
        bridge.submit(UIEvent.interactionModeChange(UIInteractionMode.WHITEBOARD));
        bridge.dispatchAll();
        assertEquals(InteractionMode.WHITEBOARD, interactionManager.getMode());
    }

    @Test
    public void testBridgeNullEvent() {
        bridge.clearQueue(); // no-op to verify queue is empty
        assertTrue(bridge.isQueueEmpty());
        bridge.dispatchAll(); // should not throw
    }

    @Test
    public void testBridgeEmptyDispatch() {
        bridge.dispatchAll(); // Should not throw
        assertTrue(bridge.isQueueEmpty());
    }

    @Test
    public void testBridgeClearQueue() {
        bridge.submit(UIEvent.toolSwitch("rotate"));
        bridge.clearQueue();
        assertEquals(0, bridge.getQueuedEventCount());
        // Tool should not have switched
        assertNull(toolManager.getCurrentToolName());
    }

    @Test
    public void testBridgeBatchSubmit() {
        bridge.submit(java.util.Arrays.asList(
                UIEvent.toolSwitch("move"),
                UIEvent.toolSwitch("rotate")
        ));
        assertEquals(2, bridge.getQueuedEventCount());
        bridge.dispatchAll();
        assertEquals("rotate", toolManager.getCurrentToolName());
    }

    // ------------------------------------------------------------------
    // ToolBar
    // ------------------------------------------------------------------

    @Test
    public void testToolBarDefaultTool() {
        assertEquals("select", toolbar.getActiveTool());
    }

    @Test
    public void testToolBarToolNames() {
        assertEquals(7, toolbar.getToolNames().size());
        assertTrue(toolbar.getToolNames().contains("select"));
        assertTrue(toolbar.getToolNames().contains("move"));
        assertTrue(toolbar.getToolNames().contains("rotate"));
        assertTrue(toolbar.getToolNames().contains("scale"));
        assertTrue(toolbar.getToolNames().contains("draw"));
        assertTrue(toolbar.getToolNames().contains("measure"));
        assertTrue(toolbar.getToolNames().contains("cut"));
    }

    @Test
    public void testToolBarSwitchTool() {
        boolean result = toolbar.switchTool("move");
        assertTrue(result);
        assertEquals("move", toolbar.getActiveTool());
    }

    @Test
    public void testToolBarSwitchNonExistentTool() {
        boolean result = toolbar.switchTool("nonexistent");
        assertFalse(result);
        assertEquals("select", toolbar.getActiveTool());
    }

    @Test
    public void testToolBarGetToolAtPosition() {
        assertEquals("select", toolbar.getToolAtPosition(0, 0));
        assertEquals("move", toolbar.getToolAtPosition(48, 0));
        assertEquals("rotate", toolbar.getToolAtPosition(96, 0));
        assertNull(toolbar.getToolAtPosition(1000, 0));
    }

    @Test
    public void testToolBarLayout() {
        assertEquals(7 * ToolBar.TOOL_BUTTON_WIDTH, toolbar.getWidth());
        assertEquals(ToolBar.TOOL_BUTTON_HEIGHT, toolbar.getHeight());
    }

    @Test
    public void testToolBarNullBridge() {
        ToolBar tb = new ToolBar(ToolBar.DEFAULT_TOOL_NAMES, null);
        tb.switchTool("move"); // Should not throw
        assertEquals("move", tb.getActiveTool());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolBarNullToolNames() {
        new ToolBar((java.util.List<String>) null, bridge);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToolBarEmptyToolNames() {
        new ToolBar(new java.util.ArrayList<String>(), bridge);
    }

    // ------------------------------------------------------------------
    // QuickToolBar
    // ------------------------------------------------------------------

    @Test
    public void testQuickToolBarDefaultTool() {
        assertEquals("move", quickToolBar.getActiveTool());
    }

    @Test
    public void testQuickToolBarToolNames() {
        assertEquals(5, quickToolBar.getToolNames().size());
        assertTrue(quickToolBar.getToolNames().contains("move"));
        assertTrue(quickToolBar.getToolNames().contains("rotate"));
        assertTrue(quickToolBar.getToolNames().contains("measure"));
        assertTrue(quickToolBar.getToolNames().contains("play"));
        assertTrue(quickToolBar.getToolNames().contains("next"));
    }

    @Test
    public void testQuickToolBarSwitchTool() {
        boolean result = quickToolBar.switchTool("rotate");
        assertTrue(result);
        assertEquals("rotate", quickToolBar.getActiveTool());
    }

    @Test
    public void testQuickToolBarLayout() {
        assertEquals(5 * QuickToolBar.QUICK_BUTTON_WIDTH, quickToolBar.getWidth());
        assertEquals(QuickToolBar.QUICK_BUTTON_HEIGHT, quickToolBar.getHeight());
        // Buttons should be >= 60px for touch
        assertTrue(QuickToolBar.QUICK_BUTTON_WIDTH >= 60);
        assertTrue(QuickToolBar.QUICK_BUTTON_HEIGHT >= 60);
    }

    @Test
    public void testQuickToolBarGetToolAtPosition() {
        assertEquals("move", quickToolBar.getToolAtPosition(0, 0));
        assertEquals("rotate", quickToolBar.getToolAtPosition(72, 0));
        assertNull(quickToolBar.getToolAtPosition(1000, 0));
    }

    // ------------------------------------------------------------------
    // SceneTreePanel
    // ------------------------------------------------------------------

    @Test
    public void testSceneTreeEmpty() {
        SceneTreePanel panel = new SceneTreePanel(scene, bridge);
        assertEquals(0, panel.getObjectCount());
        assertEquals(-1, panel.getRowAtY(0));
        assertNull(panel.getLabelText(0));
    }

    @Test
    public void testSceneTreeWithObjects() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube_001", cube);
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        scene.addObject("cyl_001", cylinder);

        SceneTreePanel panel = new SceneTreePanel(scene, bridge);
        assertEquals(2, panel.getObjectCount());
        assertNotNull(panel.getLabelText(0));
        assertNotNull(panel.getLabelText(1));
        assertTrue(panel.getLabelText(0).contains("cube_001"));
        assertTrue(panel.getLabelText(1).contains("cyl_001"));
    }

    @Test
    public void testSceneTreeSelectByIndex() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject obj = scene.addObject("cube_001", cube);

        SceneTreePanel panel = new SceneTreePanel(scene, bridge);
        panel.selectByIndex(0);

        assertEquals(obj.getId(), panel.getSelectedObjectId());
        assertEquals(obj, scene.getSelected());
    }

    @Test
    public void testSceneTreeGetRowAtY() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube_001", cube);
        scene.addObject("cube_002", new Cube(2f, 2f, 2f));

        SceneTreePanel panel = new SceneTreePanel(scene, bridge);
        assertEquals(0, panel.getRowAtY(0));
        assertEquals(0, panel.getRowAtY(23));
        assertEquals(1, panel.getRowAtY(24));
        assertEquals(1, panel.getRowAtY(47));
        assertEquals(-1, panel.getRowAtY(1000));
    }

    @Test
    public void testSceneTreeLayout() {
        SceneTreePanel panel = new SceneTreePanel(scene, bridge);
        assertEquals(220, panel.getPreferredWidth());
        assertEquals(300, panel.getPreferredHeight());
        assertEquals(24, panel.getRowHeight());
    }

    @Test
    public void testSceneTreeNullScene() {
        SceneTreePanel panel = new SceneTreePanel(null, bridge);
        assertEquals(0, panel.getObjectCount());
        assertEquals(-1, panel.getRowAtY(0));
    }

    // ------------------------------------------------------------------
    // PropertyPanel
    // ------------------------------------------------------------------

    @Test
    public void testPropertyPanelNoSelection() {
        PropertyPanel panel = new PropertyPanel();
        assertEquals(1, panel.getPropertyRowCount());
        assertEquals("No object selected", panel.getPropertyLabel(0));
    }

    @Test
    public void testPropertyPanelCube() {
        PropertyPanel panel = new PropertyPanel();
        Cube cube = new Cube(2f, 3f, 4f);
        SceneObject obj = scene.addObject("cube_001", cube);
        panel.selectObject(obj);

        assertEquals(6, panel.getPropertyRowCount());
        assertTrue(panel.getPropertyLabel(0).contains("Cube"));
        assertTrue(panel.getPropertyLabel(1).contains("2.0")); // width
        assertTrue(panel.getPropertyLabel(2).contains("3.0")); // height
        assertTrue(panel.getPropertyLabel(3).contains("4.0")); // depth
        assertTrue(panel.getPropertyLabel(4).contains("Position"));
        assertTrue(panel.getPropertyLabel(5).contains("Visible"));
    }

    @Test
    public void testPropertyPanelCylinder() {
        PropertyPanel panel = new PropertyPanel();
        Cylinder cylinder = new Cylinder(1.5f, 3f, 16);
        SceneObject obj = scene.addObject("cyl_001", cylinder);
        panel.selectObject(obj);

        assertTrue(panel.getPropertyLabel(0).contains("Cylinder"));
        assertTrue(panel.getPropertyLabel(1).contains("1.5")); // radius
        assertTrue(panel.getPropertyLabel(2).contains("3.0")); // height
        assertTrue(panel.getPropertyLabel(3).contains("16")); // segments
    }

    @Test
    public void testPropertyPanelSphere() {
        PropertyPanel panel = new PropertyPanel();
        Sphere sphere = new Sphere(2f, 16, 8);
        SceneObject obj = scene.addObject("sphere_001", sphere);
        panel.selectObject(obj);

        assertTrue(panel.getPropertyLabel(0).contains("Sphere"));
        assertTrue(panel.getPropertyLabel(1).contains("2.0")); // radius
        assertTrue(panel.getPropertyLabel(2).contains("16")); // segments
    }

    @Test
    public void testPropertyPanelRectangle() {
        PropertyPanel panel = new PropertyPanel();
        Rectangle rect = new Rectangle(5f, 3f);
        SceneObject obj = scene.addObject("rect_001", rect);
        panel.selectObject(obj);

        assertTrue(panel.getPropertyLabel(0).contains("Rectangle"));
        assertTrue(panel.getPropertyLabel(1).contains("5.0")); // width
        assertTrue(panel.getPropertyLabel(2).contains("3.0")); // height
    }

    @Test
    public void testPropertyPanelCircle() {
        PropertyPanel panel = new PropertyPanel();
        Circle circle = new Circle(2f, 16);
        SceneObject obj = scene.addObject("circle_001", circle);
        panel.selectObject(obj);

        assertTrue(panel.getPropertyLabel(0).contains("Circle"));
        assertTrue(panel.getPropertyLabel(1).contains("2.0")); // radius
        assertTrue(panel.getPropertyLabel(2).contains("16")); // segments
    }

    @Test
    public void testPropertyPanelPolygon() {
        PropertyPanel panel = new PropertyPanel();
        Polygon polygon = new Polygon(
                new Vec3(0, 0, 0),
                new Vec3(1, 0, 0),
                new Vec3(1, 1, 0),
                new Vec3(0, 1, 0)
        );
        SceneObject obj = scene.addObject("poly_001", polygon);
        panel.selectObject(obj);

        assertTrue(panel.getPropertyLabel(0).contains("Polygon"));
        assertTrue(panel.getPropertyLabel(1).contains("4")); // vertices count
    }

    @Test
    public void testPropertyPanelLayout() {
        PropertyPanel panel = new PropertyPanel();
        assertEquals(240, panel.getPreferredWidth());
        assertEquals(300, panel.getPreferredHeight());
        assertEquals(28, panel.getRowHeight());
    }

    @Test
    public void testPropertyPanelOutOfBounds() {
        PropertyPanel panel = new PropertyPanel();
        assertNull(panel.getPropertyLabel(-1));
        assertNull(panel.getPropertyLabel(100));
    }

    // ------------------------------------------------------------------
    // TeachingPanel
    // ------------------------------------------------------------------

    @Test
    public void testTeachingPanelNoManager() {
        TeachingPanel panel = new TeachingPanel(null, bridge);
        assertEquals("No lesson", panel.getLessonName());
        assertEquals("Step 0 / 0", panel.getStepDisplay());
        assertFalse(panel.isLessonActive());
    }

    @Test
    public void testTeachingPanelControls() {
        TeachingPanel panel = new TeachingPanel(null, bridge);
        assertEquals(3, panel.getControlCount());
        assertEquals("< Prev", panel.getControlLabel(0));
        assertEquals("Start", panel.getControlLabel(1));
        assertEquals("Next >", panel.getControlLabel(2));
        assertNull(panel.getControlLabel(3));
    }

    @Test
    public void testTeachingPanelTriggerControls() {
        TeachingPanel panel = new TeachingPanel(null, bridge);
        panel.triggerControl(0); // previous
        panel.triggerControl(1); // start
        assertTrue(panel.isRunning());
        panel.triggerControl(1); // stop
        assertFalse(panel.isRunning());
        panel.triggerControl(2); // next
    }

    @Test
    public void testTeachingPanelLayout() {
        TeachingPanel panel = new TeachingPanel(null, bridge);
        assertEquals(240, panel.getPreferredWidth());
        assertEquals(200, panel.getPreferredHeight());
        assertEquals(36, panel.getRowHeight());
    }

    // ------------------------------------------------------------------
    // AnimationPanel
    // ------------------------------------------------------------------

    @Test
    public void testAnimationPanelNoManager() {
        AnimationPanel panel = new AnimationPanel(null, bridge);
        assertEquals("0.0s / 0.0s", panel.getTimeDisplay());
        assertFalse(panel.hasAnimation());
        assertFalse(panel.isPlaying());
    }

    @Test
    public void testAnimationPanelControls() {
        AnimationPanel panel = new AnimationPanel(null, bridge);
        assertEquals(2, panel.getControlCount());
        assertEquals("Play", panel.getControlLabel(0));
        assertEquals("Stop", panel.getControlLabel(1));
    }

    @Test
    public void testAnimationPanelTriggerControls() {
        AnimationPanel panel = new AnimationPanel(null, bridge);
        panel.triggerControl(0); // play
        assertTrue(panel.isPlaying());
        panel.triggerControl(0); // pause
        assertFalse(panel.isPlaying());
        panel.triggerControl(1); // stop
        assertFalse(panel.isPlaying());
    }

    @Test
    public void testAnimationPanelLayout() {
        AnimationPanel panel = new AnimationPanel(null, bridge);
        assertEquals(240, panel.getPreferredWidth());
        assertEquals(150, panel.getPreferredHeight());
        assertEquals(36, panel.getRowHeight());
    }

    // ------------------------------------------------------------------
    // LayoutManager
    // ------------------------------------------------------------------

    @Test
    public void testLayoutManagerDesktop() {
        LayoutManager lm = new LayoutManager(UIInteractionMode.DESKTOP, 1024, 768);
        assertEquals(UIInteractionMode.DESKTOP, lm.getMode());
        assertEquals(1024, lm.getWindowWidth());
        assertEquals(768, lm.getWindowHeight());
        assertEquals(52, lm.getToolbarHeight());
        assertEquals(100, lm.getBottomPanelHeight());
        assertEquals(48, lm.getButtonSize());
    }

    @Test
    public void testLayoutManagerWhiteboard() {
        LayoutManager lm = new LayoutManager(UIInteractionMode.WHITEBOARD, 1024, 768);
        assertEquals(UIInteractionMode.WHITEBOARD, lm.getMode());
        assertEquals(80, lm.getToolbarHeight());
        assertEquals(80, lm.getBottomPanelHeight());
        assertEquals(60, lm.getButtonSize());
    }

    @Test
    public void testLayoutManagerDesktopLayouts() {
        LayoutManager lm = new LayoutManager(UIInteractionMode.DESKTOP, 1024, 768);
        int[] toolbar = lm.getToolbarLayout();
        assertEquals(0, toolbar[0]);
        assertEquals(0, toolbar[1]);
        assertEquals(1024, toolbar[2]);
        assertEquals(52, toolbar[3]);

        int[] canvas = lm.getCanvasLayout();
        assertEquals(220, canvas[0]); // left panel width
        assertEquals(52, canvas[1]); // toolbar height
        assertEquals(564, canvas[2]); // 1024 - 220 - 240
        assertEquals(616, canvas[3]); // 768 - 52 - 100

        int[] sceneTree = lm.getSceneTreeLayout();
        assertNotNull(sceneTree);
        assertEquals(0, sceneTree[0]);
        assertEquals(220, sceneTree[2]);

        int[] propPanel = lm.getPropertyPanelLayout();
        assertNotNull(propPanel);
        assertEquals(240, propPanel[2]);
    }

    @Test
    public void testLayoutManagerWhiteboardLayouts() {
        LayoutManager lm = new LayoutManager(UIInteractionMode.WHITEBOARD, 1024, 768);
        int[] toolbar = lm.getToolbarLayout();
        assertEquals(80, toolbar[3]); // taller toolbar

        int[] canvas = lm.getCanvasLayout();
        assertEquals(0, canvas[0]); // no left panel
        assertEquals(80, canvas[1]); // toolbar height
        assertEquals(764, canvas[2]); // 1024 - 260
        assertEquals(608, canvas[3]); // 768 - 80 - 80

        // Scene tree not shown in whiteboard
        assertNull(lm.getSceneTreeLayout());
        assertNull(lm.getPropertyPanelLayout());
    }

    @Test
    public void testLayoutManagerModeSwitch() {
        LayoutManager lm = new LayoutManager(UIInteractionMode.DESKTOP, 800, 600);
        lm.setMode(UIInteractionMode.WHITEBOARD);
        assertEquals(UIInteractionMode.WHITEBOARD, lm.getMode());
        assertEquals(80, lm.getToolbarHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayoutManagerNullMode() {
        new LayoutManager((UIInteractionMode) null, 800, 600);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayoutManagerInvalidSize() {
        new LayoutManager(UIInteractionMode.DESKTOP, 0, 600);
    }

    // ------------------------------------------------------------------
    // Workspace
    // ------------------------------------------------------------------

    @Test
    public void testWorkspaceComponents() {
        assertEquals(toolbar, workspace.getToolBar());
        assertEquals(quickToolBar, workspace.getQuickToolBar());
        assertNotNull(workspace.getSceneTreePanel());
        assertNotNull(workspace.getPropertyPanel());
        assertNotNull(workspace.getEventBridge());
        assertNotNull(workspace.getLayoutManager());
    }

    @Test
    public void testWorkspaceSize() {
        assertEquals(1024, workspace.getWidth());
        assertEquals(768, workspace.getHeight());
    }

    @Test
    public void testWorkspaceMode() {
        assertEquals(UIInteractionMode.DESKTOP, workspace.getInteractionMode());
        workspace.setInteractionMode(UIInteractionMode.WHITEBOARD);
        assertEquals(UIInteractionMode.WHITEBOARD, workspace.getInteractionMode());
    }

    @Test
    public void testWorkspaceViewMode() {
        assertNull(workspace.getViewMode());
        workspace.setViewMode(ViewMode.MODE_3D);
        assertEquals(ViewMode.MODE_3D, workspace.getViewMode());
    }

    @Test
    public void testWorkspaceToolbarClick() {
        assertTrue(workspace.processToolbarClick(0, 0)); // select button
        assertEquals("select", workspace.getToolBar().getActiveTool());

        assertTrue(workspace.processToolbarClick(48, 0)); // move button
        assertEquals("move", workspace.getToolBar().getActiveTool());
    }

    @Test
    public void testWorkspaceDispatchEvents() {
        workspace.getEventBridge().submit(UIEvent.toolSwitch("rotate"));
        workspace.dispatchEvents();
        assertEquals("rotate", toolManager.getCurrentToolName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWorkspaceNullLayoutManager() {
        new Workspace(null, bridge,
                new SceneTreePanel(scene, bridge),
                new PropertyPanel(),
                new TeachingPanel(null, bridge),
                new AnimationPanel(null, bridge));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWorkspaceNullBridge() {
        new Workspace(new LayoutManager(UIInteractionMode.DESKTOP, 800, 600), null,
                new SceneTreePanel(scene, bridge),
                new PropertyPanel(),
                new TeachingPanel(null, bridge),
                new AnimationPanel(null, bridge));
    }

    // ------------------------------------------------------------------
    // ApplicationWindow
    // ------------------------------------------------------------------

    @Test
    public void testApplicationWindowCreate() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        assertTrue(window.create());
        assertTrue(window.isCreated());
    }

    @Test
    public void testApplicationWindowComponents() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        assertNotNull(window.getWorkspace());
        assertEquals(scene, window.getScene());
        assertEquals(toolManager, window.getToolManager());
        assertEquals(interactionManager, window.getInteractionManager());
    }

    @Test
    public void testApplicationWindowClose() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        window.create();
        assertTrue(window.isCreated());
        window.close();
        assertFalse(window.isCreated());
    }

    // ------------------------------------------------------------------
    // CanvasInteractionLayer
    // ------------------------------------------------------------------

    @Test
    public void testCanvasInteractionTouchDown() {
        CanvasInteractionLayer layer = new CanvasInteractionLayer(scene, interactionManager);
        layer.onTouchDown(100, 200);
        assertEquals(100, layer.getLastTouchPosition().x, 0.01);
        assertEquals(200, layer.getLastTouchPosition().y, 0.01);
    }

    @Test
    public void testCanvasInteractionTouchMove() {
        CanvasInteractionLayer layer = new CanvasInteractionLayer(scene, interactionManager);
        layer.onTouchDown(100, 200);
        layer.onTouchMove(150, 250);
        assertEquals(150, layer.getLastTouchPosition().x, 0.01);
        assertEquals(250, layer.getLastTouchPosition().y, 0.01);
    }

    @Test
    public void testCanvasInteractionPenDown() {
        CanvasInteractionLayer layer = new CanvasInteractionLayer(scene, interactionManager);
        layer.onPenDown(300, 400, 0.8f);
        assertEquals(0.8f, layer.getLastPressure(), 0.01);
        assertEquals(300, layer.getLastTouchPosition().x, 0.01);
    }

    @Test
    public void testCanvasInteractionNullScene() {
        try {
            new CanvasInteractionLayer(null, interactionManager);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    // ------------------------------------------------------------------
    // OverlayRenderer
    // ------------------------------------------------------------------

    @Test
    public void testOverlayRendererHide() {
        OverlayRenderer overlay = new OverlayRenderer();
        assertFalse(overlay.isVisible());
        assertNull(overlay.getCurrentOverlay());
    }

    @Test
    public void testOverlayRendererSelectionBox() {
        OverlayRenderer overlay = new OverlayRenderer();
        overlay.showSelectionBox(10, 20, 100, 80);
        assertTrue(overlay.isVisible());
        assertEquals(OverlayRenderer.OverlayType.SELECTION_BOX, overlay.getCurrentOverlay());
        assertEquals(10, overlay.getOverlayX());
        assertEquals(20, overlay.getOverlayY());
        assertEquals(100, overlay.getOverlayWidth());
        assertEquals(80, overlay.getOverlayHeight());
    }

    @Test
    public void testOverlayRendererGestureHint() {
        OverlayRenderer overlay = new OverlayRenderer();
        overlay.showGestureHint(50, 50, "drag to move");
        assertTrue(overlay.isVisible());
        assertEquals(OverlayRenderer.OverlayType.GESTURE_HINT, overlay.getCurrentOverlay());
    }

    @Test
    public void testOverlayRendererConstants() {
        assertEquals(10, OverlayRenderer.TOUCH_INDICATOR_RADIUS);
        assertEquals(2, OverlayRenderer.SELECTION_BOX_WIDTH);
        assertEquals(16, OverlayRenderer.GESTURE_HINT_FONT_SIZE);
    }

    // ------------------------------------------------------------------
    // TouchLayout
    // ------------------------------------------------------------------

    @Test
    public void testTouchLayoutDesktop() {
        TouchLayout layout = new TouchLayout(UIInteractionMode.DESKTOP);
        assertEquals(44, layout.getMinTouchTargetSize());
        assertEquals(ToolBar.TOOL_BUTTON_WIDTH, layout.getToolBarButtonSize());
        assertEquals(24, layout.getPanelRowHeight());
        assertEquals(8, layout.getCanvasPadding());
        assertFalse(layout.isTouchOptimized());
    }

    @Test
    public void testTouchLayoutWhiteboard() {
        TouchLayout layout = new TouchLayout(UIInteractionMode.WHITEBOARD);
        assertEquals(60, layout.getMinTouchTargetSize());
        assertEquals(QuickToolBar.QUICK_BUTTON_WIDTH, layout.getToolBarButtonSize());
        assertEquals(36, layout.getPanelRowHeight());
        assertEquals(16, layout.getCanvasPadding());
        assertTrue(layout.isTouchOptimized());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTouchLayoutNullMode() {
        new TouchLayout(null);
    }

    // ------------------------------------------------------------------
    // TouchUIManager
    // ------------------------------------------------------------------

    @Test
    public void testTouchUIManagerDefaultMode() {
        TouchUIManager manager = new TouchUIManager(bridge);
        assertEquals(UIInteractionMode.DESKTOP, manager.getMode());
        assertTrue(manager.isDesktop());
        assertFalse(manager.isWhiteboard());
    }

    @Test
    public void testTouchUIManagerSwitchToWhiteboard() {
        TouchUIManager manager = new TouchUIManager(bridge);
        manager.enableWhiteboard();
        assertEquals(UIInteractionMode.WHITEBOARD, manager.getMode());
        assertTrue(manager.isWhiteboard());
        assertFalse(manager.isDesktop());
    }

    @Test
    public void testTouchUIManagerSwitchToDesktop() {
        TouchUIManager manager = new TouchUIManager(bridge);
        manager.enableWhiteboard();
        manager.enableDesktop();
        assertEquals(UIInteractionMode.DESKTOP, manager.getMode());
        assertTrue(manager.isDesktop());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTouchUIManagerNullMode() {
        TouchUIManager manager = new TouchUIManager(bridge);
        manager.setMode(null);
    }

    @Test
    public void testTouchUIManagerLayoutValues() {
        TouchUIManager manager = new TouchUIManager(bridge);
        // Desktop values
        assertEquals(44, manager.getMinTouchTargetSize());
        assertEquals(48, manager.getToolBarButtonSize());
        assertEquals(24, manager.getPanelRowHeight());
        assertEquals(8, manager.getCanvasPadding());

        // Switch to whiteboard
        manager.enableWhiteboard();
        assertEquals(60, manager.getMinTouchTargetSize());
        assertEquals(72, manager.getToolBarButtonSize());
        assertEquals(36, manager.getPanelRowHeight());
        assertEquals(16, manager.getCanvasPadding());
    }

    // ------------------------------------------------------------------
    // ToolbarSize constants
    // ------------------------------------------------------------------

    @Test
    public void testToolbarSizeConstants() {
        assertEquals(48, ToolbarSize.DESKTOP_BUTTON_WIDTH);
        assertEquals(48, ToolbarSize.DESKTOP_BUTTON_HEIGHT);
        assertEquals(72, ToolbarSize.WHITEBOARD_BUTTON_WIDTH);
        assertEquals(72, ToolbarSize.WHITEBOARD_BUTTON_HEIGHT);
        assertEquals(44, ToolbarSize.MIN_TOUCH_TARGET);
        assertEquals(60, ToolbarSize.WHITEBOARD_MIN_TOUCH_TARGET);
        assertEquals(220, ToolbarSize.DESKTOP_PANEL_WIDTH);
        assertEquals(260, ToolbarSize.WHITEBOARD_PANEL_WIDTH);
        assertEquals(52, ToolbarSize.DESKTOP_TOOLBAR_HEIGHT);
        assertEquals(80, ToolbarSize.WHITEBOARD_TOOLBAR_HEIGHT);
    }
}
