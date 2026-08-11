package com.geometry.interaction.mode;

import com.geometry.core.transform.Transform;
import com.geometry.interaction.constraint.Free3DConstraint;
import com.geometry.interaction.constraint.GeometryConstraint;
import com.geometry.interaction.constraint.Planar2DConstraint;
import com.geometry.renderer.ModeSwitchableRenderer;
import com.geometry.renderer.RenderMode;
import com.geometry.renderer.Renderer;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;
import com.geometry.tools.ToolContext;

import java.util.HashMap;
import java.util.Map;

/** Coordinates scene visibility, constraints and renderer projection. */
public final class ModeManager {
    private final Scene scene;
    private final Renderer renderer;
    private final Map<String, Boolean> visibilityBefore2D = new HashMap<>();
    private GeometryMode mode;
    private GeometryConstraint constraint;

    public ModeManager(Scene scene) { this(scene, null); }

    public ModeManager(Scene scene, Renderer renderer) {
        if (scene == null) throw new IllegalArgumentException("Scene cannot be null");
        this.scene = scene;
        this.renderer = renderer;
        this.mode = GeometryMode.MODE_3D;
        this.constraint = new Free3DConstraint();
        syncRenderer();
    }

    public GeometryMode getMode() { return mode; }
    public GeometryConstraint getConstraint() { return constraint; }
    public boolean is2D() { return mode == GeometryMode.MODE_2D; }
    public boolean is3D() { return mode == GeometryMode.MODE_3D; }

    /** Connect a tool context without coupling individual tools to modes. */
    public void applyTo(ToolContext toolContext) {
        if (toolContext == null) throw new IllegalArgumentException("ToolContext cannot be null");
        toolContext.setRenderMode(is2D() ? RenderMode.MODE_2D : RenderMode.MODE_3D);
        toolContext.setConstraint(constraint);
    }

    public void setMode(GeometryMode newMode) {
        if (newMode == null) throw new IllegalArgumentException("GeometryMode cannot be null");
        if (newMode == mode) { applyCurrentMode(); return; }
        if (newMode == GeometryMode.MODE_2D) {
            visibilityBefore2D.clear();
            for (SceneObject object : scene.getAllObjects()) {
                visibilityBefore2D.put(object.getId(), object.isVisible());
            }
        }
        mode = newMode;
        constraint = newMode == GeometryMode.MODE_2D ? new Planar2DConstraint() : new Free3DConstraint();
        applyCurrentMode();
        syncRenderer();
    }

    /** Apply the active mode to an object added after the mode switch. */
    public void applyTo(SceneObject object) {
        if (object == null) return;
        if (is2D()) {
            object.setOverrideTransform(constraint.constrainTransform(object.getEffectiveTransform()));
            if (!visibilityBefore2D.containsKey(object.getId())) {
                visibilityBefore2D.put(object.getId(), object.isVisible());
            }
            object.setVisible(visibilityBefore2D.get(object.getId())
                    && constraint.isMeshCompatible(object.getGeometry()));
        }
    }

    private void applyCurrentMode() {
        if (is2D()) {
            for (SceneObject object : scene.getAllObjects()) {
                if (!visibilityBefore2D.containsKey(object.getId())) {
                    visibilityBefore2D.put(object.getId(), object.isVisible());
                }
                object.setOverrideTransform(constraint.constrainTransform(object.getEffectiveTransform()));
                object.setVisible(visibilityBefore2D.get(object.getId())
                        && constraint.isMeshCompatible(object.getGeometry()));
            }
        } else {
            for (SceneObject object : scene.getAllObjects()) {
                Boolean visible = visibilityBefore2D.get(object.getId());
                if (visible != null) object.setVisible(visible);
            }
            visibilityBefore2D.clear();
        }
    }

    private void syncRenderer() {
        if (renderer instanceof ModeSwitchableRenderer) {
            ((ModeSwitchableRenderer) renderer).setRenderMode(
                    is2D() ? RenderMode.MODE_2D : RenderMode.MODE_3D);
        }
    }
}
