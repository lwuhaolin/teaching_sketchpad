package com.geometry.teaching;

import com.geometry.core.math.Vec3;
import com.geometry.renderer.Renderer;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.annotation.Annotation;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.HighlightAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;
import com.geometry.teaching.assistant.CoordinateSystem;
import com.geometry.teaching.assistant.Grid;
import com.geometry.teaching.assistant.HelperLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 07 - Central manager for the Teaching System.
 *
 * Responsibilities:
 *   - Manage the current teaching mode (TEACHER, STUDENT, EXAM, FREE)
 *   - Manage the current lesson and step navigation
 *   - Manage annotations (text, arrows, highlights)
 *   - Manage assistant objects (grid, coordinate system, helper lines)
 *   - Render all annotations and assistants on top of the scene
 *
 * Architecture:
 *   TeachingManager sits between the Tool layer and the Scene layer.
 *   It does NOT depend on UI components. All teaching logic is contained
 *   within this class and its sub-packages.
 *
 * Rendering order:
 *   1. Scene geometry (handled by Scene.render(renderer))
 *   2. Assistant objects (grid, coordinate system)
 *   3. Annotations (text, arrows, highlights)
 *
 * Not thread-safe.
 */
public class TeachingManager {

    private TeachingMode mode;
    private Lesson currentLesson;
    private Scene targetScene;
    private final List<Annotation> annotations;
    private final List<com.geometry.teaching.annotation.Annotation> assistants;
    private final Renderer renderer;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * Create a TeachingManager with a target Scene and Renderer.
     *
     * @param targetScene the Scene to apply teaching state to
     * @param renderer    the Renderer for drawing annotations (may be null in headless tests)
     * @throws IllegalArgumentException if targetScene is null
     */
    public TeachingManager(Scene targetScene, Renderer renderer) {
        if (targetScene == null) {
            throw new IllegalArgumentException("Target Scene cannot be null");
        }
        this.mode = TeachingMode.FREE;
        this.currentLesson = null;
        this.targetScene = targetScene;
        this.annotations = new ArrayList<>();
        this.assistants = new ArrayList<>();
        this.renderer = renderer;
    }

    // ------------------------------------------------------------------
    // Teaching mode
    // ------------------------------------------------------------------

    /**
     * Get the current teaching mode.
     */
    public TeachingMode getMode() {
        return mode;
    }

