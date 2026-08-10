package com.geometry.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Phase 03 - Vertex Array Object (VAO) wrapper.
 *
 * A VAO stores the vertex attribute configuration (which buffers, strides, offsets)
 * so that a single glDrawElements call can render the entire mesh.
 *
 * GL30 is available in OpenGL 3.0+ and in the GL_ARB_vertex_array_object extension.
 * LWJGL 3.3.1 always includes GL30, so this is safe for all supported platforms.
 */
public class VertexArray {

    private final int vaoId;

    /**
     * Create an empty VAO. Use {@link #bind()} and vertex attribute calls
     * to configure it, then call {@link #unbind()}.
     */
    public VertexArray() {
        this.vaoId = GL30.glGenVertexArrays();
    }

    /**
     * Bind this VAO so subsequent glVertexAttribPointer calls apply to it.
     */
    public void bind() {
        GL30.glBindVertexArray(vaoId);
    }

    /**
     * Unbind the currently bound VAO.
     */
    public void unbind() {
        GL30.glBindVertexArray(0);
    }

    /**
     * Delete the VAO and release GPU resources.
     */
    public void dispose() {
        if (vaoId != 0) {
            GL30.glDeleteVertexArrays(vaoId);
        }
    }

    public int getVaoId() {
        return vaoId;
    }
}
