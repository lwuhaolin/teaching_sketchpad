package com.geometry.ui.workspace;

import com.geometry.animation.AnimationManager;
import com.geometry.interaction.InteractionManager;
import com.geometry.scene.Scene;
import com.geometry.teaching.TeachingManager;
import com.geometry.tools.ToolManager;
import com.geometry.ui.UIEvent;
import com.geometry.ui.UIInteractionMode;
import com.geometry.ui.ViewMode;
import com.geometry.ui.Workspace;
import com.geometry.ui.bridge.UIEventBridge;
import com.geometry.ui.canvas.CanvasInteractionLayer;
import com.geometry.ui.component.BottomActionBar;
import com.geometry.ui.component.FloatingToolBar;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.component.LessonStatusBar;
import com.geometry.ui.input.InputMode;
import com.geometry.ui.input.InputModeManager;
import com.geometry.ui.panel.AnimationPanel;
import com.geometry.ui.panel.PropertyPanel;
import com.geometry.ui.panel.SceneTreePanel;
import com.geometry.ui.panel.TeachingPanel;
import com.geometry.ui.theme.EducationTheme;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Phase 13 - Real Swing teaching workspace.
 *
 * Assembles all UI components into a single window that can be
 * shown on screen.  Supports three input modes (Desktop, Whiteboard, Tablet).
 *
 * Architecture:
 *   TeachingWorkspace (Swing JFrame)
 *     ├── LessonStatusBar
 *     ├── Left panel: SceneTree (JTable) + Property (JTextArea)
 *     ├── Center: GeometryCanvasView (Swing 2D canvas)
 *     ├── Right panel: TeachingPanel + AnimationPanel
 *     ├── FloatingToolBar (whiteboard/tablet mode)
 *     └── BottomActionBar
 *
 * Not thread-safe.
 */
public class TeachingWorkspace extends JFrame {

    /** The workspace model. */
    private final Workspace workspace;

    /** The input mode manager. */
    private final InputModeManager inputModeManager;

    /** The education theme. */
    private final EducationTheme theme;

    /** Main canvas view. */
    private GeometryCanvasView canvasView;

    /** Floating toolbar (whiteboard mode). */
    private FloatingToolBar floatingToolBar;

    /** Bottom action bar. */
    private BottomActionBar bottomActionBar;

    /** Lesson status bar. */
    private LessonStatusBar statusBar;

    /** Right-side panel container. */
    private JSplitPane rightSplitPane;

    /**
     * Create a TeachingWorkspace with all dependencies.
     */
    public TeachingWorkspace(
            Scene scene,
            ToolManager toolManager,
            InteractionManager interactionManager,
            TeachingManager teachingManager,
            AnimationManager animationManager) {
        this(scene, toolManager, interactionManager, teachingManager, animationManager,
                new EducationTheme());
    }

    /**
     * Create a TeachingWorkspace with a custom theme.
     */
    public TeachingWorkspace(
            Scene scene,
            ToolManager toolManager,
            InteractionManager interactionManager,
            TeachingManager teachingManager,
            AnimationManager animationManager,
            EducationTheme theme) {
        this.theme = theme;
        this.workspace = createWorkspace(scene, toolManager, interactionManager,
                teachingManager, animationManager);
        this.inputModeManager = new InputModeManager(
                workspace.getEventBridge(), workspace.getLayoutManager());

        // Configure window
        setTitle("Geometry Teaching Engine");
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Build content
        buildContent(scene, interactionManager);
    }

    // ------------------------------------------------------------------
    // Workspace creation
    // ------------------------------------------------------------------

    private Workspace createWorkspace(
            Scene scene, ToolManager toolManager,
            InteractionManager interactionManager,
            TeachingManager teachingManager,
            AnimationManager animationManager) {
        UIEventBridge bridge = new UIEventBridge(toolManager, scene, interactionManager);
        com.geometry.ui.LayoutManager lm = new com.geometry.ui.LayoutManager(
                UIInteractionMode.DESKTOP, 1024, 768);
        SceneTreePanel stp = new SceneTreePanel(scene, bridge);
        PropertyPanel pp = new PropertyPanel();
        TeachingPanel tp = teachingManager != null
                ? new TeachingPanel(teachingManager, bridge) : new TeachingPanel(null, bridge);
        AnimationPanel ap = animationManager != null
                ? new AnimationPanel(animationManager, bridge) : new AnimationPanel(null, bridge);

        Workspace ws = new Workspace(lm, bridge, stp, pp, tp, ap);
        if (scene != null && interactionManager != null) {
            ws.setCanvasInteractionLayer(new CanvasInteractionLayer(scene, interactionManager));
        }
        return ws;
    }

