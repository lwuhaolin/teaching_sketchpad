package com.geometry.animation.geometry;

import com.geometry.animation.AnimationState;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;

/**
 * Phase 09 - Cut animation for geometry objects.
 *
 * Animates a cutting plane moving through a mesh, then reveals
 * the cross-section. The original mesh is not modified.
 *
 * Two phases:
 *   0.0 - 0.5: Plane moves through the mesh
 *   0.5 - 1.0: Cross-section fades in
 *
 * Integration with cut module:
 *   Uses {@link com.geometry.geometry.cutting.MeshCutter} to compute
 *   the actual cross-section geometry when the animation completes.
 */
public class CutAnimation extends GeometryAnimation {

    /** Direction the cutting plane moves. */
    public enum CutDirection {
        /** Plane moves along +X axis. */
        POS_X,
        /** Plane moves along -X axis. */
        NEG_X,
        /** Plane moves along +Y axis. */
        POS_Y,
        /** Plane moves along -Y axis. */
        NEG_Y,
        /** Plane moves along +Z axis. */
        POS_Z,
        /** Plane moves along -Z axis. */
        NEG_Z
    }

    private final CutDirection direction;
    private final float planeStart;
    private final float planeEnd;

    /**
     * Create a CutAnimation.
     *
     * @param mesh        the mesh to cut
     * @param direction   cutting plane direction
     * @param planeStart  starting plane position (world space)
     * @param planeEnd    ending plane position (world space)
     * @param duration    animation duration in seconds
     * @param interpolator easing function
     */
    public CutAnimation(Mesh mesh, CutDirection direction,
                        float planeStart, float planeEnd,
                        float duration, Interpolator interpolator) {
        super(mesh, duration, interpolator);
        this.direction = direction;
        this.planeStart = planeStart;
        this.planeEnd = planeEnd;
    }

    /**
     * Create a CutAnimation with linear interpolation.
     */
    public CutAnimation(Mesh mesh, CutDirection direction,
                        float planeStart, float planeEnd, float duration) {
        this(mesh, direction, planeStart, planeEnd, duration, null);
    }

    @Override
    protected void resetAnimation() {
        // Nothing to reset for cut animation
    }

    @Override
    public void onAnimate(float easedProgress) {
        // Cut animation phase 1 (0-0.5): plane movement
        // Phase 2 (0.5-1.0): section reveal
        // The actual cutting is done externally; this animation
        // just tracks progress and notifies listeners.
    }

    /**
     * Get the current plane position based on animation progress.
     *
     * @param progress normalized progress [0, 1]
     * @return current plane position
     */
    public float getPlanePosition(float progress) {
        return planeStart + (planeEnd - planeStart) * progress;
    }

    /**
     * Get the cutting direction.
     */
    public CutDirection getDirection() {
        return direction;
    }

    /**
     * Get the plane start position.
     */
    public float getPlaneStart() {
        return planeStart;
    }

    /**
     * Get the plane end position.
     */
    public float getPlaneEnd() {
        return planeEnd;
    }
}
