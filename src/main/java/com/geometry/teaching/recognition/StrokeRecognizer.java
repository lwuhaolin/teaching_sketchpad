package com.geometry.teaching.recognition;

import com.geometry.interaction.event.Vec2;

import java.util.List;

/**
 * Phase 07 - Interface for stroke-based shape recognition.
 *
 * Takes a list of 2D points (a stroke) and returns a
 * {@link ShapeRecognitionResult} with the recognized shape type,
 * confidence, and reconstructed points.
 *
 * This is a PLAIN-INTERFACES-ONLY phase. The actual recognition
 * algorithm (AI/neural network based) is implemented in a future
 * phase. The default implementation returns UNKNOWN.
 *
 * Design:
 *   - Implementations can be swapped at runtime (e.g. simple geometry
 *     heuristic now, ML model later).
 *   - The interface does not depend on any rendering or UI classes.
 *   - Input is raw stroke points; output is a structured result.
 *
 * Not thread-safe.
 */
public interface StrokeRecognizer {

    /**
     * Recognize a shape from a hand-drawn stroke.
     *
     * @param stroke ordered list of 2D points representing the user's stroke
     * @return a ShapeRecognitionResult with type, confidence, and points
     * @throws IllegalArgumentException if stroke is null or empty
     */
    ShapeRecognitionResult recognize(List<Vec2> stroke);

    /**
     * Get the name of this recognizer for debugging and logging.
     *
     * @return recognizer name
     */
    String getName();
}
