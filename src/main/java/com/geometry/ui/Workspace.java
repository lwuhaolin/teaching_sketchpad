package com.geometry.ui;

import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.canvas.CanvasInteractionLayer;
import com.geometry.ui.canvas.OverlayRenderer;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.ui.toolbar.QuickToolBar;
import com.geometry.ui.toolbar.ToolBar;

/**
 * Phase 11 - Main workspace container for the UI system.
 *
 * The Workspace is the central container that assembles all UI components:
 *   - Toolbar / QuickToolBar (tool selection)
 *   - OpenGL Canvas area (managed by CanvasInteractionLayer)
 *   - SceneTreePanel (object hierarchy)
 *   - PropertyPanel (selected object properties)
 *   - TeachingPanel (lesson control)
 *   - AnimationPanel (animation playback)
 *
 * It does NOT:
 *   - Render OpenGL (that's the Renderer's job)
 *   - Process raw input (that's the Interaction system's job)
 *   - Store geometry (that's the Scene's job)
 *
 * Architecture:
 *   ApplicationWindow
 *     └── Workspace
 *           ├── ToolBar / QuickToolBar
 *           ├── CanvasInteractionLayer (mouse/touch handling)
 *           ├── OverlayRenderer (visual feedback)
 *           ├── SceneTreePanel
 *           ├── PropertyPanel
 *           ├── TeachingPanel
 *           └── AnimationPanel
 *
 * Not thread-safe.
 */
public class Workspace {

    /** The layout manager for panel positions. */
    private final LayoutManager layoutManager;

    /** The UI event bridge. */
    private final UIEventBridge eventBridge;

    /** The toolbar for tool selection. */
    private final ToolBar toolBar;

    /** The quick toolbar for whiteboard mode. */
    private final QuickToolBar quickToolBar;

    /** The canvas interaction layer. */
    private CanvasInteractionLayer canvasInteraction;

    /** The overlay renderer. */
    private final OverlayRenderer overlayRenderer;

    /** The scene tree panel. */
    private final SceneTreePanel sceneTreePanel;

    /** The property panel. */
    private final PropertyPanel propertyPanel;

    /** The teaching panel. */
    private final TeachingPanel teachingPanel;

    /** The animation panel. */
    private final AnimationPanel animationPanel;

    /**
     * Create a Workspace with all dependencies.
     *
     * @param layoutManager       the layout manager
     * @param eventBridge         the UI event bridge
     * @param sceneTreePanel      the scene tree panel
     * @param propertyPanel       the property panel
     * @param teachingPanel       the teaching panel (may be null)
     * @param animationPanel      the animation panel (may be null)
     */
    public Workspace(
            LayoutManager layoutManager,
            UIEventBridge eventBridge,
            SceneTreePanel sceneTreePanel,
            PropertyPanel propertyPanel,
            TeachingPanel teachingPanel,
            AnimationPanel animationPanel) {
        if (layoutManager == null) {
            throw new IllegalArgumentException("LayoutManager cannot be null");
        }
        if (eventBridge == null) {
            throw new IllegalArgumentException("UIEventBridge cannot be null");
        }
        if (sceneTreePanel == null) {
            throw new IllegalArgumentException("SceneTreePanel cannot be null");
        }
        if (propertyPanel == null) {
            throw new IllegalArgumentException("PropertyPanel cannot be null");
        }
        this.layoutManager = layoutManager;
        this.eventBridge = eventBridge;
        this.toolBar = new ToolBar(eventBridge);
        this.quickToolBar = new QuickToolBar(eventBridge);
        this.canvasInteraction = null; // set separately with scene/interactionManager
        this.overlayRenderer = new OverlayRenderer();
        this.sceneTreePanel = sceneTreePanel;
        this.propertyPanel = propertyPanel;
        this.teachingPanel = teachingPanel;
        this.animationPanel = animationPanel;
    }

    // ------------------------------------------------------------------
    // Canvas interaction
    // ------------------------------------------------------------------

    /**
     * Set the canvas interaction layer (requires Scene and InteractionManager).
     *
     * @param layer the canvas interaction layer
     */
    public void setCanvasInteractionLayer(CanvasInteractionLayer layer) {
        this.canvasInteraction = layer;
    }

