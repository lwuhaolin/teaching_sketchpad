package com.geometry.ui.component;

import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import java.awt.*;

/**
 * Phase 14 - Premium rounded tool button for the classroom toolbar.
 *
 * Design:
 *   - Large touch-friendly circle / rounded-square
 *   - Icon + label
 *   - Active state with accent colour background
 *   - Smooth border and shadow feel via custom painting
 */
public final class RoundToolButton extends JButton {

    private final String symbol;
    private final Color accent;
    private boolean active;
    private final int buttonRadius;

    public RoundToolButton(String symbol, String label, Color accent) {
        super(label);
        this.symbol = symbol;
        this.accent = accent;
        this.active = false;
        this.buttonRadius = 12;

        setToolTipText(label);
        setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        setForeground(new Color(0x31, 0x3B, 0x55));
        setBackground(Color.WHITE);
        setFocusPainted(false);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        setContentAreaFilled(false);
        setPreferredSize(new Dimension(86, 80));
        setMinimumSize(new Dimension(80, 72));
        setVerticalTextPosition(BOTTOM);
        setHorizontalTextPosition(CENTER);
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int r = buttonRadius;

        // Background fill
        if (active) {
            // Active: accent colour background with slight transparency
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 18));
        } else {
            g2.setColor(Color.WHITE);
        }
        g2.fillRoundRect(0, 0, w - 1, h - 1, r, r);

        // Border
        if (active) {
            g2.setColor(accent);
        } else {
            g2.setColor(new Color(0xDE, 0xE5, 0xF0));
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, r, r);

        // Icon symbol
        g2.setColor(active ? accent : new Color(0x47, 0x55, 0x69));
        Font iconFont = new Font("Microsoft YaHei", Font.BOLD, 22);
        g2.setFont(iconFont);
        FontMetrics fm = g2.getFontMetrics();
        int symW = fm.stringWidth(symbol);
        g2.drawString(symbol, (w - symW) / 2, h / 2 + fm.getAscent() / 2 - 4);

        // Label text below icon
        g2.setColor(active ? accent : new Color(0x64, 0x74, 0x8B));
        Font labelFont = new Font("Microsoft YaHei", Font.PLAIN, 11);
        g2.setFont(labelFont);
        fm = g2.getFontMetrics();
        int labelW = fm.stringWidth(getText());
        g2.drawString(getText(), (w - labelW) / 2, h - 8);

        g2.dispose();
        super.paintComponent(graphics);
    }
}
