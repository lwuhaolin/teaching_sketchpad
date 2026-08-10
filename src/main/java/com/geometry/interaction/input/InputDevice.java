package com.geometry.interaction.input;

import com.geometry.interaction.event.InputEvent;

import java.util.List;

/**
 * Phase 05 - Interface for all input devices in the interaction system.
 *
 * Each concrete device (MouseDevice, TouchDevice, etc.) implements this interface
 * to normalise raw OS input into a stream of InputEvent objects.
 *
 * The InteractionManager polls all registered devices each frame via update().
 * Events are accumulated and then fed to the gesture recogniser.
 */
public interface InputDevice {

    /**
     * Poll the device for new input and update internal state.
     * Should be called once per frame before getEvents().
     */
    void update();

    /**
     * Return all accumulated events since the last update() call.
     * The list is cleared after this call.
     *
     * @return list of input events, may be empty but never null
     */
    List<InputEvent> getEvents();
}
