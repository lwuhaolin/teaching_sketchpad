package com.geometry.renderer;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cone;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Sphere;
import com.geometry.core.transform.Transform;
import com.geometry.core.math.Vec3;

/**
 * Phase 03 - Renderer demo application.
 *
 * Demonstrates the full rendering pipeline:
 *   1. Creates an OpenGLRenderer with a GLFW window
 *   2. Sets up a Camera
 *   3. Renders a Cube with a blue color
 *   4. Runs a simple render loop (click X to close)
 *
 * This class is intentionally simple — it is a proof-of-concept for Phase 03.
 * Future phases will replace this with a full application featuring:
 *   - Scene management (Phase 04)
 *   - Interaction / camera orbit (Phase 05)
 *   - Tool system (Phase 06)
 *   - UI (Phase 11)
 *
 * Run from command line:
 *   mvn exec:java -Dexec.mainClass="com.geometry.renderer.RendererDemo"
 *
 * Or from IDE: run main() directly.
 */
public class RendererDemo {

    public static void main(String[] args) {
        System.out.println("=== Geometry Teaching Engine — Renderer Demo ===");
        System.out.println();

        // Create the renderer
        OpenGLRenderer renderer = new OpenGLRenderer();

        // Initialize OpenGL and create the window
        renderer.initialize();

        try {
            // Add a Cube to the render queue
            Cube cube = new Cube(2f, 2f, 2f);
            // Offset it slightly so it's visible in the camera view
            cube.setTransform(new Transform(
                    new Vec3(0f, 0f, 0f),
                    new Vec3(0f, 45f, 0f),  // 45 degree yaw for a nice angle
                    new Vec3(1f, 1f, 1f)
            ));
            renderer.addGeometryObject(cube, 0.2f, 0.5f, 0.9f); // blue

            // Also add a Sphere
            Sphere sphere = new Sphere(0.8f, 16, 8);
            sphere.setTransform(new Transform(
                    new Vec3(3f, 0f, 0f),
                    new Vec3(0f, 0f, 0f),
                    new Vec3(1f, 1f, 1f)
            ));
            renderer.addGeometryObject(sphere, 0.9f, 0.3f, 0.3f); // red

            // Also add a Cylinder
            Cylinder cylinder = new Cylinder(0.6f, 1.5f, 24);
            cylinder.setTransform(new Transform(
                    new Vec3(-3f, 0f, 0f),
                    new Vec3(0f, 0f, 0f),
                    new Vec3(1f, 1f, 1f)
            ));
            renderer.addGeometryObject(cylinder, 0.3f, 0.8f, 0.3f); // green

            System.out.println("Window opened. A cube, sphere, and cylinder are rendered.");
            System.out.println("Close the window to exit.");
            System.out.println();

            // Render loop
            while (!renderer.shouldClose()) {
                renderer.clear();
                renderer.render();
                renderer.swapBuffers();
            }

        } finally {
            renderer.shutdown();
        }

        System.out.println("Demo finished.");
    }
}
