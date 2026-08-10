package com.geometry.geometry.operation;

import com.geometry.core.mesh.Mesh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Phase 08 - Result of a geometry operation.
 *
 * Holds the output meshes, a success flag, and an optional message.
 * A successful cut always produces at least one mesh; an empty result
 * means the operation could not produce valid output (e.g., mesh fully on one side).
 */
public class OperationResult {

    private final List<Mesh> meshes;
    private final boolean success;
    private final String message;

    private OperationResult(List<Mesh> meshes, boolean success, String message) {
        this.meshes = meshes != null ? new ArrayList<>(meshes) : new ArrayList<>();
        this.success = success;
        this.message = message;
    }

    /**
     * Create a successful result with the given meshes.
     */
    public static OperationResult success(Mesh... meshes) {
        return new OperationResult(
                java.util.Arrays.asList(meshes),
                true,
                null
        );
    }

    /**
     * Create a failed result with a message.
     */
    public static OperationResult failure(String message) {
        return new OperationResult(
                Collections.emptyList(),
                false,
                message
        );
    }

    /**
     * Create a successful result with zero meshes (no-op outcome).
     */
    public static OperationResult empty() {
        return success();
    }

    /**
     * Return the output meshes. Immutable view.
     */
    public List<Mesh> getMeshes() {
        return Collections.unmodifiableList(meshes);
    }

    /**
     * Return the first output mesh, or null if empty.
     */
    public Mesh getMesh() {
        return meshes.isEmpty() ? null : meshes.get(0);
    }

    /**
     * Return the number of output meshes.
     */
    public int getMeshCount() {
        return meshes.size();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "OperationResult{success=" + success + ", meshes=" + meshes.size()
                + (message != null ? ", message=\"" + message + "\"" : "") + "}";
    }
}
