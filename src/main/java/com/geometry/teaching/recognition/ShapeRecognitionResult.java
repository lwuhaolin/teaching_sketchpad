package com.geometry.teaching.recognition;

/**
 * Phase 07 - Result of shape recognition from a hand-drawn stroke.
 *
 * Contains:
 *   - The recognized shape type (CIRCLE, TRIANGLE, SQUARE, LINE, etc.)
 *   - A confidence score [0.0, 1.0]
 *   - The approximate geometry points for constructing the shape
 *
 * The {@code type} and {@code confidence} are set by the recognizer.
 * {@code points} contains the reconstructed geometry points in
 * world space, suitable for feeding into Construction objects.
 *
 * This is a result carrier — it does not perform any recognition itself.
 * Phase 07 only defines the interface; the actual AI recognition
 * is implemented in a future phase.
 *
 * Not thread-safe.
 */
public class ShapeRecognitionResult {

    /**
     * Recognized shape type.
     */
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
        /** Recognized as a point. */
        POINT,
        /** No shape recognized (low confidence). */
        UNKNOWN
    }

    private ShapeType type;
    private float confidence;
    private final float[] points; // flattened: [x1,y1, x2,y2, ...]

    /**
     * Create an empty recognition result.
     */
    public ShapeRecognitionResult() {
        this.type = ShapeType.UNKNOWN;
        this.confidence = 0f;
        this.points = new float[0];
    }

    /**
     * Create a recognition result with the given parameters.
     *
     * @param type       the recognized shape type
     * @param confidence confidence score [0.0, 1.0]
     * @param points     reconstructed geometry points (flattened xy pairs)
     * @throws IllegalArgumentException if confidence is out of range or points is null
     */
    public ShapeRecognitionResult(ShapeType type, float confidence, float[] points) {
        if (type == null) {
            throw new IllegalArgumentException("ShapeType cannot be null");
        }
        if (confidence < 0f || confidence > 1f) {
            throw new IllegalArgumentException("Confidence must be in [0, 1], got " + confidence);
        }
        if (points == null) {
            throw new IllegalArgumentException("Points array cannot be null");
        }
        this.type = type;
        this.confidence = confidence;
        this.points = points.clone(); // defensive copy
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the recognized shape type.
     */
    public ShapeType getType() {
        return type;
    }

    /**
     * Set the recognized shape type.
     *
     * @param type the new type
     */
    public void setType(ShapeType type) {
        if (type == null) {
            throw new IllegalArgumentException("ShapeType cannot be null");
        }
        this.type = type;
    }

    /**
     * Get the confidence score [0.0, 1.0].
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * Set the confidence score.
     *
     * @param confidence the new confidence [0.0, 1.0]
     * @throws IllegalArgumentException if confidence is out of range
     */
    public void setConfidence(float confidence) {
        if (confidence < 0f || confidence > 1f) {
            throw new IllegalArgumentException("Confidence must be in [0, 1], got " + confidence);
        }
        this.confidence = confidence;
    }

    /**
     * Get the reconstructed points (flattened xy pairs, z=0).
     */
    public float[] getPoints() {
        return points.clone(); // defensive copy
    }

    /**
     * Get the number of points (each point has x, y components).
     */
    public int getPointCount() {
        return points.length / 2;
    }

    /**
     * Get the x component of the n-th point.
     */
    public float getX(int n) {
        if (n < 0 || n * 2 + 1 >= points.length) {
            throw new IndexOutOfBoundsException("Point index " + n + " out of range");
        }
        return points[n * 2];
    }

    /**
     * Get the y component of the n-th point.
     */
    public float getY(int n) {
        if (n < 0 || n * 2 + 1 >= points.length) {
            throw new IndexOutOfBoundsException("Point index " + n + " out of range");
        }
        return points[n * 2 + 1];
    }

    /**
     * Check if the recognition was successful (type != UNKNOWN and confidence > 0).
     */
    public boolean isSuccess() {
        return type != ShapeType.UNKNOWN && confidence > 0f;
    }

    @Override
    public String toString() {
        return "ShapeRecognitionResult{type=" + type
                + ", confidence=" + confidence
                + ", points=" + points.length / 2 + "}";
    }
}
