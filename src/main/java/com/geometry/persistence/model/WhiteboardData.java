package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for whiteboard canvas state.
 *
 * Stores all stroke data and canvas metadata.
 *
 * Not thread-safe.
 */
public class WhiteboardData {

    private final List<StrokeData> strokes;
    private int canvasWidth;
    private int canvasHeight;

    /**
     * Create an empty WhiteboardData.
     */
    public WhiteboardData() {
        this.strokes = new ArrayList<>();
        this.canvasWidth = 800;
        this.canvasHeight = 600;
    }

    /**
     * Create a WhiteboardData with the given canvas dimensions.
     *
     * @param width  canvas width in pixels
     * @param height canvas height in pixels
     */
    public WhiteboardData(int width, int height) {
        this();
        this.canvasWidth = width > 0 ? width : 800;
        this.canvasHeight = height > 0 ? height : 600;
    }

    // ------------------------------------------------------------------
    // Stroke management
    // ------------------------------------------------------------------

    public void addStroke(StrokeData stroke) {
        if (stroke != null) {
            strokes.add(stroke);
        }
    }

    public void clearStrokes() {
        strokes.clear();
    }

    public List<StrokeData> getStrokes() {
        return new ArrayList<>(strokes);
    }

    public int getStrokeCount() {
        return strokes.size();
    }

    // ------------------------------------------------------------------
    // Canvas
    // ------------------------------------------------------------------

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth > 0 ? canvasWidth : 800;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight > 0 ? canvasHeight : 600;
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
        WhiteboardData that = (WhiteboardData) o;
        return strokes.equals(that.strokes);
    }

    @Override
    public int hashCode() {
        return strokes.hashCode();
    }

    @Override
    public String toString() {
        return "WhiteboardData{strokes=" + strokes.size()
                + ", size=" + canvasWidth + "x" + canvasHeight + "}";
    }
}
