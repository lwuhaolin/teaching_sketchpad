package com.geometry.persistence;

import com.geometry.core.geometry.Cube;
import com.geometry.core.geometry.Cylinder;
import com.geometry.core.geometry.Sphere;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.persistence.model.*;
import com.geometry.persistence.registry.GeometryRegistry;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Phase 10 - Demo program for the persistence system.
 *
 * Creates a teaching project with:
 *   - Multiple geometry objects (Cube, Cylinder, Sphere)
 *   - Teaching annotations (Text, Arrow)
 *   - Animation sequence
 *   - Whiteboard strokes
 *
 * Then saves to a .gtp file, closes, and reloads to verify round-trip.
 *
 * Usage:
 *   java com.geometry.persistence.PersistenceTeachingDemo
 */
public class PersistenceTeachingDemo {

    private static final String OUTPUT_PATH = System.getProperty("java.io.tmpdir") + File.separator + "demo.gtp";
    private static final ProjectSerializer SERIALIZER = new ProjectSerializer(new GeometryRegistry());

    public static void main(String[] args) {
        System.out.println("=== Phase 10 Persistence Demo ===\n");

        // 1. Create a scene with geometry objects
        System.out.println("1. Creating scene with geometry objects...");
        Scene scene = new Scene();

        Cube cube = new Cube(2.0f, 1.5f, 1.0f);
        SceneObject cubeObj = new SceneObject("cube1", cube);
        cubeObj.setOverrideTransform(new Transform(new Vec3(-3f, 0f, 0f), new Vec3(0f, 0f, 0f), new Vec3(1f, 1f, 1f)));
        scene.addSceneObject(cubeObj);
        System.out.println("   Added Cube: 2.0 x 1.5 x 1.0 at (-3, 0, 0)");

        Cylinder cylinder = new Cylinder(1.0f, 3.0f, 32);
        SceneObject cylObj = new SceneObject("cylinder1", cylinder);
        cylObj.setOverrideTransform(new Transform(new Vec3(0f, 0f, 0f), new Vec3(0f, 0f, 0f), new Vec3(1f, 1f, 1f)));
        scene.addSceneObject(cylObj);
        System.out.println("   Added Cylinder: radius=1.0, height=3.0 at (0, 0, 0)");

        Sphere sphere = new Sphere(0.8f, 16, 12);
        SceneObject sphereObj = new SceneObject("sphere1", sphere);
        sphereObj.setOverrideTransform(new Transform(new Vec3(3f, 0f, 0f), new Vec3(0f, 0f, 0f), new Vec3(1f, 1f, 1f)));
        scene.addSceneObject(sphereObj);
        System.out.println("   Added Sphere: radius=0.8 at (3, 0, 0)");

        // 2. Create teaching data with annotations
        System.out.println("\n2. Creating teaching data with annotations...");
        TeachingData teaching = new TeachingData();

        // Text annotation
        teaching.addAnnotation(new AnnotationData("Volume = πr²h", new float[]{0f, 2f, 0f}, 0.6f, 255, 255, 0));
        System.out.println("   Added Text Annotation: 'Volume = πr²h'");

        // Arrow annotation
        teaching.addAnnotation(new AnnotationData(new float[]{-3f, -1f, 0f}, new float[]{3f, 1f, 0f}, 0.3f, 255, 100, 100));
        System.out.println("   Added Arrow Annotation: from cube to sphere");

        // 3. Create animation sequence
        System.out.println("\n3. Creating animation sequence...");
        List<AnimationItemData> animItems = new ArrayList<>();
        animItems.add(new AnimationItemData(
                "Show Cube", AnimationItemData.AnimationItemType.MOVE, "cube1",
                new float[]{0f, -5f, 0f}, new float[]{-3f, 0f, 0f},
                null, null, null, null,
                null, 1.5f, "EASE", 0f));
        animItems.add(new AnimationItemData(
                "Show Cylinder", AnimationItemData.AnimationItemType.MOVE, "cylinder1",
                new float[]{0f, -5f, 0f}, new float[]{0f, 0f, 0f},
                null, null, null, null,
                null, 1.5f, "EASE", 0.5f));
        animItems.add(new AnimationItemData(
                "Show Sphere", AnimationItemData.AnimationItemType.MOVE, "sphere1",
                new float[]{0f, -5f, 0f}, new float[]{3f, 0f, 0f},
                null, null, null, null,
                null, 1.5f, "EASE", 1.0f));

        AnimationSequenceData seq = new AnimationSequenceData("Welcome Animation");
        seq.setDescription("Animate objects appearing on screen");
        seq.getItems().addAll(animItems);

        AnimationData animData = new AnimationData();
        animData.addSequence(seq);
        System.out.println("   Created sequence with 3 move animations");

        // 4. Create whiteboard strokes
        System.out.println("\n4. Creating whiteboard strokes...");
        List<float[]> stroke1 = new ArrayList<>();
        stroke1.add(new float[]{100f, 100f});
        stroke1.add(new float[]{150f, 90f});
        stroke1.add(new float[]{200f, 85f});
        stroke1.add(new float[]{250f, 80f});
        stroke1.add(new float[]{300f, 75f});

        List<float[]> stroke2 = new ArrayList<>();
        stroke2.add(new float[]{400f, 200f});
        stroke2.add(new float[]{450f, 190f});
        stroke2.add(new float[]{500f, 175f});

        WhiteboardData wbData = new WhiteboardData();
        wbData.addStroke(new StrokeData(stroke1, 0.8f, System.currentTimeMillis()));
        wbData.addStroke(new StrokeData(stroke2, 0.6f, System.currentTimeMillis() + 1000));
        wbData.setCanvasWidth(800);
        wbData.setCanvasHeight(600);
        System.out.println("   Added 2 whiteboard strokes (800x600 canvas)");

        // 5. Assemble ProjectData
        System.out.println("\n5. Assembling ProjectData...");
        ProjectData projectData = SERIALIZER.serializeToData(scene);
        projectData.setName("Demo Project");
        projectData.setTeaching(teaching);
        projectData.setAnimation(animData);
        projectData.setWhiteboard(wbData);
        System.out.println("   Project name: 'Demo Project'");
        System.out.println("   Objects: " + projectData.getScene().getObjects().size());
        System.out.println("   Annotations: " + projectData.getTeaching().getAnnotations().size());
        System.out.println("   Animation sequences: " + projectData.getAnimation().getSequences().size());
        System.out.println("   Whiteboard strokes: " + projectData.getWhiteboard().getStrokes().size());

        // 6. Save to .gtp file
        System.out.println("\n6. Saving to: " + OUTPUT_PATH);
        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, OUTPUT_PATH, projectData);
            System.out.println("   Save successful! File size: "
                    + new File(OUTPUT_PATH).length() + " bytes");
        } catch (Exception e) {
            System.out.println("   ERROR: Failed to save: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 7. Close and reload
        System.out.println("\n7. Reloading from file...");
        try {
            ProjectManager reloadManager = new ProjectManager();
            Scene loadedScene = reloadManager.load(OUTPUT_PATH);

            System.out.println("   Loaded " + loadedScene.getAllObjects().size() + " objects:");
            for (SceneObject obj : loadedScene.getAllObjects()) {
                System.out.println("     - " + obj.getId() + ": "
                        + obj.getGeometry().getClass().getSimpleName());
            }

            ProjectData loadedData = reloadManager.loadProjectData(OUTPUT_PATH);
            System.out.println("\n   Reloaded project data:");
            System.out.println("     - Name: " + loadedData.getName());
            System.out.println("     - Version: " + loadedData.getVersion());
            System.out.println("     - Teaching annotations: " + loadedData.getTeaching().getAnnotations().size());
            System.out.println("     - Animation sequences: " + loadedData.getAnimation().getSequences().size());
            System.out.println("     - Whiteboard strokes: " + loadedData.getWhiteboard().getStrokes().size());

            // Verify object parameters
            SceneObject loadedCube = loadedScene.findObjectById("cube1");
            if (loadedCube != null && loadedCube.getGeometry() instanceof Cube) {
                Cube loadedC = (Cube) loadedCube.getGeometry();
                System.out.println("\n   Cube verification:");
                System.out.println("     - Width: " + loadedC.getWidth() + " (expected: 2.0)");
                System.out.println("     - Height: " + loadedC.getHeight() + " (expected: 1.5)");
                System.out.println("     - Depth: " + loadedC.getDepth() + " (expected: 1.0)");
            }

            SceneObject loadedCyl = loadedScene.findObjectById("cylinder1");
            if (loadedCyl != null && loadedCyl.getGeometry() instanceof Cylinder) {
                Cylinder loadedCylObj = (Cylinder) loadedCyl.getGeometry();
                System.out.println("   Cylinder verification:");
                System.out.println("     - Radius: " + loadedCylObj.getRadius() + " (expected: 1.0)");
                System.out.println("     - Height: " + loadedCylObj.getHeight() + " (expected: 3.0)");
            }

            SceneObject loadedSphere = loadedScene.findObjectById("sphere1");
            if (loadedSphere != null && loadedSphere.getGeometry() instanceof Sphere) {
                Sphere loadedS = (Sphere) loadedSphere.getGeometry();
                System.out.println("   Sphere verification:");
                System.out.println("     - Radius: " + loadedS.getRadius() + " (expected: 0.8)");
            }

            System.out.println("\n   === Demo Complete ===");
            System.out.println("   Project saved and reloaded successfully!");
            System.out.println("   File: " + OUTPUT_PATH);

        } catch (Exception e) {
            System.out.println("   ERROR: Failed to reload: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
