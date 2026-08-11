package com.geometry.ui;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Rectangle;
import com.geometry.scene.Scene;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.theme.EducationTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Verifies that the two teaching pages do not mix plane and solid geometry. */
public class ViewPageSeparationTest {

    @Test
    public void geometryIsFilteredByTheActiveTeachingPage() {
        Scene scene = new Scene();
        scene.addObject("rectangle", new Rectangle(4f, 3f));
        scene.addObject("cube", new Cube(4f, 4f, 4f));
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, new EducationTheme());

        assertEquals(ViewMode.MODE_2D, canvas.getViewMode());
        assertEquals(1, canvas.getVisibleObjectCount());
        assertTrue(canvas.isObjectInCurrentView(scene.findObjectById("rectangle")));
        assertFalse(canvas.isObjectInCurrentView(scene.findObjectById("cube")));

        canvas.setViewMode(ViewMode.MODE_3D);
        assertEquals(1, canvas.getVisibleObjectCount());
        assertFalse(canvas.isObjectInCurrentView(scene.findObjectById("rectangle")));
        assertTrue(canvas.isObjectInCurrentView(scene.findObjectById("cube")));
    }
}
