package com.geometry.persistence.command;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Data model for command history (undo/redo).
 *
 * Stores a list of CommandData objects with an index pointer
 * to support forward/backward navigation.
 *
 * Design:
 *   - Only stores command metadata, not full scene state
 *   - Prepared for future undo/redo implementation
 *   - Not thread-safe
 *
 * Not thread-safe.
 */
public class HistoryData {

    private final List<CommandData> history;
    private int currentIndex;

    /**
     * Create an empty HistoryData.
     */
    public HistoryData() {
        this.history = new ArrayList<>();
        this.currentIndex = -1;
    }

    // ------------------------------------------------------------------
    // History management
    // ------------------------------------------------------------------

    /**
     * Add a command to the history.
     * Discards any forward history when a new command is added.
     *
     * @param command the command to add
     */
    public void addCommand(CommandData command) {
        if (command == null) {
            return;
        }
        // Discard forward history
        while (history.size() > currentIndex + 1) {
            history.remove(history.size() - 1);
        }
        history.add(command);
        currentIndex = history.size() - 1;
    }

    /**
     * Undo the last command.
     *
     * @return the undone command, or null if nothing to undo
     */
    public CommandData undo() {
        if (currentIndex < 0) {
            return null;
        }
        CommandData command = history.get(currentIndex);
        currentIndex--;
        return command;
    }

    /**
     * Redo the last undone command.
     *
     * @return the redone command, or null if nothing to redo
     */
    public CommandData redo() {
        if (currentIndex >= history.size() - 1) {
            return null;
        }
        currentIndex++;
        return history.get(currentIndex);
    }

    /**
     * Get all commands in order.
     */
    public List<CommandData> getCommands() {
        return new ArrayList<>(history);
    }

    public int getSize() {
        return history.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean canUndo() {
        return currentIndex >= 0;
    }

    public boolean canRedo() {
        return currentIndex < history.size() - 1;
    }

    /**
     * Clear all history.
     */
    public void clear() {
        history.clear();
        currentIndex = -1;
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
        HistoryData that = (HistoryData) o;
        return history.equals(that.history);
    }

    @Override
    public int hashCode() {
        return history.hashCode();
    }

    @Override
    public String toString() {
        return "HistoryData{size=" + history.size()
                + ", current=" + currentIndex + "}";
    }
}
