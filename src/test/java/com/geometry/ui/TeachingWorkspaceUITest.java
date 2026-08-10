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
import com.geometry.tools.ToolManager;
import com.geometry.ui.component.BottomActionBar;
import com.geometry.ui.component.FloatingToolBar;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.component.LessonStatusBar;
import com.geometry.ui.input.DesktopInputMode;
import com.geometry.ui.input.gesture.GestureRecognizer;
import com.geometry.ui.input.InputHandler;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.input.InputModeManager;
import com.geometry.ui.input.TabletInputMode;
import com.geometry.ui.input.WhiteboardInputMode;
import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.workspace.TeachingWorkspace;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 13 - Tests for the teaching workspace UI and input mode system.
 *
 * Tests:
 *   - InputMode enum
 *   - InputModeManager mode switching and accessors
 *   - DesktopInputMode, WhiteboardInputMode, TabletInputMode
 *   - GestureRecognizer tap/drag/double-tap/long-press
 *   - EducationTheme colours and fonts
 *   - FloatingToolBar creation and button management
 *   - BottomActionBar creation and button management
 *   - GeometryCanvasView 2D rendering and interaction
 *   - LessonStatusBar display
 *   - TeachingWorkspace integration
 *   - ApplicationWindow lifecycle
 */
public class TeachingWorkspaceUITest {

    private Scene scene;
    private ToolManager toolManager;
    private InteractionManager interactionManager;

    @Before
    public void setUp() {
        scene = new Scene();
        toolManager = new ToolManager();
        interactionManager = new InteractionManager(scene);
    }

    // ------------------------------------------------------------------
    // InputMode enum
    // ------------------------------------------------------------------

    @Test
    public void testInputModeValues() {
        assertEquals(3, InputMode.values().length);
        assertNotNull(InputMode.DESKTOP);
        assertNotNull(InputMode.WHITEBOARD);
        assertNotNull(InputMode.TABLET);
    }

    @Test
    public void testInputModeToString() {
        assertEquals("DESKTOP", InputMode.DESKTOP.name());
        assertEquals("WHITEBOARD", InputMode.WHITEBOARD.name());
        assertEquals("TABLET", InputMode.TABLET.name());
    }

    // ------------------------------------------------------------------
    // InputModeManager
    // ------------------------------------------------------------------

    @Test
    public void testInputModeManagerDefaultMode() {
        InputModeManager mgr = new InputModeManager(null, null);
        assertEquals(InputMode.DESKTOP, mgr.getMode());
        assertTrue(mgr.isDesktop());
        assertFalse(mgr.isWhiteboard());
        assertFalse(mgr.isTablet());
    }

    @Test
    public void testInputModeManagerSwitchToWhiteboard() {
        InputModeManager mgr = new InputModeManager(null, null);
        mgr.setMode(InputMode.WHITEBOARD);
        assertEquals(InputMode.WHITEBOARD, mgr.getMode());
        assertFalse(mgr.isDesktop());
        assertTrue(mgr.isWhiteboard());
        assertFalse(mgr.isTablet());
    }

    @Test
    public void testInputModeManagerSwitchToTablet() {
        InputModeManager mgr = new InputModeManager(null, null);
        mgr.setMode(InputMode.TABLET);
        assertEquals(InputMode.TABLET, mgr.getMode());
        assertFalse(mgr.isDesktop());
        assertFalse(mgr.isWhiteboard());
        assertTrue(mgr.isTablet());
    }

    @Test
    public void testInputModeManagerSwitchToDesktop() {
        InputModeManager mgr = new InputModeManager(null, null);
        mgr.setMode(InputMode.DESKTOP);
        assertEquals(InputMode.DESKTOP, mgr.getMode());
        assertTrue(mgr.isDesktop());
    }

    @Test
    public void testInputModeManagerCycle() {
        InputModeManager mgr = new InputModeManager(null, null);
        mgr.cycleMode();
        assertEquals(InputMode.WHITEBOARD, mgr.getMode());
        mgr.cycleMode();
        assertEquals(InputMode.TABLET, mgr.getMode());
        mgr.cycleMode();
        assertEquals(InputMode.DESKTOP, mgr.getMode());
    }

