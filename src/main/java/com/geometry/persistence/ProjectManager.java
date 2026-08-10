package com.geometry.persistence;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationSequence;
import com.geometry.core.math.Vec3;
import com.geometry.persistence.command.CommandData;
import com.geometry.persistence.command.HistoryData;
import com.geometry.persistence.model.*;
import com.geometry.persistence.registry.AnimationRegistry;
import com.geometry.persistence.registry.GeometryRegistry;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.annotation.Annotation;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.HighlightAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Phase 10 - Manager for project persistence operations.
 *
 * Provides high-level methods for saving and loading teaching projects.
 * Coordinates between serializer, deserializer, and registries.
 *
 * Architecture:
 *   ProjectManager
 *     ├── ProjectSerializer (save)
 *     ├── ProjectDeserializer (load)
 *     ├── GeometryRegistry (recreate geometry)
 *     ├── AnimationRegistry (recreate animations)
 *     └── HistoryData (undo/redo)
 *
 * Usage:
 *   ProjectManager manager = new ProjectManager();
 *   manager.save(scene, "myLesson.gtp");
 *   Scene loaded = manager.load("myLesson.gtp");
 *
 * Not thread-safe.
 */
public class ProjectManager {

    private final GeometryRegistry geometryRegistry;
    private final AnimationRegistry animationRegistry;
    private final ProjectSerializer serializer;
    private final ProjectDeserializer deserializer;
    private final HistoryData history;
    private String lastSavedPath;

    /**
     * Create a ProjectManager with default registries.
     */
    public ProjectManager() {
        this.geometryRegistry = new GeometryRegistry();
        this.animationRegistry = new AnimationRegistry();
        this.serializer = new ProjectSerializer(geometryRegistry);
        this.deserializer = new ProjectDeserializer(geometryRegistry, animationRegistry);
        this.history = new HistoryData();
        this.lastSavedPath = null;
    }

    /**
     * Create a ProjectManager with custom registries.
     *
     * @param geometryRegistry custom geometry registry
     * @param animationRegistry custom animation registry
     */
    public ProjectManager(GeometryRegistry geometryRegistry, AnimationRegistry animationRegistry) {
        if (geometryRegistry == null) {
            throw new IllegalArgumentException("GeometryRegistry cannot be null");
        }
        if (animationRegistry == null) {
            throw new IllegalArgumentException("AnimationRegistry cannot be null");
        }
        this.geometryRegistry = geometryRegistry;
        this.animationRegistry = animationRegistry;
        this.serializer = new ProjectSerializer(geometryRegistry);
        this.deserializer = new ProjectDeserializer(geometryRegistry, animationRegistry);
        this.history = new HistoryData();
        this.lastSavedPath = null;
    }

    // ------------------------------------------------------------------
    // Save operations
    // ------------------------------------------------------------------

    /**
     * Save a Scene to a .gtp file.
     *
     * @param scene the scene to save
     * @param filePath output file path (must end with .gtp)
     * @throws IOException if the file cannot be written
     */
    public void save(Scene scene, String filePath) throws IOException {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        ProjectData data = serializer.serializeToData(scene);
        serializer.serialize(scene, filePath, data);
        this.lastSavedPath = filePath;

        // Record the save as a command in history
        history.addCommand(new CommandData(
                java.util.UUID.randomUUID().toString(),
                CommandData.CommandType.CREATE,
                null,
                null,
                null
        ));
    }

    /**
     * Save a Scene with additional project data.
     *
     * @param scene the scene to save
     * @param filePath output file path
     * @param projectData additional project data (teaching, animation, whiteboard)
     * @throws IOException if the file cannot be written
     */
    public void save(Scene scene, String filePath, ProjectData projectData) throws IOException {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }

        ProjectData data = projectData != null
                ? projectData
                : serializer.serializeToData(scene);

        serializer.serialize(scene, filePath, data);
        this.lastSavedPath = filePath;
    }

    // ------------------------------------------------------------------
    // Load operations
    // ------------------------------------------------------------------

    /**
     * Load a Scene from a .gtp file.
     *
     * @param filePath path to the .gtp file
     * @return the loaded Scene
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file format is invalid
     */
    public Scene load(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        ProjectData data = deserializer.deserialize(filePath);
        return deserializer.rebuildScene(data.getScene());
    }

    /**
     * Load a full ProjectData from a .gtp file.
     *
     * @param filePath path to the .gtp file
     * @return the loaded ProjectData
     * @throws IOException if the file cannot be read
     */
    public ProjectData loadProjectData(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        return deserializer.deserialize(filePath);
    }

    // ------------------------------------------------------------------
    // History / Undo
    // ------------------------------------------------------------------

    /**
     * Get the command history.
     */
    public HistoryData getHistory() {
        return history;
    }

    /**
     * Check if undo is available.
     */
    public boolean canUndo() {
        return history.canUndo();
    }

    /**
     * Check if redo is available.
     */
    public boolean canRedo() {
        return history.canRedo();
    }

    /**
     * Get the path of the last saved file.
     */
    public String getLastSavedPath() {
        return lastSavedPath;
    }

    /**
     * Clear the command history.
     */
    public void clearHistory() {
        history.clear();
    }

    // ------------------------------------------------------------------
    // Registry access
    // ------------------------------------------------------------------

    /**
     * Get the geometry registry.
     */
    public GeometryRegistry getGeometryRegistry() {
        return geometryRegistry;
    }

    /**
     * Get the animation registry.
     */
    public AnimationRegistry getAnimationRegistry() {
        return animationRegistry;
    }

    /**
     * Check if a geometry type is supported.
     */
    public boolean isGeometryTypeSupported(String type) {
        return geometryRegistry.isRegistered(type);
    }

    /**
     * Get all supported geometry types.
     */
    public java.util.Set<String> getSupportedGeometryTypes() {
        return geometryRegistry.getRegisteredTypes();
    }
}
