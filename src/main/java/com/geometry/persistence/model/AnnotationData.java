package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for teaching annotations.
 *
 * Stores all annotation data (text, arrow, highlight) for persistence.
 * Annotations are NOT stored as SceneObjects — they are standalone
 * data that can be re-created on load.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "annotations": [
 *     {
 *       "type": "TEXT",
 *       "text": "r = 5",
 *       "position": [0, 3, 0],
 *       "size": 0.5,
 *       "color": [255, 255, 0]
 *     },
 *     {
 *       "type": "ARROW",
 *       "start": [0, 0, 0],
 *       "end": [5, 0, 0],
 *       "arrowSize": 0.3,
 *       "color": [255, 255, 0]
 *     }
 *   ]
 * }
 * </pre>
 *
 * Not thread-safe.
 */
public class AnnotationData {

    /** The type of annotation being stored. */
    public enum AnnotationType {
        TEXT,
        ARROW,
        HIGHLIGHT
    }

    private final AnnotationType type;
    private String text;       // for TEXT
    private float[] position;  // for TEXT and HIGHLIGHT (target position)
    private float size;        // for TEXT
    private int colorR;        // RGB color
    private int colorG;
    private int colorB;
    private float[] start;     // for ARROW
    private float[] end;       // for ARROW
    private float arrowSize;   // for ARROW
    private String targetId;   // for HIGHLIGHT (SceneObject ID)
    private String state;      // for HIGHLIGHT (NORMAL, OUTLINE, GLOW)
    private float alpha;       // for HIGHLIGHT

    /**
     * Create a TEXT annotation data.
     */
    public AnnotationData(String text, float[] position, float size, int r, int g, int b) {
        this.type = AnnotationType.TEXT;
        this.text = text;
        this.position = position != null ? position.clone() : new float[]{0f, 0f, 0f};
        this.size = size;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.start = null;
        this.end = null;
        this.arrowSize = 0f;
        this.targetId = null;
        this.state = null;
        this.alpha = 0f;
    }

    /**
     * Create an ARROW annotation data.
     */
    public AnnotationData(float[] start, float[] end, float arrowSize, int r, int g, int b) {
        this.type = AnnotationType.ARROW;
        this.text = null;
        this.position = null;
        this.size = 0f;
        this.start = start != null ? start.clone() : new float[]{0f, 0f, 0f};
        this.end = end != null ? end.clone() : new float[]{0f, 0f, 0f};
        this.arrowSize = arrowSize;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.targetId = null;
        this.state = null;
        this.alpha = 0f;
    }

    /**
     * Create a HIGHLIGHT annotation data.
     */
    public AnnotationData(String targetId, String state, int r, int g, int b, float alpha) {
        this.type = AnnotationType.HIGHLIGHT;
        this.text = null;
        this.position = null;
        this.size = 0f;
        this.start = null;
        this.end = null;
        this.arrowSize = 0f;
        this.targetId = targetId;
        this.state = state;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
        this.alpha = alpha;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public AnnotationType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public float[] getPosition() {
        return position != null ? position.clone() : null;
    }

    public float getSize() {
        return size;
    }

    public int getColorR() {
        return colorR;
    }

    public int getColorG() {
        return colorG;
    }

    public int getColorB() {
        return colorB;
    }

    public float[] getStart() {
        return start != null ? start.clone() : null;
    }

    public float[] getEnd() {
        return end != null ? end.clone() : null;
    }

    public float getArrowSize() {
        return arrowSize;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getState() {
        return state;
    }

    public float getAlpha() {
        return alpha;
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
        AnnotationData that = (AnnotationData) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "AnnotationData{type=" + type
                + (text != null ? ", text='" + text + "'" : "")
                + (start != null ? ", start=" + java.util.Arrays.toString(start) : "")
                + (targetId != null ? ", target=" + targetId : "")
                + "}";
    }
}
