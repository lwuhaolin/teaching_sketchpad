package com.geometry.teaching;

import com.geometry.scene.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 07 - A complete teaching lesson.
 *
 * A Lesson represents a structured teaching session with:
 *   - A lesson name and description
 *   - An ordered list of {@link Step} objects
 *   - A starting step index
 *
 * Lessons are used by {@link TeachingManager} to drive the teaching
 * flow. The teacher can navigate between steps, and each step
 * restores the scene to the state defined in that step.
 *
 * Example:
 *   <pre>
 *   Lesson lesson = new Lesson("Cylinder Unfolding", "Teaches cylinder net");
 *   lesson.addStep(new Step(1, "Show Cylinder", "Display a standard cylinder"));
 *   lesson.addStep(new Step(2, "Cut the Cylinder", "Apply a plane cut through the cylinder"));
 *   lesson.addStep(new Step(3, "Unfold the Side", "Show the lateral surface unfolded into a rectangle"));
 *   </pre>
 *
 * Not thread-safe.
 */
public class Lesson {

    private final String lessonName;
    private final String description;
    private final List<Step> steps;
    private int currentStepIndex;

    /**
     * Create a Lesson with the given name and description.
     *
     * @param lessonName  the name of the lesson (e.g. "Cylinder Unfolding")
     * @param description longer description of the lesson content
     * @throws IllegalArgumentException if lessonName is null or empty
     */
    public Lesson(String lessonName, String description) {
        if (lessonName == null || lessonName.isEmpty()) {
            throw new IllegalArgumentException("Lesson name cannot be null or empty");
        }
        this.lessonName = lessonName;
        this.description = description != null ? description : "";
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
    }

    /**
     * Create a Lesson with just a name (no description).
     *
     * @param lessonName the name of the lesson
     */
    public Lesson(String lessonName) {
        this(lessonName, "");
    }

    // ------------------------------------------------------------------
    // Step management
    // ------------------------------------------------------------------

    /**
     * Add a step to the lesson. Steps are stored in order.
     *
     * @param step the step to add
     * @throws IllegalArgumentException if step is null
     */
    public void addStep(Step step) {
        if (step == null) {
            throw new IllegalArgumentException("Step cannot be null");
        }
        steps.add(step);
    }

    /**
     * Get all steps in this lesson.
     */
    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Get the number of steps.
     */
    public int getStepCount() {
        return steps.size();
    }

    /**
     * Get a step by its 1-based number.
     *
     * @param stepNumber 1-based step number
     * @return the step, or null if not found
     */
    public Step getStep(int stepNumber) {
        if (stepNumber < 1 || stepNumber > steps.size()) {
            return null;
        }
        return steps.get(stepNumber - 1);
    }

    /**
     * Get the current step (by index).
     */
    public Step getCurrentStep() {
        if (steps.isEmpty()) {
            return null;
        }
        return steps.get(currentStepIndex);
    }

    /**
     * Get the current step index (0-based).
     */
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    /**
     * Get the current step number (1-based).
     *
     * @return 1-based step number, or 0 if no steps exist
     */
    public int getCurrentStepNumber() {
        return steps.isEmpty() ? 0 : currentStepIndex + 1;
    }

    /**
     * Set the current step index.
     *
     * @param index 0-based index
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public void setCurrentStepIndex(int index) {
        if (index < 0 || index >= steps.size()) {
            throw new IndexOutOfBoundsException(
                    "Step index " + index + " out of range [0, " + (steps.size() - 1) + "]");
        }
        this.currentStepIndex = index;
    }

    /**
     * Move to the next step.
     *
     * @return true if moved successfully, false if already at last step
     */
    public boolean nextStep() {
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            return true;
        }
        return false;
    }

    /**
     * Move to the previous step.
     *
     * @return true if moved successfully, false if already at first step
     */
    public boolean previousStep() {
        if (currentStepIndex > 0) {
            currentStepIndex--;
            return true;
        }
        return false;
    }

    /**
     * Go to the first step.
     */
    public void goToFirst() {
        if (!steps.isEmpty()) {
            currentStepIndex = 0;
        }
    }

    /**
     * Go to the last step.
     */
    public void goToLast() {
        if (!steps.isEmpty()) {
            currentStepIndex = steps.size() - 1;
        }
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the lesson name.
     */
    public String getLessonName() {
        return lessonName;
    }

    /**
     * Get the lesson description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Check if the lesson has no steps.
     */
    public boolean isEmpty() {
        return steps.isEmpty();
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
        Lesson lesson = (Lesson) o;
        return lessonName.equals(lesson.lessonName);
    }

    @Override
    public int hashCode() {
        return lessonName.hashCode();
    }

    @Override
    public String toString() {
        return "Lesson{name='" + lessonName + "', steps=" + steps.size()
                + ", current=" + (steps.isEmpty() ? 0 : currentStepIndex + 1) + "}";
    }
}
