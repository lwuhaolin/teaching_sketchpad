package com.geometry.animation.interaction;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationListener;
import com.geometry.animation.AnimationManager;
import com.geometry.animation.AnimationSequence;
import com.geometry.animation.geometry.ExplodeAnimation;
import com.geometry.animation.geometry.UnfoldAnimation;
import com.geometry.animation.interpolation.EaseInterpolator;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.animation.interpolation.LinearInterpolator;
import com.geometry.animation.transform.MoveAnimation;
import com.geometry.animation.transform.RotateAnimation;
import com.geometry.animation.transform.ScaleAnimation;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

/**
 * Phase 09 - Controller for animation interaction.
 *
 * Provides a high-level API for:
 *   - Creating common animations (rotate, move, unfold, explode, cut)
 *   - Managing animation sequences for teaching lessons
 *   - Handling touch/keyboard input for animation control
 *
 * Integration:
 *   Connected to Scene for object access.
 *   Connected to AnimationManager for lifecycle management.
 *   Connected to Teaching system for lesson-driven animation.
 */
public class AnimationController {

    private final Scene scene;
    private final AnimationManager animationManager;
    private final Interpolator defaultInterpolator;

    /**
     * Create an AnimationController.
     *
     * @param scene the scene containing geometry objects
     */
    public AnimationController(Scene scene) {
        this(scene, new AnimationManager());
    }

