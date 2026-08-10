package com.geometry.geometry.operation;

import com.geometry.core.mesh.Mesh;

/**
 * Phase 08 - Interface for all geometry operations.
 *
 * Every geometry operation (cut, fuse, boolean, etc.) implements this interface.
 * Operations are pure: they receive input Mesh, produce new Mesh(es), never
 * mutate the original.
 *
 * Usage pattern:
 *   GeometryOperation op = new PlaneCutOperation(plane);
 *   OperationResult result = op.execute(mesh);
 *   Mesh[] parts = result.getMeshes();
 */
public interface GeometryOperation {

    /**
     * Execute this operation on the given mesh.
     *
     * @param mesh the input mesh (must not be modified in place)
     * @return the operation result containing zero or more new meshes
     * @throws IllegalArgumentException if mesh is null
     */
    OperationResult execute(Mesh mesh);
}
