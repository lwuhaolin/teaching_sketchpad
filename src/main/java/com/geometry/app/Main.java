package com.geometry.app;

import com.geometry.runtime.ApplicationLauncher;

/**
 * Phase 12 - Main entry point.
 *
 * Geometry Teaching Engine
 *
 * Entry point that launches the application via ApplicationLauncher.
 * The launcher handles:
 *   - Runtime environment detection
 *   - System compatibility checks
 *   - Configuration loading
 *   - Resource initialization
 *   - UI window creation
 *   - Crash reporting
 *
 * Run with:
 *   mvn exec:java
 *   java -cp target/classes:... com.geometry.app.Main
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class Main {

    public static void main(String[] args) {
        // Launch the application
        ApplicationLauncher launcher = ApplicationLauncher.create();

        // Print startup info
        System.out.println(launcher.getVersion().getFullVersionString());
        System.out.println(launcher.getEnvironment().toSummary());
        System.out.println(launcher.getSystemCheck().toSummary());

        // Demo: add some geometry objects to the scene
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(2f, 2f, 2f);
        launcher.getScene().addObject("cube_001", cube);

        com.geometry.core.geometry.Cylinder cylinder =
                new com.geometry.core.geometry.Cylinder(1f, 3f, 16);
        launcher.getScene().addObject("cylinder_001", cylinder);

        com.geometry.core.geometry.Sphere sphere = new com.geometry.core.geometry.Sphere(1.5f, 16, 8);
        launcher.getScene().addObject("sphere_001", sphere);

        System.out.println("Objects in scene: " + launcher.getScene().getObjectCount());

        // Start the application
        launcher.start();

        // Note: In a full application, the main loop would run here.
        // The application stays running until stopped.
        System.out.println("Application ready. Press Ctrl+C to exit.");

        // Keep the main thread alive so the Swing window stays visible.
        // The window will be closed via JFrame.EXIT_ON_CLOSE or Ctrl+C.
        while (launcher.isRunning()) {
            try {
                Thread.sleep(500);
                launcher.update();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        launcher.stop();
    }
}
