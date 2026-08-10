package com.geometry.interaction.event;

/**
 * Phase 05 - Event emitted by a GestureRecognizer after detecting a gesture.
 *
 * Wraps the recognised gesture type together with the accumulated parameters
 * (e.g. drag distance, pinch scale factor, rotation angle).
 *
 * The GestureEvent is dispatched to the Action system, which translates it
 * into scene modifications on SceneObjects.
 */
public class GestureEvent extends InputEvent {

    /** The type of gesture that was recognised. */
    public enum GestureType {
        DRAG,
        PINCH,
        ROTATE,
        TAP
    }

    private final GestureType gestureType;

    /** Drag distance in world units (or pixels for uncalibrated input). */
    private final float distance;

    /** Pinch scale factor (e.g. 1.5 = 50% larger). */
    private final float scaleFactor;

    /** Rotation angle in degrees. */
    private final float angleDegrees;

    /** Origin point of the gesture in screen space (pixels). */
    private final Vec2 origin;

    public GestureEvent(GestureType gestureType, float distance,
                        float scaleFactor, float angleDegrees, Vec2 origin) {
        super();
        this.gestureType = gestureType;
        this.distance = distance;
        this.scaleFactor = scaleFactor;
        this.angleDegrees = angleDegrees;
        this.origin = origin;
    }

    public GestureType getGestureType() {
        return gestureType;
    }

    public float getDistance() {
        return distance;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public float getAngleDegrees() {
        return angleDegrees;
    }

    public Vec2 getOrigin() {
        return origin;
    }

    public boolean isDrag() {
        return gestureType == GestureType.DRAG;
    }

    public boolean isPinch() {
        return gestureType == GestureType.PINCH;
    }

    public boolean isRotate() {
        return gestureType == GestureType.ROTATE;
    }

    public boolean isTap() {
        return gestureType == GestureType.TAP;
    }

    @Override
    public String toString() {
        return "GestureEvent{type=" + gestureType
                + ", distance=" + distance
                + ", scale=" + scaleFactor
                + ", angle=" + angleDegrees
                + ", origin=" + origin + "}";
    }
}
