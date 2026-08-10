package com.geometry.runtime;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 12 - Startup system checker.
 *
 * Checks that the runtime environment meets minimum requirements
 * before the application proceeds with initialization.
 *
 * Checks performed:
 *   - Java version >= 1.8
 *   - OpenGL 2.1+ support (or software fallback flag)
 *   - Required directory writability
 *   - Sufficient memory
 *
 * Results are collected in a SystemCheckReport.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class SystemChecker {

    /** Minimum Java major version. */
    private static final int MIN_JAVA_MAJOR = 8;

    /** Minimum OpenGL major version. */
    private static final int MIN_OPENGL_MAJOR = 2;

    /** Minimum free memory in bytes (128 MB). */
    private static final long MIN_FREE_MEMORY_BYTES = 128 * 1024 * 1024;

    /**
     * Run all checks and return a report.
     *
     * @param environment the detected runtime environment
     * @param checkDirs   directories to verify writability for
     * @return a SystemCheckReport describing the results
     */
    public SystemCheckReport check(RuntimeEnvironment environment, List<File> checkDirs) {
        SystemCheckReport report = new SystemCheckReport();

        checkJavaVersion(environment, report);
        checkOpenGL(environment, report);
        checkMemory(environment, report);
        checkDirectories(checkDirs, report);

        return report;
    }

    /**
     * Run all checks with an empty directory list.
     */
    public SystemCheckReport check(RuntimeEnvironment environment) {
        return check(environment, new ArrayList<File>());
    }

    // ------------------------------------------------------------------
    // Individual checks
    // ------------------------------------------------------------------

    private void checkJavaVersion(RuntimeEnvironment env, SystemCheckReport report) {
        String javaVer = env.getJavaVersion();
        if (javaVer == null || javaVer.isEmpty()) {
            report.addIssue(SystemCheckIssue.WARNING, "Java version string is empty");
            return;
        }
        try {
            // Handle versions like "1.8.0_292" or "11.0.11"
            int major;
            if (javaVer.startsWith("1.")) {
                // Legacy format: 1.8.x
                major = Integer.parseInt(javaVer.substring(2, 3));
            } else {
                major = Integer.parseInt(javaVer.substring(0, javaVer.indexOf('.')));
            }
            if (major < MIN_JAVA_MAJOR) {
                report.addIssue(SystemCheckIssue.ERROR,
                        "Java version " + javaVer + " is below minimum " + MIN_JAVA_MAJOR);
            } else {
                report.addIssue(SystemCheckIssue.PASS,
                        "Java version " + javaVer + " OK (min " + MIN_JAVA_MAJOR + ")");
            }
        } catch (NumberFormatException e) {
            report.addIssue(SystemCheckIssue.WARNING,
                    "Could not parse Java version: " + javaVer);
        }
    }

    private void checkOpenGL(RuntimeEnvironment env, SystemCheckReport report) {
        if (env.isOpenGlCompatible()) {
            report.addIssue(SystemCheckIssue.PASS,
                    "OpenGL compatible (version: " + env.getOpenGlVersion() + ")");
        } else {
            report.addIssue(SystemCheckIssue.WARNING,
                    "OpenGL 2.1+ not detected; application will use compatibility fallback");
        }
    }

    private void checkMemory(RuntimeEnvironment env, SystemCheckReport report) {
        long free = env.getAvailableMemoryBytes();
        if (free < 0) {
            report.addIssue(SystemCheckIssue.WARNING, "Could not detect available memory");
            return;
        }
        if (free < MIN_FREE_MEMORY_BYTES) {
            report.addIssue(SystemCheckIssue.WARNING,
                    "Available memory " + formatBytes(free)
                            + " is below recommended " + formatBytes(MIN_FREE_MEMORY_BYTES));
        } else {
            report.addIssue(SystemCheckIssue.PASS,
                    "Memory OK: " + formatBytes(free) + " available");
        }
    }

    private void checkDirectories(List<File> dirs, SystemCheckReport report) {
        if (dirs == null || dirs.isEmpty()) {
            report.addIssue(SystemCheckIssue.PASS, "No directory checks requested");
            return;
        }
        for (File dir : dirs) {
            if (dir == null) {
                report.addIssue(SystemCheckIssue.WARNING, "Null directory in check list");
                continue;
            }
            if (!dir.exists()) {
                report.addIssue(SystemCheckIssue.WARNING,
                        "Directory does not exist: " + dir.getPath());
            } else if (!dir.canWrite()) {
                report.addIssue(SystemCheckIssue.ERROR,
                        "Directory not writable: " + dir.getPath());
            } else {
                report.addIssue(SystemCheckIssue.PASS,
                        "Directory OK: " + dir.getPath());
            }
        }
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
