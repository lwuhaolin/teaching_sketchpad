package com.geometry.interaction.action;

import com.geometry.scene.SceneObject;
import com.geometry.scene.Scene;

/**
 * Phase 05 - Action to select a SceneObject.
 *
 * Created by the InteractionManager when a TAP or CLICK gesture hits a
 * SceneObject via ray picking. The action updates the Scene's selection state.
 */
public class SelectAction implements Action {

    private final SceneObject target;
    private final Scene scene;
    /** Whether to toggle selection or force-select. */
    private final boolean toggle;

    public SelectAction(Scene scene, SceneObject target) {
        this(scene, target, false);
    }

    public SelectAction(Scene scene, SceneObject target, boolean toggle) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
        this.target = target;
        this.toggle = toggle;
    }

    @Override
    public void execute() {
        if (target == null) {
            scene.clearSelection();
            return;
        }
        if (toggle) {
            scene.toggleSelection(target);
        } else {
            scene.select(target);
        }
    }

    @Override
    public String getDescription() {
        return "SelectAction{target=" + (target != null ? target.getId() : "null")
                + ", toggle=" + toggle + "}";
    }

    public SceneObject getTarget() {
        return target;
    }

    public Scene getScene() {
        return scene;
    }

    public boolean isToggle() {
        return toggle;
    }
}
