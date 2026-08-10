package com.geometry.tools;

import com.geometry.renderer.RenderMode;
import com.geometry.renderer.Renderer;
import com.geometry.scene.Scene;
import com.geometry.scene.SelectionManager;

/**
 * Phase 06 - Context object passed to every Tool.
 *
 * Provides tools with access to the Scene, SelectionManager, Camera and
 * Renderer without creating tight coupling to the application layer.
 *
 * Tools must NOT depend on UI components (Swing panels, menus, etc.).
 * All UI interaction goes through Actions.
 */
public class ToolContext {

    private final Scene scene;
    private final SelectionManager selectionManager;
    private final Renderer renderer;
    private RenderMode renderMode;

    /**
     * Create a ToolContext.
     *
     * @param scene               the active Scene
     * @param selectionManager    the active SelectionManager
     * @param renderer            the active Renderer (may be null in headless tests)
     * @param renderMode          current render mode (2D or 3D)
     */
    public ToolContext(Scene scene, SelectionManager selectionManager,
                       Renderer renderer, RenderMode renderMode) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        if (selectionManager == null) {
            throw new IllegalArgumentException("SelectionManager cannot be null");
        }
        this.scene = scene;
        this.selectionManager = selectionManager;
        this.renderer = renderer;
        this.renderMode = renderMode != null ? renderMode : RenderMode.MODE_2D;
    }

    // ------------------------------------------------------------------
    // Scene access
    // ------------------------------------------------------------------

    public Scene getScene() {
        return scene;
    }

    // ------------------------------------------------------------------
    // Selection access
    // ------------------------------------------------------------------

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Get the currently selected SceneObject, or null if none.
     */
    public com.geometry.scene.SceneObject getSelectedObject() {
        return selectionManager.getSelected();
    }

    // ------------------------------------------------------------------
    // Renderer access
    // ------------------------------------------------------------------

    public Renderer getRenderer() {
        return renderer;
    }

    // ------------------------------------------------------------------
    // Render mode
    // ------------------------------------------------------------------

    public RenderMode getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(RenderMode renderMode) {
        if (renderMode == null) {
            throw new IllegalArgumentException("RenderMode cannot be null");
        }
        this.renderMode = renderMode;
    }

    /**
     * Check if currently in 2D mode.
     */
    public boolean is2DMode() {
        return renderMode == RenderMode.MODE_2D;
    }

    /**
     * Check if currently in 3D mode.
     */
    public boolean is3DMode() {
        return renderMode == RenderMode.MODE_3D;
    }
}
