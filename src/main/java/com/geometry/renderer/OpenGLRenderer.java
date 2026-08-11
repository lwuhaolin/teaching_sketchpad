package com.geometry.renderer;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Phase 03 - OpenGL Renderer implementation.
 *
 * Manages the GLFW window, OpenGL context, shader program, and rendering loop.
 *
 * Rendering pipeline:
 *   1. clear()  → glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
 *   2. render() → for each geometry object:
 *      a. Compute model matrix from Transform
 *      b. Set projection and view uniforms
 *      c. Bind MeshRenderer (VAO + VBO + EBO)
 *      d. glDrawElements
 *
 * The Renderer does NOT handle:
 *   - Mouse / keyboard input (Phase 05)
 *   - Scene management (Phase 04)
 *   - Tool logic (Phase 06)
 *
 * To add a geometry object to the render queue:
 *   renderer.addGeometryObject(geometryObject, color)
 *
 * To render:
 *   renderer.render();  // called each frame from the application loop
 */
public class OpenGLRenderer implements Renderer, ModeSwitchableRenderer {

    // Window dimensions
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String WINDOW_TITLE = "Geometry Teaching Engine";

    // OpenGL clear color (dark gray background)
    private static final float CLEAR_R = 0.1f;
    private static final float CLEAR_G = 0.1f;
    private static final float CLEAR_B = 0.12f;
    private static final float CLEAR_A = 1.0f;

    // The geometry objects to render (list of pairs: object + its color)
    private final java.util.List<Renderable> renderQueue;

    // OpenGL resources
    private Window window;
    private Shader shader;
    private Camera camera;
    private RenderMode renderMode;

    /**
     * A geometry object ready to be rendered, paired with its display color.
     */
    public static class Renderable {
        public final com.geometry.core.geometry.GeometryObject geometryObject;
        public final float[] color; // RGBA in [0, 1]

        public Renderable(com.geometry.core.geometry.GeometryObject obj, float r, float g, float b) {
            this(obj, new float[]{r, g, b, 1.0f});
        }

        public Renderable(com.geometry.core.geometry.GeometryObject obj, float[] color) {
            this.geometryObject = obj;
            this.color = color;
        }
    }

    /**
     * Create a new OpenGL renderer with default 3D perspective mode.
     */
    public OpenGLRenderer() {
        this.renderQueue = new java.util.ArrayList<>();
        this.renderMode = RenderMode.MODE_3D;
    }

    // ------------------------------------------------------------------
    // Renderer interface implementation
    // ------------------------------------------------------------------

    /**
     * Initialize GLFW, create the window, set up OpenGL state and shaders.
     */
    @Override
    public void initialize() {
        // 1. Initialize GLFW
        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        // 2. Configure GLFW for OpenGL 3.3 Core Profile
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // required on macOS

        // 3. Create window
        long glfwWindow = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE,
                MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindow == 0L) {
            throw new RuntimeException("Failed to create GLFW window");
        }
        this.window = new Window(glfwWindow);

        // 4. Make context current and show window
        glfwMakeContextCurrent(glfwWindow);
        glfwSwapInterval(1); // Enable vsync
        glfwShowWindow(glfwWindow);

        // 5. Set up OpenGL state
        glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        glEnable(GL_DEPTH_TEST);
        glClearColor(CLEAR_R, CLEAR_G, CLEAR_B, CLEAR_A);

        // 6. Create shader
        this.shader = createDefaultShader();

        // 7. Create camera
        this.camera = new Camera(renderMode);