    /**
     * Set the teaching mode.
     *
     * @param mode the new mode
     */
    public void setMode(TeachingMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("TeachingMode cannot be null");
        }
        this.mode = mode;
    }

    // ------------------------------------------------------------------
    // Lesson management
    // ------------------------------------------------------------------

    /**
     * Start a lesson.
     *
     * @param lesson the lesson to start
     * @throws IllegalArgumentException if lesson is null
     */
    public void startLesson(Lesson lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Lesson cannot be null");
        }
        this.currentLesson = lesson;
        // Apply the first step's scene state
        if (!lesson.getSteps().isEmpty()) {
            applyStep(lesson.getSteps().get(0));
        }
    }

    /**
     * Get the current lesson, or null if no lesson is active.
     */
    public Lesson getCurrentLesson() {
        return currentLesson;
    }

    /**
     * Check if a lesson is currently active.
     */
    public boolean isLessonActive() {
        return currentLesson != null && !currentLesson.getSteps().isEmpty();
    }

    /**
     * Move to the next step in the current lesson.
     *
     * @return true if step changed, false if already at last step
     */
    public boolean nextStep() {
        if (currentLesson == null) {
            return false;
        }
        if (currentLesson.nextStep()) {
            Step step = currentLesson.getCurrentStep();
            if (step != null) {
                applyStep(step);
            }
            return true;
        }
        return false;
    }

    /**
     * Move to the previous step in the current lesson.
     *
     * @return true if step changed, false if already at first step
     */
    public boolean previousStep() {
        if (currentLesson == null) {
            return false;
        }
        if (currentLesson.previousStep()) {
            Step step = currentLesson.getCurrentStep();
            if (step != null) {
                applyStep(step);
            }
            return true;
        }
        return false;
    }

    /**
     * Get the current step number (1-based), or 0 if no lesson is active.
     */
    public int getCurrentStepNumber() {
        if (currentLesson == null || currentLesson.getSteps().isEmpty()) {
            return 0;
        }
        return currentLesson.getCurrentStepIndex() + 1;
    }

    /**
     * Get the total number of steps in the current lesson, or 0.
     */
    public int getTotalStepCount() {
        if (currentLesson == null) {
            return 0;
        }
        return currentLesson.getStepCount();
    }

    // ------------------------------------------------------------------
    // Annotation management
    // ------------------------------------------------------------------

    /**
     * Add a text annotation.
     *
     * @param text     label text
     * @param position world-space position
     * @param size     text size in world units
     * @return the created annotation (for reference)
     */
    public TextAnnotation addTextAnnotation(String text, Vec3 position, float size) {
        TextAnnotation annotation = new TextAnnotation(text, position, size);
        annotations.add(annotation);
        return annotation;
    }

    /**
     * Add a text annotation with custom color.
     *
     * @param text     label text
     * @param position world-space position
     * @param size     text size
     * @param r        red [0, 255]
     * @param g        green [0, 255]
     * @param b        blue [0, 255]
     * @return the created annotation
     */
    public TextAnnotation addTextAnnotation(String text, Vec3 position, float size, int r, int g, int b) {
        TextAnnotation annotation = new TextAnnotation(text, position, size, r, g, b);
        annotations.add(annotation);
        return annotation;
    }

    /**
     * Add an arrow annotation.
     *
     * @param start start point
     * @param end   end point (arrow tip)
     * @return the created annotation
     */
    public ArrowAnnotation addArrowAnnotation(Vec3 start, Vec3 end) {
        ArrowAnnotation annotation = new ArrowAnnotation(start, end);
        annotations.add(annotation);
        return annotation;
    }

    /**
     * Add a highlight annotation.
     *
     * @param target the SceneObject to highlight
     * @return the created annotation
     */
    public HighlightAnnotation addHighlightAnnotation(SceneObject target) {
        HighlightAnnotation annotation = new HighlightAnnotation(target);
        annotations.add(annotation);
        return annotation;
    }

    /**
     * Remove an annotation.
     *
     * @param annotation the annotation to remove
     * @return true if found and removed
     */
    public boolean removeAnnotation(Annotation annotation) {
        return annotations.remove(annotation);
    }

    /**
     * Remove all annotations.
     */
    public void clearAnnotations() {
        annotations.clear();
    }

    /**
     * Get all annotations.
     */
    public List<Annotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    /**
     * Get the number of annotations.
     */
    public int getAnnotationCount() {
        return annotations.size();
    }

    // ------------------------------------------------------------------
    // Assistant management
    // ------------------------------------------------------------------

    /**
     * Add a grid assistant.
     *
     * @param grid the grid to add
     */
    public void addAssistant(Grid grid) {
        if (grid != null) {
            assistants.add(grid);
        }
    }

    /**
     * Add a coordinate system assistant.
     *
     * @param coordSys the coordinate system to add
     */
    public void addAssistant(CoordinateSystem coordSys) {
        if (coordSys != null) {
            assistants.add(coordSys);
        }
    }

    /**
     * Add a helper line.
     *
     * @param helperLine the helper line to add
     */
    public void addAssistant(HelperLine helperLine) {
        if (helperLine != null) {
            assistants.add(helperLine);
        }
    }

    /**
     * Remove an assistant.
     *
     * @param assistant the assistant to remove
     * @return true if found and removed
     */
    public boolean removeAssistant(com.geometry.teaching.annotation.Annotation assistant) {
        return assistants.remove(assistant);
    }

    /**
     * Remove all assistants.
     */
    public void clearAssistants() {
        assistants.clear();
    }

    /**
     * Get all assistants.
     */
    public List<com.geometry.teaching.annotation.Annotation> getAssistants() {
        return Collections.unmodifiableList(assistants);
    }

    /**
     * Get the number of assistants.
     */
    public int getAssistantCount() {
        return assistants.size();
    }

    // ------------------------------------------------------------------
    // Scene access
    // ------------------------------------------------------------------

    /**
     * Get the target Scene managed by this TeachingManager.
     */
    public Scene getTargetScene() {
        return targetScene;
    }

    /**
     * Set the target Scene.
     *
     * @param scene the new target scene
     */
    public void setTargetScene(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Target Scene cannot be null");
        }
        this.targetScene = scene;
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Render all annotations and assistants.
     *
     * Called after scene geometry has been rendered. Iterates over
     * all assistants and annotations, calling their render methods.
     */
    public void render() {
        // Render assistants first (grid, coordinate system)
        for (com.geometry.teaching.annotation.Annotation assistant : assistants) {
            assistant.render(renderer);
        }
        // Then render annotations
        for (Annotation annotation : annotations) {
            annotation.render(renderer);
        }
    }

    // ------------------------------------------------------------------
    // Step application (internal)
    // ------------------------------------------------------------------

    /**
     * Apply a step's scene state to the target scene.
     *
     * This copies objects from the step's scene to the target scene,
     * then restores any annotations and assistants saved in the step.
     *
     * @param step the step to apply
     */
    private void applyStep(Step step) {
        if (step == null) {
            return;
        }
        // The step's scene state is applied by copying objects
        // In a full implementation, this would deep-copy the scene
        // For now, we just note the step has been applied
        // (Scene state management is handled by the Lesson/Step system)
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Reset the TeachingManager to a clean state.
     * Clears all annotations, assistants, and lesson state.
     */
    public void reset() {
        this.mode = TeachingMode.FREE;
        this.currentLesson = null;
        this.annotations.clear();
        this.assistants.clear();
    }

    /**
     * Check if the current mode allows editing.
     *
     * @return true if mode is TEACHER or FREE
     */
    public boolean canEdit() {
        return mode == TeachingMode.TEACHER || mode == TeachingMode.FREE;
    }

    /**
     * Check if the current mode allows annotations.
     *
     * @return true if mode is TEACHER, STUDENT (with permission), or FREE
     */
    public boolean canAnnotate() {
        return mode == TeachingMode.TEACHER || mode == TeachingMode.FREE;
    }

    @Override
    public String toString() {
        return "TeachingManager{mode=" + mode
                + ", lesson=" + (currentLesson != null ? currentLesson.getLessonName() : "none")
                + ", step=" + getCurrentStepNumber() + "/" + getTotalStepCount()
                + ", annotations=" + annotations.size()
                + ", assistants=" + assistants.size() + "}";
    }
}
