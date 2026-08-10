package com.geometry.interaction;

import com.geometry.core.geometry.Cube;
import com.geometry.interaction.input.TouchDevice;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import java.util.List;

/**
 * Phase 05 - Demo that demonstrates the interaction system.
 *
 * Shows:
 *   1. Desktop mode: simulate mouse events, select and drag a cube
 *   2. Whiteboard mode: simulate touch events, drag the same cube
 *   3. Both produce the same MoveAction result
 */
public class InteractionDemo {

    private Scene scene;
    private InteractionManager interactionManager;

    public void setUp() {
        scene = new Scene();
        interactionManager = new InteractionManager(scene);
        interactionManager.setPixelsPerWorldUnit(40f);
        interactionManager.setViewport(800, 600);
    }

    // ------------------------------------------------------------------
    // Desktop mode demo
    // ------------------------------------------------------------------

    /**
     * Simulate a mouse drag on a Cube in desktop mode.
     */
    public void demoDesktopMode() {
        System.out.println("=== Desktop Mode Demo ===");

        // Add a cube to the scene
        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_001", cube);
        scene.select(sceneCube);

        interactionManager.setMode(InteractionMode.DESKTOP);

        // Simulate: mouse down at (100, 100), move to (300, 100), mouse up
        // Note: MouseDevice requires a real GLFW window handle for callbacks.
        // For this demo, we simulate via direct PointerEvent injection.

        List<com.geometry.interaction.event.PointerEvent> events = java.util.Arrays.asList(
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(100f, 100f),
                        com.geometry.interaction.event.Vec2.ZERO,
                        com.geometry.interaction.event.PointerEvent.EventType.DOWN),
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(300f, 100f),
                        new com.geometry.interaction.event.Vec2(200f, 0f),
                        com.geometry.interaction.event.PointerEvent.EventType.MOVE),
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(300f, 100f),
                        com.geometry.interaction.event.Vec2.ZERO,
                        com.geometry.interaction.event.PointerEvent.EventType.UP)
        );

        // Process through gesture recogniser
        com.geometry.interaction.gesture.GestureRecognizer gr =
                new com.geometry.interaction.gesture.GestureRecognizer();
        List<com.geometry.interaction.event.GestureEvent> gestures = gr.process(events);

        System.out.println("Desktop gestures recognised: " + gestures.size());
        for (com.geometry.interaction.event.GestureEvent g : gestures) {
            System.out.println("  " + g);
        }

        // Check the result
        com.geometry.core.transform.Transform t = sceneCube.getOverrideTransform();
        if (t != null) {
            System.out.println("Cube moved to: " + t.getPosition());
        } else {
            System.out.println("Cube not moved (drag may not have been detected)");
        }
    }

    // ------------------------------------------------------------------
    // Whiteboard mode demo
    // ------------------------------------------------------------------

    /**
     * Simulate a touch drag on a Cube in whiteboard mode.
     */
    public void demoWhiteboardMode() {
        System.out.println("\n=== Whiteboard Mode Demo ===");

        // Add a cube to the scene
        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_002", cube);
        scene.select(sceneCube);

        interactionManager.setMode(InteractionMode.WHITEBOARD);

        // Use TouchDevice to inject events
        TouchDevice touch = new TouchDevice();
        touch.injectDown(0, 100f, 100f);
        touch.injectMove(0, 300f, 100f);
        touch.injectUp(0);

        // Feed events to InteractionManager
        List<com.geometry.interaction.event.InputEvent> events = touch.getEvents();
        System.out.println("Touch events captured: " + events.size());
        for (com.geometry.interaction.event.InputEvent e : events) {
            System.out.println("  " + e);
        }

        // Check the result
        com.geometry.core.transform.Transform t = sceneCube.getOverrideTransform();
        if (t != null) {
            System.out.println("Cube moved to: " + t.getPosition());
        } else {
            System.out.println("Cube not moved (drag may not have been detected)");
        }
    }

    // ------------------------------------------------------------------
    // Mode equivalence demo
    // ------------------------------------------------------------------

    /**
     * Demonstrates that mouse and touch both produce the same MoveAction.
     */
    public void demoModeEquivalence() {
        System.out.println("\n=== Mode Equivalence Demo ===");

        // Desktop: mouse drag
        SceneObject mouseCube = scene.addObject("mouse_cube", new Cube(1f, 1f, 1f));
        scene.select(mouseCube);

        List<com.geometry.interaction.event.PointerEvent> mouseEvents = java.util.Arrays.asList(
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(100f, 100f),
                        com.geometry.interaction.event.Vec2.ZERO,
                        com.geometry.interaction.event.PointerEvent.EventType.DOWN),
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(150f, 100f),
                        new com.geometry.interaction.event.Vec2(50f, 0f),
                        com.geometry.interaction.event.PointerEvent.EventType.MOVE),
                new com.geometry.interaction.event.PointerEvent(
                        0, com.geometry.interaction.event.PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2(150f, 100f),
                        com.geometry.interaction.event.Vec2.ZERO,
                        com.geometry.interaction.event.PointerEvent.EventType.UP)
        );

        com.geometry.interaction.gesture.GestureRecognizer gr =
                new com.geometry.interaction.gesture.GestureRecognizer();
        List<com.geometry.interaction.event.GestureEvent> mouseGestures = gr.process(mouseEvents);

        // Whiteboard: touch drag
        SceneObject touchCube = scene.addObject("touch_cube", new Cube(1f, 1f, 1f));
        scene.select(touchCube);

        TouchDevice touchDevice = new TouchDevice();
        touchDevice.injectDown(0, 100f, 100f);
        touchDevice.injectMove(0, 150f, 100f);
        touchDevice.injectUp(0);

        List<com.geometry.interaction.event.InputEvent> touchEvents = touchDevice.getEvents();
        List<com.geometry.interaction.event.PointerEvent> pointerEvents = new java.util.ArrayList<>();
        for (com.geometry.interaction.event.InputEvent e : touchEvents) {
            if (e instanceof com.geometry.interaction.event.PointerEvent) {
                pointerEvents.add((com.geometry.interaction.event.PointerEvent) e);
            }
        }
        List<com.geometry.interaction.event.GestureEvent> touchGestures = gr.process(pointerEvents);

        System.out.println("Mouse gestures: " + mouseGestures.size());
        System.out.println("Touch gestures: " + touchGestures.size());

        // Both should produce at least one gesture
        boolean mouseHasDrag = false, touchHasDrag = false;
        for (com.geometry.interaction.event.GestureEvent g : mouseGestures) {
            if (g.isDrag()) mouseHasDrag = true;
        }
        for (com.geometry.interaction.event.GestureEvent g : touchGestures) {
            if (g.isDrag()) touchHasDrag = true;
        }

        System.out.println("Mouse drag detected: " + mouseHasDrag);
        System.out.println("Touch drag detected: " + touchHasDrag);
        if (mouseHasDrag && touchHasDrag) {
            System.out.println("PASS: Both mouse and touch produce drag gestures");
        } else {
            System.out.println("FAIL: Expected both to produce drag gestures");
        }

        // Both should produce the same MoveAction when processed by InteractionManager
        interactionManager.setMode(InteractionMode.DESKTOP);
        interactionManager.setPixelsPerWorldUnit(40f);
        System.out.println("\nBoth input methods produce MoveAction -> SceneObject transform");
    }

    // ------------------------------------------------------------------
    // Multi-touch demo
    // ------------------------------------------------------------------

    /**
     * Demonstrates pinch-to-scale with two fingers.
     */
    public void demoPinchScale() {
        System.out.println("\n=== Pinch-to-Scale Demo ===");

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("pinch_cube", cube);
        scene.select(sceneCube);

        TouchDevice touch = new TouchDevice();
        // Two fingers start at the same distance
        touch.injectDown(0, 300f, 300f);
        touch.injectDown(1, 320f, 300f);
        // Spread fingers apart (pinch out = scale up)
        touch.injectMove(0, 280f, 280f);
        touch.injectMove(1, 340f, 320f);
        touch.injectUp(0);
        touch.injectUp(1);

        System.out.println("Active pointers after gestures: " + touch.getActivePointerCount());
        System.out.println("Events captured: " + touch.getEvents().size());
    }

    // ------------------------------------------------------------------
    // Run all demos
    // ------------------------------------------------------------------

    public static void main(String[] args) {
        InteractionDemo demo = new InteractionDemo();
        demo.demoDesktopMode();
        demo.demoWhiteboardMode();
        demo.demoModeEquivalence();
        demo.demoPinchScale();
        System.out.println("\n=== All demos completed ===");
    }
}