    @Test
    public void testInputModeManagerTouchTolerance() {
        InputModeManager mgr = new InputModeManager(null, null);
        assertEquals(5, mgr.getTouchTolerance()); // desktop

        mgr.setMode(InputMode.WHITEBOARD);
        assertEquals(20, mgr.getTouchTolerance());

        mgr.setMode(InputMode.TABLET);
        assertEquals(12, mgr.getTouchTolerance());
    }

    @Test
    public void testInputModeManagerSnapRadius() {
        InputModeManager mgr = new InputModeManager(null, null);
        assertEquals(8, mgr.getObjectSnapRadius()); // desktop

        mgr.setMode(InputMode.WHITEBOARD);
        assertEquals(30, mgr.getObjectSnapRadius());

        mgr.setMode(InputMode.TABLET);
        assertEquals(15, mgr.getObjectSnapRadius());
    }

    @Test
    public void testInputModeManagerButtonSize() {
        InputModeManager mgr = new InputModeManager(null, null);
        // Desktop uses standard size
        assertTrue(mgr.getMinButtonSize() > 0);
        // Whiteboard uses larger size
        mgr.setMode(InputMode.WHITEBOARD);
        assertTrue(mgr.getMinButtonSize() >= mgr.getTouchTargetSize());
    }

    @Test
    public void testInputModeManagerFloatingToolBarSize() {
        InputModeManager mgr = new InputModeManager(null, null);
        // Desktop
        assertEquals(340, mgr.getFloatingToolBarWidth());
        assertEquals(52, mgr.getFloatingToolBarHeight());

        // Whiteboard
        mgr.setMode(InputMode.WHITEBOARD);
        assertEquals(500, mgr.getFloatingToolBarWidth());
        assertEquals(80, mgr.getFloatingToolBarHeight());

        // Tablet
        mgr.setMode(InputMode.TABLET);
        assertEquals(380, mgr.getFloatingToolBarWidth());
        assertEquals(64, mgr.getFloatingToolBarHeight());
    }

