package com.geometry.scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Phase 04 - Manager for SceneObjects within a Scene.
 *
 * Provides add, remove, findById, and getAll operations.
 * All IDs are unique within the manager.
 *
 * Not thread-safe.
 */
public class ObjectManager {

    private final List<SceneObject> objects;

    public ObjectManager() {
        this.objects = new ArrayList<>();
    }

    /**
     * Add a SceneObject. Throws if an object with the same ID already exists.
     *
     * @param sceneObject the object to add
     * @throws IllegalArgumentException if an object with the same ID already exists
     */
    public void addObject(SceneObject sceneObject) {
        if (sceneObject == null) {
            throw new IllegalArgumentException("SceneObject cannot be null");
        }
        if (findById(sceneObject.getId()) != null) {
            throw new IllegalArgumentException(
                    "Object with ID '" + sceneObject.getId() + "' already exists");
        }
        objects.add(sceneObject);
    }

    /**
     * Remove a SceneObject by reference.
     *
     * @param sceneObject the object to remove
     * @return true if the object was found and removed
     */
    public boolean removeObject(SceneObject sceneObject) {
        return objects.remove(sceneObject);
    }

    /**
     * Remove a SceneObject by ID.
     *
     * @param id the ID of the object to remove
     * @return true if the object was found and removed
     */
    public boolean removeObjectById(String id) {
        SceneObject found = findById(id);
        if (found != null) {
            return objects.remove(found);
        }
        return false;
    }

    /**
     * Find a SceneObject by its ID.
     *
     * @param id the ID to search for
     * @return the matching SceneObject, or null if not found
     */
    public SceneObject findById(String id) {
        if (id == null) {
            return null;
        }
        for (SceneObject obj : objects) {
            if (Objects.equals(obj.getId(), id)) {
                return obj;
            }
        }
        return null;
    }

    /**
     * Get all SceneObjects.
     *
     * @return an unmodifiable list of all objects
     */
    public List<SceneObject> getAll() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Get the total number of objects.
     */
    public int size() {
        return objects.size();
    }

    /**
     * Check if there are no objects.
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /**
     * Remove all objects.
     */
    public void clear() {
        objects.clear();
    }

    /**
     * Check if an object with the given ID exists.
     *
     * @param id the ID to check
     * @return true if an object with that ID exists
     */
    public boolean containsId(String id) {
        return findById(id) != null;
    }
}
