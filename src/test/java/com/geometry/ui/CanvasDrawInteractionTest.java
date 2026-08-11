package com.geometry.ui;

import com.geometry.core.geometry.Cube;
import com.geometry.interaction.action.DrawAction;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.ui.canvas.CanvasCommandListener;
import com.geometry.ui.component.GeometryCanvasView;
import com.geometry.ui.theme.EducationTheme;
import org.junit.Test;

import java.awt.event.MouseEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/** Regression tests for the semantic drawing gesture emitted by the Swing canvas. */
public class CanvasDrawInteractionTest {

    @Test
    public void rectangleGestureProducesWorldCoordinateDrawAction() {
        Scene scene = new Scene();
        GeometryCanvasView canvas = new GeometryCanvasView(scene, null, new EducationTheme());
        canvas.setSize(800, 600);
        RecordingListener listener = new RecordingListener();
        canvas.setCommandListener(listener);
        canvas.armDraw(DrawAction.DrawType.RECTANGLE);

        canvas.dispatchEvent(new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, 0L, 0,
                400, 300, 1, false));
        canvas.dispatchEvent(new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, 1L, 0,
                584, 208, 1, false));

        assertNotNull(listener.drawAction);
        assertEquals(DrawAction.DrawType.RECTANGLE, listener.drawAction.getDrawType());
        assertEquals(true, listener.drawAction.hasWorldCoordinates());
        assertEquals(0f, listener.drawAction.getWorldStart().x, 0.001f);
        assertEquals(2f, listener.drawAction.getWorldEnd().x, 0.001f);
        assertEquals(1f, listener.drawAction.getWorldEnd().y, 0.001f);
    }

    @Test
    public void solidDrawTypesAreCreatedByDrawTool() {
        Scene scene = new Scene();
        com.geometry.tools.ToolContext context = new com.geometry.tools.ToolContext(scene,
                new com.geometry.scene.SelectionManager(), null, null);
        context.setRenderMode(com.geometry.renderer.RenderMode.MODE_3D);
        com.geometry.tools.draw.DrawTool drawTool = new com.geometry.tools.draw.DrawTool(context);
        drawTool.activate();

        drawTool.handle(DrawAction.world(DrawAction.DrawType.CUBE,
                new com.geometry.core.math.Vec3(2f, 1f, 0f), null));

        assertEquals(1, scene.getObjectCount());
        assertEquals(Cube.class, scene.getSelected().getGeometry().getClass());
        assertEquals(2f, scene.getSelected().getEffectiveTransform().getPosition().x, 0.001f);
    }

    private static final class RecordingListener implements CanvasCommandListener {
        private DrawAction drawAction;
        @Override public void onSelectionChanged(SceneObject object) { }
        @Override public void onMove(SceneObject object, float x, float y) { }
        @Override public void onRotate(SceneObject object, float angle) { }
        @Override public void onScale(SceneObject object, float factor) { }
        @Override public void onCut(SceneObject object) { }
        @Override public void onDraw(DrawAction action) { drawAction = action; }
    }
}
