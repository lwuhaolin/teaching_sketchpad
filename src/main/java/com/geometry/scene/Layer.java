package com.geometry.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 04 - Layer for organizing SceneObjects into groups.
 *
 * A Layer has a name and a list of SceneObjects.
 * Layers are managed by the Scene.
 *
 * Common use cases:
 *   - "基础图形" layer for geometry primitives
 *   - "辅助线" layer for construction lines
 *   - "标注" layer for annotations
 *
 * Not thread-safe.
 */
public class Layer {

    private final String name;
    private final List<SceneObject> objects;

    /**
     * Create a Layer with the given name.
     *
     * @param name layer name (must not be null or empty)
     */
    public Layer(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Layer name cannot be null or empty");
        }
        this.name = name;
        this.objects = new ArrayList<>();
    }

    /**
     * Get the layer name.
     */
    public String getName() {
        return name;
    }

    /**
     * Add a SceneObject to this layer.
     *
     * @param sceneObject the object to add
     * @return true if the object was added
     */
    public boolean addObject(SceneObject sceneObject) {
        if (sceneObject == null) {
            throw new IllegalArgumentException("SceneObject cannot be null");
        }
        return objects.add(sceneObject);
    }

    /**
     * Remove a SceneObject from this layer.
     *
     * @param sceneObject the object to remove
     * @return true if the object was found and removed
     */
    public boolean removeObject(SceneObject sceneObject) {
        return objects.remove(sceneObject);
    }

    /**
     * Remove all SceneObjects from this layer.
     */
    public void clear() {
        objects.clear();
    }

    /**
     * Get an unmodifiable view of the objects in this layer.
     */
    public List<SceneObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Get the number of objects in this layer.
     */
    public int getObjectCount() {
        return objects.size();
    }

    /**
     * Check if this layer is empty.
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Check if this layer is visible (all objects in the layer are visible).
     */
    public boolean isVisible() {
        return !objects.isEmpty() && objects.stream().allMatch(SceneObject::isVisible);
    }

    @Override
    public String toString() {
        return "Layer{name='" + name + "', objects=" + objects.size() + "}";
    }
}
