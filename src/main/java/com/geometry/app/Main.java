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

        // Start the application
        launcher.start();

        // Simple demo: add some geometry objects
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(2f, 2f, 2f);
        launcher.getScene().addObject("cube_001", cube);

        com.geometry.core.geometry.Cylinder cylinder =
                new com.geometry.core.geometry.Cylinder(1f, 3f, 16);
        launcher.getScene().addObject("cylinder_001", cylinder);

        com.geometry.core.geometry.Sphere sphere = new com.geometry.core.geometry.Sphere(1.5f, 16, 8);
        launcher.getScene().addObject("sphere_001", sphere);

        System.out.println("Objects in scene: " + launcher.getScene().getObjectCount());

        // Note: In a full application, the main loop would run here.
        // The application stays running until stopped.
        System.out.println("Application ready. Press Ctrl+C to exit.");

        // For headless / test environments, exit immediately.
        // In a real application, a display loop would keep this running.
        launcher.stop();
    }
}
