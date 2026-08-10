package com.geometry.ui;

/**
 * Phase 11 - UI event that bridges UI components and the core engine.
 *
 * UI components (buttons, panels) generate UIEvents instead of directly
 * calling core objects. The UIEventBridge routes these events to the
 * appropriate tool or action.
 *
 * Event types:
 *   - TOOL_SWITCH: User selected a different tool
 *   - SELECT_OBJECT: User selected a scene object
 *   - VIEW_MODE_CHANGE: User changed 2D/3D view mode
 *   - INTERACTION_MODE_CHANGE: User changed whiteboard/desktop mode
 *   - TEACHING_CONTROL: Play/pause/next step in teaching
 *   - ANIMATION_CONTROL: Play/pause/stop animation
 *
 * Not thread-safe.
 */
public class UIEvent {

    /** The type of this UI event. */
    public enum EventType {
        /** Switch to a different tool. */
        TOOL_SWITCH,
        /** Select a scene object by ID. */
        SELECT_OBJECT,
        /** Change the view mode (2D/3D). */
        VIEW_MODE_CHANGE,
        /** Change the interaction mode (whiteboard/desktop). */
        INTERACTION_MODE_CHANGE,
        /** Teaching control (next step, previous step, start/stop). */
        TEACHING_CONTROL,
        /** Animation control (play, pause, stop). */
        ANIMATION_CONTROL,
        /** Toggle visibility of a scene object. */
        TOGGLE_VISIBILITY
    }

    /** The type of this event. */
    private final EventType type;

    /** The event payload data, type-dependent. */
    private final Object data;

    /**
     * Create a UIEvent.
     *
     * @param type the event type
     * @param data the event data (may be null)
     */
    public UIEvent(EventType type, Object data) {
        if (type == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }
        this.type = type;
        this.data = data;
    }

    /**
     * Create a TOOL_SWITCH event.
     *
     * @param toolName the name of the tool to switch to
     * @return the new UIEvent
     */
    public static UIEvent toolSwitch(String toolName) {
        return new UIEvent(EventType.TOOL_SWITCH, toolName);
    }

    /**
     * Create a SELECT_OBJECT event.
     *
     * @param objectId the ID of the object to select
     * @return the new UIEvent
     */
    public static UIEvent selectObject(String objectId) {
        return new UIEvent(EventType.SELECT_OBJECT, objectId);
    }

    /**
     * Create a VIEW_MODE_CHANGE event.
     *
     * @param mode the new view mode
     * @return the new UIEvent
     */
    public static UIEvent viewModeChange(ViewMode mode) {
        return new UIEvent(EventType.VIEW_MODE_CHANGE, mode);
    }

    /**
     * Create an INTERACTION_MODE_CHANGE event.
     *
     * @param mode the new interaction mode
     * @return the new UIEvent
     */
    public static UIEvent interactionModeChange(UIInteractionMode mode) {
        return new UIEvent(EventType.INTERACTION_MODE_CHANGE, mode);
    }

    /**
     * Create a TEACHING_CONTROL event.
     *
     * @param action the teaching action (String: "next", "prev", "start", "stop")
     * @return the new UIEvent
     */
    public static UIEvent teachingControl(String action) {
        return new UIEvent(EventType.TEACHING_CONTROL, action);
    }

    /**
     * Create an ANIMATION_CONTROL event.
     *
     * @param action the animation action (String: "play", "pause", "stop")
     * @return the new UIEvent
     */
    public static UIEvent animationControl(String action) {
        return new UIEvent(EventType.ANIMATION_CONTROL, action);
    }

    /**
     * Create a TOGGLE_VISIBILITY event.
     *
     * @param objectId the ID of the object to toggle
     * @return the new UIEvent
     */
    public static UIEvent toggleVisibility(String objectId) {
        return new UIEvent(EventType.TOGGLE_VISIBILITY, objectId);
    }

    /**
     * Get the event type.
     */
    public EventType getType() {
        return type;
    }

    /**
     * Get the event data.
     */
    public Object getData() {
        return data;
    }

    /**
     * Get the data as a String.
     *
     * @return the data cast to String, or null if not a String
     */
    public String getStringData() {
        return data instanceof String ? (String) data : null;
    }

    /**
     * Get the data as a ViewMode.
     *
     * @return the data cast to ViewMode, or null if not a ViewMode
     */
    public ViewMode getViewMode() {
        return data instanceof ViewMode ? (ViewMode) data : null;
    }

    /**
     * Get the data as a UIInteractionMode.
     *
     * @return the data cast to UIInteractionMode, or null if not a UIInteractionMode
     */
    public UIInteractionMode getUIInteractionMode() {
        return data instanceof UIInteractionMode ? (UIInteractionMode) data : null;
    }

    @Override
    public String toString() {
        return "UIEvent{type=" + type + ", data=" + data + "}";
    }
}
