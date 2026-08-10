package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for whiteboard strokes.
 *
 * Stores hand-drawn stroke data including:
 *   - List of points in screen space
 *   - Timestamp
 *   - Pressure (if available from input device)
 *
 * Points are stored as screen-space (pixel) coordinates.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "points": [[100, 200], [105, 198], [112, 195], ...],
 *   "pressure": 0.8,
 *   "timestamp": 1234567890
 * }
 * </pre>
 *
 * Not thread-safe.
 */
public class StrokeData {

    private final List<float[]> points;
    private float pressure;
    private long timestamp;

    /**
     * Create a StrokeData with the given points and pressure.
     *
     * @param points    list of [x, y] screen-space coordinates
     * @param pressure  pen pressure [0.0, 1.0]
     * @param timestamp millisecond timestamp
     */
    public StrokeData(List<float[]> points, float pressure, long timestamp) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Points cannot be null or empty");
        }
        this.points = new ArrayList<>(points);
        this.pressure = Math.max(0f, Math.min(1f, pressure));
        this.timestamp = timestamp;
    }

    /**
     * Create a StrokeData with the given points.
     *
     * @param points list of [x, y] screen-space coordinates
     */
    public StrokeData(List<float[]> points) {
        this(points, 0.5f, System.currentTimeMillis());
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public List<float[]> getPoints() {
        return new ArrayList<>(points);
    }

    public int getPointCount() {
        return points.size();
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = Math.max(0f, Math.min(1f, pressure));
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Get a specific point by index.
     *
     * @param index point index
     * @return [x, y] array
     */
    public float[] getPoint(int index) {
        if (index < 0 || index >= points.size()) {
            throw new IndexOutOfBoundsException(
                    "Point index " + index + " out of range [0, " + (points.size() - 1) + "]");
        }
        return points.get(index).clone();
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
        StrokeData that = (StrokeData) o;
        return points.equals(that.points);
    }

    @Override
    public int hashCode() {
        return points.hashCode();
    }

    @Override
    public String toString() {
        return "StrokeData{points=" + points.size()
                + ", pressure=" + pressure
                + ", timestamp=" + timestamp + "}";
    }
}
