package com.geometry.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 04 - Manages the selection state of SceneObjects.
 *
 * Supports:
 *   - Single and multi-selection
 *   - Select / deselect by SceneObject or by ID
 *   - Clear all selection
 *
 * Note: Mouse picking / ray casting is handled in Phase 05 (Interaction System).
 * Selection here is purely state-based.
 *
 * Not thread-safe.
 */
public class SelectionManager {

    private final List<SceneObject> selected;

    public SelectionManager() {
        this.selected = new ArrayList<>();
    }

    /**
     * Select a SceneObject. If it is already selected, this is a no-op.
     *
     * @param sceneObject the object to select
     */
    public void select(SceneObject sceneObject) {
        if (sceneObject == null) {
            return;
        }
        if (!selected.contains(sceneObject)) {
            // Deselect previous
            for (SceneObject s : selected) {
                s.setSelected(false);
            }
            selected.clear();
            selected.add(sceneObject);
            sceneObject.setSelected(true);
        }
    }

    /**
     * Select a SceneObject by ID.
     *
     * @param id     the ID of the object to select
     * @param finder callback to resolve ID to SceneObject
     * @return true if the object was found and selected
     */
    public boolean selectById(String id, java.util.function.Function<String, SceneObject> finder) {
        if (id == null || finder == null) {
            return false;
        }
        SceneObject target = finder.apply(id);
        if (target == null) {
            return false;
        }
        select(target);
        return true;
    }

    /**
     * Deselect a specific SceneObject.
     *
     * @param sceneObject the object to deselect
     */
    public void deselect(SceneObject sceneObject) {
        if (sceneObject == null) {
            return;
        }
        if (selected.remove(sceneObject)) {
            sceneObject.setSelected(false);
        }
    }

    /**
     * Deselect all objects.
     */
    public void clearSelection() {
        for (SceneObject s : selected) {
            s.setSelected(false);
        }
        selected.clear();
    }

    /**
     * Toggle selection of a SceneObject (select if not selected, deselect if selected).
     *
     * @param sceneObject the object to toggle
     */
    public void toggleSelection(SceneObject sceneObject) {
        if (sceneObject == null) {
            return;
        }
        if (selected.contains(sceneObject)) {
            deselect(sceneObject);
        } else {
            select(sceneObject);
        }
    }

    /**
     * Get the currently selected SceneObject, or null if none selected.
     */
    public SceneObject getSelected() {
        return selected.isEmpty() ? null : selected.get(0);
    }

    /**
     * Get all selected SceneObjects.
     *
     * @return an unmodifiable list of selected objects
     */
    public List<SceneObject> getSelectedObjects() {
        return Collections.unmodifiableList(selected);
    }

    /**
     * Get the number of selected objects.
     */
    public int getSelectedCount() {
        return selected.size();
    }

    /**
     * Check if a SceneObject is currently selected.
     *
     * @param sceneObject the object to check
     * @return true if selected
     */
    public boolean isSelected(SceneObject sceneObject) {
        return sceneObject != null && selected.contains(sceneObject);
    }
}
