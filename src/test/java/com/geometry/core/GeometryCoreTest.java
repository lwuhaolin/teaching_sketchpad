package com.geometry.core;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.math.MathUtil;
import com.geometry.core.math.Vec2;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;
import com.geometry.core.transform.Transform;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Phase 01 - Tests for the Geometry Core module.
 *
 * Covers: Vec3 arithmetic, Transform, Mesh construction, and GeometryObject interface.
 */
public class GeometryCoreTest {

    // ---- Vec3 tests ----

    @Test
    public void testVec3Add() {
        Vec3 a = new Vec3(1f, 2f, 3f);
        Vec3 b = new Vec3(4f, 5f, 6f);
        Vec3 result = a.add(b);
        assertEquals(5f, result.x, 1e-6f);
        assertEquals(7f, result.y, 1e-6f);
        assertEquals(9f, result.z, 1e-6f);
    }

    @Test
    public void testVec3Subtract() {
        Vec3 a = new Vec3(4f, 5f, 6f);
        Vec3 b = new Vec3(1f, 2f, 3f);
        Vec3 result = a.subtract(b);
        assertEquals(3f, result.x, 1e-6f);
        assertEquals(3f, result.y, 1e-6f);
        assertEquals(3f, result.z, 1e-6f);
    }

    @Test
    public void testVec3Multiply() {
        Vec3 v = new Vec3(2f, 3f, 4f);
        Vec3 result = v.multiply(2f);
        assertEquals(4f, result.x, 1e-6f);
        assertEquals(6f, result.y, 1e-6f);
        assertEquals(8f, result.z, 1e-6f);
    }

    @Test
    public void testVec3Length() {
        Vec3 v = new Vec3(3f, 4f, 0f);
        assertEquals(5f, v.length(), 1e-6f);
    }

    @Test
    public void testVec3Normalize() {
        Vec3 v = new Vec3(3f, 4f, 0f);
        Vec3 result = v.normalize();
        assertEquals(0.6f, result.x, 1e-6f);
        assertEquals(0.8f, result.y, 1e-6f);
        assertEquals(0f, result.z, 1e-6f);
        assertEquals(1f, result.length(), 1e-6f);
    }

    @Test
    public void testVec3NormalizeZero() {
        Vec3 result = Vec3.ZERO.normalize();
        assertEquals(Vec3.ZERO, result);
    }

    @Test
    public void testVec3Dot() {
        Vec3 a = new Vec3(1f, 0f, 0f);
        Vec3 b = new Vec3(0f, 1f, 0f);
        assertEquals(0f, a.dot(b), 1e-6f);
    }

    @Test
    public void testVec3Cross() {
        Vec3 a = new Vec3(1f, 0f, 0f);
        Vec3 b = new Vec3(0f, 1f, 0f);
        Vec3 result = a.cross(b);
        assertEquals(0f, result.x, 1e-6f);
        assertEquals(0f, result.y, 1e-6f);
        assertEquals(1f, result.z, 1e-6f);
    }

    @Test
    public void testVec3Immutable() {
        Vec3 a = new Vec3(1f, 1f, 1f);
        Vec3 b = new Vec3(2f, 2f, 2f);
        Vec3 result = a.add(b);
        // a must not be modified
        assertEquals(1f, a.x, 1e-6f);
        assertEquals(1f, a.y, 1e-6f);
        assertEquals(1f, a.z, 1e-6f);
        // result must be correct
        assertEquals(3f, result.x, 1e-6f);
        assertEquals(3f, result.y, 1e-6f);
        assertEquals(3f, result.z, 1e-6f);
    }

    // ---- Vec2 tests ----

    @Test
    public void testVec2Add() {
        Vec2 a = new Vec2(1f, 2f);
        Vec2 b = new Vec2(3f, 4f);
        Vec2 result = a.add(b);
        assertEquals(4f, result.x, 1e-6f);
        assertEquals(6f, result.y, 1e-6f);
    }

    @Test
    public void testVec2Normalize() {
        Vec2 v = new Vec2(3f, 4f);
        Vec2 result = v.normalize();
        assertEquals(1f, result.length(), 1e-6f);
    }

    @Test
    public void testVec2Dot() {
        Vec2 a = new Vec2(1f, 0f);
        Vec2 b = new Vec2(0f, 1f);
        assertEquals(0f, a.dot(b), 1e-6f);
    }

    // ---- MathUtil tests ----

