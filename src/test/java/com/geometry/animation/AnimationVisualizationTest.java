package com.geometry.animation;

import com.geometry.animation.face.FaceAnimationState;
import com.geometry.animation.face.FaceAnimator;
import com.geometry.animation.geometry.ExplodeAnimation;
import com.geometry.animation.geometry.GeometryAnimation;
import com.geometry.animation.geometry.UnfoldAnimation;
import com.geometry.animation.interaction.AnimationController;
import com.geometry.animation.interaction.InteractiveAnimation;
import com.geometry.animation.interpolation.EaseInterpolator;
import com.geometry.animation.interpolation.LinearInterpolator;
import com.geometry.animation.transform.MoveAnimation;
import com.geometry.animation.transform.RotateAnimation;
import com.geometry.animation.transform.ScaleAnimation;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.MeshFactory;
import com.geometry.core.transform.Transform;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 09 - Tests for the Animation & Visualization System.
 *
 * Tests cover:
 *   - Animation lifecycle (start, pause, finish, stop)
 *   - Face-level animation (FaceAnimator, FaceAnimationState)
 *   - Transform animations (Move, Rotate, Scale)
 *   - Geometry animations (Unfold, Explode)
 *   - AnimationSequence
 *   - InteractiveAnimation
 *   - AnimationManager event handling
 */
public class AnimationVisualizationTest {

    private Scene scene;
    private AnimationManager animManager;
    private AnimationController controller;

    @Before
    public void setUp() {
        scene = new Scene();
        animManager = new AnimationManager();
        controller = new AnimationController(scene, animManager);
    }

    // ------------------------------------------------------------------
    // Animation Lifecycle Tests
    // ------------------------------------------------------------------

    @Test
    public void testAnimationLifecycle() {
        Cube cube = new Cube(2f, 2f, 2f);
        scene.addObject("cube", cube);

        RotateAnimation anim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        // Initial state
        assertEquals(AnimationState.READY, anim.getState());
        assertFalse(anim.isFinished());
        assertEquals(0f, anim.getProgress(), 0.001f);

        // Start
        anim.start();
        assertEquals(AnimationState.RUNNING, anim.getState());

        // Update to completion
        anim.update(1.0f);
        assertEquals(AnimationState.FINISHED, anim.getState());
        assertTrue(anim.isFinished());
        assertEquals(1f, anim.getProgress(), 0.001f);

        // Stop resets
        anim.stop();
        assertEquals(AnimationState.STOPPED, anim.getState());
        assertEquals(0f, anim.getProgress(), 0.001f);
        assertEquals(new Vec3(0f, 0f, 0f), cube.getTransform().getRotation());

        // Restart
        anim.start();
        assertEquals(AnimationState.RUNNING, anim.getState());
    }

