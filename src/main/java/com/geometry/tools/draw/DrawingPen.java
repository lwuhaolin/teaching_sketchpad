package com.geometry.tools.draw;

import com.geometry.interaction.action.DrawAction;
import com.geometry.interaction.event.StrokeGestureEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.teaching.recognition.StrokeRecognizer;
import com.geometry.teaching.recognition.DefaultStrokeRecognizer;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 07 - Drawing pen tool for whiteboard stroke-to-shape recognition.
 *
 * Receives {@link StrokeGestureEvent} objects (produced by
 * {@link com.geometry.interaction.gesture.StrokeGestureRecognizer})
 * and creates corresponding geometry objects in the scene.
 *
 * Supported shapes:
 *   - POINT:  creates a small sphere at the stroke center
 *   - LINE:   creates a line between stroke start and end
 *   - CIRCLE: creates a circle from recognized points (center + radius)
 *   - RECTANGLE: creates a rectangle from recognized bounding box
 *   - TRIANGLE / POLYGON: creates a polygon from recognized points
 *
 * Uses the configured {@link StrokeRecognizer} (defaults to
 * {@link DefaultStrokeRecognizer}) for shape analysis.
 *
 * In 2D mode, all shapes are created on the z=0 plane.
 *
 * Not thread-safe.
 */
public class DrawingPen implements Tool {

    private static final int DEFAULT_SEGMENTS = 24;
    private static final float POINT_RADIUS = 0.1f;
    private static final float LINE_THICKNESS = 0.05f;
    /** Pixels per world unit for 2D screen-to-world conversion. */
    private static final float PIXELS_PER_WORLD_UNIT = 40f;
    private static final int VIEWPORT_CENTER_X = 400;
    private static final int VIEWPORT_CENTER_Y = 300;

    private final ToolContext context;
    private final StrokeRecognizer recognizer;
    private boolean active;
    private StrokeGestureEvent lastEvent;

    /**
     * Create a DrawingPen with the default recognizer.
     *
     * @param context the tool context
     */
    public DrawingPen(ToolContext context) {
        this(context, new DefaultStrokeRecognizer());
    }

