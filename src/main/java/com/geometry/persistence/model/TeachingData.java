package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for the complete teaching system state.
 *
 * Aggregates:
 *   - LessonData (lesson structure and steps)
 *   - AnnotationData (all annotations)
 *   - Teaching mode state
 *
 * Not thread-safe.
 */
public class TeachingData {

    private final List<LessonData> lessons;
    private String currentLessonId;
    private int currentStepNumber;
    private String teachingMode;
    private final List<AnnotationData> annotations;
    private final List<AssistanceData> assistants;

    /**
     * Create an empty TeachingData.
     */
    public TeachingData() {
        this.lessons = new ArrayList<>();
        this.currentLessonId = null;
        this.currentStepNumber = 0;
        this.teachingMode = "FREE";
        this.annotations = new ArrayList<>();
        this.assistants = new ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Lesson management
    // ------------------------------------------------------------------

    public void addLesson(LessonData lesson) {
        if (lesson != null) {
            lessons.add(lesson);
        }
    }

    public LessonData findLesson(String lessonTitle) {
        for (LessonData lesson : lessons) {
            if (lesson.getTitle().equals(lessonTitle)) {
                return lesson;
            }
        }
        return null;
    }

    public void removeLesson(String lessonTitle) {
        lessons.removeIf(l -> l != null && l.getTitle().equals(lessonTitle));
    }

    public List<LessonData> getLessons() {
        return new ArrayList<>(lessons);
    }

    public int getLessonCount() {
        return lessons.size();
    }

    // ------------------------------------------------------------------
    // Annotations
    // ------------------------------------------------------------------

    public void addAnnotation(AnnotationData annotation) {
        if (annotation != null) {
            annotations.add(annotation);
        }
    }

    public void clearAnnotations() {
        annotations.clear();
    }

    public List<AnnotationData> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    public int getAnnotationCount() {
        return annotations.size();
    }

    // ------------------------------------------------------------------
    // Assistants
    // ------------------------------------------------------------------

    public void addAssistant(AssistanceData assistant) {
        if (assistant != null) {
            assistants.add(assistant);
        }
    }

    public void clearAssistants() {
        assistants.clear();
    }

    public List<AssistanceData> getAssistants() {
        return new ArrayList<>(assistants);
    }

    public int getAssistantCount() {
        return assistants.size();
    }

    // ------------------------------------------------------------------
    // State
    // ------------------------------------------------------------------

    public String getCurrentLessonId() {
        return currentLessonId;
    }

    public void setCurrentLessonId(String currentLessonId) {
        this.currentLessonId = currentLessonId;
    }

    public int getCurrentStepNumber() {
        return currentStepNumber;
    }

    public void setCurrentStepNumber(int currentStepNumber) {
        this.currentStepNumber = currentStepNumber;
    }

    public String getTeachingMode() {
        return teachingMode;
    }

    public void setTeachingMode(String teachingMode) {
        this.teachingMode = teachingMode != null ? teachingMode : "FREE";
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TeachingData that = (TeachingData) o;
        return lessons.equals(that.lessons) && annotations.equals(that.annotations);
    }

    @Override
    public int hashCode() {
        return 31 * lessons.hashCode() + annotations.hashCode();
    }

    @Override
    public String toString() {
        return "TeachingData{lessons=" + lessons.size()
                + ", annotations=" + annotations.size()
                + ", mode=" + teachingMode + "}";
    }
}
