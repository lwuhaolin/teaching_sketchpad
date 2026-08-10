package com.geometry.geometry;

import com.geometry.core.geometry.Cone;
import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.geometry.analysis.Section;
import com.geometry.geometry.analysis.SectionAnalyzer;
import com.geometry.geometry.cutting.CutResult;
import com.geometry.geometry.cutting.MeshCutter;
import com.geometry.geometry.cutting.Plane;
import com.geometry.geometry.cutting.PlaneCutOperation;
import com.geometry.geometry.operation.GeometryOperation;
import com.geometry.geometry.operation.OperationResult;
import com.geometry.tools.cut.CutTool;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 08 - Tests for the Geometry Operation (Cut) System.
 *
 * Tests:
 *   - Plane: construction, distanceToPoint, side classification
 *   - MeshCutter: Cube cut, Cylinder cut, Cone cut
 *   - PlaneCutOperation: interface compliance
 *   - CutResult: mesh count, section extraction
 *   - SectionAnalyzer: shape type classification
 *   - CutTool: executeCut on SceneObject and raw Mesh
 *   - Original mesh immutability
 */
public class GeometryOperationTest {

    // ------------------------------------------------------------------
    // Plane tests
    // ------------------------------------------------------------------

    @Test
    public void testPlaneConstructionFromNormalAndDistance() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        assertEquals(0f, plane.distance, 1e-6f);
        assertTrue(Vec3.UNIT_Z.equals(plane.normal));
    }

    @Test
    public void testPlaneConstructionFromThreePoints() {
        Vec3 a = new Vec3(0f, 0f, 0f);
        Vec3 b = new Vec3(1f, 0f, 0f);
        Vec3 c = new Vec3(0f, 1f, 0f);

        Plane plane = new Plane(a, b, c);
        assertTrue(Vec3.UNIT_Z.equals(plane.normal) || Vec3.UNIT_Z.subtract(plane.normal).length() < 1e-5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlaneNullNormalThrows() {
        new Plane(null, 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlaneZeroNormalThrows() {
        new Plane(new Vec3(0f, 0f, 0f), 0f);
    }

    @Test
    public void testPlaneDistanceToPoint() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);

        float distAbove = plane.distanceToPoint(new Vec3(0f, 0f, 5f));
        assertTrue(distAbove > 0);
        assertEquals(5f, distAbove, 1e-5f);

        float distBelow = plane.distanceToPoint(new Vec3(0f, 0f, -3f));
        assertTrue(distBelow < 0);
        assertEquals(-3f, distBelow, 1e-5f);

        float distOn = plane.distanceToPoint(new Vec3(1f, 2f, 0f));
        assertEquals(0f, distOn, 1e-6f);
    }

    @Test
    public void testPlaneSideClassification() {
        Plane plane = new Plane(Vec3.UNIT_Y, 0f);

        assertTrue(plane.isPositiveSide(new Vec3(0f, 5f, 0f)));
        assertTrue(plane.isNegativeSide(new Vec3(0f, -5f, 0f)));
        assertTrue(plane.isOnPlane(new Vec3(1f, 0f, 1f)));
    }

    @Test
    public void testPlaneOffOrigin() {
        Plane plane = new Plane(Vec3.UNIT_Y, 2f);

        float dist = plane.distanceToPoint(new Vec3(0f, 3f, 0f));
        assertEquals(5f, dist, 1e-5f);

        dist = plane.distanceToPoint(new Vec3(0f, -3f, 0f));
        assertEquals(-1f, dist, 1e-5f);
    }

    // ------------------------------------------------------------------
    // OperationResult tests
    // ------------------------------------------------------------------

    @Test
    public void testOperationResultSuccess() {
        Mesh mesh = new Mesh();
        OperationResult result = OperationResult.success(mesh);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getMeshCount());
        assertEquals(mesh, result.getMesh());
    }

    @Test
    public void testOperationResultMultipleMeshes() {
        Mesh m1 = new Mesh();
        Mesh m2 = new Mesh();
        OperationResult result = OperationResult.success(m1, m2);
        assertTrue(result.isSuccess());
        assertEquals(2, result.getMeshCount());
    }

    @Test
    public void testOperationResultFailure() {
        OperationResult result = OperationResult.failure("mesh too small");
        assertFalse(result.isSuccess());
        assertEquals("mesh too small", result.getMessage());
        assertEquals(0, result.getMeshCount());
    }

    @Test
    public void testOperationResultEmpty() {
        OperationResult result = OperationResult.empty();
        assertTrue(result.isSuccess());
        assertEquals(0, result.getMeshCount());
    }

    // ------------------------------------------------------------------
    // Cube cut tests
    // ------------------------------------------------------------------

    @Test
    public void testCubeCutHorizontal() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();
        int originalFaceCount = mesh.getFaceCount();

        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
        assertEquals(originalFaceCount, mesh.getFaceCount());
    }

    @Test
    public void testCubeCutVertical() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();

        Plane plane = new Plane(Vec3.UNIT_X, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testCubeCutDiagonal() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();

        Plane plane = new Plane(new Vec3(1f, 1f, 0f).normalize(), 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testCubeCutBelowBottom() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();

        Plane plane = new Plane(Vec3.UNIT_Y, -10f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getMeshCount());
    }

    @Test
    public void testCubeCutAboveTop() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();

        Plane plane = new Plane(Vec3.UNIT_Y, 10f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertEquals(1, result.getMeshCount());
    }

    // ------------------------------------------------------------------
    // Cylinder cut tests
    // ------------------------------------------------------------------

    @Test
    public void testCylinderCutHorizontal() {
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        Mesh mesh = cylinder.getMesh();
        int originalFaceCount = mesh.getFaceCount();

        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
        assertEquals(originalFaceCount, mesh.getFaceCount());
    }

    @Test
    public void testCylinderCutVertical() {
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        Mesh mesh = cylinder.getMesh();

        Plane plane = new Plane(Vec3.UNIT_X, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testCylinderCutAngled() {
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        Mesh mesh = cylinder.getMesh();

        Plane plane = new Plane(new Vec3(1f, 1f, 0f).normalize(), 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    // ------------------------------------------------------------------
    // Cone cut tests
    // ------------------------------------------------------------------

    @Test
    public void testConeCutHorizontal() {
        Cone cone = new Cone(1f, 2f, 16);
        Mesh mesh = cone.getMesh();

        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testConeCutVertical() {
        Cone cone = new Cone(1f, 2f, 16);
        Mesh mesh = cone.getMesh();

        Plane plane = new Plane(Vec3.UNIT_X, 0f);
        OperationResult result = MeshCutter.cut(mesh, plane);

        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    // ------------------------------------------------------------------
    // PlaneCutOperation tests
    // ------------------------------------------------------------------

    @Test
    public void testPlaneCutOperationInterface() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        GeometryOperation op = new PlaneCutOperation(plane);
        assertNotNull(op);
    }

    @Test
    public void testPlaneCutOperationExecute() {
        Cube cube = new Cube(2f, 2f, 2f);
        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        GeometryOperation op = new PlaneCutOperation(plane);

        OperationResult result = op.execute(cube.getMesh());
        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlaneCutOperationNullPlaneThrows() {
        new PlaneCutOperation(null);
    }

    // ------------------------------------------------------------------
    // CutResult tests
    // ------------------------------------------------------------------

    @Test
    public void testCutResultCreation() {
        OperationResult opResult = OperationResult.success(new Mesh(), new Mesh());
        CutResult cutResult = new CutResult(opResult, null);

        assertTrue(cutResult.isSuccess());
        assertEquals(2, cutResult.getMeshCount());
        assertNull(cutResult.getSection());
    }

    @Test
    public void testCutResultWithSection() {
        OperationResult opResult = OperationResult.success(new Mesh());
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        Section section = new Section(new java.util.ArrayList<Vec3>(), plane);
        CutResult cutResult = new CutResult(opResult, section);

        assertNotNull(cutResult.getSection());
        assertEquals(plane, cutResult.getSection().getCuttingPlane());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCutResultNullOperationResultThrows() {
        new CutResult(null, null);
    }

    // ------------------------------------------------------------------
    // Section tests
    // ------------------------------------------------------------------

    @Test
    public void testSectionEmpty() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        Section section = new Section(new java.util.ArrayList<Vec3>(), plane);
        assertTrue(section.isEmpty());
        assertEquals(0, section.pointCount());
    }

    @Test
    public void testSectionWithPoints() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        points.add(new Vec3(1f, 0f, 0f));
        points.add(new Vec3(0f, 1f, 0f));
        points.add(new Vec3(-1f, 0f, 0f));
        points.add(new Vec3(0f, -1f, 0f));

        Section section = new Section(points, plane);
        assertEquals(4, section.pointCount());
        assertFalse(section.isEmpty());
    }

    @Test
    public void testSectionArea() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        points.add(new Vec3(-1f, -1f, 0f));
        points.add(new Vec3(1f, -1f, 0f));
        points.add(new Vec3(1f, 1f, 0f));
        points.add(new Vec3(-1f, 1f, 0f));

        Section section = new Section(points, plane);
        float area = section.computeArea();
        assertEquals(4f, area, 0.1f);
    }

    // ------------------------------------------------------------------
    // SectionAnalyzer tests
    // ------------------------------------------------------------------

    @Test
    public void testSectionAnalyzerEmpty() {
        Section empty = new Section(new java.util.ArrayList<Vec3>(), new Plane(Vec3.UNIT_Z, 0f));
        assertEquals(SectionAnalyzer.ShapeType.UNKNOWN,
                SectionAnalyzer.analyze(empty));
    }

    @Test
    public void testSectionAnalyzerTriangle() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        points.add(new Vec3(0f, 0f, 0f));
        points.add(new Vec3(1f, 0f, 0f));
        points.add(new Vec3(0f, 1f, 0f));

        Section section = new Section(points, plane);
        assertEquals(SectionAnalyzer.ShapeType.TRIANGLE,
                SectionAnalyzer.analyze(section));
    }

    @Test
    public void testSectionAnalyzerQuadrilateral() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        points.add(new Vec3(0f, 0f, 0f));
        points.add(new Vec3(1f, 0f, 0f));
        points.add(new Vec3(1f, 1f, 0f));
        points.add(new Vec3(0f, 1f, 0f));

        Section section = new Section(points, plane);
        assertEquals(SectionAnalyzer.ShapeType.QUADRILATERAL,
                SectionAnalyzer.analyze(section));
    }

    @Test
    public void testSectionAnalyzerPolygon() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        for (int i = 0; i < 6; i++) {
            double angle = i * 2 * Math.PI / 6;
            points.add(new Vec3((float) Math.cos(angle), (float) Math.sin(angle), 0f));
        }

        Section section = new Section(points, plane);
        assertEquals(SectionAnalyzer.ShapeType.POLYGON,
                SectionAnalyzer.analyze(section));
    }

    @Test
    public void testSectionAnalyzerApproxCircle() {
        Plane plane = new Plane(Vec3.UNIT_Z, 0f);
        java.util.List<Vec3> points = new java.util.ArrayList<Vec3>();
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            double angle = i * 2 * Math.PI / segments;
            points.add(new Vec3((float) Math.cos(angle), (float) Math.sin(angle), 0f));
        }

        Section section = new Section(points, plane);
        assertEquals(SectionAnalyzer.ShapeType.APPROX_CIRCLE,
                SectionAnalyzer.analyze(section));
    }

    // ------------------------------------------------------------------
    // CutTool tests
    // ------------------------------------------------------------------

    @Test
    public void testCutToolDoesNotThrow() {
        com.geometry.tools.ToolContext toolContext =
                new com.geometry.tools.ToolContext(
                        new com.geometry.scene.Scene(),
                        new com.geometry.scene.SelectionManager(),
                        null,
                        com.geometry.renderer.RenderMode.MODE_2D
                );

        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        com.geometry.scene.SceneObject so =
                toolContext.getScene().addObject("cube_cut", cube);

        CutResult result = cutTool.executeCut(so, new Vec3(0f, 1f, 0f), 0f);
        assertNotNull(result);
    }

    @Test
    public void testCutToolExecuteCutOnMesh() {
        Cube cube = new Cube(2f, 2f, 2f);
        CutTool cutTool = new CutTool(null);

        CutResult result = cutTool.executeCut(cube.getMesh(), new Vec3(0f, 1f, 0f), 0f);
        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testCutToolOriginalMeshNotModified() {
        Cube cube = new Cube(2f, 2f, 2f);
        int originalFaces = cube.getMesh().getFaceCount();

        CutTool cutTool = new CutTool(null);
        cutTool.executeCut(cube.getMesh(), new Vec3(0f, 1f, 0f), 0f);

        assertEquals(originalFaces, cube.getMesh().getFaceCount());
    }

    @Test
    public void testCutToolInactiveDoesNothing() {
        com.geometry.scene.Scene scene = new com.geometry.scene.Scene();
        com.geometry.scene.SelectionManager sm = new com.geometry.scene.SelectionManager();
        com.geometry.tools.ToolContext toolContext =
                new com.geometry.tools.ToolContext(scene, sm, null,
                        com.geometry.renderer.RenderMode.MODE_2D);

        CutTool cutTool = new CutTool(toolContext);
        // Not activated

        Cube cube = new Cube(2f, 2f, 2f);
        com.geometry.scene.SceneObject so = scene.addObject("cube_inactive", cube);
        int initialCount = scene.getObjectCount();

        cutTool.executeCut(so, new Vec3(0f, 1f, 0f), 0f);

        assertEquals(initialCount, scene.getObjectCount());
    }

    @Test
    public void testCutToolNullTargetReturnsNull() {
        CutTool cutTool = new CutTool(null);
        cutTool.activate();

        assertNull(cutTool.executeCut((com.geometry.scene.SceneObject) null, new Vec3(0f, 1f, 0f), 0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCutToolNullNormalThrows() {
        CutTool cutTool = new CutTool(null);
        cutTool.activate();
        // Cast to disambiguate: pass null SceneObject, null Vec3
        cutTool.executeCut((com.geometry.scene.SceneObject) null, (Vec3) null, 0f);
    }

    // ------------------------------------------------------------------
    // Immutability tests
    // ------------------------------------------------------------------

    @Test
    public void testMeshNotModifiedByCut() {
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        Mesh original = cylinder.getMesh();
        int originalVertexCount = original.getVertexCount();
        int originalFaceCount = original.getFaceCount();

        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        MeshCutter.cut(original, plane);

        assertEquals(originalVertexCount, original.getVertexCount());
        assertEquals(originalFaceCount, original.getFaceCount());
    }

    @Test
    public void testCutProducesValidMeshes() {
        Cube cube = new Cube(2f, 2f, 2f);
        Plane plane = new Plane(Vec3.UNIT_Y, 0f);
        OperationResult result = MeshCutter.cut(cube.getMesh(), plane);

        for (Mesh mesh : result.getMeshes()) {
            assertFalse("Cut piece should not be empty", mesh.isEmpty());
            assertTrue("Cut piece should have faces", mesh.getFaceCount() > 0);
            assertTrue("Cut piece should have vertices", mesh.getVertexCount() > 0);
        }
    }

    @Test
    public void testCutToolWithCylinder() {
        Cylinder cylinder = new Cylinder(1f, 2f, 16);
        CutTool cutTool = new CutTool(null);
        cutTool.activate();

        CutResult result = cutTool.executeCut(cylinder.getMesh(), new Vec3(0f, 1f, 0f), 0f);
        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }

    @Test
    public void testCutToolWithCone() {
        Cone cone = new Cone(1f, 2f, 16);
        CutTool cutTool = new CutTool(null);
        cutTool.activate();

        CutResult result = cutTool.executeCut(cone.getMesh(), new Vec3(0f, 1f, 0f), 0f);
        assertTrue(result.isSuccess());
        assertTrue(result.getMeshCount() >= 1);
    }
}
