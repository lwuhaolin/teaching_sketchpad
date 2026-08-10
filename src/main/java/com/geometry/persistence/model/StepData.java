package com.geometry.persistence.model;

/**
 * Phase 10 - Data model for a single teaching step.
 *
 * Stores the title, description, and animation reference for one step
 * in a lesson. Actions (what the student should do) are also stored.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "number": 2,
 *   "title": "Cut the Cylinder",
 *   "description": "Apply a plane cut through the cylinder",
 *   "animationId": "anim_cut_cylinder",
 *   "actions": ["Select the cut tool", "Define the cutting plane"]
 * }
 * </pre>
 *
 * Not thread-safe.
 */
public class StepData {

    private final int stepNumber;
    private String title;
    private String description;
    private String animationId;
    private final java.util.List<String> actions;

    /**
     * Create a StepData.
     *
     * @param stepNumber 1-based step number
     * @param title      short title
     * @param description step description
     * @param animationId animation sequence ID to play (may be null)
     * @param actions    list of expected actions (may be empty)
     */
    public StepData(int stepNumber, String title, String description,
                    String animationId, java.util.List<String> actions) {
        if (stepNumber <= 0) {
            throw new IllegalArgumentException("Step number must be positive, got " + stepNumber);
        }
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        this.stepNumber = stepNumber;
        this.title = title;
        this.description = description != null ? description : "";
        this.animationId = animationId;
        this.actions = actions != null ? new java.util.ArrayList<>(actions) : new java.util.ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public int getStepNumber() {
        return stepNumber;
    }

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

    public String getAnimationId() {
        return animationId;
    }

    public void setAnimationId(String animationId) {
        this.animationId = animationId;
    }

    public java.util.List<String> getActions() {
        return new java.util.ArrayList<>(actions);
    }

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
        StepData stepData = (StepData) o;
        return stepNumber == stepData.stepNumber;
    }

    @Override
    public int hashCode() {
        return stepNumber;
    }

    @Override
    public String toString() {
        return "StepData{" + stepNumber + ": '" + title
                + "', anim=" + animationId + "}";
    }
}
