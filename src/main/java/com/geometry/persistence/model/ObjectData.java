package com.geometry.persistence.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Phase 10 - Data model for a single geometry object in a project.
 *
 * Stores geometry type, parameters, and transform — NOT mesh vertex data.
 * On load, the GeometryRegistry uses the type and parameters to recreate
 * the GeometryObject and regenerate its mesh via MeshFactory.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "id": "cube001",
 *   "type": "Cube",
 *   "transform": {
 *     "position": [0, 0, 0],
 *     "rotation": [0, 0, 0],
 *     "scale": [1, 1, 1]
 *   },
 *   "parameters": {
 *     "width": 1.0,
 *     "height": 1.0,
 *     "depth": 1.0
 *   }
 * }
 * </pre>
 *
 * Parameters are stored as a flexible key-value map to support all geometry types.
 *
 * Not thread-safe.
 */
public class ObjectData {

    private String id;
    private String type;
    private float[] position;
    private float[] rotation;
    private float[] scale;
    private Map<String, Float> parameters;

    /**
     * Create an ObjectData.
     *
     * @param id       unique identifier
     * @param type     geometry type (e.g. "Cube", "Cylinder")
     * @param position world position [x, y, z]
     * @param rotation Euler angles in degrees [pitch, yaw, roll]
     * @param scale    scale factors [x, y, z]
     * @param parameters geometry-specific parameters
     */
    public ObjectData(String id, String type, float[] position, float[] rotation,
                      float[] scale, Map<String, Float> parameters) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        this.id = id;
        this.type = type;
        this.position = position != null ? position.clone() : new float[]{0f, 0f, 0f};
        this.rotation = rotation != null ? rotation.clone() : new float[]{0f, 0f, 0f};
        this.scale = scale != null ? scale.clone() : new float[]{1f, 1f, 1f};
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }
        this.type = type;
    }

    public float[] getPosition() {
        return position.clone();
    }

    public void setPosition(float[] position) {
        if (position == null || position.length != 3) {
            throw new IllegalArgumentException("Position must be a 3-float array");
        }
        this.position = position.clone();
    }

    public float[] getRotation() {
        return rotation.clone();
    }

    public void setRotation(float[] rotation) {
        if (rotation == null || rotation.length != 3) {
            throw new IllegalArgumentException("Rotation must be a 3-float array");
        }
        this.rotation = rotation.clone();
    }

    public float[] getScale() {
        return scale.clone();
    }

    public void setScale(float[] scale) {
        if (scale == null || scale.length != 3) {
            throw new IllegalArgumentException("Scale must be a 3-float array");
        }
        this.scale = scale.clone();
    }

    public Map<String, Float> getParameters() {
        return new HashMap<>(parameters);
    }

    public void setParameters(Map<String, Float> parameters) {
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
    }

    public float getParameter(String key, float defaultValue) {
        return parameters.getOrDefault(key, defaultValue);
    }

    public void setParameter(String key, float value) {
        parameters.put(key, value);
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ObjectData that = (ObjectData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ObjectData{id='" + id + "', type='" + type
                + "', pos=" + java.util.Arrays.toString(position)
                + ", params=" + parameters + "}";
    }
}
