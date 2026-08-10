package com.geometry.runtime.config;

/**
 * Phase 12 - Application configuration holder.
 *
 * Stores runtime configuration values loaded from a config file or
 * defaults. All values have sensible defaults so the application
 * can start without a config file.
 *
 * Configuration keys:
 *   window.width      - default 1024
 *   window.height     - default 768
 *   window.resizable  - default true
 *   render.quality    - "LOW", "NORMAL", "HIGH"
 *   render.fps        - default 60
 *   ui.language       - "zh" or "en"
 *   ui.theme          - "light" or "dark"
 *   performance.mode  - "COMPATIBILITY", "NORMAL", "HIGH"
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class AppConfig {

    /** Default window width. */
    public static final int DEFAULT_WINDOW_WIDTH = 1024;

    /** Default window height. */
    public static final int DEFAULT_WINDOW_HEIGHT = 768;

    /** Default target FPS. */
    public static final int DEFAULT_FPS = 60;

    /** Default quality setting. */
    public static final String DEFAULT_QUALITY = "NORMAL";

    /** Default language. */
    public static final String DEFAULT_LANGUAGE = "zh";

    /** Default theme. */
    public static final String DEFAULT_THEME = "light";

    /** Default performance mode. */
    public static final String DEFAULT_PERFORMANCE_MODE = "NORMAL";

    /** Default window resizable. */
    public static final boolean DEFAULT_RESIZABLE = true;

    // ------------------------------------------------------------------
    // Window settings
    // ------------------------------------------------------------------

    private int windowWidth = DEFAULT_WINDOW_WIDTH;
    private int windowHeight = DEFAULT_WINDOW_HEIGHT;
    private boolean windowResizable = DEFAULT_RESIZABLE;

    // ------------------------------------------------------------------
    // Render settings
    // ------------------------------------------------------------------

    private String renderQuality = DEFAULT_QUALITY;
    private int targetFPS = DEFAULT_FPS;

    // ------------------------------------------------------------------
    // UI settings
    // ------------------------------------------------------------------

    private String language = DEFAULT_LANGUAGE;
    private String theme = DEFAULT_THEME;

    // ------------------------------------------------------------------
    // Performance settings
    // ------------------------------------------------------------------

    private String performanceMode = DEFAULT_PERFORMANCE_MODE;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * Create an AppConfig with default values.
     */
    public AppConfig() {
    }

    /**
     * Create an AppConfig with the given window size.
     */
    public AppConfig(int width, int height) {
        this.windowWidth = Math.max(400, width);
        this.windowHeight = Math.max(300, height);
    }

    // ------------------------------------------------------------------
    // Window accessors
    // ------------------------------------------------------------------

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int width) {
        this.windowWidth = Math.max(400, width);
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int height) {
        this.windowHeight = Math.max(300, height);
    }

    public boolean isWindowResizable() {
        return windowResizable;
    }

    public void setWindowResizable(boolean resizable) {
        this.windowResizable = resizable;
    }

    // ------------------------------------------------------------------
    // Render accessors
    // ------------------------------------------------------------------

    public String getRenderQuality() {
        return renderQuality;
    }

    public void setRenderQuality(String quality) {
        if (quality == null) {
            throw new IllegalArgumentException("renderQuality cannot be null");
        }
        String upper = quality.toUpperCase();
        if (!upper.equals("LOW") && !upper.equals("NORMAL") && !upper.equals("HIGH")) {
            throw new IllegalArgumentException(
                    "renderQuality must be LOW, NORMAL, or HIGH, got: " + quality);
        }
        this.renderQuality = upper;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public void setTargetFPS(int fps) {
        this.targetFPS = Math.max(1, Math.min(120, fps));
    }

    // ------------------------------------------------------------------
    // UI accessors
    // ------------------------------------------------------------------

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        if (language == null) {
            throw new IllegalArgumentException("language cannot be null");
        }
        this.language = language.toLowerCase();
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        if (theme == null) {
            throw new IllegalArgumentException("theme cannot be null");
        }
        String upper = theme.toUpperCase();
        if (!upper.equals("LIGHT") && !upper.equals("DARK")) {
            throw new IllegalArgumentException(
                    "theme must be LIGHT or DARK, got: " + theme);
        }
        this.theme = upper;
    }

    // ------------------------------------------------------------------
    // Performance accessors
    // ------------------------------------------------------------------

    public String getPerformanceMode() {
        return performanceMode;
    }

    public void setPerformanceMode(String mode) {
        if (mode == null) {
            throw new IllegalArgumentException("performanceMode cannot be null");
        }
        String upper = mode.toUpperCase();
        if (!upper.equals("COMPATIBILITY") && !upper.equals("NORMAL")
                && !upper.equals("HIGH")) {
            throw new IllegalArgumentException(
                    "performanceMode must be COMPATIBILITY, NORMAL, or HIGH, got: " + mode);
        }
        this.performanceMode = upper;
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    /**
     * Get a human-readable summary.
     */
    public String toSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("AppConfig {\n");
        sb.append("  window: ").append(windowWidth).append("x").append(windowHeight)
          .append(", resizable=").append(windowResizable).append("\n");
        sb.append("  render: quality=").append(renderQuality)
          .append(", fps=").append(targetFPS).append("\n");
        sb.append("  ui: language=").append(language)
          .append(", theme=").append(theme).append("\n");
        sb.append("  performance: mode=").append(performanceMode).append("\n");
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        return toSummary();
    }
}
