package com.geometry.ui.panel;

import com.geometry.animation.AnimationManager;
import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;

/**
 * Phase 11 - Animation panel for controlling playback.
 *
 * Displays animation playback controls:
 *   - Play / Pause / Stop
 *   - Current time display
 *
 * Generates UIEvents for animation controls through the bridge.
 * The panel does NOT call AnimationManager methods directly.
 *
 * Not thread-safe.
 */
public class AnimationPanel {

    /** The animation manager for querying state. */
    private final AnimationManager animationManager;

    /** The event bridge for control events. */
    private final UIEventBridge bridge;

    /** Whether the animation is currently playing. */
    private boolean playing;

    /**
     * Create an AnimationPanel.
     *
     * @param animationManager the animation manager (may be null in tests)
     * @param bridge           the UIEventBridge (may be null in tests)
     */
    public AnimationPanel(AnimationManager animationManager, UIEventBridge bridge) {
        this.animationManager = animationManager;
        this.bridge = bridge;
        this.playing = false;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    /**
     * Get the preferred width of this panel in pixels.
     */
    public int getPreferredWidth() {
        return 240;
    }

    /**
     * Get the preferred height of this panel in pixels.
     */
    public int getPreferredHeight() {
        return 150;
    }

    /**
     * Get the height per button row in pixels.
     */
    public int getRowHeight() {
        return 36;
    }

    // ------------------------------------------------------------------
    // State display
    // ------------------------------------------------------------------

    /**
     * Get the current playback time string based on animation progress.
     * Format: "0.0s / 5.0s"
     */
    public String getTimeDisplay() {
        if (animationManager == null || animationManager.getAnimationCount() == 0) {
            return "0.0s / 0.0s";
        }
        com.geometry.animation.Animation firstAnim = animationManager.getAnimations().get(0);
        float duration = firstAnim.getDuration();
        float currentTime = firstAnim.getProgress() * duration;
        return String.format("%.1fs / %.1fs", currentTime, duration);
    }

    /**
     * Check if an animation is currently available.
     */
    public boolean hasAnimation() {
        return animationManager != null && animationManager.getAnimationCount() > 0;
    }

    /**
     * Check if the animation is currently playing.
     */
    public boolean isPlaying() {
        return playing;
    }

    // ------------------------------------------------------------------
    // Controls
    // ------------------------------------------------------------------

    /**
     * Trigger the play/pause control.
     */
    public void onPlayPause() {
        this.playing = !playing;
        if (bridge != null) {
            bridge.submit(UIEvent.animationControl(playing ? "play" : "pause"));
        }
    }

    /**
     * Trigger the stop control.
     */
    public void onStop() {
        this.playing = false;
        if (bridge != null) {
            bridge.submit(UIEvent.animationControl("stop"));
        }
    }

    /**
     * Get the label for the control button at the given index.
     *
     * @param index 0=play/pause, 1=stop
     * @return the button label, or null if index is invalid
     */
    public String getControlLabel(int index) {
        switch (index) {
            case 0: return playing ? "Pause" : "Play";
            case 1: return "Stop";
            default: return null;
        }
    }

    /**
     * Get the number of control buttons.
     */
    public int getControlCount() {
        return 2;
    }

    /**
     * Trigger the control at the given index.
     *
     * @param index 0=play/pause, 1=stop
     */
    public void triggerControl(int index) {
        switch (index) {
            case 0: onPlayPause(); break;
            case 1: onStop(); break;
        }
    }
}
