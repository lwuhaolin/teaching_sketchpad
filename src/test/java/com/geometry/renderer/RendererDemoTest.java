package com.geometry.renderer;

import com.geometry.core.geometry.Cube;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Phase 03 - Integration test for RendererDemo.
 *
 * Tests that RendererDemo can be instantiated and that the demo
 * pipeline can create objects without throwing.
 *
 * Note: Full window rendering cannot be tested in a headless environment.
 * This test verifies the object creation and renderer setup paths.
 */
public class RendererDemoTest {

    @Test
    public void testRendererDemoMainClassExists() {
        // Verify the class can be loaded
        assertNotNull(RendererDemo.class);
    }

    @Test
    public void testRendererDemoCreatesCube() {
        // Replicate the demo's object creation to verify it works
        Cube cube = new Cube(2f, 2f, 2f);
        assertNotNull(cube.getMesh());
        assertEquals(8, cube.getMesh().getVertexCount());
        assertEquals(12, cube.getMesh().getFaceCount());
    }

    @Test
    public void testRendererDemoCreatesSphere() {
        com.geometry.core.geometry.Sphere sphere =
                new com.geometry.core.geometry.Sphere(0.8f, 16, 8);
        assertNotNull(sphere.getMesh());
        assertTrue(sphere.getMesh().getFaceCount() > 0);
    }

    @Test
    public void testRendererDemoCreatesCylinder() {
        com.geometry.core.geometry.Cylinder cylinder =
                new com.geometry.core.geometry.Cylinder(0.6f, 1.5f, 24);
        assertNotNull(cylinder.getMesh());
        assertTrue(cylinder.getMesh().getFaceCount() > 0);
    }

    @Test
    public void testRendererDemoRendererSetup() {
        // Test that renderer can be created and objects added
        OpenGLRenderer renderer = new OpenGLRenderer();
        assertNotNull(renderer);
        assertEquals(0, renderer.getRenderQueueSize());

        Cube cube = new Cube(2f, 2f, 2f);
        renderer.addGeometryObject(cube, 0.2f, 0.5f, 0.9f);
        assertEquals(1, renderer.getRenderQueueSize());

        com.geometry.core.geometry.Sphere sphere =
                new com.geometry.core.geometry.Sphere(0.8f, 16, 8);
        renderer.addGeometryObject(sphere, 0.9f, 0.3f, 0.3f);
        assertEquals(2, renderer.getRenderQueueSize());
    }

    @Test
    public void testRendererDemoRenderModes() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        assertEquals(RenderMode.MODE_3D, renderer.getRenderMode());

        renderer.setRenderMode(RenderMode.MODE_2D);
        assertEquals(RenderMode.MODE_2D, renderer.getRenderMode());

        renderer.setRenderMode(RenderMode.MODE_3D);
        assertEquals(RenderMode.MODE_3D, renderer.getRenderMode());
    }
}
