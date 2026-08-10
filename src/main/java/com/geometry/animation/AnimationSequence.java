package com.geometry.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 09 - A sequence of animations for teaching lessons.
 *
 * Represents an ordered list of animation steps that play in sequence.
 * Each step has a title, optional delay, and an Animation to execute.
 *
 * Example usage:
 *   AnimationSequence seq = new AnimationSequence("Cylinder Unfold");
 *   seq.addStep(new AnimationItem("Rotate", rotateAnim, 0.5f));
 *   seq.addStep(new AnimationItem("Cut", cutAnim, 1.0f));
 *   seq.addStep(new AnimationItem("Unfold", unfoldAnim, 2.0f));
 *
 * Integration:
 *   Called by TeachingManager for each Step in a Lesson.
 *   Supports interactive progress control.
 */
public class AnimationSequence {

    private final String name;
    private final List<AnimationItem> steps;
    private int currentStepIndex;
    private AnimationState sequenceState;

    /**
     * Create an AnimationSequence with the given name.
     *
     * @param name sequence identifier (e.g. "Cylinder Unfold Demo")
     */
    public AnimationSequence(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Sequence name cannot be null or empty");
        }
        this.name = name;
        this.steps = new ArrayList<>();
        this.currentStepIndex = 0;
        this.sequenceState = AnimationState.READY;
    }

    /**
     * Add an animation step to the sequence.
     *
     * @param stepName       human-readable name for this step
     * @param animation      the animation to run
     * @param delayInSeconds time to wait before starting this step (0 = immediate)
     */
    public void addStep(String stepName, Animation animation, float delayInSeconds) {
        steps.add(new AnimationItem(stepName, animation, delayInSeconds));
    }

    /**
     * Add an animation step with no delay.
     *
     * @param stepName  human-readable name
     * @param animation the animation to run
     */
    public void addStep(String stepName, Animation animation) {
        addStep(stepName, animation, 0f);
    }

    /**
     * Get all steps in the sequence.
     */
    public List<AnimationItem> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Get the number of steps.
     */
    public int getStepCount() {
        return steps.size();
    }

    /**
     * Start the sequence from the beginning.
     */
    public void start() {
        currentStepIndex = 0;
        sequenceState = AnimationState.RUNNING;
        playCurrentStep();
    }

    /**
     * Advance to the next step in the sequence.
     *
     * @return true if advanced, false if at end
     */
    public boolean nextStep() {
        if (currentStepIndex < steps.size() - 1) {
            AnimationItem currentItem = steps.get(currentStepIndex);
            currentItem.getAnimation().stop();
            currentStepIndex++;
            playCurrentStep();
            return true;
        }
        return false;
    }

    /**
     * Go back to the previous step.
     *
     * @return true if moved back, false if at start
     */
    public boolean previousStep() {
        if (currentStepIndex > 0) {
            AnimationItem currentItem = steps.get(currentStepIndex);
            currentItem.getAnimation().stop();
            currentStepIndex--;
            playCurrentStep();
            return true;
        }
        return false;
    }

    /**
     * Update the sequence by delta time.
     * Called every frame.
     *
     * @param deltaTime time in seconds
     */
    public void update(float deltaTime) {
        if (sequenceState != AnimationState.RUNNING) {
            return;
        }
        if (currentStepIndex < steps.size()) {
            AnimationItem item = steps.get(currentStepIndex);
            Animation anim = item.getAnimation();
            anim.update(deltaTime);
            if (anim.isFinished()) {
                onStepComplete(item);
            }
        }
    }

    /**
     * Stop the sequence and all animations.
     */
    public void stop() {
        for (AnimationItem item : steps) {
            item.getAnimation().stop();
        }
        sequenceState = AnimationState.STOPPED;
    }

    /**
     * Get the current step index (0-based).
     */
    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    /**
     * Get the name of the current step, or null if no steps.
     */
    public String getCurrentStepName() {
        if (currentStepIndex >= 0 && currentStepIndex < steps.size()) {
            return steps.get(currentStepIndex).getName();
        }
        return null;
    }

    /**
     * Check if the sequence is complete.
     */
    public boolean isFinished() {
        return sequenceState == AnimationState.FINISHED
                || (sequenceState == AnimationState.RUNNING
                && currentStepIndex >= steps.size());
    }

    /**
     * Get the sequence state.
     */
    public AnimationState getState() {
        return sequenceState;
    }

    /**
     * Get the sequence name.
     */
    public String getName() {
        return name;
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private void playCurrentStep() {
        if (currentStepIndex < steps.size()) {
            AnimationItem item = steps.get(currentStepIndex);
            item.getAnimation().start();
        }
    }

    private void onStepComplete(AnimationItem completedItem) {
        completedItem.getAnimation().stop();
        if (currentStepIndex < steps.size() - 1) {
            currentStepIndex++;
            playCurrentStep();
        } else {
            sequenceState = AnimationState.FINISHED;
        }
    }

    /**
     * A single step in an animation sequence.
     */
    public static class AnimationItem {
        private final String name;
        private final Animation animation;
        private final float delaySeconds;

        public AnimationItem(String name, Animation animation, float delaySeconds) {
            this.name = name;
            this.animation = animation;
            this.delaySeconds = delaySeconds;
        }

        public String getName() {
            return name;
        }

        public Animation getAnimation() {
            return animation;
        }

        public float getDelaySeconds() {
            return delaySeconds;
        }
    }
}
