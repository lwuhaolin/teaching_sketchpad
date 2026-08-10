package com.geometry.teaching;

import com.geometry.core.math.Vec3;
import com.geometry.interaction.event.Vec2;
import com.geometry.renderer.RenderMode;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.scene.SelectionManager;
import com.geometry.teaching.annotation.ArrowAnnotation;
import com.geometry.teaching.annotation.HighlightAnnotation;
import com.geometry.teaching.annotation.TextAnnotation;
import com.geometry.teaching.assistant.CoordinateSystem;
import com.geometry.teaching.assistant.Grid;
import com.geometry.teaching.assistant.HelperLine;
import com.geometry.teaching.construction.CircleConstruction;
import com.geometry.teaching.construction.LineConstruction;
import com.geometry.teaching.construction.PointConstruction;
import com.geometry.teaching.recognition.ShapeRecognitionResult;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 07 - Tests for the Teaching System.
 *
 * Tests:
 *   - TeachingMode enum
 *   - Annotation creation and properties
 *   - Lesson and Step lifecycle
 *   - TeachingManager: annotations, assistants, mode switching
 *   - Construction objects (Point, Line, Circle)
 *   - Assistant objects (Grid, CoordinateSystem, HelperLine)
 *   - Recognition result structure
 *   - TeachingManager does not depend on Geometry/Scene internals
 */
public class TeachingTest {

    private Scene scene;
    private com.geometry.renderer.Renderer mockRenderer;
    private TeachingManager teachingManager;

    @Before
    public void setUp() {
        scene = new Scene();
        mockRenderer = null; // headless test — no OpenGL
        teachingManager = new TeachingManager(scene, mockRenderer);
    }

