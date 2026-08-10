package com.geometry.core;

import com.geometry.core.geometry.*;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.transform.Transform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 02 - Tests for the Geometry Object system.
 *
 * Covers: Rectangle, Circle, Polygon, Cube, Cylinder, Cone, Sphere.
 * Tests mesh counts, parameter modification, and mesh regeneration.
 */
public class GeometryObjectTest {

    // ---- Rectangle tests ----

    @Test
    public void testRectangleMesh() {
        Rectangle rect = new Rectangle(4f, 2f);
        Mesh mesh = rect.getMesh();

        assertEquals(4, mesh.getVertexCount());
        assertEquals(2, mesh.getFaceCount());
        assertNotNull(mesh.getVertices());
        assertNotNull(mesh.getFaces());
    }

    @Test
    public void testRectanglePositionAtOrigin() {
        Rectangle rect = new Rectangle(2f, 2f);
        Mesh mesh = rect.getMesh();

        // Should be centred at origin with z=0
        for (int i = 0; i < mesh.getVertexCount(); i++) {
            assertEquals(0f, mesh.getVertex(i).getPosition().z, 1e-6f);
        }
    }

    @Test
    public void testRectangleParameterChange() {
        Rectangle rect = new Rectangle(2f, 2f);
        Mesh before = rect.getMesh();

        rect.setWidth(4f);
        Mesh after = rect.getMesh();

        assertNotSame(before, after);
        assertEquals(4, after.getVertexCount());
    }

