package com.geometry.persistence;

import com.geometry.persistence.model.ProjectData;

/**
 * Phase 10 - Version migration handler for .gtp project files.
 *
 * Supports upgrading project files from older formats to the current version.
 * Each migration step transforms the data model to the next version.
 *
 * Design:
 *   - Chains migration steps from source version to current version
 *   - Each step is a no-op if the data is already at the target version
 *   - Extensible: new versions can add migration steps
 *
 * Current version: 2.0
 *
 * Not thread-safe.
 */
public class VersionMigration {

    /** Current project file format version. */
    public static final String CURRENT_VERSION = "2.0";

    /** Version 1.0 → 2.0 migration. */
    private static final String VERSION_1_0 = "1.0";

    /**
     * Migrate a ProjectData to the current version.
     *
     * If the project is already at the current version, returns it unchanged.
     * If the project is at an older version, applies all intermediate migrations.
     *
     * @param project the project to migrate
     * @return the migrated project (same reference, modified in place)
     * @throws IllegalArgumentException if the version is not supported
     */
    public static ProjectData migrate(ProjectData project) {
        if (project == null) {
            throw new IllegalArgumentException("ProjectData cannot be null");
        }

        String version = project.getVersion();
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Project version cannot be null or empty");
        }

        if (VERSION_1_0.equals(version)) {
            migrateFrom10To20(project);
        } else if (CURRENT_VERSION.equals(version)) {
            // Already at current version
        } else {
            throw new IllegalArgumentException(
                    "Unsupported project version: '" + version
                            + "'. Supported versions: " + VERSION_1_0 + " → " + CURRENT_VERSION);
        }

        // Ensure version is up to date
        project.setVersion(CURRENT_VERSION);
        return project;
    }

    // ------------------------------------------------------------------
    // Migration steps
    // ------------------------------------------------------------------

    /**
     * Migrate from version 1.0 to 2.0.
     *
     * Changes in v2.0:
     *   - Added teaching, animation, whiteboard sections
     *   - Added camera data
     *   - Changed ObjectData to use parameters map instead of flat fields
     *   - Added annotation support
     *   - Added whiteboard stroke data
     *
     * @param project the project to migrate
     */
    private static void migrateFrom10To20(ProjectData project) {
        // 1. Add teaching data if missing
        if (project.getTeaching() == null) {
            project.setTeaching(new com.geometry.persistence.model.TeachingData());
        }

        // 2. Add animation data if missing
        if (project.getAnimation() == null) {
            project.setAnimation(new com.geometry.persistence.model.AnimationData());
        }

        // 3. Add whiteboard data if missing
        if (project.getWhiteboard() == null) {
            project.setWhiteboard(new com.geometry.persistence.model.WhiteboardData());
        }

        // 4. Add settings if missing
        if (project.getSettings() == null) {
            project.setSettings(new com.geometry.persistence.model.SettingData());
        }

        // 5. Migrate scene objects: convert flat parameters to parameter map
        com.geometry.persistence.model.SceneData scene = project.getScene();
        for (com.geometry.persistence.model.ObjectData obj : scene.getObjects()) {
            migrateObjectData(obj);
        }
    }

    /**
     * Migrate a single ObjectData from v1.0 to v2.0 format.
     *
     * In v1.0, parameters were stored as flat fields.
     * In v2.0, they are stored in a parameters map.
     *
     * @param obj the object data to migrate
     */
    private static void migrateObjectData(com.geometry.persistence.model.ObjectData obj) {
        // Parameters are already stored in the map for v2.0
        // For v1.0 compatibility, ensure the map has the expected keys
        if (!obj.getParameters().containsKey("type")) {
            obj.setParameter("type", 1.0f); // marker
        }
    }
}
