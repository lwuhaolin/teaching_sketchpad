package com.geometry.teaching.annotation;

import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Interface for all teaching annotations.
 *
 * Annotations are overlay elements rendered on top of the scene geometry.
 * They include text labels, arrows, highlights, and other visual cues
 * used for teaching and explanation.
 *
 * Each annotation is responsible for rendering itself when given a
 * {@link Renderer}. Annotations must NOT modify scene geometry.
 *
 * Design:
 *   Annotation objects are stored in {@link com.geometry.teaching.TeachingManager}
 *   and rendered after scene geometry in the overlay pass.
 */
public interface Annotation {

    /**
     * Render this annotation on top of the scene.
     *
     * The renderer is already set up (projection, view matrices bound).
     * Implementations should use the renderer's overlay drawing capabilities.
     *
     * @param renderer the renderer to use for drawing this annotation
     */
    void render(Renderer renderer);

    /**
     * Get the human-readable description of this annotation.
     *
     * @return description string, never null
     */
    String getDescription();
}
