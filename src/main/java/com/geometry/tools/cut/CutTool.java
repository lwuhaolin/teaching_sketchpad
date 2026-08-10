package com.geometry.tools.cut;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;
import com.geometry.geometry.analysis.Section;
import com.geometry.geometry.analysis.SectionAnalyzer;
import com.geometry.geometry.cutting.CutResult;
import com.geometry.geometry.cutting.MeshCutter;
import com.geometry.geometry.cutting.Plane;
import com.geometry.geometry.operation.OperationResult;
import com.geometry.interaction.action.Action;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.tools.Tool;
import com.geometry.tools.ToolContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Phase 08 - Cut tool implementation.
 *
 * Receives cut actions and executes mesh-plane intersection.
 * On successful cut, replaces the original SceneObject with the cut pieces
 * in the Scene.
 *
 * Flow:
 *   CutAction → CutTool.executeCut() → MeshCutter.cut() → CutResult
 *   → Scene: remove original, add new meshes as SceneObjects
 *
 * Supports both keyboard/mouse mode and whiteboard stroke mode.
 */
public class CutTool implements Tool {

    private final ToolContext context;
    private boolean active;

    public CutTool(ToolContext context) {
        this.context = context;
        this.active = false;
    }

    @Override
    public String getName() {
        return "cut";
    }

    @Override
    public void activate() {
        this.active = true;
    }

    @Override
    public void deactivate() {
        this.active = false;
    }

    @Override
    public void handle(Action action) {
        if (!active) {
            return;
        }
        // Cut tool is event-driven via executeCut().
        // handle() is kept for future action-based cut triggers.
    }

    @Override
    public void update() {
        // CutTool is event-driven.
    }

    /**
     * Execute a cut operation on the given scene object.
     *
     * The original object is removed from the scene and replaced with
     * the cut pieces (or kept if the cut produced no valid division).
     *
     * @param target   the SceneObject to cut
     * @param normal   the normal of the cutting plane
     * @param distance the signed distance of the plane from origin
     * @return the CutResult, or null if the cut failed
     */
    public CutResult executeCut(SceneObject target, Vec3 normal, float distance) {
        if (!active) {
            return null;
        }
        if (normal == null) {
            throw new IllegalArgumentException("Normal cannot be null");
        }
        if (target == null) {
            return null;
        }

        // If no context (headless/mesh-level), use mesh-level cut
        if (context == null) {
            return executeCut(target.getGeometry().getMesh(), normal, distance);
        }

        Scene scene = context.getScene();
        Mesh mesh = target.getGeometry().getMesh();
        if (mesh == null || mesh.isEmpty()) {
            return null;
        }

        // Create the cutting plane
        Plane plane = new Plane(normal, distance);

        // Execute the cut
        OperationResult result = MeshCutter.cut(mesh, plane);
        if (!result.isSuccess()) {
            return new CutResult(result, null);
        }

        List<Mesh> cutMeshes = result.getMeshes();
        if (cutMeshes.isEmpty()) {
            return new CutResult(result, null);
        }

        // Generate section from intersection points
        Section section = extractSection(mesh, plane);

        // Remove the original object
        scene.removeObject(target);

        // Add cut pieces as new SceneObjects
        List<SceneObject> newObjects = new ArrayList<>();
        for (int i = 0; i < cutMeshes.size(); i++) {
            Mesh piece = cutMeshes.get(i);
            if (!piece.isEmpty()) {
                // Create a simple GeometryObject wrapper for the cut piece
                GeometryObject geo = new CutPieceGeometry(piece);
                SceneObject so = scene.addObject("cut_piece_" + i, geo);
                // Offset slightly so pieces are visible separately
                Vec3 offset = normal.normalize().multiply(0.05f * (i + 1));
                Transform t = so.getEffectiveTransform();
                so.setOverrideTransform(new Transform(t.getPosition().add(offset),
                        t.getRotation(), t.getScale()));
                newObjects.add(so);
            }
        }

        return new CutResult(result, section);
    }

    /**
     * Execute a cut operation on a raw Mesh (without scene integration).
     *
     * @param mesh   the mesh to cut
     * @param normal the plane normal
     * @param distance the plane distance
     * @return the CutResult
     */
    public CutResult executeCut(Mesh mesh, Vec3 normal, float distance) {
        if (mesh == null) {
            throw new IllegalArgumentException("Mesh cannot be null");
        }
        if (normal == null) {
            throw new IllegalArgumentException("Normal cannot be null");
        }

        Plane plane = new Plane(normal, distance);
        OperationResult result = MeshCutter.cut(mesh, plane);
        Section section = extractSection(mesh, plane);
        return new CutResult(result, section);
    }

