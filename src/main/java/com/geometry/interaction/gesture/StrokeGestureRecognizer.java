package com.geometry.interaction.gesture;

import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.StrokeGestureEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.teaching.recognition.StrokeRecognizer;
import com.geometry.teaching.recognition.DefaultStrokeRecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 07 - Recognizes shapes from pen strokes.
 *
 * Tracks pen/touch DOWN → MOVE* → UP sequences and, on UP,
 * analyzes the collected stroke points to determine the shape
 * type (CIRCLE, LINE, RECTANGLE, TRIANGLE, POLYGON, POINT).
 *
 * Uses the configured {@link StrokeRecognizer} (defaults to
 * {@link DefaultStrokeRecognizer}) for shape analysis.
 *
 * Unlike {@link GestureRecognizer} (which handles drag/pinch/rotate/tap),
 * this recognizer handles pen-stroke-to-shape recognition.
 *
 * In WHITEBOARD mode with a PenDevice registered, pen strokes
 * are automatically routed to this recognizer. In DESKTOP mode,
 * pen strokes are ignored (use the standard GestureRecognizer).
 *
 * Not thread-safe.
 */
public class StrokeGestureRecognizer {

    /** Minimum number of points needed for shape recognition. */
    private static final int MIN_POINTS = 3;

    /** Maximum points to buffer before dropping old ones (prevents memory bloat). */
    private static final int MAX_BUFFERED_POINTS = 500;

    /** Maximum stroke distance (pixels) to consider as a point. */
    private static final float POINT_MAX_DISTANCE = 5f;

    private final StrokeRecognizer recognizer;
    private final List<Vec2> strokeBuffer;
    private boolean recording;
    private float strokeDistance;
    private Vec2 strokeStart;

    /**
     * Create a StrokeGestureRecognizer with the default recognizer.
     */
    public StrokeGestureRecognizer() {
        this(new DefaultStrokeRecognizer());
    }

    /**
     * Create a StrokeGestureRecognizer with the given recognizer.
     *
     * @param recognizer the stroke shape recognizer (must not be null)
     */
    public StrokeGestureRecognizer(StrokeRecognizer recognizer) {
        if (recognizer == null) {
            throw new IllegalArgumentException("StrokeRecognizer cannot be null");
        }
        this.recognizer = recognizer;
        this.strokeBuffer = new ArrayList<>();
        this.recording = false;
        this.strokeDistance = 0f;
        this.strokeStart = null;
    }

    // ------------------------------------------------------------------
    // Event processing
    // ------------------------------------------------------------------

    /**
     * Process a pointer event. When a PEN DOWN starts a stroke, begins
     * recording. On MOVE, accumulates points. On UP, analyzes the stroke
     * and returns a {@link StrokeGestureEvent} if recognized.
     *
     * Other pointer types (MOUSE, TOUCH) are ignored.
     *
     * @param event the pointer event
     * @return a StrokeGestureEvent if a stroke was completed, or null
     */
    public StrokeGestureEvent process(PointerEvent event) {
        if (event == null) {
            return null;
        }

        // Only process PEN input
        if (event.getPointerType() != PointerEvent.PointerType.PEN) {
            return null;
        }

        switch (event.getEventType()) {
            case DOWN:
                onStrokeDown(event);
                return null;
            case MOVE:
                onStrokeMove(event);
                return null;
            case UP:
                return onStrokeUp(event);
            default:
                return null;
        }
    }

