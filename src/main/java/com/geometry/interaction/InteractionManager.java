package com.geometry.interaction;

import com.geometry.interaction.action.*;
import com.geometry.interaction.event.GestureEvent;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.StrokeGestureEvent;
import com.geometry.interaction.gesture.GestureRecognizer;
import com.geometry.interaction.gesture.StrokeGestureRecognizer;
import com.geometry.interaction.input.InputDevice;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 05 - Central interaction manager.
 *
 * Orchestrates the full interaction pipeline:
 *
 *   InputDevice.update()
 *       → InputDevice.getEvents()  (List&lt;PointerEvent&gt;)
 *       → GestureRecognizer.process()  (List&lt;GestureEvent&gt;)
 *       → InteractionManager → List&lt;Action&gt;
 *       → Action.execute()  (modifies SceneObject / Scene)
 *
 * Key design decisions:
 *   - Tools do NOT receive raw pointer events; they receive Actions.
 *   - The input device type (mouse vs touch vs pen) is invisible to Actions.
 *   - Both mouse drag and touch drag produce the same MoveAction.
 *   - Ray picking is used for selection in both 2D and 3D modes.
 *
 * Ray picking strategy:
 *   For each visible SceneObject, compute its axis-aligned bounding box in
 *   world space, transform it by the inverse view-projection matrix to get
 *   a screen-space AABB, then test whether the click point falls inside.
 *   The closest object (by z-depth) wins.
 */
public class InteractionManager {

    private final Scene scene;
    private final List<InputDevice> inputDevices;
    private final GestureRecognizer gestureRecognizer;
    private final StrokeGestureRecognizer strokeGestureRecognizer;
    private InteractionMode mode;

    // Viewport dimensions (set by the renderer each frame)
    private int viewportWidth;
    private int viewportHeight;

    /** Scale factor: pixels per world unit (used in 2D mode for picking). */
    private float pixelsPerWorldUnit;

    /**
     * Create an InteractionManager for the given Scene.
     *
     * @param scene the Scene to interact with
     */
    public InteractionManager(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
        this.inputDevices = new ArrayList<>();
        this.gestureRecognizer = new GestureRecognizer();
        this.strokeGestureRecognizer = new StrokeGestureRecognizer();
        this.mode = InteractionMode.DESKTOP;
        this.viewportWidth = 800;
        this.viewportHeight = 600;
        this.pixelsPerWorldUnit = 40f; // default for 2D mode
    }

    // ------------------------------------------------------------------
    // Device registration
    // ------------------------------------------------------------------

    /**
     * Register an input device. Devices are polled in registration order.
     *
     * @param device the input device to register
     */
    public void registerDevice(InputDevice device) {
        if (device == null) {
            throw new IllegalArgumentException("InputDevice cannot be null");
        }
        inputDevices.add(device);
    }

    /**
     * Remove a previously registered input device.
     */
    public void unregisterDevice(InputDevice device) {
        inputDevices.remove(device);
    }

    /**
     * Get all registered input devices.
     */
    public List<InputDevice> getInputDevices() {
        return Collections.unmodifiableList(inputDevices);
    }

    // ------------------------------------------------------------------
    // Mode
    // ------------------------------------------------------------------

    /**
     * Get the current interaction mode.
     */
    public InteractionMode getMode() {
        return mode;
    }

