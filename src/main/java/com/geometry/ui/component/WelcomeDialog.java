package com.geometry.ui.component;

import com.geometry.ui.UITeachingMode;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.resource.UiStrings;

import javax.swing.*;
import java.awt.*;

/**
 * Phase 14 - Product-level welcome dialog.
 *
 * A modern, friendly first-launch experience that guides the user
 * to select their role (teacher / student) and input mode (desktop /
 * whiteboard / tablet) before entering the main workspace.
 */
public final class WelcomeDialog extends JDialog {

    private UITeachingMode teachingMode = UITeachingMode.TEACHER;
    private InputMode inputMode = InputMode.DESKTOP;

    private WelcomeDialog(Frame owner) {
        super(owner, UiStrings.text("welcome.title"), true);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        buildContent();
        pack();
        setMinimumSize(new Dimension(560, 460));
        setLocationRelativeTo(owner);
    }

    public static Profile choose(Frame owner) {
        WelcomeDialog dialog = new WelcomeDialog(owner);
        dialog.setVisible(true);
        return new Profile(dialog.teachingMode, dialog.inputMode);
    }

    private void buildContent() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(0xF0, 0xF4, 0xFA));

        // ── Header ─────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout(0, 8));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(36, 32, 8, 32));

        JLabel title = new JLabel("◆  " + UiStrings.text("app.welcome"));
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 30));
        title.setForeground(new Color(0x1E, 0x3A, 0x8A));
        header.add(title, BorderLayout.NORTH);

        JLabel tagline = new JLabel(UiStrings.text("app.welcome.tagline"));
        tagline.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        tagline.setForeground(new Color(0x64, 0x74, 0x8B));
        header.add(tagline, BorderLayout.SOUTH);

        root.add(header, BorderLayout.NORTH);

        // ── Body ───────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(8, 32, 8, 32));

        // Role selection
        JPanel roleSection = new JPanel();
        roleSection.setOpaque(false);
        roleSection.setLayout(new BoxLayout(roleSection, BoxLayout.Y_AXIS));

        JLabel roleLabel = new JLabel("选择身份");
        roleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        roleLabel.setForeground(new Color(0x37, 0x41, 0x51));
        roleSection.add(roleLabel);
        roleSection.add(Box.createVerticalStrut(10));

        JPanel roleCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        roleCards.setOpaque(false);

        JRadioButton teacherCard = roleCard("👩‍🏫", UiStrings.text("welcome.teacher.title"),
                UiStrings.text("welcome.teacher.desc"), UITeachingMode.TEACHER);
        JRadioButton studentCard = roleCard("👨‍🎓", UiStrings.text("welcome.student.title"),
                UiStrings.text("welcome.student.desc"), UITeachingMode.STUDENT);
        teacherCard.setSelected(true);
        teacherCard.addActionListener(e -> teachingMode = UITeachingMode.TEACHER);
        studentCard.addActionListener(e -> teachingMode = UITeachingMode.STUDENT);
        roleCards.add(teacherCard);
        roleCards.add(studentCard);
        roleSection.add(roleCards);
        body.add(roleSection, BorderLayout.NORTH);

        // Input mode selection
        JPanel modeSection = new JPanel();
        modeSection.setOpaque(false);
        modeSection.setLayout(new BoxLayout(modeSection, BoxLayout.Y_AXIS));

        JLabel modeLabel = new JLabel("选择交互方式");
        modeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        modeLabel.setForeground(new Color(0x37, 0x41, 0x51));
        modeSection.add(modeLabel);
        modeSection.add(Box.createVerticalStrut(10));

        JPanel modeCards = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        modeCards.setOpaque(false);

        JRadioButton desktopCard = modeCard("🖱️", UiStrings.text("input.desktop"),
                UiStrings.text("welcome.desktop.desc"), InputMode.DESKTOP);
        JRadioButton whiteboardCard = modeCard("🖥️", UiStrings.text("input.whiteboard"),
                UiStrings.text("welcome.whiteboard.desc"), InputMode.WHITEBOARD);
        JRadioButton tabletCard = modeCard("📱", UiStrings.text("input.tablet"),
                UiStrings.text("welcome.tablet.desc"), InputMode.TABLET);
        desktopCard.setSelected(true);
        desktopCard.addActionListener(e -> inputMode = InputMode.DESKTOP);
        whiteboardCard.addActionListener(e -> inputMode = InputMode.WHITEBOARD);
        tabletCard.addActionListener(e -> inputMode = InputMode.TABLET);
        modeCards.add(desktopCard);
        modeCards.add(whiteboardCard);
        modeCards.add(tabletCard);
        modeSection.add(modeCards);
        body.add(modeSection, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 24));
        footer.setOpaque(false);

        JButton startBtn = new JButton("▶  " + UiStrings.text("welcome.start"));
        startBtn.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        startBtn.setBackground(new Color(0x2B, 0x6C, 0xB0));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setContentAreaFilled(true);
        startBtn.setPreferredSize(new Dimension(200, 50));
        startBtn.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        startBtn.addActionListener(e -> dispose());
        footer.add(startBtn);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JRadioButton roleCard(String icon, String title, String desc, UITeachingMode mode) {
        String html = "<html><div style='text-align:center;'>"
                + "<div style='font-size:32px;margin-bottom:6px;'>" + icon + "</div>"
                + "<div style='font-size:15px;font-weight:bold;color:#1E293B;'>" + title + "</div>"
                + "<div style='font-size:12px;color:#64748B;margin-top:4px;'>" + desc + "</div>"
                + "</div></html>";
        JRadioButton btn = new JRadioButton(html);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1, 0xE3, 0xF5)),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        btn.setContentAreaFilled(false);
        return btn;
    }

    private JRadioButton modeCard(String icon, String title, String desc, InputMode mode) {
        String html = "<html><div style='text-align:center;'>"
                + "<div style='font-size:28px;margin-bottom:4px;'>" + icon + "</div>"
                + "<div style='font-size:14px;font-weight:bold;color:#1E293B;'>" + title + "</div>"
                + "<div style='font-size:11px;color:#64748B;margin-top:2px;'>" + desc + "</div>"
                + "</div></html>";
        JRadioButton btn = new JRadioButton(html);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1, 0xE3, 0xF5)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(140, 100));
        return btn;
    }

    public static final class Profile {
        private final UITeachingMode teachingMode;
        private final InputMode inputMode;

        Profile(UITeachingMode teachingMode, InputMode inputMode) {
            this.teachingMode = teachingMode;
            this.inputMode = inputMode;
        }

        public UITeachingMode getTeachingMode() { return teachingMode; }
        public InputMode getInputMode() { return inputMode; }
    }
}