    @Test
    public void testInputModeManagerNullModeThrows() {
        InputModeManager mgr = new InputModeManager(null, null);
        try {
            mgr.setMode(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testInputModeManagerHandlers() {
        InputModeManager mgr = new InputModeManager(null, null);
        List<InputHandler> handlers = mgr.getHandlers();
        assertEquals(3, handlers.size());
        assertTrue(handlers.get(0) instanceof DesktopInputMode);
        assertTrue(handlers.get(1) instanceof WhiteboardInputMode);
        assertTrue(handlers.get(2) instanceof TabletInputMode);
    }

    @Test
    public void testInputModeManagerActiveHandler() {
        InputModeManager mgr = new InputModeManager(null, null);
        InputHandler handler = mgr.getActiveHandler();
        assertNotNull(handler);
        assertTrue(handler.supports(InputMode.DESKTOP));
    }

    @Test
    public void testInputModeManagerToUIInteractionMode() {
        assertEquals(UIInteractionMode.DESKTOP,
                InputModeManager.toUIInteractionMode(InputMode.DESKTOP));
        assertEquals(UIInteractionMode.WHITEBOARD,
                InputModeManager.toUIInteractionMode(InputMode.WHITEBOARD));
        assertEquals(UIInteractionMode.WHITEBOARD,
                InputModeManager.toUIInteractionMode(InputMode.TABLET));
    }

    // ------------------------------------------------------------------
    // DesktopInputMode
    // ------------------------------------------------------------------

    @Test
    public void testDesktopInputModeSupportsDesktop() {
        DesktopInputMode handler = new DesktopInputMode();
        assertTrue(handler.supports(InputMode.DESKTOP));
        assertFalse(handler.supports(InputMode.WHITEBOARD));
        assertFalse(handler.supports(InputMode.TABLET));
    }

    @Test
    public void testDesktopInputModeMouseMove() {
        DesktopInputMode handler = new DesktopInputMode();
        handler.onMouseMove(100, 200);
        assertEquals(100, handler.getLastX());
        assertEquals(200, handler.getLastY());
    }

    @Test
    public void testDesktopInputModeMouseDown() {
        DesktopInputMode handler = new DesktopInputMode();
        handler.onMouseDown(100, 200);
        assertEquals(100, handler.getLastX());
        assertEquals(200, handler.getLastY());
    }

    @Test
    public void testDesktopInputModeMouseUp() {
        DesktopInputMode handler = new DesktopInputMode();
        handler.onMouseDown(100, 200);
        handler.onMouseUp(100, 200);
        assertEquals(100, handler.getLastX());
        assertEquals(200, handler.getLastY());
    }

    @Test
    public void testDesktopInputModeKeyboard() {
        DesktopInputMode handler = new DesktopInputMode();
        handler.onKeyPress(32);   // Space
        handler.onKeyRelease(32); // Space
        // Should not throw
    }

    @Test
    public void testDesktopInputModeScroll() {
        DesktopInputMode handler = new DesktopInputMode();
        handler.onScroll(5);
        handler.onScroll(-3);
        // Should not throw
    }

    // ------------------------------------------------------------------
    // WhiteboardInputMode
    // ------------------------------------------------------------------

    @Test
    public void testWhiteboardInputModeSupportsWhiteboard() {
        WhiteboardInputMode handler = new WhiteboardInputMode();
        assertTrue(handler.supports(InputMode.WHITEBOARD));
        assertFalse(handler.supports(InputMode.DESKTOP));
        assertFalse(handler.supports(InputMode.TABLET));
    }

    @Test
    public void testWhiteboardInputModeTouchEvents() {
        WhiteboardInputMode handler = new WhiteboardInputMode();
        handler.onMouseDown(100, 200);
        handler.onMouseMove(110, 210);
        handler.onMouseUp(110, 210);
        // Should not throw
    }

    @Test
    public void testWhiteboardInputModePenEvents() {
        WhiteboardInputMode handler = new WhiteboardInputMode();
        handler.onPenDown(100, 200, 0.5f);
        handler.onPenMove(110, 210, 0.6f);
        handler.onPenUp(110, 210, 0f);
        // Should not throw
    }

    @Test
    public void testWhiteboardInputModeGestureRecognizer() {
        WhiteboardInputMode handler = new WhiteboardInputMode();
        GestureRecognizer gr = handler.getGestureRecognizer();
        assertNotNull(gr);
    }

    @Test
    public void testWhiteboardInputModeEscCancel() {
        WhiteboardInputMode handler = new WhiteboardInputMode();
        handler.onKeyPress(27); // ESC
        // Should not throw, cancels gesture
    }

    // ------------------------------------------------------------------
    // TabletInputMode
    // ------------------------------------------------------------------

    @Test
    public void testTabletInputModeSupportsTablet() {
        TabletInputMode handler = new TabletInputMode();
        assertTrue(handler.supports(InputMode.TABLET));
        assertFalse(handler.supports(InputMode.DESKTOP));
        assertFalse(handler.supports(InputMode.WHITEBOARD));
    }

    @Test
    public void testTabletInputModeTouchAndPen() {
        TabletInputMode handler = new TabletInputMode();
        handler.onMouseDown(100, 200);
        handler.onPenDown(150, 250, 0.8f);
        assertEquals(0.8f, handler.getLastPressure(), 0.01);
        handler.onPenUp(150, 250, 0.8f);
        assertEquals(0f, handler.getLastPressure(), 0.01);
    }

    @Test
    public void testTabletInputModeGestureRecognizer() {
        TabletInputMode handler = new TabletInputMode();
        GestureRecognizer gr = handler.getGestureRecognizer();
        assertNotNull(gr);
    }

    @Test
    public void testTabletInputModePressureClamping() {
        TabletInputMode handler = new TabletInputMode();
        handler.onPenDown(0, 0, -1f);
        assertEquals(0f, handler.getLastPressure(), 0.01);
        handler.onPenDown(0, 0, 2f);
        assertEquals(1f, handler.getLastPressure(), 0.01);
    }

    // ------------------------------------------------------------------
    // GestureRecognizer
    // ------------------------------------------------------------------

    @Test
    public void testGestureRecognizerTap() {
        GestureRecognizer gr = new GestureRecognizer();
        gr.processDown(100, 100);
        assertTrue(gr.processUp(100, 100));
    }

    @Test
    public void testGestureRecognizerDrag() {
        GestureRecognizer gr = new GestureRecognizer();
        gr.processDown(100, 100);
        gr.processMove(120, 120); // beyond 10px threshold
        assertFalse(gr.processUp(120, 120));
    }

    @Test
    public void testGestureRecognizerDoubleTap() {
        GestureRecognizer gr = new GestureRecognizer();
        // First tap
        gr.processDown(100, 100);
        gr.processUp(100, 100);
        // Second tap quickly
        gr.processDown(100, 100);
        gr.processUp(100, 100);
        assertTrue(gr.isDoubleTap());
    }

    @Test
    public void testGestureRecognizerCancel() {
        GestureRecognizer gr = new GestureRecognizer();
        gr.processDown(100, 100);
        gr.cancel();
        assertEquals(GestureRecognizer.State.IDLE, gr.getState());
    }

    @Test
    public void testGestureRecognizerDownCoordinates() {
        GestureRecognizer gr = new GestureRecognizer();
        gr.processDown(50, 75);
        assertEquals(50, gr.getDownX());
        assertEquals(75, gr.getDownY());
    }

    @Test
    public void testGestureRecognizerInitialState() {
        GestureRecognizer gr = new GestureRecognizer();
        assertEquals(GestureRecognizer.State.IDLE, gr.getState());
    }

    @Test
    public void testGestureRecognizerSmallMovementIsTap() {
        GestureRecognizer gr = new GestureRecognizer();
        gr.processDown(100, 100);
        gr.processMove(105, 105); // within 10px threshold
        assertTrue(gr.processUp(105, 105));
    }

    // ------------------------------------------------------------------
    // EducationTheme
    // ------------------------------------------------------------------

    @Test
    public void testEducationThemeColours() {
        EducationTheme theme = new EducationTheme();
        assertNotNull(theme.getBackgroundColor());
        assertNotNull(theme.getPanelBackgroundColor());
        assertNotNull(theme.getToolbarColor());
        assertNotNull(theme.getToolbarTextColor());
        assertNotNull(theme.getActiveToolColor());
        assertNotNull(theme.getCanvasBorderColor());
        assertNotNull(theme.getSelectionColor());
        assertNotNull(theme.getTextColour());
        assertNotNull(theme.getCanvasDrawColor());
    }

    @Test
    public void testEducationThemeFonts() {
        EducationTheme theme = new EducationTheme();
        Font desktopFont = theme.getFont(InputMode.DESKTOP);
        assertNotNull(desktopFont);
        assertEquals(14, desktopFont.getSize());

        Font whiteboardFont = theme.getFont(InputMode.WHITEBOARD);
        assertNotNull(whiteboardFont);
        assertEquals(20, whiteboardFont.getSize());

        Font tabletFont = theme.getFont(InputMode.TABLET);
        assertNotNull(tabletFont);
        assertEquals(18, tabletFont.getSize());
    }

    @Test
    public void testEducationThemeLabelFont() {
        EducationTheme theme = new EducationTheme();
        Font font = theme.getLabelFont(InputMode.DESKTOP);
        assertNotNull(font);
        assertEquals(14, font.getSize());

        font = theme.getLabelFont(InputMode.WHITEBOARD);
        assertEquals(16, font.getSize());
    }

    @Test
    public void testEducationThemeButtonFontWithNull() {
        EducationTheme theme = new EducationTheme();
        Font font = theme.getButtonFont(null);
        assertNotNull(font);
        assertEquals(14, font.getSize());
    }

    @Test
    public void testEducationThemeButtonFont() {
        EducationTheme theme = new EducationTheme();
        Font desktop = theme.getButtonFont(InputMode.DESKTOP);
        assertNotNull(desktop);
        assertEquals(14, desktop.getSize());

        Font whiteboard = theme.getButtonFont(InputMode.WHITEBOARD);
        assertNotNull(whiteboard);
        assertEquals(18, whiteboard.getSize());

        Font tablet = theme.getButtonFont(InputMode.TABLET);
        assertNotNull(tablet);
        assertEquals(16, tablet.getSize());
    }

    @Test
    public void testEducationThemeWhiteboardFontLarger() {
        EducationTheme theme = new EducationTheme();
        Font desktop = theme.getFont(InputMode.DESKTOP);
        Font whiteboard = theme.getFont(InputMode.WHITEBOARD);
        assertTrue(whiteboard.getSize() > desktop.getSize());
    }

    // ------------------------------------------------------------------
    // FloatingToolBar
    // ------------------------------------------------------------------

    @Test
    public void testFloatingToolBarCreation() {
        EducationTheme theme = new EducationTheme();
        FloatingToolBar toolbar = new FloatingToolBar(theme);
        assertNotNull(toolbar);
        assertFalse(toolbar.isVisibleNow());
    }

    @Test
    public void testFloatingToolBarAddButton() {
        EducationTheme theme = new EducationTheme();
        FloatingToolBar toolbar = new FloatingToolBar(theme);
        toolbar.addToolButton("Select", "select", e -> {});
        toolbar.addToolButton("Move", "move", e -> {});
        // Verify buttons were added (indirectly via revalidation not failing)
    }

    @Test
    public void testFloatingToolBarClear() {
        EducationTheme theme = new EducationTheme();
        FloatingToolBar toolbar = new FloatingToolBar(theme);
        toolbar.addToolButton("Select", "select", e -> {});
        toolbar.clear();
        // Should not throw
    }

    @Test
    public void testFloatingToolBarHideShow() {
        EducationTheme theme = new EducationTheme();
        FloatingToolBar toolbar = new FloatingToolBar(theme);
        toolbar.doShow();
        assertTrue(toolbar.isVisibleNow());
        toolbar.doHide();
        assertFalse(toolbar.isVisibleNow());
    }

    @Test
    public void testFloatingToolBarShowAt() {
        EducationTheme theme = new EducationTheme();
        FloatingToolBar toolbar = new FloatingToolBar(theme);
        toolbar.showAt(100, 200);
        assertTrue(toolbar.isVisibleNow());
    }

    // ------------------------------------------------------------------
    // BottomActionBar
    // ------------------------------------------------------------------

    @Test
    public void testBottomActionBarCreation() {
        EducationTheme theme = new EducationTheme();
        BottomActionBar bar = new BottomActionBar(theme);
        assertNotNull(bar);
        assertEquals(0, bar.getButtonCount());
    }

    @Test
    public void testBottomActionBarAddButton() {
        EducationTheme theme = new EducationTheme();
        BottomActionBar bar = new BottomActionBar(theme);
        bar.addButton("2D", "mode_2d", e -> {});
        assertEquals(1, bar.getButtonCount());
        bar.addButton("3D", "mode_3d", e -> {});
        assertEquals(2, bar.getButtonCount());
    }

    @Test
    public void testBottomActionBarClear() {
        EducationTheme theme = new EducationTheme();
        BottomActionBar bar = new BottomActionBar(theme);
        bar.addButton("Reset", "reset", e -> {});
        bar.clear();
        assertEquals(0, bar.getButtonCount());
    }

    @Test
    public void testBottomActionBarHeight() {
        BottomActionBar bar = new BottomActionBar(new EducationTheme());
        assertEquals(56, bar.getBarHeight());
    }

    // ------------------------------------------------------------------
    // GeometryCanvasView
    // ------------------------------------------------------------------

    @Test
    public void testGeometryCanvasViewCreation() {
        EducationTheme theme = new EducationTheme();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        assertNotNull(canvas);
        assertEquals(ViewMode.MODE_2D, canvas.getViewMode());
        assertEquals(-1, canvas.getSelectedIndex());
    }

    @Test
    public void testGeometryCanvasViewSetViewMode() {
        EducationTheme theme = new EducationTheme();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        canvas.setViewMode(ViewMode.MODE_3D);
        assertEquals(ViewMode.MODE_3D, canvas.getViewMode());
        canvas.setViewMode(ViewMode.MODE_2D);
        assertEquals(ViewMode.MODE_2D, canvas.getViewMode());
    }

    @Test
    public void testGeometryCanvasViewResetView() {
        EducationTheme theme = new EducationTheme();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        canvas.resetView();
        assertEquals(-1, canvas.getSelectedIndex());
    }

    @Test
    public void testGeometryCanvasViewTouchForwarding() {
        EducationTheme theme = new EducationTheme();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        // Touch forwarding should not throw even with empty scene
        canvas.onTouchDown(100, 100);
        canvas.onPenDown(100, 100, 0.5f);
    }

    @Test
    public void testGeometryCanvasViewWithObjects() {
        EducationTheme theme = new EducationTheme();
        Rectangle rect = new Rectangle(50f, 30f);
        Cube cube = new Cube(40f, 40f, 40f);
        scene.addObject("rect1", rect);
        scene.addObject("cube1", cube);

        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        // Canvas should render without error
        canvas.setSize(800, 600);
        canvas.repaint();

        // Verify objects are in scene
        List<SceneObject> objects = scene.getAllObjects();
        assertEquals(2, objects.size());
    }

    @Test
    public void testGeometryCanvasViewPaintWithEmptyScene() {
        EducationTheme theme = new EducationTheme();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, theme);
        canvas.setSize(800, 600);
        // Should not throw with empty scene
        canvas.repaint();
    }

    // ------------------------------------------------------------------
    // LessonStatusBar
    // ------------------------------------------------------------------

    @Test
    public void testLessonStatusBarCreation() {
        EducationTheme theme = new EducationTheme();
        LessonStatusBar bar = new LessonStatusBar(theme);
        assertNotNull(bar);
    }

    @Test
    public void testLessonStatusBarDisplay() {
        EducationTheme theme = new EducationTheme();
        LessonStatusBar bar = new LessonStatusBar(theme);
        bar.setLessonName("Triangle Properties");
        bar.setStepDisplay("Step 2 of 5");
        bar.setModeDisplay("DESKTOP");
        // Should not throw
    }

    // ------------------------------------------------------------------
    // TeachingWorkspace Integration
    // ------------------------------------------------------------------

    @Test
    public void testTeachingWorkspaceCreation() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        assertNotNull(ws);
        assertNotNull(ws.getWorkspace());
        assertNotNull(ws.getCanvasView());
        assertNotNull(ws.getStatusBar());
        assertNotNull(ws.getBottomActionBar());
        assertNotNull(ws.getFloatingToolBar());
        assertNotNull(ws.getInputModeManager());
    }

    @Test
    public void testTeachingWorkspaceWithNullManagers() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                null, null);
        assertNotNull(ws);
        assertNotNull(ws.getWorkspace());
        assertNotNull(ws.getCanvasView());
    }

    @Test
    public void testTeachingWorkspaceInputModes() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        // Start in desktop mode
        assertEquals(InputMode.DESKTOP, ws.getInputModeManager().getMode());

        // Switch to whiteboard
        ws.setInputMode(InputMode.WHITEBOARD);
        assertEquals(InputMode.WHITEBOARD, ws.getInputModeManager().getMode());

        // Switch to tablet
        ws.setInputMode(InputMode.TABLET);
        assertEquals(InputMode.TABLET, ws.getInputModeManager().getMode());

        // Switch back to desktop
        ws.setInputMode(InputMode.DESKTOP);
        assertEquals(InputMode.DESKTOP, ws.getInputModeManager().getMode());
    }

    @Test
    public void testTeachingWorkspaceViewModes() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        ws.setViewMode(ViewMode.MODE_3D);
        assertEquals(ViewMode.MODE_3D, ws.getCanvasView().getViewMode());

        ws.setViewMode(ViewMode.MODE_2D);
        assertEquals(ViewMode.MODE_2D, ws.getCanvasView().getViewMode());
    }

    @Test
    public void testTeachingWorkspaceToolSwitch() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        // Switching to an unknown tool should not throw
        ws.switchTool("unknown_tool");
        // Switching to a registered tool should work
        ws.switchTool("move");
    }

    @Test
    public void testTeachingWorkspaceStatusBarUpdate() {
        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        ws.updateStatusBar("Circle Properties", "Step 1 of 3");
        // Should not throw
    }

    @Test
    public void testTeachingWorkspaceCanvasInteraction() {
        Rectangle rect = new Rectangle(50f, 30f);
        SceneObject obj = scene.addObject("rect1", rect);

        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        GeometryCanvasView canvas = ws.getCanvasView();
        canvas.setPreferredSize(new Dimension(800, 600));
        canvas.setSize(800, 600);
        canvas.revalidate();
        // Simulate click near object center (center is at origin by default)
        // handleTap is called directly to avoid invokeLater race from onTouchDown
        canvas.handleTap(400, 300);
        // Should select the object
        assertEquals(0, canvas.getSelectedIndex());
    }

    @Test
    public void testTeachingWorkspaceCanvasClickMiss() {
        Cube cube = new Cube(40f, 40f, 40f);
        // Place far away using override transform
        Transform farTransform = new Transform(
                new Vec3(1000f, 1000f, 0f),
                new Vec3(0, 0, 0),
                new Vec3(1, 1, 1));
        SceneObject cubeObj = scene.addObject("cube1", cube);
        cubeObj.setOverrideTransform(farTransform);

        TeachingWorkspace ws = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        GeometryCanvasView canvas = ws.getCanvasView();
        canvas.setPreferredSize(new Dimension(800, 600));
        canvas.setSize(800, 600);
        canvas.revalidate();
        // Click near center — far object should not be selected
        canvas.handleTap(400, 300);
        // Should clear selection (no object within 100 units)
        assertEquals(-1, canvas.getSelectedIndex());
    }

    // ------------------------------------------------------------------
    // ApplicationWindow Integration
    // ------------------------------------------------------------------

    @Test
    public void testApplicationWindowCreateAndClose() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        assertFalse(window.isCreated());
        assertTrue(window.create());
        assertTrue(window.isCreated());
        assertNotNull(window.getWorkspace());
        assertNotNull(window.getCanvasView());
        assertNotNull(window.getInputModeManager());

        window.close();
        assertFalse(window.isCreated());
        // After close, do not call getWorkspace() as it triggers lazy init
        // Instead verify the window is no longer created
    }

    @Test
    public void testApplicationWindowLazyInit() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        // getWorkspace() should trigger lazy create
        assertNotNull(window.getWorkspace());
        assertTrue(window.isCreated());
    }

    @Test
    public void testApplicationWindowAccessors() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        window.create();

        assertEquals(scene, window.getScene());
        assertEquals(toolManager, window.getToolManager());
        assertEquals(interactionManager, window.getInteractionManager());
        assertNotNull(window.getTeachingManager());
        assertNotNull(window.getAnimationManager());
    }

    @Test
    public void testApplicationWindowDoubleCreate() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());

        assertTrue(window.create());
        assertTrue(window.create()); // second call should return true without error
        assertTrue(window.isCreated());
    }

    @Test
    public void testApplicationWindowGetCanvasView() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        window.create();
        assertNotNull(window.getCanvasView());
        assertEquals(ViewMode.MODE_2D, window.getCanvasView().getViewMode());
    }

    @Test
    public void testApplicationWindowGetInputModeManager() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        window.create();
        assertNotNull(window.getInputModeManager());
        assertEquals(InputMode.DESKTOP, window.getInputModeManager().getMode());
    }

    @Test
    public void testApplicationWindowCloseResetsState() {
        ApplicationWindow window = new ApplicationWindow(
                scene, toolManager, interactionManager,
                new TeachingManager(scene, null),
                new AnimationManager());
        window.create();
        window.close();
        assertFalse(window.isCreated());
        // After close, getWorkspace would trigger lazy init, so verify state differently
        // Use getTeachingWorkspace() directly which returns null after close
        assertNull(window.getTeachingWorkspace());
    }
}
