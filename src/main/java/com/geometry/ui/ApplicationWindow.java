package com.geometry.ui;

import com.geometry.animation.AnimationManager;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.teaching.TeachingManager;
import com.geometry.tools.ToolManager;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.input.InputModeManager;
import com.geometry.ui.workspace.TeachingWorkspace;

/**
 * Phase 13 - Real Swing application window.
 *
 * Creates and manages the top-level Swing JFrame that contains
 * the complete TeachingWorkspace.  This replaces the headless
 * ApplicationWindow from Phase 11.
 *
 * Usage:
 *   ApplicationWindow window = new ApplicationWindow(scene, toolManager, ...);
 *   window.create();
 *   window.show();
 *
 * Not thread-safe.
 */
public class ApplicationWindow {

    /** The teaching workspace (Swing JFrame). */
    private TeachingWorkspace teachingWorkspace;

    /** Whether the window has been created. */
    private boolean created;

    /** The core scene. */
    private final Scene scene;

    /** The tool manager. */
    private final ToolManager toolManager;

    /** The interaction manager. */
    private final InteractionManager interactionManager;

    /** The teaching manager. */
    private final TeachingManager teachingManager;

    /** The animation manager. */
    private final AnimationManager animationManager;

    /**
     * Create an ApplicationWindow.
     *
     * @param scene              the scene
     * @param toolManager        the tool manager
     * @param interactionManager the interaction manager
     * @param teachingManager    the teaching manager (may be null)
     * @param animationManager   the animation manager (may be null)
     */
    public ApplicationWindow(
            Scene scene,
            ToolManager toolManager,
            InteractionManager interactionManager,
            TeachingManager teachingManager,
            AnimationManager animationManager) {
        this.scene = scene;
        this.toolManager = toolManager;
        this.interactionManager = interactionManager;
        this.teachingManager = teachingManager;
        this.animationManager = animationManager;
        this.created = false;
        this.teachingWorkspace = null;
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Create the Swing window.
     *
     * @return true if creation succeeded
     */
    public boolean create() {
        if (created) {
            return true;
        }
        teachingWorkspace = new TeachingWorkspace(
                scene, toolManager, interactionManager,
                teachingManager, animationManager);
        created = true;
        return true;
    }

    /**
     * Show the window on screen.
     */
    public void show() {
        if (!created) {
            create();
        }
        if (teachingWorkspace != null) {
            teachingWorkspace.showWorkspace();
        }
    }

    /**
     * Close the window.
     */
    public void close() {
        if (teachingWorkspace != null) {
            teachingWorkspace.dispose();
        }
        created = false;
        teachingWorkspace = null;
    }

    /**
     * Force-recreate the window (for testing).
     */
    public void forceRecreate() {
        created = false;
        teachingWorkspace = null;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /**
     * Get the teaching workspace.
     */
    public TeachingWorkspace getTeachingWorkspace() {
        return teachingWorkspace;
    }

    /**
     * Get the workspace model.
     */
    public Workspace getWorkspace() {
        if (teachingWorkspace != null) {
            return teachingWorkspace.getWorkspace();
        }
        // Lazy init for tests that check workspace without calling create()
        if (!created) {
            create();
        }
        return teachingWorkspace != null ? teachingWorkspace.getWorkspace() : null;
    }

    /**
     * Get the scene.
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Get the tool manager.
     */
    public ToolManager getToolManager() {
        return toolManager;
    }

    /**
     * Get the interaction manager.
     */
    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    /**
     * Get the teaching manager.
     */
    public TeachingManager getTeachingManager() {
        return teachingManager;
    }

    /**
     * Get the animation manager.
     */
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    /**
     * Check if the window has been created.
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Get the input mode manager.
     */
    public InputModeManager getInputModeManager() {
        return teachingWorkspace != null ? teachingWorkspace.getInputModeManager() : null;
    }

    /**
     * Get the geometry canvas view.
     */
    public GeometryCanvasView getCanvasView() {
        return teachingWorkspace != null ? teachingWorkspace.getCanvasView() : null;
    }
}
