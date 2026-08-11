package com.geometry.tools.draw;

import com.geometry.interaction.event.Vec2;
import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.DrawAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

/**
 * Phase 06 - Draw tool implementation.
 *
 * Creates GeometryObjects from DrawActions.
 *
 * Supported draw types (Phase 06 scope):
 *   - POINT:    creates a small sphere at the point location
 *   - LINE:     creates a thin box (capsule-like) between two points
 *   - RECTANGLE: creates a Rectangle geometry
 *   - CIRCLE:   creates a Circle geometry
 *
 * Future (Phase 07+):
 *   - Hand-written shape recognition
 *   - Pen stroke to geometry
 *
 * Mode adaptation:
 *   - 2D mode: all objects are created on z=0 plane
 *   - 3D mode: objects are placed at the given 3D position
 */
public class DrawTool implements Tool {

    private static final int DEFAULT_SEGMENTS = 16;
    private static final float POINT_RADIUS = 0.1f;
    private static final float LINE_THICKNESS = 0.05f;
    /** Pixels per world unit for 2D screen-to-world conversion. */
    private static final float PIXELS_PER_WORLD_UNIT_2D = 40f;
    private static final int VIEWPORT_CENTER_X = 400;
    private static final int VIEWPORT_CENTER_Y = 300;

    private final ToolContext context;
    private boolean active;
    /** True while a draw gesture is in progress. */
    private boolean drawing;

