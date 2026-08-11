package com.geometry.ui.component;

import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Phase 13 - Floating toolbar that can be repositioned.
 *
 * Used primarily in whiteboard and tablet modes.  The toolbar
 * floats above the canvas and can be dragged to a new position
 * by the user.  It auto-hides after a period of inactivity.
 *
 * Not thread-safe.
 */
public class FloatingToolBar extends JPanel {

    /** Margin from edges to keep the toolbar visible. */
    private static final int MARGIN = 20;

    /** Time in ms before the toolbar auto-hides (3000 ms). */
    private static final long AUTO_HIDE_DELAY = 3000L;

    /** The theme applied to this toolbar. */
    private final EducationTheme theme;

    /** Buttons added to the toolbar. */
    private final JPanel contentPanel;

    /** Timer for auto-hide. */
    private Timer autoHideTimer;

    /** Internal visibility flag — managed separately from Swing to avoid recursion. */
    private boolean visibleNow;

    /**
     * Create a floating toolbar.
     *
     * @param theme the education theme
     */
    public FloatingToolBar(EducationTheme theme) {
        this.theme = theme;
        this.contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        this.contentPanel.setOpaque(true);
        this.contentPanel.setBackground(theme.getHeaderColor());
        this.contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        this.setLayout(new BorderLayout());
        this.add(contentPanel);
        this.setOpaque(false);
        this.visibleNow = false;

        // Make draggable
        setupDragHandler();

        // Auto-hide
        this.autoHideTimer = new Timer((int) AUTO_HIDE_DELAY, e -> doHide());
        this.autoHideTimer.setRepeats(false);
    }

    // ------------------------------------------------------------------
    // Content
    // ------------------------------------------------------------------

    /**
     * Add a tool button to the floating toolbar.
     *
     * @param text   button label
     * @param action action command string
     * @param listener button action listener
     */
    public void addToolButton(String text, String action, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(theme.getButtonFont(com.geometry.ui.input.InputMode.DESKTOP));
        btn.setBackground(theme.getPrimaryColor());
        btn.setForeground(theme.getHeaderTextColor());
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setPreferredSize(new Dimension(64, 56));
        btn.addActionListener(listener);
        contentPanel.add(btn);
        revalidate();
        repaint();
    }

    /**
     * Clear all buttons.
     */
    public void clear() {
        contentPanel.removeAll();
        revalidate();
        repaint();
    }

    // ------------------------------------------------------------------
    // Visibility
    // ------------------------------------------------------------------

    /**
     * Show the toolbar at the given position (relative to parent).
     */
    public void showAt(int x, int y) {
        setLocation(x, y);
        setBounds(x, y, Math.max(200, contentPanel.getPreferredSize().width + 24),
                Math.max(60, contentPanel.getPreferredSize().height + 16));
        doShow();
    }

    /**
     * Show the toolbar at the default position (bottom-center).
     */
    public void showDefault() {
        Container parent = getParent();
        if (parent != null) {
            Dimension parentSize = parent.getSize();
            int w = getWidth() > 0 ? getWidth() : 200;
            int h = getHeight() > 0 ? getHeight() : 60;
            int x = Math.max(MARGIN, parentSize.width / 2 - w / 2);
            int y = Math.max(MARGIN, parentSize.height - h - MARGIN);
            showAt(x, y);
        } else {
            showAt(MARGIN, MARGIN);
        }
    }

    /**
     * Hide the toolbar.
     */
    public void hide() {
        doHide();
    }

    /**
     * Internal show — sets flag and calls super.setVisible(true).
     * The guard flag prevents recursion through Swing's property change chain.
     */
    public void doShow() {
        if (visibleNow) return;
        this.visibleNow = true;
        // Use invokeLater to break any recursion through the EDT
        if (SwingUtilities.isEventDispatchThread()) {
            super.setVisible(true);
        } else {
            SwingUtilities.invokeLater(() -> super.setVisible(true));
        }
    }

    /**
     * Internal hide — sets flag and calls super.setVisible(false).
     */
    public void doHide() {
        if (!visibleNow) return;
        this.visibleNow = false;
        if (autoHideTimer != null) {
            autoHideTimer.stop();
        }
        if (SwingUtilities.isEventDispatchThread()) {
            super.setVisible(false);
        } else {
            SwingUtilities.invokeLater(() -> super.setVisible(false));
        }
    }

    /**
     * Check if the toolbar is currently visible.
     */
    public boolean isVisibleNow() {
        return visibleNow;
    }

    private void restartAutoHide() {
        if (autoHideTimer != null) {
            autoHideTimer.stop();
            autoHideTimer.start();
        }
    }

    // ------------------------------------------------------------------
    // Dragging
    // ------------------------------------------------------------------

    private void setupDragHandler() {
        final Point initialClick = new Point();
        this.contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick.setLocation(e.getPoint());
            }
        });
        this.contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point loc = getLocation();
                int newX = loc.x + e.getX() - initialClick.x;
                int newY = loc.y + e.getY() - initialClick.y;
                // Clamp to parent bounds
                Container parent = getParent();
                if (parent != null) {
                    Dimension ps = parent.getSize();
                    newX = Math.max(0, Math.min(newX, ps.width - getWidth()));
                    newY = Math.max(0, Math.min(newY, ps.height - getHeight()));
                }
                setLocation(newX, newY);
                restartAutoHide();
            }
        });
    }
}
