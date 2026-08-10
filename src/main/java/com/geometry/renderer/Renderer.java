package com.geometry.renderer;

/**
 * Phase 03 - Renderer interface.
 *
 * Defines the contract for all renderers in the engine.
 * The Renderer is responsible for:
 *   - Initializing OpenGL context and resources
 *   - Clearing the screen each frame
 *   - Drawing all scene geometry
 *   - Cleaning up OpenGL resources on shutdown
 *
 * Renderer must NOT handle:
 *   - Input / mouse / keyboard
 *   - Business logic / teaching content
 *   - File persistence
 *   - Tool state
 *
 * @see OpenGLRenderer
 */
public interface Renderer {

    /**
     * Initialize the renderer. Called once before the render loop starts.
     * Sets up the OpenGL context, shaders, and any one-time resources.
     */
    void initialize();

    /**
     * Clear the framebuffer (color and depth buffers).
     * Called at the start of each frame.
     */
    void clear();

    /**
     * Render all current scene geometry to the framebuffer.
     * Called once per frame inside the render loop.
     */
    void render();

    /**
     * Release all OpenGL resources (shaders, VAOs, VBOs).
     * Called when the application is shutting down.
     */
    void shutdown();

    /**
     * Render a teaching annotation on top of the scene.
     *
     * This is called by annotation and assistant objects during the
     * teaching overlay pass. Implementations that support OpenGL
     * should render the annotation using the current projection and
     * view matrices. Headless/mock implementations may ignore this.
     *
     * @param annotation the annotation to render
     */
    default void renderAnnotation(com.geometry.teaching.annotation.Annotation annotation) {
        // Default: no-op for headless/mock renderers
    }
}
