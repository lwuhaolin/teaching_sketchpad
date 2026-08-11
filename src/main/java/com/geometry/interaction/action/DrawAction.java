package com.geometry.interaction.action;

import com.geometry.interaction.event.Vec2;
import com.geometry.core.math.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 06 - Action to draw/create a new GeometryObject.
 *
 * Used by DrawTool to create geometry primitives.
 * The drawType determines what kind of object is created:
 *   POINT, LINE, RECTANGLE, CIRCLE.
 *
 * Concrete parameters depend on the drawType.
 */
public class DrawAction implements Action {

    public enum DrawType {
        POINT,
        LINE,
        RECTANGLE,
        CIRCLE,
        CUBE,
        SPHERE,
        CYLINDER,
        CONE
    }

    private final DrawType drawType;
    private final Vec2 start;
    private final Vec2 end;
    /** Additional parameters depending on drawType. */
    private final List<Float> params;
    /** Optional world coordinates supplied by a viewport that owns projection. */
    private final Vec3 worldStart;
    private final Vec3 worldEnd;

    /**
     * Create a DrawAction for a POINT.
     */
    public DrawAction(Vec2 point) {
        this(DrawType.POINT, point, point, new ArrayList<>());
    }

    /**
     * Create a DrawAction for LINE, RECTANGLE, or CIRCLE.
     *
     * @param drawType the type of object to draw
     * @param start    start point in screen/pixel coordinates
     * @param end      end point in screen/pixel coordinates
     * @param params   additional parameters (e.g. radius for circle)
     */
    public DrawAction(DrawType drawType, Vec2 start, Vec2 end, List<Float> params) {
        this(drawType, start, end, params, null, null);
    }

    private DrawAction(DrawType drawType, Vec2 start, Vec2 end, List<Float> params,
                       Vec3 worldStart, Vec3 worldEnd) {
        if (drawType == null) {
            throw new IllegalArgumentException("DrawType cannot be null");
        }
        this.drawType = drawType;
        this.start = start != null ? start : Vec2.ZERO;
        this.end = end != null ? end : Vec2.ZERO;
        this.params = params != null ? new ArrayList<>(params) : new ArrayList<>();
        this.worldStart = worldStart;
        this.worldEnd = worldEnd;
    }

    /** Creates an action whose coordinates have already been projected to world space. */
    public static DrawAction world(DrawType drawType, Vec3 start, Vec3 end) {
        Vec3 safeStart = start != null ? start : new Vec3(0f, 0f, 0f);
        Vec3 safeEnd = end != null ? end : safeStart;
        return new DrawAction(drawType, Vec2.ZERO, Vec2.ZERO, Collections.<Float>emptyList(),
                safeStart, safeEnd);
    }

    @Override
    public void execute() {
        // DrawAction is a request — the actual creation happens in DrawTool.
        // This method is a no-op here; subclasses or DrawTool handle creation.
    }

    @Override
    public String getDescription() {
        return "DrawAction{type=" + drawType
                + ", start=" + start + ", end=" + end + "}";
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public Vec2 getStart() {
        return start;
    }

    public Vec2 getEnd() {
        return end;
    }

    public List<Float> getParams() {
        return Collections.unmodifiableList(params);
    }

    public boolean hasWorldCoordinates() { return worldStart != null; }
    public Vec3 getWorldStart() { return worldStart; }
    public Vec3 getWorldEnd() { return worldEnd; }
}
