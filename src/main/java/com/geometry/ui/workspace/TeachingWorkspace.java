package com.geometry.ui.workspace;

import com.geometry.animation.AnimationManager;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.TeachingManager;
import com.geometry.teaching.TeachingMode;
import com.geometry.tools.ToolManager;
import com.geometry.ui.*;
import com.geometry.ui.bridge.ToolBootstrapper;
import com.geometry.ui.component.*;
import com.geometry.ui.canvas.CanvasCommandListener;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.input.InputModeManager;
import com.geometry.ui.interaction.TeachingInteractionController;
import com.geometry.ui.resource.UiStrings;
import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import com.geometry.interaction.action.MoveAction;
import com.geometry.interaction.action.RotateAction;
import com.geometry.interaction.action.ScaleAction;
import com.geometry.interaction.action.DrawAction;
import com.geometry.interaction.mode.GeometryMode;
import com.geometry.interaction.mode.ModeManager;
import com.geometry.scene.SelectionManager;
import com.geometry.tools.cut.CutTool;
import com.geometry.core.math.Vec3;

/**
 * Phase 14 — Product-level teaching workspace.
 *
 * Layout (desktop / 键鼠 mode):
 *
 *  ┌──────────────────────────────────────────────────────────────────┐
 *  │  LOGO   当前课程: 圆柱的展开   步骤 2/6     模式▼  撤销  重做  帮助 │  ← Header
 *  ├──────┬─────────────────────────────────────────┬─────────────────┤
 *  │      │                                         │  教学步骤       │
 *  │ 导航 │           几何白板区域                   │  ① 认识几何体  ✓│
 *  │ 栏   │        (Canvas 占主要面积)               │  ② 观察结构    ●│
 *  │      │                                         │  ③ 切开几何体  ○│
 *  │ 对象 │                                         │  ④ 展开侧面    ○│
 *  │ 列表 │                                         │  ⑤ 验证结论    ○│
 *  │      │                                         │  ─────────────  │
 *  │      │                                         │  💡 点击对象…  │
 *  │      │                                         │  [上一步][下一步]│
 *  ├──────┴─────────────────────────────────────────┴─────────────────┤
 *  │  选择  移动  旋转  缩放  切割  展开  测量  动画                   │  ← Bottom toolbar
 *  └──────────────────────────────────────────────────────────────────┘
 *
 * In whiteboard/tablet mode the side panels collapse and the bottom
 * toolbar becomes a floating bar.
 */
public class TeachingWorkspace extends JFrame {

    private final Scene scene;
    private final EducationTheme theme;
    private final ToolManager toolManager;
    private final TeachingInteractionController commandController;
    private final TeachingManager teachingManager;
    private final InputModeManager inputModeManager;
    private final ModeManager geometryModeManager;

    private GeometryCanvasView canvasView;
    private GeometryCanvasView twoDimensionalCanvas;
    private GeometryCanvasView threeDimensionalCanvas;
    private CardLayout viewPageLayout;
    private JPanel viewPages;
    private FloatingToolBar floatingToolBar;
    private JPanel bottomToolbar;
    private JPanel leftSidebar;
    private JPanel rightPanel;
    private JLayeredPane teachingStage;
    private JPanel canvasCard;
    private JButton objectsButton;
    private JPanel lessonCard;
    private ContextToolRing contextToolRing;
    private JPanel objectListContainer;
    private JLabel toolHint;
    private JButton modeLabel;
    private JButton twoDimensionalPageButton;
    private JButton threeDimensionalPageButton;
    private final List<RoundToolButton> toolButtons = new ArrayList<>();
    private UITeachingMode teachingMode = UITeachingMode.TEACHER;
    private boolean welcomeShown = false;

    public TeachingWorkspace(Scene scene, ToolManager toolManager,
                             InteractionManager interactionManager,
                             TeachingManager teachingManager,
                             AnimationManager animationManager) {
        this(scene, toolManager, interactionManager, teachingManager, animationManager,
                new EducationTheme());
    }

