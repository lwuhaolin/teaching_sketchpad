package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for animation sequences.
 *
 * Stores animation sequence data including:
 *   - Sequence name and description
 *   - List of animation items (step name, animation type, parameters)
 *   - Face animation data for unfold/explode animations
 *
 * On load, the AnimationRegistry uses this data to recreate
 * AnimationSequence with the appropriate Animation objects.
 *
 * Example JSON representation:
 * <pre>
 * {
 *   "name": "Cylinder Unfold",
 *   "description": "Unfolds cylinder into 2D net",
 *   "items": [
 *     {
 *       "name": "Rotate",
 *       "type": "ROTATE",
 *       "targetId": "cylinder001",
 *       "from": [0, 0, 0],
 *       "to": [0, 180, 0],
 *       "duration": 1.0,
 *       "interpolator": "LINEAR"
 *     },
 *     {
 *       "name": "Unfold",
 *       "type": "UNFOLD",
 *       "targetId": "cylinder001",
 *       "unfoldType": "CYLINDER",
 *       "duration": 2.0,
 *       "interpolator": "EASE"
 *     }
 *   ]
 * }
 * </pre>
 *
 * Not thread-safe.
 */
public class AnimationSequenceData {

    private String name;
    private String description;
    private final List<AnimationItemData> items;

    /**
     * Create an empty AnimationSequenceData.
     */
    public AnimationSequenceData() {
        this.name = "";
        this.description = "";
        this.items = new ArrayList<>();
    }

    /**
     * Create an AnimationSequenceData with the given name.
     *
     * @param name sequence name
     */
    public AnimationSequenceData(String name) {
        this();
        this.name = name != null ? name : "";
    }

    // ------------------------------------------------------------------
    // Item management
    // ------------------------------------------------------------------

    public void addItem(AnimationItemData item) {
        if (item != null) {
            items.add(item);
        }
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public List<AnimationItemData> getItems() {
        return new ArrayList<>(items);
    }

    public int getItemCount() {
        return items.size();
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description : "";
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
        AnimationSequenceData that = (AnimationSequenceData) o;
        return name.equals(that.name) && items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + items.hashCode();
    }

    @Override
    public String toString() {
        return "AnimationSequenceData{name='" + name + "', items=" + items.size() + "}";
    }
}
