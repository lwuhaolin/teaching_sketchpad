package com.geometry.renderer;

import com.geometry.core.mesh.Face;
import com.geometry.core.mesh.Mesh;
import com.geometry.core.mesh.Vertex;
import com.geometry.core.transform.Transform;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 03 - Renders a single Mesh to the screen.
 *
 * Uploads Mesh vertex and face data to GPU buffers (VBO + EBO) and creates
 * a VAO with the correct vertex attribute layout.
 *
 * Vertex attribute layout (interleaved):
 *   offset 0: position  (3 floats: x, y, z)
 *   offset 3: normal    (3 floats: nx, ny, nz)
 *   offset 6: uv        (2 floats: u, v)
 *   stride: 8 floats per vertex
 *
 * Usage:
 *   MeshRenderer mr = new MeshRenderer(mesh);
 *   mr.render(transform, color);
 *   mr.dispose(); // when mesh is no longer needed
 *
 * Not thread-safe. All calls must come from the GL thread.
 */
public class MeshRenderer {

    private static final int STRIDE = 8; // floats per vertex
    private static final int POS_OFFSET = 0;
    private static final int NORM_OFFSET = 3;
    private static final int UV_OFFSET = 6;

    private final VertexArray vao;
    private final VertexBuffer vbo;
    private final IndexBuffer ebo;
    private final int indexCount;

    /**
     * Build GPU buffers from a Mesh.
     *
     * @param mesh the mesh to upload; must have faces
     * @throws IllegalArgumentException if the mesh is empty
     */
    public MeshRenderer(Mesh mesh) {
        if (mesh.isEmpty()) {
            throw new IllegalArgumentException("Cannot render an empty mesh");
        }

        // 1. Build interleaved vertex data
        List<Float> floatList = new ArrayList<>();
        for (Vertex v : mesh.getVertices()) {
            floatList.add(v.getPosition().x);
            floatList.add(v.getPosition().y);
            floatList.add(v.getPosition().z);
            if (v.getNormal() != null) {
                floatList.add(v.getNormal().x);
                floatList.add(v.getNormal().y);
                floatList.add(v.getNormal().z);
            } else {
                floatList.add(0f);
                floatList.add(0f);
                floatList.add(1f); // default outward normal
            }
            if (v.getUv() != null) {
                floatList.add(v.getUv().x);
                floatList.add(v.getUv().y);
            } else {
                floatList.add(0f);
                floatList.add(0f);
            }
        }
        float[] vertexData = new float[floatList.size()];
        for (int i = 0; i < floatList.size(); i++) {
            vertexData[i] = floatList.get(i);
        }

        // 2. Build index data
        int faceCount = mesh.getFaceCount();
        int[] faceIdx = new int[faceCount * 3];
        for (int i = 0; i < faceCount; i++) {
            Face f = mesh.getFace(i);
            faceIdx[i * 3] = f.getVertexIndex(0);
            faceIdx[i * 3 + 1] = f.getVertexIndex(1);
            faceIdx[i * 3 + 2] = f.getVertexIndex(2);
        }
        short[] indices = new short[faceIdx.length];
        for (int i = 0; i < faceIdx.length; i++) {
            indices[i] = (short) faceIdx[i];
        }
        this.indexCount = indices.length;

        // 3. Create VAO
        this.vao = new VertexArray();
        vao.bind();

        // 4. Create and upload VBO
        this.vbo = new VertexBuffer(vertexData);
        vbo.bind();
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, STRIDE * 4, 0);
        GL20.glEnableVertexAttribArray(0); // position

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, STRIDE * 4, 3 * 4);
        GL20.glEnableVertexAttribArray(1); // normal

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, STRIDE * 4, 6 * 4);
        GL20.glEnableVertexAttribArray(2); // uv

        vbo.unbind();

        // 5. Create and upload EBO
        this.ebo = new IndexBuffer(indices);

        vao.unbind();
    }

    /**
     * Render this mesh with the given model transform and color.
     *
     * @param shader   active Shader program
     * @param transform model-space transform
     * @param color    RGBA color (r, g, b, a) — values in [0, 1]
     */
    public void render(Shader shader, Transform transform, float[] color) {
        vao.bind();
        ebo.bind();

        shader.setFloatArray("uModelMatrix", transformToOpenGLMatrix(transform));
        shader.setFloat("uColorR", color[0]);
        shader.setFloat("uColorG", color[1]);
        shader.setFloat("uColorB", color[2]);
        shader.setFloat("uColorA", color.length > 3 ? color[3] : 1.0f);

        GL11.glDrawElements(GL11.GL_TRIANGLES, indexCount, GL11.GL_UNSIGNED_SHORT, 0);

        ebo.unbind();
        vao.unbind();
    }

    /**
     * Render without color override (uses shader default).
     *
     * @param shader    active Shader program
     * @param transform model-space transform
     */
    public void render(Shader shader, Transform transform) {
        float[] defaultColor = {1.0f, 1.0f, 1.0f, 1.0f};
        render(shader, transform, defaultColor);
    }

    /**
     * Convert a Transform to a 4x4 column-major float array for OpenGL.
     * Handles translation, rotation (Euler angles in degrees), and scale.
     */
    private float[] transformToOpenGLMatrix(Transform t) {
        com.geometry.core.math.Vec3 pos = t.getPosition();
        com.geometry.core.math.Vec3 rot = t.getRotation();
        com.geometry.core.math.Vec3 scale = t.getScale();

        float sx = (float) Math.sin(Math.toRadians(rot.x));
        float cx = (float) Math.cos(Math.toRadians(rot.x));
        float sy = (float) Math.sin(Math.toRadians(rot.y));
        float cy = (float) Math.cos(Math.toRadians(rot.y));
        float sz = (float) Math.sin(Math.toRadians(rot.z));
        float cz = (float) Math.cos(Math.toRadians(rot.z));

        // Rotation matrix (XYZ Euler)
        float m00 = cy * cz;               float m01 = cz * sx * sy - cx * sz;  float m02 = sx * sz + cx * cz * sy;
        float m10 = cy * sz;               float m11 = cx * cz + sx * sy * sz;  float m12 = cx * sy * sz - cz * sx;
        float m20 = -sy;                   float m21 = cy * sx;                 float m22 = cx * cy;

        float[] matrix = new float[16];
        // Column-major: matrix[row + col*4]
        matrix[0] = m00 * scale.x;  matrix[1] = m10 * scale.x;  matrix[2] = m20 * scale.x;  matrix[3] = 0f;
        matrix[4] = m01 * scale.y;  matrix[5] = m11 * scale.y;  matrix[6] = m21 * scale.y;  matrix[7] = 0f;
        matrix[8] = m02 * scale.z;  matrix[9] = m12 * scale.z;  matrix[10] = m22 * scale.z; matrix[11] = 0f;
        matrix[12] = pos.x;         matrix[13] = pos.y;         matrix[14] = pos.z;         matrix[15] = 1f;

        return matrix;
    }

    /**
     * Delete all GPU resources held by this MeshRenderer.
     */
    public void dispose() {
        vao.dispose();
        vbo.dispose();
        ebo.dispose();
    }
}
