package com.geometry.teaching.assistant;

import com.geometry.core.math.Vec3;
import com.geometry.renderer.Renderer;

/**
 * Phase 07 - Coordinate system helper for 3D teaching mode.
 *
 * Renders the X, Y, and Z axes as colored lines originating from the
 * world origin. Used in 3D mode to help students understand spatial
 * orientation:
 *   - X axis: red
 *   - Y axis: green
 *   - Z axis: blue
 *
 * Each axis is rendered as a line with an arrowhead at the positive end.
 *
 * Not thread-safe.
 */
public class CoordinateSystem implements com.geometry.teaching.annotation.Annotation {

    private final float axisLength;
    private final int originR;
    private final int originG;
    private final int originB;
    private final int xR;
    private final int xG;
    private final int xB;
    private final int yR;
    private final int yG;
    private final int yB;
    private final int zR;
    private final int zG;
    private final int zB;
    private boolean visible;

    // Default axis length
    private static final float DEFAULT_AXIS_LENGTH = 5.0f;

    // Default axis colors (standard RGB convention, [0, 255])
    private static final int DEFAULT_X_R = 255, DEFAULT_X_G = 51, DEFAULT_X_B = 51;
    private static final int DEFAULT_Y_R = 51, DEFAULT_Y_G = 255, DEFAULT_Y_B = 51;
    private static final int DEFAULT_Z_R = 51, DEFAULT_Z_G = 51, DEFAULT_Z_B = 255;

    /**
     * Create a coordinate system with default settings.
     */
    public CoordinateSystem() {
        this(DEFAULT_AXIS_LENGTH);
    }

    /**
     * Create a coordinate system with custom axis length.
     *
     * @param axisLength length of each axis in world units (must be positive)
     * @throws IllegalArgumentException if axisLength <= 0
     */
    public CoordinateSystem(float axisLength) {
        if (axisLength <= 0) {
            throw new IllegalArgumentException("Axis length must be positive, got " + axisLength);
        }
        this.axisLength = axisLength;
        this.originR = 180;
        this.originG = 180;
        this.originB = 180;
        this.xR = DEFAULT_X_R;
        this.xG = DEFAULT_X_G;
        this.xB = DEFAULT_X_B;
        this.yR = DEFAULT_Y_R;
        this.yG = DEFAULT_Y_G;
        this.yB = DEFAULT_Y_B;
        this.zR = DEFAULT_Z_R;
        this.zG = DEFAULT_Z_G;
        this.zB = DEFAULT_Z_B;
        this.visible = true;
    }

    // ------------------------------------------------------------------
    // Annotation interface
    // ------------------------------------------------------------------

    /**
     * Render the coordinate axes as annotation overlays.
     *
     * @param renderer the active renderer
     */
    @Override
    public void render(Renderer renderer) {
        if (renderer == null || !visible) {
            return;
        }
        renderer.renderAnnotation(this);
    }

    @Override
    public String getDescription() {
        return "CoordinateSystem{length=" + axisLength + ", visible=" + visible + "}";
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the axis length in world units.
     */
    public float getAxisLength() {
        return axisLength;
    }

    /**
     * Get the origin color (R, G, B in [0, 255]).
     */
    public int getOriginR() {
        return originR;
    }

    public int getOriginG() {
        return originG;
    }

    public int getOriginB() {
        return originB;
    }

    /**
     * Get the X axis color (R, G, B in [0, 255]).
     */
    public int getXColorR() {
        return xR;
    }

    public int getXColorG() {
        return xG;
    }

    public int getXColorB() {
        return xB;
    }

    /**
     * Get the Y axis color (R, G, B in [0, 255]).
     */
    public int getYColorR() {
        return yR;
    }

    public int getYColorG() {
        return yG;
    }

    public int getYColorB() {
        return yB;
    }

    /**
     * Get the Z axis color (R, G, B in [0, 255]).
     */
    public int getZColorR() {
        return zR;
    }

    public int getZColorG() {
        return zG;
    }

    public int getZColorB() {
        return zB;
    }

    /**
     * Check if the coordinate system is visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set coordinate system visibility.
     *
     * @param visible true to show, false to hide
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Get the axis end points for rendering.
     *
     * @return array of 3 Vec3: [X_axis_end, Y_axis_end, Z_axis_end]
     */
    public Vec3[] getAxisEndpoints() {
        return new Vec3[]{
                new Vec3(axisLength, 0f, 0f), // X axis
                new Vec3(0f, axisLength, 0f), // Y axis
                new Vec3(0f, 0f, axisLength)  // Z axis
        };
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
        CoordinateSystem that = (CoordinateSystem) o;
        return Float.compare(that.axisLength, axisLength) == 0
                && visible == that.visible
                && originR == that.originR
                && originG == that.originG
                && originB == that.originB
                && xR == that.xR && xG == that.xG && xB == that.xB
                && yR == that.yR && yG == that.yG && yB == that.yB
                && zR == that.zR && zG == that.zG && zB == that.zB;
    }

    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(axisLength);
        result = 31 * result + originR;
        result = 31 * result + originG;
        result = 31 * result + originB;
        result = 31 * result + xR;
        result = 31 * result + xG;
        result = 31 * result + xB;
        result = 31 * result + yR;
        result = 31 * result + yG;
        result = 31 * result + yB;
        result = 31 * result + zR;
        result = 31 * result + zG;
        result = 31 * result + zB;
        result = 31 * result + (visible ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CoordinateSystem{length=" + axisLength
                + ", x=(" + xR + "," + xG + "," + xB + ")"
                + ", y=(" + yR + "," + yG + "," + yB + ")"
                + ", z=(" + zR + "," + zG + "," + zB + ")"
                + ", visible=" + visible + "}";
    }
}
