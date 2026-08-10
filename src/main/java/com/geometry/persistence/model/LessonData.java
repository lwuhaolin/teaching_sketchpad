package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for a teaching lesson.
 *
 * Stores a complete lesson with title, description, and ordered steps.
 * Each step references animations by ID for replay.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "title": "Cylinder Unfolding",
 *   "description": "Teaches how cylinder lateral surface unfolds",
 *   "steps": [
 *     {
 *       "number": 1,
 *       "title": "Show Cylinder",
 *       "description": "Display a standard cylinder",
 *       "animationId": "anim_cylinder_show"
 *     },
 *     {
 *       "number": 2,
 *       "title": "Cut Cylinder",
 *       "description": "Apply a plane cut",
 *       "animationId": "anim_cut"
 *     }
 *   ]
 * }
 * </pre>
 *
 * Not thread-safe.
 */
public class LessonData {

    private String title;
    private String description;
    private final List<StepData> steps;

    /**
     * Create an empty LessonData.
     */
    public LessonData() {
        this.title = "";
        this.description = "";
        this.steps = new ArrayList<>();
    }

    /**
     * Create a LessonData with title and description.
     *
     * @param title       lesson title
     * @param description lesson description
     */
    public LessonData(String title, String description) {
        this();
        this.title = title;
        this.description = description != null ? description : "";
    }

    // ------------------------------------------------------------------
    // Step management
    // ------------------------------------------------------------------

    /**
     * Add a step to the lesson.
     *
     * @param step step data to add
     */
    public void addStep(StepData step) {
        if (step != null) {
            steps.add(step);
        }
    }

    /**
     * Remove a step by its 1-based number.
     *
     * @param stepNumber 1-based step number
     * @return true if removed
     */
    public boolean removeStep(int stepNumber) {
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i).getStepNumber() == stepNumber) {
                steps.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Get all steps.
     */
    public List<StepData> getSteps() {
        return new ArrayList<>(steps);
    }

    public int getStepCount() {
        return steps.size();
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
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
        LessonData that = (LessonData) o;
        return title.equals(that.title) && steps.equals(that.steps);
    }

    @Override
    public int hashCode() {
        return 31 * title.hashCode() + steps.hashCode();
    }

    @Override
    public String toString() {
        return "LessonData{title='" + title + "', steps=" + steps.size() + "}";
    }
}
