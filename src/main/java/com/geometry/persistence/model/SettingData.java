package com.geometry.persistence.model;

/**
 * Phase 10 - Data model for project settings.
 *
 * Stores application-level settings such as render mode, grid settings,
 * and other user preferences.
 *
 * Not thread-safe.
 */
public class SettingData {

    private String renderMode;
    private boolean showGrid;
    private boolean showCoordinateSystem;
    private float gridSpacing;
    private boolean autoSave;
    private int autoSaveIntervalSeconds;

    /**
     * Create a SettingData with default values.
     */
    public SettingData() {
        this.renderMode = "MODE_2D";
        this.showGrid = true;
        this.showCoordinateSystem = false;
        this.gridSpacing = 1.0f;
        this.autoSave = false;
        this.autoSaveIntervalSeconds = 60;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getRenderMode() {
        return renderMode;
    }

    public void setRenderMode(String renderMode) {
        this.renderMode = renderMode != null ? renderMode : "MODE_2D";
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowCoordinateSystem() {
        return showCoordinateSystem;
    }

    public void setShowCoordinateSystem(boolean showCoordinateSystem) {
        this.showCoordinateSystem = showCoordinateSystem;
    }

    public float getGridSpacing() {
        return gridSpacing;
    }

    public void setGridSpacing(float gridSpacing) {
        this.gridSpacing = gridSpacing > 0 ? gridSpacing : 1.0f;
    }

    public boolean isAutoSave() {
        return autoSave;
    }

    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public int getAutoSaveIntervalSeconds() {
        return autoSaveIntervalSeconds;
    }

    public void setAutoSaveIntervalSeconds(int interval) {
        this.autoSaveIntervalSeconds = interval > 0 ? interval : 60;
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
        SettingData that = (SettingData) o;
        return showGrid == that.showGrid
                && showCoordinateSystem == that.showCoordinateSystem
                && autoSave == that.autoSave
                && autoSaveIntervalSeconds == that.autoSaveIntervalSeconds
                && Float.compare(that.gridSpacing, gridSpacing) == 0
                && renderMode.equals(that.renderMode);
    }

    @Override
    public int hashCode() {
        int result = renderMode.hashCode();
        result = 31 * result + (showGrid ? 1 : 0);
        result = 31 * result + (showCoordinateSystem ? 1 : 0);
        result = 31 * result + Float.floatToIntBits(gridSpacing);
        result = 31 * result + (autoSave ? 1 : 0);
        result = 31 * result + autoSaveIntervalSeconds;
        return result;
    }

    @Override
    public String toString() {
        return "SettingData{renderMode=" + renderMode
                + ", grid=" + showGrid
                + ", coordSys=" + showCoordinateSystem
                + ", autoSave=" + autoSave + "}";
    }
}
