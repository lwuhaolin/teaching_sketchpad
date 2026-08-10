package com.geometry.scene;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Rectangle;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.renderer.OpenGLRenderer;
import com.geometry.renderer.RenderMode;

/**
 * Phase 04 - Demo that shows Scene management with OpenGL.
 *
 * Adds a Cube, Cylinder, and Rectangle to a Scene,
 * then renders them through the Scene → OpenGLRenderer pipeline.
 *
 * Run with:
 *   mvn exec:java -Dexec.mainClass="com.geometry.scene.SceneDemo"
 */
public class SceneDemo {

    public static void main(String[] args) {
        System.out.println("Scene Demo - Phase 04");
        System.out.println();

        OpenGLRenderer renderer = new OpenGLRenderer();
        renderer.initialize();

        Scene scene = new Scene();

        // Add Cube (center, blue)
        Cube cube = new Cube(2f, 2f, 2f);
        cube.setTransform(new Transform(
                new Vec3(0f, 0f, 0f),
                new Vec3(0f, 45f, 0f),
                new Vec3(1f, 1f, 1f)
        ));
        scene.addObject("cube_001", cube);

        // Add Cylinder (left, green)
        Cylinder cylinder = new Cylinder(0.6f, 1.5f, 24);
        cylinder.setTransform(new Transform(
                new Vec3(-3f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(1f, 1f, 1f)
        ));
        scene.addObject("cylinder_001", cylinder);

        // Add Rectangle (right, red, in XY plane z=0)
        Rectangle rectangle = new Rectangle(2f, 1.5f);
        rectangle.setTransform(new Transform(
                new Vec3(3f, 0f, 0f),
                new Vec3(0f, 0f, 0f),
                new Vec3(1f, 1f, 1f)
        ));
        scene.addObject("rectangle_001", rectangle);

        System.out.println("Scene: " + scene);
        System.out.println("Objects: " + scene.getObjectCount());
        System.out.println();

        System.out.println("Renderer initialized. Window opened.");
        System.out.println("Close the window to exit.");
        System.out.println();

        // Color palette: base colors + selected highlight
        float[] cubeColor = {0.2f, 0.5f, 0.9f, 1.0f};
        float[] cylinderColor = {0.3f, 0.8f, 0.3f, 1.0f};
        float[] rectangleColor = {0.9f, 0.3f, 0.3f, 1.0f};
        float[] selectedColor = {1.0f, 1.0f, 0.5f, 1.0f}; // yellow highlight

        while (!renderer.shouldClose()) {
            renderer.clear();

            // Update scene (regenerates meshes if params changed)
            scene.update();

            // Render each object individually with its color
            for (SceneObject obj : scene.getAllObjects()) {
                float[] color;
                if (obj.getId().equals("cube_001")) {
                    color = obj.isSelected() ? selectedColor : cubeColor;
                } else if (obj.getId().equals("cylinder_001")) {
                    color = obj.isSelected() ? selectedColor : cylinderColor;
                } else {
                    color = obj.isSelected() ? selectedColor : rectangleColor;
                }
                renderer.renderSceneObject(obj, color);
            }

            renderer.swapBuffers();
        }

        renderer.shutdown();
        System.out.println("Scene Demo finished.");
    }
}
