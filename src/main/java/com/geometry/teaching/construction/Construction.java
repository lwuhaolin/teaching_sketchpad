package com.geometry.teaching.construction;

import com.geometry.core.geometry.GeometryObject;

/**
 * Phase 07 - Interface for geometric construction operations.
 *
 * Construction objects build new {@link GeometryObject} instances from
 * existing geometric primitives (points, lines, circles, etc.).
 *
 * This system supports teaching scenarios like:
 *   - Given two points, construct a line segment
 *   - Given a center and radius, construct a circle
 *   - Given three points, construct a circle through them
 *
 * The interface is deliberately simple: {@code build()} returns the
 * constructed GeometryObject. Construction objects may hold transient
 * state (e.g. intermediate points being dragged) that affects the
 * returned geometry.
 *
 * Not thread-safe.
 */
public interface Construction {

    /**
     * Build and return the constructed GeometryObject.
     *
     * @return the newly constructed geometry object, never null
     */
    GeometryObject build();
}
