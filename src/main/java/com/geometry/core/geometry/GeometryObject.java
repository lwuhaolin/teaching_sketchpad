package com.geometry.core.geometry;

import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;

/**
 * Phase 01 - Interface for all geometry objects.
 *
 * Every renderable object in the engine (Cube, Cylinder, Sphere, Rectangle, etc.)
 * must implement this interface. It provides access to the object's mesh data,
 * its world-space transform, and a method to regenerate the mesh when parameters change.
 *
 * Phase 02 will introduce concrete implementations (Cube, Sphere, etc.).
 */
public interface GeometryObject {

    /**
     * Return the triangle mesh representing this object's current geometry.
     * The mesh must be up-to-date with the current transform and parameters.
     */
    Mesh getMesh();

    /**
     * Return the current world-space transform (position, rotation, scale).
     */
    Transform getTransform();

    /**
     * Set the world-space transform.
     * @throws IllegalArgumentException if transform is null.
     */
    void setTransform(Transform transform);

    /**
     * Regenerate the mesh from the current parameters and transform.
     * Called when parameters change or when the mesh needs to be refreshed.
     */
    void updateMesh();
}
