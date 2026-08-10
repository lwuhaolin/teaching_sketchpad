package com.geometry.scene;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.transform.Transform;

import java.util.Objects;

/**
 * Phase 04 - Wrapper for a GeometryObject inside a Scene.
 *
 * Each SceneObject holds:
 *   - A unique ID (used for selection, undo, persistence)
 *   - The underlying GeometryObject
 *   - An override Transform (null means use the geometry's own transform)
 *   - Visibility and selection state
 *
 * The Scene owns SceneObjects and drives their update/render lifecycle.
 */
public class SceneObject {

    private final String id;
    private final GeometryObject geometry;
    private Transform overrideTransform;
    private boolean visible;
    private boolean selected;

    /**
     * Create a SceneObject with a generated ID.
     *
     * @param geometry the geometry object to wrap
     */
    public SceneObject(GeometryObject geometry) {
        this("object_" + String.format("%04x", Math.abs(System.identityHashCode(geometry)) & 0xFFFF), geometry);
    }

    /**
     * Create a SceneObject with an explicit ID.
     *
     * @param id       unique identifier
     * @param geometry the geometry object to wrap
     */
    public SceneObject(String id, GeometryObject geometry) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (geometry == null) {
            throw new IllegalArgumentException("GeometryObject cannot be null");
        }
        this.id = id;
        this.geometry = geometry;
        this.overrideTransform = null;
        this.visible = true;
        this.selected = false;
    }

    // ------------------------------------------------------------------
    // Identity
    // ------------------------------------------------------------------

    public String getId() {
        return id;
    }

    // ------------------------------------------------------------------
    // Geometry access
    // ------------------------------------------------------------------

    public GeometryObject getGeometry() {
        return geometry;
    }

    /**
     * Return the effective transform: override if set, otherwise the geometry's own.
     */
    public Transform getEffectiveTransform() {
        return overrideTransform != null ? overrideTransform : geometry.getTransform();
    }

    /**
     * Set an override transform for this scene object.
     * Pass null to clear the override and use the geometry's own transform.
     */
    public void setOverrideTransform(Transform overrideTransform) {
        this.overrideTransform = overrideTransform;
    }

    public Transform getOverrideTransform() {
        return overrideTransform;
    }

    // ------------------------------------------------------------------
    // Visibility
    // ------------------------------------------------------------------

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    // ------------------------------------------------------------------
    // Selection
    // ------------------------------------------------------------------

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Toggle selection state.
     */
    public void toggleSelected() {
        this.selected = !this.selected;
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Update the underlying geometry's mesh (called by Scene.update()).
     */
    public void update() {
        geometry.updateMesh();
    }

    // ------------------------------------------------------------------
    // Equals / HashCode
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SceneObject that = (SceneObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SceneObject{id='" + id + "', visible=" + visible
                + ", selected=" + selected + ", geometry=" + geometry.getClass().getSimpleName() + "}";
    }
}
