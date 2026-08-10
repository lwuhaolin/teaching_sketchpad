package com.geometry.interaction.action;

import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

/**
 * Phase 06 - Action to delete a SceneObject from the Scene.
 *
 * Created by DeleteTool or keyboard shortcut (Delete key).
 */
public class DeleteAction implements Action {

    private final SceneObject target;
    private final Scene scene;

    public DeleteAction(Scene scene, SceneObject target) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
        this.target = target;
    }

    @Override
    public void execute() {
        if (target != null) {
            scene.removeObject(target);
        }
    }

    @Override
    public String getDescription() {
        return "DeleteAction{target=" + (target != null ? target.getId() : "null") + "}";
    }

    public SceneObject getTarget() {
        return target;
    }

    public Scene getScene() {
        return scene;
    }
}
