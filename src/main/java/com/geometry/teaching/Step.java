package com.geometry.teaching;

import com.geometry.scene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 07 - A step in a teaching lesson.
 *
 * Each Step represents a single state of the teaching scene at a
 * particular moment in the lesson. It includes:
 *   - A title and description for the step
 *   - A snapshot of the Scene state (objects, transforms, annotations)
 *   - A list of actions the teacher expects the student to perform
 *
 * Steps are created and managed by {@link Lesson} and navigated
 * by {@link TeachingManager}.
 *
 * Not thread-safe.
 */
public class Step {

    private final int stepNumber;
    private final String title;
    private final String description;
    private final Scene sceneState;
    private final List<String> actions;

    /**
     * Create a Step with the given parameters.
     *
     * @param stepNumber    1-based step number in the lesson
     * @param title         short title for the step (e.g. "Show Cylinder")
     * @param description   detailed description of what the student should see/do
     * @param sceneState    the Scene state for this step (will be copied)
     * @param actions       expected actions for the student (may be empty)
     * @throws IllegalArgumentException if stepNumber <= 0, title is blank, or sceneState is null
     */
    public Step(int stepNumber, String title, String description,
                 Scene sceneState, List<String> actions) {
        if (stepNumber <= 0) {
            throw new IllegalArgumentException("Step number must be positive, got " + stepNumber);
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (sceneState == null) {
            throw new IllegalArgumentException("SceneState cannot be null");
        }
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description != null ? description : "";
        this.sceneState = sceneState;
        this.actions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
    }

    /**
     * Create a simple Step with no scene state and no actions.
     *
     * @param stepNumber 1-based step number
     * @param title      short title
     * @param description step description
     */
    public Step(int stepNumber, String title, String description) {
        this(stepNumber, title, description, new Scene(), Collections.emptyList());
    }

    /**
     * Create a simple Step with just a number and title.
     *
     * @param stepNumber 1-based step number
     * @param title      short title
     */
    public Step(int stepNumber, String title) {
        this(stepNumber, title, "");
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the 1-based step number.
     */
    public int getStepNumber() {
        return stepNumber;
    }

    /**
     * Get the step title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the step description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the Scene state for this step.
     *
     * @return the scene (never null)
     */
    public Scene getSceneState() {
        return sceneState;
    }

    /**
     * Get the list of expected actions.
     */
    public List<String> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /**
     * Add an expected action to this step.
     *
     * @param action action description string
     */
    public void addAction(String action) {
        if (action != null && !action.isEmpty()) {
            actions.add(action);
        }
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
        Step step = (Step) o;
        return stepNumber == step.stepNumber && title.equals(step.title);
    }

    @Override
    public int hashCode() {
        return 31 * stepNumber + title.hashCode();
    }

    @Override
    public String toString() {
        return "Step{" + stepNumber + ": '" + title + "'"
                + ", actions=" + actions.size() + "}";
    }
}
