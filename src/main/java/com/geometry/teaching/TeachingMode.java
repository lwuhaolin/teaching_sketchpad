package com.geometry.teaching;

/**
 * Phase 07 - Teaching mode selector.
 *
 * Controls the behaviour of the Teaching System:
 *   TEACHER — full editing and annotation capabilities.
 *   STUDENT — read-only view with guided steps.
 *   EXAM    — constrained mode for assessment.
 *   FREE    — no restrictions, unrestricted interaction.
 *
 * The mode affects which tools are available and whether annotations
 * can be modified by students.
 *
 * Not thread-safe.
 */
public enum TeachingMode {

    /**
     * Teacher mode: all tools and annotations are active.
     */
    TEACHER,

    /**
     * Student mode: guided learning with limited interaction.
     */
    STUDENT,

    /**
     * Exam mode: restricted actions, time-limited steps.
     */
    EXAM,

    /**
     * Free mode: no restrictions, similar to TEACHER but without
     * lesson enforcement.
     */
    FREE
}