    /**
     * Create a DrawingPen with the given recognizer.
     *
     * @param context    the tool context
     * @param recognizer the stroke recognizer (must not be null)
     */
    public DrawingPen(ToolContext context, StrokeRecognizer recognizer) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.recognizer = recognizer != null ? recognizer : new DefaultStrokeRecognizer();
        this.active = false;
        this.lastEvent = null;
    }

    @Override
    public String getName() {
        return "drawing-pen";
    }

    @Override
    public void activate() {
        this.active = true;
        this.lastEvent = null;
    }

    @Override
    public void deactivate() {
        this.active = false;
        this.lastEvent = null;
    }

    // ------------------------------------------------------------------
    // Tool interface — DrawingPen only handles StrokeGestureEvent
    // ------------------------------------------------------------------

    @Override
    public void handle(com.geometry.interaction.action.Action action) {
        if (!active) {
            return;
        }
        if (!(action instanceof StrokeGestureEvent)) {
            return;
        }
        StrokeGestureEvent strokeEvent = (StrokeGestureEvent) action;
        this.lastEvent = strokeEvent;
        processStroke(strokeEvent);
    }

    @Override
    public void update() {
        // DrawingPen is event-driven; no per-frame update needed.
    }

    // ------------------------------------------------------------------
    // Stroke processing
    // ------------------------------------------------------------------

    /**
     * Process a recognized stroke and create the corresponding geometry.
     */
    private void processStroke(StrokeGestureEvent event) {
        if (event == null) {
            return;
        }

        switch (event.getShapeType()) {
            case POINT:
                handlePoint(event);
                break;
            case LINE:
                handleLine(event);
                break;
            case CIRCLE:
                handleCircle(event);
                break;
            case RECTANGLE:
                handleRectangle(event);
                break;
            case TRIANGLE:
            case POLYGON:
                handlePolygon(event);
                break;
            default:
                // Unknown shape — ignore
                break;
        }
    }

    /**
     * Handle a POINT stroke — create a small sphere at the stroke center.
     */
    private void handlePoint(StrokeGestureEvent event) {
        Vec2 center = event.getPointCount() > 0
                ? event.getPoints()[0]
                : new Vec2(VIEWPORT_CENTER_X, VIEWPORT_CENTER_Y);
        com.geometry.core.math.Vec3 worldPos = screenToWorld(center);
        com.geometry.core.geometry.Sphere pointGeo =
                new com.geometry.core.geometry.Sphere(POINT_RADIUS, 8, 4);
        pointGeo.setTransform(new com.geometry.core.transform.Transform(
                worldPos,
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        context.getScene().addObject(pointGeo);
    }

    /**
     * Handle a LINE stroke — create a line from start to end point.
     */
    private void handleLine(StrokeGestureEvent event) {
        Vec2[] pts = event.getPoints();
        if (pts.length < 2) {
            // Fallback to point
            handlePoint(event);
            return;
        }
        Vec2 start = pts[0];
        Vec2 end = pts[pts.length - 1];
        com.geometry.core.math.Vec3 worldStart = screenToWorld(start);
        com.geometry.core.math.Vec3 worldEnd = screenToWorld(end);

        float dx = worldEnd.x - worldStart.x;
        float dy = worldEnd.y - worldStart.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.01f) {
            handlePoint(event);
            return;
        }

        float centerX = (worldStart.x + worldEnd.x) / 2f;
        float centerY = (worldStart.y + worldEnd.y) / 2f;
        float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));

        com.geometry.core.geometry.Cube lineBox =
                new com.geometry.core.geometry.Cube(length, LINE_THICKNESS, 0.05f);
        lineBox.setTransform(new com.geometry.core.transform.Transform(
                new com.geometry.core.math.Vec3(centerX, centerY, 0f),
                new com.geometry.core.math.Vec3(0, 0, angleDeg),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        context.getScene().addObject(lineBox);
    }

    /**
     * Handle a CIRCLE stroke — create a circle from recognized points.
     *
     * Computes the center and radius from the stroke points.
     */
    private void handleCircle(StrokeGestureEvent event) {
        Vec2[] pts = event.getPoints();
        if (pts.length < 3) {
            // Not enough points for a circle — fall back to point
            handlePoint(event);
            return;
        }

        // Compute bounding box to estimate center and radius
        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Vec2 p : pts) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        float centerX = (minX + maxX) / 2f;
        float centerY = (minY + maxY) / 2f;
        float radiusPx = Math.max(maxX - centerX, maxY - centerY);

        // Convert to world space
        com.geometry.core.math.Vec3 worldCenter = screenToWorld(new Vec2(centerX, centerY));
        float worldRadius = radiusPx / PIXELS_PER_WORLD_UNIT;

        if (worldRadius < 0.01f) {
            handlePoint(event);
            return;
        }

        com.geometry.core.geometry.Circle circle =
                new com.geometry.core.geometry.Circle(worldRadius, DEFAULT_SEGMENTS);
        circle.setTransform(new com.geometry.core.transform.Transform(
                worldCenter,
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        context.getScene().addObject(circle);
    }

    /**
     * Handle a RECTANGLE stroke — create a rectangle from bounding box.
     */
    private void handleRectangle(StrokeGestureEvent event) {
        Vec2[] pts = event.getPoints();
        if (pts.length < 2) {
            handlePoint(event);
            return;
        }

        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (Vec2 p : pts) {
            minX = Math.min(minX, p.x);
            maxX = Math.max(maxX, p.x);
            minY = Math.min(minY, p.y);
            maxY = Math.max(maxY, p.y);
        }

        float widthPx = maxX - minX;
        float heightPx = maxY - minY;
        if (widthPx < 1f || heightPx < 1f) {
            handlePoint(event);
            return;
        }

        float centerX = (minX + maxX) / 2f;
        float centerY = (minY + maxY) / 2f;
        com.geometry.core.math.Vec3 worldCenter = screenToWorld(new Vec2(centerX, centerY));
        float worldWidth = widthPx / PIXELS_PER_WORLD_UNIT;
        float worldHeight = heightPx / PIXELS_PER_WORLD_UNIT;

        com.geometry.core.geometry.Rectangle rect =
                new com.geometry.core.geometry.Rectangle(worldWidth, worldHeight);
        rect.setTransform(new com.geometry.core.transform.Transform(
                worldCenter,
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        context.getScene().addObject(rect);
    }

    /**
     * Handle a TRIANGLE or POLYGON stroke — create a polygon from points.
     */
    private void handlePolygon(StrokeGestureEvent event) {
        Vec2[] pts = event.getPoints();
        if (pts.length < 3) {
            handlePoint(event);
            return;
        }

        List<com.geometry.core.math.Vec3> worldPoints = new ArrayList<>();
        for (Vec2 p : pts) {
            worldPoints.add(screenToWorld(p));
        }

        com.geometry.core.math.Vec3[] worldPts =
                worldPoints.toArray(new com.geometry.core.math.Vec3[0]);

        com.geometry.core.geometry.Polygon polygon =
                new com.geometry.core.geometry.Polygon(worldPts);
        context.getScene().addObject(polygon);
    }

    // ------------------------------------------------------------------
    // Screen-to-world conversion
    // ------------------------------------------------------------------

    /**
     * Convert screen-space Vec2 to world-space coordinates.
     *
     * Viewport center is world origin. Y-axis is flipped.
     *
     * @param screen screen-space position (pixels, origin top-left)
     * @return world-space position
     */
    private com.geometry.core.math.Vec3 screenToWorld(Vec2 screen) {
        float worldX = (screen.x - VIEWPORT_CENTER_X) / PIXELS_PER_WORLD_UNIT;
        float worldY = (VIEWPORT_CENTER_Y - screen.y) / PIXELS_PER_WORLD_UNIT;
        return new com.geometry.core.math.Vec3(worldX, worldY, 0f);
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    /**
     * Get the last processed stroke event.
     */
    public StrokeGestureEvent getLastEvent() {
        return lastEvent;
    }

    /**
     * Get the recognizer name.
     */
    public String getRecognizerName() {
        return recognizer.getName();
    }
}
