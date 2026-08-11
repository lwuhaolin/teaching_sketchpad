package com.geometry.ui;

import com.geometry.core.geometry.Cube;
import com.geometry.scene.Scene;
import com.geometry.ui.component.LwjglThreeDimensionalCanvas;
import com.geometry.ui.theme.EducationTheme;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Verifies the UI-facing contract of the embedded LWJGL 3D view. */
public class LwjglThreeDimensionalCanvasTest {

    @Test
    public void canvasStartsInThreeDimensionalModeAndShowsSolidGeometry() {
        Scene scene = new Scene();
        scene.addObject("cube", new Cube(2f, 2f, 2f));

        LwjglThreeDimensionalCanvas canvas =
                new LwjglThreeDimensionalCanvas(scene, new EducationTheme());

        assertEquals(ViewMode.MODE_3D, canvas.getViewMode());
        assertEquals(1, canvas.getVisibleObjectCount());
        assertTrue(canvas.isObjectInCurrentView(scene.findObjectById("cube")));
    }
}
