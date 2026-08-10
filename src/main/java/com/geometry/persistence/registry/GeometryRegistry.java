package com.geometry.persistence.registry;

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
import com.geometry.persistence.model.ObjectData;

import java.util.HashMap;
import java.util.Map;

/**
 * Phase 10 - Registry for creating GeometryObjects from persisted data.
 *
 * Uses a type-to-creator mapping to avoid if/else chains.
 * Each geometry type has a registered factory method that takes
 * an ObjectData and returns a GeometryObject with the correct
 * parameters and transform.
 *
 * Design:
 *   - Extensible: new geometry types can be registered dynamically
 *   - No direct mesh data — mesh is regenerated from parameters
 *   - Throws UnsupportedOperationException for unknown types
 *
 * Not thread-safe for registration changes, but lookup is read-only.
 */
public class GeometryRegistry {

    /** Functional interface for geometry creation. */
    @FunctionalInterface
    public interface GeometryCreator {
        /**
         * Create a GeometryObject from the given ObjectData.
         *
         * @param data the serialized object data
         * @return the created GeometryObject
         * @throws IllegalArgumentException if the data is invalid
         */
        GeometryObject create(ObjectData data);
    }

    private final Map<String, GeometryCreator> creators;

    /**
     * Create a GeometryRegistry with all built-in geometry types registered.
     */
    public GeometryRegistry() {
        this.creators = new HashMap<>();
        registerBuiltIns();
    }

    /**
     * Register a built-in geometry creator.
     *
     * @param type    type name (e.g. "Cube")
     * @param creator the creator function
     */
    public void register(String type, GeometryCreator creator) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be null or empty");
        }
        if (creator == null) {
            throw new IllegalArgumentException("Creator cannot be null");
        }
        creators.put(type.toUpperCase(), creator);
    }

    /**
     * Unregister a geometry type.
     *
     * @param type type name
     */
    public void unregister(String type) {
        creators.remove(type.toUpperCase());
    }

    /**
     * Check if a type is registered.
     *
     * @param type type name
     * @return true if registered
     */
    public boolean isRegistered(String type) {
        return type != null && creators.containsKey(type.toUpperCase());
    }

    /**
     * Create a GeometryObject from ObjectData.
     *
     * @param data the serialized object data
     * @return the created GeometryObject
     * @throws IllegalArgumentException if the type is not registered or data is invalid
     */
    public GeometryObject create(ObjectData data) {
        if (data == null) {
            throw new IllegalArgumentException("ObjectData cannot be null");
        }
        GeometryCreator creator = creators.get(data.getType().toUpperCase());
        if (creator == null) {
            throw new IllegalArgumentException(
                    "Unknown geometry type: '" + data.getType()
                            + "'. Registered types: " + creators.keySet());
        }
        return creator.create(data);
    }

    /**
     * Get all registered type names.
     */
    public java.util.Set<String> getRegisteredTypes() {
        return java.util.Collections.unmodifiableSet(creators.keySet());
    }

    // ------------------------------------------------------------------
    // Built-in registrations
    // ------------------------------------------------------------------

    private void registerBuiltIns() {
        // Cube
        register("CUBE", data -> {
            float width = data.getParameter("width", 1.0f);
            float height = data.getParameter("height", 1.0f);
            float depth = data.getParameter("depth", 1.0f);
            Cube cube = new Cube(width, height, depth);
            cube.setTransform(buildTransform(data));
            return cube;
        });

        // Cylinder
        register("CYLINDER", data -> {
            float radius = data.getParameter("radius", 1.0f);
            float height = data.getParameter("height", 2.0f);
            int segments = (int) data.getParameter("segments", 16f);
            Cylinder cylinder = new Cylinder(radius, height, segments);
            cylinder.setTransform(buildTransform(data));
            return cylinder;
        });

        // Cone
        register("CONE", data -> {
            float radius = data.getParameter("radius", 1.0f);
            float height = data.getParameter("height", 2.0f);
            int segments = (int) data.getParameter("segments", 16f);
            Cone cone = new Cone(radius, height, segments);
            cone.setTransform(buildTransform(data));
            return cone;
        });

        // Sphere
        register("SPHERE", data -> {
            float radius = data.getParameter("radius", 1.0f);
            int segments = (int) data.getParameter("segments", 16f);
            int rings = (int) data.getParameter("rings", 8f);
            Sphere sphere = new Sphere(radius, segments, rings);
            sphere.setTransform(buildTransform(data));
            return sphere;
        });

        // Circle (2D)
        register("CIRCLE", data -> {
            float radius = data.getParameter("radius", 1.0f);
            int segments = (int) data.getParameter("segments", 24f);
            Circle circle = new Circle(radius, segments);
            circle.setTransform(buildTransform(data));
            return circle;
        });

        // Rectangle (2D)
        register("RECTANGLE", data -> {
            float width = data.getParameter("width", 2.0f);
            float height = data.getParameter("height", 1.0f);
            Rectangle rect = new Rectangle(width, height);
            rect.setTransform(buildTransform(data));
            return rect;
        });

        // Polygon (2D)
        register("POLYGON", data -> {
            // Polygon points are stored as a count placeholder in parameters
            // We default to a triangle since point arrays can't be stored in Map<String,Float>
            Polygon poly = new Polygon(
                    new Vec3(-1f, -1f, 0f),
                    new Vec3(1f, -1f, 0f),
                    new Vec3(0f, 1f, 0f)
            );
            poly.setTransform(buildTransform(data));
            return poly;
        });
    }

    /**
     * Build a Transform from ObjectData.
     *
     * @param data the serialized object data
     * @return the Transform
     */
    private Transform buildTransform(ObjectData data) {
        Vec3 position = new Vec3(
                data.getPosition()[0],
                data.getPosition()[1],
                data.getPosition()[2]
        );
        Vec3 rotation = new Vec3(
                data.getRotation()[0],
                data.getRotation()[1],
                data.getRotation()[2]
        );
        Vec3 scale = new Vec3(
                data.getScale()[0],
                data.getScale()[1],
                data.getScale()[2]
        );
        return new Transform(position, rotation, scale);
    }
}
