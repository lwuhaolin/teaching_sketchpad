package com.geometry.runtime.logging;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Phase 12 - Application logger.
 *
 * Provides structured logging with levels:
 *   INFO    - normal startup and operation messages
 *   WARN    - non-fatal warnings
 *   ERROR   - error conditions
 *   DEBUG   - detailed diagnostic information
 *
 * Logs are written to both console (stderr for WARN/ERROR) and
 * a log file. Log file path is configurable.
 *
 * Thread-safe for concurrent logging.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class Logger {

    /** Log level: informational. */
    public static final String INFO = "INFO";

    /** Log level: warning. */
    public static final String WARN = "WARN";

    /** Log level: error. */
    public static final String ERROR = "ERROR";

    /** Log level: debug. */
    public static final String DEBUG = "DEBUG";

    /** Default log file name. */
    public static final String DEFAULT_LOG_FILE = "logs/application.log";

    /** Date format for log entries. */
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

    /** Minimum log level to output. */
    private final String minLevel;

    /** Log file writer, or null if logging to file is disabled. */
    private Writer fileWriter;

    /** Lock for thread-safe logging. */
    private final Object lock = new Object();

    /**
     * Create a Logger that writes to the default log file.
     * Minimum level is INFO.
     */
    public Logger() {
        this(INFO, null);
    }

    /**
     * Create a Logger with the given minimum level and log file.
     *
     * @param minLevel minimum log level to output (INFO, WARN, ERROR, DEBUG)
     * @param logFile  log file path, or null to disable file logging
     */
    public Logger(String minLevel, String logFile) {
        this.minLevel = minLevel;
        if (logFile != null && !logFile.isEmpty()) {
            try {
                File dir = new File(logFile).getParentFile();
                if (dir != null && !dir.exists()) {
                    dir.mkdirs();
                }
                this.fileWriter = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(logFile, true),
                                StandardCharsets.UTF_8));
            } catch (IOException e) {
                this.fileWriter = null;
            }
        } else {
            this.fileWriter = null;
        }
    }

    // ------------------------------------------------------------------
    // Logging methods
    // ------------------------------------------------------------------

    /**
     * Log an INFO message.
     */
    public void info(String message) {
        log(INFO, message, null);
    }

    /**
     * Log a WARN message.
     */
    public void warn(String message) {
        log(WARN, message, null);
    }

    /**
     * Log an ERROR message.
     */
    public void error(String message) {
        log(ERROR, message, null);
    }

    /**
     * Log an ERROR message with an exception.
     */
    public void error(String message, Throwable throwable) {
        log(ERROR, message, throwable);
    }

    /**
     * Log a DEBUG message.
     */
    public void debug(String message) {
        log(DEBUG, message, null);
    }

    /**
     * Log a message with the given level.
     */
    public void log(String level, String message) {
        log(level, message, null);
    }

    /**
     * Internal log method. Thread-safe.
     */
    private void log(String level, String message, Throwable throwable) {
        if (getLevelPriority(level) < getLevelPriority(minLevel)) {
            return;
        }
        String timestamp = DATE_FORMAT.format(new Date());
        String threadName = Thread.currentThread().getName();
        String formatted = String.format("[%s] [%s] [%s] %s",
                timestamp, level, threadName, message);

        synchronized (lock) {
            // Console output
            if (ERROR.equals(level) || WARN.equals(level)) {
                System.err.println(formatted);
                if (throwable != null) {
                    throwable.printStackTrace(System.err);
                }
            } else {
                System.out.println(formatted);
                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }

            // File output
            if (fileWriter != null) {
                try {
                    fileWriter.write(formatted + "\n");
                    if (throwable != null) {
                        StringWriter sw = new StringWriter();
                        throwable.printStackTrace(new PrintWriter(sw));
                        fileWriter.write(sw.toString() + "\n");
                    }
                    fileWriter.flush();
                } catch (IOException e) {
                    // Log file write failure - don't throw
                    System.err.println("[Logger] Failed to write to log file: " + e.getMessage());
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Level priority (higher = more severe)
    // ------------------------------------------------------------------

    private int getLevelPriority(String level) {
        if (level == null) return -1;
        switch (level) {
            case DEBUG: return 0;
            case INFO: return 1;
            case WARN: return 2;
            case ERROR: return 3;
            default: return -1;
        }
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Close the logger and release resources.
     */
    public void close() {
        synchronized (lock) {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
}