    /**
     * Get the canvas interaction layer, or null if not set.
     */
    public CanvasInteractionLayer getCanvasInteractionLayer() {
        return canvasInteraction;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    /**
     * Get the layout manager.
     */
    public LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Get the total workspace width.
     */
    public int getWidth() {
        return layoutManager.getWindowWidth();
    }

    /**
     * Get the total workspace height.
     */
    public int getHeight() {
        return layoutManager.getWindowHeight();
    }

    // ------------------------------------------------------------------
    // Panels
    // ------------------------------------------------------------------

    /**
     * Get the tool bar.
     */
    public ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * Get the quick tool bar.
     */
    public QuickToolBar getQuickToolBar() {
        return quickToolBar;
    }

    /**
     * Get the scene tree panel.
     */
    public SceneTreePanel getSceneTreePanel() {
        return sceneTreePanel;
    }

    /**
     * Get the property panel.
     */
    public PropertyPanel getPropertyPanel() {
        return propertyPanel;
    }

    /**
     * Get the teaching panel.
     */
    public TeachingPanel getTeachingPanel() {
        return teachingPanel;
    }

    /**
     * Get the animation panel.
     */
    public AnimationPanel getAnimationPanel() {
        return animationPanel;
    }

    /**
     * Get the overlay renderer.
     */
    public OverlayRenderer getOverlayRenderer() {
        return overlayRenderer;
    }

    /**
     * Get the UI event bridge.
     */
    public UIEventBridge getEventBridge() {
        return eventBridge;
    }

    // ------------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------------

    /**
     * Process a toolbar button click.
     *
     * @param x x coordinate in workspace
     * @param y y coordinate in workspace
     * @return true if a tool was switched
     */
    public boolean processToolbarClick(int x, int y) {
        // Check quick toolbar first (whiteboard mode)
        if (layoutManager.getMode() == UIInteractionMode.WHITEBOARD) {
            String tool = quickToolBar.getToolAtPosition(x, y);
            if (tool != null) {
                return quickToolBar.switchTool(tool);
            }
        }

        // Check main toolbar
        String tool = toolBar.getToolAtPosition(x, y);
        if (tool != null) {
            return toolBar.switchTool(tool);
        }
        return false;
    }

    /**
     * Process a scene tree row click.
     *
     * @param x x coordinate in workspace
     * @param y y coordinate in workspace
     * @return true if an object was selected
     */
    public boolean processSceneTreeClick(int x, int y) {
        if (layoutManager.getMode() == UIInteractionMode.WHITEBOARD) {
            return false; // Scene tree not shown in whiteboard
        }
        int[] layout = layoutManager.getSceneTreeLayout();
        if (layout == null) {
            return false;
        }
        int localX = x - layout[0];
        int localY = y - layout[1];
        int index = sceneTreePanel.getRowAtY(localY);
        if (index >= 0) {
            sceneTreePanel.selectByIndex(index);
            return true;
        }
        return false;
    }

    /**
     * Process a teaching panel control click.
     *
     * @param x x coordinate in workspace
     * @param y y coordinate in workspace
     * @return true if a control was triggered
     */
    public boolean processTeachingClick(int x, int y) {
        if (teachingPanel == null) {
            return false;
        }
        int[] layout = layoutManager.getTeachingPanelLayout();
        if (layout == null) {
            return false;
        }
        int localX = x - layout[0];
        int localY = y - layout[1];
        int rowHeight = teachingPanel.getRowHeight();
        int index = localY / rowHeight;
        if (index >= 0 && index < teachingPanel.getControlCount()) {
            teachingPanel.triggerControl(index);
            return true;
        }
        return false;
    }

    /**
     * Process an animation panel control click.
     *
     * @param x x coordinate in workspace
     * @param y y coordinate in workspace
     * @return true if a control was triggered
     */
    public boolean processAnimationClick(int x, int y) {
        if (animationPanel == null) {
            return false;
        }
        int[] layout = layoutManager.getAnimationPanelLayout();
        if (layout == null) {
            return false;
        }
        int localX = x - layout[0];
        int localY = y - layout[1];
        int rowHeight = animationPanel.getRowHeight();
        int index = localY / rowHeight;
        if (index >= 0 && index < animationPanel.getControlCount()) {
            animationPanel.triggerControl(index);
            return true;
        }
        return false;
    }

    /**
     * Dispatch all pending UI events from the bridge.
     * Call this once per frame.
     */
    public void dispatchEvents() {
        eventBridge.dispatchAll();
    }

    /**
     * Get the current UI interaction mode.
     */
    public UIInteractionMode getInteractionMode() {
        return layoutManager.getMode();
    }

    /**
     * Set the UI interaction mode (changes layout).
     *
     * @param mode the new mode
     */
    public void setInteractionMode(UIInteractionMode mode) {
        layoutManager.setMode(mode);
    }

    /**
     * Get the current view mode.
     *
     * @return the current view mode, or null if not set
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Set the view mode.
     *
     * @param mode the new view mode
     */
    public void setViewMode(ViewMode mode) {
        this.viewMode = mode;
    }

    // ------------------------------------------------------------------
    // View mode
    // ------------------------------------------------------------------

    /** The current view mode. */
    private ViewMode viewMode;
}
