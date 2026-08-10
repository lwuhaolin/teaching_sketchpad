package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for the animation system state.
 *
 * Stores all animation sequences and the current animation timeline state.
 *
 * Not thread-safe.
 */
public class AnimationData {

    private final List<AnimationSequenceData> sequences;
    private String currentSequenceId;
    private int currentStepIndex;

    /**
     * Create an empty AnimationData.
     */
    public AnimationData() {
        this.sequences = new ArrayList<>();
        this.currentSequenceId = null;
        this.currentStepIndex = 0;
    }

    // ------------------------------------------------------------------
    // Sequence management
    // ------------------------------------------------------------------

    public void addSequence(AnimationSequenceData sequence) {
        if (sequence != null) {
            sequences.add(sequence);
        }
    }

    public AnimationSequenceData findSequence(String name) {
        for (AnimationSequenceData seq : sequences) {
            if (seq.getName().equals(name)) {
                return seq;
            }
        }
        return null;
    }

    public void removeSequence(String name) {
        sequences.removeIf(s -> s != null && s.getName().equals(name));
    }

    public List<AnimationSequenceData> getSequences() {
        return new ArrayList<>(sequences);
    }

    public int getSequenceCount() {
        return sequences.size();
    }

    // ------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------

    public String getCurrentSequenceId() {
        return currentSequenceId;
    }

    public void setCurrentSequenceId(String currentSequenceId) {
        this.currentSequenceId = currentSequenceId;
    }

    public int getCurrentStepIndex() {
        return currentStepIndex;
    }

    public void setCurrentStepIndex(int currentStepIndex) {
        this.currentStepIndex = currentStepIndex;
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
        AnimationData that = (AnimationData) o;
        return sequences.equals(that.sequences);
    }

    @Override
    public int hashCode() {
        return sequences.hashCode();
    }

    @Override
    public String toString() {
        return "AnimationData{sequences=" + sequences.size()
                + ", current=" + currentSequenceId + "}";
    }
}
