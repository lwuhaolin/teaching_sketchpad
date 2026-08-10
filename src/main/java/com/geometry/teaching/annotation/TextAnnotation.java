package com.geometry.teaching.annotation;

import com.geometry.core.math.Vec3;
import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Text annotation for labeling geometry objects.
 *
 * Renders a text label at a fixed 3D world position.
 * Used for:
 *   - Point labels ("A", "B", "C")
 *   - Dimension labels ("r = 5", "h = 10")
 *   - Formula annotations ("V = πr²h")
 *
 * Position is in world space (z = 0 for 2D mode, arbitrary for 3D).
 * Size is in world units (scales with zoom in 3D mode).
 *
 * Not thread-safe.
 */
public class TextAnnotation implements Annotation {

    private final String text;
    private final Vec3 position;
    private final float size;
    private final int colorR;
    private final int colorG;
    private final int colorB;

    // Default colors (white text)
    private static final int DEFAULT_COLOR_R = 255;
    private static final int DEFAULT_COLOR_G = 255;
    private static final int DEFAULT_COLOR_B = 255;

    /**
     * Create a text annotation.
     *
     * @param text     the label text (e.g. "r = 5")
     * @param position world-space position to render at
     * @param size     text size in world units (e.g. 0.5f for half a unit)
     */
    public TextAnnotation(String text, Vec3 position, float size) {
        this(text, position, size, DEFAULT_COLOR_R, DEFAULT_COLOR_G, DEFAULT_COLOR_B);
    }

    /**
     * Create a text annotation with custom color.
     *
     * @param text     the label text
     * @param position world-space position
     * @param size     text size in world units
     * @param r        red component [0, 255]
     * @param g        green component [0, 255]
     * @param b        blue component [0, 255]
     * @throws IllegalArgumentException if text is null or empty
     */
    public TextAnnotation(String text, Vec3 position, float size, int r, int g, int b) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }
        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive, got " + size);
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            throw new IllegalArgumentException("Color components must be in [0, 255]");
        }
        this.text = text;
        this.position = position;
        this.size = size;
        this.colorR = r;
        this.colorG = g;
        this.colorB = b;
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Render this text annotation.
     *
     * For OpenGL-based renderers, this draws a small quad with the text
     * at the specified position. For headless/mock renderers, the call
     * is a no-op.
     *
     * @param renderer the active renderer
     */
    @Override
    public void render(Renderer renderer) {
        if (renderer == null) {
            return;
        }
        renderer.renderAnnotation(this);
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    @Override
    public String getDescription() {
        return "TextAnnotation{text='" + text + "', pos=" + position + "}";
    }

    /**
     * Get the text content.
     */
    public String getText() {
        return text;
    }

    /**
     * Get the world-space position.
     */
    public Vec3 getPosition() {
        return position;
    }

    /**
     * Get the text size in world units.
     */
    public float getSize() {
        return size;
    }

    /**
     * Get the red component [0, 255].
     */
    public int getColorR() {
        return colorR;
    }

    /**
     * Get the green component [0, 255].
     */
    public int getColorG() {
        return colorG;
    }

    /**
     * Get the blue component [0, 255].
     */
    public int getColorB() {
        return colorB;
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
        TextAnnotation that = (TextAnnotation) o;
        return Float.compare(that.size, size) == 0
                && colorR == that.colorR
                && colorG == that.colorG
                && colorB == that.colorB
                && text.equals(that.text)
                && position.equals(that.position);
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + position.hashCode();
        result = 31 * result + Float.floatToIntBits(size);
        result = 31 * result + colorR;
        result = 31 * result + colorG;
        result = 31 * result + colorB;
        return result;
    }

    @Override
    public String toString() {
        return "TextAnnotation{text='" + text + "', pos=" + position
                + ", size=" + size + ", color=(" + colorR + "," + colorG + "," + colorB + ")}";
    }
}