    // ------------------------------------------------------------------
    // Content building
    // ------------------------------------------------------------------

    private void buildContent(Scene scene, InteractionManager interactionManager) {
        // Canvas
        this.canvasView = new GeometryCanvasView(scene, null, theme);
        ViewMode initialMode = workspace.getViewMode();
        if (initialMode != null) {
            this.canvasView.setViewMode(initialMode);
        }

        // Status bar
        this.statusBar = new LessonStatusBar(theme);

        // Floating toolbar
        this.floatingToolBar = new FloatingToolBar(theme);
        setupFloatingToolbar();

        // Bottom action bar
        this.bottomActionBar = new BottomActionBar(theme);
        setupBottomActionBar();

        // Right panel: Teaching + Animation stacked
        this.rightSplitPane = buildRightPanel();
        rightSplitPane.setVisible(workspace.getLayoutManager().getMode()
                != UIInteractionMode.WHITEBOARD);

        // Layout
        setLayout(new BorderLayout());

        // Top: status bar
        add(statusBar, BorderLayout.NORTH);

        // Center: left panel + canvas, and right panels
        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), canvasView);
        centerSplit.setResizeWeight(0.25);
        centerSplit.setDividerLocation(0.25);

        JSplitPane rightContainer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                centerSplit, rightSplitPane);
        rightContainer.setResizeWeight(0.85);
        add(rightContainer, BorderLayout.CENTER);

        // Bottom: action bar
        add(bottomActionBar, BorderLayout.SOUTH);

        // Floating toolbar lives above everything in layered pane
        // Don't call setVisible(false) here — it triggers Swing property change recursion.
        // Use visibleNow flag instead.
        getLayeredPane().add(floatingToolBar, JLayeredPane.PALETTE_LAYER);

        // Set size
        pack();
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }

    private JPanel buildLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(theme.getPanelBackgroundColor());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Scene tree as a JTable
        JScrollPane treeScroll = new JScrollPane(buildSceneTreeTable());
        treeScroll.setPreferredSize(new Dimension(220, 400));
        leftPanel.add(treeScroll, BorderLayout.CENTER);

        // Property display
        leftPanel.add(buildPropertyPanel(), BorderLayout.SOUTH);

        return leftPanel;
    }

    private JTable buildSceneTreeTable() {
        String[] columns = {"ID", "Type"};
        SceneTreePanel treePanel = workspace.getSceneTreePanel();
        int count = treePanel.getObjectCount();
        Object[][] data = new Object[count][];
        for (int i = 0; i < count; i++) {
            String label = treePanel.getLabelText(i);
            if (label != null && label.contains("[")) {
                String id = label.substring(0, label.indexOf(' '));
                String type = label.substring(label.indexOf('[') + 1, label.indexOf(']'));
                data[i] = new Object[]{id, type};
            } else {
                data[i] = new Object[]{label != null ? label : "", ""};
            }
        }

        JTable table = new JTable(data, columns);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setFont(theme.getLabelFont(InputMode.DESKTOP));
        table.setBackground(theme.getPanelBackgroundColor());
        table.setGridColor(theme.getCanvasBorderColor());

        // Click to select
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    workspace.getSceneTreePanel().selectByIndex(row);
                }
            }
        });

        return table;
    }

    private JPanel buildPropertyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(theme.getPanelBackgroundColor());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getCanvasBorderColor()),
                "Properties"));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setBackground(theme.getPanelBackgroundColor());
        textArea.setForeground(theme.getTextColour());
        updatePropertyDisplay(textArea);

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private void updatePropertyDisplay(JTextArea textArea) {
        PropertyPanel propPanel = workspace.getPropertyPanel();
        if (propPanel.getPropertyRowCount() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < propPanel.getPropertyRowCount(); i++) {
                sb.append(propPanel.getPropertyLabel(i)).append("\n");
            }
            textArea.setText(sb.toString());
        } else {
            textArea.setText("No object selected");
        }
    }

    private JSplitPane buildRightPanel() {
        TeachingPanel tp = workspace.getTeachingPanel();
        AnimationPanel ap = workspace.getAnimationPanel();

        // Teaching panel as a Swing component
        JPanel teachingComp = new JPanel(new BorderLayout());
        teachingComp.setBackground(theme.getPanelBackgroundColor());
        teachingComp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getCanvasBorderColor()),
                "Teaching"));

        JTextArea teachingInfo = new JTextArea();
        teachingInfo.setEditable(false);
        teachingInfo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        teachingInfo.setBackground(theme.getPanelBackgroundColor());
        teachingInfo.setForeground(theme.getTextColour());
        updateTeachingDisplay(teachingInfo, tp);

        // Teaching controls
        JPanel teachingControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton prevBtn = new JButton("Prev");
        JButton playBtn = new JButton(tp != null && tp.isRunning() ? "Stop" : "Start");
        JButton nextBtn = new JButton("Next");
        prevBtn.setFont(theme.getButtonFont(InputMode.DESKTOP));
        playBtn.setFont(theme.getButtonFont(InputMode.DESKTOP));
        nextBtn.setFont(theme.getButtonFont(InputMode.DESKTOP));
        prevBtn.addActionListener(e -> {
            if (tp != null) tp.triggerControl(0);
            updateTeachingDisplay(teachingInfo, tp);
            playBtn.setText(tp != null && tp.isRunning() ? "Stop" : "Start");
        });
        playBtn.addActionListener(e -> {
            if (tp != null) tp.triggerControl(1);
            updateTeachingDisplay(teachingInfo, tp);
            playBtn.setText(tp != null && tp.isRunning() ? "Stop" : "Start");
        });
        nextBtn.addActionListener(e -> {
            if (tp != null) tp.triggerControl(2);
            updateTeachingDisplay(teachingInfo, tp);
        });
        teachingControls.add(prevBtn);
        teachingControls.add(playBtn);
        teachingControls.add(nextBtn);

        teachingComp.add(new JScrollPane(teachingInfo), BorderLayout.CENTER);
        teachingComp.add(teachingControls, BorderLayout.SOUTH);

        // Animation panel as a Swing component
        JPanel animComp = new JPanel(new BorderLayout());
        animComp.setBackground(theme.getPanelBackgroundColor());
        animComp.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(theme.getCanvasBorderColor()),
                "Animation"));

        JTextArea animInfo = new JTextArea();
        animInfo.setEditable(false);
        animInfo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        animInfo.setBackground(theme.getPanelBackgroundColor());
        animInfo.setForeground(theme.getTextColour());
        updateAnimationDisplay(animInfo, ap);

        JPanel animControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        JButton playAnimBtn = new JButton(ap != null && ap.isPlaying() ? "Pause" : "Play");
        JButton stopAnimBtn = new JButton("Stop");
        playAnimBtn.setFont(theme.getButtonFont(InputMode.DESKTOP));
        stopAnimBtn.setFont(theme.getButtonFont(InputMode.DESKTOP));
        playAnimBtn.addActionListener(e -> {
            if (ap != null) ap.triggerControl(0);
            updateAnimationDisplay(animInfo, ap);
            playAnimBtn.setText(ap != null && ap.isPlaying() ? "Pause" : "Play");
        });
        stopAnimBtn.addActionListener(e -> {
            if (ap != null) ap.triggerControl(1);
            updateAnimationDisplay(animInfo, ap);
            playAnimBtn.setText("Play");
        });
        animControls.add(playAnimBtn);
        animControls.add(stopAnimBtn);

        animComp.add(new JScrollPane(animInfo), BorderLayout.CENTER);
        animComp.add(animControls, BorderLayout.SOUTH);

        return new JSplitPane(JSplitPane.VERTICAL_SPLIT, teachingComp, animComp);
    }

    private void updateTeachingDisplay(JTextArea textArea, TeachingPanel panel) {
        if (panel == null) {
            textArea.setText("No teaching manager");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Lesson: ").append(panel.getLessonName()).append("\n");
        sb.append("Step: ").append(panel.getStepDisplay()).append("\n");
        sb.append("Running: ").append(panel.isLessonActive()).append("\n");
        textArea.setText(sb.toString());
    }

    private void updateAnimationDisplay(JTextArea textArea, AnimationPanel panel) {
        if (panel == null) {
            textArea.setText("No animation manager");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Time: ").append(panel.getTimeDisplay()).append("\n");
        sb.append("Playing: ").append(panel.isPlaying()).append("\n");
        sb.append("Has animation: ").append(panel.hasAnimation()).append("\n");
        textArea.setText(sb.toString());
    }

    // ------------------------------------------------------------------
    // Toolbar setup
    // ------------------------------------------------------------------

    private void setupFloatingToolbar() {
        floatingToolBar.addToolButton("Select", "select", e -> switchTool("select"));
        floatingToolBar.addToolButton("Move", "move", e -> switchTool("move"));
        floatingToolBar.addToolButton("Rotate", "rotate", e -> switchTool("rotate"));
        floatingToolBar.addToolButton("Measure", "measure", e -> switchTool("measure"));
        floatingToolBar.addToolButton("Cut", "cut", e -> switchTool("cut"));
        floatingToolBar.addToolButton("Unfold", "unfold", e -> {});
    }

    private void setupBottomActionBar() {
        bottomActionBar.addButton("2D", "mode_2d", e -> setViewMode(ViewMode.MODE_2D));
        bottomActionBar.addButton("3D", "mode_3d", e -> setViewMode(ViewMode.MODE_3D));
        bottomActionBar.addButton("Desktop", "mode_desktop", e -> setInputMode(InputMode.DESKTOP));
        bottomActionBar.addButton("Whiteboard", "mode_whiteboard", e -> setInputMode(InputMode.WHITEBOARD));
        bottomActionBar.addButton("Tablet", "mode_tablet", e -> setInputMode(InputMode.TABLET));
        bottomActionBar.addButton("Reset", "reset", e -> {
            canvasView.resetView();
            floatingToolBar.hide();
        });
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Get the workspace model.
     */
    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Get the input mode manager.
     */
    public InputModeManager getInputModeManager() {
        return inputModeManager;
    }

    /**
     * Get the geometry canvas view.
     */
    public GeometryCanvasView getCanvasView() {
        return canvasView;
    }

    /**
     * Get the floating toolbar.
     */
    public FloatingToolBar getFloatingToolBar() {
        return floatingToolBar;
    }

    /**
     * Get the lesson status bar.
     */
    public LessonStatusBar getStatusBar() {
        return statusBar;
    }

    /**
     * Get the bottom action bar.
     */
    public BottomActionBar getBottomActionBar() {
        return bottomActionBar;
    }

    /**
     * Switch to a tool by name.
     */
    public void switchTool(String toolName) {
        workspace.getEventBridge().submit(UIEvent.toolSwitch(toolName));
        workspace.dispatchEvents();
    }

    /**
     * Set the view mode (2D / 3D).
     */
    public void setViewMode(ViewMode mode) {
        workspace.setViewMode(mode);
        canvasView.setViewMode(mode);
        statusBar.setModeDisplay(mode == ViewMode.MODE_2D ? "2D" : "3D");
    }

    /**
     * Set the input mode (Desktop / Whiteboard / Tablet).
     */
    public void setInputMode(InputMode mode) {
        inputModeManager.setMode(mode);
        // Update layout
        UIInteractionMode uiMode = InputModeManager.toUIInteractionMode(mode);
        workspace.getLayoutManager().setMode(uiMode);
        // Show/hide panels
        boolean isWhiteboard = mode == InputMode.WHITEBOARD || mode == InputMode.TABLET;
        rightSplitPane.setVisible(!isWhiteboard);
        // Show floating toolbar in whiteboard/tablet
        if (isWhiteboard) {
            floatingToolBar.showDefault();
        } else {
            floatingToolBar.hide();
        }
        statusBar.setModeDisplay(mode.name());
    }

    /**
     * Show the window.
     */
    public void showWorkspace() {
        setVisible(true);
    }

    /**
     * Update the status bar with lesson info.
     */
    public void updateStatusBar(String lessonName, String stepDisplay) {
        statusBar.setLessonName(lessonName);
        statusBar.setStepDisplay(stepDisplay);
    }
}
