package com.geometry.ui.interaction;

import com.geometry.animation.Animation;
import com.geometry.animation.AnimationManager;
import com.geometry.animation.interaction.AnimationController;
import com.geometry.scene.Scene;
import com.geometry.teaching.TeachingManager;
import com.geometry.ui.bridge.UICommandHandler;

/** Bridges teaching and animation commands from the UI to existing services. */
public final class TeachingInteractionController implements UICommandHandler {

    private final TeachingManager teachingManager;
    private final AnimationManager animationManager;
    private final AnimationController animationController;
    private String lastFeedback;

    public TeachingInteractionController(Scene scene, TeachingManager teachingManager,
                                         AnimationManager animationManager) {
        this.teachingManager = teachingManager;
        this.animationManager = animationManager;
        this.animationController = scene != null && animationManager != null
                ? new AnimationController(scene, animationManager) : null;
        this.lastFeedback = "请选择一个模型，再选择操作工具";
    }

    @Override
    public void handleTeachingControl(String action) {
        if (teachingManager == null) {
            lastFeedback = "当前课程尚未加载";
            return;
        }
        if ("next".equals(action)) {
            lastFeedback = teachingManager.nextStep() ? "已进入下一教学步骤" : "已经是最后一步";
        } else if ("prev".equals(action)) {
            lastFeedback = teachingManager.previousStep() ? "已返回上一步" : "已经是第一步";
        } else if ("start".equals(action)) {
            lastFeedback = teachingManager.isLessonActive() ? "课程演示已开始" : "当前没有可演示的课程";
        } else if ("stop".equals(action)) {
            lastFeedback = "课程演示已暂停";
        }
    }

    @Override
    public void handleAnimationControl(String action) {
        if (animationManager == null) {
            lastFeedback = "动画服务不可用";
            return;
        }
        if ("play".equals(action)) {
            animationManager.play();
            lastFeedback = "正在播放动画";
        } else if ("pause".equals(action)) {
            animationManager.pause();
            lastFeedback = "动画已暂停";
        } else if ("stop".equals(action)) {
            animationManager.stop();
            lastFeedback = "动画已回到开始位置";
        } else if ("unfold".equals(action)) {
            createUnfoldAnimation();
        }
    }

    private void createUnfoldAnimation() {
        if (animationController == null) {
            lastFeedback = "动画服务不可用";
            return;
        }
        // The controller owns type validation and creates a new animation without changing the mesh.
        try {
            // Scene selection is checked by the controller. The type check below is deliberately
            // performed by attempting the appropriate supported animation.
            Animation animation;
            // Both calls validate the currently selected object and leave geometry untouched.
            try {
                animation = animationController.createCubeUnfoldAnimation(1.8f);
            } catch (IllegalStateException cubeMismatch) {
                animation = animationController.createCylinderUnfoldAnimation(1.8f);
            }
            animationManager.clearAnimations();
            animationManager.addAnimation(animation);
            animationManager.play();
            lastFeedback = "正在展示几何体的展开过程";
        } catch (IllegalStateException ex) {
            lastFeedback = "请选择立方体或圆柱体后再展开";
        }
    }

    public String getLastFeedback() {
        return lastFeedback;
    }
}
