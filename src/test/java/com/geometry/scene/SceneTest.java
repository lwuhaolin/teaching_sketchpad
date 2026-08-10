package com.geometry.scene;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 04 - Tests for the Scene System.
 *
 * Tests:
 *   - SceneObject creation and properties
 *   - Scene add/remove/query
 *   - Layer management
 *   - SelectionManager operations
 *   - ObjectManager operations
 */
public class SceneTest {

    private Scene scene;
    private ObjectManager objManager;
    private SelectionManager selectionManager;

    @Before
    public void setUp() {
        scene = new Scene();
        objManager = new ObjectManager();
        selectionManager = new SelectionManager();
    }

    // ------------------------------------------------------------------
    // SceneObject tests
    // ------------------------------------------------------------------

    @Test
    public void testSceneObjectCreate() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject("test_001", cube);

        assertEquals("test_001", so.getId());
        assertEquals(cube, so.getGeometry());
        assertTrue(so.isVisible());
        assertFalse(so.isSelected());
        assertNull(so.getOverrideTransform());
    }

    @Test
    public void testSceneObjectAutoId() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject(cube);

        assertNotNull(so.getId());
        assertFalse(so.getId().isEmpty());
        assertEquals(cube, so.getGeometry());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSceneObjectNullId() {
        new SceneObject((String) null, new Cube(1f, 1f, 1f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSceneObjectEmptyId() {
        new SceneObject("", new Cube(1f, 1f, 1f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSceneObjectNullGeometry() {
        new SceneObject("test", null);
    }

    @Test
    public void testSceneObjectToggleSelected() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject(cube);

        assertFalse(so.isSelected());
        so.toggleSelected();
        assertTrue(so.isSelected());
        so.toggleSelected();
        assertFalse(so.isSelected());
    }

    @Test
    public void testSceneObjectOverrideTransform() {
        Cube cube = new Cube(1f, 1f, 1f);
        Transform original = cube.getTransform();
        Transform override = new Transform(new Vec3(5f, 5f, 5f), new Vec3(0f, 0f, 0f), new Vec3(2f, 2f, 2f));

        SceneObject so = new SceneObject(cube);
        assertEquals(original, so.getEffectiveTransform());

        so.setOverrideTransform(override);
        assertEquals(override, so.getEffectiveTransform());

        so.setOverrideTransform(null);
        assertEquals(original, so.getEffectiveTransform());
    }

    @Test
    public void testSceneObjectEqualsById() {
        Cube cube1 = new Cube(1f, 1f, 1f);
        Cube cube2 = new Cube(1f, 1f, 1f);
        SceneObject so1 = new SceneObject("same_id", cube1);
        SceneObject so2 = new SceneObject("same_id", cube2);

        assertEquals(so1, so2);
        assertEquals(so1.hashCode(), so2.hashCode());
    }

    // ------------------------------------------------------------------
    // ObjectManager tests
    // ------------------------------------------------------------------

    @Test
    public void testObjectManagerAddAndGetSize() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject("cube_001", cube);

        objManager.addObject(so);
        assertEquals(1, objManager.size());
        assertTrue(objManager.containsId("cube_001"));
    }

    @Test
    public void testObjectManagerFindById() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject("cube_001", cube);
        objManager.addObject(so);

        SceneObject found = objManager.findById("cube_001");
        assertNotNull(found);
        assertEquals(so, found);
    }

    @Test
    public void testObjectManagerFindByIdNotFound() {
        Cube cube = new Cube(1f, 1f, 1f);
        objManager.addObject(new SceneObject("cube_001", cube));

        assertNull(objManager.findById("nonexistent"));
    }

    @Test
    public void testObjectManagerRemoveById() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject("cube_001", cube);
        objManager.addObject(so);
        assertTrue(objManager.removeObjectById("cube_001"));
        assertEquals(0, objManager.size());
        assertNull(objManager.findById("cube_001"));
    }

    @Test
    public void testObjectManagerRemoveByIdNotFound() {
        Cube cube = new Cube(1f, 1f, 1f);
        objManager.addObject(new SceneObject("cube_001", cube));
        assertFalse(objManager.removeObjectById("nonexistent"));
    }

    @Test
    public void testObjectManagerRemoveByReference() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = new SceneObject("cube_001", cube);
        objManager.addObject(so);
        assertTrue(objManager.removeObject(so));
        assertEquals(0, objManager.size());
    }

    @Test
    public void testObjectManagerClear() {
        objManager.addObject(new SceneObject("a", new Cube(1f, 1f, 1f)));
        objManager.addObject(new SceneObject("b", new Cylinder(1f, 2f, 16)));
        objManager.clear();
        assertEquals(0, objManager.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObjectManagerDuplicateId() {
        objManager.addObject(new SceneObject("dup", new Cube(1f, 1f, 1f)));
        objManager.addObject(new SceneObject("dup", new Cube(1f, 1f, 1f)));
    }

    @Test
    public void testObjectManagerGetAll() {
        objManager.addObject(new SceneObject("a", new Cube(1f, 1f, 1f)));
        objManager.addObject(new SceneObject("b", new Cylinder(1f, 2f, 16)));
        List<SceneObject> all = objManager.getAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testObjectManagerIsEmpty() {
        assertTrue(objManager.isEmpty());
        objManager.addObject(new SceneObject("a", new Cube(1f, 1f, 1f)));
        assertFalse(objManager.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testObjectManagerAddNull() {
        objManager.addObject(null);
    }

    // ------------------------------------------------------------------
    // Layer tests
    // ------------------------------------------------------------------

    @Test
    public void testLayerCreate() {
        Layer layer = new Layer("基础图形");
        assertEquals("基础图形", layer.getName());
        assertTrue(layer.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayerNullName() {
        new Layer(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLayerEmptyName() {
        new Layer("");
    }

    @Test
    public void testLayerAddObject() {
        Layer layer = new Layer("基础图形");
        SceneObject so = new SceneObject("cube", new Cube(1f, 1f, 1f));
        layer.addObject(so);
        assertEquals(1, layer.getObjectCount());
        assertTrue(layer.getObjects().contains(so));
    }

    @Test
    public void testLayerRemoveObject() {
        Layer layer = new Layer("基础图形");
        SceneObject so = new SceneObject("cube", new Cube(1f, 1f, 1f));
        layer.addObject(so);
        assertTrue(layer.removeObject(so));
        assertEquals(0, layer.getObjectCount());
    }

    @Test
    public void testLayerClear() {
        Layer layer = new Layer("基础图形");
        layer.addObject(new SceneObject("a", new Cube(1f, 1f, 1f)));
        layer.addObject(new SceneObject("b", new Cylinder(1f, 2f, 16)));
        layer.clear();
        assertEquals(0, layer.getObjectCount());
    }

    // ------------------------------------------------------------------
    // SelectionManager tests
    // ------------------------------------------------------------------

    @Test
    public void testSelectionManagerSelectAndDeselect() {
        SceneObject so1 = new SceneObject("a", new Cube(1f, 1f, 1f));
        SceneObject so2 = new SceneObject("b", new Cube(1f, 1f, 1f));

        selectionManager.select(so1);
        assertTrue(selectionManager.isSelected(so1));
        assertFalse(selectionManager.isSelected(so2));
        assertEquals(so1, selectionManager.getSelected());

        selectionManager.deselect(so1);
        assertFalse(selectionManager.isSelected(so1));
        assertNull(selectionManager.getSelected());
    }

    @Test
    public void testSelectionManagerSelectById() {
        SceneObject so = new SceneObject("cube_001", new Cube(1f, 1f, 1f));

        boolean result = selectionManager.selectById("cube_001", id ->
                id.equals("cube_001") ? so : null);
        assertTrue(result);
        assertTrue(selectionManager.isSelected(so));
    }

    @Test
    public void testSelectionManagerClear() {
        SceneObject so1 = new SceneObject("a", new Cube(1f, 1f, 1f));
        SceneObject so2 = new SceneObject("b", new Cube(1f, 1f, 1f));
        selectionManager.select(so1);
        selectionManager.clearSelection();
        assertFalse(selectionManager.isSelected(so1));
        assertEquals(0, selectionManager.getSelectedCount());
    }

    @Test
    public void testSelectionManagerToggle() {
        SceneObject so = new SceneObject("a", new Cube(1f, 1f, 1f));
        selectionManager.toggleSelection(so);
        assertTrue(selectionManager.isSelected(so));
        selectionManager.toggleSelection(so);
        assertFalse(selectionManager.isSelected(so));
    }

    @Test
    public void testSelectionManagerGetSelectedObjects() {
        SceneObject so = new SceneObject("a", new Cube(1f, 1f, 1f));
        selectionManager.select(so);
        List<SceneObject> selected = selectionManager.getSelectedObjects();
        assertEquals(1, selected.size());
        assertEquals(so, selected.get(0));
    }

    // ------------------------------------------------------------------
    // Scene tests
    // ------------------------------------------------------------------

    @Test
    public void testSceneAddObject() {
        Cube cube = new Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject(cube);
        assertNotNull(so);
        assertEquals(1, scene.getObjectCount());
    }

    @Test
    public void testSceneAddObjectWithId() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        assertEquals("cube_001", so.getId());
        assertEquals(1, scene.getObjectCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSceneAddNullObject() {
        scene.addObject((Cube) null);
    }

    @Test
    public void testSceneRemoveObject() {
        SceneObject so = scene.addObject(new Cube(1f, 1f, 1f));
        assertEquals(1, scene.getObjectCount());
        assertTrue(scene.removeObject(so));
        assertEquals(0, scene.getObjectCount());
    }

    @Test
    public void testSceneRemoveObjectById() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        assertTrue(scene.removeObjectById("cube_001"));
        assertEquals(0, scene.getObjectCount());
        assertNull(scene.findObjectById("cube_001"));
    }

    @Test
    public void testSceneFindById() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        SceneObject found = scene.findObjectById("cube_001");
        assertNotNull(found);
        assertEquals(so, found);
    }

    @Test
    public void testSceneFindByIdNotFound() {
        assertNull(scene.findObjectById("nonexistent"));
    }

    @Test
    public void testSceneClear() {
        scene.addObject(new Cube(1f, 1f, 1f));
        scene.addObject(new Cylinder(1f, 2f, 16));
        scene.clear();
        assertEquals(0, scene.getObjectCount());
    }

    @Test
    public void testSceneIsEmpty() {
        assertTrue(scene.isEmpty());
        scene.addObject(new Cube(1f, 1f, 1f));
        assertFalse(scene.isEmpty());
    }

    @Test
    public void testSceneAddAndGetAllObjects() {
        SceneObject cube = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        SceneObject cylinder = scene.addObject("cyl_001", new Cylinder(1f, 2f, 16));
        SceneObject rect = scene.addObject("rect_001", new Rectangle(2f, 2f));

        List<SceneObject> all = scene.getAllObjects();
        assertEquals(3, all.size());
        assertTrue(all.contains(cube));
        assertTrue(all.contains(cylinder));
        assertTrue(all.contains(rect));
    }

    // ------------------------------------------------------------------
    // Scene selection tests
    // ------------------------------------------------------------------

    @Test
    public void testSceneSelect() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        scene.select(so);
        assertTrue(scene.isSelected(so));
        assertEquals(so, scene.getSelected());
    }

    @Test
    public void testSceneSelectById() {
        scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        assertTrue(scene.selectById("cube_001"));
        assertEquals(1, scene.getSelectedObjects().size());
    }

    @Test
    public void testSceneClearSelection() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        scene.select(so);
        scene.clearSelection();
        assertFalse(scene.isSelected(so));
        assertNull(scene.getSelected());
    }

    // ------------------------------------------------------------------
    // Scene visibility tests
    // ------------------------------------------------------------------

    @Test
    public void testSceneSetAllVisible() {
        SceneObject a = scene.addObject("a", new Cube(1f, 1f, 1f));
        SceneObject b = scene.addObject("b", new Cylinder(1f, 2f, 16));
        a.setVisible(false);
        b.setVisible(false);
        scene.setAllVisible(true);
        assertTrue(a.isVisible());
        assertTrue(b.isVisible());
    }

    @Test
    public void testSceneSetVisibleById() {
        scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        assertTrue(scene.setVisibleById("cube_001", false));
        assertFalse(scene.findObjectById("cube_001").isVisible());
        assertFalse(scene.setVisibleById("nonexistent", true));
    }

    // ------------------------------------------------------------------
    // Scene layer tests
    // ------------------------------------------------------------------

    @Test
    public void testSceneAddLayer() {
        Layer layer = new Layer("基础图形");
        assertTrue(scene.addLayer(layer));
        assertEquals(1, scene.getLayerCount());
    }

    @Test
    public void testSceneRemoveLayer() {
        Layer layer = new Layer("基础图形");
        scene.addLayer(layer);
        assertTrue(scene.removeLayer(layer));
        assertEquals(0, scene.getLayerCount());
    }

    @Test
    public void testSceneMultipleLayers() {
        scene.addLayer(new Layer("基础图形"));
        scene.addLayer(new Layer("辅助线"));
        scene.addLayer(new Layer("标注"));
        assertEquals(3, scene.getLayerCount());
    }

    // ------------------------------------------------------------------
    // Scene update test
    // ------------------------------------------------------------------

    @Test
    public void testSceneUpdate() {
        SceneObject so = scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        // Update should not throw
        scene.update();
        assertNotNull(so.getGeometry().getMesh());
        assertFalse(so.getGeometry().getMesh().isEmpty());
    }

    // ------------------------------------------------------------------
    // Scene toString test
    // ------------------------------------------------------------------

    @Test
    public void testSceneToString() {
        scene.addObject("cube_001", new Cube(1f, 1f, 1f));
        String str = scene.toString();
        assertTrue(str.contains("objects=1"));
        assertTrue(str.contains("layers=0"));
    }
}
