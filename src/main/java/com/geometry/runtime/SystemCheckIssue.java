package com.geometry.runtime;

/**
 * Phase 12 - Result of a single system check.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class SystemCheckIssue {

    /** Check passed successfully. */
    public static final String PASS = "PASS";

    /** Check produced a warning (non-fatal). */
    public static final String WARNING = "WARNING";

    /** Check failed with an error (fatal). */
    public static final String ERROR = "ERROR";

    /** The check result category. */
    private final String result;

    /** Human-readable message. */
    private final String message;

    /**
     * Create a check issue.
     *
     * @param result  "PASS", "WARNING", or "ERROR"
     * @param message human-readable description
     */
    public SystemCheckIssue(String result, String message) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }
        this.result = result;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPass() {
        return PASS.equals(result);
    }

    public boolean isWarning() {
        return WARNING.equals(result);
    }

    public boolean isError() {
        return ERROR.equals(result);
    }

    @Override
    public String toString() {
        return result + ": " + message;
    }
}
