package com.geometry.interaction.input;

import com.geometry.interaction.event.InputEvent;
import com.geometry.interaction.event.PointerEvent;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Phase 05 - Mouse input device using GLFW callbacks.
 *
 * Collects mouse button, motion, and scroll events and translates them into
 * PointerEvents. Buttons 0 (left), 1 (right), 2 (middle) map to pointer IDs
 * 0, 1, 2 respectively.
 *
 * GLFW is always available in this engine (used by the renderer), so this
 * device can read directly from GLFW without platform-specific code.
 */
public class MouseDevice implements InputDevice {

    private final long glfwWindow;

    // State
    private double lastCursorX = 0;
    private double lastCursorY = 0;
    private final List<PointerEvent> events;

    // Button state tracking
    private boolean[] buttonDown = new boolean[3];
    private boolean[] buttonJustPressed = new boolean[3];
    private boolean[] buttonJustReleased = new boolean[3];

    public MouseDevice(long glfwWindow) {
        if (glfwWindow == 0L) {
            throw new IllegalArgumentException("GLFW window handle cannot be zero");
        }
        this.glfwWindow = glfwWindow;
        this.events = new ArrayList<>();
        setupCallbacks();
    }

    private void setupCallbacks() {
        // Cursor pos callback
        GLFWCursorPosCallbackI callback = (window, xpos, ypos) -> {
            double dx = xpos - lastCursorX;
            double dy = ypos - lastCursorY;
            if (dx != 0 || dy != 0) {
                events.add(new PointerEvent(
                        0,
                        PointerEvent.PointerType.MOUSE,
                        new com.geometry.interaction.event.Vec2((float) xpos, (float) ypos),
                        new com.geometry.interaction.event.Vec2((float) dx, (float) -dy),
                        PointerEvent.EventType.MOVE
                ));
            }
            lastCursorX = xpos;
            lastCursorY = ypos;
        };
        glfwSetCursorPosCallback(glfwWindow, callback);

        // Mouse button callback
        GLFWMouseButtonCallbackI buttonCallback = (window, button, action, mods) -> {
            int pointerId = Math.min(button, 2);
            if (action == GLFW_PRESS) {
                if (!buttonDown[pointerId]) {
                    buttonJustPressed[pointerId] = true;
                    events.add(new PointerEvent(
                            pointerId,
                            PointerEvent.PointerType.MOUSE,
                            new com.geometry.interaction.event.Vec2((float) lastCursorX, (float) lastCursorY),
                            com.geometry.interaction.event.Vec2.ZERO,
                            PointerEvent.EventType.DOWN
                    ));
                }
                buttonDown[pointerId] = true;
            } else if (action == GLFW_RELEASE) {
                if (buttonDown[pointerId]) {
                    buttonJustReleased[pointerId] = true;
                    events.add(new PointerEvent(
                            pointerId,
                            PointerEvent.PointerType.MOUSE,
                            new com.geometry.interaction.event.Vec2((float) lastCursorX, (float) lastCursorY),
                            com.geometry.interaction.event.Vec2.ZERO,
                            PointerEvent.EventType.UP
                    ));
                    // If it was a quick press+release with no move, treat as click
                    if (!buttonJustPressed[pointerId]) {
                        events.add(new PointerEvent(
                                pointerId,
                                PointerEvent.PointerType.MOUSE,
                                new com.geometry.interaction.event.Vec2((float) lastCursorX, (float) lastCursorY),
                                com.geometry.interaction.event.Vec2.ZERO,
                                PointerEvent.EventType.CLICK
                        ));
                    }
                }
                buttonDown[pointerId] = false;
            }
        };
        glfwSetMouseButtonCallback(glfwWindow, buttonCallback);

        // Scroll callback
        GLFWScrollCallbackI scrollCallback = (window, xoffset, yoffset) -> {
            events.add(new PointerEvent(
                    -1, // scroll wheel has no pointer ID
                    PointerEvent.PointerType.MOUSE,
                    new com.geometry.interaction.event.Vec2((float) lastCursorX, (float) lastCursorY),
                    new com.geometry.interaction.event.Vec2((float) xoffset, (float) yoffset),
                    PointerEvent.EventType.MOVE
            ));
        };
        glfwSetScrollCallback(glfwWindow, scrollCallback);
    }

    @Override
    public void update() {
        // Reset just-pressed/just-released flags
        for (int i = 0; i < buttonJustPressed.length; i++) {
            buttonJustPressed[i] = false;
            buttonJustReleased[i] = false;
        }
    }

    @Override
    public List<InputEvent> getEvents() {
        List<InputEvent> snapshot = new ArrayList<>(events);
        events.clear();
        return Collections.unmodifiableList(snapshot);
    }

    /**
     * Get the current mouse position in screen pixels.
     */
    public com.geometry.interaction.event.Vec2 getPosition() {
        double[] x = new double[1];
        double[] y = new double[1];
        glfwGetCursorPos(glfwWindow, x, y);
        return new com.geometry.interaction.event.Vec2((float) x[0], (float) y[0]);
    }

    /**
     * Check if a mouse button is currently held down.
     *
     * @param buttonIndex 0 = left, 1 = right, 2 = middle
     */
    public boolean isButtonPressed(int buttonIndex) {
        if (buttonIndex < 0 || buttonIndex >= buttonDown.length) {
            return false;
        }
        return buttonDown[buttonIndex];
    }

    /**
     * Check if a mouse button was just pressed this frame.
     */
    public boolean isButtonJustPressed(int buttonIndex) {
        if (buttonIndex < 0 || buttonIndex >= buttonJustPressed.length) {
            return false;
        }
        return buttonJustPressed[buttonIndex];
    }
}
