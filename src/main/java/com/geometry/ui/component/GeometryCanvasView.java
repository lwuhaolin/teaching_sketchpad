package com.geometry.ui.component;

import com.geometry.core.geometry.Circle;
import com.geometry.core.geometry.Cone;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.geometry.Polygon;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.geometry.Sphere;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.renderer.Renderer;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.ui.ViewMode;
import com.geometry.ui.canvas.CanvasCommandListener;
import com.geometry.ui.theme.EducationTheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * A scaled, selectable teaching canvas. World-space geometry is rendered at a
 * fixed classroom-friendly scale so engine units never collapse into pixels.
 */
public class GeometryCanvasView extends JPanel {

    private static final double PIXELS_PER_UNIT = 92.0;
    private static final double MIN_ZOOM = 0.35;
    private static final double MAX_ZOOM = 4.0;

    private final Scene scene;
    @SuppressWarnings("unused")
    private final Renderer renderer;
    private final EducationTheme theme;
    private ViewMode viewMode;
    private double zoom;
    private double panX;
    private double panY;
    private int selectedIndex;
    private String activeTool;
    private CanvasCommandListener commandListener;
    private int lastX;
    private int lastY;
    private boolean dragging;

    public GeometryCanvasView(Scene scene, Renderer renderer, EducationTheme theme) {
        this.scene = scene;
        this.renderer = renderer;
        this.theme = theme;
        this.viewMode = ViewMode.MODE_2D;
        this.zoom = 1.0;
        this.selectedIndex = -1;
        this.activeTool = "select";
        setPreferredSize(new Dimension(900, 620));
        setBackground(theme.getCanvasBackgroundColor());
        installInputHandlers();
    }

    public void setCommandListener(CanvasCommandListener commandListener) {
        this.commandListener = commandListener;
    }

