package com.geometry.ui.bridge;

import com.geometry.ui.UIEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 11 - Bridges UI events to the core engine action system.
 *
 * UI components (buttons, panels) generate UIEvents. They never call
 * core objects directly. The UIEventBridge translates events into
 * actions or state changes in the core system.
 *
 * Flow:
 *   Button.click() → UIEvent → UIEventBridge → Action/Tool/State change
 *
 * The bridge holds references to:
 *   - ToolManager: for tool switching
 *   - Scene: for object selection and visibility
 *
 * Not thread-safe.
 */
public class UIEventBridge {

    /** Reference to the tool manager for tool switching. */
    private final com.geometry.tools.ToolManager toolManager;

    /** Reference to the scene for object operations. */
    private final com.geometry.scene.Scene scene;

    /** Reference to the interaction manager for viewport mode changes. */
    private final com.geometry.interaction.InteractionManager interactionManager;

    /**
     * Queue of UI events waiting to be dispatched.
     */
    private final List<UIEvent> eventQueue;

    /**
     * Create a UIEventBridge with the required core references.
     *
     * @param toolManager        the tool manager (may be null in headless tests)
     * @param scene              the scene (may be null in headless tests)
     * @param interactionManager the interaction manager (may be null)
     */
    public UIEventBridge(
            com.geometry.tools.ToolManager toolManager,
            com.geometry.scene.Scene scene,
            com.geometry.interaction.InteractionManager interactionManager) {
        this.toolManager = toolManager;
        this.scene = scene;
        this.interactionManager = interactionManager;
        this.eventQueue = new ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Event submission
    // ------------------------------------------------------------------

    /**
     * Submit a UIEvent for processing.
     * Events are queued and processed in order.
     *
     * @param event the UI event to submit
     */
    public void submit(UIEvent event) {
        if (event != null) {
            eventQueue.add(event);
        }
    }

    /**
     * Submit multiple UIEvents for processing.
     *
     * @param events the list of UI events to submit
     */
    public void submit(List<UIEvent> events) {
        if (events != null) {
            eventQueue.addAll(events);
        }
    }

    /**
     * Get the number of events currently in the queue.
     */
    public int getQueuedEventCount() {
        return eventQueue.size();
    }

    /**
     * Clear all pending events in the queue.
     */
    public void clearQueue() {
        eventQueue.clear();
    }

    // ------------------------------------------------------------------
    // Event dispatch (called once per frame)
    // ------------------------------------------------------------------

    /**
     * Dispatch all pending events in the queue.
     *
     * Processes events in FIFO order. After processing,
     * the queue is cleared.
     */
    public void dispatchAll() {
        List<UIEvent> pending = new ArrayList<>(eventQueue);
        eventQueue.clear();

        for (UIEvent event : pending) {
            dispatch(event);
        }
    }

    /**
     * Dispatch a single UIEvent to the appropriate handler.
     *
     * @param event the event to dispatch
     */
    private void dispatch(UIEvent event) {
        if (event == null) {
            return;
        }

        switch (event.getType()) {
            case TOOL_SWITCH:
                onToolSwitch(event);
                break;
            case SELECT_OBJECT:
                onSelectObject(event);
                break;
            case VIEW_MODE_CHANGE:
                onViewModeChange(event);
                break;
            case INTERACTION_MODE_CHANGE:
                onInteractionModeChange(event);
                break;
            case TEACHING_CONTROL:
                onTeachingControl(event);
                break;
            case ANIMATION_CONTROL:
                onAnimationControl(event);
                break;
            case TOGGLE_VISIBILITY:
                onToggleVisibility(event);
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------
    // Event handlers
    // ------------------------------------------------------------------

    private void onToolSwitch(UIEvent event) {
        if (toolManager == null) {
            return;
        }
        String toolName = event.getStringData();
        if (toolName != null && !toolName.isEmpty()) {
            toolManager.switchTool(toolName);
        }
    }

    private void onSelectObject(UIEvent event) {
        if (scene == null) {
            return;
        }
        String objectId = event.getStringData();
        if (objectId != null) {
            com.geometry.scene.SceneObject obj = scene.findObjectById(objectId);
            if (obj != null) {
                scene.select(obj);
            } else {
                scene.clearSelection();
            }
        } else {
            scene.clearSelection();
        }
    }

    private void onViewModeChange(UIEvent event) {
        // View mode change is handled by the application layer
        // The UI event is preserved for the application to act on
    }

    private void onInteractionModeChange(UIEvent event) {
        if (interactionManager == null) {
            return;
        }
        com.geometry.ui.UIInteractionMode mode = event.getUIInteractionMode();
        if (mode != null) {
            com.geometry.interaction.InteractionMode imMode =
                    mode == com.geometry.ui.UIInteractionMode.WHITEBOARD
                            ? com.geometry.interaction.InteractionMode.WHITEBOARD
                            : com.geometry.interaction.InteractionMode.DESKTOP;
            interactionManager.setMode(imMode);
        }
    }

    private void onTeachingControl(UIEvent event) {
        // Teaching controls are handled by the application layer
        // The UI event is preserved for the application to act on
    }

    private void onAnimationControl(UIEvent event) {
        // Animation controls are handled by the application layer
        // The UI event is preserved for the application to act on
    }

    private void onToggleVisibility(UIEvent event) {
        if (scene == null) {
            return;
        }
        String objectId = event.getStringData();
        if (objectId != null) {
            com.geometry.scene.SceneObject obj = scene.findObjectById(objectId);
            if (obj != null) {
                obj.setVisible(!obj.isVisible());
            }
        }
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the event queue size.
     */
    public int getQueueSize() {
        return eventQueue.size();
    }

    /**
     * Check if the event queue is empty.
     */
    public boolean isQueueEmpty() {
        return eventQueue.isEmpty();
    }
}
