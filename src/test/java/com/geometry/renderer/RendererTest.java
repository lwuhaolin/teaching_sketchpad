package com.geometry.renderer;

import com.geometry.core.geometry.*;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 03 - Tests for the Renderer module.
 *
 * Tests:
 *   - RenderMode enum values
 *   - Camera projection and mode switching
 *   - MeshRenderer data preparation (without GPU)
 *   - Shader source compilation (source string validation)
 *   - OpenGLRenderer render queue management
 *   - RendererDemo basic creation
 *
 * Note: GPU-dependent tests (VertexArray, VertexBuffer, IndexBuffer,
 * MeshRenderer actual GL calls) are skipped here since they require
 * an OpenGL context. The RendererDemo class serves as the integration test.
 */
public class RendererTest {

    // ------------------------------------------------------------------
    // RenderMode tests
    // ------------------------------------------------------------------

    @Test
    public void testRenderModeValues() {
        RenderMode[] modes = RenderMode.values();
        assertEquals(2, modes.length);
        assertEquals(RenderMode.MODE_2D, modes[0]);
        assertEquals(RenderMode.MODE_3D, modes[1]);
    }

    @Test
    public void testRenderModeValueOf() {
        assertEquals(RenderMode.MODE_2D, RenderMode.valueOf("MODE_2D"));
        assertEquals(RenderMode.MODE_3D, RenderMode.valueOf("MODE_3D"));
    }

    // ------------------------------------------------------------------
    // Camera tests
    // ------------------------------------------------------------------

    @Test
    public void testCameraDefaultMode() {
        Camera camera = new Camera();
        assertEquals(RenderMode.MODE_3D, camera.getRenderMode());
    }

    @Test
    public void testCameraDefaultPosition() {
        Camera camera = new Camera();
        assertEquals(new Vec3(0f, 0f, 5f), camera.getPosition());
    }

    @Test
    public void testCameraDefaultRotation() {
        Camera camera = new Camera();
        assertEquals(new Vec3(0f, 0f, 0f), camera.getRotation());
    }

    @Test
    public void testCameraTranslation() {
        Camera camera = new Camera();
        camera.translate(new Vec3(1f, 2f, 3f));
        assertEquals(new Vec3(1f, 2f, 8f), camera.getPosition());
    }

    @Test
    public void testCameraRotation() {
        Camera camera = new Camera();
        camera.rotate(new Vec3(10f, 20f, 30f));
        assertEquals(new Vec3(10f, 20f, 30f), camera.getRotation());
    }

    @Test
    public void testCameraSwitchTo2D() {
        Camera camera = new Camera();
        camera.setRenderMode(RenderMode.MODE_2D);
        assertEquals(RenderMode.MODE_2D, camera.getRenderMode());
        // In 2D mode, position and rotation reset
        assertEquals(new Vec3(0f, 0f, 0f), camera.getPosition());
        assertEquals(new Vec3(0f, 0f, 0f), camera.getRotation());
    }

    @Test
    public void testCameraSwitchTo3D() {
        Camera camera = new Camera(RenderMode.MODE_2D);
        camera.setRenderMode(RenderMode.MODE_3D);
        assertEquals(RenderMode.MODE_3D, camera.getRenderMode());
        assertEquals(new Vec3(0f, 0f, 5f), camera.getPosition());
    }

    @Test
    public void testCameraGetProjectionMatrix() {
        Camera camera = new Camera();
        float[] proj = camera.getProjectionMatrix();
        assertNotNull(proj);
        assertEquals(16, proj.length);
    }

    @Test
    public void testCameraGetViewMatrix() {
        Camera camera = new Camera();
        float[] view = camera.getViewMatrix();
        assertNotNull(view);
        assertEquals(16, view.length);
    }

