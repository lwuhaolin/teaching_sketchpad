package com.geometry.animation.geometry;

import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;

/**
 * Phase 09 - Section (cross-section) animation.
 *
 * Highlights and animates the cross-section face created by a cut.
 * The section mesh fades in and optionally pulses to draw attention.
 *
 * Used after CutAnimation to emphasize the resulting cross-section.
 *
 * Example teaching scenarios:
 *   - Cylinder cut → circular section
 *   - Cone oblique cut → elliptical section
 *   - Cube cut → rectangular section
 */
public class SectionAnimation extends GeometryAnimation {

    private final Mesh sectionMesh;
    private final float highlightColorR;
    private final float highlightColorG;
    private final float highlightColorB;

    /**
     * Create a SectionAnimation.
     *
     * @param sourceMesh       the original mesh (for reference)
     * @param sectionMesh      the cross-section mesh to animate
     * @param duration         animation duration in seconds
     * @param highlightColorR  highlight red component [0, 1]
     * @param highlightColorG  highlight green component [0, 1]
     * @param highlightColorB  highlight blue component [0, 1]
     * @param interpolator     easing function
     */
    public SectionAnimation(Mesh sourceMesh, Mesh sectionMesh, float duration,
                            float highlightColorR, float highlightColorG, float highlightColorB,
                            Interpolator interpolator) {
        super(sourceMesh, duration, interpolator);
        if (sectionMesh == null) {
            throw new IllegalArgumentException("Section mesh cannot be null");
        }
        this.sectionMesh = sectionMesh;
        this.highlightColorR = highlightColorR;
        this.highlightColorG = highlightColorG;
        this.highlightColorB = highlightColorB;
    }

    /**
     * Create a SectionAnimation with red highlight and linear interpolation.
     */
    public SectionAnimation(Mesh sourceMesh, Mesh sectionMesh, float duration) {
        this(sourceMesh, sectionMesh, duration, 1.0f, 0.3f, 0.3f, null);
    }

    @Override
    protected void resetAnimation() {
        // Nothing to reset
    }

    @Override
    public void onAnimate(float easedProgress) {
        // Section animation controls visibility/opacity of the section mesh
        // The actual rendering is handled by the Renderer
    }

    /**
     * Get the section mesh.
     */
    public Mesh getSectionMesh() {
        return sectionMesh;
    }

    /**
     * Get the highlight color as an RGB array.
     */
    public float[] getHighlightColor() {
        return new float[]{highlightColorR, highlightColorG, highlightColorB};
    }

    /**
     * Compute the opacity for the section mesh at current progress.
     * Fades in from 0 to 1 during the animation.
     *
     * @param progress normalized progress [0, 1]
     * @return opacity value [0, 1]
     */
    public float getOpacity(float progress) {
        return progress;
    }
}
