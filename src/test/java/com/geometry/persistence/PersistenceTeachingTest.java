package com.geometry.persistence;

import com.geometry.animation.AnimationSequence;
import com.geometry.core.geometry.*;
import com.geometry.core.math.Vec3;
import com.geometry.core.transform.Transform;
import com.geometry.persistence.model.*;
import com.geometry.persistence.registry.AnimationRegistry;
import com.geometry.persistence.registry.GeometryRegistry;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Phase 10 - Comprehensive tests for the persistence system.
 *
 * Tests cover:
 *   - Geometry object serialization/deserialization
 *   - Teaching/lesson/step restoration
 *   - Animation sequence restoration
 *   - Whiteboard stroke persistence
 *   - Round-trip fidelity (save → load → verify)
 *   - Version migration
 *   - Error handling (invalid paths, null inputs)
 *
 * All tests use a temporary directory to avoid polluting the filesystem.
 */
public class PersistenceTeachingTest {

    private static final String TEST_DIR = System.getProperty("java.io.tmpdir") + File.separator + "gtp_test";
    private static final GeometryRegistry GEOMETRY_REGISTRY = new GeometryRegistry();
    private static final AnimationRegistry ANIMATION_REGISTRY = new AnimationRegistry();
    private static final ProjectSerializer SERIALIZER = new ProjectSerializer(GEOMETRY_REGISTRY);
    private static final ProjectDeserializer DESERIALIZER = new ProjectDeserializer(GEOMETRY_REGISTRY, ANIMATION_REGISTRY);

    static {
        // Ensure test directory exists
        new File(TEST_DIR).mkdirs();
    }

    // ------------------------------------------------------------------
    // Test runner
    // ------------------------------------------------------------------

    private static int passed = 0;
    private static int failed = 0;
    private static int total = 0;

