package com.geometry.interaction.gesture;

import com.geometry.interaction.event.PointerEvent;
import com.geometry.interaction.event.StrokeGestureEvent;
import com.geometry.interaction.event.Vec2;
import com.geometry.teaching.recognition.DefaultStrokeRecognizer;
import com.geometry.teaching.recognition.ShapeRecognitionResult;
import com.geometry.teaching.recognition.StrokeRecognizer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Phase 07 - Tests for StrokeGestureRecognizer.
 *
 * Tests:
 *   - Stroke recognition from pen DOWN → MOVE* → UP sequences
 *   - Shape type mapping (POINT, LINE, CIRCLE, RECTANGLE, etc.)
 *   - Confidence scores
 *   - Integration with StrokeRecognizer implementations
 *   - Event buffering and edge cases
 */
public class StrokeGestureRecognitionTest {

    private StrokeGestureRecognizer recognizer;

    @Before
    public void setUp() {
        recognizer = new StrokeGestureRecognizer();
    }

    // ------------------------------------------------------------------
    // StrokeGestureRecognizer basic tests
    // ------------------------------------------------------------------

    @Test
    public void testStrokeRecognizerDefaultName() {
        assertEquals("default-stub", recognizer.getName());
    }

    @Test
    public void testStrokeRecognizerIsNotRecordingInitially() {
        assertFalse(recognizer.isRecording());
        assertEquals(0, recognizer.getBufferedPointCount());
    }

