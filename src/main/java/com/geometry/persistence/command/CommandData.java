package com.geometry.persistence.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for a command in the undo/redo history.
 *
 * Stores the command type, target object ID, and the before/after
 * state needed to undo/redo the operation.
 *
 * Design:
 *   - Only stores metadata needed for undo/redo
 *   - Does NOT store full mesh data — only parameter changes
 *   - Prepared for future undo/redo implementation
 *
 * Example:
 *   <pre>
 *   {
 *     "type": "MOVE",
 *     "targetId": "cube001",
 *     "before": {"position": [0,0,0]},
 *     "after": {"position": [5,0,0]}
 *   }
 *   </pre>
 *
 * Not thread-safe.
 */
public class CommandData {

    /** Command type constants. */
    public enum CommandType {
        CREATE,
        DELETE,
        MOVE,
        ROTATE,
        SCALE,
        TRANSFORM,
        ANNOTATE,
        TEACHING_STEP
    }

    private final String id;
    private final CommandType type;
    private String targetId;
    private java.util.Map<String, Object> beforeState;
    private java.util.Map<String, Object> afterState;
    private long timestamp;

    /**
     * Create a CommandData.
     *
     * @param id       unique command ID
     * @param type     command type
     * @param targetId target object ID (may be null)
     * @param beforeState state before the command (may be null)
     * @param afterState  state after the command (may be null)
     */
    public CommandData(String id, CommandType type, String targetId,
                       java.util.Map<String, Object> beforeState,
                       java.util.Map<String, Object> afterState) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("CommandType cannot be null");
        }
        this.id = id;
        this.type = type;
        this.targetId = targetId;
        this.beforeState = beforeState != null ? new java.util.HashMap<>(beforeState) : new java.util.HashMap<>();
        this.afterState = afterState != null ? new java.util.HashMap<>(afterState) : new java.util.HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public CommandType getType() {
        return type;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public java.util.Map<String, Object> getBeforeState() {
        return new java.util.HashMap<>(beforeState);
    }

    public java.util.Map<String, Object> getAfterState() {
        return new java.util.HashMap<>(afterState);
    }

    public long getTimestamp() {
        return timestamp;
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
        CommandData that = (CommandData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "CommandData{id='" + id + "', type=" + type
                + ", target=" + targetId + ", time=" + timestamp + "}";
    }
}
