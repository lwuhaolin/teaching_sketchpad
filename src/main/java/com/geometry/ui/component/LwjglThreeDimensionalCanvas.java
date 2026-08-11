package com.geometry.ui.component;

import com.geometry.core.geometry.Circle;
import com.geometry.core.geometry.GeometryObject;
import com.geometry.core.geometry.Polygon;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.mesh.Mesh;
import com.geometry.renderer.MeshRenderer;
import com.geometry.renderer.Shader;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.ui.ViewMode;
import com.geometry.ui.canvas.CanvasCommandListener;
import com.geometry.ui.theme.EducationTheme;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * GPU-backed 3D teaching view embedded in the Swing workspace.
 *
 * This is intentionally a UI adapter: it observes the existing Scene and
 * renders its meshes without changing Geometry, Mesh, Scene or Tool state.
 */
public class LwjglThreeDimensionalCanvas extends GeometryCanvasView {

    private static final float MIN_DISTANCE = 2.5f;
    private static final float MAX_DISTANCE = 40f;
    private static final float DEFAULT_DISTANCE = 10f;

    private final Scene scene;
    private final OpenGlViewport viewport;
    private final Timer renderTimer;

    public LwjglThreeDimensionalCanvas(Scene scene, EducationTheme theme) {
        super(scene, null, theme);
        this.scene = scene;
        setViewMode(ViewMode.MODE_3D);
        setLayout(new BorderLayout());
        viewport = new OpenGlViewport();
        viewport.setPreferredSize(new Dimension(900, 620));
        add(viewport, BorderLayout.CENTER);
        renderTimer = new Timer(33, e -> viewport.renderFrame());
    }

    @Override
    public void setCommandListener(CanvasCommandListener commandListener) {
        super.setCommandListener(commandListener);
    }