    public DrawTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
        this.drawing = false;
    }

    @Override
    public String getName() {
        return "draw";
    }

    @Override
    public void activate() {
        this.active = true;
        this.drawing = false;
    }

    @Override
    public void deactivate() {
        this.active = false;
        this.drawing = false;
    }

    @Override
    public void handle(Action action) {
        if (!active) {
            return;
        }
        if (!(action instanceof DrawAction)) {
            return;
        }
        DrawAction drawAction = (DrawAction) action;
        switch (drawAction.getDrawType()) {
            case POINT:
                handlePoint(drawAction);
                break;
            case LINE:
            case RECTANGLE:
            case CIRCLE:
                handleShape(drawAction);
                break;
            case CUBE:
            case SPHERE:
            case CYLINDER:
            case CONE:
                handleSolid(drawAction);
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------------
    // Point drawing
    // ------------------------------------------------------------------

    /**
     * Handle a POINT draw action — create a small sphere at the given position.
     */
    private void handlePoint(DrawAction action) {
        com.geometry.core.math.Vec3 worldPos = worldStart(action);
        com.geometry.core.geometry.Sphere pointGeo =
                new com.geometry.core.geometry.Sphere(POINT_RADIUS, 8, 4);
        pointGeo.setTransform(new com.geometry.core.transform.Transform(
                worldPos,
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        context.getScene().addObject(pointGeo);
    }

    // ------------------------------------------------------------------
    // Shape drawing
    // ------------------------------------------------------------------

    /**
     * Handle LINE, RECTANGLE, CIRCLE draw actions.
     */
    private void handleShape(DrawAction action) {
        Vec2 start = action.getStart();
        Vec2 end = action.getEnd();
        com.geometry.core.math.Vec3 worldStart = worldStart(action);
        com.geometry.core.math.Vec3 worldEnd = worldEnd(action);

        com.geometry.core.geometry.GeometryObject geometry;
        switch (action.getDrawType()) {
            case RECTANGLE:
                geometry = createRectangle(worldStart, worldEnd);
                if (geometry == null) return;
                break;
            case CIRCLE:
                geometry = createCircle(worldStart, worldEnd);
                if (geometry == null) return;
                break;
            case LINE:
                geometry = createLine(worldStart, worldEnd);
                if (geometry == null) return;
                break;
            default:
                return;
        }
        context.getScene().addObject(geometry);
    }

    /** Creates a classroom-sized solid at the world location selected in the 3D viewport. */
    private void handleSolid(DrawAction action) {
        com.geometry.core.math.Vec3 position = worldStart(action);
        com.geometry.core.geometry.GeometryObject geometry;
        switch (action.getDrawType()) {
            case CUBE:
                geometry = new com.geometry.core.geometry.Cube(2f, 2f, 2f);
                break;
            case SPHERE:
                geometry = new com.geometry.core.geometry.Sphere(1.1f, 20, 12);
                break;
            case CYLINDER:
                geometry = new com.geometry.core.geometry.Cylinder(1f, 2.4f, 20);
                break;
            case CONE:
                geometry = new com.geometry.core.geometry.Cone(1.1f, 2.4f, 20);
                break;
            default:
                return;
        }
        geometry.setTransform(new com.geometry.core.transform.Transform(position,
                new com.geometry.core.math.Vec3(0f, 0f, 0f),
                new com.geometry.core.math.Vec3(1f, 1f, 1f)));
        context.getScene().addObject(geometry);
    }

    private com.geometry.core.math.Vec3 worldStart(DrawAction action) {
        return action.hasWorldCoordinates() ? action.getWorldStart() : screenToWorld(action.getStart());
    }

    private com.geometry.core.math.Vec3 worldEnd(DrawAction action) {
        return action.hasWorldCoordinates() ? action.getWorldEnd() : screenToWorld(action.getEnd());
    }

    /**
     * Create a Rectangle from two corner points (world space).
     */
    private com.geometry.core.geometry.GeometryObject createRectangle(
            com.geometry.core.math.Vec3 start, com.geometry.core.math.Vec3 end) {
        float width = Math.abs(end.x - start.x);
        float height = Math.abs(end.y - start.y);
        if (width < 0.01f || height < 0.01f) {
            return null; // Too small to be meaningful
        }
        float centerX = (start.x + end.x) / 2f;
        float centerY = (start.y + end.y) / 2f;
        com.geometry.core.geometry.Rectangle rect =
                new com.geometry.core.geometry.Rectangle(width, height);
        rect.setTransform(new com.geometry.core.transform.Transform(
                new com.geometry.core.math.Vec3(centerX, centerY, 0f),
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        return rect;
    }

    /**
     * Create a Circle from two points (center and edge) in world space.
     */
    private com.geometry.core.geometry.GeometryObject createCircle(
            com.geometry.core.math.Vec3 start, com.geometry.core.math.Vec3 end) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float radius = (float) Math.sqrt(dx * dx + dy * dy);
        if (radius < 0.01f) {
            return null;
        }
        com.geometry.core.geometry.Circle circle =
                new com.geometry.core.geometry.Circle(radius, DEFAULT_SEGMENTS);
        circle.setTransform(new com.geometry.core.transform.Transform(
                new com.geometry.core.math.Vec3(start.x, start.y, 0f),
                new com.geometry.core.math.Vec3(0, 0, 0),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        return circle;
    }

    /**
     * Create a LINE representation as a thin box between two points.
     */
    private com.geometry.core.geometry.GeometryObject createLine(
            com.geometry.core.math.Vec3 start, com.geometry.core.math.Vec3 end) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        if (length < 0.01f) {
            return null;
        }
        float centerX = (start.x + end.x) / 2f;
        float centerY = (start.y + end.y) / 2f;
        // Rotation angle to align the box with the line direction
        float angleDeg = (float) Math.toDegrees(Math.atan2(dy, dx));
        com.geometry.core.geometry.Cube lineBox =
                new com.geometry.core.geometry.Cube(length, LINE_THICKNESS, 0.05f);
        lineBox.setTransform(new com.geometry.core.transform.Transform(
                new com.geometry.core.math.Vec3(centerX, centerY, 0f),
                new com.geometry.core.math.Vec3(0, 0, angleDeg),
                new com.geometry.core.math.Vec3(1, 1, 1)));
        return lineBox;
    }

    // ------------------------------------------------------------------
    // Screen-to-world conversion
    // ------------------------------------------------------------------

    /**
     * Convert a screen-space Vec2 to world-space coordinates.
     *
     * Viewport center is world origin. Y-axis is flipped (screen Y goes down,
     * world Y goes up).
     *
     * @param screen the screen-space position (pixels, origin top-left)
     * @return the world-space position
     */
    private com.geometry.core.math.Vec3 screenToWorld(com.geometry.interaction.event.Vec2 screen) {
        float ppm = context.is2DMode()
                ? PIXELS_PER_WORLD_UNIT_2D
                : 1f;
        // Flip Y because screen Y goes down, world Y goes up
        float worldX = (screen.x - VIEWPORT_CENTER_X) / ppm;
        float worldY = (VIEWPORT_CENTER_Y - screen.y) / ppm;
        return new com.geometry.core.math.Vec3(worldX, worldY, 0f);
    }

    @Override
    public void update() {
        // DrawTool may track in-progress strokes here in future phases.
    }
}