    /**
     * Process a list of pointer events. Returns all completed stroke events.
     *
     * @param events list of pointer events
     * @return list of StrokeGestureEvents (may be empty)
     */
    public List<StrokeGestureEvent> process(List<PointerEvent> events) {
        List<StrokeGestureEvent> results = new ArrayList<>();
        if (events == null) {
            return results;
        }
        for (PointerEvent event : events) {
            StrokeGestureEvent result = process(event);
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    // ------------------------------------------------------------------
    // Stroke lifecycle
    // ------------------------------------------------------------------

    private void onStrokeDown(PointerEvent event) {
        Vec2 pos = event.getPosition();
        this.strokeBuffer.clear();
        this.strokeBuffer.add(pos);
        this.strokeDistance = 0f;
        this.strokeStart = pos;
        this.recording = true;
    }

    private void onStrokeMove(PointerEvent event) {
        if (!recording) {
            return;
        }
        Vec2 pos = event.getPosition();
        strokeBuffer.add(pos);
        // Keep buffer manageable
        if (strokeBuffer.size() > MAX_BUFFERED_POINTS) {
            // Drop old points, keep last MAX_BUFFERED_POINTS
            strokeBuffer.subList(0, strokeBuffer.size() - MAX_BUFFERED_POINTS).clear();
        }
        Vec2 delta = event.getDelta();
        strokeDistance += delta.length();
    }

    private StrokeGestureEvent onStrokeUp(PointerEvent event) {
        if (!recording) {
            return null;
        }
        recording = false;
        Vec2 lastPos = event.getPosition();
        // Add final position
        if (strokeBuffer.isEmpty() || !strokeBuffer.get(strokeBuffer.size() - 1).equals(lastPos)) {
            strokeBuffer.add(lastPos);
        }

        // Compute total distance including last segment
        if (strokeStart != null && !strokeBuffer.isEmpty()) {
            Vec2 first = strokeBuffer.get(0);
            float lastSegDist = lastPos.subtract(first).length();
            // Add distance from last recorded point to final up position
            if (strokeBuffer.size() >= 2) {
                Vec2 prev = strokeBuffer.get(strokeBuffer.size() - 2);
                strokeDistance += prev.subtract(lastPos).length();
            }
        }

        return analyzeStroke();
    }

    // ------------------------------------------------------------------
    // Shape analysis
    // ------------------------------------------------------------------

    /**
     * Analyze the current stroke buffer and produce a StrokeGestureEvent.
     *
     * @return the gesture event, or null if the stroke is too short
     */
    private StrokeGestureEvent analyzeStroke() {
        if (strokeBuffer.size() < MIN_POINTS) {
            // Too few points — treat as a point
            Vec2 center = strokeBuffer.isEmpty() ? Vec2.ZERO : strokeBuffer.get(0);
            return new StrokeGestureEvent(
                    StrokeGestureEvent.ShapeType.POINT,
                    0.9f,
                    new Vec2[]{center},
                    strokeDistance
            );
        }

        // Delegate to the configured recognizer for shape analysis
        com.geometry.teaching.recognition.ShapeRecognitionResult result =
                recognizer.recognize(new ArrayList<>(strokeBuffer));

        // Map recognition result to StrokeGestureEvent
        StrokeGestureEvent.ShapeType shapeType = mapShapeType(result.getType());
        float confidence = result.getConfidence();

        // Convert recognized points back to screen-space Vec2 array
        float[] recPoints = result.getPoints();
        Vec2[] screenPoints;
        if (recPoints.length == 0) {
            // Fallback: use the raw stroke buffer points
            screenPoints = strokeBuffer.toArray(new Vec2[0]);
        } else {
            screenPoints = new Vec2[recPoints.length / 2];
            for (int i = 0; i < screenPoints.length; i++) {
                screenPoints[i] = new Vec2(recPoints[i * 2], recPoints[i * 2 + 1]);
            }
        }

        return new StrokeGestureEvent(
                shapeType,
                confidence,
                screenPoints,
                strokeDistance
        );
    }

    /**
     * Map a ShapeRecognitionResult.ShapeType to a StrokeGestureEvent.ShapeType.
     */
    private StrokeGestureEvent.ShapeType mapShapeType(
            com.geometry.teaching.recognition.ShapeRecognitionResult.ShapeType type) {
        if (type == null) {
            return StrokeGestureEvent.ShapeType.UNKNOWN;
        }
        switch (type) {
            case CIRCLE:
                return StrokeGestureEvent.ShapeType.CIRCLE;
            case TRIANGLE:
                return StrokeGestureEvent.ShapeType.TRIANGLE;
            case RECTANGLE:
                return StrokeGestureEvent.ShapeType.RECTANGLE;
            case LINE:
                return StrokeGestureEvent.ShapeType.LINE;
            case POLYGON:
                return StrokeGestureEvent.ShapeType.POLYGON;
            case POINT:
                return StrokeGestureEvent.ShapeType.POINT;
            default:
                return StrokeGestureEvent.ShapeType.UNKNOWN;
        }
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the configured recognizer name.
     */
    public String getName() {
        return recognizer.getName();
    }

    /**
     * Check if a stroke is currently being recorded.
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * Get the number of points currently buffered.
     */
    public int getBufferedPointCount() {
        return strokeBuffer.size();
    }

    /**
     * Reset the recognizer state (clears any in-progress stroke).
     */
    public void reset() {
        this.strokeBuffer.clear();
        this.recording = false;
        this.strokeDistance = 0f;
        this.strokeStart = null;
    }
}