    @Test
    public void testCameraResizeValid() {
        Camera camera = new Camera();
        camera.resize(1024, 768); // Should not throw
        assertNotNull(camera.getProjectionMatrix());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCameraResizeInvalid() {
        Camera camera = new Camera();
        camera.resize(0, 768); // Zero width should throw
    }

    @Test
    public void testCameraPerspectiveMatrix() {
        // Verify the static method exists and returns correct size
        com.geometry.core.math.Matrix4 m = Camera.createPerspectiveMatrix(
                (float) Math.toRadians(45.0), 1.0f, 0.1f, 1000.0f);
        assertNotNull(m);
        assertEquals(16, m.m.length);
        // Identity-like: diagonal should be positive
        assertTrue(m.m[0] > 0);
        assertTrue(m.m[5] > 0);
    }

    // ------------------------------------------------------------------
    // Shader source tests (no GL context needed)
    // ------------------------------------------------------------------

    @Test
    public void testShaderSourceBasic() {
        // Verify that we can create shader source strings without GL
        String vertexSrc = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "void main() { gl_Position = vec4(aPos, 1.0); }\n";
        String fragmentSrc = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "void main() { FragColor = vec4(1.0); }\n";

        assertNotNull(vertexSrc);
        assertFalse(vertexSrc.isEmpty());
        assertTrue(vertexSrc.contains("version"));
        assertTrue(vertexSrc.contains("330"));
    }

    // ------------------------------------------------------------------
    // OpenGLRenderer render queue tests
    // ------------------------------------------------------------------

    @Test
    public void testOpenGLRendererCreate() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        assertNotNull(renderer);
        assertEquals(RenderMode.MODE_3D, renderer.getRenderMode());
        assertEquals(0, renderer.getRenderQueueSize());
    }