    // ------------------------------------------------------------------
    // TeachingMode tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingModeValues() {
        TeachingMode[] modes = TeachingMode.values();
        assertEquals(4, modes.length);
        assertTrue(Arrays.asList(modes).contains(TeachingMode.TEACHER));
        assertTrue(Arrays.asList(modes).contains(TeachingMode.STUDENT));
        assertTrue(Arrays.asList(modes).contains(TeachingMode.EXAM));
        assertTrue(Arrays.asList(modes).contains(TeachingMode.FREE));
    }

    // ------------------------------------------------------------------
    // TextAnnotation tests
    // ------------------------------------------------------------------

    @Test
    public void testTextAnnotationCreation() {
        TextAnnotation annotation = new TextAnnotation("r = 5", new Vec3(0f, 0f, 0f), 0.5f);
        assertEquals("r = 5", annotation.getText());
        assertEquals(new Vec3(0f, 0f, 0f), annotation.getPosition());
        assertEquals(0.5f, annotation.getSize(), 0.001f);
    }

    @Test
    public void testTextAnnotationWithColor() {
        TextAnnotation annotation = new TextAnnotation("height h", new Vec3(1f, 2f, 0f),
                0.3f, 255, 0, 0);
        assertEquals(255, annotation.getColorR());
        assertEquals(0, annotation.getColorG());
        assertEquals(0, annotation.getColorB());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextAnnotationNullText() {
        new TextAnnotation(null, new Vec3(0f, 0f, 0f), 0.5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextAnnotationEmptyText() {
        new TextAnnotation("", new Vec3(0f, 0f, 0f), 0.5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextAnnotationNullPosition() {
        new TextAnnotation("label", null, 0.5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextAnnotationZeroSize() {
        new TextAnnotation("label", new Vec3(0f, 0f, 0f), 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTextAnnotationInvalidColor() {
        new TextAnnotation("label", new Vec3(0f, 0f, 0f), 0.5f, 300, 0, 0);
    }

    // ------------------------------------------------------------------
    // ArrowAnnotation tests
    // ------------------------------------------------------------------

    @Test
    public void testArrowAnnotationCreation() {
        ArrowAnnotation arrow = new ArrowAnnotation(
                new Vec3(0f, 0f, 0f),
                new Vec3(5f, 0f, 0f)
        );
        assertEquals(new Vec3(0f, 0f, 0f), arrow.getStart());
        assertEquals(new Vec3(5f, 0f, 0f), arrow.getEnd());
        assertEquals(0.3f, arrow.getArrowSize(), 0.001f); // default
    }

    @Test
    public void testArrowAnnotationWithColor() {
        ArrowAnnotation arrow = new ArrowAnnotation(
                new Vec3(0f, 0f, 0f),
                new Vec3(3f, 4f, 0f),
                255, 255, 0
        );
        assertEquals(255, arrow.getColorR());
        assertEquals(255, arrow.getColorG());
        assertEquals(0, arrow.getColorB());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrowAnnotationNullStart() {
        new ArrowAnnotation(null, new Vec3(1f, 0f, 0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrowAnnotationNullEnd() {
        new ArrowAnnotation(new Vec3(0f, 0f, 0f), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testArrowAnnotationZeroArrowSize() {
        new ArrowAnnotation(new Vec3(0f, 0f, 0f), new Vec3(1f, 0f, 0f), 0f, 255, 255, 255);
    }

    // ------------------------------------------------------------------
    // HighlightAnnotation tests
    // ------------------------------------------------------------------

    @Test
    public void testHighlightAnnotationCreation() {
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("test_cube", cube);
        HighlightAnnotation highlight = new HighlightAnnotation(so);
        assertEquals(so, highlight.getTarget());
        assertEquals(HighlightAnnotation.HighlightState.GLOW, highlight.getState());
    }

    @Test
    public void testHighlightAnnotationWithState() {
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("test_cube2", cube);
        HighlightAnnotation highlight = new HighlightAnnotation(
                so,
                HighlightAnnotation.HighlightState.OUTLINE,
                255, 255, 0,
                0.5f
        );
        assertEquals(HighlightAnnotation.HighlightState.OUTLINE, highlight.getState());
        assertEquals(0.5f, highlight.getAlpha(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHighlightAnnotationNullTarget() {
        new HighlightAnnotation((SceneObject) null);
    }

    @Test
    public void testHighlightAnnotationStateToggle() {
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("test_cube3", cube);
        HighlightAnnotation highlight = new HighlightAnnotation(so);
        assertTrue(highlight.isHighlightActive());

        highlight.setState(HighlightAnnotation.HighlightState.NORMAL);
        assertFalse(highlight.isHighlightActive());

        highlight.setState(HighlightAnnotation.HighlightState.GLOW);
        assertTrue(highlight.isHighlightActive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHighlightAnnotationNullState() {
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("test_cube4", cube);
        HighlightAnnotation highlight = new HighlightAnnotation(so);
        highlight.setState(null);
    }

    // ------------------------------------------------------------------
    // Lesson tests
    // ------------------------------------------------------------------

    @Test
    public void testLessonCreation() {
        Lesson lesson = new Lesson("Cylinder Unfolding", "Teaches cylinder net");
        assertEquals("Cylinder Unfolding", lesson.getLessonName());
        assertEquals("Teaches cylinder net", lesson.getDescription());
        assertTrue(lesson.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLessonNullName() {
        new Lesson(null, "description");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLessonEmptyName() {
        new Lesson("", "description");
    }

    @Test
    public void testLessonAddSteps() {
        Lesson lesson = new Lesson("Test Lesson");
        lesson.addStep(new Step(1, "Step 1", "Description 1"));
        lesson.addStep(new Step(2, "Step 2", "Description 2"));
        lesson.addStep(new Step(3, "Step 3", "Description 3"));

        assertEquals(3, lesson.getStepCount());
        assertEquals("Step 1", lesson.getStep(1).getTitle());
        assertEquals("Step 2", lesson.getStep(2).getTitle());
        assertEquals("Step 3", lesson.getStep(3).getTitle());
        assertNull(lesson.getStep(4)); // out of range
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLessonAddNullStep() {
        Lesson lesson = new Lesson("Test");
        lesson.addStep(null);
    }

    @Test
    public void testLessonStepNavigation() {
        Lesson lesson = new Lesson("Test");
        lesson.addStep(new Step(1, "First"));
        lesson.addStep(new Step(2, "Second"));
        lesson.addStep(new Step(3, "Third"));

        lesson.goToFirst();
        assertEquals(1, lesson.getCurrentStepNumber());
        assertEquals("First", lesson.getCurrentStep().getTitle());

        assertTrue(lesson.nextStep());
        assertEquals(2, lesson.getCurrentStepNumber());
        assertEquals("Second", lesson.getCurrentStep().getTitle());

        assertTrue(lesson.nextStep());
        assertEquals(3, lesson.getCurrentStepNumber());
        assertEquals("Third", lesson.getCurrentStep().getTitle());

        assertFalse(lesson.nextStep()); // already at last
        assertEquals(3, lesson.getCurrentStepNumber());

        assertTrue(lesson.previousStep());
        assertEquals(2, lesson.getCurrentStepNumber());

        lesson.goToLast();
        assertEquals(3, lesson.getCurrentStepNumber());

        lesson.goToFirst();
        assertEquals(1, lesson.getCurrentStepNumber());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testLessonInvalidStepIndex() {
        Lesson lesson = new Lesson("Test");
        lesson.addStep(new Step(1, "First"));
        lesson.setCurrentStepIndex(5);
    }

    @Test
    public void testLessonStepActions() {
        Step step = new Step(1, "Step 1", "Desc");
        step.addAction("Move the cube");
        step.addAction("Rotate 90°");
        step.addAction("Measure the distance");

        List<String> actions = step.getActions();
        assertEquals(3, actions.size());
        assertEquals("Move the cube", actions.get(0));
        assertEquals("Rotate 90°", actions.get(1));
        assertEquals("Measure the distance", actions.get(2));
    }

    @Test
    public void testLessonEquals() {
        Lesson l1 = new Lesson("Same Name");
        Lesson l2 = new Lesson("Same Name");
        assertEquals(l1, l2);
        assertEquals(l1.hashCode(), l2.hashCode());
    }

    // ------------------------------------------------------------------
    // Step tests
    // ------------------------------------------------------------------

    @Test
    public void testStepCreation() {
        Step step = new Step(1, "Show Cube", "Display a cube in the scene");
        assertEquals(1, step.getStepNumber());
        assertEquals("Show Cube", step.getTitle());
        assertEquals("Display a cube in the scene", step.getDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStepZeroNumber() {
        new Step(0, "Title", "Desc", new Scene(), Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStepNullTitle() {
        new Step(1, null, "Desc", new Scene(), Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStepEmptyTitle() {
        new Step(1, "", "Desc", new Scene(), Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStepNullScene() {
        new Step(1, "Title", "Desc", null, Collections.emptyList());
    }

    @Test
    public void testStepWithSceneState() {
        Scene sceneState = new Scene();
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        sceneState.addObject("cube", cube);

        Step step = new Step(1, "Show Cube", "Display a cube", sceneState, Collections.emptyList());
        assertEquals(1, step.getSceneState().getObjectCount());
    }

    // ------------------------------------------------------------------
    // TeachingManager tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerDefaultMode() {
        assertEquals(TeachingMode.FREE, teachingManager.getMode());
    }

    @Test
    public void testTeachingManagerSetMode() {
        teachingManager.setMode(TeachingMode.TEACHER);
        assertEquals(TeachingMode.TEACHER, teachingManager.getMode());

        teachingManager.setMode(TeachingMode.STUDENT);
        assertEquals(TeachingMode.STUDENT, teachingManager.getMode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTeachingManagerNullMode() {
        teachingManager.setMode(null);
    }

    @Test
    public void testTeachingManagerCanEdit() {
        teachingManager.setMode(TeachingMode.TEACHER);
        assertTrue(teachingManager.canEdit());

        teachingManager.setMode(TeachingMode.FREE);
        assertTrue(teachingManager.canEdit());

        teachingManager.setMode(TeachingMode.STUDENT);
        assertFalse(teachingManager.canEdit());

        teachingManager.setMode(TeachingMode.EXAM);
        assertFalse(teachingManager.canEdit());
    }

    @Test
    public void testTeachingManagerCanAnnotate() {
        teachingManager.setMode(TeachingMode.TEACHER);
        assertTrue(teachingManager.canAnnotate());

        teachingManager.setMode(TeachingMode.FREE);
        assertTrue(teachingManager.canAnnotate());

        teachingManager.setMode(TeachingMode.STUDENT);
        assertFalse(teachingManager.canAnnotate());
    }

    @Test
    public void testTeachingManagerNullScene() {
        try {
            new TeachingManager(null, null);
            fail("Should throw IllegalArgumentException for null scene");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testTeachingManagerGetTargetScene() {
        assertEquals(scene, teachingManager.getTargetScene());
    }

    @Test
    public void testTeachingManagerSetTargetScene() {
        Scene newScene = new Scene();
        teachingManager.setTargetScene(newScene);
        assertEquals(newScene, teachingManager.getTargetScene());
    }

    // ------------------------------------------------------------------
    // TeachingManager annotation tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerAddTextAnnotation() {
        TextAnnotation ta = teachingManager.addTextAnnotation("r = 5", new Vec3(0f, 0f, 0f), 0.5f);
        assertNotNull(ta);
        assertEquals(1, teachingManager.getAnnotationCount());
    }

    @Test
    public void testTeachingManagerAddArrowAnnotation() {
        ArrowAnnotation arrow = teachingManager.addArrowAnnotation(
                new Vec3(0f, 0f, 0f), new Vec3(5f, 0f, 0f)
        );
        assertNotNull(arrow);
        assertEquals(1, teachingManager.getAnnotationCount());
    }

    @Test
    public void testTeachingManagerAddHighlightAnnotation() {
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("highlight_cube", cube);

        HighlightAnnotation highlight = teachingManager.addHighlightAnnotation(so);
        assertNotNull(highlight);
        assertEquals(1, teachingManager.getAnnotationCount());
    }

    @Test
    public void testTeachingManagerRemoveAnnotation() {
        TextAnnotation ta = teachingManager.addTextAnnotation("label", new Vec3(0f, 0f, 0f), 0.5f);
        assertEquals(1, teachingManager.getAnnotationCount());

        assertTrue(teachingManager.removeAnnotation(ta));
        assertEquals(0, teachingManager.getAnnotationCount());
    }

    @Test
    public void testTeachingManagerClearAnnotations() {
        teachingManager.addTextAnnotation("a", new Vec3(0f, 0f, 0f), 0.5f);
        teachingManager.addTextAnnotation("b", new Vec3(1f, 0f, 0f), 0.5f);
        teachingManager.addArrowAnnotation(new Vec3(0f, 0f, 0f), new Vec3(1f, 0f, 0f));
        assertEquals(3, teachingManager.getAnnotationCount());

        teachingManager.clearAnnotations();
        assertEquals(0, teachingManager.getAnnotationCount());
    }

    // ------------------------------------------------------------------
    // TeachingManager assistant tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerAddGrid() {
        Grid grid = new Grid();
        teachingManager.addAssistant(grid);
        assertEquals(1, teachingManager.getAssistantCount());
    }

    @Test
    public void testTeachingManagerAddCoordinateSystem() {
        CoordinateSystem coordSys = new CoordinateSystem();
        teachingManager.addAssistant(coordSys);
        assertEquals(1, teachingManager.getAssistantCount());
    }

    @Test
    public void testTeachingManagerAddHelperLine() {
        HelperLine line = new HelperLine(new Vec3(0f, 0f, 0f), new Vec3(5f, 0f, 0f));
        teachingManager.addAssistant(line);
        assertEquals(1, teachingManager.getAssistantCount());
    }

    @Test
    public void testTeachingManagerClearAssistants() {
        teachingManager.addAssistant(new Grid());
        teachingManager.addAssistant(new CoordinateSystem());
        teachingManager.addAssistant(new HelperLine(Vec3.ZERO, Vec3.UNIT_X));
        assertEquals(3, teachingManager.getAssistantCount());

        teachingManager.clearAssistants();
        assertEquals(0, teachingManager.getAssistantCount());
    }

    // ------------------------------------------------------------------
    // TeachingManager lesson tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerStartLesson() {
        Lesson lesson = new Lesson("Test Lesson");
        lesson.addStep(new Step(1, "Step 1", "Description 1"));
        lesson.addStep(new Step(2, "Step 2", "Description 2"));

        teachingManager.startLesson(lesson);
        assertTrue(teachingManager.isLessonActive());
        assertEquals("Test Lesson", teachingManager.getCurrentLesson().getLessonName());
        assertEquals(1, teachingManager.getCurrentStepNumber());
        assertEquals(2, teachingManager.getTotalStepCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTeachingManagerStartNullLesson() {
        teachingManager.startLesson(null);
    }

    @Test
    public void testTeachingManagerNextStep() {
        Lesson lesson = new Lesson("Test");
        lesson.addStep(new Step(1, "First"));
        lesson.addStep(new Step(2, "Second"));
        lesson.addStep(new Step(3, "Third"));

        teachingManager.startLesson(lesson);
        assertEquals(1, teachingManager.getCurrentStepNumber());

        teachingManager.nextStep();
        assertEquals(2, teachingManager.getCurrentStepNumber());

        teachingManager.nextStep();
        assertEquals(3, teachingManager.getCurrentStepNumber());

        assertFalse(teachingManager.nextStep()); // at end
    }

    @Test
    public void testTeachingManagerPreviousStep() {
        Lesson lesson = new Lesson("Test");
        lesson.addStep(new Step(1, "First"));
        lesson.addStep(new Step(2, "Second"));
        lesson.addStep(new Step(3, "Third"));

        teachingManager.startLesson(lesson);
        teachingManager.nextStep();
        teachingManager.nextStep();
        assertEquals(3, teachingManager.getCurrentStepNumber());

        teachingManager.previousStep();
        assertEquals(2, teachingManager.getCurrentStepNumber());

        teachingManager.previousStep();
        assertEquals(1, teachingManager.getCurrentStepNumber());

        assertFalse(teachingManager.previousStep()); // at start
    }

    @Test
    public void testTeachingManagerNoLesson() {
        assertFalse(teachingManager.isLessonActive());
        assertEquals(0, teachingManager.getCurrentStepNumber());
        assertEquals(0, teachingManager.getTotalStepCount());
        assertFalse(teachingManager.nextStep());
        assertFalse(teachingManager.previousStep());
    }

    // ------------------------------------------------------------------
    // TeachingManager render test (headless)
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerRenderHeadless() {
        teachingManager.addTextAnnotation("test", new Vec3(0f, 0f, 0f), 0.5f);
        teachingManager.addAssistant(new Grid());

        // Should not throw in headless mode
        teachingManager.render();
    }

    // ------------------------------------------------------------------
    // TeachingManager reset test
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerReset() {
        teachingManager.setMode(TeachingMode.TEACHER);
        teachingManager.addTextAnnotation("temp", new Vec3(0f, 0f, 0f), 0.5f);
        teachingManager.addAssistant(new Grid());

        Lesson lesson = new Lesson("Temp Lesson");
        lesson.addStep(new Step(1, "Step 1", "Desc"));
        teachingManager.startLesson(lesson);

        teachingManager.reset();

        assertEquals(TeachingMode.FREE, teachingManager.getMode());
        assertNull(teachingManager.getCurrentLesson());
        assertEquals(0, teachingManager.getAnnotationCount());
        assertEquals(0, teachingManager.getAssistantCount());
    }

    // ------------------------------------------------------------------
    // Construction tests
    // ------------------------------------------------------------------

    @Test
    public void testPointConstruction() {
        PointConstruction construction = new PointConstruction(new Vec3(3f, 4f, 0f));
        com.geometry.core.geometry.GeometryObject point = construction.build();
        assertNotNull(point);
        assertNotNull(point.getMesh());
        assertFalse(point.getMesh().isEmpty());
    }

    @Test
    public void testPointConstructionDefaultRadius() {
        PointConstruction construction = new PointConstruction(new Vec3(0f, 0f, 0f));
        // Default radius is 0.1f
        assertEquals(0.1f, construction.getRadius(), 0.001f);
    }

    @Test
    public void testPointConstructionCustomRadius() {
        PointConstruction construction = new PointConstruction(new Vec3(1f, 2f, 0f), 0.5f);
        assertEquals(0.5f, construction.getRadius(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPointConstructionNullPosition() {
        new PointConstruction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPointConstructionZeroRadius() {
        new PointConstruction(new Vec3(0f, 0f, 0f), 0f);
    }

    @Test
    public void testLineConstruction() {
        LineConstruction construction = new LineConstruction(
                new Vec3(0f, 0f, 0f),
                new Vec3(3f, 4f, 0f)
        );
        com.geometry.core.geometry.GeometryObject line = construction.build();
        assertNotNull(line);
        assertNotNull(line.getMesh());
        assertFalse(line.getMesh().isEmpty());
        assertEquals(5f, construction.getLength(), 0.001f); // 3-4-5 triangle
    }

    @Test
    public void testLineConstructionSamePoint() {
        // When points are the same, it should create a point instead
        LineConstruction construction = new LineConstruction(
                new Vec3(1f, 1f, 0f),
                new Vec3(1f, 1f, 0f)
        );
        com.geometry.core.geometry.GeometryObject line = construction.build();
        assertNotNull(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLineConstructionNullPointA() {
        new LineConstruction(null, new Vec3(1f, 0f, 0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLineConstructionNullPointB() {
        new LineConstruction(new Vec3(0f, 0f, 0f), null);
    }

    @Test
    public void testCircleConstructionCenterRadius() {
        CircleConstruction construction = new CircleConstruction(
                new Vec3(0f, 0f, 0f),
                5f,
                32
        );
        com.geometry.core.geometry.GeometryObject circle = construction.build();
        assertNotNull(circle);
        assertNotNull(circle.getMesh());
        assertFalse(circle.getMesh().isEmpty());
        assertEquals(new Vec3(0f, 0f, 0f), construction.getCenter());
        assertEquals(5f, construction.getRadius(), 0.001f);
    }

    @Test
    public void testCircleConstructionThreePoints() {
        CircleConstruction construction = new CircleConstruction(
                new Vec3(0f, 0f, 0f),
                new Vec3(4f, 0f, 0f),
                new Vec3(0f, 3f, 0f)
        );
        com.geometry.core.geometry.GeometryObject circle = construction.build();
        assertNotNull(circle);
        // Right triangle: circumscircle center is midpoint of hypotenuse
        // Hypotenuse from (4,0) to (0,3), midpoint = (2, 1.5)
        assertEquals(2f, construction.getCenter().x, 0.01f);
        assertEquals(1.5f, construction.getCenter().y, 0.01f);
        assertEquals(2.5f, construction.getRadius(), 0.01f); // hypotenuse/2 = 5/2 = 2.5
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCircleConstructionCollinearPoints() {
        new CircleConstruction(
                new Vec3(0f, 0f, 0f),
                new Vec3(1f, 1f, 0f),
                new Vec3(2f, 2f, 0f) // collinear with origin
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCircleConstructionNullCenter() {
        new CircleConstruction(null, 5f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCircleConstructionZeroRadius() {
        new CircleConstruction(new Vec3(0f, 0f, 0f), 0f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCircleConstructionTooFewSegments() {
        new CircleConstruction(new Vec3(0f, 0f, 0f), 5f, 2);
    }

    // ------------------------------------------------------------------
    // Grid tests
    // ------------------------------------------------------------------

    @Test
    public void testGridDefault() {
        Grid grid = new Grid();
        assertEquals(Grid.Density.MEDIUM, grid.getDensity());
        assertEquals(10, grid.getGridSize());
        assertTrue(grid.isVisible());
    }

    @Test
    public void testGridCustom() {
        Grid grid = new Grid(Grid.Density.DENSE, 20, 100, 100, 100, 0.5f);
        assertEquals(Grid.Density.DENSE, grid.getDensity());
        assertEquals(20, grid.getGridSize());
        assertEquals(0.5f, grid.getAlpha(), 0.001f);
        assertEquals(0.5f, grid.getMajorSpacing(), 0.001f);
        assertEquals(0.25f, grid.getMinorSpacing(), 0.001f);
    }

    @Test
    public void testGridVisibility() {
        Grid grid = new Grid();
        grid.setVisible(false);
        assertFalse(grid.isVisible());
        grid.setVisible(true);
        assertTrue(grid.isVisible());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGridNullDensity() {
        new Grid(null, 10, 100, 100, 100, 0.3f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGridZeroSize() {
        new Grid(Grid.Density.SPARSE, 0, 100, 100, 100, 0.3f);
    }

    // ------------------------------------------------------------------
    // CoordinateSystem tests
    // ------------------------------------------------------------------

    @Test
    public void testCoordinateSystemDefault() {
        CoordinateSystem coordSys = new CoordinateSystem();
        assertEquals(5.0f, coordSys.getAxisLength(), 0.001f);
        assertTrue(coordSys.isVisible());
    }

    @Test
    public void testCoordinateSystemCustom() {
        CoordinateSystem coordSys = new CoordinateSystem(10f);
        assertEquals(10f, coordSys.getAxisLength(), 0.001f);
    }

    @Test
    public void testCoordinateSystemAxisColors() {
        CoordinateSystem coordSys = new CoordinateSystem();
        assertEquals(255, coordSys.getXColorR());
        assertEquals(51, coordSys.getXColorG());
        assertEquals(51, coordSys.getXColorB());
        assertEquals(51, coordSys.getYColorR());
        assertEquals(255, coordSys.getYColorG());
        assertEquals(51, coordSys.getYColorB());
        assertEquals(51, coordSys.getZColorR());
        assertEquals(51, coordSys.getZColorG());
        assertEquals(255, coordSys.getZColorB());
    }

    @Test
    public void testCoordinateSystemAxisEndpoints() {
        CoordinateSystem coordSys = new CoordinateSystem(5f);
        Vec3[] endpoints = coordSys.getAxisEndpoints();
        assertEquals(3, endpoints.length);
        assertEquals(new Vec3(5f, 0f, 0f), endpoints[0]); // X
        assertEquals(new Vec3(0f, 5f, 0f), endpoints[1]); // Y
        assertEquals(new Vec3(0f, 0f, 5f), endpoints[2]); // Z
    }

    @Test
    public void testCoordinateSystemVisibility() {
        CoordinateSystem coordSys = new CoordinateSystem();
        coordSys.setVisible(false);
        assertFalse(coordSys.isVisible());
        coordSys.setVisible(true);
        assertTrue(coordSys.isVisible());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCoordinateSystemZeroLength() {
        new CoordinateSystem(0f);
    }

    // ------------------------------------------------------------------
    // HelperLine tests
    // ------------------------------------------------------------------

    @Test
    public void testHelperLineDefault() {
        HelperLine line = new HelperLine(new Vec3(0f, 0f, 0f), new Vec3(5f, 0f, 0f));
        assertEquals(HelperLine.HelperLineType.SOLID, line.getType());
        assertEquals(0.6f, line.getAlpha(), 0.001f);
    }

    @Test
    public void testHelperLineDashed() {
        HelperLine line = new HelperLine(
                new Vec3(0f, 0f, 0f),
                new Vec3(3f, 4f, 0f),
                HelperLine.HelperLineType.DASHED,
                200, 100, 50,
                0.8f
        );
        assertEquals(HelperLine.HelperLineType.DASHED, line.getType());
        assertEquals(200, line.getColorR());
        assertEquals(100, line.getColorG());
        assertEquals(50, line.getColorB());
        assertEquals(0.8f, line.getAlpha(), 0.001f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHelperLineNullStart() {
        new HelperLine(null, new Vec3(1f, 0f, 0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHelperLineNullEnd() {
        new HelperLine(new Vec3(0f, 0f, 0f), null);
    }

    // ------------------------------------------------------------------
    // ShapeRecognitionResult tests
    // ------------------------------------------------------------------

    @Test
    public void testRecognitionResultCircle() {
        ShapeRecognitionResult result = new ShapeRecognitionResult(
                ShapeRecognitionResult.ShapeType.CIRCLE,
                0.92f,
                new float[]{0f, 0f, 5f, 0f, 0f, 5f}
        );
        assertEquals(ShapeRecognitionResult.ShapeType.CIRCLE, result.getType());
        assertEquals(0.92f, result.getConfidence(), 0.001f);
        assertEquals(3, result.getPointCount());
        assertEquals(0f, result.getX(0), 0.001f);
        assertEquals(0f, result.getY(0), 0.001f);
        assertEquals(5f, result.getX(1), 0.001f);
        assertEquals(0f, result.getY(1), 0.001f);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testRecognitionResultUnknown() {
        ShapeRecognitionResult result = new ShapeRecognitionResult();
        assertEquals(ShapeRecognitionResult.ShapeType.UNKNOWN, result.getType());
        assertEquals(0f, result.getConfidence(), 0.001f);
        assertFalse(result.isSuccess());
    }

    @Test
    public void testRecognitionResultSetters() {
        ShapeRecognitionResult result = new ShapeRecognitionResult();
        result.setType(ShapeRecognitionResult.ShapeType.TRIANGLE);
        result.setConfidence(0.85f);
        assertEquals(ShapeRecognitionResult.ShapeType.TRIANGLE, result.getType());
        assertEquals(0.85f, result.getConfidence(), 0.001f);
        assertTrue(result.isSuccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecognitionResultNullType() {
        new ShapeRecognitionResult(null, 0.5f, new float[]{0f, 0f});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecognitionResultInvalidConfidence() {
        new ShapeRecognitionResult(ShapeRecognitionResult.ShapeType.CIRCLE, 1.5f, new float[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRecognitionResultNullPoints() {
        new ShapeRecognitionResult(ShapeRecognitionResult.ShapeType.CIRCLE, 0.5f, null);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRecognitionResultOutOfBounds() {
        ShapeRecognitionResult result = new ShapeRecognitionResult(
                ShapeRecognitionResult.ShapeType.CIRCLE,
                0.5f,
                new float[]{0f, 0f}
        );
        result.getX(1); // only 1 point available
    }

    // ------------------------------------------------------------------
    // StrokeRecognizer tests
    // ------------------------------------------------------------------

    @Test
    public void testDefaultRecognizerReturnsUnknown() {
        com.geometry.teaching.recognition.StrokeRecognizer recognizer =
                new com.geometry.teaching.recognition.DefaultStrokeRecognizer();
        assertNotNull(recognizer);
        assertEquals("default-stub", recognizer.getName());

        List<Vec2> stroke = Arrays.asList(
                new Vec2(0f, 0f),
                new Vec2(1f, 1f),
                new Vec2(2f, 0f)
        );
        ShapeRecognitionResult result = recognizer.recognize(stroke);
        assertEquals(ShapeRecognitionResult.ShapeType.UNKNOWN, result.getType());
        assertEquals(0f, result.getConfidence(), 0.001f);
        assertFalse(result.isSuccess());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultRecognizerNullStroke() {
        com.geometry.teaching.recognition.StrokeRecognizer recognizer =
                new com.geometry.teaching.recognition.DefaultStrokeRecognizer();
        recognizer.recognize(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDefaultRecognizerEmptyStroke() {
        com.geometry.teaching.recognition.StrokeRecognizer recognizer =
                new com.geometry.teaching.recognition.DefaultStrokeRecognizer();
        recognizer.recognize(Collections.emptyList());
    }

    // ------------------------------------------------------------------
    // Integration: TeachingManager with annotations and assistants
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerFullWorkflow() {
        // Setup
        teachingManager.setMode(TeachingMode.TEACHER);

        // Add objects to scene
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        com.geometry.core.geometry.Cylinder cylinder = new com.geometry.core.geometry.Cylinder(1f, 2f, 16);
        SceneObject cubeObj = scene.addObject("cube", cube);
        SceneObject cylObj = scene.addObject("cylinder", cylinder);

        // Add annotations
        teachingManager.addTextAnnotation("Cube", new Vec3(0f, 0f, 0f), 0.5f);
        teachingManager.addTextAnnotation("Cylinder", new Vec3(3f, 0f, 0f), 0.5f);
        teachingManager.addArrowAnnotation(new Vec3(0f, -1f, 0f), new Vec3(0f, 0f, 0f));
        teachingManager.addHighlightAnnotation(cubeObj);

        assertEquals(4, teachingManager.getAnnotationCount());

        // Add assistants
        teachingManager.addAssistant(new Grid());
        teachingManager.addAssistant(new CoordinateSystem());
        teachingManager.addAssistant(new HelperLine(Vec3.ZERO, new Vec3(5f, 0f, 0f)));

        assertEquals(3, teachingManager.getAssistantCount());

        // Render (headless — should not throw)
        teachingManager.render();

        // Create and start a lesson
        Lesson lesson = new Lesson("Basic Geometry", "Introduction to 3D shapes");
        lesson.addStep(new Step(1, "Show Shapes", "Display cube and cylinder"));
        lesson.addStep(new Step(2, "Highlight Cube", "Focus on the cube"));
        lesson.addStep(new Step(3, "Add Annotations", "Label the shapes"));

        teachingManager.startLesson(lesson);
        assertTrue(teachingManager.isLessonActive());
        assertEquals(1, teachingManager.getCurrentStepNumber());
        assertEquals(3, teachingManager.getTotalStepCount());

        // Navigate steps
        teachingManager.nextStep();
        assertEquals(2, teachingManager.getCurrentStepNumber());
        teachingManager.nextStep();
        assertEquals(3, teachingManager.getCurrentStepNumber());
        teachingManager.previousStep();
        assertEquals(2, teachingManager.getCurrentStepNumber());

        // Reset
        teachingManager.reset();
        assertFalse(teachingManager.isLessonActive());
        assertEquals(TeachingMode.FREE, teachingManager.getMode());
    }

    // ------------------------------------------------------------------
    // Architecture isolation tests
    // ------------------------------------------------------------------

    @Test
    public void testTeachingManagerDoesNotAccessGeometryInternals() {
        // TeachingManager should only interact with SceneObject (not GeometryObject directly)
        // and should not modify mesh data
        com.geometry.core.geometry.Cube cube = new com.geometry.core.geometry.Cube(1f, 1f, 1f);
        SceneObject so = scene.addObject("iso_test", cube);

        // Add highlight — this should work through SceneObject, not GeometryObject
        teachingManager.addHighlightAnnotation(so);
        assertEquals(1, teachingManager.getAnnotationCount());

        // The cube's mesh should be unchanged
        assertFalse(cube.getMesh().isEmpty());
    }

    @Test
    public void testTeachingManagerWorksIn2DMode() {
        teachingManager.setMode(TeachingMode.FREE);

        // Add 2D-style annotations at z=0
        teachingManager.addTextAnnotation("Point A", new Vec3(0f, 0f, 0f), 0.3f);
        teachingManager.addTextAnnotation("Point B", new Vec3(5f, 0f, 0f), 0.3f);
        teachingManager.addArrowAnnotation(new Vec3(0f, 0f, 0f), new Vec3(5f, 0f, 0f));

        // Add grid for 2D mode
        teachingManager.addAssistant(new Grid(Grid.Density.MEDIUM, 10, 80, 80, 80, 0.3f));

        assertEquals(3, teachingManager.getAnnotationCount());
        assertEquals(1, teachingManager.getAssistantCount());

        // Render should not throw
        teachingManager.render();
    }

    @Test
    public void testTeachingManagerWorksIn3DMode() {
        teachingManager.setMode(TeachingMode.TEACHER);

        // Add 3D annotations
        teachingManager.addTextAnnotation("Height h", new Vec3(0f, 2f, 0f), 0.4f);
        teachingManager.addArrowAnnotation(new Vec3(0f, 0f, 0f), new Vec3(0f, 5f, 0f));

        // Add coordinate system for 3D mode
        teachingManager.addAssistant(new CoordinateSystem(5f));

        assertEquals(2, teachingManager.getAnnotationCount());
        assertEquals(1, teachingManager.getAssistantCount());

        teachingManager.render();
    }
}
