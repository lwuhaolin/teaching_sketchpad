package com.geometry.ui;

/**
 * Phase 11 - UI-level teaching mode.
 *
 * Controls what the UI panel system exposes:
 *   - TEACHER: full editing controls visible
 *   - STUDENT: read-only view, some controls hidden
 *   - EXAM: constrained mode, limited annotations
 *   - FREE: no restrictions, full controls visible
 *
 * This is distinct from {@link com.geometry.teaching.TeachingMode} which
 * controls the teaching system's internal logic. This enum controls
 * what the UI displays and allows.
 *
 * Not thread-safe.
 */
public enum UITeachingMode {

    /**
     * Teacher mode: all panels fully interactive.
     */
    TEACHER,

    /**
     * Student mode: reading and guided interaction only.
     */
    STUDENT,

    /**
     * Exam mode: restricted annotations and tool usage.
     */
    EXAM,

    /**
     * Free mode: no restrictions, similar to TEACHER without lessons.
     */
    FREE
}
