package com.geometry.interaction;

import com.geometry.core.geometry.Cube;
import com.geometry.interaction.action.MoveAction;
import com.geometry.interaction.action.RotateAction;
import com.geometry.interaction.action.ScaleAction;
import com.geometry.interaction.action.SelectAction;
import com.geometry.interaction.event.GestureEvent;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.interaction.gesture.GestureRecognizer;
import com.geometry.interaction.input.TouchDevice;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 05 - Tests for the Interaction System.
 *
 * Tests:
 *   - InteractionMode values and switching
 *   - PointerEvent creation and properties
 *   - GestureRecognizer: drag, pinch, rotate, tap detection
 *   - Action creation and execution (MoveAction, ScaleAction, SelectAction)
 *   - InputDevice registration and event flow
 *   - Mode-independent gesture-to-action conversion (mouse drag == touch drag)
 */
public class InteractionTest {

    private Scene scene;
    private InteractionManager interactionManager;
    private GestureRecognizer gestureRecognizer;

    @Before
    public void setUp() {
        scene = new Scene();
        interactionManager = new InteractionManager(scene);
        gestureRecognizer = new GestureRecognizer();
    }

    // ------------------------------------------------------------------
    // InteractionMode tests
    // ------------------------------------------------------------------

    @Test
    public void testInteractionModeValues() {
        InteractionMode[] modes = InteractionMode.values();
        assertEquals(2, modes.length);
        assertEquals(InteractionMode.DESKTOP, modes[0]);
        assertEquals(InteractionMode.WHITEBOARD, modes[1]);
    }

    @Test
    public void testInteractionModeSwitch() {
        interactionManager.setMode(InteractionMode.WHITEBOARD);
        assertEquals(InteractionMode.WHITEBOARD, interactionManager.getMode());

        interactionManager.setMode(InteractionMode.DESKTOP);
        assertEquals(InteractionMode.DESKTOP, interactionManager.getMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInteractionManagerNullScene() {
        new InteractionManager(null);
    }

    // ------------------------------------------------------------------
    // PointerEvent tests
    // ------------------------------------------------------------------

    @Test
    public void testPointerEventMouseDown() {
        PointerEvent event = new PointerEvent(
                0, PointerEvent.PointerType.MOUSE,
                new Vec2(100f, 200f),
                Vec2.ZERO,
                PointerEvent.EventType.DOWN
        );
        assertEquals(0, event.getPointerId());
        assertEquals(PointerEvent.PointerType.MOUSE, event.getPointerType());
        assertEquals(PointerEvent.EventType.DOWN, event.getEventType());
        assertTrue(event.isDown());
        assertFalse(event.isUp());
        assertFalse(event.isMove());
        assertFalse(event.isClick());
    }

    @Test
    public void testPointerEventTouchMove() {
        PointerEvent move = new PointerEvent(
                1, PointerEvent.PointerType.TOUCH,
                new Vec2(150f, 250f),
                new Vec2(5f, -3f),
                PointerEvent.EventType.MOVE
        );
        assertTrue(move.isMove());
        assertEquals(5f, move.getDelta().x, 0.001f);
        assertEquals(-3f, move.getDelta().y, 0.001f);
    }

    @Test
    public void testPointerEventPenWithPressure() {
        PointerEvent event = new PointerEvent(
                0, PointerEvent.PointerType.PEN,
                new Vec2(300f, 400f),
                Vec2.ZERO,
                PointerEvent.EventType.DOWN,
                0.75f
        );
        assertEquals(PointerEvent.PointerType.PEN, event.getPointerType());
        assertEquals(0.75f, event.getPressure(), 0.001f);
    }

    @Test
    public void testPointerEventNullPositionDefaultsToZero() {
        PointerEvent event = new PointerEvent(
                0, PointerEvent.PointerType.MOUSE,
                null, null, PointerEvent.EventType.DOWN
        );
        assertEquals(Vec2.ZERO, event.getPosition());
        assertEquals(Vec2.ZERO, event.getDelta());
    }

    // ------------------------------------------------------------------
    // GestureRecognizer tests
    // ------------------------------------------------------------------

    @Test
    public void testGestureRecogniserTaps() {
        // Simulate: DOWN at (100,100), UP at (101,101) — small movement = TAP
        List<GestureEvent> gestures = gestureRecognizer.process(Arrays.asList(
                new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                        new Vec2(100f, 100f),
                        Vec2.ZERO,
                        PointerEvent.EventType.DOWN),
                new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                        new Vec2(101f, 101f),
                        new Vec2(1f, 1f),
                        PointerEvent.EventType.UP)
        ));

        assertEquals(1, gestures.size());
        assertTrue(gestures.get(0).isTap());
    }

