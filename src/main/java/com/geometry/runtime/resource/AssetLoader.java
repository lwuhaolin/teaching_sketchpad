package com.geometry.runtime.resource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 12 - Asset loader for application assets.
 *
 * Provides lazy-loading and caching for common asset types:
 *   - Shader files (.glsl)
 *   - Model files
 *   - Lesson files (.gtp)
 *   - Audio files
 *
 * Assets are loaded from the classpath and cached by ResourceManager.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class AssetLoader {

    private final ResourceManager resourceManager;

    /** Asset path prefix for shaders. */
    public static final String SHADER_PATH = "shaders/";

    /** Asset path prefix for models. */
    public static final String MODEL_PATH = "models/";

    /** Asset path prefix for lessons. */
    public static final String LESSON_PATH = "lessons/";

    /** Asset path prefix for audio. */
    public static final String AUDIO_PATH = "audio/";

    /**
     * Create an AssetLoader with a new ResourceManager.
     */
    public AssetLoader() {
        this(new ResourceManager());
    }

    /**
     * Create an AssetLoader with the given ResourceManager.
     */
    public AssetLoader(ResourceManager resourceManager) {
        if (resourceManager == null) {
            throw new IllegalArgumentException("resourceManager cannot be null");
        }
        this.resourceManager = resourceManager;
    }

    // ------------------------------------------------------------------
    // Shader loading
    // ------------------------------------------------------------------

    /**
     * Load a vertex shader by name.
     *
     * @param name the shader name (without extension)
     * @return the shader source code, or null if not found
     */
    public String loadVertexShader(String name) {
        return resourceManager.loadResourceString(SHADER_PATH + name + ".vs.glsl");
    }

    /**
     * Load a fragment shader by name.
     *
     * @param name the shader name (without extension)
     * @return the shader source code, or null if not found
     */
    public String loadFragmentShader(String name) {
        return resourceManager.loadResourceString(SHADER_PATH + name + ".fs.glsl");
    }

    /**
     * Load a shader pair (vertex + fragment) by name.
     *
     * @return a ShaderPair with both shaders, or null if either is missing
     */
    public ShaderPair loadShaderPair(String name) {
        String vs = loadVertexShader(name);
        String fs = loadFragmentShader(name);
        if (vs == null || fs == null) {
            return null;
        }
        return new ShaderPair(vs, fs);
    }

    // ------------------------------------------------------------------
    // Model loading
    // ------------------------------------------------------------------

    /**
     * Load a model resource as bytes.
     *
     * @param name the model name (without extension)
     * @return the model bytes, or null if not found
     */
    public byte[] loadModel(String name) {
        return resourceManager.loadResource(MODEL_PATH + name + ".obj");
    }

    // ------------------------------------------------------------------
    // Lesson loading
    // ------------------------------------------------------------------

    /**
     * Load a lesson file as a String.
     *
     * @param lessonName the lesson identifier
     * @return the lesson content, or null if not found
     */
    public String loadLesson(String lessonName) {
        return resourceManager.loadResourceString(LESSON_PATH + lessonName + ".gtp");
    }

    /**
     * Load a lesson file as bytes.
     *
     * @param lessonName the lesson identifier
     * @return the lesson bytes, or null if not found
     */
    public byte[] loadLessonBytes(String lessonName) {
        return resourceManager.loadResource(LESSON_PATH + lessonName + ".gtp");
    }

    // ------------------------------------------------------------------
    // General resource loading
    // ------------------------------------------------------------------

    /**
     * Load any resource from the classpath.
     *
     * @param path the resource path
     * @return the resource bytes, or null if not found
     */
    public byte[] load(String path) {
        return resourceManager.loadResource(path);
    }

    /**
     * Load any resource as a String from the classpath.
     *
     * @param path the resource path
     * @return the resource as a String, or null if not found
     */
    public String loadString(String path) {
        return resourceManager.loadResourceString(path);
    }

    // ------------------------------------------------------------------
    // Cache management
    // ------------------------------------------------------------------

    /**
     * Get the underlying ResourceManager.
     */
    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    /**
     * Clear all cached assets.
     */
    public void clearCache() {
        resourceManager.clearCache();
    }

    /**
     * Get asset loading statistics.
     */
    public String getStats() {
        return resourceManager.getSummary();
    }

    /**
     * Check if a resource is cached.
     */
    public boolean isCached(String resourceName) {
        return resourceManager.loadResource(resourceName) != null;
    }
}

/**
 * Phase 12 - Holds a paired vertex and fragment shader.
 */
class ShaderPair {
    private final String vertexShader;
    private final String fragmentShader;

    ShaderPair(String vertexShader, String fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    public String getVertexShader() {
        return vertexShader;
    }

    public String getFragmentShader() {
        return fragmentShader;
    }
}
