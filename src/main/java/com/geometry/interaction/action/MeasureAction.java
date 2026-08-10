package com.geometry.interaction.action;

import com.geometry.core.math.Vec2;

/**
 * Phase 06 - Action to measure distance between two points or angle between three points.
 *
 * Used by MeasureTool. Results are returned via {@link #getResult()} after execution.
 */
public class MeasureAction implements Action {

    public enum MeasureType {
        DISTANCE,
        ANGLE
    }

    private final MeasureType measureType;
    /** For DISTANCE: two points. For ANGLE: three points (vertex at index 1). */
    private final Vec2 p1;
    private final Vec2 p2;
    private final Vec2 p3;
    /** Result in world units (distance) or degrees (angle). Set after execute(). */
    private float result;

    /**
     * Create a distance measurement action.
     */
    public MeasureAction(Vec2 p1, Vec2 p2) {
        this(MeasureType.DISTANCE, p1, p2, null);
    }

    /**
     * Create an angle measurement action (three points, p2 is the vertex).
     */
    public MeasureAction(Vec2 p1, Vec2 p2, Vec2 p3) {
        this(MeasureType.ANGLE, p1, p2, p3);
    }

    private MeasureAction(MeasureType measureType, Vec2 p1, Vec2 p2, Vec2 p3) {
        if (measureType == null) {
            throw new IllegalArgumentException("MeasureType cannot be null");
        }
        this.measureType = measureType;
        this.p1 = p1 != null ? p1 : Vec2.ZERO;
        this.p2 = p2 != null ? p2 : Vec2.ZERO;
        this.p3 = p3;
    }

    @Override
    public void execute() {
        switch (measureType) {
            case DISTANCE:
                result = p1.subtract(p2).length();
                break;
            case ANGLE:
                result = computeAngle(p1, p2, p3);
                break;
            default:
                result = 0f;
        }
    }

    /**
     * Compute the angle in degrees at p2 formed by p1-p2-p3.
     */
    private float computeAngle(Vec2 a, Vec2 b, Vec2 c) {
        if (c == null) {
            return 0f;
        }
        float dx1 = a.x - b.x;
        float dy1 = a.y - b.y;
        float dx2 = c.x - b.x;
        float dy2 = c.y - b.y;
        float dot = dx1 * dx2 + dy1 * dy2;
        float len1 = (float) Math.sqrt(dx1 * dx1 + dy1 * dy1);
        float len2 = (float) Math.sqrt(dx2 * dx2 + dy2 * dy2);
        if (len1 == 0f || len2 == 0f) {
            return 0f;
        }
        float cosAngle = dot / (len1 * len2);
        // Clamp to [-1, 1] to avoid NaN from floating point errors
        cosAngle = Math.max(-1f, Math.min(1f, cosAngle));
        return (float) Math.toDegrees(Math.acos(cosAngle));
    }

    @Override
    public String getDescription() {
        return "MeasureAction{type=" + measureType
                + ", p1=" + p1 + ", p2=" + p2 + ", p3=" + p3
                + ", result=" + result + "}";
    }

    public MeasureType getMeasureType() {
        return measureType;
    }

    public Vec2 getP1() {
        return p1;
    }

    public Vec2 getP2() {
        return p2;
    }

    public Vec2 getP3() {
        return p3;
    }

    public float getResult() {
        return result;
    }
}
