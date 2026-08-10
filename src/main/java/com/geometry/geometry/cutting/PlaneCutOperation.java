package com.geometry.geometry.cutting;

import com.geometry.core.mesh.Mesh;
import com.geometry.geometry.operation.GeometryOperation;
import com.geometry.geometry.operation.OperationResult;

/**
 * Phase 08 - Plane cutting operation.
 *
 * Implements {@link GeometryOperation} to cut a mesh with a plane.
 * This is the primary operation for the Cut System.
 *
 * Usage:
 * <pre>
 *   Plane plane = new Plane(Vec3.UNIT_Y, 0f);
 *   PlaneCutOperation op = new PlaneCutOperation(plane);
 *   OperationResult result = op.execute(mesh);
 *   Mesh[] parts = result.getMeshes();
 * </pre>
 *
 * The original mesh is never modified. The operation produces new Mesh
 * objects representing the parts on each side of the cutting plane.
 */
public class PlaneCutOperation implements GeometryOperation {

    private final Plane plane;

    /**
     * Create a plane cut operation.
     *
     * @param plane the cutting plane
     * @throws IllegalArgumentException if plane is null
     */
    public PlaneCutOperation(Plane plane) {
        if (plane == null) {
            throw new IllegalArgumentException("Plane cannot be null");
        }
        this.plane = plane;
    }

    /**
     * Return the cutting plane used by this operation.
     */
    public Plane getPlane() {
        return plane;
    }

    @Override
    public OperationResult execute(Mesh mesh) {
        return MeshCutter.cut(mesh, plane);
    }

    @Override
    public String toString() {
        return "PlaneCutOperation{plane=" + plane + "}";
    }
}
