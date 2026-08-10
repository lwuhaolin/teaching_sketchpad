package com.geometry.tools.measure;

import com.geometry.interaction.action.Action;
import com.geometry.interaction.action.MeasureAction;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 06 - Measure tool implementation (stub).
 *
 * Records measurement points and produces MeasureAction results.
 *
 * Currently supports:
 *   - DISTANCE: measure distance between two points
 *   - ANGLE: measure angle between three points
 *
 * Phase 07 will add annotation rendering for measurement results.
 */
public class MeasureTool implements Tool {

    private final ToolContext context;
    private boolean active;
    /** Points collected during the current measurement session. */
    private final List<com.geometry.core.math.Vec3> points;
    /** The last computed measurement result. */
    private float lastResult;
    private MeasureAction.MeasureType lastType;

    public MeasureTool(ToolContext context) {
        if (context == null) {
            throw new IllegalArgumentException("ToolContext cannot be null");
        }
        this.context = context;
        this.active = false;
        this.points = new ArrayList<>();
        this.lastResult = 0f;
        this.lastType = null;
    }

    @Override
    public String getName() {
        return "measure";
    }

    @Override
    public void activate() {
        this.active = true;
        this.points.clear();
        this.lastResult = 0f;
        this.lastType = null;
    }

    @Override
    public void deactivate() {
        this.active = false;
        this.points.clear();
    }

    @Override
    public void handle(Action action) {
        if (!active) {
            return;
        }
        if (!(action instanceof MeasureAction)) {
            return;
        }
        MeasureAction measureAction = (MeasureAction) action;
        measureAction.execute();
        this.lastResult = measureAction.getResult();
        this.lastType = measureAction.getMeasureType();
    }

    @Override
    public void update() {
        // No per-frame state for the basic measure tool.
    }

    /**
     * Get the result of the last measurement.
     *
     * @return the last computed result, or 0f if no measurement has been made
     */
    public float getLastResult() {
        return lastResult;
    }

    /**
     * Get the type of the last measurement.
     */
    public MeasureAction.MeasureType getLastType() {
        return lastType;
    }

    /**
     * Get all points collected so far in the current session.
     */
    public List<com.geometry.core.math.Vec3> getPoints() {
        return new ArrayList<>(points);
    }

    /**
     * Add a point to the current measurement session.
     */
    public void addPoint(com.geometry.core.math.Vec3 point) {
        if (point == null) {
            return;
        }
        points.add(point);
    }
}
