package com.geometry.teaching;

/**
 * Phase 07 - Marker interface for teaching-specific actions.
 *
 * TeachingAction extends the base {@link com.geometry.interaction.action.Action}
 * and is used to represent actions that have teaching semantics, such as:
 *   - Revealing an annotation
 *   - Hiding an object
 *   - Starting/stopping an animation (Phase 09)
 *   - Advancing to the next lesson step
 *
 * This interface is a marker — it does not add methods yet, but it
 * allows the TeachingManager to distinguish teaching actions from
 * regular tool actions.
 *
 * Implementation note: Future phases will add concrete subclasses
 * such as RevealAnnotationAction, HideObjectAction, etc.
 */
public interface TeachingAction extends com.geometry.interaction.action.Action {
    // Marker interface for teaching-specific actions
    // Concrete implementations will be added in future phases
}