    public TeachingWorkspace(Scene scene, ToolManager toolManager,
                             InteractionManager interactionManager,
                             TeachingManager teachingManager,
                             AnimationManager animationManager,
                             EducationTheme theme) {
        this.theme = theme;
        this.scene = scene;
        this.toolManager = toolManager;
        this.teachingManager = teachingManager;
        ToolBootstrapper.registerMissingTools(toolManager, scene);
        this.geometryModeManager = new ModeManager(scene);
        this.geometryModeManager.applyTo(ToolBootstrapper.getToolContext(toolManager));
        this.commandController = new TeachingInteractionController(scene, teachingManager, animationManager);
        this.inputModeManager = new InputModeManager(null, null);

        setTitle(UiStrings.text("app.title"));
        setMinimumSize(new Dimension(1024, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildContent(scene, toolManager, interactionManager, animationManager);
        geometryModeManager.setMode(GeometryMode.MODE_2D);
        geometryModeManager.applyTo(ToolBootstrapper.getToolContext(toolManager));
    }

    // ── Build ─────────────────────────────────────────────────────────

    private void buildContent(Scene scene, ToolManager toolManager,
                               InteractionManager interactionManager,
                               AnimationManager animationManager) {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(theme.getBackgroundColor());

        // Header
        root.add(buildHeader(), BorderLayout.NORTH);

        // Canvas-first workspace. Supporting information lives in overlays,
        // not permanent engineering-style sidebars.
        teachingStage = new JLayeredPane();
        teachingStage.setOpaque(true);
        teachingStage.setBackground(theme.getBackgroundColor());
        teachingStage.setBorder(new EmptyBorder(10, 12, 10, 12));

        viewPageLayout = new CardLayout();
        viewPages = new JPanel(viewPageLayout);
        viewPages.setOpaque(false);
        twoDimensionalCanvas = createCanvasPage(ViewMode.MODE_2D);
        threeDimensionalCanvas = createThreeDimensionalCanvas();
        canvasView = twoDimensionalCanvas;
        viewPages.add(twoDimensionalCanvas, ViewMode.MODE_2D.name());
        viewPages.add(threeDimensionalCanvas, ViewMode.MODE_3D.name());

        // The canvas remains the single dominant visual surface.
        canvasCard = new JPanel(new BorderLayout());
        canvasCard.setOpaque(true);
        canvasCard.setBackground(theme.getCanvasBackgroundColor());
        canvasCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getCanvasBorderColor(), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        canvasCard.add(viewPages, BorderLayout.CENTER);
        teachingStage.add(canvasCard, JLayeredPane.DEFAULT_LAYER);

        leftSidebar = buildObjectDrawer(scene);
        leftSidebar.setVisible(false);
        teachingStage.add(leftSidebar, JLayeredPane.PALETTE_LAYER);

        objectsButton = overlayButton("▣  对象", new Color(0x31, 0x3B, 0x55));
        objectsButton.addActionListener(e -> {
            leftSidebar.setVisible(!leftSidebar.isVisible());
            teachingStage.revalidate();
            teachingStage.repaint();
        });
        teachingStage.add(objectsButton, JLayeredPane.PALETTE_LAYER);

        lessonCard = buildLessonCard();
        teachingStage.add(lessonCard, JLayeredPane.PALETTE_LAYER);

        contextToolRing = new ContextToolRing(e -> handleToolAction(e.getActionCommand(), e.getActionCommand()));
        contextToolRing.setVisible(false);
        teachingStage.add(contextToolRing, JLayeredPane.MODAL_LAYER);
        teachingStage.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutTeachingStage(); }
        });
        root.add(teachingStage, BorderLayout.CENTER);

        bottomToolbar = buildBottomToolbar();
        root.add(bottomToolbar, BorderLayout.SOUTH);

        // Floating toolbar (whiteboard / tablet mode)
        floatingToolBar = new FloatingToolBar(theme);
        configureFloatingToolbar();
        getLayeredPane().add(floatingToolBar, JLayeredPane.PALETTE_LAYER);

