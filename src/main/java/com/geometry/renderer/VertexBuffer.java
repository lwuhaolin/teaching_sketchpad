package com.geometry.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Phase 03 - Vertex Buffer Object (VBO) wrapper.
 *
 * Uploads vertex attribute data (positions, normals, UVs) to GPU memory.
 * Each MeshRenderer may hold one VBA (combined VBO + vertex attributes).
 *
 * Design:
 *   - Created once per Mesh upload.
 *   - Buffer data is immutable after creation.
 *   - Call dispose() when the mesh is destroyed to avoid GPU memory leaks.
 */
public class VertexBuffer {

    private final int bufferId;

    /**
     * Create and upload vertex data to the GPU.
     *
     * @param data float array containing interleaved vertex attributes
     *             Expected layout per vertex: x, y, z, nx, ny, nz, u, v
     *             (8 floats per vertex)
     */
    public VertexBuffer(float[] data) {
        this.bufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Bind this buffer so vertex attributes can be configured or read.
     */
    public void bind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferId);
    }

    /**
     * Unbind the currently bound array buffer.
     */
    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Delete the GL buffer object.
     */
    public void dispose() {
        if (bufferId != 0) {
            GL15.glDeleteBuffers(bufferId);
        }
    }

    public int getBufferId() {
        return bufferId;
    }
}
