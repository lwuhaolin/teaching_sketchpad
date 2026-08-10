package com.geometry.runtime.logging;

import com.geometry.runtime.RuntimeEnvironment;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Phase 12 - Automatic crash reporter.
 *
 * Catches uncaught exceptions and writes a crash report to
 * logs/crash.log including:
 *   - Timestamp
 *   - Exception type and message
 *   - Full stack trace
 *   - System/environment information
 *   - OpenGL information
 *
 * Install via:
 *   CrashReporter.install(globalHandler);
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class CrashReporter {

    /** Default crash log file name. */
    public static final String DEFAULT_CRASH_LOG = "logs/crash.log";

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    private final Logger logger;
    private final String crashLogPath;
    private final RuntimeEnvironment environment;

    /** Whether the global uncaught exception handler has been installed. */
    private volatile boolean installed;

    /**
     * Create a CrashReporter with default log path.
     */
    public CrashReporter() {
        this(DEFAULT_CRASH_LOG);
    }

    /**
     * Create a CrashReporter with the specified log path.
     *
     * @param crashLogPath path to the crash log file
     */
    public CrashReporter(String crashLogPath) {
        this(crashLogPath, new Logger(), new RuntimeEnvironment());
    }

    /**
     * Create a CrashReporter with explicit dependencies (for testing).
     */
    public CrashReporter(String crashLogPath, Logger logger, RuntimeEnvironment environment) {
        this.crashLogPath = crashLogPath;
        this.logger = logger;
        this.environment = environment;
        this.installed = false;
    }

    // ------------------------------------------------------------------
    // Installation
    // ------------------------------------------------------------------

    /**
     * Install as the global uncaught exception handler.
     * After this call, all uncaught exceptions will be logged.
     */
    public void install() {
        if (installed) {
            return;
        }
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                reportCrash(t, e);
            }
        });
        installed = true;
    }

    /**
     * Check if the reporter has been installed.
     */
    public boolean isInstalled() {
        return installed;
    }

    // ------------------------------------------------------------------
    // Reporting
    // ------------------------------------------------------------------

    /**
     * Report a crash (called by the uncaught exception handler or manually).
     *
     * @param thread the thread where the crash occurred
     * @param throwable the exception that caused the crash
     */
    public void reportCrash(Thread thread, Throwable throwable) {
        if (throwable == null) {
            return;
        }
        try {
            File dir = new File(crashLogPath).getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }

            try (Writer writer = new OutputStreamWriter(
                    new FileOutputStream(crashLogPath, true),
                    StandardCharsets.UTF_8)) {

                writer.write("=== CRASH REPORT ===\n");
                writer.write("Time: " + DATE_FORMAT.format(new Date()) + "\n");
                writer.write("Thread: " + thread.getName() + "\n");
                writer.write("Exception: " + throwable.getClass().getName()
                        + ": " + throwable.getMessage() + "\n\n");

                writer.write("--- Stack Trace ---\n");
                StringWriter sw = new StringWriter();
                throwable.printStackTrace(new PrintWriter(sw));
                writer.write(sw.toString());
                writer.write("\n");

                writer.write("--- System Information ---\n");
                writer.write(environment.toSummary());
                writer.write("\n");

                writer.write("--- JVM Arguments ---\n");
                String[] args = ManagementFactoryHelper.getJVMArguments();
                for (String arg : args) {
                    writer.write("  " + arg + "\n");
                }
                writer.write("\n");

                writer.write("=== END REPORT ===\n\n");
                writer.flush();
            }

            logger.error("Crash reported to " + crashLogPath + ": "
                    + throwable.getClass().getName() + " - " + throwable.getMessage());

        } catch (IOException e) {
            System.err.println("Failed to write crash report: " + e.getMessage());
        }
    }

    /**
     * Manually report a crash without going through the thread handler.
     */
    public void reportCrash(Throwable throwable) {
        reportCrash(Thread.currentThread(), throwable);
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    /**
     * Get the crash log file path.
     */
    public String getCrashLogPath() {
        return crashLogPath;
    }
}

/**
 * Helper to access JVM arguments (Java 8 compatible).
 */
class ManagementFactoryHelper {
    static String[] getJVMArguments() {
        try {
            java.lang.management.RuntimeMXBean rb =
                    java.lang.management.ManagementFactory.getRuntimeMXBean();
            return rb.getInputArguments().toArray(new String[0]);
        } catch (Throwable e) {
            return new String[0];
        }
    }
}
