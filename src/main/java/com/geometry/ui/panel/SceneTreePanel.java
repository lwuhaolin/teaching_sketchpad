package com.geometry.ui.panel;

import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 11 - Scene tree panel for displaying the scene object hierarchy.
 *
 * Shows a tree-like list of all SceneObjects in the scene.
 * Clicking an item selects the corresponding object and generates
 * a UIEvent for selection.
 *
 * The panel does NOT hold a reference to core geometry.
 * It only interacts via the Scene reference and UIEventBridge.
 *
 * Not thread-safe.
 */
public class SceneTreePanel {

    /** The scene to display. */
    private final Scene scene;

    /** The event bridge for selection events. */
    private final UIEventBridge bridge;

    /** The currently selected object ID. */
    private String selectedObjectId;

    /**
     * Create a SceneTreePanel.
     *
     * @param scene  the scene to display (may be null in tests)
     * @param bridge the UIEventBridge (may be null in tests)
     */
    public SceneTreePanel(Scene scene, UIEventBridge bridge) {
        this.scene = scene;
        this.bridge = bridge;
        this.selectedObjectId = null;
    }

    // ------------------------------------------------------------------
    // Rendering info
    // ------------------------------------------------------------------

    /**
     * Get the preferred width of this panel in pixels.
     */
    public int getPreferredWidth() {
        return 220;
    }

    /**
     * Get the preferred height of this panel in pixels.
     */
    public int getPreferredHeight() {
        return 300;
    }

    /**
     * Get the height per item row in pixels.
     */
    public int getRowHeight() {
        return 24;
    }

    // ------------------------------------------------------------------
    // Selection
    // ------------------------------------------------------------------

    /**
     * Get the currently selected object ID.
     */
    public String getSelectedObjectId() {
        return selectedObjectId;
    }

    /**
     * Select an object by index in the scene tree.
     *
     * @param index the 0-based index in the scene object list
     */
    public void selectByIndex(int index) {
        if (scene == null || scene.getObjectCount() == 0) {
            return;
        }
        List<SceneObject> objects = scene.getAllObjects();
        if (index < 0 || index >= objects.size()) {
            return;
        }
        SceneObject obj = objects.get(index);
        this.selectedObjectId = obj.getId();
        if (bridge != null) {
            bridge.submit(UIEvent.selectObject(obj.getId()));
        }
    }

    /**
     * Check if a vertical position (y) falls within a tree item row.
     *
     * @param y the y coordinate within the panel
     * @return the object index, or -1 if not on any row
     */
    public int getRowAtY(int y) {
        if (y < 0 || scene == null) {
            return -1;
        }
        int rowHeight = getRowHeight();
        int index = y / rowHeight;
        if (index >= 0 && index < scene.getObjectCount()) {
            return index;
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Display content
    // ------------------------------------------------------------------

    /**
     * Get the display label for a scene object at the given index.
     *
     * @param index the 0-based index
     * @return the label string, or null if index is invalid
     */
    public String getLabelText(int index) {
        if (scene == null || index < 0) {
            return null;
        }
        List<SceneObject> objects = scene.getAllObjects();
        if (index >= objects.size()) {
            return null;
        }
        SceneObject obj = objects.get(index);
        String type = obj.getGeometry().getClass().getSimpleName();
        return obj.getId() + " [" + type + "]";
    }

    /**
     * Get the number of objects in the tree.
     */
    public int getObjectCount() {
        return scene != null ? scene.getObjectCount() : 0;
    }
}