    @Override
    public void setActiveTool(String activeTool) {
        super.setActiveTool(activeTool);
        viewport.setCursor("select".equals(activeTool)
                ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        renderTimer.start();
    }

    @Override
    public void removeNotify() {
        renderTimer.stop();
        viewport.disposeViewport();
        super.removeNotify();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        // The heavyweight AWTGLCanvas covers this component. Do not draw the
        // legacy Graphics2D pseudo-3D representation underneath it.
        super.paintComponent(graphics);
    }

    /** Resets only UI-owned camera state; geometry state remains untouched. */
    @Override
    public void resetView() {
        viewport.resetCamera();
        super.resetView();
    }

    private boolean isSolid(SceneObject object) {
        if (object == null || !object.isVisible()) {
            return false;
        }
        GeometryObject geometry = object.getGeometry();
        return !(geometry instanceof Rectangle)
                && !(geometry instanceof Circle)
                && !(geometry instanceof Polygon);
    }

    private static GLData createGlData() {
        GLData data = new GLData();
        data.doubleBuffer = true;
        data.depthSize = 24;
        data.majorVersion = 3;
        data.minorVersion = 3;
        data.profile = GLData.Profile.CORE;
        data.swapInterval = Integer.valueOf(1);
        return data;
    }

    private final class OpenGlViewport extends AWTGLCanvas {

        private final Map<Mesh, MeshRenderer> meshRenderers = new HashMap<Mesh, MeshRenderer>();
        private final float[] projection = new float[16];
        private final float[] view = new float[16];
        private final Matrix4f projectionMatrix = new Matrix4f();
        private final Matrix4f viewMatrix = new Matrix4f();
        private Shader shader;
        private boolean disposed;
        private float yaw = -35f;
        private float pitch = 24f;
        private float distance = DEFAULT_DISTANCE;
        private int dragX;
        private int dragY;

        OpenGlViewport() {
            super(createGlData());
            installCameraInput();
        }

        @Override
        public void initGL() {
            GL.createCapabilities();
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glEnable(GL_CULL_FACE);
            glClearColor(0.94f, 0.97f, 1.0f, 1.0f);
            shader = new Shader(vertexShaderSource(), fragmentShaderSource());
        }

        @Override
        public void paintGL() {
            if (disposed || shader == null) {
                return;
            }
            int width = Math.max(1, getWidth());
            int height = Math.max(1, getHeight());
            glViewport(0, 0, width, height);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            updateCamera(width, height);
            shader.use();
            shader.setFloatArray("uProjectionMatrix", projection);
            shader.setFloatArray("uViewMatrix", view);
            for (SceneObject object : scene.getAllObjects()) {
                if (isSolid(object)) {
                    renderObject(object);
                }
            }
            shader.disable();
            swapBuffers();
        }

        private void renderObject(SceneObject object) {
            Mesh mesh = object.getGeometry().getMesh();
            if (mesh.isEmpty()) {
                return;
            }
            MeshRenderer meshRenderer = meshRenderers.get(mesh);
            if (meshRenderer == null) {
                meshRenderer = new MeshRenderer(mesh);
                meshRenderers.put(mesh, meshRenderer);
            }
            meshRenderer.render(shader, object.getEffectiveTransform(), colorFor(object));
        }

        private void updateCamera(int width, int height) {
            float yawRadians = (float) Math.toRadians(yaw);
            float pitchRadians = (float) Math.toRadians(pitch);
            Vector3f eye = new Vector3f(
                    distance * (float) (Math.cos(pitchRadians) * Math.cos(yawRadians)),
                    distance * (float) Math.sin(pitchRadians),
                    distance * (float) (Math.cos(pitchRadians) * Math.sin(yawRadians)));
            projectionMatrix.identity().perspective((float) Math.toRadians(45f),
                    (float) width / (float) height, 0.1f, 100f).get(projection);
            viewMatrix.identity().lookAt(eye, new Vector3f(0f, 0f, 0f),
                    new Vector3f(0f, 1f, 0f)).get(view);
        }

        private float[] colorFor(SceneObject object) {
            GeometryObject geometry = object.getGeometry();
            String type = geometry.getClass().getSimpleName();
            if ("Cube".equals(type)) return new float[]{0.22f, 0.49f, 0.88f, 1f};
            if ("Cylinder".equals(type)) return new float[]{0.96f, 0.60f, 0.16f, 1f};
            if ("Sphere".equals(type)) return new float[]{0.53f, 0.36f, 0.87f, 1f};
            if ("Cone".equals(type)) return new float[]{0.05f, 0.70f, 0.73f, 1f};
            return new float[]{0.39f, 0.49f, 0.61f, 1f};
        }

        private void installCameraInput() {
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent event) {
                    dragX = event.getX();
                    dragY = event.getY();
                    requestFocus();
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent event) {
                    yaw += (event.getX() - dragX) * 0.45f;
                    pitch = Math.max(-80f, Math.min(80f, pitch + (event.getY() - dragY) * 0.35f));
                    dragX = event.getX();
                    dragY = event.getY();
                    renderFrame();
                }
            });
            addMouseWheelListener(new java.awt.event.MouseWheelListener() {
                @Override public void mouseWheelMoved(MouseWheelEvent event) {
                    distance = Math.max(MIN_DISTANCE, Math.min(MAX_DISTANCE,
                            distance * (event.getWheelRotation() > 0 ? 1.12f : 0.89f)));
                    renderFrame();
                }
            });
        }

        void renderFrame() {
            if (isDisplayable() && !disposed) {
                render();
            }
        }

        void resetCamera() {
            yaw = -35f;
            pitch = 24f;
            distance = DEFAULT_DISTANCE;
            renderFrame();
        }

        void disposeViewport() {
            if (disposed) {
                return;
            }
            disposed = true;
            boolean contextInitialized = shader != null;
            if (contextInitialized && isDisplayable()) {
                runInContext(new Runnable() {
                    @Override public void run() {
                        for (MeshRenderer renderer : meshRenderers.values()) {
                            renderer.dispose();
                        }
                        meshRenderers.clear();
                        if (shader != null) {
                            shader.dispose();
                            shader = null;
                        }
                    }
                });
            }
            // AWTGLCanvas.removeNotify() destroys the native context after
            // this adapter has released its GL resources.
        }

        private String vertexShaderSource() {
            return "#version 330 core\n"
                    + "layout (location = 0) in vec3 aPos;\n"
                    + "uniform mat4 uModelMatrix;\n"
                    + "uniform mat4 uViewMatrix;\n"
                    + "uniform mat4 uProjectionMatrix;\n"
                    + "void main() { gl_Position = uProjectionMatrix * uViewMatrix * uModelMatrix * vec4(aPos, 1.0); }\n";
        }

        private String fragmentShaderSource() {
            return "#version 330 core\n"
                    + "uniform float uColorR;\n"
                    + "uniform float uColorG;\n"
                    + "uniform float uColorB;\n"
                    + "uniform float uColorA;\n"
                    + "out vec4 FragColor;\n"
                    + "void main() { FragColor = vec4(uColorR, uColorG, uColorB, uColorA); }\n";
        }
    }
}