    @Test
    public void testStrokeRecognizerReset() {
        // Start a stroke
        recognizer.process(makePenDown(100f, 100f));
        assertTrue(recognizer.isRecording());

        // Reset
        recognizer.reset();
        assertFalse(recognizer.isRecording());
        assertEquals(0, recognizer.getBufferedPointCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStrokeRecognizerNullRecognizer() {
        new StrokeGestureRecognizer(null);
    }

    // ------------------------------------------------------------------
    // Pen DOWN — start recording
    // ------------------------------------------------------------------

    @Test
    public void testPenDownStartsRecording() {
        PointerEvent down = makePenDown(100f, 100f);
        StrokeGestureEvent result = recognizer.process(down);

        assertNull(result); // No event on DOWN
        assertTrue(recognizer.isRecording());
        assertEquals(1, recognizer.getBufferedPointCount());
    }

    @Test
    public void testPenDownStoresStartPosition() {
        recognizer.process(makePenDown(200f, 300f));
        // Cannot directly inspect buffer, but we can verify recording state
        assertTrue(recognizer.isRecording());
    }

    // ------------------------------------------------------------------
    // Pen MOVE — accumulate points
    // ------------------------------------------------------------------

    @Test
    public void testPenMoveAccumulatesPoints() {
        recognizer.process(makePenDown(100f, 100f));
        recognizer.process(makePenMove(105f, 105f));
        recognizer.process(makePenMove(110f, 110f));

        assertTrue(recognizer.isRecording());
        assertEquals(3, recognizer.getBufferedPointCount());
    }

    @Test
    public void testPenMoveWithoutDownIsIgnored() {
        // No DOWN first
        StrokeGestureEvent result = recognizer.process(makePenMove(100f, 100f));
        assertNull(result);
        assertFalse(recognizer.isRecording());
        assertEquals(0, recognizer.getBufferedPointCount());
    }

    // ------------------------------------------------------------------
    // Pen UP — complete stroke
    // ------------------------------------------------------------------

    @Test
    public void testPenUpCompletesStroke() {
        recognizer.process(makePenDown(100f, 100f));
        recognizer.process(makePenMove(105f, 105f));
        StrokeGestureEvent result = recognizer.process(makePenUp(110f, 110f));

        assertNotNull(result);
        assertFalse(recognizer.isRecording());
    }

    @Test
    public void testPenUpWithoutDownIsIgnored() {
        StrokeGestureEvent result = recognizer.process(makePenUp(100f, 100f));
        assertNull(result);
        assertFalse(recognizer.isRecording());
    }

    // ------------------------------------------------------------------
    // Non-pen input is ignored
    // ------------------------------------------------------------------

    @Test
    public void testMouseEventsAreIgnored() {
        List<PointerEvent> events = new ArrayList<>();
        events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(100f, 100f), Vec2.ZERO, PointerEvent.EventType.DOWN));
        events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(150f, 100f), new Vec2(50f, 0f), PointerEvent.EventType.MOVE));
        events.add(new PointerEvent(0, PointerEvent.PointerType.MOUSE,
                new Vec2(150f, 100f), Vec2.ZERO, PointerEvent.EventType.UP));

        List<StrokeGestureEvent> results = recognizer.process(events);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testTouchEventIsIgnored() {
        PointerEvent event = new PointerEvent(0, PointerEvent.PointerType.TOUCH,
                new Vec2(100f, 100f), Vec2.ZERO, PointerEvent.EventType.DOWN);
        StrokeGestureEvent result = recognizer.process(event);
        assertNull(result);
    }

    // ------------------------------------------------------------------
    // Batch processing
    // ------------------------------------------------------------------

    @Test
    public void testProcessBatchOfEvents() {
        List<PointerEvent> events = new ArrayList<>();
        events.add(makePenDown(100f, 100f));
        events.add(makePenMove(105f, 105f));
        events.add(makePenMove(110f, 110f));
        events.add(makePenUp(115f, 115f));

        List<StrokeGestureEvent> results = recognizer.process(events);
        assertEquals(1, results.size());
        StrokeGestureEvent event = results.get(0);
        assertNotNull(event);
        assertTrue(event.getPointCount() > 0);
    }

    @Test
    public void testProcessNullEvents() {
        List<StrokeGestureEvent> results = recognizer.process((List<PointerEvent>) null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testProcessEmptyEvents() {
        List<StrokeGestureEvent> results = recognizer.process(new ArrayList<>());
        assertTrue(results.isEmpty());
    }

    // ------------------------------------------------------------------
    // Short stroke = POINT shape
    // ------------------------------------------------------------------

    @Test
    public void testVeryShortStrokeIsPoint() {
        List<PointerEvent> events = new ArrayList<>();
        events.add(makePenDown(100f, 100f));
        // Move only 1 pixel
        events.add(makePenMove(101f, 101f));
        events.add(makePenUp(101f, 101f));
        List<StrokeGestureEvent> results = recognizer.process(events);

        assertEquals(1, results.size());
        StrokeGestureEvent result = results.get(0);
        assertNotNull(result);
        assertEquals(StrokeGestureEvent.ShapeType.POINT, result.getShapeType());
        assertTrue(result.isSuccess());
        assertEquals(1, result.getPointCount());
    }

    // ------------------------------------------------------------------
    // StrokeRecognizer integration
    // ------------------------------------------------------------------

    @Test
    public void testStrokeWithCustomRecognizer() {
        StrokeRecognizer customRecognizer = new StrokeRecognizer() {
            @Override
            public ShapeRecognitionResult recognize(List<Vec2> stroke) {
                return new ShapeRecognitionResult(
                        ShapeRecognitionResult.ShapeType.CIRCLE,
                        0.95f,
                        new float[]{200f, 200f, 50f} // center + radius
                );
            }

            @Override
            public String getName() {
                return "test-recognizer";
            }
        };

        StrokeGestureRecognizer pen = new StrokeGestureRecognizer(customRecognizer);
        assertEquals("test-recognizer", pen.getName());

        List<PointerEvent> events = new ArrayList<>();
        events.add(makePenDown(100f, 100f));
        events.add(makePenMove(150f, 100f));
        events.add(makePenMove(200f, 150f));
        events.add(makePenUp(200f, 200f));

        List<StrokeGestureEvent> results = pen.process(events);
        assertEquals(1, results.size());
        assertEquals(StrokeGestureEvent.ShapeType.CIRCLE, results.get(0).getShapeType());
        assertEquals(0.95f, results.get(0).getConfidence(), 0.001f);
    }

    @Test
    public void testDefaultRecognizerReturnsUnknown() {
        List<PointerEvent> events = new ArrayList<>();
        events.add(makePenDown(100f, 100f));
        events.add(makePenMove(150f, 100f));
        events.add(makePenMove(200f, 150f));
        events.add(makePenUp(200f, 200f));

        List<StrokeGestureEvent> results = recognizer.process(events);
        assertEquals(1, results.size());
        // Default stub always returns UNKNOWN
        assertEquals(StrokeGestureEvent.ShapeType.UNKNOWN, results.get(0).getShapeType());
    }

    // ------------------------------------------------------------------
    // Helper methods
    // ------------------------------------------------------------------

    private PointerEvent makePenDown(float x, float y) {
        return new PointerEvent(0, PointerEvent.PointerType.PEN,
                new Vec2(x, y), Vec2.ZERO, PointerEvent.EventType.DOWN, 0.8f);
    }

    private PointerEvent makePenMove(float x, float y) {
        return new PointerEvent(0, PointerEvent.PointerType.PEN,
                new Vec2(x, y), new Vec2(x - 100f, y - 100f), PointerEvent.EventType.MOVE, 0.8f);
    }

    private PointerEvent makePenUp(float x, float y) {
        return new PointerEvent(0, PointerEvent.PointerType.PEN,
                new Vec2(x, y), Vec2.ZERO, PointerEvent.EventType.UP, 0.8f);
    }
}
