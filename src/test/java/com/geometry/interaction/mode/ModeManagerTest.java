package com.geometry.interaction.mode;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.renderer.OpenGLRenderer;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Tests the unified-Mesh 2D/3D mode policy without requiring an OpenGL context. */
public class ModeManagerTest {
    @Test
    public void entering2DConstrainsPlanarObjectAndHidesSolid() {
        Scene scene = new Scene();
        SceneObject rectangle = scene.addObject("rectangle", new Rectangle(2f, 1f));
        rectangle.setOverrideTransform(new Transform(
                new Vec3(2f, 3f, 5f), new Vec3(10f, 20f, 30f), new Vec3(2f, 3f, 4f)));
        SceneObject cube = scene.addObject("cube", new Cube(1f, 1f, 1f));

        ModeManager manager = new ModeManager(scene);
        manager.setMode(GeometryMode.MODE_2D);

        assertTrue(rectangle.isVisible());
        assertFalse(cube.isVisible());
        assertEquals(0f, rectangle.getEffectiveTransform().getPosition().z, 0.0001f);
        assertEquals(0f, rectangle.getEffectiveTransform().getRotation().x, 0.0001f);
        assertEquals(0f, rectangle.getEffectiveTransform().getRotation().y, 0.0001f);
        assertEquals(30f, rectangle.getEffectiveTransform().getRotation().z, 0.0001f);
        assertEquals(4f, rectangle.getEffectiveTransform().getScale().z, 0.0001f);
    }

    @Test
    public void returningTo3DRestoresPreSwitchVisibilityAndProjection() {
        Scene scene = new Scene();
        SceneObject cube = scene.addObject("cube", new Cube(1f, 1f, 1f));
        OpenGLRenderer renderer = new OpenGLRenderer();
        ModeManager manager = new ModeManager(scene, renderer);

        manager.setMode(GeometryMode.MODE_2D);
        assertEquals(RenderMode.MODE_2D, renderer.getRenderMode());
        assertFalse(cube.isVisible());

        manager.setMode(GeometryMode.MODE_3D);
        assertEquals(RenderMode.MODE_3D, renderer.getRenderMode());
        assertTrue(cube.isVisible());
    }
}
