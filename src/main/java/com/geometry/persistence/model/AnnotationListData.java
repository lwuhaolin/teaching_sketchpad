package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for annotation persistence (text, arrow, highlight).
 *
 * Aggregates all annotation data for a project.
 *
 * Not thread-safe.
 */
public class AnnotationListData {

    private final List<AnnotationData> annotations;

    /**
     * Create an empty AnnotationListData.
     */
    public AnnotationListData() {
        this.annotations = new ArrayList<>();
    }

    public void addAnnotation(AnnotationData annotation) {
        if (annotation != null) {
            annotations.add(annotation);
        }
    }

    public void clear() {
        annotations.clear();
    }

    public List<AnnotationData> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    public int getCount() {
        return annotations.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnnotationListData that = (AnnotationListData) o;
        return annotations.equals(that.annotations);
    }

    @Override
    public int hashCode() {
        return annotations.hashCode();
    }

    @Override
    public String toString() {
        return "AnnotationListData{count=" + annotations.size() + "}";
    }
}