    /**
     * Extract section points from the mesh-plane intersections.
     *
     * @param mesh  the original mesh
     * @param plane the cutting plane
     * @return a Section with the intersection contour, or null
     */
    public static Section extractSection(Mesh mesh, Plane plane) {
        if (mesh == null || plane == null) {
            return null;
        }

        List<Vec3> intersectionPoints = new ArrayList<>();
        java.util.Set<Long> visitedEdges = new java.util.HashSet<>();

        for (com.geometry.core.mesh.Face face : mesh.getFaces()) {
            int[] indices = face.getVertexIndices();
            if (indices.length != 3) continue;

            for (int e = 0; e < 3; e++) {
                int a = indices[e];
                int b = indices[(e + 1) % 3];

                // Skip if already processed this edge
                Long edgeKey = edgeKey(a, b);
                if (visitedEdges.contains(edgeKey)) continue;
                visitedEdges.add(edgeKey);

                com.geometry.core.mesh.Vertex vA = mesh.getVertex(a);
                com.geometry.core.mesh.Vertex vB = mesh.getVertex(b);
                float distA = plane.distanceToPoint(vA.getPosition());
                float distB = plane.distanceToPoint(vB.getPosition());

                // Edge crosses the plane
                if (Math.signum(distA) != Math.signum(distB)
                        && Math.abs(distA - distB) > 1e-8f) {
                    float t = distA / Math.abs(distA - distB);
                    Vec3 intersect = new Vec3(
                            vA.getPosition().x + (vB.getPosition().x - vA.getPosition().x) * t,
                            vA.getPosition().y + (vB.getPosition().y - vA.getPosition().y) * t,
                            vA.getPosition().z + (vB.getPosition().z - vA.getPosition().z) * t
                    );
                    intersectionPoints.add(intersect);
                }
            }
        }

        if (intersectionPoints.isEmpty()) {
            return null;
        }

        // Order points around the section contour
        List<Vec3> ordered = orderSectionPoints(intersectionPoints, plane);
        return new Section(ordered, plane);
    }

    /**
     * Order intersection points around the section contour by angle.
     * Projects points onto the plane and sorts by polar angle.
     */
    private static List<Vec3> orderSectionPoints(List<Vec3> points, Plane plane) {
        if (points.size() < 3) {
            return points;
        }

        // Compute centroid
        float cx = 0, cy = 0, cz = 0;
        for (Vec3 p : points) {
            cx += p.x; cy += p.y; cz += p.z;
        }
        cx /= points.size(); cy /= points.size(); cz /= points.size();
        Vec3 centroid = new Vec3(cx, cy, cz);

        // Build local 2D coordinate system on the plane
        Vec3 normal = plane.normal;
        Vec3 up = Vec3.UNIT_Y;
        if (Math.abs(normal.dot(up)) > 0.999f) {
            up = Vec3.UNIT_X;
        }
        Vec3 right = normal.cross(up).normalize();
        Vec3 forward = right.cross(normal).normalize();

        // Project to 2D and compute angles
        double[] angles = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
            Vec3 relative = points.get(i).subtract(centroid);
            angles[i] = Math.atan2(relative.dot(forward), relative.dot(right));
        }

        // Sort indices by angle
        Integer[] indices = new Integer[points.size()];
        for (int i = 0; i < points.size(); i++) indices[i] = i;
        java.util.Arrays.sort(indices, (a, b) -> Double.compare(angles[a], angles[b]));

        // Reorder points
        List<Vec3> ordered = new ArrayList<>(points.size());
        for (int i : indices) {
            ordered.add(points.get(i));
        }
        return ordered;
    }

    private static Long edgeKey(int a, int b) {
        long longA = ((long) a) << 32;
        long longB = (long) b;
        return longA ^ longB;
    }

    /**
     * Simple GeometryObject wrapper for a cut piece mesh.
     * Used to add cut pieces to the Scene.
     */
    static class CutPieceGeometry implements GeometryObject {
        private final Mesh mesh;
        private Transform transform;

        CutPieceGeometry(Mesh mesh) {
            this.mesh = mesh;
            this.transform = new Transform();
        }

        @Override
        public Mesh getMesh() {
            return mesh;
        }

        @Override
        public Transform getTransform() {
            return transform;
        }

        @Override
        public void setTransform(Transform transform) {
            if (transform == null) throw new IllegalArgumentException("Transform cannot be null");
            this.transform = transform;
        }

        @Override
        public void updateMesh() {
            // Cut pieces are static — no parameter changes
        }
    }
}
