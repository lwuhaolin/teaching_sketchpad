package com.geometry.interaction.input;

import com.geometry.interaction.event.InputEvent;
import com.geometry.interaction.event.PointerEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 05 - Electronic pen input device.
 *
 * Simulates pen input with position and pressure.
 * In a real deployment, pressure data would come from a Wacom or similar
 * tablet API. Here it is exposed for future annotation and drawing features.
 *
 * Only pointer ID 0 is used (single pen tip).
 */
public class PenDevice implements InputDevice {

    private float pressure;
    private boolean pressed;
    private float lastX;
    private float lastY;
    private final List<InputEvent> events;

    public PenDevice() {
        this.pressure = 0f;
        this.pressed = false;
        this.lastX = 0f;
        this.lastY = 0f;
        this.events = new ArrayList<>();
    }

    @Override
    public void update() {
        // State is updated via direct method calls
    }

    @Override
    public List<InputEvent> getEvents() {
        List<InputEvent> snapshot = new ArrayList<>(events);
        events.clear();
        return Collections.unmodifiableList(snapshot);
    }

    // ------------------------------------------------------------------
    // Injection API
    // ------------------------------------------------------------------

    /**
     * Inject a pen DOWN event.
     *
     * @param x        screen X in pixels
     * @param y        screen Y in pixels
     * @param pressure pressure value in [0, 1]
     */
    public void injectDown(float x, float y, float pressure) {
        this.pressure = Math.max(0f, Math.min(1f, pressure));
        this.pressed = true;
        this.lastX = x;
        this.lastY = y;
        events.add(new PointerEvent(
                0, PointerEvent.PointerType.PEN,
                new com.geometry.interaction.event.Vec2(x, y),
                com.geometry.interaction.event.Vec2.ZERO,
                PointerEvent.EventType.DOWN,
                this.pressure
        ));
    }

    /**
     * Inject a pen MOVE event (only when pressed).
     *
     * @param x new screen X
     * @param y new screen Y
     */
    public void injectMove(float x, float y) {
        if (!pressed) {
            return;
        }
        float dx = x - lastX;
        float dy = y - lastY;
        this.lastX = x;
        this.lastY = y;
        events.add(new PointerEvent(
                0, PointerEvent.PointerType.PEN,
                new com.geometry.interaction.event.Vec2(x, y),
                new com.geometry.interaction.event.Vec2(dx, dy),
                PointerEvent.EventType.MOVE,
                pressure
        ));
    }

    /**
     * Inject a pen UP event.
     */
    public void injectUp() {
        if (!pressed) {
            return;
        }
        this.pressed = false;
        events.add(new PointerEvent(
                0, PointerEvent.PointerType.PEN,
                new com.geometry.interaction.event.Vec2(lastX, lastY),
                com.geometry.interaction.event.Vec2.ZERO,
                PointerEvent.EventType.UP,
                pressure
        ));
    }

    public float getPressure() {
        return pressure;
    }

    public boolean isPressed() {
        return pressed;
    }

    public com.geometry.interaction.event.Vec2 getPosition() {
        return new com.geometry.interaction.event.Vec2(lastX, lastY);
    }
}
