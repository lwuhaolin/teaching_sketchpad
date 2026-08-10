package com.geometry.ui.canvas;

import com.geometry.interaction.InteractionManager;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

/**
 * Phase 11 - Canvas interaction layer for whiteboard/touch input.
 *
 * This class handles touch and pen interactions on the OpenGL canvas.
 * It translates touch events into scene interactions without modifying
 * the geometry directly.
 *
 * Responsibilities:
 *   - Forward touch events to the InteractionManager
 *   - Provide selection feedback (highlight picked objects)
 *   - Track touch position for gestures
 *
 * Does NOT:
 *   - Modify geometry meshes
 *   - Handle rendering
 *   - Manage tool state
 *
 * Not thread-safe.
 */
public class CanvasInteractionLayer {

    /** The scene for object queries. */
    private final Scene scene;

    /** The interaction manager for event processing. */
    private final InteractionManager interactionManager;

    /** The last known touch position in screen pixels. */
    private Vec2 lastTouchPosition;

    /** The last known pen pressure value. */
    private float lastPressure;

    /**
     * Create a CanvasInteractionLayer.
     *
     * @param scene                the scene to interact with
     * @param interactionManager   the interaction manager
     */
    public CanvasInteractionLayer(Scene scene, InteractionManager interactionManager) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
        this.interactionManager = interactionManager;
        this.lastTouchPosition = null;
        this.lastPressure = 0f;
    }

    // ------------------------------------------------------------------
    // Touch events
    // ------------------------------------------------------------------

    /**
     * Process a touch DOWN event.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onTouchDown(int x, int y) {
        this.lastTouchPosition = new Vec2(x, y);
        this.lastPressure = 1.0f;

        if (interactionManager != null) {
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.TOUCH,
                    new Vec2(x, y),
                    Vec2.ZERO,
                    PointerEvent.EventType.DOWN
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    /**
     * Process a touch MOVE event.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onTouchMove(int x, int y) {
        Vec2 prevPos = lastTouchPosition != null ? lastTouchPosition : new Vec2(x, y);
        this.lastTouchPosition = new Vec2(x, y);

        if (interactionManager != null) {
            Vec2 delta = new Vec2(x - prevPos.x, y - prevPos.y);
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.TOUCH,
                    new Vec2(x, y),
                    delta,
                    PointerEvent.EventType.MOVE,
                    lastPressure
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    /**
     * Process a touch UP event.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onTouchUp(int x, int y) {
        Vec2 prevPos = lastTouchPosition != null ? lastTouchPosition : new Vec2(x, y);
        this.lastTouchPosition = null;

        if (interactionManager != null) {
            Vec2 delta = new Vec2(x - prevPos.x, y - prevPos.y);
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.TOUCH,
                    new Vec2(x, y),
                    delta,
                    PointerEvent.EventType.UP,
                    lastPressure
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    // ------------------------------------------------------------------
    // Pen events
    // ------------------------------------------------------------------

    /**
     * Process a pen DOWN event.
     *
     * @param x       x coordinate in screen pixels
     * @param y       y coordinate in screen pixels
     * @param pressure pressure value in [0, 1]
     */
    public void onPenDown(int x, int y, float pressure) {
        this.lastTouchPosition = new Vec2(x, y);
        this.lastPressure = Math.max(0f, Math.min(1f, pressure));

        if (interactionManager != null) {
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.PEN,
                    new Vec2(x, y),
                    Vec2.ZERO,
                    PointerEvent.EventType.DOWN,
                    this.lastPressure
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    /**
     * Process a pen MOVE event.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onPenMove(int x, int y) {
        Vec2 prevPos = lastTouchPosition != null ? lastTouchPosition : new Vec2(x, y);
        this.lastTouchPosition = new Vec2(x, y);

        if (interactionManager != null) {
            Vec2 delta = new Vec2(x - prevPos.x, y - prevPos.y);
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.PEN,
                    new Vec2(x, y),
                    delta,
                    PointerEvent.EventType.MOVE,
                    lastPressure
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    /**
     * Process a pen UP event.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onPenUp(int x, int y) {
        Vec2 prevPos = lastTouchPosition != null ? lastTouchPosition : new Vec2(x, y);
        this.lastTouchPosition = null;

        if (interactionManager != null) {
            Vec2 delta = new Vec2(x - prevPos.x, y - prevPos.y);
            PointerEvent event = new PointerEvent(
                    0,
                    PointerEvent.PointerType.PEN,
                    new Vec2(x, y),
                    delta,
                    PointerEvent.EventType.UP,
                    lastPressure
            );
            interactionManager.getStrokeGestureRecognizer().process(event);
        }
    }

    // ------------------------------------------------------------------
    // Mouse events
    // Mouse events are handled by MouseDevice → InteractionManager.update()
    // This method is kept as a hook for custom mouse handling if needed.
    // ------------------------------------------------------------------

    /**
     * Process a mouse DOWN event.
     * Note: Standard mouse events flow through MouseDevice, not this layer.
     * This is a hook for custom handling.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onMouseDown(int x, int y) {
        this.lastTouchPosition = new Vec2(x, y);
    }

    /**
     * Process a mouse MOVE event.
     * Note: Standard mouse events flow through MouseDevice, not this layer.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onMouseMove(int x, int y) {
        Vec2 prevPos = lastTouchPosition != null ? lastTouchPosition : new Vec2(x, y);
        this.lastTouchPosition = new Vec2(x, y);
    }

    /**
     * Process a mouse UP event.
     * Note: Standard mouse events flow through MouseDevice, not this layer.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     */
    public void onMouseUp(int x, int y) {
        this.lastTouchPosition = null;
    }

    // ------------------------------------------------------------------
    // Selection feedback
    // ------------------------------------------------------------------

    /**
     * Get the object under the given screen position, if any.
     *
     * @param x x coordinate in screen pixels
     * @param y y coordinate in screen pixels
     * @return the picked SceneObject, or null
     */
    public SceneObject pickObjectAt(int x, int y) {
        if (interactionManager == null) {
            return null;
        }
        return interactionManager.pickObjectAt(new Vec2(x, y));
    }

    /**
     * Get the last known touch position.
     */
    public Vec2 getLastTouchPosition() {
        return lastTouchPosition;
    }

    /**
     * Get the last known pen pressure.
     */
    public float getLastPressure() {
        return lastPressure;
    }
}
