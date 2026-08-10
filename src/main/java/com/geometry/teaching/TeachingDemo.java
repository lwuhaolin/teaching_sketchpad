package com.geometry.teaching;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.math.Vec3;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.HighlightAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;
import com.geometry.teaching.assistant.CoordinateSystem;
import com.geometry.teaching.assistant.Grid;
import com.geometry.teaching.assistant.HelperLine;
import com.geometry.teaching.construction.CircleConstruction;
import com.geometry.teaching.construction.LineConstruction;
import com.geometry.teaching.construction.PointConstruction;

/**
 * Phase 07 - Demo showing the Teaching System in action.
 *
 * This demo creates a scene with a Cube and Cylinder, adds annotations
 * (text labels, arrows, highlights), adds assistant objects (grid,
 * coordinate system), and runs through a simple lesson.
 *
 * Note: This demo runs in headless mode (no OpenGL window). Annotations
 * and assistants are created and managed but not visually rendered.
 *
 * Run with: java com.geometry.teaching.TeachingDemo
 */
public class TeachingDemo {

    public static void main(String[] args) {
        System.out.println("=== Geometry Teaching Engine - Teaching System Demo ===\n");

        // 1. Create scene with geometry objects
        Scene scene = new Scene();
        Cube cube = new Cube(2f, 2f, 2f);
        Cylinder cylinder = new Cylinder(1.5f, 3f, 32);
        SceneObject cubeObj = scene.addObject("cube", cube);
        SceneObject cylObj = scene.addObject("cylinder", cylinder);

        System.out.println("Scene objects:");
        System.out.println("  " + cubeObj);
        System.out.println("  " + cylObj);
        System.out.println();

        // 2. Create TeachingManager
        TeachingManager tm = new TeachingManager(scene, null);
        tm.setMode(TeachingMode.TEACHER);

        // 3. Add annotations
        System.out.println("--- Annotations ---");

        TextAnnotation radiusLabel = tm.addTextAnnotation("r = 1.5",
                new Vec3(2f, 0f, 0f), 0.3f, 255, 255, 0);
        System.out.println("Added text: " + radiusLabel.getText());

        TextAnnotation heightLabel = tm.addTextAnnotation("h = 3",
                new Vec3(-2f, 0f, 0f), 0.3f, 0, 255, 0);
        System.out.println("Added text: " + heightLabel.getText());

        ArrowAnnotation heightArrow = tm.addArrowAnnotation(
                new Vec3(-1.5f, -1f, 0f),
                new Vec3(-1.5f, 2f, 0f)
        );
        System.out.println("Added arrow: " + heightArrow.getDescription());

        HighlightAnnotation cubeHighlight = tm.addHighlightAnnotation(cubeObj);
        System.out.println("Added highlight: " + cubeHighlight.getDescription());

        System.out.println("Total annotations: " + tm.getAnnotationCount());
        System.out.println();

        // 4. Add assistant objects
        System.out.println("--- Assistants ---");

        Grid grid = new Grid(Grid.Density.MEDIUM, 10, 80, 80, 80, 0.3f);
        tm.addAssistant(grid);
        System.out.println("Added grid: " + grid.getDensity() + " density, " + grid.getGridSize() + " units");

        CoordinateSystem coordSys = new CoordinateSystem(5f);
        tm.addAssistant(coordSys);
        System.out.println("Added coordinate system (length=" + coordSys.getAxisLength() + ")");

        HelperLine centerLine = new HelperLine(
                new Vec3(-5f, 0f, 0f),
                new Vec3(5f, 0f, 0f),
                HelperLine.HelperLineType.CENTER,
                150, 150, 150,
                0.6f
        );
        tm.addAssistant(centerLine);
        System.out.println("Added center line (helper)");

        System.out.println("Total assistants: " + tm.getAssistantCount());
        System.out.println();

        // 5. Create and run a lesson
        System.out.println("--- Lesson ---");

        Lesson lesson = new Lesson("Cylinder Properties",
                "Explore the properties of a cylinder: radius, height, and volume");

        lesson.addStep(new Step(1, "Show Cylinder",
                "Display a cylinder with radius 1.5 and height 3"));
        lesson.addStep(new Step(2, "Label Radius",
                "Add a label pointing to the base radius"));
        lesson.addStep(new Step(3, "Label Height",
                "Add a label showing the height dimension"));
        lesson.addStep(new Step(4, "Highlight Base",
                "Highlight the bottom base of the cylinder"));
        lesson.addStep(new Step(5, "Compute Volume",
                "Calculate and display: V = π × r² × h = π × 2.25 × 3 ≈ 21.2"));

        tm.startLesson(lesson);
        System.out.println("Started lesson: " + tm.getCurrentLesson().getLessonName());
        System.out.println("Total steps: " + tm.getTotalStepCount());
        System.out.println("Current step: " + tm.getCurrentStepNumber());
        System.out.println();

        // Navigate through steps
        for (int i = 1; i <= tm.getTotalStepCount(); i++) {
            Step currentStep = tm.getCurrentLesson().getCurrentStep();
            System.out.println("Step " + i + ": " + currentStep.getTitle()
                    + " — " + currentStep.getDescription());
            if (i < tm.getTotalStepCount()) {
                tm.nextStep();
            }
        }
        System.out.println();

        // 6. Use construction system
        System.out.println("--- Geometric Constructions ---");

        // Point construction
        PointConstruction pointA = new PointConstruction(new Vec3(0f, 0f, 0f));
        System.out.println("Point at origin: " + pointA.build().getClass().getSimpleName());

        PointConstruction pointB = new PointConstruction(new Vec3(3f, 4f, 0f), 0.15f);
        System.out.println("Point at (3,4): " + pointB.build().getClass().getSimpleName());

        // Line construction
        LineConstruction lineAB = new LineConstruction(
                new Vec3(0f, 0f, 0f),
                new Vec3(3f, 4f, 0f)
        );
        System.out.println("Line from (0,0) to (3,4): length = " + lineAB.getLength());

        // Circle construction (center + radius)
        CircleConstruction circle1 = new CircleConstruction(
                new Vec3(0f, 0f, 0f), 5f, 32
        );
        System.out.println("Circle at origin, r=5: center=" + circle1.getCenter()
                + ", radius=" + circle1.getRadius());

        // Circle construction (three points)
        CircleConstruction circle2 = new CircleConstruction(
                new Vec3(0f, 0f, 0f),
                new Vec3(4f, 0f, 0f),
                new Vec3(0f, 3f, 0f)
        );
        System.out.println("Circle through (0,0),(4,0),(0,3): center=" + circle2.getCenter()
                + ", radius=" + String.format("%.2f", circle2.getRadius()));

        System.out.println();

        // 7. Recognition stub
        System.out.println("--- Shape Recognition (stub) ---");
        com.geometry.teaching.recognition.StrokeRecognizer recognizer =
                new com.geometry.teaching.recognition.DefaultStrokeRecognizer();
        System.out.println("Recognizer: " + recognizer.getName());
        System.out.println("Note: AI recognition not yet implemented (Phase 07 stub)");
        System.out.println();

        // 8. Teaching mode behaviors
        System.out.println("--- Teaching Modes ---");
        for (TeachingMode mode : TeachingMode.values()) {
            tm.setMode(mode);
            System.out.println("  " + mode + ": canEdit=" + tm.canEdit()
                    + ", canAnnotate=" + tm.canAnnotate());
        }
        System.out.println();

        // 9. Render annotations (headless)
        System.out.println("--- Rendering (headless) ---");
        tm.render();
        System.out.println("Render complete (headless mode — no OpenGL window)");
        System.out.println();

        // 10. Reset
        tm.reset();
        System.out.println("--- Reset ---");
        System.out.println("Mode: " + tm.getMode());
        System.out.println("Lesson active: " + tm.isLessonActive());
        System.out.println("Annotations: " + tm.getAnnotationCount());
        System.out.println("Assistants: " + tm.getAssistantCount());
        System.out.println();

        System.out.println("=== Teaching System Demo Complete ===");
    }
}
