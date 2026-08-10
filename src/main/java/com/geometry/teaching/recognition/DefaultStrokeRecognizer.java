package com.geometry.teaching.recognition;

import com.geometry.interaction.event.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 07 - Default stroke recognizer stub.
 *
 * Returns UNKNOWN for all inputs. This stub allows the teaching
 * pipeline to function without an AI recognition backend.
 *
 * A real implementation would analyse the stroke geometry
 * (curvature, closure, corner count) to determine the shape type.
 *
 * Not thread-safe.
 */
public class DefaultStrokeRecognizer implements StrokeRecognizer {

    private static final String NAME = "default-stub";

    /**
     * Create a default recognizer.
     */
    public DefaultStrokeRecognizer() {
    }

    // ------------------------------------------------------------------
    // StrokeRecognizer interface
    // ------------------------------------------------------------------

    /**
     * Recognize a stroke.
     *
     * Always returns UNKNOWN with 0.0 confidence.
     *
     * @param stroke ordered list of 2D points
     * @return an UNKNOWN ShapeRecognitionResult
     * @throws IllegalArgumentException if stroke is null or empty
     */
    @Override
    public ShapeRecognitionResult recognize(List<Vec2> stroke) {
        if (stroke == null || stroke.isEmpty()) {
            throw new IllegalArgumentException("Stroke cannot be null or empty");
        }
        // Stub: return unknown result
        return new ShapeRecognitionResult(
                ShapeRecognitionResult.ShapeType.UNKNOWN,
                0.0f,
                new float[0]
        );
    }

    /**
     * Get the recognizer name.
     */
    @Override
    public String getName() {
        return NAME;
    }
}
