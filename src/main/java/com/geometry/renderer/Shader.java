package com.geometry.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Phase 03 - OpenGL shader program wrapper.
 *
 * Loads vertex and fragment shader source from classpath resources,
 * compiles them, links a program, and provides uniform/attribute access.
 *
 * Shader source files are stored under:
 *   resources/shader/vertex.glsl
 *   resources/shader/fragment.glsl
 *
 * Design:
 *   - Each Shader instance owns one linked GL program.
 *   - Uniforms are cached by name to avoid repeated glGetUniformLocation calls.
 *   - Must be bound (use()) before any draw call that needs this program.
 *
 * @see MeshRenderer
 */
public class Shader {

    private final int programId;
    private final Map<String, Integer> uniformCache;

    /**
     * Create a shader program from vertex and fragment source strings.
     *
     * @param vertexSource   GLSL source for the vertex shader
     * @param fragmentSource GLSL source for the fragment shader
     */
    public Shader(String vertexSource, String fragmentSource) {
        int vs = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
        int fs = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);

        this.programId = GL20.glCreateProgram();
        GL20.glAttachShader(programId, vs);
        GL20.glAttachShader(programId, fs);
        GL20.glLinkProgram(programId);

        int linked = GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS);
        if (linked == 0) {
            String infoLog = GL20.glGetProgramInfoLog(programId, 1024);
            GL20.glDeleteProgram(programId);
            throw new RuntimeException("Shader program link failed:\n" + infoLog);
        }

        GL20.glDetachShader(programId, vs);
        GL20.glDetachShader(programId, fs);
        GL20.glDeleteShader(vs);
        GL20.glDeleteShader(fs);

        this.uniformCache = new HashMap<>();
    }

    /**
     * Load a shader source from the classpath.
     *
     * @param path relative path under the classpath root (e.g. "shader/vertex.glsl")
     * @return the file contents as a String
     */
    public static String loadSource(String path) {
        InputStream is = Shader.class.getClassLoader().getResourceAsStream(path);
        if (is == null) {
            throw new IllegalArgumentException("Shader source not found: " + path);
        }
        try {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shader source: " + path, e);
        }
    }

    /**
     * Bind this shader program so subsequent GL calls use it.
     */
    public void use() {
        GL20.glUseProgram(programId);
    }

    /**
     * Unbind the current shader program.
     */
    public void disable() {
        GL20.glUseProgram(0);
    }

    /**
     * Get the GL program ID.
     */
    public int getProgramId() {
        return programId;
    }

    /**
     * Set a float uniform by name.
     *
     * @param name  uniform variable name in the GLSL shader
     * @param value uniform value
     */
    public void setFloat(String name, float value) {
        GL20.glUniform1f(getUniformLocation(name), value);
    }

    /**
     * Set a Vec3 uniform by name (3 floats).
     *
     * @param name  uniform variable name
     * @param v     the vector value
     */
    public void setVec3(String name, com.geometry.core.math.Vec3 v) {
        GL20.glUniform3f(getUniformLocation(name), v.x, v.y, v.z);
    }

    /**
     * Set a float array uniform by name.
     * Supports 16-float arrays (e.g. 4x4 matrices).
     *
     * @param name   uniform variable name
     * @param values array of floats
     */
    public void setFloatArray(String name, float[] values) {
        GL20.glUniform1fv(getUniformLocation(name), values);
    }

    private int getUniformLocation(String name) {
        Integer loc = uniformCache.get(name);
        if (loc == null) {
            loc = GL20.glGetUniformLocation(programId, name);
            if (loc < 0) {
                throw new IllegalArgumentException(
                        "Uniform '" + name + "' not found in shader program");
            }
            uniformCache.put(name, loc);
        }
        return loc;
    }

    private int compileShader(int type, String source) {
        int shaderId = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderId, source);
        GL20.glCompileShader(shaderId);

        int compiled = GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS);
        if (compiled == 0) {
            String infoLog = GL20.glGetShaderInfoLog(shaderId, 1024);
            GL20.glDeleteShader(shaderId);
            throw new RuntimeException("Shader compile failed (" + type + "):\n" + infoLog);
        }
        return shaderId;
    }

    /**
     * Clean up the GL program.
     */
    public void dispose() {
        if (programId != 0) {
            GL20.glDeleteProgram(programId);
        }
    }
}
