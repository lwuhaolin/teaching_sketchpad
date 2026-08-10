package com.geometry.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Phase 03 - Index Buffer Object (IBO/EBO) wrapper.
 *
 * Holds the element indices that define which vertices form each triangle.
 * Enables index-based rendering to reduce vertex duplication.
 */
public class IndexBuffer {

    private final int bufferId;
    private final int indexCount;

    /**
     * Create and upload index data to the GPU.
     *
     * @param indices short array of triangle vertex indices
     */
    public IndexBuffer(short[] indices) {
        this.indexCount = indices.length;
        this.bufferId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Bind this buffer as the element array buffer.
     */
    public void bind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferId);
    }

    /**
     * Unbind the element array buffer.
     */
    public void unbind() {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    /**
     * Return the number of indices.
     */
    public int getIndexCount() {
        return indexCount;
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
