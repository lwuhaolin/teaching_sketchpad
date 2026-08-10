package com.geometry.ui;

import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.canvas.CanvasInteractionLayer;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.tools.ToolManager;
import com.geometry.teaching.TeachingManager;
import com.geometry.animation.AnimationManager;
import com.geometry.scene.Scene;
import com.geometry.interaction.InteractionManager;

/**
 * Phase 11 - Main application window.
 *
 * Creates and manages the top-level window containing the Workspace.
 * This class is responsible for:
 *   - Creating the Workspace with all dependencies
 *   - Providing access to core engine references
 *   - Managing the application lifecycle
 *
 * It does NOT:
 *   - Handle OpenGL rendering (that's the Renderer's job)
 *   - Process input events directly (handled by InteractionManager)
 *   - Manage scene state (handled by Scene)
 *
 * Usage:
 *   ApplicationWindow window = new ApplicationWindow(
 *       scene, toolManager, interactionManager,
 *       teachingManager, animationManager
 *   );
 *   window.create();
 *   window.show();
 *
 * Not thread-safe.
 */
public class ApplicationWindow {

    /** The workspace managed by this window. */
    private final Workspace workspace;

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

    /** Whether the window has been created. */
    private boolean created;

    /**
     * Create an ApplicationWindow with all engine dependencies.
     *
     * @param scene              the scene (may be null in tests)
     * @param toolManager        the tool manager (may be null in tests)
     * @param interactionManager the interaction manager (may be null in tests)
     * @param teachingManager    the teaching manager (may be null in tests)
     * @param animationManager   the animation manager (may be null in tests)
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
        this.workspace = createWorkspace();
    }

    // ------------------------------------------------------------------
    // Workspace creation
    // ------------------------------------------------------------------

    private Workspace createWorkspace() {
        // Create event bridge
        UIEventBridge bridge = new UIEventBridge(toolManager, scene, interactionManager);

        // Create layout manager (desktop mode, default size)
        LayoutManager layoutManager = new LayoutManager(
                UIInteractionMode.DESKTOP,
                1024,
                768
        );

        // Create panels
        SceneTreePanel sceneTreePanel = new SceneTreePanel(scene, bridge);
        PropertyPanel propertyPanel = new PropertyPanel();
        TeachingPanel teachingPanel = new TeachingPanel(teachingManager, bridge);
        AnimationPanel animationPanel = new AnimationPanel(animationManager, bridge);

        // Create workspace
        Workspace workspace = new Workspace(
                layoutManager, bridge,
                sceneTreePanel, propertyPanel,
                teachingPanel, animationPanel
        );

        // Set up canvas interaction layer if we have the dependencies
        if (scene != null && interactionManager != null) {
            CanvasInteractionLayer canvasLayer = new CanvasInteractionLayer(
                    scene, interactionManager);
            workspace.setCanvasInteractionLayer(canvasLayer);
        }

        return workspace;
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Create the window. In a real application this would create the
     * actual OS window. In headless mode, this is a no-op.
     *
     * @return true if creation succeeded
     */
    public boolean create() {
        if (created) {
            return true;
        }
        created = true;
        return true;
    }

    /**
     * Show the window.
     */
    public void show() {
        // In a real application this would make the window visible.
        // For headless/testing, this is a no-op.
    }

    /**
     * Check if the window is created.
     */
    public boolean isCreated() {
        return created;
    }

    /**
     * Close the window.
     */
    public void close() {
        created = false;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /**
     * Get the workspace.
     */
    public Workspace getWorkspace() {
        return workspace;
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
}
