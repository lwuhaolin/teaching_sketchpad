package com.geometry.ui.component;

import com.geometry.renderer.RenderMode;
import com.geometry.renderer.Renderer;
import com.geometry.scene.Scene;
import com.geometry.ui.ViewMode;
import com.geometry.ui.theme.EducationTheme;
import com.geometry.ui.theme.UIStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 13 - Geometry canvas view for Swing windows.
 *
 * This component is a JPanel that displays geometry in 2D mode
 * (using Swing Graphics2D).  In a full application it would
 * embed an OpenGL canvas; for this phase we provide a 2D
 * fallback that demonstrates the layout and interaction.
 *
 * Supports:
 *   - Drawing scene objects in 2D orthographic projection
 *   - Mouse interaction (click to select, drag to move)
 *   - Zoom via mouse wheel
 *   - Touch coordinate forwarding for whiteboard/tablet
 *
 * Not thread-safe.
 */
public class GeometryCanvasView extends JPanel {

    /** The scene to render. */
    private final Scene scene;

    /** The renderer for 3D mode (may be null in 2D-only tests). */
    private final Renderer renderer;

    /** The theme. */
    private final EducationTheme theme;

    /** View mode (2D or 3D). */
    private ViewMode viewMode;

    /** Zoom factor. */
    private double zoom;

    /** Pan offset (in scene coordinates). */
    private double panX;
    private double panY;

    /** Objects drawn during the last paint cycle (for hit testing). */
    private final List<DrawnObject> drawnObjects;

    /** Currently selected object index. */
    private int selectedIndex;

    /** Last mouse position for drag. */
    private int lastMouseX;
    private int lastMouseY;
    private boolean dragging;