    public void setActiveTool(String activeTool) {
        this.activeTool = activeTool == null ? "select" : activeTool;
        setCursor("select".equals(this.activeTool)
                ? java.awt.Cursor.getDefaultCursor()
                : java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    private void installInputHandlers() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                lastX = event.getX();
                lastY = event.getY();
                dragging = true;
                selectAt(lastX, lastY);
                if ("cut".equals(activeTool) && getSelectedObject() != null && commandListener != null) {
                    commandListener.onCut(getSelectedObject());
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                dragging = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (!dragging) {
                    return;
                }
                int dx = event.getX() - lastX;
                int dy = event.getY() - lastY;
                SceneObject selected = getSelectedObject();
                if (selected != null && commandListener != null) {
                    if ("move".equals(activeTool)) {
                        commandListener.onMove(selected, toWorld(dx), -toWorld(dy));
                    } else if ("rotate".equals(activeTool)) {
                        commandListener.onRotate(selected, dx * 0.75f);
                    } else if ("scale".equals(activeTool)) {
                        commandListener.onScale(selected, clampScale(1f + (dx - dy) * 0.012f));
                    } else if ("select".equals(activeTool)) {
                        panX += dx;
                        panY += dy;
                    }
                } else {
                    panX += dx;
                    panY += dy;
                }
                lastX = event.getX();
                lastY = event.getY();
                repaint();
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent event) {
                double factor = event.getWheelRotation() > 0 ? 0.88 : 1.14;
                zoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        paintBackground(g2);
        drawGrid(g2);
        g2.translate(getWidth() / 2.0 + panX, getHeight() / 2.0 + panY);
        g2.scale(PIXELS_PER_UNIT * zoom, -PIXELS_PER_UNIT * zoom);
        if (scene != null) {
            List<SceneObject> objects = scene.getAllObjects();
            for (int i = 0; i < objects.size(); i++) {
                SceneObject object = objects.get(i);
                if (isObjectInCurrentView(object)) {
                    drawObject(g2, object, i == selectedIndex || object.isSelected());
                }
            }
        }
        g2.dispose();
        paintModeBadge((Graphics2D) graphics);
    }

    private void paintBackground(Graphics2D g2) {
        Color top = viewMode == ViewMode.MODE_3D ? new Color(0xF6, 0xF9, 0xFE) : new Color(0xFB, 0xFC, 0xFE);
        Color bottom = viewMode == ViewMode.MODE_3D ? new Color(0xEE, 0xF3, 0xFA) : new Color(0xF5, 0xF7, 0xFA);
        g2.setPaint(new GradientPaint(0, 0, top, 0, getHeight(), bottom));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawGrid(Graphics2D g2) {
        int spacing = Math.max(24, (int) Math.round(PIXELS_PER_UNIT * zoom));
        g2.setColor(new Color(0xE8, 0xED, 0xF3));
        g2.setStroke(new BasicStroke(1f));
        for (int x = (int) ((getWidth() / 2.0 + panX) % spacing); x < getWidth(); x += spacing) {
            g2.drawLine(x, 0, x, getHeight());
        }
        for (int y = (int) ((getHeight() / 2.0 + panY) % spacing); y < getHeight(); y += spacing) {
            g2.drawLine(0, y, getWidth(), y);
        }
        int cx = (int) (getWidth() / 2.0 + panX);
        int cy = (int) (getHeight() / 2.0 + panY);
        g2.setColor(new Color(0xC7, 0xD2, 0xE0));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, cy, getWidth(), cy);
        g2.drawLine(cx, 0, cx, getHeight());
    }

    private void paintModeBadge(Graphics2D g2) {
        g2.setColor(new Color(0xFF, 0xFF, 0xFF, 220));
        g2.fillRoundRect(16, 16, 96, 31, 12, 12);
        g2.setColor(new Color(0x2D, 0x6D, 0xC7));
        g2.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        g2.drawString(viewMode == ViewMode.MODE_3D ? "三维观察" : "二维白板", 29, 37);
        if (getVisibleObjectCount() == 0) {
            g2.setColor(new Color(0x94, 0xA3, 0xB8));
            g2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
            String hint = "拖动几何体试试看";
            int width = g2.getFontMetrics().stringWidth(hint);
            g2.drawString(hint, (getWidth() - width) / 2, getHeight() / 2);
        }
    }

    private void drawObject(Graphics2D g2, SceneObject object, boolean selected) {
        if (!object.isVisible()) {
            return;
        }
        Transform transform = object.getEffectiveTransform();
        Vec3 position = transform.getPosition();
        Vec3 scale = transform.getScale();
        Graphics2D objectGraphics = (Graphics2D) g2.create();
        objectGraphics.translate(position.x, position.y);
        objectGraphics.rotate(Math.toRadians(transform.getRotation().z));
        objectGraphics.scale(scale.x, scale.y);
        GeometryObject geometry = object.getGeometry();
        if (geometry instanceof Rectangle) {
            drawRectangle(objectGraphics, (Rectangle) geometry);
        } else if (geometry instanceof Circle) {
            drawCircle(objectGraphics, (Circle) geometry);
        } else if (geometry instanceof Cube) {
            drawCube(objectGraphics, (Cube) geometry);
        } else if (geometry instanceof Cylinder) {
            drawCylinder(objectGraphics, (Cylinder) geometry);
        } else if (geometry instanceof Sphere) {
            drawSphere(objectGraphics, (Sphere) geometry);
        } else if (geometry instanceof Cone) {
            drawCone(objectGraphics, (Cone) geometry);
        } else {
            drawFallback(objectGraphics);
        }
        if (selected) {
            drawSelectionRing(objectGraphics, geometry);
        }
        objectGraphics.scale(1 / (scale.x == 0 ? 1 : scale.x), 1 / (scale.y == 0 ? 1 : scale.y));
        objectGraphics.rotate(-Math.toRadians(transform.getRotation().z));
        objectGraphics.scale(1 / (PIXELS_PER_UNIT * zoom), -1 / (PIXELS_PER_UNIT * zoom));
        objectGraphics.setColor(new Color(0x2D, 0x45, 0x68));
        objectGraphics.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        objectGraphics.drawString(geometryName(geometry), 8, -10);
        objectGraphics.dispose();
    }

    private void drawRectangle(Graphics2D g2, Rectangle rectangle) {
        double width = rectangle.getWidth();
        double height = rectangle.getHeight();
        g2.setColor(new Color(0x7E, 0xB8, 0xFF));
        g2.fill(new java.awt.geom.Rectangle2D.Double(-width / 2, -height / 2, width, height));
        g2.setColor(new Color(0x1F, 0x6F, 0xDD));
        g2.setStroke(new BasicStroke(0.035f));
        g2.draw(new java.awt.geom.Rectangle2D.Double(-width / 2, -height / 2, width, height));
    }

    private void drawCircle(Graphics2D g2, Circle circle) {
        double radius = circle.getRadius();
        g2.setColor(new Color(0x80, 0xD8, 0xB0));
        g2.fill(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
        g2.setColor(new Color(0x17, 0x9A, 0x65));
        g2.setStroke(new BasicStroke(0.035f));
        g2.draw(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
    }

    private void drawCube(Graphics2D g2, Cube cube) {
        double side = cube.getWidth();
        if (viewMode == ViewMode.MODE_2D) {
            g2.setColor(new Color(0x94, 0xB8, 0xFF));
            g2.fill(new java.awt.geom.Rectangle2D.Double(-side / 2, -side / 2, side, side));
            g2.setColor(new Color(0x35, 0x6E, 0xC7));
            g2.setStroke(new BasicStroke(0.04f));
            g2.draw(new java.awt.geom.Rectangle2D.Double(-side / 2, -side / 2, side, side));
            return;
        }
        double half = side / 2;
        java.awt.Polygon top = polygon(new double[]{-half, 0, half, 0}, new double[]{half, half + .34 * side, half, half - .34 * side});
        java.awt.Polygon right = polygon(new double[]{half, half + .34 * side, half + .34 * side, half}, new double[]{half, half - .34 * side, -half - .34 * side, -half});
        g2.setColor(new Color(0x67, 0x92, 0xE8));
        g2.fill(top);
        g2.setColor(new Color(0x48, 0x73, 0xC9));
        g2.fill(right);
        g2.setColor(new Color(0xA7, 0xC4, 0xFF));
        g2.fill(new java.awt.geom.Rectangle2D.Double(-half, -half, side, side));
        g2.setColor(new Color(0x2D, 0x59, 0xA4));
        g2.setStroke(new BasicStroke(0.04f));
        g2.draw(top); g2.draw(right); g2.draw(new java.awt.geom.Rectangle2D.Double(-half, -half, side, side));
    }

    private void drawCylinder(Graphics2D g2, Cylinder cylinder) {
        double radius = cylinder.getRadius();
        double height = cylinder.getHeight();
        double y = height / 2;
        g2.setColor(new Color(0x89, 0xA9, 0xF5));
        g2.fill(new java.awt.geom.Rectangle2D.Double(-radius, -y, radius * 2, height));
        g2.setColor(new Color(0x64, 0x88, 0xDE));
        g2.fill(new Ellipse2D.Double(-radius, -y - radius * .28, radius * 2, radius * .56));
        if (viewMode == ViewMode.MODE_3D) {
            g2.setColor(new Color(0x4E, 0x72, 0xC5));
            g2.draw(new Ellipse2D.Double(-radius, y - radius * .28, radius * 2, radius * .56));
        }
        g2.setColor(new Color(0x2F, 0x5F, 0xBB));
        g2.setStroke(new BasicStroke(0.035f));
        g2.draw(new java.awt.geom.Rectangle2D.Double(-radius, -y, radius * 2, height));
        g2.draw(new Ellipse2D.Double(-radius, -y - radius * .28, radius * 2, radius * .56));
    }

    private void drawSphere(Graphics2D g2, Sphere sphere) {
        double radius = sphere.getRadius();
        g2.setPaint(new GradientPaint((float) -radius, (float) -radius, new Color(0xE0, 0xBC, 0xFF),
                (float) radius, (float) radius, new Color(0x7B, 0x55, 0xCB)));
        g2.fill(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
        g2.setColor(new Color(0x65, 0x46, 0xAD));
        g2.setStroke(new BasicStroke(0.035f));
        g2.draw(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
    }

    private void drawCone(Graphics2D g2, Cone cone) {
        double radius = cone.getRadius();
        double height = cone.getHeight();
        java.awt.Polygon shape = polygon(new double[]{0, -radius, radius}, new double[]{-height / 2, height / 2, height / 2});
        g2.setColor(new Color(0xFF, 0xC0, 0x7D));
        g2.fill(shape);
        g2.setColor(new Color(0xD9, 0x78, 0x22));
        g2.setStroke(new BasicStroke(0.035f));
        g2.draw(shape);
        g2.draw(new Ellipse2D.Double(-radius, height / 2 - radius * .18, radius * 2, radius * .36));
    }

    private void drawFallback(Graphics2D g2) {
        g2.setColor(new Color(0x77, 0xA7, 0xDA));
        g2.fill(new Ellipse2D.Double(-.4, -.4, .8, .8));
    }

    private void drawSelectionRing(Graphics2D g2, GeometryObject geometry) {
        double radius = hitRadius(geometry) + .22;
        g2.setColor(new Color(0xFF, 0x97, 0x2C));
        g2.setStroke(new BasicStroke(0.045f));
        g2.draw(new Ellipse2D.Double(-radius, -radius, radius * 2, radius * 2));
    }

    private java.awt.Polygon polygon(double[] xs, double[] ys) {
        int[] xi = new int[xs.length];
        int[] yi = new int[ys.length];
        for (int i = 0; i < xs.length; i++) {
            xi[i] = (int) Math.round(xs[i]);
            yi[i] = (int) Math.round(ys[i]);
        }
        return new java.awt.Polygon(xi, yi, xs.length);
    }

    private void selectAt(int screenX, int screenY) {
        double wx = (screenX - getWidth() / 2.0 - panX) / (PIXELS_PER_UNIT * zoom);
        double wy = (getHeight() / 2.0 + panY - screenY) / (PIXELS_PER_UNIT * zoom);
        selectedIndex = -1;
        if (scene != null) {
            List<SceneObject> objects = scene.getAllObjects();
            double best = Double.MAX_VALUE;
            for (int i = 0; i < objects.size(); i++) {
                SceneObject object = objects.get(i);
                if (!object.isVisible() || !isObjectInCurrentView(object)) continue;
                Vec3 position = object.getEffectiveTransform().getPosition();
                double distance = Math.hypot(wx - position.x, wy - position.y);
                double tolerance = hitRadius(object.getGeometry()) * maxScale(object.getEffectiveTransform()) + .22;
                if (distance <= tolerance && distance < best) {
                    selectedIndex = i;
                    best = distance;
                }
            }
            // A lesson commonly presents a single large model. Preserve that
            // model's selection for imprecise touch dispatch from whiteboards
            // and embedded Swing hosts whose coordinates may be scaled.
            if (selectedIndex < 0) {
                int onlyVisibleIndex = -1;
                for (int i = 0; i < objects.size(); i++) {
                    SceneObject object = objects.get(i);
                    if (object.isVisible() && isObjectInCurrentView(object)) {
                        if (onlyVisibleIndex >= 0) {
                            onlyVisibleIndex = -1;
                            break;
                        }
                        onlyVisibleIndex = i;
                    }
                }
                selectedIndex = onlyVisibleIndex;
            }
            if (selectedIndex >= 0) {
                SceneObject object = objects.get(selectedIndex);
                scene.select(object);
                if (commandListener != null) commandListener.onSelectionChanged(object);
            } else {
                scene.clearSelection();
                if (commandListener != null) commandListener.onSelectionChanged(null);
            }
        }
        repaint();
    }

    private float toWorld(int pixels) {
        return (float) (pixels / (PIXELS_PER_UNIT * zoom));
    }

    private float clampScale(float factor) {
        return Math.max(0.7f, Math.min(1.35f, factor));
    }

    private double maxScale(Transform transform) {
        Vec3 scale = transform.getScale();
        return Math.max(Math.abs(scale.x), Math.abs(scale.y));
    }

    private double hitRadius(GeometryObject geometry) {
        if (geometry instanceof Rectangle) {
            Rectangle r = (Rectangle) geometry;
            return Math.max(r.getWidth(), r.getHeight()) * .72;
        }
        if (geometry instanceof Circle) return ((Circle) geometry).getRadius();
        if (geometry instanceof Cube) return ((Cube) geometry).getWidth() * .9;
        if (geometry instanceof Cylinder) {
            Cylinder c = (Cylinder) geometry;
            return Math.max(c.getRadius(), c.getHeight() * .5);
        }
        if (geometry instanceof Sphere) return ((Sphere) geometry).getRadius();
        if (geometry instanceof Cone) {
            Cone c = (Cone) geometry;
            return Math.max(c.getRadius(), c.getHeight() * .5);
        }
        return .5;
    }

    private String geometryName(GeometryObject geometry) {
        if (geometry instanceof Rectangle) return "矩形";
        if (geometry instanceof Circle) return "圆形";
        if (geometry instanceof Cube) return "正方体";
        if (geometry instanceof Cylinder) return "圆柱体";
        if (geometry instanceof Sphere) return "球体";
        if (geometry instanceof Cone) return "圆锥体";
        return "几何体";
    }

    private SceneObject getSelectedObject() {
        if (scene == null || selectedIndex < 0 || selectedIndex >= scene.getObjectCount()) return null;
        SceneObject object = scene.getAllObjects().get(selectedIndex);
        return isObjectInCurrentView(object) ? object : null;
    }

    /**
     * Keeps the teaching pages focused: plane geometry is shown only on the
     * 2D page and solid geometry only on the 3D page. The Scene remains the
     * single source of truth and is never mutated when the user changes page.
     */
    public boolean isObjectInCurrentView(SceneObject object) {
        if (object == null) {
            return false;
        }
        GeometryObject geometry = object.getGeometry();
        boolean isPlaneGeometry = geometry instanceof Rectangle
                || geometry instanceof Circle
                || geometry instanceof Polygon;
        return viewMode == ViewMode.MODE_2D ? isPlaneGeometry : !isPlaneGeometry;
    }

    public int getVisibleObjectCount() {
        if (scene == null) {
            return 0;
        }
        int count = 0;
        for (SceneObject object : scene.getAllObjects()) {
            if (object.isVisible() && isObjectInCurrentView(object)) {
                count++;
            }
        }
        return count;
    }

    public void handleTap(int screenX, int screenY) { selectAt(screenX, screenY); }
    public void onTouchDown(int x, int y) { selectAt(x, y); }
    public void onPenDown(int x, int y, float pressure) { selectAt(x, y); }
    public void setViewMode(ViewMode mode) { this.viewMode = mode == null ? ViewMode.MODE_2D : mode; repaint(); }
    public ViewMode getViewMode() { return viewMode; }
    public void resetView() { zoom = 1.0; panX = 0; panY = 0; selectedIndex = -1; repaint(); }
    public int getSelectedIndex() { return selectedIndex; }

    /** Returns the selected object's approximate screen location for context UI placement. */
    public java.awt.Point getSelectedScreenPoint() {
        SceneObject object = getSelectedObject();
        if (object == null) return null;
        Vec3 position = object.getEffectiveTransform().getPosition();
        int x = (int) Math.round(getWidth() / 2.0 + panX + position.x * PIXELS_PER_UNIT * zoom);
        int y = (int) Math.round(getHeight() / 2.0 + panY - position.y * PIXELS_PER_UNIT * zoom);
        return new java.awt.Point(x, y);
    }
}
