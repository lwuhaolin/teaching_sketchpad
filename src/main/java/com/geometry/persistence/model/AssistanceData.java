package com.geometry.persistence.model;

/**
 * Phase 10 - Data model for teaching assistant objects (grid, coordinate system, etc.).
 *
 * Stores the type and configuration of assistant objects.
 *
 * Not thread-safe.
 */
public class AssistanceData {

    /** Types of assistant objects. */
    public enum AssistanceType {
        GRID,
        COORDINATE_SYSTEM,
        HELPER_LINE
    }

    private final AssistanceType type;
    private final java.util.Map<String, Object> properties;

    /**
     * Create an AssistanceData.
     *
     * @param type       the type of assistant
     * @param properties configuration properties (may be null/empty)
     */
    public AssistanceData(AssistanceType type, java.util.Map<String, Object> properties) {
        if (type == null) {
            throw new IllegalArgumentException("AssistanceType cannot be null");
        }
        this.type = type;
        this.properties = properties != null ? new java.util.HashMap<>(properties) : new java.util.HashMap<>();
    }

    public AssistanceType getType() {
        return type;
    }

    public java.util.Map<String, Object> getProperties() {
        return new java.util.HashMap<>(properties);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssistanceData that = (AssistanceData) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "AssistanceData{type=" + type + "}";
    }
}
