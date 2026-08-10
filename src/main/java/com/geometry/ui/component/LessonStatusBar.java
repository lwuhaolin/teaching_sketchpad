package com.geometry.ui.component;

import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Phase 13 - Lesson status bar displayed at the top of the workspace.
 *
 * Shows the current lesson name, step number, and mode indicator.
 *
 * Not thread-safe.
 */
public class LessonStatusBar extends JPanel {

    /** Height of the status bar. */
    private static final int STATUS_BAR_HEIGHT = 32;

    /** The theme. */
    private final EducationTheme theme;

    /** Lesson name label. */
    private final JLabel lessonLabel;

    /** Step display label. */
    private final JLabel stepLabel;

    /** Mode indicator label. */
    private final JLabel modeLabel;

    /**
     * Create a lesson status bar.
     *
     * @param theme the education theme
     */
    public LessonStatusBar(EducationTheme theme) {
        this.theme = theme;
        setLayout(new FlowLayout(FlowLayout.LEFT, 16, 0));
        setBackground(theme.getToolbarColor());
        setPreferredSize(new Dimension(0, STATUS_BAR_HEIGHT));
        setMinimumSize(new Dimension(0, STATUS_BAR_HEIGHT));

        lessonLabel = new JLabel("Lesson: —");
        lessonLabel.setForeground(theme.getToolbarTextColor());
        lessonLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        stepLabel = new JLabel("Step: 0 / 0");
        stepLabel.setForeground(theme.getToolbarTextColor());
        stepLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        modeLabel = new JLabel("Mode: Desktop");
        modeLabel.setForeground(new Color(0x2E, 0xCC, 0x71));
        modeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));

        add(lessonLabel);
        add(stepLabel);
        add(Box.createHorizontalStrut(200));
        add(modeLabel);
    }

    /**
     * Update the lesson name.
     */
    public void setLessonName(String name) {
        lessonLabel.setText("Lesson: " + (name != null ? name : "—"));
    }

    /**
     * Update the step display.
     */
    public void setStepDisplay(String display) {
        stepLabel.setText("Step: " + (display != null ? display : "0 / 0"));
    }

    /**
     * Update the mode indicator.
     */
    public void setModeDisplay(String mode) {
        modeLabel.setText("Mode: " + mode);
    }

    /**
     * Get the status bar height.
     */
    public int getStatusBarHeight() {
        return STATUS_BAR_HEIGHT;
    }
}