    /**
     * Set the interaction mode.
     *
     * @param mode DESKTOP or WHITEBOARD
     */
    public void setMode(InteractionMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("InteractionMode cannot be null");
        }
        this.mode = mode;
    }

    // ------------------------------------------------------------------
    // Viewport
    // ------------------------------------------------------------------

    /**
     * Set the viewport dimensions. Must be called each frame after resize.
     */
    public void setViewport(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Viewport dimensions must be positive");
        }
        this.viewportWidth = width;
        this.viewportHeight = height;
    }

    public int getViewportWidth() {
        return viewportWidth;
    }

    public int getViewportHeight() {
        return viewportHeight;
    }

    /**
     * Set the pixels-per-world-unit ratio for 2D picking.
     */
    public void setPixelsPerWorldUnit(float ratio) {
        if (ratio <= 0) {
            throw new IllegalArgumentException("pixelsPerWorldUnit must be positive");
        }
        this.pixelsPerWorldUnit = ratio;
    }

    public float getPixelsPerWorldUnit() {
        return pixelsPerWorldUnit;
    }

    // ------------------------------------------------------------------
    // Main update loop — called once per frame
    // ------------------------------------------------------------------

    /**
     * Process all input this frame and execute resulting actions.
     *
     * Pipeline:
     *   1. Poll all input devices
     *   2. Separate pointer events by type:
     *      - PEN events → StrokeGestureRecognizer (shape recognition)
     *      - Other events → GestureRecognizer (drag/pinch/rotate)
     *   3. Convert gestures to actions based on current mode
     *   4. Execute actions
     */
    public void update() {
        // 1. Poll all devices
        for (InputDevice device : inputDevices) {
            device.update();
        }

        // 2. Collect all pointer events, separating pen from others
        List<PointerEvent> allPointerEvents = new ArrayList<>();
        List<PointerEvent> penEvents = new ArrayList<>();
        List<PointerEvent> nonPenEvents = new ArrayList<>();
        for (com.geometry.interaction.event.InputEvent e : inputEvents()) {
            if (e instanceof PointerEvent) {
                PointerEvent pe = (PointerEvent) e;
                allPointerEvents.add(pe);
                if (pe.getPointerType() == PointerEvent.PointerType.PEN) {
                    penEvents.add(pe);
                } else {
                    nonPenEvents.add(pe);
                }
            }
        }

        if (allPointerEvents.isEmpty()) {
            return;
        }

        // 3. Process pen strokes through stroke recognizer
        List<StrokeGestureEvent> strokeEvents =
                strokeGestureRecognizer.process(penEvents);

        // 4. Process non-pen gestures through standard gesture recognizer
        List<GestureEvent> gestures =
                !nonPenEvents.isEmpty() ? gestureRecognizer.process(nonPenEvents) : new ArrayList<>();

        // 5. Execute stroke gesture events (they are Actions)
        for (StrokeGestureEvent strokeEvent : strokeEvents) {
            strokeEvent.execute();
        }

        // 6. Convert gestures to actions and execute
        for (GestureEvent gesture : gestures) {
            List<Action> actions = convertGestureToActions(gesture, allPointerEvents);
            for (Action action : actions) {
                action.execute();
            }
        }
    }

    /**
     * Collect all events from all registered input devices.
     */
    private List<com.geometry.interaction.event.InputEvent> inputEvents() {
        List<com.geometry.interaction.event.InputEvent> all = new ArrayList<>();
        for (InputDevice device : inputDevices) {
            for (com.geometry.interaction.event.InputEvent e : device.getEvents()) {
                all.add(e);
            }
        }
        return all;
    }

    // ------------------------------------------------------------------
    // Gesture-to-Action conversion
    // ------------------------------------------------------------------

    /**
     * Convert a recognised gesture into one or more Actions.
     *
     * The conversion depends on the current InteractionMode.
     */
    public List<Action> convertGestureToActions(GestureEvent gesture,
                                                  List<PointerEvent> sourceEvents) {
        List<Action> actions = new ArrayList<>();

        switch (gesture.getGestureType()) {
            case TAP:
                actions.addAll(onTap(gesture.getOrigin(), sourceEvents));
                break;
            case DRAG:
                actions.addAll(onDrag(gesture, sourceEvents));
                break;
            case PINCH:
                actions.addAll(onPinch(gesture));
                break;
            case ROTATE:
                actions.addAll(onRotate(gesture));
                break;
        }
        return actions;
    }

    /**
     * Handle a tap: select the object under the tap point.
     */
    private List<Action> onTap(com.geometry.interaction.event.Vec2 screenPos,
                                List<PointerEvent> sourceEvents) {
        List<Action> actions = new ArrayList<>();

        // In whiteboard mode, a tap also starts a potential drag
        if (mode == InteractionMode.WHITEBOARD) {
            // Find object under tap
            SceneObject picked = pickObjectAt(screenPos);
            if (picked != null) {
                actions.add(new SelectAction(scene, picked, true));
            } else {
                scene.clearSelection();
                actions.add(new SelectAction(scene, null));
            }
        } else {
            // Desktop mode: left-click selects
            SceneObject picked = pickObjectAt(screenPos);
            if (picked != null) {
                actions.add(new SelectAction(scene, picked, true));
            } else {
                scene.clearSelection();
                actions.add(new SelectAction(scene, null));
            }
        }
        return actions;
    }

    /**
     * Handle a drag: move the selected object.
     */
    private List<Action> onDrag(GestureEvent gesture,
                                 List<PointerEvent> sourceEvents) {
        List<Action> actions = new ArrayList<>();
        SceneObject selected = scene.getSelected();

        if (selected == null) {
            return actions;
        }

        // Convert screen-space drag distance to world-space movement
        float worldDeltaX = gesture.getDistance() / pixelsPerWorldUnit;
        float worldDeltaY = gesture.getDistance() / pixelsPerWorldUnit;

        // Determine drag direction from source events (last MOVE event)
        PointerEvent lastMove = null;
        for (int i = sourceEvents.size() - 1; i >= 0; i--) {
            if (sourceEvents.get(i).isMove()) {
                lastMove = sourceEvents.get(i);
                break;
            }
        }

        if (lastMove != null) {
            com.geometry.interaction.event.Vec2 delta = lastMove.getDelta();
            worldDeltaX = delta.x / pixelsPerWorldUnit;
            worldDeltaY = -delta.y / pixelsPerWorldUnit; // flip Y for screen coords
        }

        actions.add(new MoveAction(selected, worldDeltaX, worldDeltaY));
        return actions;
    }

    /**
     * Handle a pinch: scale the selected object.
     */
    private List<Action> onPinch(GestureEvent gesture) {
        List<Action> actions = new ArrayList<>();
        SceneObject selected = scene.getSelected();

        if (selected == null) {
            return actions;
        }

        actions.add(new ScaleAction(selected, gesture.getScaleFactor()));
        return actions;
    }

    /**
     * Handle a rotation gesture: rotate the selected object.
     */
    private List<Action> onRotate(GestureEvent gesture) {
        List<Action> actions = new ArrayList<>();
        SceneObject selected = scene.getSelected();

        if (selected == null) {
            return actions;
        }

        actions.add(new RotateAction(selected, gesture.getAngleDegrees()));
        return actions;
    }

    // ------------------------------------------------------------------
    // Ray picking
    // ------------------------------------------------------------------

    /**
     * Pick the top-most visible SceneObject at the given screen position.
     *
     * Uses bounding-box overlap test in screen space.
     *
     * @param screenPos pixel coordinates (origin top-left)
     * @return the picked SceneObject, or null if nothing was hit
     */
    public SceneObject pickObjectAt(com.geometry.interaction.event.Vec2 screenPos) {
        if (screenPos == null) {
            return null;
        }

        SceneObject closest = null;
        float closestDepth = Float.MAX_VALUE;

        for (SceneObject obj : scene.getAllObjects()) {
            if (!obj.isVisible()) {
                continue;
            }

            float[] bbox = getWorldBoundingBox(obj);
            if (bbox == null) {
                continue;
            }

            // Transform bbox corners to screen space and test overlap
            com.geometry.interaction.event.Vec2 screenBbox =
                    worldToScreen(bbox, viewportWidth, viewportHeight);

            if (screenBbox != null && isPointInScreenBbox(screenPos, screenBbox)) {
                // Use average Z of bbox as depth proxy
                float avgDepth = (bbox[4] + bbox[5] + bbox[6] + bbox[7]) / 4f;
                if (avgDepth < closestDepth) {
                    closestDepth = avgDepth;
                    closest = obj;
                }
            }
        }

        return closest;
    }

    /**
     * Get the axis-aligned bounding box of a SceneObject in world space.
     * Returns [minX, minY, minZ, maxX, maxY, maxZ].
     */
    private float[] getWorldBoundingBox(SceneObject obj) {
        com.geometry.core.mesh.Mesh mesh = obj.getGeometry().getMesh();
        if (mesh == null || mesh.isEmpty()) {
            return null;
        }

        com.geometry.core.transform.Transform transform = obj.getEffectiveTransform();
        com.geometry.core.math.Vec3 pos = transform.getPosition();
        com.geometry.core.math.Vec3 scale = transform.getScale();
        com.geometry.core.math.Vec3 rot = transform.getRotation();

        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
        float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

        for (com.geometry.core.mesh.Vertex v : mesh.getVertices()) {
            com.geometry.core.math.Vec3 worldPos = v.getPosition();
            // Apply scale (component-wise)
            worldPos = new com.geometry.core.math.Vec3(
                    worldPos.x * scale.x,
                    worldPos.y * scale.y,
                    worldPos.z * scale.z);
            // Apply rotation (simplified: only Z rotation for 2D, full for 3D)
            worldPos = rotatePoint(worldPos, rot);
            // Apply translation
            worldPos = worldPos.add(pos);

            minX = Math.min(minX, worldPos.x);
            minY = Math.min(minY, worldPos.y);
            minZ = Math.min(minZ, worldPos.z);
            maxX = Math.max(maxX, worldPos.x);
            maxY = Math.max(maxY, worldPos.y);
            maxZ = Math.max(maxZ, worldPos.z);
        }

        return new float[]{minX, minY, minZ, maxX, maxY, maxZ};
    }

    /**
     * Rotate a point by Euler angles (degrees).
     * Simplified rotation around Y axis for basic picking.
     */
    private com.geometry.core.math.Vec3 rotatePoint(com.geometry.core.math.Vec3 p,
                                                     com.geometry.core.math.Vec3 rotDeg) {
        float radY = (float) Math.toRadians(rotDeg.y);
        float cosY = (float) Math.cos(radY);
        float sinY = (float) Math.sin(radY);
        float x = p.x * cosY + p.z * sinY;
        float z = -p.x * sinY + p.z * cosY;
        float y = p.y;
        return new com.geometry.core.math.Vec3(x, y, z);
    }

    /**
     * Transform a world-space AABB to screen-space bounds.
     * Returns [screenMinX, screenMinY, screenMinZ, screenMaxX, screenMaxY, screenMaxZ]
     * or null if the object is outside the frustum.
     */
    private com.geometry.interaction.event.Vec2 worldToScreen(float[] bbox,
                                                               int width, int height) {
        // Simple orthographic projection for picking
        // World origin is at viewport centre
        float cx = width / 2f;
        float cy = height / 2f;

        float worldRangeX = viewportWidth / (2f * pixelsPerWorldUnit);
        float worldRangeY = viewportHeight / (2f * pixelsPerWorldUnit);

        float screenMinX = cx + (bbox[0] / worldRangeX) * cx;
        float screenMinY = cy - (bbox[1] / worldRangeY) * cy;
        float screenMaxX = cx + (bbox[3] / worldRangeX) * cx;
        float screenMaxY = cy - (bbox[4] / worldRangeY) * cy;

        // Return the diagonal representing the bbox extent
        return new com.geometry.interaction.event.Vec2(screenMaxX - screenMinX,
                screenMaxY - screenMinY);
    }

    /**
     * Check if a point is within a screen-space bounding box.
     */
    private boolean isPointInScreenBbox(com.geometry.interaction.event.Vec2 point,
                                         com.geometry.interaction.event.Vec2 bbox) {
        // bbox represents size from origin; check if point is within bounds
        float cx = viewportWidth / 2f;
        float cy = viewportHeight / 2f;
        return Math.abs(point.x - cx) <= bbox.x / 2f
                && Math.abs(point.y - cy) <= bbox.y / 2f;
    }

    // ------------------------------------------------------------------
    // Keyboard shortcuts
    // ------------------------------------------------------------------

    /**
     * Process keyboard events and execute corresponding actions.
     * Called separately from the main gesture pipeline.
     */
    public void processKeyboardEvents(
            List<com.geometry.interaction.input.KeyboardDevice.KeyboardEvent> events) {
        for (com.geometry.interaction.input.KeyboardDevice.KeyboardEvent e : events) {
            if (e.getEventType() != com.geometry.interaction.input.KeyboardDevice.KeyboardEvent.EventType.DOWN) {
                continue;
            }
            com.geometry.interaction.input.KeyboardDevice.Key key = e.getKey();
            switch (key) {
                case DELETE:
                    SceneObject selected = scene.getSelected();
                    if (selected != null) {
                        scene.removeObject(selected);
                    }
                    break;
                case ESCAPE:
                    scene.clearSelection();
                    break;
                case SPACE:
                    // Toggle render mode between 2D and 3D
                    toggleRenderMode();
                    break;
            }
        }
    }

    /**
     * Toggle between 2D and 3D render mode.
     * Notifies the renderer if one is available.
     */
    public void toggleRenderMode() {
        // This is a placeholder; the actual renderer toggle is done via
        // the application layer. Here we just log the intent.
        // Phase 06 tools will handle this more directly.
    }

    /**
     * Get the currently selected SceneObject (convenience accessor).
     */
    public SceneObject getSelectedObject() {
        return scene.getSelected();
    }

    /**
     * Get the current interaction mode as a string.
     */
    public String getModeString() {
        return mode.name();
    }

    // ------------------------------------------------------------------
    // Stroke gesture recognition
    // ------------------------------------------------------------------

    /**
     * Get the stroke gesture recognizer used for pen-based shape recognition.
     */
    public StrokeGestureRecognizer getStrokeGestureRecognizer() {
        return strokeGestureRecognizer;
    }

    /**
     * Set a custom stroke gesture recognizer.
     *
     * @param recognizer the recognizer to use (must not be null)
     */
    public void setStrokeGestureRecognizer(StrokeGestureRecognizer recognizer) {
        if (recognizer == null) {
            throw new IllegalArgumentException("StrokeGestureRecognizer cannot be null");
        }
        this.strokeGestureRecognizer.reset();
    }
}
