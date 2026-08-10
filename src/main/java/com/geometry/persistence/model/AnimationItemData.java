package com.geometry.persistence.model;

/**
 * Phase 10 - Data model for a single animation item in a sequence.
 *
 * Stores all the parameters needed to recreate an Animation on load.
 * The animation type determines which fields are used.
 *
 * Supported types:
 *   - MOVE:    uses fromPosition and toPosition
 *   - ROTATE:  uses fromRotation and toRotation
 *   - SCALE:   uses fromScale and toScale
 *   - UNFOLD:  uses targetId and unfoldType
 *   - EXPLODE: uses targetId
 *   - CUT:     uses targetId
 *   - SECTION: uses targetId
 *
 * Not thread-safe.
 */
public class AnimationItemData {

    /** Animation type constants matching the enum in AnimationRegistry. */
    public enum AnimationItemType {
        MOVE,
        ROTATE,
        SCALE,
        UNFOLD,
        EXPLODE,
        CUT,
        SECTION
    }

    private String name;
    private AnimationItemType type;
    private String targetId;
    private float[] fromPosition;
    private float[] toPosition;
    private float[] fromRotation;
    private float[] toRotation;
    private float[] fromScale;
    private float[] toScale;
    private String unfoldType;   // for UNFOLD: CUBE, CYLINDER, CONE
    private float duration;
    private String interpolator; // "LINEAR" or "EASE"
    private float delaySeconds;

    /**
     * Create an AnimationItemData.
     *
     * @param name       human-readable name
     * @param type       animation type
     * @param targetId   target SceneObject ID
     * @param fromPos    start position [x, y, z] (may be null)
     * @param toPos      end position [x, y, z] (may be null)
     * @param fromRot    start rotation [pitch, yaw, roll] (may be null)
     * @param toRot      end rotation [pitch, yaw, roll] (may be null)
     * @param fromScale  start scale [x, y, z] (may be null)
     * @param toScale    end scale [x, y, z] (may be null)
     * @param unfoldType unfold type (may be null)
     * @param duration   animation duration in seconds
     * @param interpolator interpolator type ("LINEAR" or "EASE")
     * @param delaySeconds delay before start
     */
    public AnimationItemData(String name, AnimationItemType type, String targetId,
                             float[] fromPos, float[] toPos,
                             float[] fromRot, float[] toRot,
                             float[] fromScale, float[] toScale,
                             String unfoldType, float duration,
                             String interpolator, float delaySeconds) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.name = name;
        this.type = type;
        this.targetId = targetId;
        this.fromPosition = fromPos != null ? fromPos.clone() : null;
        this.toPosition = toPos != null ? toPos.clone() : null;
        this.fromRotation = fromRot != null ? fromRot.clone() : null;
        this.toRotation = toRot != null ? toRot.clone() : null;
        this.fromScale = fromScale != null ? fromScale.clone() : null;
        this.toScale = toScale != null ? toScale.clone() : null;
        this.unfoldType = unfoldType;
        this.duration = duration > 0 ? duration : 1f;
        this.interpolator = interpolator != null ? interpolator : "LINEAR";
        this.delaySeconds = delaySeconds >= 0 ? delaySeconds : 0f;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public AnimationItemType getType() {
        return type;
    }

    public void setType(AnimationItemType type) {
        this.type = type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public float[] getFromPosition() {
        return fromPosition != null ? fromPosition.clone() : null;
    }

    public float[] getToPosition() {
        return toPosition != null ? toPosition.clone() : null;
    }

    public float[] getFromRotation() {
        return fromRotation != null ? fromRotation.clone() : null;
    }

    public float[] getToRotation() {
        return toRotation != null ? toRotation.clone() : null;
    }

    public float[] getFromScale() {
        return fromScale != null ? fromScale.clone() : null;
    }

    public float[] getToScale() {
        return toScale != null ? toScale.clone() : null;
    }

    public String getUnfoldType() {
        return unfoldType;
    }

    public void setUnfoldType(String unfoldType) {
        this.unfoldType = unfoldType;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration > 0 ? duration : 1f;
    }

    public String getInterpolator() {
        return interpolator;
    }

    public void setInterpolator(String interpolator) {
        this.interpolator = interpolator != null ? interpolator : "LINEAR";
    }

    public float getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(float delaySeconds) {
        this.delaySeconds = delaySeconds >= 0 ? delaySeconds : 0f;
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
        AnimationItemData that = (AnimationItemData) o;
        return name.equals(that.name) && type == that.type;
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + type.hashCode();
    }

    @Override
    public String toString() {
        return "AnimationItemData{name='" + name + "', type=" + type
                + ", target=" + targetId + ", duration=" + duration + "}";
    }
}
