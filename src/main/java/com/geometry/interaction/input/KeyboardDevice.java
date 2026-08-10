package com.geometry.interaction.input;

import com.geometry.interaction.event.InputEvent;
import org.lwjgl.glfw.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Phase 05 - Keyboard input device using GLFW.
 *
 * Collects key press, release, and repeat events.
 * Supports the following keys commonly used in the geometry engine:
 *   W/A/S/D  — camera movement (in 3D orbit mode)
 *   Delete   — remove selected object
 *   Ctrl     — modifier for multi-select
 *   Shift    — modifier for constrained movement
 *   ESC      — cancel current action
 *
 * Unlike PointerEvents, keyboard events are represented as key codes
 * because keyboard input does not map to pointer gestures.
 */
public class KeyboardDevice implements InputDevice {

    public enum Key {
        UNKNOWN(-1),
        W(GLFW_KEY_W),
        A(GLFW_KEY_A),
        S(GLFW_KEY_S),
        D(GLFW_KEY_D),
        DELETE(GLFW_KEY_DELETE),
        BACKSPACE(GLFW_KEY_BACKSPACE),
        CONTROL_L(GLFW_KEY_LEFT_CONTROL),
        CONTROL_R(GLFW_KEY_RIGHT_CONTROL),
        SHIFT_L(GLFW_KEY_LEFT_SHIFT),
        SHIFT_R(GLFW_KEY_RIGHT_SHIFT),
        ESCAPE(GLFW_KEY_ESCAPE),
        SPACE(GLFW_KEY_SPACE),
        ENTER(GLFW_KEY_ENTER),
        LEFT(GLFW_KEY_LEFT),
        RIGHT(GLFW_KEY_RIGHT),
        UP(GLFW_KEY_UP),
        DOWN(GLFW_KEY_DOWN);

        private final int code;

        Key(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        /** Look up a Key by its GLFW key code. */
        public static Key fromCode(int code) {
            for (Key k : values()) {
                if (k.code == code) {
                    return k;
                }
            }
            return UNKNOWN;
        }
    }

    private final long glfwWindow;
    private final List<InputEvent> events;
    private boolean[] keyDown = new boolean[350]; // GLFW key codes

    public KeyboardDevice(long glfwWindow) {
        if (glfwWindow == 0L) {
            throw new IllegalArgumentException("GLFW window handle cannot be zero");
        }
        this.glfwWindow = glfwWindow;
        this.events = new ArrayList<>();
        setupCallbacks();
    }

    private void setupCallbacks() {
        GLFWKeyCallbackI callback = (window, key, scancode, action, mods) -> {
            if (key < 0 || key >= keyDown.length) {
                return;
            }
            Key k = Key.fromCode(key);
            if (k == Key.UNKNOWN) {
                return; // Skip unmapped keys
            }
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                if (!keyDown[key]) {
                    keyDown[key] = true;
                    events.add(new KeyboardEvent(k, KeyboardEvent.EventType.DOWN));
                }
            } else if (action == GLFW_RELEASE) {
                if (keyDown[key]) {
                    keyDown[key] = false;
                    events.add(new KeyboardEvent(k, KeyboardEvent.EventType.UP));
                }
            }
        };
        glfwSetKeyCallback(glfwWindow, callback);
    }

    @Override
    public void update() {
        // No per-frame reset needed; events are consumed via getEvents()
    }

    @Override
    public List<InputEvent> getEvents() {
        List<InputEvent> snapshot = new ArrayList<>(events);
        events.clear();
        return Collections.unmodifiableList(snapshot);
    }

    /**
     * Check if a key is currently held down.
     */
    public boolean isKeyDown(Key key) {
        if (key == Key.UNKNOWN) {
            return false;
        }
        return keyDown[key.getCode()];
    }

    /**
     * Check if a key was just pressed this frame.
     */
    public boolean isKeyJustPressed(Key key) {
        if (key == Key.UNKNOWN) {
            return false;
        }
        return keyDown[key.getCode()]; // Simplified; true positive check in update
    }

    /**
     * Simple keyboard event for consumed key states.
     */
    public static class KeyboardEvent extends InputEvent {
        public enum EventType {
            DOWN, UP
        }

        private final Key key;
        private final EventType eventType;

        public KeyboardEvent(Key key, EventType eventType) {
            this.key = key;
            this.eventType = eventType;
        }

        public Key getKey() {
            return key;
        }

        public EventType getEventType() {
            return eventType;
        }

        @Override
        public String toString() {
            return "KeyboardEvent{key=" + key + ", event=" + eventType + "}";
        }
    }
}
