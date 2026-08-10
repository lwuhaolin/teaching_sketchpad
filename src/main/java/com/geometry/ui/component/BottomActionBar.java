package com.geometry.ui.component;

import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 13 - Bottom action bar for desktop mode.
 *
 * Fixed at the bottom of the window.  Contains primary action
 * buttons that are always visible.
 *
 * Not thread-safe.
 */
public class BottomActionBar extends JPanel {

    /** Height of the action bar. */
    private static final int BAR_HEIGHT = 56;

    /** The theme applied. */
    private final EducationTheme theme;

    /** Buttons in the action bar. */
    private final List<JButton> buttons;

    /**
     * Create a bottom action bar.
     *
     * @param theme the education theme
     */
    public BottomActionBar(EducationTheme theme) {
        this.theme = theme;
        this.buttons = new ArrayList<>();
        setLayout(new FlowLayout(FlowLayout.CENTER, 12, 0));
        setBackground(theme.getToolbarColor());
        setPreferredSize(new Dimension(0, BAR_HEIGHT));
        setMinimumSize(new Dimension(0, BAR_HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, BAR_HEIGHT));
    }

    /**
     * Add an action button.
     *
     * @param text   button label
     * @param action action command
     * @param listener listener
     */
    public void addButton(String text, String action, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(theme.getButtonFont(com.geometry.ui.input.InputMode.DESKTOP));
        btn.setBackground(theme.getActiveToolColor());
        btn.setForeground(theme.getToolbarTextColor());
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(80, 40));
        btn.setActionCommand(action);
        btn.addActionListener(listener);
        add(btn);
        buttons.add(btn);
        revalidate();
        repaint();
    }

    /**
     * Remove all buttons.
     */
    public void clear() {
        buttons.clear();
        removeAll();
        revalidate();
        repaint();
    }

    /**
     * Get the number of buttons.
     */
    public int getButtonCount() {
        return buttons.size();
    }

    /**
     * Get the bar height.
     */
    public int getBarHeight() {
        return BAR_HEIGHT;
    }
}
