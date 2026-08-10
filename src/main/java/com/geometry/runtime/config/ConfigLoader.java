package com.geometry.runtime.config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Phase 12 - Loads and saves AppConfig from/to a JSON-like properties file.
 *
 * Uses a simple key=value format (not full JSON) to avoid external
 * dependencies and ensure Java 8 / Windows 7 compatibility.
 *
 * File format example:
 *   # comment line
 *   window.width=1280
 *   window.height=720
 *   render.quality=HIGH
 *   render.fps=60
 *   ui.language=zh
 *   ui.theme=dark
 *   performance.mode=NORMAL
 *
 * If the config file does not exist, AppConfig defaults are used.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class ConfigLoader {

    /** Default config file name. */
    public static final String DEFAULT_CONFIG_FILE = "config.properties";

    /**
     * Load configuration from the given file.
     *
     * @param configFile the config file path
     * @return the loaded AppConfig (never null)
     */
    public AppConfig load(File configFile) {
        AppConfig config = new AppConfig();
        if (configFile == null || !configFile.exists()) {
            return config;
        }
        try (InputStream is = new FileInputStream(configFile)) {
            loadFromStream(config, is);
        } catch (IOException e) {
            // Return defaults on read failure
            return config;
        }
        return config;
    }

    /**
     * Load configuration from the given file path string.
     *
     * @param path the config file path
     * @return the loaded AppConfig (never null)
     */
    public AppConfig load(String path) {
        if (path == null || path.isEmpty()) {
            return new AppConfig();
        }
        return load(new File(path));
    }

    /**
     * Load configuration from a classpath resource.
     *
     * @param resourceName the classpath resource name (e.g. "config/default.properties")
     * @return the loaded AppConfig (never null)
     */
    public AppConfig loadFromClasspath(String resourceName) {
        AppConfig config = new AppConfig();
        if (resourceName == null || resourceName.isEmpty()) {
            return config;
        }
        InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            return config;
        }
        try {
            loadFromStream(config, is);
        } catch (IOException e) {
            // Ignore, return defaults
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {
            }
        }
        return config;
    }

    /**
     * Save the given config to the specified file.
     *
     * @param config     the config to save (must not be null)
     * @param configFile the output file path (must not be null)
     * @return true if saved successfully
     */
    public boolean save(AppConfig config, File configFile) {
        if (config == null || configFile == null) {
            return false;
        }
        try {
            File parent = configFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (OutputStream os = new FileOutputStream(configFile);
                 Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                saveToWriter(config, writer);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Save the given config to the specified path string.
     */
    public boolean save(AppConfig config, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        return save(config, new File(path));
    }

    // ------------------------------------------------------------------
    // Internal parsing
    // ------------------------------------------------------------------

    private void loadFromStream(AppConfig config, InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int eqIndex = line.indexOf('=');
            if (eqIndex < 0) {
                continue;
            }
            String key = line.substring(0, eqIndex).trim();
            String value = line.substring(eqIndex + 1).trim();
            applyKey(config, key, value);
        }
    }

    private void applyKey(AppConfig config, String key, String value) {
        switch (key) {
            case "window.width":
                try {
                    config.setWindowWidth(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                }
                break;
            case "window.height":
                try {
                    config.setWindowHeight(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                }
                break;
            case "window.resizable":
                config.setWindowResizable(Boolean.parseBoolean(value));
                break;
            case "render.quality":
                config.setRenderQuality(value);
                break;
            case "render.fps":
                try {
                    config.setTargetFPS(Integer.parseInt(value));
                } catch (NumberFormatException ignored) {
                }
                break;
            case "ui.language":
                config.setLanguage(value);
                break;
            case "ui.theme":
                config.setTheme(value);
                break;
            case "performance.mode":
                config.setPerformanceMode(value);
                break;
            default:
                // Unknown key, silently ignore
                break;
        }
    }

    private void saveToWriter(AppConfig config, Writer writer) throws IOException {
        Map<String, String> properties = toProperties(config);
        // Sort keys for deterministic output
        java.util.TreeMap<String, String> sorted = new java.util.TreeMap<>(properties);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        writer.flush();
    }

    private Map<String, String> toProperties(AppConfig config) {
        Map<String, String> props = new HashMap<>();
        props.put("window.width", String.valueOf(config.getWindowWidth()));
        props.put("window.height", String.valueOf(config.getWindowHeight()));
        props.put("window.resizable", String.valueOf(config.isWindowResizable()));
        props.put("render.quality", config.getRenderQuality());
        props.put("render.fps", String.valueOf(config.getTargetFPS()));
        props.put("ui.language", config.getLanguage());
        props.put("ui.theme", config.getTheme());
        props.put("performance.mode", config.getPerformanceMode());
        return props;
    }
}