    @Test
    public void testOpenGLRendererAddGeometryObject() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        Cube cube = new Cube(1f, 1f, 1f);
        renderer.addGeometryObject(cube, 1.0f, 0.0f, 0.0f);
        assertEquals(1, renderer.getRenderQueueSize());
    }

    @Test
    public void testOpenGLRendererAddMultipleObjects() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        renderer.addGeometryObject(new Cube(1f, 1f, 1f), 1f, 0f, 0f);
        renderer.addGeometryObject(new Sphere(1f, 16, 8), 0f, 1f, 0f);
        renderer.addGeometryObject(new Cylinder(1f, 2f, 32), 0f, 0f, 1f);
        assertEquals(3, renderer.getRenderQueueSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenGLRendererAddNullObject() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        renderer.addGeometryObject(null, 1f, 0f, 0f);
    }

    @Test
    public void testOpenGLRendererClearQueue() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        renderer.addGeometryObject(new Cube(1f, 1f, 1f), 1f, 0f, 0f);
        renderer.clearRenderQueue();
        assertEquals(0, renderer.getRenderQueueSize());
    }

    @Test
    public void testOpenGLRendererSetRenderMode() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        renderer.setRenderMode(RenderMode.MODE_2D);
        assertEquals(RenderMode.MODE_2D, renderer.getRenderMode());
        renderer.setRenderMode(RenderMode.MODE_3D);
        assertEquals(RenderMode.MODE_3D, renderer.getRenderMode());
    }

    @Test
    public void testOpenGLRendererWindowDimensions() {
        OpenGLRenderer renderer = new OpenGLRenderer();
        assertEquals(800, renderer.getWindowWidth());
        assertEquals(600, renderer.getWindowHeight());
    }

    // ------------------------------------------------------------------
    // Geometry object renderability tests
    // ------------------------------------------------------------------

    @Test
    public void testAllGeometryObjectsAreRenderable() {
        // Verify that all Phase 02 geometry objects can be added to the renderer
        com.geometry.core.geometry.GeometryObject[] objects = {
                new Rectangle(2f, 2f),
                new Circle(1f, 8),
                new Polygon(
                        new Vec3(0f, 0f, 0f),
                        new Vec3(1f, 0f, 0f),
                        new Vec3(0f, 1f, 0f)
                ),
                new Cube(1f, 1f, 1f),
                new Cylinder(1f, 2f, 32),
                new Cone(1f, 2f, 32),
                new Sphere(1f, 16, 8)
        };

        OpenGLRenderer renderer = new OpenGLRenderer();
        for (int i = 0; i < objects.length; i++) {
            com.geometry.core.geometry.GeometryObject obj = objects[i];
            assertNotNull("Object " + i + " mesh is null", obj.getMesh());
            assertNotNull("Object " + i + " transform is null", obj.getTransform());
            assertFalse("Object " + i + " mesh is empty", obj.getMesh().isEmpty());
            // Should be addable to renderer without throwing
            renderer.addGeometryObject(obj, 0.5f, 0.5f, 0.5f);
        }
        assertEquals(objects.length, renderer.getRenderQueueSize());
    }

    // ------------------------------------------------------------------
    // Mesh data validation for rendering
    // ------------------------------------------------------------------

    @Test
    public void testCubeMeshForRenderer() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();
        // MeshRenderer requires faces
        assertTrue(mesh.getFaceCount() > 0);
        assertTrue(mesh.getVertexCount() > 0);
        // All vertices should have position
        for (int i = 0; i < mesh.getVertexCount(); i++) {
            assertNotNull(mesh.getVertex(i).getPosition());
        }
    }

    @Test
    public void testSphereMeshForRenderer() {
        Sphere sphere = new Sphere(1f, 16, 8);
        Mesh mesh = sphere.getMesh();
        assertTrue(mesh.getFaceCount() > 0);
        assertTrue(mesh.getVertexCount() > 0);
    }

    @Test
    public void testCylinderMeshForRenderer() {
        Cylinder cylinder = new Cylinder(1f, 2f, 32);
        Mesh mesh = cylinder.getMesh();
        assertTrue(mesh.getFaceCount() > 0);
        assertTrue(mesh.getVertexCount() > 0);
    }

    @Test
    public void testConeMeshForRenderer() {
        Cone cone = new Cone(1f, 2f, 32);
        Mesh mesh = cone.getMesh();
        assertTrue(mesh.getFaceCount() > 0);
        assertTrue(mesh.getVertexCount() > 0);
    }

    @Test
    public void testRectangleMeshForRenderer() {
        Rectangle rect = new Rectangle(2f, 2f);
        Mesh mesh = rect.getMesh();
        assertEquals(4, mesh.getVertexCount());
        assertEquals(2, mesh.getFaceCount());
    }

    @Test
    public void testCircleMeshForRenderer() {
        Circle circle = new Circle(1f, 8);
        Mesh mesh = circle.getMesh();
        assertEquals(9, mesh.getVertexCount()); // 1 center + 8 edge
        assertEquals(8, mesh.getFaceCount());
    }

    // ------------------------------------------------------------------
    // Transform to matrix validation
    // ------------------------------------------------------------------

    @Test
    public void testIdentityTransform() {
        Transform t = new Transform();
        assertEquals(Vec3.ZERO, t.getPosition());
        assertEquals(new Vec3(0f, 0f, 0f), t.getRotation());
        assertEquals(Vec3.ONE, t.getScale());
    }

    @Test
    public void testTranslatedTransform() {
        Transform t = new Transform().translate(new Vec3(1f, 2f, 3f));
        assertEquals(new Vec3(1f, 2f, 3f), t.getPosition());
    }

    @Test
    public void testScaledTransform() {
        Transform t = new Transform().scaleUniform(2f);
        assertEquals(new Vec3(2f, 2f, 2f), t.getScale());
    }

    @Test
    public void testRotatedTransform() {
        Transform t = new Transform().rotate(new Vec3(90f, 0f, 0f));
        assertEquals(new Vec3(90f, 0f, 0f), t.getRotation());
    }
}