    @Test
    public void testRectangleInvalidParams() {
        try {
            new Rectangle(0, 2f);
            fail("Should throw for zero width");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("positive"));
        }
        try {
            new Rectangle(-1f, 2f);
            fail("Should throw for negative width");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("positive"));
        }
    }

    // ---- Circle tests ----

    @Test
    public void testCircleMesh() {
        Circle circle = new Circle(1f, 8);
        Mesh mesh = circle.getMesh();

        // 1 center + 8 edge vertices = 9
        assertEquals(9, mesh.getVertexCount());
        // 8 triangles
        assertEquals(8, mesh.getFaceCount());
    }

    @Test
    public void testCircleSegments() {
        Circle circle = new Circle(1f, 16);
        Mesh mesh = circle.getMesh();

        assertEquals(17, mesh.getVertexCount()); // 1 center + 16 edge
        assertEquals(16, mesh.getFaceCount());
    }

    @Test
    public void testCircleParameterChange() {
        Circle circle = new Circle(1f, 8);
        Mesh before = circle.getMesh();

        circle.setRadius(2f);
        Mesh after = circle.getMesh();

        assertNotSame(before, after);
        assertEquals(9, after.getVertexCount());
    }

    @Test
    public void testCircleInvalidSegments() {
        try {
            new Circle(1f, 2);
            fail("Should throw for segments < 3");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(">= 3"));
        }
    }

    // ---- Polygon tests ----

    @Test
    public void testPolygonTriangle() {
        Polygon poly = new Polygon(
                new com.geometry.core.math.Vec3(0f, 0f, 0f),
                new com.geometry.core.math.Vec3(1f, 0f, 0f),
                new com.geometry.core.math.Vec3(0f, 1f, 0f)
        );
        Mesh mesh = poly.getMesh();
        assertEquals(3, mesh.getVertexCount());
        assertEquals(1, mesh.getFaceCount());
    }

    @Test
    public void testPolygonQuadrilateral() {
        Polygon poly = new Polygon(
                new com.geometry.core.math.Vec3(-1f, -1f, 0f),
                new com.geometry.core.math.Vec3(1f, -1f, 0f),
                new com.geometry.core.math.Vec3(1f, 1f, 0f),
                new com.geometry.core.math.Vec3(-1f, 1f, 0f)
        );
        Mesh mesh = poly.getMesh();
        assertEquals(4, mesh.getVertexCount());
        assertEquals(2, mesh.getFaceCount());
    }

    @Test
    public void testPolygonInvalidPoints() {
        try {
            new Polygon(
                    new com.geometry.core.math.Vec3(0f, 0f, 0f),
                    new com.geometry.core.math.Vec3(1f, 0f, 0f)
            );
            fail("Should throw for < 3 points");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("3 vertices"));
        }
    }

    // ---- Cube tests ----

    @Test
    public void testCubeMesh() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh mesh = cube.getMesh();

        assertEquals(8, mesh.getVertexCount());
        assertEquals(12, mesh.getFaceCount());
    }

    @Test
    public void testCubeAsymmetric() {
        Cube cube = new Cube(1f, 2f, 3f);
        Mesh mesh = cube.getMesh();

        assertEquals(8, mesh.getVertexCount());
        assertEquals(12, mesh.getFaceCount());
    }

    @Test
    public void testCubeParameterChange() {
        Cube cube = new Cube(2f, 2f, 2f);
        Mesh before = cube.getMesh();

        cube.setWidth(4f);
        Mesh after = cube.getMesh();

        assertNotSame(before, after);
        assertEquals(8, after.getVertexCount());
    }

    // ---- Cylinder tests ----

    @Test
    public void testCylinderMesh32Segments() {
        Cylinder cylinder = new Cylinder(1f, 2f, 32);
        Mesh mesh = cylinder.getMesh();

        // Bottom: 32 + 1 center = 33
        // Top: 32 + 1 center = 33
        // Total: 66 vertices
        assertEquals(66, mesh.getVertexCount());
        // Bottom cap: 32, Top cap: 32, Side: 64
        assertEquals(128, mesh.getFaceCount());
    }

    @Test
    public void testCylinderMesh8Segments() {
        Cylinder cylinder = new Cylinder(1f, 2f, 8);
        Mesh mesh = cylinder.getMesh();

        assertEquals(18, mesh.getVertexCount()); // 8+1 + 8+1
        assertEquals(32, mesh.getFaceCount()); // 8 + 8 + 16
    }

    @Test
    public void testCylinderParameterChange() {
        Cylinder cylinder = new Cylinder(1f, 2f, 32);
        Mesh before = cylinder.getMesh();

        cylinder.setHeight(4f);
        Mesh after = cylinder.getMesh();

        assertNotSame(before, after);
        assertEquals(66, after.getVertexCount());
    }

    @Test
    public void testCylinderInvalidSegments() {
        try {
            new Cylinder(1f, 2f, 2);
            fail("Should throw for segments < 3");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(">= 3"));
        }
    }

    // ---- Cone tests ----

    @Test
    public void testConeMesh() {
        Cone cone = new Cone(1f, 2f, 32);
        Mesh mesh = cone.getMesh();

        // 1 apex + 32 base + 1 base center = 34 vertices
        assertEquals(34, mesh.getVertexCount());
        // Side: 32, Base: 32 = 64 triangles
        assertEquals(64, mesh.getFaceCount());
    }

    @Test
    public void testConeParameterChange() {
        Cone cone = new Cone(1f, 2f, 16);
        Mesh before = cone.getMesh();

        cone.setRadius(2f);
        Mesh after = cone.getMesh();

        assertNotSame(before, after);
        // 1 apex + 16 base + 1 base center = 18
        assertEquals(18, after.getVertexCount());
    }

    // ---- Sphere tests ----

    @Test
    public void testSphereMesh() {
        Sphere sphere = new Sphere(1f, 16, 8);
        Mesh mesh = sphere.getMesh();

        // North pole + 8 rings * 16 segments + south pole
        int expectedVertices = 1 + 8 * 16 + 1;
        assertEquals(expectedVertices, mesh.getVertexCount());

        // North pole: 16, 7 middle bands * 32, South pole: 16 = 16 + 224 + 16 = 256
        int expectedFaces = 16 + 7 * 16 * 2 + 16;
        assertEquals(expectedFaces, mesh.getFaceCount());
    }

    @Test
    public void testSphereMinimal() {
        Sphere sphere = new Sphere(1f, 3, 2);
        Mesh mesh = sphere.getMesh();

        // 1 + 2*3 + 1 = 8 vertices
        assertEquals(8, mesh.getVertexCount());
    }

    @Test
    public void testSphereParameterChange() {
        Sphere sphere = new Sphere(1f, 16, 8);
        Mesh before = sphere.getMesh();

        sphere.setRadius(2f);
        Mesh after = sphere.getMesh();

        assertNotSame(before, after);
        assertEquals(before.getVertexCount(), after.getVertexCount());
    }

    // ---- Transform tests ----

    @Test
    public void testObjectHasTransform() {
        Cube cube = new Cube(1f, 1f, 1f);
        Transform t = cube.getTransform();
        assertNotNull(t);
        assertEquals(com.geometry.core.math.Vec3.ZERO, t.getPosition());
        assertEquals(new com.geometry.core.math.Vec3(1f, 1f, 1f), t.getScale());
    }

    @Test
    public void testObjectSetTransform() {
        Cylinder cylinder = new Cylinder(1f, 2f, 32);
        Transform original = cylinder.getTransform();

        Transform translated = new Transform(
                new com.geometry.core.math.Vec3(1f, 2f, 3f),
                new com.geometry.core.math.Vec3(0f, 0f, 0f),
                new com.geometry.core.math.Vec3(1f, 1f, 1f)
        );
        cylinder.setTransform(translated);
        assertEquals(translated, cylinder.getTransform());
    }

    // ---- GeometryObject interface ----

    @Test
    public void testAllObjectsImplementGeometryObject() {
        GeometryObject[] objects = {
                new Rectangle(2f, 2f),
                new Circle(1f, 8),
                new Polygon(
                        new com.geometry.core.math.Vec3(0f, 0f, 0f),
                        new com.geometry.core.math.Vec3(1f, 0f, 0f),
                        new com.geometry.core.math.Vec3(0f, 1f, 0f)
                ),
                new Cube(1f, 1f, 1f),
                new Cylinder(1f, 2f, 32),
                new Cone(1f, 2f, 32),
                new Sphere(1f, 16, 8)
        };

        for (GeometryObject obj : objects) {
            assertNotNull(obj.getMesh());
            assertNotNull(obj.getTransform());
            obj.updateMesh(); // Should not throw
            assertFalse(obj.getMesh().isEmpty());
        }
    }
}
