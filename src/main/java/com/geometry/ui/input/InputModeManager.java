package com.geometry.ui.input;

import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.LayoutManager;
import com.geometry.ui.ToolbarSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 13 - Manages the current input mode and dispatches
 * mode-change UI events.
 *
 * The InputModeManager is the single source of truth for which
 * interaction style is active.  It also computes mode-specific
 * layout values (touch tolerance, button sizes, etc.) so that
 * UI components can query them without depending on the manager
 * directly.
 *
 * Architecture:
 *   ApplicationWindow
 *     └── InputModeManager
 *           ├── DesktopInputMode   (mouse + keyboard handlers)
 *           ├── WhiteboardInputMode (touch + pen handlers)
 *           └── TabletInputMode    (hybrid handlers)
 *
 * Not thread-safe.
 */
public class InputModeManager {

    /** The current input mode. */
    private InputMode mode;

    /** The UI event bridge (may be null in headless tests). */
    private final UIEventBridge bridge;

    /** The layout manager used to adapt sizes to the current mode. */
    private final LayoutManager layoutManager;

    /** Active input handlers for the current mode. */
    private final List<InputHandler> handlers;

    /**
     * Create an InputModeManager.
     *
     * @param bridge        the UI event bridge (may be null in tests)
     * @param layoutManager the layout manager (may be null in tests)
     */
    public InputModeManager(UIEventBridge bridge, LayoutManager layoutManager) {
        this.mode = InputMode.DESKTOP;
        this.bridge = bridge;
        this.layoutManager = layoutManager;
        this.handlers = new ArrayList<>();
        this.handlers.add(new DesktopInputMode());
        this.handlers.add(new WhiteboardInputMode());
        this.handlers.add(new TabletInputMode());
    }

    // ------------------------------------------------------------------
    // Mode switching
    // ------------------------------------------------------------------

    /**
     * Get the current input mode.
     */
    public InputMode getMode() {
        return mode;
    }

    /**
     * Switch to a new input mode.
     *
     * @param newMode the mode to switch to
     */
    public void setMode(InputMode newMode) {
        if (newMode == null) {
            throw new IllegalArgumentException("InputMode cannot be null");
        }
        this.mode = newMode;
        // Notify bridge so other components can react
        if (bridge != null) {
            bridge.submit(UIEvent.interactionModeChange(
                    toUIInteractionMode(newMode)));
        }
    }

    /**
     * Switch to desktop mode.
     */
    public void setDesktopMode() {
        setMode(InputMode.DESKTOP);
    }

    /**
     * Switch to whiteboard mode.
     */
    public void setWhiteboardMode() {
        setMode(InputMode.WHITEBOARD);
    }

    /**
     * Switch to tablet mode.
     */
    public void setTabletMode() {
        setMode(InputMode.TABLET);
    }

    /**
     * Check if currently in desktop mode.
     */
    public boolean isDesktop() {
        return mode == InputMode.DESKTOP;
    }

    /**
     * Check if currently in whiteboard mode.
     */
    public boolean isWhiteboard() {
        return mode == InputMode.WHITEBOARD;
    }

    /**
     * Check if currently in tablet mode.
     */
    public boolean isTablet() {
        return mode == InputMode.TABLET;
    }

    /**
     * Cycle through all three modes.
     */
    public void cycleMode() {
        switch (mode) {
            case DESKTOP:
                setMode(InputMode.WHITEBOARD);
                break;
            case WHITEBOARD:
                setMode(InputMode.TABLET);
                break;
            case TABLET:
                setMode(InputMode.DESKTOP);
                break;
        }
    }

    // ------------------------------------------------------------------
    // Mode-specific layout helpers
    // ------------------------------------------------------------------

    /**
     * Get the touch tolerance radius in pixels for the current mode.
     * Larger in whiteboard for finger input, smaller in desktop for precision.
     */
    public int getTouchTolerance() {
        switch (mode) {
            case WHITEBOARD: return 20;
            case TABLET:     return 12;
            default:         return 5; // desktop / mouse
        }
    }

    /**
     * Get the object snap radius in pixels.
     * Used to automatically snap cursor to nearby vertices.
     */
    public int getObjectSnapRadius() {
        switch (mode) {
            case WHITEBOARD: return 30;
            case TABLET:     return 15;
            default:         return 8;
        }
    }

    /**
     * Get the minimum button size for the current mode.
     */
    public int getMinButtonSize() {
        switch (mode) {
            case WHITEBOARD: return ToolbarSize.WHITEBOARD_BUTTON_WIDTH;
            case TABLET:     return 56;
            default:         return ToolbarSize.DESKTOP_BUTTON_WIDTH;
        }
    }

    /**
     * Get the floating toolbar width for the current mode.
     */
    public int getFloatingToolBarWidth() {
        switch (mode) {
            case WHITEBOARD: return 500;
            case TABLET:     return 380;
            default:         return 340;
        }
    }

    /**
     * Get the floating toolbar height for the current mode.
     */
    public int getFloatingToolBarHeight() {
        switch (mode) {
            case WHITEBOARD: return 80;
            case TABLET:     return 64;
            default:         return 52;
        }
    }

    // ------------------------------------------------------------------
    // Handler access
    // ------------------------------------------------------------------

    /**
     * Get the handler that corresponds to the current mode.
     *
     * @return the active InputHandler, or null if not found
     */
    public InputHandler getActiveHandler() {
        for (InputHandler h : handlers) {
            if (h.supports(mode)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Get all registered handlers (for testing / reflection).
     */
    public List<InputHandler> getHandlers() {
        return new ArrayList<>(handlers);
    }

    // ------------------------------------------------------------------
    // Conversion helpers
    // ------------------------------------------------------------------

    /**
     * Convert an InputMode to the legacy UIInteractionMode enum.
     */
    public static com.geometry.ui.UIInteractionMode toUIInteractionMode(InputMode mode) {
        if (mode == InputMode.WHITEBOARD || mode == InputMode.TABLET) {
            return com.geometry.ui.UIInteractionMode.WHITEBOARD;
        }
        return com.geometry.ui.UIInteractionMode.DESKTOP;
    }

    /**
     * Get the current touch tolerance for use by UI components.
     */
    public int getTouchTargetSize() {
        switch (mode) {
            case WHITEBOARD: return 60;
            case TABLET:     return 48;
            default:         return 44;
        }
    }
}