    @Test
    public void testDegreeToRadian() {
        assertEquals(0f, MathUtil.degreeToRadian(0f), 1e-6f);
        assertEquals(MathUtil.PI, MathUtil.degreeToRadian(180f), 1e-6f);
    }

    @Test
    public void testRadianToDegree() {
        assertEquals(0f, MathUtil.radianToDegree(0f), 1e-6f);
        assertEquals(180f, MathUtil.radianToDegree(MathUtil.PI), 1e-6f);
    }

    @Test
    public void testClamp() {
        assertEquals(5f, MathUtil.clamp(10f, 0f, 5f), 1e-6f);
        assertEquals(0f, MathUtil.clamp(-3f, 0f, 5f), 1e-6f);
        assertEquals(3f, MathUtil.clamp(3f, 0f, 5f), 1e-6f);
    }

    @Test
    public void testLerp() {
        assertEquals(0f, MathUtil.lerp(0f, 10f, 0f), 1e-6f);
        assertEquals(10f, MathUtil.lerp(0f, 10f, 1f), 1e-6f);
        assertEquals(5f, MathUtil.lerp(0f, 10f, 0.5f), 1e-6f);
    }

    @Test
    public void testApproxEqual() {
        assertTrue(MathUtil.approxEqual(1f, 1f + 1e-7f));
        assertFalse(MathUtil.approxEqual(1f, 2f));
    }

    // ---- Transform tests ----

    @Test
    public void testTransformIdentity() {
        Transform t = new Transform();
        assertEquals(Vec3.ZERO, t.getPosition());
        assertEquals(new Vec3(0f, 0f, 0f), t.getRotation());
        assertEquals(Vec3.ONE, t.getScale());
    }

    @Test
    public void testTransformTranslate() {
        Transform t = new Transform();
        Transform translated = t.translate(new Vec3(1f, 2f, 3f));
        assertEquals(new Vec3(1f, 2f, 3f), translated.getPosition());
        // original must be unchanged
        assertEquals(Vec3.ZERO, t.getPosition());
    }

    @Test
    public void testTransformScaleUniform() {
        Transform t = new Transform();
        Transform scaled = t.scaleUniform(2f);
        assertEquals(new Vec3(2f, 2f, 2f), scaled.getScale());
    }

    @Test
    public void testTransformRotate() {
        Transform t = new Transform();
        Transform rotated = t.rotate(new Vec3(90f, 0f, 0f));
        assertEquals(new Vec3(90f, 0f, 0f), rotated.getRotation());
    }

    // ---- Mesh tests ----

    @Test
    public void testMeshCreateTriangle() {
        Mesh mesh = new Mesh();
        int v0 = mesh.addVertex(new Vertex(new Vec3(0f, 0f, 0f)));
        int v1 = mesh.addVertex(new Vertex(new Vec3(1f, 0f, 0f)));
        int v2 = mesh.addVertex(new Vertex(new Vec3(0f, 1f, 0f)));
        mesh.addFace(v0, v1, v2);

        assertEquals(3, mesh.getVertexCount());
        assertEquals(0, mesh.getEdgeCount());
        assertEquals(1, mesh.getFaceCount());
    }

    @Test
    public void testMeshVerticesAreAccessible() {
        Mesh mesh = new Mesh();
        Vec3 pos = new Vec3(5f, -2f, 3f);
        int idx = mesh.addVertex(new Vertex(pos));
        assertEquals(pos, mesh.getVertex(idx).getPosition());
    }

    @Test
    public void testMeshEmpty() {
        Mesh mesh = new Mesh();
        assertTrue(mesh.isEmpty());
        mesh.addVertex(new Vertex(Vec3.ZERO));
        assertFalse(mesh.isEmpty());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMeshAddEdgeOutOfRange() {
        Mesh mesh = new Mesh();
        mesh.addEdge(0, 1); // no vertices exist yet
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMeshAddFaceOutOfRange() {
        Mesh mesh = new Mesh();
        mesh.addFace(0, 1, 2); // no vertices exist yet
    }

    // ---- GeometryObject interface test ----

    @Test
    public void testGeometryObjectInterfaceExists() {
        // Verify the interface exists and has the required methods.
        // We use reflection to check method signatures without needing a concrete implementation.
        try {
            GeometryObject.class.getMethod("getMesh");
            GeometryObject.class.getMethod("getTransform");
            GeometryObject.class.getMethod("updateMesh");
        } catch (NoSuchMethodException e) {
            fail("GeometryObject missing required method: " + e.getMessage());
        }
    }
}