    /**
     * Create an AnimationController with a custom AnimationManager.
     *
     * @param scene             the scene containing geometry objects
     * @param animationManager  the animation manager to use
     */
    public AnimationController(Scene scene, AnimationManager animationManager) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        this.scene = scene;
        this.animationManager = animationManager;
        this.defaultInterpolator = EaseInterpolator.getInstance();
    }

    // ------------------------------------------------------------------
    // Animation creation helpers
    // ------------------------------------------------------------------

    /**
     * Create a rotate animation for the given object.
     *
     * @param objectId   ID of the scene object to rotate
     * @param yawDeg     target Y-axis rotation in degrees
     * @param duration   animation duration in seconds
     * @return the created animation
     */
    public Animation createRotateAnimation(String objectId, float yawDeg, float duration) {
        SceneObject obj = scene.findObjectById(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Object not found: " + objectId);
        }
        com.geometry.core.transform.Transform start = obj.getEffectiveTransform();
        com.geometry.core.transform.Transform end = start.rotate(new Vec3(0f, yawDeg, 0f));
        return new RotateAnimation(
                obj.getGeometry(),
                start.getPosition(),
                start.getRotation(),
                end.getRotation(),
                duration,
                defaultInterpolator);
    }

    /**
     * Create a move animation for the given object.
     *
     * @param objectId  ID of the scene object to move
     * @param endPos    target position
     * @param duration  animation duration in seconds
     * @return the created animation
     */
    public Animation createMoveAnimation(String objectId, Vec3 endPos, float duration) {
        SceneObject obj = scene.findObjectById(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Object not found: " + objectId);
        }
        com.geometry.core.transform.Transform start = obj.getEffectiveTransform();
        return new MoveAnimation(
                obj.getGeometry(),
                start.getPosition(),
                endPos,
                duration,
                defaultInterpolator);
    }

    /**
     * Create a scale animation for the given object.
     *
     * @param objectId  ID of the scene object to scale
     * @param scaleFactor target uniform scale factor
     * @param duration  animation duration in seconds
     * @return the created animation
     */
    public Animation createScaleAnimation(String objectId, float scaleFactor, float duration) {
        SceneObject obj = scene.findObjectById(objectId);
        if (obj == null) {
            throw new IllegalArgumentException("Object not found: " + objectId);
        }
        com.geometry.core.transform.Transform start = obj.getEffectiveTransform();
        return new ScaleAnimation(
                obj.getGeometry(),
                start.getPosition(),
                start.getScale(),
                new Vec3(scaleFactor, scaleFactor, scaleFactor),
                duration,
                defaultInterpolator);
    }

    /**
     * Create an unfold animation for a cube mesh.
     *
     * @param duration animation duration in seconds
     * @return the created animation
     */
    public Animation createCubeUnfoldAnimation(float duration) {
        SceneObject obj = scene.getSelected();
        if (obj == null) {
            throw new IllegalStateException("No object selected for unfold animation");
        }
        if (!(obj.getGeometry() instanceof Cube)) {
            throw new IllegalStateException("Selected object is not a Cube");
        }
        Mesh mesh = obj.getGeometry().getMesh();
        return new UnfoldAnimation(
                mesh, duration,
                UnfoldAnimation.UnfoldType.CUBE,
                defaultInterpolator);
    }

    /**
     * Create an unfold animation for a cylinder mesh.
     *
     * @param duration animation duration in seconds
     * @return the created animation
     */
    public Animation createCylinderUnfoldAnimation(float duration) {
        SceneObject obj = scene.getSelected();
        if (obj == null) {
            throw new IllegalStateException("No object selected for unfold animation");
        }
        if (!(obj.getGeometry() instanceof Cylinder)) {
            throw new IllegalStateException("Selected object is not a Cylinder");
        }
        Mesh mesh = obj.getGeometry().getMesh();
        return new UnfoldAnimation(
                mesh, duration,
                UnfoldAnimation.UnfoldType.CYLINDER,
                defaultInterpolator);
    }

    /**
     * Create an explode animation for the selected object.
     *
     * @param duration animation duration in seconds
     * @return the created animation
     */
    public Animation createExplodeAnimation(float duration) {
        SceneObject obj = scene.getSelected();
        if (obj == null) {
            throw new IllegalStateException("No object selected for explode animation");
        }
        Mesh mesh = obj.getGeometry().getMesh();
        return new ExplodeAnimation(
                mesh, duration, defaultInterpolator);
    }

    /**
     * Create an interactive animation wrapper around an existing animation.
     *
     * @param animation the animation to wrap
     * @return the interactive wrapper
     */
    public InteractiveAnimation makeInteractive(Animation animation) {
        return new InteractiveAnimation(animation, animationManager);
    }

    // ------------------------------------------------------------------
    // Sequence management
    // ------------------------------------------------------------------

    /**
     * Create an animation sequence for a teaching lesson.
     *
     * @param name sequence name
     * @return the created sequence
     */
    public AnimationSequence createSequence(String name) {
        return new AnimationSequence(name);
    }

    /**
     * Add an animation to the manager and return an interactive wrapper.
     *
     * @param animation the animation to add
     * @return the interactive wrapper
     */
    public InteractiveAnimation addAndInteract(Animation animation) {
        animationManager.addAnimation(animation);
        return new InteractiveAnimation(animation, animationManager);
    }

    // ------------------------------------------------------------------
    // Playback control
    // ------------------------------------------------------------------

    /**
     * Play all managed animations.
     */
    public void play() {
        animationManager.play();
    }

    /**
     * Pause all managed animations.
     */
    public void pause() {
        animationManager.pause();
    }

    /**
     * Resume all paused animations.
     */
    public void resume() {
        animationManager.resume();
    }

    /**
     * Stop all managed animations.
     */
    public void stop() {
        animationManager.stop();
    }

    /**
     * Update all animations by delta time.
     *
     * @param deltaTime time in seconds since last frame
     */
    public void update(float deltaTime) {
        animationManager.update(deltaTime);
    }

    // ------------------------------------------------------------------
    // Listener management
    // ------------------------------------------------------------------

    /**
     * Add an animation listener.
     *
     * @param listener the listener to add
     */
    public void addListener(AnimationListener listener) {
        animationManager.addListener(listener);
    }

    /**
     * Remove an animation listener.
     *
     * @param listener the listener to remove
     */
    public void removeListener(AnimationListener listener) {
        animationManager.removeListener(listener);
    }

    /**
     * Get the animation manager.
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }
}
