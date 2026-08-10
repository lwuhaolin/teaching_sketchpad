package com.geometry.interaction.input;

import com.geometry.interaction.event.InputEvent;
import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Phase 05 - Simulated touch input device.
 *
 * In a real whiteboard deployment, this would be backed by touch hardware
 * (e.g. via JNI or a platform-specific API). For now, it provides a programmatic
 * API to inject touch events for testing and for environments where GLFW does
 * not expose native touch.
 *
 * Supports:
 *   - Single-touch: DOWN → MOVE* → UP (→ DragGesture)
 *   - Multi-touch:  up to 10 simultaneous pointers
 */
public class TouchDevice implements InputDevice {

    private static final int MAX_POINTERS = 10;

    // Per-pointer state
    private final Map<Integer, PointerState> pointerState = new HashMap<>();
    private final List<InputEvent> events;

    public TouchDevice() {
        this.events = new ArrayList<>();
    }

    @Override
    public void update() {
        // TouchDevice state is updated via direct method calls (injectDown, injectMove, etc.)
        // No polling from OS required.
    }

    @Override
    public List<InputEvent> getEvents() {
        List<InputEvent> snapshot = new ArrayList<>(events);
        events.clear();
        return Collections.unmodifiableList(snapshot);
    }

    // ------------------------------------------------------------------
    // Public injection API
    // ------------------------------------------------------------------

    /**
     * Inject a touch DOWN event at the given screen position.
     *
     * @param pointerId unique pointer ID in [0, MAX_POINTERS-1]
     * @param x         screen X in pixels
     * @param y         screen Y in pixels
     */
    public void injectDown(int pointerId, float x, float y) {
        if (pointerId < 0 || pointerId >= MAX_POINTERS) {
            throw new IllegalArgumentException("Pointer ID out of range: " + pointerId);
        }
        Vec2 pos = new Vec2(x, y);
        events.add(new PointerEvent(pointerId, PointerEvent.PointerType.TOUCH,
                pos, Vec2.ZERO, PointerEvent.EventType.DOWN));
        pointerState.put(pointerId, new PointerState(pos, pos));
    }

    /**
     * Inject a touch MOVE event for an already-down pointer.
     *
     * @param pointerId the pointer that moved
     * @param x         new screen X
     * @param y         new screen Y
     */
    public void injectMove(int pointerId, float x, float y) {
        PointerState state = pointerState.get(pointerId);
        if (state == null) {
            return; // Ignore moves for pointers that are not down
        }
        Vec2 newPos = new Vec2(x, y);
        Vec2 delta = newPos.subtract(state.lastPos);
        events.add(new PointerEvent(pointerId, PointerEvent.PointerType.TOUCH,
                newPos, delta, PointerEvent.EventType.MOVE));
        state.lastPos = newPos;
    }

    /**
     * Inject a touch UP event, ending the pointer session.
     *
     * @param pointerId the pointer to release
     */
    public void injectUp(int pointerId) {
        PointerState state = pointerState.remove(pointerId);
        if (state == null) {
            return;
        }
        events.add(new PointerEvent(pointerId, PointerEvent.PointerType.TOUCH,
                state.lastPos, Vec2.ZERO, PointerEvent.EventType.UP));
    }

    /**
     * Get the current position of a pointer, or null if not active.
     */
    public Vec2 getPointerPos(int pointerId) {
        PointerState state = pointerState.get(pointerId);
        return state != null ? state.lastPos : null;
    }

    /**
     * Get the number of currently active (down) pointers.
     */
    public int getActivePointerCount() {
        return pointerState.size();
    }

    /**
     * Get IDs of all currently active pointers.
     */
    public List<Integer> getActivePointerIds() {
        return new ArrayList<>(pointerState.keySet());
    }

    // ------------------------------------------------------------------
    // Internal state
    // ------------------------------------------------------------------

    private static class PointerState {
        final Vec2 downPos;
        Vec2 lastPos;

        PointerState(Vec2 downPos, Vec2 lastPos) {
            this.downPos = downPos;
            this.lastPos = lastPos;
        }
    }
}