        setContentPane(root);
        pack();
        setSize(1400, 880);
        setLocationRelativeTo(null);
        layoutTeachingStage();
        applyProductMode();
    }

    private GeometryCanvasView createCanvasPage(ViewMode viewMode) {
        GeometryCanvasView page = new GeometryCanvasView(scene, null, theme);
        page.setViewMode(viewMode);
        page.setCommandListener(createCanvasCommandListener());
        page.setOpaque(false);
        return page;
    }

    private GeometryCanvasView createThreeDimensionalCanvas() {
        LwjglThreeDimensionalCanvas page = new LwjglThreeDimensionalCanvas(scene, theme);
        page.setCommandListener(createCanvasCommandListener());
        page.setOpaque(false);
        return page;
    }

    private void layoutTeachingStage() {
        if (teachingStage == null) return;
        int width = teachingStage.getWidth();
        int height = teachingStage.getHeight();
        if (canvasCard != null) canvasCard.setBounds(12, 10, Math.max(0, width - 24), Math.max(0, height - 20));
        if (objectsButton != null) objectsButton.setBounds(30, Math.max(24, height - 64), 106, 38);
        if (leftSidebar != null) leftSidebar.setBounds(30, Math.max(20, height - 340), 272, 264);
        if (lessonCard != null) lessonCard.setBounds(Math.max(20, width - 250), Math.max(20, height - 108), 220, 72);
        if (contextToolRing != null && contextToolRing.isVisible()) {
            int x = Math.min(Math.max(20, contextToolRing.getX()), Math.max(20, width - contextToolRing.getWidth() - 20));
            int y = Math.min(Math.max(20, contextToolRing.getY()), Math.max(20, height - contextToolRing.getHeight() - 20));
            contextToolRing.setLocation(x, y);
        }
    }

    private JButton overlayButton(String text, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        button.setForeground(foreground);
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9, 0xE3, 0xF0)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)));
        return button;
    }

    private JPanel buildObjectDrawer(Scene activeScene) {
        JPanel drawer = new JPanel(new BorderLayout(0, 8));
        drawer.setBackground(Color.WHITE);
        drawer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9, 0xE3, 0xF0)),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        JLabel title = new JLabel("对象");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        title.setForeground(theme.getTextColour());
        drawer.add(title, BorderLayout.NORTH);
        JPanel entries = new JPanel();
        entries.setOpaque(false);
        entries.setLayout(new BoxLayout(entries, BoxLayout.Y_AXIS));
        if (activeScene != null && activeScene.getObjectCount() > 0) {
            for (SceneObject object : activeScene.getAllObjects()) {
                JButton entry = new JButton("●  " + geometryName(object));
                entry.setHorizontalAlignment(SwingConstants.LEFT);
                entry.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
                entry.setForeground(theme.getTextColour());
                entry.setBackground(Color.WHITE);
                entry.setFocusPainted(false);
                entry.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
                entries.add(entry);
            }
        } else {
            JLabel empty = new JLabel("还没有可显示的对象");
            empty.setForeground(theme.getTextSecondaryColour());
            empty.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            entries.add(empty);
        }
        drawer.add(entries, BorderLayout.CENTER);
        JButton reset = new JButton("复位视图");
        reset.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        reset.addActionListener(e -> canvasView.resetView());
        drawer.add(reset, BorderLayout.SOUTH);
        return drawer;
    }

    private JPanel buildLessonCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9, 0xE3, 0xF0)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        JLabel step = new JLabel("步骤 1");
        step.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        step.setForeground(theme.getPrimaryColor());
        JLabel detail = new JLabel("认识几何体");
        detail.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        detail.setForeground(theme.getTextColour());
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(step); text.add(Box.createVerticalStrut(3)); text.add(detail);
        card.add(text, BorderLayout.CENTER);
        JButton expand = new JButton("⌃");
        expand.setToolTipText("教学步骤");
        expand.setFocusPainted(false);
        expand.setBorderPainted(false);
        expand.setContentAreaFilled(false);
        expand.addActionListener(e -> showLessonMenu(expand));
        card.add(expand, BorderLayout.EAST);
        return card;
    }

    private void showLessonMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        String[] steps = {"1  认识几何体", "2  观察结构", "3  切开圆柱体", "4  展开侧面", "5  验证展开图"};
        for (String step : steps) {
            JMenuItem item = new JMenuItem(step);
            item.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            menu.add(item);
        }
        menu.show(invoker, -165, -165);
    }

    // ── Header ────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(theme.getHeaderColor());
        header.setPreferredSize(new Dimension(0, UIStyle.HEADER_HEIGHT));
        header.setMinimumSize(new Dimension(0, UIStyle.HEADER_HEIGHT));

        // Left: logo
        JLabel logo = new JLabel("▣  " + UiStrings.text("lesson.default"));
        logo.setFont(new Font("Microsoft YaHei", Font.BOLD, 17));
        logo.setForeground(theme.getTextColour());
        logo.setBorder(new EmptyBorder(0, 20, 0, 0));
        header.add(logo, BorderLayout.WEST);

        // Center: lesson info
        JPanel lessonInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        lessonInfo.setOpaque(false);

        JLabel lessonLabel = new JLabel("几何课堂");
        lessonLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        lessonLabel.setForeground(theme.getTextSecondaryColour());
        lessonInfo.add(lessonLabel);

        // Step indicator
        JPanel stepBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        stepBadge.setOpaque(false);
        JLabel stepLabel = new JLabel("步骤 1 / 5");
        stepLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        stepLabel.setForeground(theme.getTextColour());
        stepBadge.add(stepLabel);
        lessonInfo.add(stepBadge);

        lessonInfo.add(buildViewPageSwitcher());

        header.add(lessonInfo, BorderLayout.CENTER);

        // Right: controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        modeLabel = new JButton("✋ 触控 / 🖱 键鼠");
        modeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        modeLabel.setForeground(theme.getPrimaryColor());
        modeLabel.setBackground(new Color(0xEE, 0xF4, 0xFF));
        modeLabel.setContentAreaFilled(true);
        modeLabel.setBorderPainted(false);
        modeLabel.setFocusPainted(false);
        modeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modeLabel.setPreferredSize(new Dimension(136, 34));
        modeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showModeMenu(modeLabel);
            }
        });
        controls.add(modeLabel);

        JButton helpBtn = headerIconBtn("?");
        helpBtn.addActionListener(e -> showHelp());
        controls.add(helpBtn);

        JLabel avatar = new JLabel("老师");
        avatar.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        avatar.setForeground(theme.getTextSecondaryColour());
        controls.setBorder(new EmptyBorder(0, 0, 0, 16));
        controls.add(avatar);

        header.add(controls, BorderLayout.EAST);
        return header;
    }

    private JPanel buildViewPageSwitcher() {
        JPanel switcher = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switcher.setOpaque(false);
        twoDimensionalPageButton = createViewPageButton("二维", ViewMode.MODE_2D);
        threeDimensionalPageButton = createViewPageButton("三维", ViewMode.MODE_3D);
        switcher.add(twoDimensionalPageButton);
        switcher.add(threeDimensionalPageButton);
        updateViewPageButtons(ViewMode.MODE_2D);
        return switcher;
    }

    private JButton createViewPageButton(String text, final ViewMode viewMode) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        button.setPreferredSize(new Dimension(58, 30));
        button.addActionListener(e -> setViewMode(viewMode));
        return button;
    }

    private void updateViewPageButtons(ViewMode activeMode) {
        updateViewPageButton(twoDimensionalPageButton, activeMode == ViewMode.MODE_2D);
        updateViewPageButton(threeDimensionalPageButton, activeMode == ViewMode.MODE_3D);
    }

    private void updateViewPageButton(JButton button, boolean active) {
        if (button == null) {
            return;
        }
        button.setForeground(active ? Color.WHITE : theme.getTextSecondaryColour());
        button.setBackground(active ? theme.getPrimaryColor() : new Color(0xF1, 0xF5, 0xF9));
        button.setContentAreaFilled(true);
    }

    private JButton headerIconBtn(String icon) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        btn.setForeground(theme.getTextSecondaryColour());
        btn.setBackground(new Color(0xF1, 0xF5, 0xF9));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(36, 32));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        return btn;
    }

    private void showModeMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        JLabel title = new JLabel("选择操作方式");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        title.setBorder(new EmptyBorder(12, 14, 6, 14));
        menu.add(title);
        String[] modes = {
                UiStrings.text("input.desktop"),
                UiStrings.text("input.whiteboard"),
                UiStrings.text("input.tablet")
        };
        InputMode[] modeValues = {InputMode.DESKTOP, InputMode.WHITEBOARD, InputMode.TABLET};
        for (int i = 0; i < modes.length; i++) {
            final int modeIdx = i;
            String prefix = i == 0 ? "● " : "○ ";
            String suffix = i == 0 ? "（推荐课堂）" : "";
            JMenuItem item = new JMenuItem(prefix + modes[i] + suffix);
            item.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            item.addActionListener(e -> setInputMode(modeValues[modeIdx]));
            item.setPreferredSize(new Dimension(250, 38));
            menu.add(item);
        }
        menu.show(invoker, 0, invoker.getHeight());
    }

    // ── Left sidebar ──────────────────────────────────────────────────

    private JPanel buildLeftSidebar(Scene scene) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(UIStyle.SIDEBAR_WIDTH, 0));
        sidebar.setMinimumSize(new Dimension(UIStyle.SIDEBAR_WIDTH, 0));

        // ── Navigation section ──
        JPanel navSection = new JPanel();
        navSection.setLayout(new BoxLayout(navSection, BoxLayout.Y_AXIS));
        navSection.setOpaque(false);

        String[] navItems = {"课程", "对象", "工具", "测量", "动画", "标注", "设置"};
        for (int i = 0; i < navItems.length; i++) {
            JButton navBtn = new JButton(navItems[i]);
            navBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
            navBtn.setForeground(i == 1 ? theme.getSidebarActiveTextColor()
                    : new Color(0x47, 0x55, 0x69));
            navBtn.setBackground(i == 1 ? theme.getSidebarActiveColor() : Color.WHITE);
            navBtn.setFocusPainted(false);
            navBtn.setBorderPainted(false);
            navBtn.setContentAreaFilled(true);
            navBtn.setAlignmentX(CENTER_ALIGNMENT);
            navBtn.setPreferredSize(new Dimension(UIStyle.SIDEBAR_WIDTH - 8, 36));
            navBtn.setMaximumSize(new Dimension(UIStyle.SIDEBAR_WIDTH - 8, 36));
            navSection.add(navBtn);
            navSection.add(Box.createVerticalStrut(2));
        }
        sidebar.add(navSection);
        sidebar.add(Box.createVerticalStrut(4));

        // ── Object list ──
        objectListContainer = buildObjectListPanel(scene);
        sidebar.add(objectListContainer);
        sidebar.add(Box.createVerticalStrut(4));

        return sidebar;
    }

    private JPanel buildObjectListPanel(Scene scene) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        header.setOpaque(false);
        JLabel headerLabel = new JLabel("  " + UiStrings.text("panel.objects"));
        headerLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        headerLabel.setForeground(new Color(0x64, 0x74, 0x8B));
        header.add(headerLabel);
        panel.add(header, BorderLayout.NORTH);

        // Object list
        JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        listContainer.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        if (scene != null) {
            for (SceneObject obj : scene.getAllObjects()) {
                if (canvasView == null || canvasView.isObjectInCurrentView(obj)) {
                    JPanel row = buildObjectRow(obj);
                    listContainer.add(row);
                }
            }
        }
        if (scene == null || canvasView == null || canvasView.getVisibleObjectCount() == 0) {
            JLabel empty = new JLabel("  " + UiStrings.text("hint.empty"));
            empty.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
            empty.setForeground(new Color(0x94, 0xA3, 0xB8));
            empty.setAlignmentX(CENTER_ALIGNMENT);
            listContainer.add(empty);
        }

        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll, BorderLayout.CENTER);

        // Reset view button
        JButton resetBtn = new JButton("⟲ " + UiStrings.text("tool.reset"));
        resetBtn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        resetBtn.setForeground(new Color(0x3B, 0x82, 0xF6));
        resetBtn.setBackground(Color.WHITE);
        resetBtn.setFocusPainted(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setContentAreaFilled(true);
        resetBtn.setPreferredSize(new Dimension(0, 26));
        resetBtn.addActionListener(e -> {
            if (canvasView != null) canvasView.resetView();
            toolHint.setText(UiStrings.text("hint.reset"));
        });
        panel.add(resetBtn, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildObjectRow(SceneObject obj) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(UIStyle.SIDEBAR_WIDTH - 4, 30));
        row.setMaximumSize(new Dimension(UIStyle.SIDEBAR_WIDTH - 4, 30));
        row.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        String name = geometryName(obj);
        Color objColor = getObjectColor(obj);

        JLabel icon = new JLabel("●");
        icon.setForeground(objColor);
        icon.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));

        JLabel label = new JLabel("  " + name);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        label.setForeground(new Color(0x37, 0x41, 0x51));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        row.add(icon);
        row.add(label);

        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(0xF0, 0xF4, 0xFA));
                row.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(Color.WHITE);
                row.setOpaque(true);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Select this object in the canvas
                if (canvasView != null) {
                    canvasView.handleTap(
                            canvasView.getWidth() / 2,
                            canvasView.getHeight() / 2);
                }
            }
        });

        return row;
    }

    // ── Right panel (teaching steps) ──────────────────────────────────

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setMinimumSize(new Dimension(200, 0));

        // ── Steps ──
        JPanel stepContainer = new JPanel();
        stepContainer.setLayout(new BoxLayout(stepContainer, BoxLayout.Y_AXIS));
        stepContainer.setOpaque(false);
        stepContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Panel header
        JPanel stepHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        stepHeader.setOpaque(false);
        JLabel stepTitle = new JLabel(UiStrings.text("panel.steps"));
        stepTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        stepTitle.setForeground(new Color(0x37, 0x41, 0x51));
        stepHeader.add(stepTitle);
        stepContainer.add(stepHeader);
        stepContainer.add(Box.createVerticalStrut(8));

        String[] stepNames = {
                UiStrings.text("step.one"),
                UiStrings.text("step.two"),
                UiStrings.text("step.three"),
                UiStrings.text("step.four"),
                UiStrings.text("step.five")
        };
        for (int i = 0; i < stepNames.length; i++) {
            JPanel stepRow = buildStepRow(i + 1, stepNames[i], i == 0);
            stepContainer.add(stepRow);
        }

        JScrollPane stepScroll = new JScrollPane(stepContainer);
        stepScroll.setBorder(null);
        stepScroll.setOpaque(false);
        stepScroll.getViewport().setOpaque(false);
        stepScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(stepScroll, BorderLayout.CENTER);

        // ── Hint box ──
        JPanel hintBox = new JPanel(new BorderLayout(8, 0));
        hintBox.setOpaque(false);
        hintBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xFE, 0xF2, 0xA0)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel hintIcon = new JLabel("💡");
        hintIcon.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        hintBox.add(hintIcon, BorderLayout.WEST);

        JPanel hintContent = new JPanel(new BorderLayout());
        hintContent.setOpaque(false);
        JLabel hintTitle = new JLabel(UiStrings.text("panel.help"));
        hintTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        hintTitle.setForeground(new Color(0x92, 0x40, 0x0E));
        hintContent.add(hintTitle, BorderLayout.NORTH);

        JLabel hintText = new JLabel("<html><div style='font-size:12px;color:#64748B;line-height:1.5'>"
                + UiStrings.text("hint.teacher") + "</div></html>");
        hintText.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        hintText.setForeground(new Color(0x64, 0x74, 0x8B));
        hintContent.add(hintText, BorderLayout.CENTER);
        hintBox.add(hintContent, BorderLayout.CENTER);
        panel.add(hintBox, BorderLayout.SOUTH);

        // ── Navigation ──
        JPanel navRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navRow.setOpaque(false);
        navRow.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JButton prevBtn = productButton(UiStrings.text("action.previous"),
                new Color(0xF1, 0xF5, 0xF9), new Color(0x47, 0x55, 0x69));
        prevBtn.addActionListener(e -> {
            submitAndDispatch(UIEvent.teachingControl("prev"));
            updateHint();
        });
        navRow.add(prevBtn);

        JButton nextBtn = productButton(UiStrings.text("action.next"),
                theme.getPrimaryColor(), Color.WHITE);
        nextBtn.addActionListener(e -> {
            submitAndDispatch(UIEvent.teachingControl("next"));
            updateHint();
        });
        navRow.add(nextBtn);

        panel.add(navRow, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildStepRow(int num, String name, boolean active) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 36));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Number circle
        JPanel numCircle = new JPanel();
        numCircle.setOpaque(true);
        numCircle.setBackground(active ? theme.getPrimaryColor() : new Color(0xE2, 0xE8, 0xF0));
        numCircle.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        numCircle.setPreferredSize(new Dimension(26, 26));
        numCircle.setMinimumSize(new Dimension(26, 26));
        JLabel numLabel = new JLabel(String.valueOf(num));
        numLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        numLabel.setForeground(active ? Color.WHITE : new Color(0x64, 0x74, 0x8B));
        numCircle.add(numLabel);
        row.add(numCircle);

        // Step name
        JLabel stepLabel = new JLabel(name);
        stepLabel.setFont(new Font("Microsoft YaHei", active ? Font.BOLD : Font.PLAIN, 13));
        stepLabel.setForeground(active ? theme.getPrimaryColor() : new Color(0x47, 0x55, 0x69));
        row.add(stepLabel);

        // Indicator for active step
        if (active) {
            JLabel dot = new JLabel("●");
            dot.setForeground(theme.getSuccessColor());
            dot.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
            row.add(dot);
        }

        row.setCursor(new Cursor(Cursor.HAND_CURSOR));
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(new Color(0xF0, 0xF4, 0xFA));
                row.setOpaque(true);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(Color.WHITE);
                row.setOpaque(true);
            }
        });

        return row;
    }

    // ── Bottom toolbar ────────────────────────────────────────────────

    private JPanel buildBottomToolbar() {
        bottomToolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        bottomToolbar.setOpaque(true);
        bottomToolbar.setBackground(new Color(0xFA, 0xFB, 0xFF));
        bottomToolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getCanvasBorderColor()),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        bottomToolbar.setPreferredSize(new Dimension(0, UIStyle.BOTTOM_TOOLBAR_HEIGHT));

        JButton addGeometry = productButton("＋ 新增", theme.getPrimaryColor(), Color.WHITE);
        addGeometry.addActionListener(e -> showAddGeometryMenu(addGeometry));
        bottomToolbar.add(addGeometry);

        String[] tools = {"select", "move", "rotate", "scale", "cut", "unfold", "measure", "animation"};
        String[] labels = {
                UiStrings.text("tool.select"),
                UiStrings.text("tool.move"),
                UiStrings.text("tool.rotate"),
                UiStrings.text("tool.scale"),
                UiStrings.text("tool.cut"),
                UiStrings.text("tool.unfold"),
                UiStrings.text("tool.measure"),
                UiStrings.text("tool.animation")
        };
        String[] symbols = {"◎", "⇄", "↻", "⤡", "✂", "▤", "⌁", "▶"};

        for (int i = 0; i < tools.length; i++) {
            final int toolIdx = i;
            Color accent = theme.getToolAccentColor(i);
            RoundToolButton btn = new RoundToolButton(symbols[i], labels[i], accent);
            btn.addActionListener(e -> handleToolAction(tools[toolIdx], labels[toolIdx]));
            toolButtons.add(btn);
            bottomToolbar.add(btn);
        }

        // Hint label at the right
        toolHint = new JLabel(UiStrings.text("hint.select"));
        toolHint.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        toolHint.setForeground(new Color(0x94, 0xA3, 0xB8));
        toolHint.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        bottomToolbar.add(toolHint);

        return bottomToolbar;
    }

    private void handleToolAction(String action, String label) {
        if ("unfold".equals(action)) {
            submitAndDispatch(UIEvent.animationControl("unfold"));
        } else if ("animation".equals(action)) {
            submitAndDispatch(UIEvent.animationControl("play"));
        } else {
            switchTool(action);
        }
        if (contextToolRing != null) contextToolRing.setVisible(false);
        for (RoundToolButton btn : toolButtons) {
            btn.setActive(label.equals(btn.getText()));
        }
        updateHint();
    }

    private void showAddGeometryMenu(Component invoker) {
        JPopupMenu menu = new JPopupMenu();
        if (canvasView.getViewMode() == ViewMode.MODE_2D) {
            addGeometryItem(menu, "点", DrawAction.DrawType.POINT);
            addGeometryItem(menu, "线段", DrawAction.DrawType.LINE);
            addGeometryItem(menu, "矩形", DrawAction.DrawType.RECTANGLE);
            addGeometryItem(menu, "圆", DrawAction.DrawType.CIRCLE);
        } else {
            addGeometryItem(menu, "立方体", DrawAction.DrawType.CUBE);
            addGeometryItem(menu, "球体", DrawAction.DrawType.SPHERE);
            addGeometryItem(menu, "圆柱体", DrawAction.DrawType.CYLINDER);
            addGeometryItem(menu, "圆锥体", DrawAction.DrawType.CONE);
        }
        menu.show(invoker, 0, -menu.getPreferredSize().height);
    }

    private void addGeometryItem(JPopupMenu menu, String label, final DrawAction.DrawType type) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            switchTool("draw");
            canvasView.armDraw(type);
            toolHint.setText(canvasView.getViewMode() == ViewMode.MODE_2D
                    ? "拖拽白板以创建" + label : "在三维视图中单击以放置" + label);
        });
        menu.add(item);
    }

    // ── Floating toolbar ──────────────────────────────────────────────

    private void configureFloatingToolbar() {
        floatingToolBar.addToolButton(UiStrings.text("tool.select"), "select", e -> switchTool("select"));
        floatingToolBar.addToolButton(UiStrings.text("tool.move"), "move", e -> switchTool("move"));
        floatingToolBar.addToolButton(UiStrings.text("tool.rotate"), "rotate", e -> switchTool("rotate"));
        floatingToolBar.addToolButton(UiStrings.text("tool.cut"), "cut", e -> switchTool("cut"));
        floatingToolBar.addToolButton(UiStrings.text("tool.unfold"), "unfold", e -> {
            submitAndDispatch(UIEvent.animationControl("unfold"));
        });
        floatingToolBar.addToolButton(UiStrings.text("tool.animation"), "animation", e -> {
            submitAndDispatch(UIEvent.animationControl("play"));
        });
    }

    // ── Helper methods ────────────────────────────────────────────────

    private JButton productButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setMaximumSize(new Dimension(90, 34));
        return btn;
    }

    private String geometryName(SceneObject obj) {
        String type = obj.getGeometry().getClass().getSimpleName();
        switch (type) {
            case "Cube":        return UiStrings.text("obj.cube");
            case "Cylinder":    return UiStrings.text("obj.cylinder");
            case "Sphere":      return UiStrings.text("obj.sphere");
            case "Cone":        return UiStrings.text("obj.cone");
            case "Rectangle":   return UiStrings.text("obj.rectangle");
            case "Circle":      return UiStrings.text("obj.circle");
            default:            return type;
        }
    }

    private Color getObjectColor(SceneObject obj) {
        String type = obj.getGeometry().getClass().getSimpleName();
        switch (type) {
            case "Cube":        return new Color(0xEF, 0x44, 0x44);
            case "Cylinder":    return new Color(0xF5, 0x9E, 0x0B);
            case "Sphere":      return new Color(0x8B, 0x5C, 0xF6);
            case "Cone":        return new Color(0x06, 0xB6, 0xD4);
            case "Rectangle":   return new Color(0x3B, 0x82, 0xF6);
            case "Circle":      return new Color(0x10, 0xB9, 0x81);
            default:            return new Color(0x6B, 0x72, 0x80);
        }
    }

    private void updateHint() {
        if (commandController != null) {
            toolHint.setText(commandController.getLastFeedback());
        }
    }

    // ── Public API ────────────────────────────────────────────────────

    public void switchTool(String toolName) {
        String[] toolKeys = {"select", "move", "rotate", "scale", "cut", "unfold", "measure", "animation"};
        for (int i = 0; i < toolKeys.length; i++) {
            if (i < toolButtons.size()) {
                toolButtons.get(i).setActive(toolKeys[i].equals(toolName));
            }
        }
        // Dispatch tool switch directly without going through submitAndDispatch
        // (which would call switchTool again, causing infinite recursion)
        if (commandController != null) {
            // teaching/animation handlers
        }
        // Actually switch the tool via ToolManager
        if (toolManager != null) {
            try {
                toolManager.switchTool(toolName);
                if (twoDimensionalCanvas != null) twoDimensionalCanvas.setActiveTool(toolName);
                if (threeDimensionalCanvas != null) threeDimensionalCanvas.setActiveTool(toolName);
            } catch (IllegalArgumentException ignored) {
                // Unknown tool — just update the UI hint
            }
        }
        String hint = "当前工具：" + UiStrings.text("tool." + toolName)
                + "，请在几何白板中点击模型操作";
        toolHint.setText(hint);
    }

    public void setViewMode(ViewMode mode) {
        ViewMode targetMode = mode == null ? ViewMode.MODE_2D : mode;
        canvasView = targetMode == ViewMode.MODE_3D ? threeDimensionalCanvas : twoDimensionalCanvas;
        geometryModeManager.setMode(targetMode == ViewMode.MODE_2D
                ? GeometryMode.MODE_2D : GeometryMode.MODE_3D);
        geometryModeManager.applyTo(ToolBootstrapper.getToolContext(toolManager));
        if (viewPageLayout != null && viewPages != null) {
            viewPageLayout.show(viewPages, targetMode.name());
        }
        updateViewPageButtons(targetMode);
        refreshObjectList();
        if (toolHint != null) {
            toolHint.setText(targetMode == ViewMode.MODE_2D
                    ? "已切换到二维页面，仅显示平面图形"
                    : "已切换到三维页面，仅显示立体图形");
        }
        revalidate();
        repaint();
    }

    private void refreshObjectList() {
        if (leftSidebar == null || objectListContainer == null) {
            return;
        }
        int index = -1;
        Component[] components = leftSidebar.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == objectListContainer) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            leftSidebar.remove(index);
            objectListContainer = buildObjectListPanel(scene);
            leftSidebar.add(objectListContainer, index);
        }
    }

    public void setInputMode(InputMode mode) {
        inputModeManager.setMode(mode);
        applyProductMode();
    }

    public void setTeachingMode(UITeachingMode mode) {
        if (mode == null) return;
        teachingMode = mode;
        applyProductMode();
    }

    public void showWorkspace() {
        if (!welcomeShown && !GraphicsEnvironment.isHeadless()) {
            WelcomeDialog.Profile profile = WelcomeDialog.choose(this);
            setTeachingMode(profile.getTeachingMode());
            setInputMode(profile.getInputMode());
            welcomeShown = true;
        }
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    private void submitAndDispatch(UIEvent event) {
        if (commandController != null) {
            if (event.getType() == UIEvent.EventType.TEACHING_CONTROL) {
                commandController.handleTeachingControl((String) event.getData());
            } else if (event.getType() == UIEvent.EventType.ANIMATION_CONTROL) {
                commandController.handleAnimationControl((String) event.getData());
            } else if (event.getType() == UIEvent.EventType.TOOL_SWITCH) {
                switchTool((String) event.getData());
            }
        }
    }

    private void applyProductMode() {
        boolean desktop = inputModeManager.getMode() == InputMode.DESKTOP;
        boolean whiteboard = inputModeManager.getMode() == InputMode.WHITEBOARD;
        boolean tablet = inputModeManager.getMode() == InputMode.TABLET;

        // Supporting panels are intentionally on-demand in every mode.
        if (leftSidebar != null) leftSidebar.setVisible(false);
        if (rightPanel != null) rightPanel.setVisible(false);
        if (objectsButton != null) objectsButton.setVisible(true);
        if (lessonCard != null) lessonCard.setVisible(true);

        if (desktop) {
            floatingToolBar.hide();
        } else {
            floatingToolBar.showDefault();
        }

        int toolbarHeight = whiteboard ? 108 : (tablet ? 94 : 92);
        bottomToolbar.setPreferredSize(new Dimension(0, toolbarHeight));
        for (RoundToolButton btn : toolButtons) {
            int btnSize = whiteboard ? 104 : (tablet ? 90 : 86);
            btn.setPreferredSize(new Dimension(btnSize, toolbarHeight - 12));
        }
        int touchTolerance = inputModeManager.getTouchTolerance();
        if (twoDimensionalCanvas != null) twoDimensionalCanvas.setTouchTolerancePixels(touchTolerance);
        if (threeDimensionalCanvas != null) threeDimensionalCanvas.setTouchTolerancePixels(touchTolerance);

        // Update mode label
        String modeText = desktop ? UiStrings.text("input.desktop")
                : (whiteboard ? UiStrings.text("input.whiteboard")
                : UiStrings.text("input.tablet"));
        modeLabel.setText(whiteboard ? "✋ 白板触控" : (tablet ? "▣ 平板模式" : "🖱 键鼠操作"));

        layoutTeachingStage();
        revalidate();
        repaint();
    }

    private void showHelp() {
        String msg = "<html><div style='font-family:Microsoft YaHei;font-size:13px;line-height:1.7'>"
                + "<b>" + UiStrings.text("tool.help") + "</b><br><br>"
                + "1. 在左侧对象列表中点击模型，或在白板上直接点击<br>"
                + "2. 使用底部工具栏切换工具（选择、移动、旋转、切割…）<br>"
                + "3. 右侧教学步骤面板可切换课程进度<br>"
                + "4. 点击右上角「键鼠模式▼」可切换输入模式<br>"
                + "5. 滚轮缩放，拖拽平移画布<br>"
                + "</div></html>";
        JOptionPane.showMessageDialog(this, msg, UiStrings.text("tool.help"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Routes resolved canvas gestures through the active tool contract. */
    private CanvasCommandListener createCanvasCommandListener() {
        return new CanvasCommandListener() {
            @Override
            public void onSelectionChanged(SceneObject object) {
                SelectionManager selection = ToolBootstrapper.getSelectionManager(toolManager);
                if (selection != null) {
                    selection.clearSelection();
                    if (object != null) {
                        selection.select(object);
                    }
                }
                toolHint.setText(object == null
                        ? "请选择白板中的几何体"
                        : "已选择" + productGeometryName(object) + "，现在可使用下方工具");
                showContextTools(object);
            }

            @Override
            public void onMove(SceneObject object, float deltaX, float deltaY) {
                toolManager.dispatchAction(new MoveAction(object, deltaX, deltaY));
                toolHint.setText("正在移动" + productGeometryName(object));
            }

            @Override
            public void onRotate(SceneObject object, float angleDegrees) {
                toolManager.dispatchAction(new RotateAction(object, angleDegrees));
                toolHint.setText("正在旋转" + productGeometryName(object));
            }

            @Override
            public void onScale(SceneObject object, float scaleFactor) {
                toolManager.dispatchAction(new ScaleAction(object, scaleFactor));
                toolHint.setText("正在缩放" + productGeometryName(object));
            }

            @Override
            public void onCut(SceneObject object) {
                if (toolManager.getCurrentTool() instanceof CutTool) {
                    ((CutTool) toolManager.getCurrentTool()).executeCut(
                            object, new Vec3(0f, 1f, 0f), 0f);
                    toolHint.setText("已执行水平切割，可继续选择切割后的几何体");
                    canvasView.resetView();
                }
            }

            @Override
            public void onDraw(DrawAction action) {
                toolManager.dispatchAction(action);
                SceneObject created = scene.getSelected();
                if (created != null) {
                    onSelectionChanged(created);
                    refreshObjectList();
                    toolHint.setText("已新增" + productGeometryName(created));
                }
            }
        };
    }

    private void showContextTools(SceneObject object) {
        if (contextToolRing == null) return;
        if (object == null) {
            contextToolRing.setVisible(false);
            return;
        }
        Point point = canvasView.getSelectedScreenPoint();
        if (point == null) return;
        Point canvasOrigin = SwingUtilities.convertPoint(canvasView, 0, 0, teachingStage);
        int x = canvasOrigin.x + point.x - contextToolRing.getPreferredSize().width / 2;
        int y = canvasOrigin.y + point.y - 82;
        contextToolRing.setBounds(x, y, contextToolRing.getPreferredSize().width,
                contextToolRing.getPreferredSize().height);
        contextToolRing.setVisible(true);
        teachingStage.repaint();
    }

    private String productGeometryName(SceneObject object) {
        String type = object.getGeometry().getClass().getSimpleName();
        if ("Cube".equals(type)) return "正方体";
        if ("Cylinder".equals(type)) return "圆柱体";
        if ("Sphere".equals(type)) return "球体";
        if ("Cone".equals(type)) return "圆锥体";
        if ("Rectangle".equals(type)) return "矩形";
        if ("Circle".equals(type)) return "圆形";
        return "几何体";
    }

    // ── Accessors ─────────────────────────────────────────────────────

    public GeometryCanvasView getCanvasView() { return canvasView; }
    public GeometryCanvasView getTwoDimensionalCanvas() { return twoDimensionalCanvas; }
    public GeometryCanvasView getThreeDimensionalCanvas() { return threeDimensionalCanvas; }
    public InputModeManager getInputModeManager() { return inputModeManager; }
    public TeachingInteractionController getCommandController() { return commandController; }
    public UITeachingMode getTeachingMode() { return teachingMode; }
    public FloatingToolBar getFloatingToolBar() { return floatingToolBar; }
    public JPanel getBottomToolbar() { return bottomToolbar; }

    // ── Backward-compatibility accessors ──────────────────────────────
    // Kept for existing tests and demo code that reference the Phase 11-13 API.

    /**
     * Returns a minimal Workspace instance for backward compatibility.
     * This does NOT represent the actual Swing workspace layout.
     */
    @Deprecated
    public Workspace getWorkspace() {
        try {
            com.geometry.ui.bridge.UIEventBridge bridge =
                    new com.geometry.ui.bridge.UIEventBridge(null, scene, null);
            return new Workspace(
                    new com.geometry.ui.LayoutManager(
                            com.geometry.ui.UIInteractionMode.DESKTOP, 1024, 768),
                    bridge,
                    new com.geometry.ui.panel.SceneTreePanel(scene, bridge),
                    new com.geometry.ui.panel.PropertyPanel(),
                    new com.geometry.ui.panel.TeachingPanel(teachingManager, bridge),
                    new com.geometry.ui.panel.AnimationPanel(null, bridge));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the status bar for backward compatibility.
     */
    @Deprecated
    public LessonStatusBar getStatusBar() {
        return new LessonStatusBar(theme);
    }

    /**
     * Returns the bottom action bar for backward compatibility.
     */
    @Deprecated
    public BottomActionBar getBottomActionBar() {
        return new BottomActionBar(theme);
    }

    /**
     * Update the status bar text for backward compatibility.
     * In the product UI this updates the header lesson info label.
     */
    @Deprecated
    public void updateStatusBar(String lesson, String step) {
        // No-op in product UI — status is shown in header
    }
}