    public static void main(String[] args) {
        System.out.println("=== Phase 10 Persistence Tests ===\n");

        // Geometry tests
        testSerializeDeserializeCube();
        testSerializeDeserializeCylinder();
        testSerializeDeserializeCone();
        testSerializeDeserializeSphere();
        testSerializeDeserializeCircle();
        testSerializeDeserializeRectangle();
        testSerializeDeserializePolygon();
        testMultiObjectScene();
        testTransformRoundTrip();

        // Teaching tests
        testTeachingSerialization();
        testAnnotationSerialization();
        testLessonAndSteps();

        // Animation tests
        testAnimationSequenceSerialization();
        testMoveAnimationRoundTrip();
        testRotateAnimationRoundTrip();
        testUnfoldAnimationRoundTrip();

        // Whiteboard tests
        testWhiteboardStrokeSerialization();

        // ProjectManager tests
        testProjectManagerSaveLoad();

        // Version migration
        testVersionMigration();

        // Error handling
        testNullSceneThrows();
        testNullFilePathThrows();
        testInvalidFilePathExtension();
        testMissingFileThrows();
        testUnsupportedVersionThrows();

        // Summary
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + total);

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    private static void assertTrue(String testName, boolean condition) {
        total++;
        if (condition) {
            passed++;
            System.out.println("  PASS: " + testName);
        } else {
            failed++;
            System.out.println("  FAIL: " + testName);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        total++;
        if ((expected == null && actual == null) ||
                (expected != null && expected.equals(actual))) {
            passed++;
            System.out.println("  PASS: " + testName);
        } else {
            failed++;
            System.out.println("  FAIL: " + testName
                    + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static void assertNotNull(String testName, Object obj) {
        total++;
        if (obj != null) {
            passed++;
            System.out.println("  PASS: " + testName);
        } else {
            failed++;
            System.out.println("  FAIL: " + testName);
        }
    }

    private static void assertArrayEquals(String testName, float[] expected, float[] actual, float epsilon) {
        total++;
        if (expected == null || actual == null) {
            failed++;
            System.out.println("  FAIL: " + testName + " (null array)");
            return;
        }
        if (expected.length != actual.length) {
            failed++;
            System.out.println("  FAIL: " + testName + " (length mismatch: " + expected.length + " vs " + actual.length + ")");
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(expected[i] - actual[i]) > epsilon) {
                failed++;
                System.out.println("  FAIL: " + testName + " at index " + i
                        + " (expected: " + expected[i] + ", actual: " + actual[i] + ")");
                return;
            }
        }
        passed++;
        System.out.println("  PASS: " + testName);
    }

    private static void assertEqualsFloat(String testName, float expected, float actual, float epsilon) {
        total++;
        if (Math.abs(expected - actual) <= epsilon) {
            passed++;
            System.out.println("  PASS: " + testName);
        } else {
            failed++;
            System.out.println("  FAIL: " + testName
                    + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }

    private static String getTempPath(String name) {
        return TEST_DIR + File.separator + name + ".gtp";
    }

    private static void cleanup(String filePath) {
        new File(filePath).delete();
    }

    // ------------------------------------------------------------------
    // Geometry serialization tests
    // ------------------------------------------------------------------

    private static void testSerializeDeserializeCube() {
        System.out.println("\n--- Geometry: Cube ---");
        String path = getTempPath("cube");

        Scene scene = new Scene();
        Cube cube = new Cube(2.0f, 3.0f, 4.0f);
        SceneObject obj = new SceneObject("cube001", cube);
        obj.setOverrideTransform(new Transform(new Vec3(1f, 2f, 3f), new Vec3(0f, 0f, 0f), new Vec3(1f, 1f, 1f)));
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Cube type", "Cube", objData.getType());
            assertEqualsFloat("Cube width", 2.0f, objData.getParameter("width", 0f), 0.001f);
            assertEqualsFloat("Cube height", 3.0f, objData.getParameter("height", 0f), 0.001f);
            assertEqualsFloat("Cube depth", 4.0f, objData.getParameter("depth", 0f), 0.001f);
            assertArrayEquals("Cube position", new float[]{1f, 2f, 3f}, objData.getPosition(), 0.001f);

            // Round-trip through file
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            assertNotNull("Loaded scene is not null", loaded);
            assertEquals("Loaded object count", 1, loaded.getAllObjects().size());

            SceneObject loadedObj = loaded.findObjectById("cube001");
            assertNotNull("Loaded cube by ID", loadedObj);
            assertTrue("Loaded cube is Cube instance", loadedObj.getGeometry() instanceof Cube);
            Cube loadedCube = (Cube) loadedObj.getGeometry();
            assertEqualsFloat("Loaded cube width", 2.0f, loadedCube.getWidth(), 0.001f);
            assertEqualsFloat("Loaded cube height", 3.0f, loadedCube.getHeight(), 0.001f);
            assertEqualsFloat("Loaded cube depth", 4.0f, loadedCube.getDepth(), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Cube serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializeCylinder() {
        System.out.println("\n--- Geometry: Cylinder ---");
        String path = getTempPath("cylinder");

        Scene scene = new Scene();
        Cylinder cyl = new Cylinder(1.5f, 4.0f, 32);
        SceneObject obj = new SceneObject("cyl001", cyl);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Cylinder type", "Cylinder", objData.getType());
            assertEqualsFloat("Cylinder radius", 1.5f, objData.getParameter("radius", 0f), 0.001f);
            assertEqualsFloat("Cylinder height", 4.0f, objData.getParameter("height", 0f), 0.001f);
            assertEqualsFloat("Cylinder segments", 32.0f, objData.getParameter("segments", 0f), 0.001f);

            // Round-trip
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("cyl001");
            assertNotNull("Loaded cylinder by ID", loadedObj);
            assertTrue("Loaded cylinder is Cylinder instance", loadedObj.getGeometry() instanceof Cylinder);
            Cylinder loadedCyl = (Cylinder) loadedObj.getGeometry();
            assertEqualsFloat("Loaded cylinder radius", 1.5f, loadedCyl.getRadius(), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Cylinder serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializeCone() {
        System.out.println("\n--- Geometry: Cone ---");
        String path = getTempPath("cone");

        Scene scene = new Scene();
        Cone cone = new Cone(2.0f, 5.0f, 24);
        SceneObject obj = new SceneObject("cone001", cone);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Cone type", "Cone", objData.getType());
            assertEqualsFloat("Cone radius", 2.0f, objData.getParameter("radius", 0f), 0.001f);
            assertEqualsFloat("Cone height", 5.0f, objData.getParameter("height", 0f), 0.001f);

            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("cone001");
            assertNotNull("Loaded cone by ID", loadedObj);
            assertTrue("Loaded cone is Cone instance", loadedObj.getGeometry() instanceof Cone);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Cone serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializeSphere() {
        System.out.println("\n--- Geometry: Sphere ---");
        String path = getTempPath("sphere");

        Scene scene = new Scene();
        Sphere sphere = new Sphere(1.0f, 16, 12);
        SceneObject obj = new SceneObject("sphere001", sphere);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Sphere type", "Sphere", objData.getType());
            assertEqualsFloat("Sphere radius", 1.0f, objData.getParameter("radius", 0f), 0.001f);
            assertEqualsFloat("Sphere segments", 16.0f, objData.getParameter("segments", 0f), 0.001f);
            assertEqualsFloat("Sphere rings", 12.0f, objData.getParameter("rings", 0f), 0.001f);

            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("sphere001");
            assertNotNull("Loaded sphere by ID", loadedObj);
            assertTrue("Loaded sphere is Sphere instance", loadedObj.getGeometry() instanceof Sphere);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Sphere serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializeCircle() {
        System.out.println("\n--- Geometry: Circle ---");
        String path = getTempPath("circle");

        Scene scene = new Scene();
        Circle circle = new Circle(3.0f, 24);
        SceneObject obj = new SceneObject("circle001", circle);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Circle type", "Circle", objData.getType());
            assertEqualsFloat("Circle radius", 3.0f, objData.getParameter("radius", 0f), 0.001f);

            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("circle001");
            assertNotNull("Loaded circle by ID", loadedObj);
            assertTrue("Loaded circle is Circle instance", loadedObj.getGeometry() instanceof Circle);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Circle serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializeRectangle() {
        System.out.println("\n--- Geometry: Rectangle ---");
        String path = getTempPath("rectangle");

        Scene scene = new Scene();
        Rectangle rect = new Rectangle(5.0f, 3.0f);
        SceneObject obj = new SceneObject("rect001", rect);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Rectangle type", "Rectangle", objData.getType());
            assertEqualsFloat("Rectangle width", 5.0f, objData.getParameter("width", 0f), 0.001f);
            assertEqualsFloat("Rectangle height", 3.0f, objData.getParameter("height", 0f), 0.001f);

            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("rect001");
            assertNotNull("Loaded rectangle by ID", loadedObj);
            assertTrue("Loaded rectangle is Rectangle instance", loadedObj.getGeometry() instanceof Rectangle);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Rectangle serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testSerializeDeserializePolygon() {
        System.out.println("\n--- Geometry: Polygon ---");
        String path = getTempPath("polygon");

        Scene scene = new Scene();
        Polygon poly = new Polygon(
                new Vec3(0f, 0f, 0f),
                new Vec3(2f, 0f, 0f),
                new Vec3(2f, 2f, 0f),
                new Vec3(0f, 2f, 0f)
        );
        SceneObject obj = new SceneObject("poly001", poly);
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertEquals("Polygon type", "Polygon", objData.getType());

            // Round-trip
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("poly001");
            assertNotNull("Loaded polygon by ID", loadedObj);
            assertTrue("Loaded polygon is Polygon instance", loadedObj.getGeometry() instanceof Polygon);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Polygon serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testMultiObjectScene() {
        System.out.println("\n--- Geometry: Multi-Object Scene ---");
        String path = getTempPath("multi");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));
        scene.addSceneObject(new SceneObject("cyl1", new Cylinder(1f, 2f, 16)));
        scene.addSceneObject(new SceneObject("sphere1", new Sphere(0.5f, 12, 8)));
        scene.addSceneObject(new SceneObject("rect1", new Rectangle(3f, 2f)));

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            assertEquals("4 objects serialized", 4, data.getScene().getObjects().size());

            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            assertEquals("4 objects loaded", 4, loaded.getAllObjects().size());
            assertNotNull("cube1 loaded", loaded.findObjectById("cube1"));
            assertNotNull("cyl1 loaded", loaded.findObjectById("cyl1"));
            assertNotNull("sphere1 loaded", loaded.findObjectById("sphere1"));
            assertNotNull("rect1 loaded", loaded.findObjectById("rect1"));

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Multi-object serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testTransformRoundTrip() {
        System.out.println("\n--- Geometry: Transform Round-Trip ---");
        String path = getTempPath("transform");

        Scene scene = new Scene();
        Cube cube = new Cube(2f, 2f, 2f);
        SceneObject obj = new SceneObject("trans001", cube);
        obj.setOverrideTransform(new Transform(
                new Vec3(10f, 20f, 30f),
                new Vec3(45f, 90f, 0f),
                new Vec3(2f, 2f, 2f)));
        scene.addSceneObject(obj);

        try {
            ProjectData data = SERIALIZER.serializeToData(scene);
            ObjectData objData = data.getScene().getObjects().get(0);

            assertArrayEquals("Position preserved", new float[]{10f, 20f, 30f}, objData.getPosition(), 0.001f);
            assertArrayEquals("Rotation preserved", new float[]{45f, 90f, 0f}, objData.getRotation(), 0.001f);
            assertArrayEquals("Scale preserved", new float[]{2f, 2f, 2f}, objData.getScale(), 0.001f);

            // Round-trip
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            Scene loaded = manager.load(path);
            SceneObject loadedObj = loaded.findObjectById("trans001");
            assertNotNull("Loaded transform object", loadedObj);
            Transform loadedT = loadedObj.getEffectiveTransform();
            assertEqualsFloat("Loaded position X", 10f, loadedT.getPosition().x, 0.001f);
            assertEqualsFloat("Loaded position Y", 20f, loadedT.getPosition().y, 0.001f);
            assertEqualsFloat("Loaded position Z", 30f, loadedT.getPosition().z, 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Transform serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    // ------------------------------------------------------------------
    // Teaching serialization tests
    // ------------------------------------------------------------------

    private static void testTeachingSerialization() {
        System.out.println("\n--- Teaching: Basic Serialization ---");
        String path = getTempPath("teaching");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));

        TeachingData teaching = new TeachingData();
        teaching.addLesson(new LessonData("Introduction", "Teaches basic concepts"));
        teaching.setCurrentLessonId("lesson1");
        teaching.setCurrentStepNumber(1);
        teaching.setTeachingMode("LECTURE");
        teaching.addAnnotation(new AnnotationData("Hello", new float[]{0f, 0f, 0f}, 0.5f, 255, 255, 0));
        teaching.addAssistant(new AssistanceData(AssistanceData.AssistanceType.GRID, null));

        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setTeaching(teaching);
        data.setName("Teaching Test");

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            assertEquals("Project name", "Teaching Test", loaded.getName());
            assertEquals("Version", "2.0", loaded.getVersion());
            assertNotNull("Teaching data exists", loaded.getTeaching());
            assertEquals("1 lesson", 1, loaded.getTeaching().getLessons().size());
            assertEquals("Current lesson", "lesson1", loaded.getTeaching().getCurrentLessonId());
            assertEquals("Current step", 1, loaded.getTeaching().getCurrentStepNumber());
            assertEquals("Teaching mode", "LECTURE", loaded.getTeaching().getTeachingMode());
            assertEquals("1 annotation", 1, loaded.getTeaching().getAnnotations().size());
            assertEquals("1 assistant", 1, loaded.getTeaching().getAssistants().size());

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Teaching serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testAnnotationSerialization() {
        System.out.println("\n--- Teaching: Annotation Serialization ---");
        String path = getTempPath("annotations");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));

        TeachingData teaching = new TeachingData();
        // TEXT annotation
        teaching.addAnnotation(new AnnotationData("Label", new float[]{0f, 1f, 0f}, 0.8f, 255, 0, 0));
        // ARROW annotation
        teaching.addAnnotation(new AnnotationData(new float[]{0f, 0f, 0f}, new float[]{1f, 1f, 1f}, 0.3f, 0, 255, 0));
        // HIGHLIGHT annotation
        teaching.addAnnotation(new AnnotationData("cube1", "GLOW", 255, 255, 0, 0.4f));

        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setTeaching(teaching);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            TeachingData loadedTeaching = loaded.getTeaching();
            assertEquals("3 annotations", 3, loadedTeaching.getAnnotations().size());

            // Verify TEXT annotation
            AnnotationData textAnn = loadedTeaching.getAnnotations().get(0);
            assertEquals("TEXT type", "TEXT", textAnn.getType().toString());
            assertEquals("TEXT color R", 255, textAnn.getColorR());
            assertEquals("TEXT color G", 0, textAnn.getColorG());
            assertEquals("TEXT color B", 0, textAnn.getColorB());

            // Verify ARROW annotation
            AnnotationData arrowAnn = loadedTeaching.getAnnotations().get(1);
            assertEquals("ARROW type", "ARROW", arrowAnn.getType().toString());
            assertArrayEquals("ARROW start", new float[]{0f, 0f, 0f}, arrowAnn.getStart(), 0.001f);

            // Verify HIGHLIGHT annotation
            AnnotationData highlightAnn = loadedTeaching.getAnnotations().get(2);
            assertEquals("HIGHLIGHT type", "HIGHLIGHT", highlightAnn.getType().toString());
            assertEquals("HIGHLIGHT target", "cube1", highlightAnn.getTargetId());
            assertEquals("HIGHLIGHT state", "GLOW", highlightAnn.getState());

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Annotation serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testLessonAndSteps() {
        System.out.println("\n--- Teaching: Lesson and Steps ---");
        String path = getTempPath("lesson");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));

        TeachingData teaching = new TeachingData();
        LessonData lesson = new LessonData("Lesson 1", "First lesson");
        lesson.addStep(new StepData(1, "Step 1", "Description 1", "anim1",
                Arrays.asList("Action 1", "Action 2")));
        lesson.addStep(new StepData(2, "Step 2", "Description 2", "anim2",
                Arrays.asList("Action 3")));
        teaching.addLesson(lesson);
        teaching.setCurrentLessonId("lesson1");
        teaching.setCurrentStepNumber(2);

        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setTeaching(teaching);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            TeachingData loadedTeaching = loaded.getTeaching();
            assertEquals("1 lesson", 1, loadedTeaching.getLessons().size());
            assertEquals("2 steps", 2, loadedTeaching.getLessons().get(0).getSteps().size());
            assertEquals("Current step", 2, loadedTeaching.getCurrentStepNumber());

            StepData step1 = loadedTeaching.getLessons().get(0).getSteps().get(0);
            assertEquals("Step 1 title", "Step 1", step1.getTitle());
            assertEquals("Step 1 animation", "anim1", step1.getAnimationId());
            assertEquals("Step 1 action count", 2, step1.getActions().size());

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Lesson serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    // ------------------------------------------------------------------
    // Animation serialization tests
    // ------------------------------------------------------------------

    private static void testAnimationSequenceSerialization() {
        System.out.println("\n--- Animation: Sequence Serialization ---");
        String path = getTempPath("animation");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));
        scene.addSceneObject(new SceneObject("cyl1", new Cylinder(1f, 2f, 16)));

        // Build animation data manually
        List<AnimationItemData> items = new ArrayList<>();
        items.add(new AnimationItemData(
                "Move Cube", AnimationItemData.AnimationItemType.MOVE, "cube1",
                new float[]{0f, 0f, 0f}, new float[]{5f, 0f, 0f},
                null, null, null, null,
                null, 2f, "LINEAR", 0f));
        items.add(new AnimationItemData(
                "Rotate Cylinder", AnimationItemData.AnimationItemType.ROTATE, "cyl1",
                null, null,
                new float[]{0f, 0f, 0f}, new float[]{0f, 90f, 0f},
                null, null,
                null, 3f, "EASE", 0.5f));

        AnimationSequenceData seq = new AnimationSequenceData("Demo Animation");
        seq.setDescription("Demo sequence");
        for (AnimationItemData item : items) seq.addItem(item);

        AnimationData animData = new AnimationData();
        animData.addSequence(seq);
        animData.setCurrentSequenceId("Demo Animation");

        TeachingData teaching = new TeachingData();
        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setAnimation(animData);
        data.setTeaching(teaching);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            AnimationData loadedAnim = loaded.getAnimation();
            assertEquals("1 sequence", 1, loadedAnim.getSequences().size());
            assertEquals("Current sequence", "Demo Animation", loadedAnim.getCurrentSequenceId());

            AnimationSequenceData loadedSeq = loadedAnim.getSequences().get(0);
            assertEquals("Sequence name", "Demo Animation", loadedSeq.getName());
            assertEquals("2 items", 2, loadedSeq.getItems().size());

            AnimationItemData item0 = loadedSeq.getItems().get(0);
            assertEquals("Item 0 type", "MOVE", item0.getType().toString());
            assertEquals("Item 0 target", "cube1", item0.getTargetId());
            assertEqualsFloat("Item 0 duration", 2f, item0.getDuration(), 0.001f);
            assertEquals("Item 0 interpolator", "LINEAR", item0.getInterpolator());

            AnimationItemData item1 = loadedSeq.getItems().get(1);
            assertEquals("Item 1 type", "ROTATE", item1.getType().toString());
            assertEquals("Item 1 target", "cyl1", item1.getTargetId());
            assertEqualsFloat("Item 1 duration", 3f, item1.getDuration(), 0.001f);
            assertEquals("Item 1 interpolator", "EASE", item1.getInterpolator());
            assertEqualsFloat("Item 1 delay", 0.5f, item1.getDelaySeconds(), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Animation serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testMoveAnimationRoundTrip() {
        System.out.println("\n--- Animation: Move Animation Round-Trip ---");
        String path = getTempPath("move_anim");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));

        AnimationSequenceData seq = new AnimationSequenceData("Move Test");
        AnimationItemData item = new AnimationItemData(
                "Move", AnimationItemData.AnimationItemType.MOVE, "cube1",
                new float[]{0f, 0f, 0f}, new float[]{10f, 0f, 0f},
                null, null, null, null,
                null, 1f, "LINEAR", 0f);
        seq.addItem(item);

        AnimationData animData = new AnimationData();
        animData.addSequence(seq);

        TeachingData teaching = new TeachingData();
        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setAnimation(animData);
        data.setTeaching(teaching);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            AnimationItemData loadedItem = loaded.getAnimation().getSequences().get(0).getItems().get(0);
            assertEquals("Loaded type", "MOVE", loadedItem.getType().toString());
            assertArrayEquals("Loaded fromPos", new float[]{0f, 0f, 0f}, loadedItem.getFromPosition(), 0.001f);
            assertArrayEquals("Loaded toPos", new float[]{10f, 0f, 0f}, loadedItem.getToPosition(), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Move animation serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testRotateAnimationRoundTrip() {
        System.out.println("\n--- Animation: Rotate Animation Round-Trip ---");
        String path = getTempPath("rotate_anim");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("sphere1", new Sphere(1f, 16, 12)));

        AnimationSequenceData seq = new AnimationSequenceData("Rotate Test");
        AnimationItemData item = new AnimationItemData(
                "Rotate", AnimationItemData.AnimationItemType.ROTATE, "sphere1",
                null, null,
                new float[]{0f, 0f, 0f}, new float[]{0f, 180f, 0f},
                null, null,
                null, 2f, "EASE", 0f);
        seq.addItem(item);

        AnimationData animData = new AnimationData();
        animData.addSequence(seq);

        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setAnimation(animData);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            AnimationItemData loadedItem = loaded.getAnimation().getSequences().get(0).getItems().get(0);
            assertEquals("Loaded type", "ROTATE", loadedItem.getType().toString());
            assertArrayEquals("Loaded fromRot", new float[]{0f, 0f, 0f}, loadedItem.getFromRotation(), 0.001f);
            assertArrayEquals("Loaded toRot", new float[]{0f, 180f, 0f}, loadedItem.getToRotation(), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Rotate animation serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    private static void testUnfoldAnimationRoundTrip() {
        System.out.println("\n--- Animation: Unfold Animation Round-Trip ---");
        String path = getTempPath("unfold_anim");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cyl1", new Cylinder(1f, 3f, 24)));

        AnimationSequenceData seq = new AnimationSequenceData("Unfold Test");
        AnimationItemData item = new AnimationItemData(
                "Unfold Cylinder", AnimationItemData.AnimationItemType.UNFOLD, "cyl1",
                null, null, null, null, null, null,
                "CYLINDER", 3f, "LINEAR", 0f);
        seq.addItem(item);

        AnimationData animData = new AnimationData();
        animData.addSequence(seq);

        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setAnimation(animData);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            AnimationItemData loadedItem = loaded.getAnimation().getSequences().get(0).getItems().get(0);
            assertEquals("Loaded type", "UNFOLD", loadedItem.getType().toString());
            assertEquals("Loaded unfoldType", "CYLINDER", loadedItem.getUnfoldType());
            assertEqualsFloat("Loaded duration", 3f, loadedItem.getDuration(), 0.001f);

            // Verify animation can be created from data
            AnimationRegistry animReg = new AnimationRegistry();
            Scene rebuildScene = manager.load(path);
            AnimationSequence loadedSeq = animReg.createSequence(rebuildScene,
                    loaded.getAnimation().getSequences().get(0));
            assertNotNull("Animation sequence created", loadedSeq);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Unfold animation serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    // ------------------------------------------------------------------
    // Whiteboard serialization tests
    // ------------------------------------------------------------------

    private static void testWhiteboardStrokeSerialization() {
        System.out.println("\n--- Whiteboard: Stroke Serialization ---");
        String path = getTempPath("whiteboard");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(1f, 1f, 1f)));

        List<float[]> points = new ArrayList<>();
        points.add(new float[]{100f, 200f});
        points.add(new float[]{110f, 195f});
        points.add(new float[]{125f, 188f});
        points.add(new float[]{140f, 180f});
        points.add(new float[]{155f, 170f});

        StrokeData stroke = new StrokeData(points, 0.7f, 1234567890L);
        WhiteboardData wbData = new WhiteboardData();
        wbData.addStroke(stroke);
        wbData.setCanvasWidth(800);
        wbData.setCanvasHeight(600);

        TeachingData teaching = new TeachingData();
        ProjectData data = SERIALIZER.serializeToData(scene);
        data.setWhiteboard(wbData);
        data.setTeaching(teaching);

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path, data);
            ProjectData loaded = manager.loadProjectData(path);

            WhiteboardData loadedWb = loaded.getWhiteboard();
            assertEquals("1 stroke", 1, loadedWb.getStrokes().size());
            assertEquals("Canvas width", 800, loadedWb.getCanvasWidth());
            assertEquals("Canvas height", 600, loadedWb.getCanvasHeight());

            StrokeData loadedStroke = loadedWb.getStrokes().get(0);
            assertEquals("5 points", 5, loadedStroke.getPointCount());
            assertEqualsFloat("Pressure", 0.7f, loadedStroke.getPressure(), 0.001f);
            assertEquals("Timestamp", 1234567890L, loadedStroke.getTimestamp());
            assertArrayEquals("First point", new float[]{100f, 200f}, loadedStroke.getPoint(0), 0.001f);
            assertArrayEquals("Last point", new float[]{155f, 170f}, loadedStroke.getPoint(4), 0.001f);

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: Whiteboard serialization threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    // ------------------------------------------------------------------
    // ProjectManager tests
    // ------------------------------------------------------------------

    private static void testProjectManagerSaveLoad() {
        System.out.println("\n--- ProjectManager: Save/Load ---");
        String path = getTempPath("manager");

        Scene scene = new Scene();
        scene.addSceneObject(new SceneObject("cube1", new Cube(2f, 2f, 2f)));
        scene.addSceneObject(new SceneObject("cyl1", new Cylinder(1f, 3f, 24)));

        try {
            ProjectManager manager = new ProjectManager();
            manager.save(scene, path);
            assertEquals("Last saved path", path, manager.getLastSavedPath());
            assertTrue("History has save command", manager.canUndo());

            // Load
            Scene loaded = manager.load(path);
            assertEquals("2 objects loaded", 2, loaded.getAllObjects().size());
            assertNotNull("cube1 loaded", loaded.findObjectById("cube1"));
            assertNotNull("cyl1 loaded", loaded.findObjectById("cyl1"));

            // Test getGeometryRegistry
            assertTrue("GeometryRegistry is accessible", manager.getGeometryRegistry() != null);
            assertTrue("Cube type supported", manager.isGeometryTypeSupported("Cube"));
            assertTrue("Cylinder type supported", manager.isGeometryTypeSupported("Cylinder"));

            // Clear history
            manager.clearHistory();
            assertTrue("Can undo after clear", !manager.canUndo());

            cleanup(path);
        } catch (IOException e) {
            failed++;
            System.out.println("  FAIL: ProjectManager test threw exception: " + e.getMessage());
            cleanup(path);
        }
    }

    // ------------------------------------------------------------------
    // Version migration tests
    // ------------------------------------------------------------------

    private static void testVersionMigration() {
        System.out.println("\n--- Version Migration ---");

        // Test with a v1.0 style project (simulated)
        ProjectData v1Data = new ProjectData("Migration Test");
        v1Data.setVersion("1.0");
        v1Data.setScene(new SceneData());
        SceneData scene = v1Data.getScene();
        Map<String, Float> params = new HashMap<>();
        params.put("width", 1f);
        params.put("height", 1f);
        params.put("depth", 1f);
        scene.addObject(new ObjectData("obj1", "Cube",
                new float[]{0f, 0f, 0f}, new float[]{0f, 0f, 0f}, new float[]{1f, 1f, 1f},
                params));

        // Migrate
        ProjectData migrated = VersionMigration.migrate(v1Data);
        assertEquals("Version migrated to 2.0", "2.0", migrated.getVersion());
        assertNotNull("Teaching data migrated", migrated.getTeaching());
        assertNotNull("Animation data migrated", migrated.getAnimation());
        assertNotNull("Whiteboard data migrated", migrated.getWhiteboard());
        assertNotNull("Settings data migrated", migrated.getSettings());

        // Already at v2.0
        ProjectData v2Data = new ProjectData("Already 2.0");
        v2Data.setVersion("2.0");
        ProjectData migratedV2 = VersionMigration.migrate(v2Data);
        assertEquals("v2.0 unchanged", "2.0", migratedV2.getVersion());

        // Unsupported version
        try {
            ProjectData badData = new ProjectData("Bad");
            badData.setVersion("99.0");
            VersionMigration.migrate(badData);
            failed++;
            System.out.println("  FAIL: Unsupported version should throw exception");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("  PASS: Unsupported version correctly rejected");
        }
        total++;
    }

    // ------------------------------------------------------------------
    // Error handling tests
    // ------------------------------------------------------------------

    private static void testNullSceneThrows() {
        System.out.println("\n--- Error: Null Scene ---");
        try {
            ProjectManager manager = new ProjectManager();
            manager.save(null, TEST_DIR + File.separator + "null.gtp");
            failed++;
            System.out.println("  FAIL: Null scene should throw exception");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("  PASS: Null scene correctly rejected");
        } catch (IOException e) {
            // Not expected
        }
        total++;
    }

    private static void testNullFilePathThrows() {
        System.out.println("\n--- Error: Null File Path ---");
        try {
            ProjectManager manager = new ProjectManager();
            manager.save(new Scene(), null);
            failed++;
            System.out.println("  FAIL: Null file path should throw exception");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("  PASS: Null file path correctly rejected");
        } catch (IOException e) {
            // Not expected
        }
        total++;
    }

    private static void testInvalidFilePathExtension() {
        System.out.println("\n--- Error: Invalid Extension ---");
        try {
            ProjectManager manager = new ProjectManager();
            manager.save(new Scene(), TEST_DIR + File.separator + "bad.txt");
            failed++;
            System.out.println("  FAIL: Non-.gtp extension should throw exception");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("  PASS: Invalid extension correctly rejected");
        } catch (IOException e) {
            // Not expected
        }
        total++;
    }

    private static void testMissingFileThrows() {
        System.out.println("\n--- Error: Missing File ---");
        try {
            ProjectManager manager = new ProjectManager();
            manager.load(TEST_DIR + File.separator + "nonexistent.gtp");
            failed++;
            System.out.println("  FAIL: Missing file should throw exception");
        } catch (IOException e) {
            passed++;
            System.out.println("  PASS: Missing file correctly rejected");
        }
        total++;
    }

    private static void testUnsupportedVersionThrows() {
        System.out.println("\n--- Error: Unsupported Version ---");
        // Create a .gtp file with an unsupported version
        String path = getTempPath("bad_version");
        try {
            FileWriter fw = new FileWriter(path);
            fw.write("{\"version\": \"0.5\", \"project\": {}, \"scene\": {\"objects\": []}}");
            fw.close();

            ProjectManager manager = new ProjectManager();
            manager.load(path);
            failed++;
            System.out.println("  FAIL: Unsupported version should throw exception");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("  PASS: Unsupported version correctly rejected");
        } catch (IOException e) {
            passed++;
            System.out.println("  PASS: Unsupported version correctly rejected (IO error)");
        }
        total++;
        cleanup(path);
    }
}
