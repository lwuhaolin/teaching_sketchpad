package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Scene data model for persistence.
 *
 * Stores the complete scene state including:
 *   - Geometry objects (with parameters, not mesh data)
 *   - Layer names
 *   - Camera state (position, target, up vector)
 *   - View mode (2D or 3D)
 *   - Selection state
 *
 * Design:
 *   - Does NOT store mesh vertices/faces — only geometry parameters
 *   - Camera is stored as transform-like data
 *
 * Not thread-safe.
 */
public class SceneData {

    private final List<ObjectData> objects;
    private final List<String> layers;
    private final CameraData camera;
    private String viewMode;
    private String selectedObjectId;

    /**
     * Create an empty SceneData.
     */
    public SceneData() {
        this.objects = new ArrayList<>();
        this.layers = new ArrayList<>();
        this.camera = new CameraData();
        this.viewMode = "MODE_2D";
        this.selectedObjectId = null;
    }

    // ------------------------------------------------------------------
    // Object management
    // ------------------------------------------------------------------

    public void addObject(ObjectData objectData) {
        if (objectData != null) {
            objects.add(objectData);
        }
    }

    public void setObjects(List<ObjectData> newObjects) {
        if (newObjects != null) {
            objects.clear();
            objects.addAll(newObjects);
        }
    }

    public void removeObject(String objectId) {
        objects.removeIf(obj -> obj != null && obj.getId().equals(objectId));
    }

    public ObjectData findObject(String objectId) {
        for (ObjectData obj : objects) {
            if (obj != null && obj.getId().equals(objectId)) {
                return obj;
            }
        }
        return null;
    }

    public List<ObjectData> getObjects() {
        return new ArrayList<>(objects);
    }

    public int getObjectCount() {
        return objects.size();
    }

    // ------------------------------------------------------------------
    // Layer management
    // ------------------------------------------------------------------

    public void addLayer(String layerName) {
        if (layerName != null && !layerName.isEmpty()) {
            layers.add(layerName);
        }
    }

    public List<String> getLayers() {
        return new ArrayList<>(layers);
    }

    public int getLayerCount() {
        return layers.size();
    }

    // ------------------------------------------------------------------
    // Camera
    // ------------------------------------------------------------------

    public CameraData getCamera() {
        return camera;
    }

    public void setCamera(CameraData camera) {
        if (camera == null) return;
        this.camera.setPosition(camera.getPosition());
        this.camera.setTarget(camera.getTarget());
        this.camera.setUp(camera.getUp());
        this.camera.setFov(camera.getFov());
    }

    // ------------------------------------------------------------------
    // View mode and selection
    // ------------------------------------------------------------------

    public String getViewMode() {
        return viewMode;
    }

    public void setViewMode(String viewMode) {
        this.viewMode = viewMode != null ? viewMode : "MODE_2D";
    }

    public String getSelectedObjectId() {
        return selectedObjectId;
    }

    public void setSelectedObjectId(String selectedObjectId) {
        this.selectedObjectId = selectedObjectId;
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SceneData sceneData = (SceneData) o;
        return objects.equals(sceneData.objects);
    }

    @Override
    public int hashCode() {
        return objects.hashCode();
    }

    @Override
    public String toString() {
        return "SceneData{objects=" + objects.size()
                + ", layers=" + layers.size()
                + ", viewMode=" + viewMode
                + ", selected=" + selectedObjectId + "}";
    }
}
