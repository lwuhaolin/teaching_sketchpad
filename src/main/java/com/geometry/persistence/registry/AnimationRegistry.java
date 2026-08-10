package com.geometry.persistence.registry;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationSequence;
import com.geometry.animation.face.FaceAnimator;
import com.geometry.animation.geometry.ExplodeAnimation;
import com.geometry.animation.geometry.UnfoldAnimation;
import com.geometry.animation.interpolation.EaseInterpolator;
import com.geometry.animation.interpolation.Interpolator;
import com.geometry.animation.interpolation.LinearInterpolator;
import com.geometry.animation.transform.MoveAnimation;
import com.geometry.animation.transform.RotateAnimation;
import com.geometry.animation.transform.ScaleAnimation;
import com.geometry.core.math.Vec3;
import com.geometry.persistence.model.AnimationItemData;
import com.geometry.persistence.model.AnimationSequenceData;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

/**
 * Phase 10 - Registry for creating Animation objects from persisted data.
 *
 * Reconstructs AnimationSequence and individual Animation objects
 * from AnimationSequenceData and AnimationItemData.
 *
 * Design:
 *   - Uses type-based dispatch (no if/else chains for animation types)
 *   - Looks up target SceneObjects by ID
 *   - Supports all animation types from Phase 09
 *
 * Not thread-safe.
 */
public class AnimationRegistry {

    /**
     * Create an AnimationSequence from AnimationSequenceData.
     *
     * @param scene    the scene containing the geometry objects
     * @param sequenceData the serialized sequence data
     * @return the reconstructed AnimationSequence
     * @throws IllegalArgumentException if the sequence data is invalid
     */
    public AnimationSequence createSequence(Scene scene, AnimationSequenceData sequenceData) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        if (sequenceData == null) {
            throw new IllegalArgumentException("AnimationSequenceData cannot be null");
        }

        AnimationSequence sequence = new AnimationSequence(sequenceData.getName());

        for (AnimationItemData item : sequenceData.getItems()) {
            Animation anim = createAnimation(scene, item);
            if (anim != null) {
                sequence.addStep(item.getName(), anim, item.getDelaySeconds());
            }
        }

        return sequence;
    }

    /**
     * Create an Animation from AnimationItemData.
     *
     * @param scene the scene containing the geometry objects
     * @param item  the serialized animation item
     * @return the created Animation, or null if the target is not found
     */
    public Animation createAnimation(Scene scene, AnimationItemData item) {
        if (item == null) {
            return null;
        }

        SceneObject target = scene.findObjectById(item.getTargetId());
        if (target == null) {
            // Target object not found — skip this animation item
            return null;
        }

        Interpolator interpolator = createInterpolator(item.getInterpolator());
        float duration = item.getDuration();

        switch (item.getType()) {
            case MOVE:
                return createMoveAnimation(target, item, interpolator, duration);
            case ROTATE:
                return createRotateAnimation(target, item, interpolator, duration);
            case SCALE:
                return createScaleAnimation(target, item, interpolator, duration);
            case UNFOLD:
                return createUnfoldAnimation(target, item, interpolator, duration);
            case EXPLODE:
                return createExplodeAnimation(target, item, interpolator, duration);
            case CUT:
            case SECTION:
                // Cut/Section animations require MeshCutter — not available in serialization
                return null;
            default:
                return null;
        }
    }

    // ------------------------------------------------------------------
    // Animation creation
    // ------------------------------------------------------------------

    private Animation createMoveAnimation(SceneObject target, AnimationItemData item,
                                          Interpolator interpolator, float duration) {
        Vec3 fromPos = item.getFromPosition() != null
                ? new Vec3(item.getFromPosition()[0], item.getFromPosition()[1], item.getFromPosition()[2])
                : target.getEffectiveTransform().getPosition();
        Vec3 toPos = item.getToPosition() != null
                ? new Vec3(item.getToPosition()[0], item.getToPosition()[1], item.getToPosition()[2])
                : new Vec3(0f, 0f, 0f);
        return new MoveAnimation(target.getGeometry(), fromPos, toPos, duration, interpolator);
    }

    private Animation createRotateAnimation(SceneObject target, AnimationItemData item,
                                            Interpolator interpolator, float duration) {
        Vec3 fromRot = item.getFromRotation() != null
                ? new Vec3(item.getFromRotation()[0], item.getFromRotation()[1], item.getFromRotation()[2])
                : target.getEffectiveTransform().getRotation();
        Vec3 toRot = item.getToRotation() != null
                ? new Vec3(item.getToRotation()[0], item.getToRotation()[1], item.getToRotation()[2])
                : new Vec3(0f, 0f, 0f);
        return new RotateAnimation(target.getGeometry(), fromRot, fromRot, toRot, duration, interpolator);
    }

    private Animation createScaleAnimation(SceneObject target, AnimationItemData item,
                                           Interpolator interpolator, float duration) {
        Vec3 fromScale = item.getFromScale() != null
                ? new Vec3(item.getFromScale()[0], item.getFromScale()[1], item.getFromScale()[2])
                : target.getEffectiveTransform().getScale();
        Vec3 toScale = item.getToScale() != null
                ? new Vec3(item.getToScale()[0], item.getToScale()[1], item.getToScale()[2])
                : Vec3.ONE;
        return new ScaleAnimation(target.getGeometry(), fromScale, fromScale, toScale, duration, interpolator);
    }

    private Animation createUnfoldAnimation(SceneObject target, AnimationItemData item,
                                            Interpolator interpolator, float duration) {
        String unfoldTypeStr = item.getUnfoldType();
        UnfoldAnimation.UnfoldType unfoldType;
        if (unfoldTypeStr != null) {
            try {
                unfoldType = UnfoldAnimation.UnfoldType.valueOf(unfoldTypeStr);
            } catch (IllegalArgumentException e) {
                unfoldType = UnfoldAnimation.UnfoldType.CUBE;
            }
        } else {
            unfoldType = UnfoldAnimation.UnfoldType.CUBE;
        }
        return new UnfoldAnimation(target.getGeometry().getMesh(), duration, unfoldType, interpolator);
    }

    private Animation createExplodeAnimation(SceneObject target, AnimationItemData item,
                                             Interpolator interpolator, float duration) {
        return new ExplodeAnimation(target.getGeometry().getMesh(), duration, interpolator);
    }

    // ------------------------------------------------------------------
    // Interpolator
    // ------------------------------------------------------------------

    private Interpolator createInterpolator(String type) {
        if ("EASE".equalsIgnoreCase(type)) {
            return EaseInterpolator.getInstance();
        }
        return LinearInterpolator.getInstance();
    }
}
