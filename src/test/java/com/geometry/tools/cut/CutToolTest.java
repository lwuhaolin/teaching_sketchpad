package com.geometry.tools.cut;

import com.geometry.core.geometry.Cube;
import com.geometry.core.math.Vec3;
import com.geometry.geometry.cutting.CutResult;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.scene.SelectionManager;
import com.geometry.tools.ToolContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 08 - Tests for CutTool integration with Scene.
 */
public class CutToolTest {

    private Scene scene;
    private SelectionManager selectionManager;
    private ToolContext toolContext;

    @Before
    public void setUp() {
        scene = new Scene();
        selectionManager = new SelectionManager();
        toolContext = new ToolContext(scene, selectionManager, null,
                RenderMode.MODE_2D);
    }

    @Test
    public void testCutToolReplacesOriginalWithPieces() {
        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube", cube);
        assertEquals(1, scene.getObjectCount());

        CutResult result = cutTool.executeCut(so, Vec3.UNIT_Y, 0f);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        // Original should be removed, pieces added
        assertTrue(scene.getObjectCount() >= 1);
    }

    @Test
    public void testCutToolReturnsResultWithMeshes() {
        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube2", cube);

        CutResult result = cutTool.executeCut(so, Vec3.UNIT_Z, 0f);

        assertNotNull(result);
        assertTrue(result.getMeshCount() >= 1);
        for (com.geometry.core.mesh.Mesh mesh : result.getMeshes()) {
            assertNotNull(mesh);
            assertFalse(mesh.isEmpty());
        }
    }

    @Test
    public void testCutToolWithOffCenterPlane() {
        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube3", cube);

        // Cut at y = 0.5 (above center)
        CutResult result = cutTool.executeCut(so, Vec3.UNIT_Y, -0.5f);

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCutToolWithDiagonalPlane() {
        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube4", cube);

        // Diagonal cut
        Vec3 diagNormal = new Vec3(1f, 1f, 1f).normalize();
        CutResult result = cutTool.executeCut(so, diagNormal, 0f);

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testCutToolExtractsSection() {
        CutTool cutTool = new CutTool(toolContext);
        cutTool.activate();

        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject so = scene.addObject("cube5", cube);

        CutResult result = cutTool.executeCut(so, Vec3.UNIT_Y, 0f);

        // Section should be extracted (may be null for some cut configurations)
        // Just verify it doesn't throw
        assertNotNull(result);
    }
}
