package com.geometry.interaction.event;

import com.geometry.interaction.action.Action;

/**
 * Phase 07 - Event representing a completed pen stroke recognized as a shape.
 *
 * Emitted by {@link com.geometry.interaction.gesture.StrokeGestureRecognizer}
 * after a pen stroke (DOWN → MOVE* → UP) is analyzed. Carries the shape type
 * (CIRCLE, LINE, RECTANGLE, etc.), confidence, and the recognized points
 * suitable for creating geometry objects.
 *
 * Implements {@link Action} so it can be dispatched through the tool system
 * to a {@link com.geometry.tools.draw.DrawingPen} for geometry creation.
 *
 * Not thread-safe.
 */
public class StrokeGestureEvent extends InputEvent implements Action {

    /** The shape type recognized from the stroke. */
    private final ShapeType shapeType;

    /** Confidence score [0.0, 1.0] of the recognition. */
    private final float confidence;

    /** Screen-space points of the recognized stroke, in pixel coordinates. */
    private final Vec2[] points;

    /** Total distance traveled by the stroke (in pixels). */
    private final float strokeDistance;

    public enum ShapeType {
        /** Recognized as a circle or ellipse. */
        CIRCLE,
        /** Recognized as a triangle. */
        TRIANGLE,
        /** Recognized as a square or rectangle. */
        RECTANGLE,
        /** Recognized as a line segment. */
        LINE,
        /** Recognized as a polygon with more than 3 sides. */
        POLYGON,
        /** Recognized as a point (very short stroke). */
        POINT,
        /** No shape recognized (low confidence or too few points). */
        UNKNOWN
    }

    /**
     * Create a StrokeGestureEvent.
     *
     * @param shapeType    the recognized shape type
     * @param confidence   confidence score [0, 1]
     * @param points       the stroke points (will be copied)
     * @param strokeDistance total distance traveled in pixels
     * @throws IllegalArgumentException if shapeType is null or points is null
     */
    public StrokeGestureEvent(ShapeType shapeType, float confidence,
                               Vec2[] points, float strokeDistance) {
        if (shapeType == null) {
            throw new IllegalArgumentException("ShapeType cannot be null");
        }
        if (points == null) {
            throw new IllegalArgumentException("Points cannot be null");
        }
        this.shapeType = shapeType;
        this.confidence = Math.max(0f, Math.min(1f, confidence));
        this.points = points.clone();
        this.strokeDistance = strokeDistance;
    }

    // ------------------------------------------------------------------
    // Action interface
    // ------------------------------------------------------------------

    /**
     * Execute this action. StrokeGestureEvent is a no-op on execute()
     * because the actual geometry creation is handled by {@link com.geometry.tools.draw.DrawingPen}.
     * The DrawingPen receives this event via the Tool system and creates
     * the corresponding geometry object in the scene.
     */
    @Override
    public void execute() {
        // No-op: geometry creation is delegated to DrawingPen via Tool system.
    }

    /**
     * Get a description of this stroke gesture action.
     */
    @Override
    public String getDescription() {
        return "StrokeGestureEvent{type=" + shapeType
                + ", confidence=" + confidence
                + ", points=" + points.length + "}";
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the recognized shape type.
     */
    public ShapeType getShapeType() {
        return shapeType;
    }

    /**
     * Check if the recognized shape is a circle.
     */
    public boolean isCircle() {
        return shapeType == ShapeType.CIRCLE;
    }

    /**
     * Check if the recognized shape is a line.
     */
    public boolean isLine() {
        return shapeType == ShapeType.LINE;
    }

    /**
     * Check if the recognized shape is a rectangle.
     */
    public boolean isRectangle() {
        return shapeType == ShapeType.RECTANGLE;
    }

    /**
     * Check if the recognized shape is a triangle.
     */
    public boolean isTriangle() {
        return shapeType == ShapeType.TRIANGLE;
    }

    /**
     * Check if the recognized shape is a polygon.
     */
    public boolean isPolygon() {
        return shapeType == ShapeType.POLYGON;
    }

    /**
     * Check if the recognized shape is a point.
     */
    public boolean isPoint() {
        return shapeType == ShapeType.POINT;
    }

    /**
     * Check if the recognition was successful (type != UNKNOWN and confidence > 0).
     */
    public boolean isSuccess() {
        return shapeType != ShapeType.UNKNOWN && confidence > 0f;
    }

    /**
     * Get the confidence score [0.0, 1.0].
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * Get the stroke points.
     */
    public Vec2[] getPoints() {
        return points.clone();
    }

    /**
     * Get the number of stroke points.
     */
    public int getPointCount() {
        return points.length;
    }

    /**
     * Get the total stroke distance in pixels.
     */
    public float getStrokeDistance() {
        return strokeDistance;
    }

    @Override
    public String toString() {
        return "StrokeGestureEvent{type=" + shapeType
                + ", confidence=" + confidence
                + ", points=" + points.length
                + ", distance=" + strokeDistance + "}";
    }
}