    @Test
    public void testPauseAndResume() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation anim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 180f, 0f),
                2f,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(0.5f);
        float progressAtPause = anim.getProgress();

        anim.pause();
        assertEquals(AnimationState.PAUSED, anim.getState());

        // Update while paused should not change progress
        anim.update(1f);
        assertEquals(progressAtPause, anim.getProgress(), 0.001f);
        assertEquals(AnimationState.PAUSED, anim.getState());

        // Resume
        anim.start();
        assertEquals(AnimationState.RUNNING, anim.getState());
    }

    // ------------------------------------------------------------------
    // Transform Animation Tests
    // ------------------------------------------------------------------

    @Test
    public void testMoveAnimation() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject obj = scene.addObject("cube", cube);

        MoveAnimation anim = new MoveAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(5f, 0f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(0.5f);

        // At 50% progress, position should be halfway
        Vec3 expectedPos = new Vec3(2.5f, 0f, 0f);
        assertEquals(expectedPos, cube.getTransform().getPosition());
        assertEquals(0.5f, anim.getProgress(), 0.001f);
    }

    @Test
    public void testRotateAnimation() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation anim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(0.5f);

        // At 50% progress, rotation should be 45 degrees
        assertEquals(45f, cube.getTransform().getRotation().y, 0.01f);
    }

    @Test
    public void testScaleAnimation() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        ScaleAnimation anim = new ScaleAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                Vec3.ONE,
                new Vec3(2f, 2f, 2f),
                1f,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(0.5f);

        // At 50% progress, scale should be 1.5
        Vec3 expectedScale = new Vec3(1.5f, 1.5f, 1.5f);
        assertEquals(expectedScale, cube.getTransform().getScale());
    }

    // ------------------------------------------------------------------
    // Face Animation Tests
    // ------------------------------------------------------------------

    @Test
    public void testFaceAnimationState() {
        Vec3[] startVerts = {
                new Vec3(0f, 0f, 0f),
                new Vec3(1f, 0f, 0f),
                new Vec3(0f, 1f, 0f)
        };
        Vec3[] endVerts = {
                new Vec3(0f, 2f, 0f),
                new Vec3(1f, 2f, 0f),
                new Vec3(0f, 3f, 0f)
        };

        FaceAnimationState state = new FaceAnimationState(0, startVerts, endVerts);

        assertEquals(0, state.getFaceIndex());
        assertEquals(startVerts[0], state.getStartVertices()[0]);
        assertEquals(endVerts[0], state.getEndVertices()[0]);

        // Interpolate at 0.5
        Vec3[] interp = state.interpolateVertices(0.5f);
        assertEquals(new Vec3(0f, 1f, 0f), interp[0]);
        assertEquals(new Vec3(1f, 1f, 0f), interp[1]);
        assertEquals(new Vec3(0f, 2f, 0f), interp[2]);
    }

    @Test
    public void testFaceAnimator() {
        Mesh cubeMesh = MeshFactory.createCube(2f, 2f, 2f);
        FaceAnimator animator = new FaceAnimator(cubeMesh);

        // At progress 0, should return copy of original mesh
        Mesh meshAtZero = animator.getAnimatedMesh(0f);
        assertEquals(cubeMesh.getFaceCount(), meshAtZero.getFaceCount());

        // Add a face state
        Vec3[] startVerts = {
                new Vec3(-1f, -1f, -1f),
                new Vec3(1f, -1f, -1f),
                new Vec3(1f, 1f, -1f)
        };
        Vec3[] endVerts = {
                new Vec3(-1f, -1f, -3f),
                new Vec3(1f, -1f, -3f),
                new Vec3(1f, 1f, -3f)
        };
        animator.addFaceState(new FaceAnimationState(0, startVerts, endVerts));

        // At progress 0.5, the face should be halfway
        Mesh meshAtHalf = animator.getAnimatedMesh(0.5f);
        assertEquals(cubeMesh.getFaceCount(), meshAtHalf.getFaceCount());

        // At progress 1, should have end positions
        Mesh meshAtOne = animator.getAnimatedMesh(1f);
        assertEquals(cubeMesh.getFaceCount(), meshAtOne.getFaceCount());
    }

    // ------------------------------------------------------------------
    // Geometry Animation Tests
    // ------------------------------------------------------------------

    @Test
    public void testUnfoldAnimationCube() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh originalMesh = cube.getMesh();

        UnfoldAnimation anim = new UnfoldAnimation(
                originalMesh,
                2f,
                UnfoldAnimation.UnfoldType.CUBE,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(1f); // half way

        assertEquals(AnimationState.RUNNING, anim.getState());
        assertEquals(0.5f, anim.getProgress(), 0.001f);

        // Should be able to get animated mesh
        Mesh animatedMesh = anim.getFaceAnimator().getAnimatedMesh(anim.getProgress());
        assertNotNull(animatedMesh);
        assertEquals(originalMesh.getFaceCount(), animatedMesh.getFaceCount());
    }

    @Test
    public void testUnfoldAnimationCylinder() {
        Cylinder cylinder = new Cylinder(1.5f, 3f, 16);
        Mesh originalMesh = cylinder.getMesh();

        UnfoldAnimation anim = new UnfoldAnimation(
                originalMesh,
                3f,
                UnfoldAnimation.UnfoldType.CYLINDER,
                LinearInterpolator.getInstance()
        );

        anim.start();
        anim.update(1.5f);

        assertEquals(AnimationState.RUNNING, anim.getState());
        Mesh animatedMesh = anim.getFaceAnimator().getAnimatedMesh(anim.getProgress());
        assertNotNull(animatedMesh);
    }

    @Test
    public void testExplodeAnimation() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh originalMesh = cube.getMesh();

        ExplodeAnimation anim = new ExplodeAnimation(
                originalMesh,
                2f,
                EaseInterpolator.getInstance()
        );

        anim.start();
        anim.update(1f);

        assertEquals(AnimationState.RUNNING, anim.getState());
        Mesh animatedMesh = anim.getFaceAnimator().getAnimatedMesh(anim.getProgress());
        assertNotNull(animatedMesh);
        // Exploded mesh should have same number of faces
        assertEquals(originalMesh.getFaceCount(), animatedMesh.getFaceCount());
    }

    // ------------------------------------------------------------------
    // AnimationSequence Tests
    // ------------------------------------------------------------------

    @Test
    public void testAnimationSequence() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        AnimationSequence seq = new AnimationSequence("Test Sequence");

        RotateAnimation rotAnim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 45f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        ScaleAnimation scaleAnim = new ScaleAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                Vec3.ONE,
                new Vec3(2f, 2f, 2f),
                1f,
                LinearInterpolator.getInstance()
        );

        seq.addStep("Rotate", rotAnim);
        seq.addStep("Scale", scaleAnim);

        assertEquals(2, seq.getStepCount());
        assertEquals("Rotate", seq.getCurrentStepName());

        seq.start();
        assertEquals(AnimationState.RUNNING, seq.getState());

        // Advance past first step
        seq.update(1.5f);
        assertEquals("Scale", seq.getCurrentStepName());

        // Advance past second step
        seq.update(1f);
        assertTrue(seq.isFinished());
        assertEquals(AnimationState.FINISHED, seq.getState());
    }

    // ------------------------------------------------------------------
    // InteractiveAnimation Tests
    // ------------------------------------------------------------------

    @Test
    public void testInteractiveAnimation() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation baseAnim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 180f, 0f),
                2f,
                LinearInterpolator.getInstance()
        );

        InteractiveAnimation interactive = controller.makeInteractive(baseAnim);
        animManager.addAnimation(interactive);

        // Start playing
        interactive.start();
        interactive.update(0.5f);
        assertEquals(0.25f, interactive.getProgress(), 0.001f);

        // Scrub to 0.75
        interactive.setProgress(0.75f);
        assertTrue(interactive.isInteractiveMode());
        assertEquals(0.75f, interactive.getProgress(), 0.001f);

        // Resume
        interactive.resume();
        assertFalse(interactive.isInteractiveMode());
        assertEquals(AnimationState.RUNNING, interactive.getState());
    }

    // ------------------------------------------------------------------
    // AnimationManager Tests
    // ------------------------------------------------------------------

    @Test
    public void testAnimationManager() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation anim1 = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        MoveAnimation anim2 = new MoveAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(1f, 0f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        animManager.addAnimation(anim1);
        animManager.addAnimation(anim2);

        assertEquals(2, animManager.getAnimationCount());

        // Play both
        animManager.play();
        assertTrue(animManager.isAnyRunning());

        // Update both
        animManager.update(0.5f);
        assertEquals(0.5f, anim1.getProgress(), 0.001f);
        assertEquals(0.5f, anim2.getProgress(), 0.001f);

        // Pause
        animManager.pause();
        assertEquals(AnimationState.PAUSED, anim1.getState());

        // Resume
        animManager.resume();
        assertEquals(AnimationState.RUNNING, anim1.getState());

        // Stop
        animManager.stop();
        assertEquals(AnimationState.STOPPED, anim1.getState());
        assertEquals(AnimationState.STOPPED, anim2.getState());
    }

    @Test
    public void testAnimationManagerListener() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation anim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                0.1f,
                LinearInterpolator.getInstance()
        );

        animManager.addAnimation(anim);

        boolean[] startCalled = {false};
        boolean[] completeCalled = {false};

        animManager.addListener(new AnimationListener() {
            @Override
            public void onAnimationStart(AnimationEvent event) {
                startCalled[0] = true;
            }

            @Override
            public void onAnimationComplete(AnimationEvent event) {
                completeCalled[0] = true;
            }
        });

        animManager.play();
        animManager.update(0.2f);

        assertTrue("onAnimationStart should be called", startCalled[0]);
        assertTrue("onAnimationComplete should be called", completeCalled[0]);
    }

    // ------------------------------------------------------------------
    // Progress Setting Tests
    // ------------------------------------------------------------------

    @Test
    public void testProgressSetting() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        RotateAnimation anim = new RotateAnimation(
                cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 180f, 0f),
                1f,
                LinearInterpolator.getInstance()
        );

        // Set progress without starting
        anim.setProgress(0.5f);
        assertEquals(0.5f, anim.getProgress(), 0.001f);
        assertEquals(90f, cube.getTransform().getRotation().y, 0.01f);

        // Set progress to 0
        anim.setProgress(0f);
        assertEquals(0f, anim.getProgress(), 0.001f);
        assertEquals(0f, cube.getTransform().getRotation().y, 0.01f);

        // Set progress to 1
        anim.setProgress(1f);
        assertEquals(1f, anim.getProgress(), 0.001f);
        assertEquals(180f, cube.getTransform().getRotation().y, 0.01f);
    }

    // ------------------------------------------------------------------
    // Interpolator Tests
    // ------------------------------------------------------------------

    @Test
    public void testLinearInterpolator() {
        LinearInterpolator interp = LinearInterpolator.getInstance();
        assertEquals(0f, interp.interpolate(0f), 0.001f);
        assertEquals(0.5f, interp.interpolate(0.5f), 0.001f);
        assertEquals(1f, interp.interpolate(1f), 0.001f);
    }

    @Test
    public void testEaseInterpolator() {
        EaseInterpolator interp = EaseInterpolator.getInstance();
        // Ease in-out: starts slow, ends slow
        float t0 = interp.interpolate(0f);
        float t1 = interp.interpolate(1f);
        float tHalf = interp.interpolate(0.5f);

        assertEquals(0f, t0, 0.001f);
        assertEquals(1f, t1, 0.001f);
        assertEquals(0.5f, tHalf, 0.001f);

        // Ease should be < linear at beginning and end
        float tQuarter = interp.interpolate(0.25f);
        float tThreeQuarter = interp.interpolate(0.75f);
        assertTrue("Ease should be slower at start", tQuarter < 0.25f + 0.01f);
        assertTrue("Ease should be slower at end", tThreeQuarter > 0.75f - 0.01f);
    }

    // ------------------------------------------------------------------
    // Edge Cases
    // ------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testNullTargetAnimation() {
        new RotateAnimation(null,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                1f,
                LinearInterpolator.getInstance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeDurationAnimation() {
        Cube cube = new Cube(1f, 1f, 1f);
        new RotateAnimation(cube,
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 90f, 0f),
                -1f,
                LinearInterpolator.getInstance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMeshGeometryAnimation() {
        new ExplodeAnimation(null, 1f, LinearInterpolator.getInstance());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFaceAnimationState() {
        new FaceAnimationState(0, null, new Vec3[]{Vec3.ZERO, Vec3.UNIT_X, Vec3.UNIT_Y});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFaceAnimationStateVertexCount() {
        new FaceAnimationState(0,
                new Vec3[]{Vec3.ZERO, Vec3.UNIT_X},
                new Vec3[]{Vec3.ZERO, Vec3.UNIT_X});
    }

    @Test
    public void testTimeline() {
        Timeline timeline = new Timeline(0f);
        timeline.update(0.5f);
        assertEquals(0.5f, timeline.getCurrentTime(), 0.001f);

        timeline.pause();
        timeline.update(1f);
        assertEquals(0.5f, timeline.getCurrentTime(), 0.001f); // paused

        timeline.resume();
        timeline.update(0.5f);
        assertEquals(1.0f, timeline.getCurrentTime(), 0.001f);

        timeline.setTimeScale(2f);
        timeline.update(0.5f);
        assertEquals(2.0f, timeline.getCurrentTime(), 0.001f);

        timeline.reset();
        assertEquals(0f, timeline.getCurrentTime(), 0.001f);
    }

    @Test
    public void testSequenceStepNavigation() {
        Cube cube = new Cube(1f, 1f, 1f);
        scene.addObject("cube", cube);

        AnimationSequence seq = new AnimationSequence("Nav Test");
        seq.addStep("Step1",
                new RotateAnimation(cube,
                        new Vec3(0f, 0f, 0f),
                        new Vec3(0f, 0f, 0f),
                        new Vec3(0f, 45f, 0f),
                        0.1f,
                        LinearInterpolator.getInstance()));
        seq.addStep("Step2",
                new MoveAnimation(cube,
                        new Vec3(0f, 0f, 0f),
                        new Vec3(1f, 0f, 0f),
                        0.1f,
                        LinearInterpolator.getInstance()));

        seq.start();
        assertEquals(0, seq.getCurrentStepIndex());
        assertEquals("Step1", seq.getCurrentStepName());

        seq.nextStep();
        assertEquals(1, seq.getCurrentStepIndex());
        assertEquals("Step2", seq.getCurrentStepName());

        seq.previousStep();
        assertEquals(0, seq.getCurrentStepIndex());
        assertEquals("Step1", seq.getCurrentStepName());

        seq.previousStep(); // at start, should not go further
        assertEquals(0, seq.getCurrentStepIndex());
    }
}
