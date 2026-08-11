package com.geometry.ui.bridge;

/**
 * Application-side command handler for UI events that need teaching or
 * animation services.  UI components submit events; they never invoke core
 * managers directly.
 */
public interface UICommandHandler {

    void handleTeachingControl(String action);

    void handleAnimationControl(String action);
}
