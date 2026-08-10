package com.geometry.core.mesh;

import com.geometry.core.math.Vec2;
import com.geometry.core.math.Vec3;

/**
 * Phase 01 - Mesh vertex.
 *
 * Represents a single point in the mesh with position, normal, and UV coordinates.
 * Normals and UVs are optional — null means they are not set.
 *
 * Mutable: properties can be changed after construction.
 */
public class Vertex {

    private Vec3 position;
    private Vec3 normal;
    private Vec2 uv;

    public Vertex(Vec3 position) {
        this(position, null, null);
    }

    public Vertex(Vec3 position, Vec3 normal, Vec2 uv) {
        this.position = position;
        this.normal = normal;
        this.uv = uv;
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public Vec3 getNormal() {
        return normal;
    }

    public void setNormal(Vec3 normal) {
        this.normal = normal;
    }

    public Vec2 getUv() {
        return uv;
    }

    public void setUv(Vec2 uv) {
        this.uv = uv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vertex vertex = (Vertex) o;
        return position.equals(vertex.position)
                && java.util.Objects.equals(normal, vertex.normal)
                && java.util.Objects.equals(uv, vertex.uv);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + java.util.Objects.hashCode(normal);
        result = 31 * result + java.util.Objects.hashCode(uv);
        return result;
    }

    @Override
    public String toString() {
        return "Vertex{pos=" + position + ", normal=" + normal + ", uv=" + uv + "}";
    }
}
