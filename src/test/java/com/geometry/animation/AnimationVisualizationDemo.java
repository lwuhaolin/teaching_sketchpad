package com.geometry.animation;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationManager;
import com.geometry.animation.AnimationSequence;
import com.geometry.animation.geometry.ExplodeAnimation;
import com.geometry.animation.geometry.GeometryAnimation;
import com.geometry.animation.geometry.UnfoldAnimation;
import com.geometry.animation.interaction.AnimationController;
import com.geometry.animation.interaction.InteractiveAnimation;
import com.geometry.animation.interpolation.EaseInterpolator;
import com.geometry.animation.transform.RotateAnimation;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.math.Vec3;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

/**
 * Phase 09 - Demo for the Animation & Visualization System.
 *
 * Demonstrates:
 *   Demo1: Cube rotation animation
 *   Demo2: Cube unfold animation
 *   Demo3: Cylinder unfold animation
 *   Demo4: Cube explode animation
 *
 * Run with: java -cp ... com.geometry.animation.AnimationVisualizationDemo
 */
public class AnimationVisualizationDemo {

    private final Scene scene;
    private final AnimationController controller;
    private final AnimationManager animManager;

    public AnimationVisualizationDemo() {
        this.scene = new Scene();
        this.controller = new AnimationController(scene);
        this.animManager = controller.getAnimationManager();
    }

    // ------------------------------------------------------------------
    // Demo 1: Cube Rotation
    // ------------------------------------------------------------------

