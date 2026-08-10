package com.geometry.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 12 - Aggregated report from SystemChecker.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class SystemCheckReport {

    private final List<SystemCheckIssue> issues = new ArrayList<>();
    private boolean passed;

    /**
     * Create an empty report.
     */
    public SystemCheckReport() {
        this.passed = true;
    }

    /**
     * Add a check issue to the report.
     */
    public void addIssue(String result, String message) {
        issues.add(new SystemCheckIssue(result, message));
        if (SystemCheckIssue.ERROR.equals(result)) {
            this.passed = false;
        }
    }

    /**
     * Get all issues in order.
     */
    public List<SystemCheckIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    /**
     * Get only ERROR-level issues.
     */
    public List<SystemCheckIssue> getErrors() {
        List<SystemCheckIssue> errors = new ArrayList<>();
        for (SystemCheckIssue issue : issues) {
            if (issue.isError()) {
                errors.add(issue);
            }
        }
        return errors;
    }

    /**
     * Get only WARNING-level issues.
     */
    public List<SystemCheckIssue> getWarnings() {
        List<SystemCheckIssue> warnings = new ArrayList<>();
        for (SystemCheckIssue issue : issues) {
            if (issue.isWarning()) {
                warnings.add(issue);
            }
        }
        return warnings;
    }

    /**
     * Whether the report has no ERROR issues.
     */
    public boolean isPassed() {
        return passed;
    }

    /**
     * Get a summary string.
     */
    public String toSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("System Check Report: ").append(passed ? "PASSED" : "FAILED").append("\n");
        for (SystemCheckIssue issue : issues) {
            sb.append("  [").append(issue.getResult()).append("] ").append(issue.getMessage())
              .append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toSummary();
    }
}
