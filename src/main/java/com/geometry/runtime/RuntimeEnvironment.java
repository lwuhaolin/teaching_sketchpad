package com.geometry.runtime;

/**
 * Phase 12 - Runtime environment information holder.
 *
 * Captures system-level runtime information detected at startup:
 *   - OS type and version
 *   - Java version
 *   - GPU / OpenGL information
 *   - Memory and CPU info
 *
 * Read-only after construction.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class RuntimeEnvironment {

    /** Operating system name (e.g. "Windows 10"). */
    private final String osName;

    /** Operating system version (e.g. "10.0"). */
    private final String osVersion;

    /** Operating system architecture (e.g. "amd64"). */
    private final String osArch;

    /** Java runtime version string (e.g. "1.8.0_292"). */
    private final String javaVersion;

    /** Java vendor (e.g. "Oracle Corporation"). */
    private final String javaVendor;

    /** JVM architecture (e.g. "x86_64"). */
    private final String javaArch;

    /** OpenGL version string reported by the driver. Empty if unavailable. */
    private final String openGlVersion;

    /** OpenGL vendor string. Empty if unavailable. */
    private final String openGlVendor;

    /** OpenGL renderer string. Empty if unavailable. */
    private final String openGlRenderer;

    /** Available physical memory in bytes (negative if unknown). */
    private final long availableMemoryBytes;

    /** Total physical memory in bytes (negative if unknown). */
    private final long totalMemoryBytes;

    /** Number of available processors. */
    private final int processorCount;

    /** Whether OpenGL 2.1+ is available. */
    private final boolean openGlCompatible;

    /**
     * Create a RuntimeEnvironment from system properties.
     */
    public RuntimeEnvironment() {
        this.osName = System.getProperty("os.name", "Unknown");
        this.osVersion = System.getProperty("os.version", "Unknown");
        this.osArch = System.getProperty("os.arch", "Unknown");
        this.javaVersion = System.getProperty("java.version", "Unknown");
        this.javaVendor = System.getProperty("java.vendor", "Unknown");
        this.javaArch = System.getProperty("java.arch", "Unknown");
        this.openGlVersion = detectOpenGLVersion();
        this.openGlVendor = detectOpenGLVendor();
        this.openGlRenderer = detectOpenGLRenderer();
        this.openGlCompatible = checkOpenGLCompatibility();
        this.availableMemoryBytes = detectAvailableMemory();
        this.totalMemoryBytes = detectTotalMemory();
        this.processorCount = Runtime.getRuntime().availableProcessors();
    }

    // ------------------------------------------------------------------
    // Detection helpers
    // ------------------------------------------------------------------

    private String detectOpenGLVersion() {
        try {
            return org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VERSION);
        } catch (Throwable t) {
            return "";
        }
    }

    private String detectOpenGLVendor() {
        try {
            return org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_VENDOR);
        } catch (Throwable t) {
            return "";
        }
    }

    private String detectOpenGLRenderer() {
        try {
            return org.lwjgl.opengl.GL11.glGetString(org.lwjgl.opengl.GL11.GL_RENDERER);
        } catch (Throwable t) {
            return "";
        }
    }

    private boolean checkOpenGLCompatibility() {
        String version = openGlVersion;
        if (version == null || version.isEmpty()) {
            return false;
        }
        try {
            // Extract major version from "3.3.0 ..." or "2.1.0 ..."
            String majorStr = version.substring(0, Math.min(3, version.length()));
            int major = Integer.parseInt(majorStr);
            return major >= 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private long detectAvailableMemory() {
        try {
            return Runtime.getRuntime().freeMemory();
        } catch (Throwable e) {
            return -1;
        }
    }

    private long detectTotalMemory() {
        try {
            return Runtime.getRuntime().totalMemory();
        } catch (Throwable e) {
            return -1;
        }
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public String getOsName() {
        return osName;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getOsArch() {
        return osArch;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaVendor() {
        return javaVendor;
    }

    public String getJavaArch() {
        return javaArch;
    }

    public String getOpenGlVersion() {
        return openGlVersion;
    }

    public String getOpenGlVendor() {
        return openGlVendor;
    }

    public String getOpenGlRenderer() {
        return openGlRenderer;
    }

    public long getAvailableMemoryBytes() {
        return availableMemoryBytes;
    }

    public long getTotalMemoryBytes() {
        return totalMemoryBytes;
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public boolean isOpenGlCompatible() {
        return openGlCompatible;
    }

    /**
     * Get a human-readable summary of the environment.
     */
    public String toSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("OS: ").append(osName).append(" ").append(osVersion)
          .append(" (").append(osArch).append(")\n");
        sb.append("Java: ").append(javaVersion).append(" (").append(javaVendor)
          .append(", ").append(javaArch).append(")\n");
        sb.append("OpenGL: ").append(openGlVersion != null ? openGlVersion : "N/A");
        if (openGlRenderer != null && !openGlRenderer.isEmpty()) {
            sb.append(" [").append(openGlRenderer).append("]");
        }
        sb.append("\n");
        sb.append("Memory: ").append(formatBytes(availableMemoryBytes))
          .append(" / ").append(formatBytes(totalMemoryBytes)).append("\n");
        sb.append("CPUs: ").append(processorCount).append("\n");
        sb.append("OpenGL Compatible: ").append(openGlCompatible);
        return sb.toString();
    }

    private String formatBytes(long bytes) {
        if (bytes < 0) {
            return "Unknown";
        }
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