    /**
     * Demo 1: Rotate a cube continuously.
     * Shows basic TransformAnimation usage.
     */
    public void demoRotateCube() {
        System.out.println("=== Demo 1: Cube Rotation ===");

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_rotate", cube);

        // Rotate 360 degrees around Y axis in 3 seconds
        RotateAnimation rotateAnim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 360f, 0f),
                3f,
                EaseInterpolator.getInstance()
        );

        animManager.addAnimation(rotateAnim);

        // Simulate a few frames
        simulateAnimation(rotateAnim, 3.1f, 0.1f);

        System.out.println("Cube rotation demo complete.");
        System.out.println("Final state: " + rotateAnim.getState());
        System.out.println("Cube transform: " + cube.getTransform());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Demo 2: Cube Unfold
    // ------------------------------------------------------------------

    /**
     * Demo 2: Unfold a cube into its net.
     * Shows GeometryAnimation with face-level animation.
     */
    public void demoCubeUnfold() {
        System.out.println("=== Demo 2: Cube Unfold ===");

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_unfold", cube);

        UnfoldAnimation unfoldAnim = new UnfoldAnimation(
                cube.getMesh(),
                3f,
                UnfoldAnimation.UnfoldType.CUBE,
                EaseInterpolator.getInstance()
        );

        animManager.addAnimation(unfoldAnim);

        // Simulate animation
        simulateGeometryAnimation(unfoldAnim, 3.0f, 0.1f);

        System.out.println("Cube unfold demo complete.");
        System.out.println("Final state: " + unfoldAnim.getState());
        System.out.println("Animated mesh faces: " + unfoldAnim.getFaceAnimator().getAnimatedMesh(1.0f).getFaceCount());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Demo 3: Cylinder Unfold
    // ------------------------------------------------------------------

    /**
     * Demo 3: Unfold a cylinder into a rectangle.
     * Shows Cylinder-specific unfold logic.
     */
    public void demoCylinderUnfold() {
        System.out.println("=== Demo 3: Cylinder Unfold ===");

        Cylinder cylinder = new Cylinder(1.5f, 3f, 16);
        SceneObject sceneCyl = scene.addObject("cylinder_unfold", cylinder);

        UnfoldAnimation unfoldAnim = new UnfoldAnimation(
                cylinder.getMesh(),
                4f,
                UnfoldAnimation.UnfoldType.CYLINDER,
                EaseInterpolator.getInstance()
        );

        animManager.addAnimation(unfoldAnim);

        // Simulate animation
        simulateGeometryAnimation(unfoldAnim, 4.0f, 0.1f);

        System.out.println("Cylinder unfold demo complete.");
        System.out.println("Final state: " + unfoldAnim.getState());
        System.out.println("Animated mesh faces: " + unfoldAnim.getFaceAnimator().getAnimatedMesh(1.0f).getFaceCount());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Demo 4: Cube Explode
    // ------------------------------------------------------------------

    /**
     * Demo 4: Explode a cube to show its faces.
     * Shows face-level animation for structural teaching.
     */
    public void demoCubeExplode() {
        System.out.println("=== Demo 4: Cube Explode ===");

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject sceneCube = scene.addObject("cube_explode", cube);

        ExplodeAnimation explodeAnim = new ExplodeAnimation(
                cube.getMesh(),
                2.5f,
                EaseInterpolator.getInstance()
        );

        animManager.addAnimation(explodeAnim);

        // Simulate animation
        simulateGeometryAnimation(explodeAnim, 2.5f, 0.1f);

        System.out.println("Cube explode demo complete.");
        System.out.println("Final state: " + explodeAnim.getState());
        System.out.println("Animated mesh faces: " + explodeAnim.getFaceAnimator().getAnimatedMesh(1.0f).getFaceCount());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Demo 5: Animation Sequence
    // ------------------------------------------------------------------

    /**
     * Demo 5: Multi-step animation sequence.
     * Shows how to chain animations for teaching lessons.
     */
    public void demoAnimationSequence() {
        System.out.println("=== Demo 5: Animation Sequence ===");

        Cube cube = new Cube(2f, 2f, 2f);
        scene.addObject("cube_seq", cube);

        AnimationSequence seq = new AnimationSequence("Cube Teaching Sequence");
        seq.addStep("Rotate",
                new RotateAnimation(cube,
                        new Vec3(0f, 0f, 0f),
                        new Vec3(0f, 0f, 0f),
                        new Vec3(0f, 90f, 0f),
                        1.5f,
                        EaseInterpolator.getInstance()));
        seq.addStep("Scale Up",
                new com.geometry.animation.transform.ScaleAnimation(
                        cube,
                        new Vec3(0f, 0f, 0f),
                        new Vec3(1f, 1f, 1f),
                        new Vec3(2f, 2f, 2f),
                        1.5f,
                        EaseInterpolator.getInstance()));
        seq.addStep("Explode",
                new ExplodeAnimation(cube.getMesh(), 2f, EaseInterpolator.getInstance()));

        // Simulate sequence
        seq.start();
        simulateSequence(seq, 5.0f, 0.2f);

        System.out.println("Animation sequence demo complete.");
        System.out.println("Final state: " + seq.getState());
        System.out.println("Current step: " + seq.getCurrentStepName());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Demo 6: Interactive Animation
    // ------------------------------------------------------------------

    /**
     * Demo 6: Interactive animation control.
     * Shows how to scrub animation progress manually.
     */
    public void demoInteractiveAnimation() {
        System.out.println("=== Demo 6: Interactive Animation ===");

        Cube cube = new Cube(2f, 2f, 2f);
        scene.addObject("cube_interactive", cube);

        RotateAnimation rotateAnim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 180f, 0f),
                4f,
                EaseInterpolator.getInstance()
        );

        InteractiveAnimation interactive = controller.makeInteractive(rotateAnim);
        animManager.addAnimation(interactive);

        // Simulate normal play
        interactive.start();
        interactive.update(1.0f);
        System.out.println("After 1s: progress=" + interactive.getProgress());

        // Scrub to middle
        interactive.setProgress(0.5f);
        System.out.println("After scrub to 0.5: progress=" + interactive.getProgress());
        System.out.println("Interactive mode: " + interactive.isInteractiveMode());

        // Resume normal play
        interactive.resume();
        interactive.update(0.5f);
        System.out.println("After resume + 0.5s: progress=" + interactive.getProgress());
        System.out.println("Interactive mode: " + interactive.isInteractiveMode());
        System.out.println();
    }

    // ------------------------------------------------------------------
    // Simulation helpers
    // ------------------------------------------------------------------

    private void simulateAnimation(Animation anim, float totalDuration, float dt) {
        anim.start();
        float elapsed = 0f;
        while (elapsed < totalDuration) {
            float step = Math.min(dt, totalDuration - elapsed);
            anim.update(step);
            elapsed += step;
            System.out.println("  t=" + String.format("%.1f", elapsed)
                    + " state=" + anim.getState()
                    + " progress=" + String.format("%.2f", anim.getProgress()));
        }
    }

    private void simulateGeometryAnimation(GeometryAnimation anim, float totalDuration, float dt) {
        anim.start();
        float elapsed = 0f;
        while (elapsed < totalDuration) {
            float step = Math.min(dt, totalDuration - elapsed);
            anim.update(step);
            elapsed += step;
            System.out.println("  t=" + String.format("%.1f", elapsed)
                    + " state=" + anim.getState()
                    + " progress=" + String.format("%.2f", anim.getProgress())
                    + " faces=" + anim.getFaceAnimator().getAnimatedMesh(anim.getProgress()).getFaceCount());
        }
    }

    private void simulateSequence(AnimationSequence seq, float totalDuration, float dt) {
        float elapsed = 0f;
        while (elapsed < totalDuration && !seq.isFinished()) {
            float step = Math.min(dt, totalDuration - elapsed);
            seq.update(step);
            elapsed += step;
            System.out.println("  t=" + String.format("%.1f", elapsed)
                    + " step=" + seq.getCurrentStepName()
                    + " state=" + seq.getState());
        }
    }

    // ------------------------------------------------------------------
    // Main
    // ------------------------------------------------------------------

    public static void main(String[] args) {
        AnimationVisualizationDemo demo = new AnimationVisualizationDemo();
        demo.demoRotateCube();
        demo.demoCubeUnfold();
        demo.demoCylinderUnfold();
        demo.demoCubeExplode();
        demo.demoAnimationSequence();
        demo.demoInteractiveAnimation();
        System.out.println("All demos completed successfully.");
    }
}
