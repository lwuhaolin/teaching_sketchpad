package com.geometry.interaction.action;

/**
 * Phase 05 - Base interface for all interaction actions.
 *
 * Actions are the intermediate representation between gestures and scene
 * modifications. A GestureRecognizer produces GestureEvents, which the
 * InteractionManager translates into Actions. Tools receive Actions (not
 * raw events), so they are input-device agnostic.
 *
 * Action types:
 *   - SelectAction:  select a SceneObject
 *   - MoveAction:    translate a SceneObject
 *   - RotateAction:  rotate a SceneObject or the camera
 *   - ScaleAction:   scale a SceneObject or the camera
 *
 * Concrete implementations are in the action sub-package (Phase 06 will
 * introduce Tool classes that consume these Actions).
 */
public interface Action {

    /**
     * Execute this action. The action should modify the appropriate SceneObject
     * or Camera state.
     */
    void execute();

    /**
     * Get a human-readable description of this action.
     */
    String getDescription();
}