        // Print OpenGL info
        System.out.println("OpenGL Renderer initialized");
        System.out.println("  Vendor: " + glGetString(GL_VENDOR));
        System.out.println("  Renderer: " + glGetString(GL_RENDERER));
        System.out.println("  Version: " + glGetString(GL_VERSION));
    }

    /**
     * Clear the color and depth buffers.
     */
    @Override
    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Render all geometry objects in the queue.
     */
    @Override
    public void render() {
        if (shader == null || camera == null) {
            return;
        }

        shader.use();

        // Set projection and view uniforms
        shader.setFloatArray("uProjectionMatrix", camera.getProjectionMatrix());
        shader.setFloatArray("uViewMatrix", camera.getViewMatrix());

        for (Renderable r : renderQueue) {
            com.geometry.core.mesh.Mesh mesh = r.geometryObject.getMesh();
            com.geometry.core.transform.Transform transform = r.geometryObject.getTransform();

            // Create or reuse MeshRenderer for this mesh
            MeshRenderer meshRenderer = getOrMakeMeshRenderer(mesh);

            // Render with color
            meshRenderer.render(shader, transform, r.color);
        }

        shader.disable();
    }

    /**
     * Shutdown: destroy the window and release all OpenGL resources.
     */
    @Override
    public void shutdown() {
        // Dispose shader
        if (shader != null) {
            shader.dispose();
            shader = null;
        }

        // Dispose window
        if (window != null) {
            window.dispose();
            window = null;
        }

        // Dispose all cached MeshRenderers
        disposeAllMeshRenderers();

        // Terminate GLFW
        glfwTerminate();

        System.out.println("OpenGL Renderer shut down");
    }

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Add a geometry object to the render queue.
     *
     * @param geometryObject the object to render
     * @param r              red component [0, 1]
     * @param g              green component [0, 1]
     * @param b              blue component [0, 1]
     */
    public void addGeometryObject(com.geometry.core.geometry.GeometryObject geometryObject,
                                   float r, float g, float b) {
        if (geometryObject == null) {
            throw new IllegalArgumentException("GeometryObject cannot be null");
        }
        renderQueue.add(new Renderable(geometryObject, r, g, b));
    }

    /**
     * Render a single SceneObject directly (bypasses the render queue).
     *
     * @param sceneObject the SceneObject to render
     * @param color       RGBA color array (values in [0, 1])
     */
    public void renderSceneObject(
            com.geometry.scene.SceneObject sceneObject, float[] color) {
        if (sceneObject == null || !sceneObject.isVisible()) {
            return;
        }
        if (shader == null || camera == null) {
            return;
        }

        com.geometry.core.mesh.Mesh mesh = sceneObject.getGeometry().getMesh();
        com.geometry.core.transform.Transform transform = sceneObject.getEffectiveTransform();

        MeshRenderer meshRenderer = getOrMakeMeshRenderer(mesh);
        meshRenderer.render(shader, transform, color);
    }

    /**
     * Remove all objects from the render queue.
     */
    public void clearRenderQueue() {
        renderQueue.clear();
    }

    /**
     * Get the number of objects in the render queue.
     */
    public int getRenderQueueSize() {
        return renderQueue.size();
    }

    /**
     * Set the render mode (2D orthographic or 3D perspective).
     *
     * @param mode the new render mode
     */
    public void setRenderMode(RenderMode mode) {
        this.renderMode = mode;
        if (camera != null) {
            camera.setRenderMode(mode);
        }
    }

    /**
     * Get the current render mode.
     */
    public RenderMode getRenderMode() {
        return renderMode;
    }

    /**
     * Get the Camera used by this renderer.
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the window width.
     */
    public int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    /**
     * Get the window height.
     */
    public int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    /**
     * Check whether the window should close (user clicked X).
     */
    public boolean shouldClose() {
        return window != null && window.shouldClose();
    }

    /**
     * Swap buffers and poll GLFW events.
     * Call this once per frame after render().
     */
    public void swapBuffers() {
        if (window != null) {
            glfwSwapBuffers(window.getHandle());
            glfwPollEvents();
        }
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    /**
     * Create the default shader program from embedded GLSL source.
     */
    private Shader createDefaultShader() {
        String vertexSource =
                "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "layout (location = 1) in vec3 aNormal;\n" +
                "layout (location = 2) in vec2 aUv;\n" +
                "\n" +
                "uniform mat4 uModelMatrix;\n" +
                "uniform mat4 uViewMatrix;\n" +
                "uniform mat4 uProjectionMatrix;\n" +
                "\n" +
                "out vec3 vNormal;\n" +
                "out vec2 vUv;\n" +
                "out vec3 vFragPos;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * vec4(aPos, 1.0);\n" +
                "    vFragPos = aPos;\n" +
                "    vNormal = aNormal;\n" +
                "    vUv = aUv;\n" +
                "}\n";

        String fragmentSource =
                "#version 330 core\n" +
                "in vec3 vNormal;\n" +
                "in vec2 vUv;\n" +
                "in vec3 vFragPos;\n" +
                "\n" +
                "uniform vec4 uColor;\n" +
                "\n" +
                "out vec4 FragColor;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                "    FragColor = uColor;\n" +
                "}\n";

        return new Shader(vertexSource, fragmentSource);
    }

    /**
     * Render loop entry point.
     * Runs until the window is closed or shouldClose() returns true.
     */
    public void run() {
        initialize();
        try {
            while (!shouldClose()) {
                clear();
                render();
                swapBuffers();
            }
        } finally {
            shutdown();
        }
    }

    // ---- MeshRenderer cache (one per unique Mesh instance) ----
    private final java.util.Map<com.geometry.core.mesh.Mesh, MeshRenderer> meshRendererCache =
            new java.util.HashMap<>();

    private MeshRenderer getOrMakeMeshRenderer(com.geometry.core.mesh.Mesh mesh) {
        MeshRenderer mr = meshRendererCache.get(mesh);
        if (mr == null) {
            mr = new MeshRenderer(mesh);
            meshRendererCache.put(mesh, mr);
        }
        return mr;
    }

    private void disposeAllMeshRenderers() {
        for (MeshRenderer mr : meshRendererCache.values()) {
            mr.dispose();
        }
        meshRendererCache.clear();
    }
}