    @Test
    public void testGestureRecogniserDrag() {
        // Simulate: DOWN at (100,100), multiple MOVEs, then UP — large movement = DRAG
        List<PointerEvent> events = new java.util.ArrayList<>();
        events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(100f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.DOWN));

        for (int i = 1; i <= 10; i++) {
            events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                    new Vec2(100f + i * 5f, 100f),
                    new Vec2(5f, 0f),
                    PointerEvent.EventType.MOVE));
        }
        events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(150f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.UP));

        List<GestureEvent> gestures = gestureRecognizer.process(events);
        assertEquals(1, gestures.size());
        assertTrue(gestures.get(0).isDrag());
        assertTrue(gestures.get(0).getDistance() > 0f);
    }

    @Test
    public void testGestureRecogniserPinch() {
        // Two fingers start close, then move apart
        Vec2 f1Start = new Vec2(200f, 300f);
        Vec2 f2Start = new Vec2(220f, 300f);
        Vec2 f1End = new Vec2(200f, 350f);
        Vec2 f2End = new Vec2(220f, 250f);

        List<PointerEvent> events = new java.util.ArrayList<>();
        // Finger 1 down
        events.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                f1Start, Vec2.ZERO,
                PointerEvent.EventType.DOWN));
        // Finger 2 down (triggers two-finger mode)
        events.add(new PointerEvent(1, PointerEvent.PointerType.TOUCH,
                f2Start, Vec2.ZERO,
                PointerEvent.EventType.DOWN));
        // Move both fingers apart
        events.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                f1End, f1End.subtract(f1Start), PointerEvent.EventType.MOVE));
        events.add(new PointerEvent(1, PointerEvent.PointerType.TOUCH,
                f2End, f2End.subtract(f2Start), PointerEvent.EventType.MOVE));
        // Lift both
        events.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                f1End, Vec2.ZERO,
                PointerEvent.EventType.UP));
        events.add(new PointerEvent(1, PointerEvent.PointerType.TOUCH,
                f2End, Vec2.ZERO,
                PointerEvent.EventType.UP));

        List<GestureEvent> gestures = gestureRecognizer.process(events);
        // Should produce at least one PINCH or ROTATE gesture
        boolean hasPinchOrRotate = false;
        for (GestureEvent g : gestures) {
            if (g.isPinch() || g.isRotate()) {
                hasPinchOrRotate = true;
                break;
            }
        }
        assertTrue("Expected pinch or rotate gesture from two-finger input", hasPinchOrRotate);
    }

    @Test
    public void testGestureRecogniserEmptyEvents() {
        List<GestureEvent> gestures = gestureRecognizer.process(new java.util.ArrayList<>());
        assertTrue(gestures.isEmpty());
    }

    // ------------------------------------------------------------------
    // TouchDevice tests
    // ------------------------------------------------------------------

    @Test
    public void testTouchDeviceInjectDownAndMove() {
        TouchDevice touch = new TouchDevice();
        touch.injectDown(0, 100f, 200f);
        touch.injectMove(0, 110f, 210f);

        List<com.geometry.interaction.event.InputEvent> events = touch.getEvents();
        assertEquals(2, events.size());
        assertTrue(events.get(0) instanceof PointerEvent);
        assertTrue(events.get(1) instanceof PointerEvent);

        PointerEvent down = (PointerEvent) events.get(0);
        assertEquals(PointerEvent.EventType.DOWN, down.getEventType());
        assertEquals(100f, down.getPosition().x, 0.001f);

        PointerEvent move = (PointerEvent) events.get(1);
        assertEquals(PointerEvent.EventType.MOVE, move.getEventType());
        assertEquals(10f, move.getDelta().x, 0.001f);
    }

    @Test
    public void testTouchDeviceMultiTouch() {
        TouchDevice touch = new TouchDevice();
        touch.injectDown(0, 100f, 100f);
        touch.injectDown(1, 200f, 200f);

        assertEquals(2, touch.getActivePointerCount());
        assertTrue(touch.getActivePointerIds().contains(0));
        assertTrue(touch.getActivePointerIds().contains(1));

        touch.injectUp(0);
        assertEquals(1, touch.getActivePointerCount());
        assertFalse(touch.getActivePointerIds().contains(0));
        assertTrue(touch.getActivePointerIds().contains(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTouchDeviceInvalidPointerId() {
        TouchDevice touch = new TouchDevice();
        touch.injectDown(99, 0f, 0f);
    }

    @Test
    public void testTouchDeviceMoveWithoutDownIgnored() {
        TouchDevice touch = new TouchDevice();
        touch.injectMove(0, 100f, 100f); // No DOWN first — should be ignored
        List<com.geometry.interaction.event.InputEvent> events = touch.getEvents();
        assertTrue(events.isEmpty());
    }

    // ------------------------------------------------------------------
    // PenDevice tests
    // ------------------------------------------------------------------

    @Test
    public void testPenDeviceInjectDownMoveUp() {
        com.geometry.interaction.input.PenDevice pen = new com.geometry.interaction.input.PenDevice();
        pen.injectDown(100f, 200f, 0.8f);
        pen.injectMove(105f, 205f);
        pen.injectUp();

        List<com.geometry.interaction.event.InputEvent> events = pen.getEvents();
        assertEquals(3, events.size());

        PointerEvent down = (PointerEvent) events.get(0);
        assertEquals(PointerEvent.PointerType.PEN, down.getPointerType());
        assertEquals(0.8f, down.getPressure(), 0.001f);

        PointerEvent move = (PointerEvent) events.get(1);
        assertTrue(move.isMove());
        assertEquals(5f, move.getDelta().x, 0.001f);
        assertEquals(5f, move.getDelta().y, 0.001f);
    }

    @Test
    public void testPenDeviceMoveWithoutDownIgnored() {
        com.geometry.interaction.input.PenDevice pen = new com.geometry.interaction.input.PenDevice();
        pen.injectMove(100f, 100f); // No DOWN — should be ignored
        List<com.geometry.interaction.event.InputEvent> events = pen.getEvents();
        assertTrue(events.isEmpty());
    }

    // ------------------------------------------------------------------
    // Action tests
    // ------------------------------------------------------------------

    @Test
    public void testMoveAction() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube", cube);

        interactionManager.setPixelsPerWorldUnit(40f);
        // Simulate a drag of 200 pixels = 5 world units
        MoveAction move = new MoveAction(so, 5f, 0f);
        move.execute();

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(5f, t.getPosition().x, 0.001f);
        assertEquals(0f, t.getPosition().y, 0.001f);
    }

    @Test
    public void testScaleAction() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube", cube);

        ScaleAction scale = new ScaleAction(so, 2.0f);
        scale.execute();

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(2f, t.getScale().x, 0.001f);
        assertEquals(2f, t.getScale().y, 0.001f);
        assertEquals(2f, t.getScale().z, 0.001f);
    }

    @Test
    public void testRotateAction() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("cube", cube);

        RotateAction rotate = new RotateAction(so, 90f);
        rotate.execute();

        com.geometry.core.transform.Transform t = so.getOverrideTransform();
        assertNotNull(t);
        assertEquals(90f, t.getRotation().z, 0.001f);
    }

    @Test
    public void testSelectActionSelectsObject() {
        SceneObject so = scene.addObject("cube", new Cube(1f, 1f, 1f));
        assertNotNull(scene.getSelected()); // auto-selected on add

        SelectAction select = new SelectAction(scene, so);
        select.execute();
        assertTrue(scene.isSelected(so));
    }

    @Test
    public void testSelectActionClearsSelection() {
        SceneObject so = scene.addObject("cube", new Cube(1f, 1f, 1f));
        scene.clearSelection();
        assertNull(scene.getSelected());

        SelectAction clear = new SelectAction(scene, null);
        clear.execute();
        assertNull(scene.getSelected());
    }

    @Test
    public void testActionDescriptions() {
        SceneObject so = scene.addObject("cube", new Cube(1f, 1f, 1f));
        MoveAction move = new MoveAction(so, 1f, 2f);
        assertTrue(move.getDescription().contains("MoveAction"));
        assertTrue(move.getDescription().contains("cube"));

        ScaleAction scale = new ScaleAction(so, 1.5f);
        assertTrue(scale.getDescription().contains("ScaleAction"));

        RotateAction rotate = new RotateAction(so, 45f);
        assertTrue(rotate.getDescription().contains("RotateAction"));
    }

    // ------------------------------------------------------------------
    // InteractionManager end-to-end tests
    // ------------------------------------------------------------------

    @Test
    public void testInteractionManagerDeviceRegistration() {
        TouchDevice touch = new TouchDevice();
        interactionManager.registerDevice(touch);
        assertEquals(1, interactionManager.getInputDevices().size());

        interactionManager.unregisterDevice(touch);
        assertTrue(interactionManager.getInputDevices().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInteractionManagerNullDevice() {
        interactionManager.registerDevice(null);
    }

    @Test
    public void testInteractionManagerViewport() {
        interactionManager.setViewport(1024, 768);
        assertEquals(1024, interactionManager.getViewportWidth());
        assertEquals(768, interactionManager.getViewportHeight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInteractionManagerInvalidViewport() {
        interactionManager.setViewport(0, 100);
    }

    @Test
    public void testInteractionManagerPixelsPerWorldUnit() {
        interactionManager.setPixelsPerWorldUnit(50f);
        assertEquals(50f, interactionManager.getPixelsPerWorldUnit(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInteractionManagerZeroPixelsPerWorldUnit() {
        interactionManager.setPixelsPerWorldUnit(0f);
    }

    // ------------------------------------------------------------------
    // Mode-independent gesture conversion test
    // ------------------------------------------------------------------

    @Test
    public void testMouseAndTouchBothProduceMoveAction() {
        // Setup: add a cube and select it
        SceneObject cube = scene.addObject("cube", new Cube(2f, 2f, 2f));
        scene.select(cube);

        // Simulate mouse drag via gesture recogniser
        List<PointerEvent> mouseEvents = new java.util.ArrayList<>();
        mouseEvents.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(100f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.DOWN));
        mouseEvents.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(150f, 100f),
                new Vec2(50f, 0f),
                PointerEvent.EventType.MOVE));
        mouseEvents.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(150f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.UP));

        // Simulate touch drag via gesture recogniser
        List<PointerEvent> touchEvents = new java.util.ArrayList<>();
        touchEvents.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                new Vec2(100f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.DOWN));
        touchEvents.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                new Vec2(150f, 100f),
                new Vec2(50f, 0f),
                PointerEvent.EventType.MOVE));
        touchEvents.add(new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                new Vec2(150f, 100f),
                Vec2.ZERO,
                PointerEvent.EventType.UP));

        // Both should produce a DRAG gesture
        List<GestureEvent> mouseGestures = gestureRecognizer.process(mouseEvents);
        List<GestureEvent> touchGestures = gestureRecognizer.process(touchEvents);

        assertFalse("Mouse should produce gestures", mouseGestures.isEmpty());
        assertFalse("Touch should produce gestures", touchGestures.isEmpty());

        // Find the drag gestures
        GestureEvent mouseDrag = null, touchDrag = null;
        for (GestureEvent g : mouseGestures) {
            if (g.isDrag()) mouseDrag = g;
        }
        for (GestureEvent g : touchGestures) {
            if (g.isDrag()) touchDrag = g;
        }

        assertNotNull("Mouse drag not found", mouseDrag);
        assertNotNull("Touch drag not found", touchDrag);

        // Both should produce the same MoveAction when processed by InteractionManager
        interactionManager.setMode(InteractionMode.DESKTOP);
        interactionManager.setPixelsPerWorldUnit(40f);
        interactionManager.registerDevice(new TouchDevice());

        // Process mouse gestures through the full pipeline
        for (GestureEvent g : mouseGestures) {
            List<com.geometry.interaction.event.PointerEvent> emptyEvents = new java.util.ArrayList<>();
            List<com.geometry.interaction.action.Action> actions =
                    interactionManager.convertGestureToActions(g, emptyEvents);
            for (com.geometry.interaction.action.Action action : actions) {
                action.execute();
            }
        }
        com.geometry.core.transform.Transform mouseResult =
                cube.getOverrideTransform();

        // Reset
        cube.setOverrideTransform(null);
        scene.select(cube);

        // Process touch gestures through the full pipeline
        interactionManager.setMode(InteractionMode.WHITEBOARD);
        interactionManager.update(); // no-op: no new touch events injected

        // Re-register and process touch
        interactionManager.unregisterDevice(null);
        com.geometry.interaction.input.TouchDevice touchDevice = new com.geometry.interaction.input.TouchDevice();
        touchDevice.injectDown(0, 100f, 100f);
        touchDevice.injectMove(0, 150f, 100f);
        touchDevice.injectUp(0);
        interactionManager.registerDevice(touchDevice);
        interactionManager.update();
        com.geometry.core.transform.Transform touchResult =
                cube.getOverrideTransform();

        // Both should have moved the cube by approximately the same amount
        assertNotNull("Mouse drag should produce MoveAction", mouseResult);
        assertNotNull("Touch drag should produce MoveAction", touchResult);
        assertEquals(mouseResult.getPosition().x, touchResult.getPosition().x, 0.01f);
    }

    // ------------------------------------------------------------------
    // Camera ray picking tests
    // ------------------------------------------------------------------

    @Test
    public void testCameraRayFromScreen() {
        com.geometry.renderer.Camera camera = new com.geometry.renderer.Camera();
        float[] ray = camera.getRayFromScreen(400f, 300f, 800, 600);
        assertNotNull(ray);
        assertEquals(6, ray.length);
        // Ray direction should point along negative Z (camera at z=5 looking at origin)
        assertTrue("Ray direction Z should be negative", ray[5] < 0f);
    }

    @Test
    public void testCameraRayDirectionIsUnitVector() {
        com.geometry.renderer.Camera camera = new com.geometry.renderer.Camera();
        float[] ray = camera.getRayFromScreen(400f, 300f, 800, 600);
        float dx = ray[3], dy = ray[4], dz = ray[5];
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        assertTrue("Ray direction should be a unit vector", Math.abs(len - 1f) < 0.001f);
    }

    // ------------------------------------------------------------------
    // GestureEvent tests
    // ------------------------------------------------------------------

    @Test
    public void testGestureEventDrag() {
        GestureEvent g = new GestureEvent(
                GestureEvent.GestureType.DRAG,
                100f, 1f, 0f,
                new Vec2(100f, 200f)
        );
        assertTrue(g.isDrag());
        assertEquals(100f, g.getDistance(), 0.001f);
        assertEquals(100f, g.getOrigin().x, 0.001f);
    }

    @Test
    public void testGestureEventPinch() {
        GestureEvent g = new GestureEvent(
                GestureEvent.GestureType.PINCH,
                0f, 1.5f, 0f,
                new Vec2(400f, 300f)
        );
        assertTrue(g.isPinch());
        assertEquals(1.5f, g.getScaleFactor(), 0.001f);
    }

    @Test
    public void testGestureEventRotate() {
        GestureEvent g = new GestureEvent(
                GestureEvent.GestureType.ROTATE,
                0f, 1f, 45f,
                new Vec2(400f, 300f)
        );
        assertTrue(g.isRotate());
        assertEquals(45f, g.getAngleDegrees(), 0.001f);
    }
}