    /**
     * Create a geometry canvas view.
     *
     * @param scene   the scene to display
     * @param renderer the OpenGL renderer (may be null)
     * @param theme   the education theme
     */
    public GeometryCanvasView(Scene scene, Renderer renderer, EducationTheme theme) {
        this.scene = scene;
        this.renderer = renderer;
        this.theme = theme;
        this.viewMode = ViewMode.MODE_2D;
        this.zoom = 1.0;
        this.panX = 0;
        this.panY = 0;
        this.drawnObjects = new ArrayList<>();
        this.selectedIndex = -1;
        this.dragging = false;

        setPreferredSize(new Dimension(800, 600));
        setBackground(theme.getCanvasDrawColor());
        setBorder(BorderFactory.createLineBorder(theme.getCanvasBorderColor(), 2));

        // Mouse handlers
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMouseX = e.getX();
                lastMouseY = e.getY();
                dragging = true;
                handleTap(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    int dx = e.getX() - lastMouseX;
                    int dy = e.getY() - lastMouseY;
                    panX -= dx / zoom;
                    panY += dy / zoom;
                    repaint();
                }
                lastMouseX = e.getX();
                lastMouseY = e.getY();
            }
        });

        // Zoom with mouse wheel
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = e.getUnitsToScroll() > 0 ? 1.1 : 1.0 / 1.1;
                zoom *= factor;
                zoom = Math.max(0.1, Math.min(zoom, 10.0));
                repaint();
            }
        });
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawnObjects.clear();

        // Apply transform
        int w = getWidth();
        int h = getHeight();
        g2.translate((int)(w / 2.0 + panX * zoom), (int)(h / 2.0 - panY * zoom));
        g2.scale(zoom, zoom);

        // Draw grid in 2D mode
        if (viewMode == ViewMode.MODE_2D) {
            drawGrid(g2, w, h);
        }

        // Draw scene objects
        if (scene != null) {
            int idx = 0;
            for (com.geometry.scene.SceneObject obj : scene.getAllObjects()) {
                drawObject(g2, obj, idx == selectedIndex);
                drawnObjects.add(new DrawnObject(obj, idx));
                idx++;
            }
        }
    }

    private void drawGrid(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(0xE8, 0xE8, 0xE8));
        g2.setStroke(new BasicStroke(1f));
        int gridSize = 50;
        int startX = -(int)(w / 2.0 / zoom) - (int) panX;
        int startY = -(int)(h / 2.0 / zoom) - (int) panY;
        for (int x = startX - (startX % gridSize); x < startX + (int)(w / zoom) + gridSize; x += gridSize) {
            g2.drawLine(x, startY, x, startY + (int)(h / zoom));
        }
        for (int y = startY - (startY % gridSize); y < startY + (int)(h / zoom) + gridSize; y += gridSize) {
            g2.drawLine(startX, y, startX + (int)(w / zoom), y);
        }
        // Axes
        g2.setColor(new Color(0x95, 0xA5, 0xA6));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(startX, 0, startX + (int)(w / zoom), 0);
        g2.drawLine(0, startY, 0, startY + (int)(h / zoom));
    }

    private void drawObject(Graphics2D g2, com.geometry.scene.SceneObject obj, boolean selected) {
        com.geometry.core.geometry.GeometryObject geo = obj.getGeometry();
        com.geometry.core.transform.Transform t = obj.getEffectiveTransform();
        com.geometry.core.math.Vec3 pos = t.getPosition();

        g2.setColor(obj.isVisible() ? getColour(geo.getClass().getSimpleName()) : new Color(0xCC, 0xCC, 0xCC));
        if (selected) {
            g2.setColor(theme.getSelectionColor());
        }

        if (geo instanceof com.geometry.core.geometry.Rectangle) {
            com.geometry.core.geometry.Rectangle rect =
                    (com.geometry.core.geometry.Rectangle) geo;
            int hw = (int)(rect.getWidth() / 2f);
            int hh = (int)(rect.getHeight() / 2f);
            g2.fillRect((int) pos.x - hw, (int) pos.y - hh, hw * 2, hh * 2);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect((int) pos.x - hw, (int) pos.y - hh, hw * 2, hh * 2);
        } else if (geo instanceof com.geometry.core.geometry.Circle) {
            com.geometry.core.geometry.Circle circle =
                    (com.geometry.core.geometry.Circle) geo;
            int r = (int) circle.getRadius();
            g2.fillOval((int) (pos.x - r), (int) (pos.y - r), r * 2, r * 2);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval((int) (pos.x - r), (int) (pos.y - r), r * 2, r * 2);
        } else if (geo instanceof com.geometry.core.geometry.Cube) {
            com.geometry.core.geometry.Cube cube =
                    (com.geometry.core.geometry.Cube) geo;
            int s = (int)(cube.getWidth() / 2f);
            g2.fillRect((int) pos.x - s, (int) pos.y - s, s * 2, s * 2);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRect((int) pos.x - s, (int) pos.y - s, s * 2, s * 2);
        } else {
            // Default: draw a small circle
            g2.fillOval((int) (pos.x - 10), (int) (pos.y - 10), 20, 20);
        }

        // Draw label
        g2.setColor(theme.getTextColour());
        g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        g2.drawString(obj.getId(), (int) pos.x + 5, (int) pos.y - 5);
    }

    private Color getColour(String typeName) {
        switch (typeName) {
            case "Rectangle": return new Color(0x34, 0x98, 0xDB);
            case "Circle":    return new Color(0x2E, 0xCC, 0x71);
            case "Cube":      return new Color(0xE7, 0x4C, 0x3C);
            case "Sphere":    return new Color(0x9B, 0x59, 0xB6);
            case "Cylinder":  return new Color(0xF3, 0x9C, 0x12);
            case "Cone":      return new Color(0x1A, 0xBC, 0x9C);
            case "Polygon":   return new Color(0x7F, 0x8C, 0x8D);
            default:          return new Color(0x34, 0x98, 0xDB);
        }
    }

    // ------------------------------------------------------------------
    // Interaction
    // ------------------------------------------------------------------

    public void handleTap(int screenX, int screenY) {
        // Convert screen to scene coords
        int w = getWidth();
        int h = getHeight();
        double sceneX = (screenX - w / 2.0 - panX * zoom) / zoom;
        double sceneY = (h / 2.0 - screenY - panY * zoom) / zoom;

        // Find closest object
        int bestIdx = -1;
        double bestDist = Double.MAX_VALUE;
        int idx = 0;
        if (scene != null) {
            for (com.geometry.scene.SceneObject obj : scene.getAllObjects()) {
                com.geometry.core.math.Vec3 pos = obj.getEffectiveTransform().getPosition();
                double dx = sceneX - pos.x;
                double dy = sceneY - pos.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < bestDist) {
                    bestDist = dist;
                    bestIdx = idx;
                }
                idx++;
            }
        }
        if (bestIdx >= 0 && bestDist < 100.0) {
            selectedIndex = bestIdx;
            if (scene != null) {
                List<com.geometry.scene.SceneObject> objs = scene.getAllObjects();
                if (bestIdx < objs.size()) {
                    scene.select(objs.get(bestIdx));
                }
            }
        } else {
            selectedIndex = -1;
            if (scene != null) {
                scene.clearSelection();
            }
        }
        repaint();
    }

    // ------------------------------------------------------------------
    // Touch forwarding (for whiteboard/tablet)
    // ------------------------------------------------------------------

    /**
     * Forward a touch event to the canvas (for whiteboard mode).
     *
     * @param x screen x
     * @param y screen y
     */
    public void onTouchDown(int x, int y) {
        SwingUtilities.invokeLater(() -> {
            handleTap(x, y);
        });
    }

    /**
     * Forward a pen event to the canvas.
     *
     * @param x        screen x
     * @param y        screen y
     * @param pressure pen pressure [0, 1]
     */
    public void onPenDown(int x, int y, float pressure) {
        onTouchDown(x, y);
    }

    // ------------------------------------------------------------------
    // Mode control
    // ------------------------------------------------------------------

    /**
     * Set the view mode.
     *
     * @param mode the new view mode
     */
    public void setViewMode(ViewMode mode) {
        this.viewMode = mode;
        repaint();
    }

    /**
     * Get the current view mode.
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Reset zoom and pan.
     */
    public void resetView() {
        zoom = 1.0;
        panX = 0;
        panY = 0;
        repaint();
    }

    /**
     * Get the selected object index.
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    // ------------------------------------------------------------------
    // Inner class
    // ------------------------------------------------------------------

    private static class DrawnObject {
        final com.geometry.scene.SceneObject object;
        final int index;

        DrawnObject(com.geometry.scene.SceneObject object, int index) {
            this.object = object;
            this.index = index;
        }
    }
}
