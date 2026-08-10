package com.geometry.ui.panel;

import com.geometry.teaching.Lesson;
import com.geometry.teaching.Step;
import com.geometry.teaching.TeachingManager;
import com.geometry.ui.UIEvent;
import com.geometry.ui.bridge.UIEventBridge;

/**
 * Phase 11 - Teaching panel for lesson and step control.
 *
 * Displays the current lesson name, step number, and controls:
 *   - Previous step
 *   - Next step
 *   - Start/stop lesson
 *
 * Generates UIEvents for teaching controls through the bridge.
 * The panel does NOT call TeachingManager methods directly.
 *
 * Not thread-safe.
 */
public class TeachingPanel {

    /** The teaching manager for querying state. */
    private final TeachingManager teachingManager;

    /** The event bridge for control events. */
    private final UIEventBridge bridge;

    /** Whether the lesson is currently considered "running". */
    private boolean running;

    /**
     * Create a TeachingPanel.
     *
     * @param teachingManager the teaching manager (may be null in tests)
     * @param bridge          the UIEventBridge (may be null in tests)
     */
    public TeachingPanel(TeachingManager teachingManager, UIEventBridge bridge) {
        this.teachingManager = teachingManager;
        this.bridge = bridge;
        this.running = false;
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
        return 200;
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
     * Get the current lesson name, or "No lesson" if none active.
     */
    public String getLessonName() {
        if (teachingManager == null) {
            return "No lesson";
        }
        Lesson lesson = teachingManager.getCurrentLesson();
        return lesson != null ? lesson.getLessonName() : "No lesson";
    }

    /**
     * Get the current step display string, e.g. "Step 2 / 5".
     */
    public String getStepDisplay() {
        if (teachingManager == null) {
            return "Step 0 / 0";
        }
        int current = teachingManager.getCurrentStepNumber();
        int total = teachingManager.getTotalStepCount();
        return "Step " + current + " / " + total;
    }

    /**
     * Check if a lesson is currently active.
     */
    public boolean isLessonActive() {
        return teachingManager != null && teachingManager.isLessonActive();
    }

    /**
     * Check if the panel is in running state.
     */
    public boolean isRunning() {
        return running;
    }

    // ------------------------------------------------------------------
    // Controls
    // ------------------------------------------------------------------

    /**
     * Trigger the "next step" control.
     */
    public void onNextStep() {
        if (bridge != null) {
            bridge.submit(UIEvent.teachingControl("next"));
        }
    }

    /**
     * Trigger the "previous step" control.
     */
    public void onPreviousStep() {
        if (bridge != null) {
            bridge.submit(UIEvent.teachingControl("prev"));
        }
    }

    /**
     * Trigger the "start" control.
     */
    public void onStart() {
        this.running = true;
        if (bridge != null) {
            bridge.submit(UIEvent.teachingControl("start"));
        }
    }

    /**
     * Trigger the "stop" control.
     */
    public void onStop() {
        this.running = false;
        if (bridge != null) {
            bridge.submit(UIEvent.teachingControl("stop"));
        }
    }

    /**
     * Get the label for the control button at the given index.
     *
     * @param index 0=previous, 1=start/stop, 2=next
     * @return the button label, or null if index is invalid
     */
    public String getControlLabel(int index) {
        switch (index) {
            case 0: return "< Prev";
            case 1: return running ? "Stop" : "Start";
            case 2: return "Next >";
            default: return null;
        }
    }

    /**
     * Get the number of control buttons.
     */
    public int getControlCount() {
        return 3;
    }

    /**
     * Trigger the control at the given index.
     *
     * @param index 0=previous, 1=start/stop, 2=next
     */
    public void triggerControl(int index) {
        switch (index) {
            case 0:
                onPreviousStep();
                break;
            case 1:
                if (running) {
                    onStop();
                } else {
                    onStart();
                }
                break;
            case 2:
                onNextStep();
                break;
        }
    }
}
