package com.geometry.ui.component;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

/**
 * A short-lived object action palette. It only exposes actions meaningful
 * after a geometry object has been selected, keeping the whiteboard calm.
 */
public final class ContextToolRing extends JPanel {

    public ContextToolRing(ActionListener listener) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 5));
        setPreferredSize(new Dimension(238, 54));
        addButton("↻", "rotate", "旋转", listener);
        addButton("✂", "cut", "切割", listener);
        addButton("▤", "unfold", "展开", listener);
        addButton("⌁", "measure", "测量", listener);
    }

    private void addButton(String icon, String command, String hint, ActionListener listener) {
        JButton button = new JButton(icon);
        button.setActionCommand(command);
        button.setToolTipText(hint);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        button.setForeground(new Color(0x3A, 0x7A, 0xFE));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(46, 42));
        button.addActionListener(listener);
        add(button);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(255, 255, 255, 242));
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g.setColor(new Color(0xD9, 0xE3, 0xF0));
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g.dispose();
        super.paintComponent(graphics);
    }
}
