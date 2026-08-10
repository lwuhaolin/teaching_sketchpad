package com.geometry.scene;

import com.geometry.core.geometry.GeometryObject;
import com.geometry.renderer.Renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Phase 04 - Scene management system.
 *
 * The Scene is the central container for all geometry in the engine.
 * It manages:
 *   - SceneObjects (with unique IDs, visibility, selection)
 *   - Layers (groups of SceneObjects)
 *   - Selection state
 *
 * Rendering flow:
 *   Scene.render(renderer)
 *     → for each SceneObject:
 *       if visible: renderer.renderSceneObject(sceneObject)
 *
 * Update flow:
 *   Scene.update()
 *     → for each SceneObject:
 *       sceneObject.update()  // regenerates mesh if params changed
 *
 * The Scene does NOT handle:
 *   - Mouse input (Phase 05)
 *   - Tool logic (Phase 06)
 *   - File persistence (Phase 10)
 *
 * Not thread-safe.
 */
public class Scene {

    private final ObjectManager objectManager;
    private final List<Layer> layers;
    private final SelectionManager selectionManager;

    /**
     * Create an empty Scene.
     */
    public Scene() {
        this.objectManager = new ObjectManager();
        this.layers = new ArrayList<>();
        this.selectionManager = new SelectionManager();
    }

    // ------------------------------------------------------------------
    // Object management
    // ------------------------------------------------------------------

    /**
     * Add a GeometryObject to the Scene with an auto-generated ID.
     *
     * @param geometry the geometry object to add
     * @return the created SceneObject
     */
    public SceneObject addObject(GeometryObject geometry) {
        if (geometry == null) {
            throw new IllegalArgumentException("GeometryObject cannot be null");
        }
        SceneObject sceneObject = new SceneObject(geometry);
        addSceneObject(sceneObject);
        return sceneObject;
    }

    /**
     * Add a GeometryObject to the Scene with a specific ID.
     *
     * @param id       the unique identifier
     * @param geometry the geometry object to add
     * @return the created SceneObject
     * @throws IllegalArgumentException if an object with the same ID already exists
     */
    public SceneObject addObject(String id, GeometryObject geometry) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (geometry == null) {
            throw new IllegalArgumentException("GeometryObject cannot be null");
        }
        SceneObject sceneObject = new SceneObject(id, geometry);
        addSceneObject(sceneObject);
        return sceneObject;
    }

    /**
     * Add a pre-created SceneObject to the Scene.
     *
     * @param sceneObject the SceneObject to add
     * @throws IllegalArgumentException if an object with the same ID already exists
     */
    public void addSceneObject(SceneObject sceneObject) {
        if (sceneObject == null) {
            throw new IllegalArgumentException("SceneObject cannot be null");
        }
        objectManager.addObject(sceneObject);
        selectionManager.select(sceneObject); // auto-select on add for convenience
    }

    /**
     * Remove a SceneObject from the Scene.
     *
     * @param sceneObject the SceneObject to remove
     * @return true if the object was found and removed
     */
    public boolean removeObject(SceneObject sceneObject) {
        if (sceneObject == null) {
            return false;
        }
        selectionManager.deselect(sceneObject);
        return objectManager.removeObject(sceneObject);
    }

    /**
     * Remove a SceneObject by ID.
     *
     * @param id the ID of the object to remove
     * @return true if the object was found and removed
     */
    public boolean removeObjectById(String id) {
        SceneObject target = findObjectById(id);
        if (target != null) {
            return removeObject(target);
        }
        return false;
    }

    /**
     * Find a SceneObject by its ID.
     *
     * @param id the ID to search for
     * @return the matching SceneObject, or null if not found
     */
    public SceneObject findObjectById(String id) {
        return objectManager.findById(id);
    }

    /**
     * Get all SceneObjects in the Scene.
     */
    public List<SceneObject> getAllObjects() {
        return objectManager.getAll();
    }

    /**
     * Get the number of objects in the Scene.
     */
    public int getObjectCount() {
        return objectManager.size();
    }

    /**
     * Check if the Scene is empty.
     */
    public boolean isEmpty() {
        return objectManager.isEmpty();
    }

    /**
     * Remove all objects from the Scene.
     */
    public void clear() {
        selectionManager.clearSelection();
        objectManager.clear();
        for (Layer layer : layers) {
            layer.clear();
        }
    }

    // ------------------------------------------------------------------
    // Layer management
    // ------------------------------------------------------------------

    /**
     * Add a Layer to the Scene.
     *
     * @param layer the layer to add
     * @return true if the layer was added
     */
    public boolean addLayer(Layer layer) {
        if (layer == null) {
            throw new IllegalArgumentException("Layer cannot be null");
        }
        return layers.add(layer);
    }

    /**
     * Remove a Layer from the Scene.
     *
     * @param layer the layer to remove
     * @return true if the layer was found and removed
     */
    public boolean removeLayer(Layer layer) {
        if (layer == null) {
            return false;
        }
        return layers.remove(layer);
    }

    /**
     * Get all Layers in the Scene.
     */
    public List<Layer> getLayers() {
        return Collections.unmodifiableList(layers);
    }

    /**
     * Get the number of Layers in the Scene.
     */
    public int getLayerCount() {
        return layers.size();
    }

    // ------------------------------------------------------------------
    // Selection
    // ------------------------------------------------------------------

    /**
     * Select a SceneObject.
     *
     * @param sceneObject the object to select
     */
    public void select(SceneObject sceneObject) {
        selectionManager.select(sceneObject);
    }

    /**
     * Select a SceneObject by ID.
     *
     * @param id the ID of the object to select
     * @return true if found and selected
     */
    public boolean selectById(String id) {
        return selectionManager.selectById(id, this::findObjectById);
    }

    /**
     * Clear all selection.
     */
    public void clearSelection() {
        selectionManager.clearSelection();
    }

    /**
     * Get the currently selected SceneObject, or null if none.
     */
    public SceneObject getSelected() {
        return selectionManager.getSelected();
    }

    /**
     * Toggle selection of a SceneObject (select if not selected, deselect if selected).
     *
     * @param sceneObject the object to toggle
     */
    public void toggleSelection(SceneObject sceneObject) {
        selectionManager.toggleSelection(sceneObject);
    }

    /**
     * Get all selected SceneObjects.
     */
    public List<SceneObject> getSelectedObjects() {
        return selectionManager.getSelectedObjects();
    }

    /**
     * Check if a SceneObject is selected.
     */
    public boolean isSelected(SceneObject sceneObject) {
        return selectionManager.isSelected(sceneObject);
    }

    // ------------------------------------------------------------------
    // Update
    // ------------------------------------------------------------------

    /**
     * Update all SceneObjects in the Scene.
     * Called once per frame before rendering.
     */
    public void update() {
        for (SceneObject sceneObject : objectManager.getAll()) {
            sceneObject.update();
        }
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    /**
     * Render all visible SceneObjects using the provided Renderer.
     *
     * The renderer must support rendering SceneObjects.
     * For OpenGLRenderer, this iterates visible objects and calls
     * {@link OpenGLRenderer#renderSceneObject(SceneObject, float[], float[], float[])}.
     *
     * @param renderer the renderer to use
     */
    public void render(Renderer renderer) {
        for (SceneObject sceneObject : objectManager.getAll()) {
            if (!sceneObject.isVisible()) {
                continue;
            }
            renderSceneObject(renderer, sceneObject);
        }
    }

    /**
     * Render a single SceneObject using the provided Renderer.
     *
     * @param renderer      the renderer to use
     * @param sceneObject   the object to render
     */
    public void renderSceneObject(Renderer renderer, SceneObject sceneObject) {
        renderSceneObject(renderer, sceneObject, 0.5f, 0.5f, 0.5f);
    }

    /**
     * Render a single SceneObject using the provided Renderer.
     *
     * @param renderer         the renderer to use
     * @param sceneObject      the object to render
     * @param baseColor        base color (R, G, B in [0, 1])
     * @param selectedColor    highlight color when selected (R, G, B in [0, 1])
     */
    public void renderSceneObject(Renderer renderer, SceneObject sceneObject,
                                  float[] baseColor, float[] selectedColor) {
        if (!sceneObject.isVisible()) {
            return;
        }
        float[] color = sceneObject.isSelected() ? selectedColor : baseColor;
        renderSceneObject(renderer, sceneObject, color);
    }

    /**
     * Render a single SceneObject using the provided Renderer with a single color.
     *
     * @param renderer      the renderer to use
     * @param sceneObject   the object to render
     * @param r             red component [0, 1]
     * @param g             green component [0, 1]
     * @param b             blue component [0, 1]
     */
    public void renderSceneObject(Renderer renderer, SceneObject sceneObject,
                                  float r, float g, float b) {
        if (!sceneObject.isVisible()) {
            return;
        }
        renderSceneObject(renderer, sceneObject, new float[]{r, g, b, 1.0f});
    }

    /**
     * Render a single SceneObject using the provided Renderer with a full color array.
     *
     * @param renderer      the renderer to use
     * @param sceneObject   the object to render
     * @param color         RGBA color array
     */
    public void renderSceneObject(Renderer renderer, SceneObject sceneObject, float[] color) {
        if (!sceneObject.isVisible() || renderer == null) {
            return;
        }
        // Cast to OpenGLRenderer for the actual rendering
        if (renderer instanceof com.geometry.renderer.OpenGLRenderer) {
            ((com.geometry.renderer.OpenGLRenderer) renderer).renderSceneObject(sceneObject, color);
        } else {
            // Fallback: try to render the underlying geometry
            renderer.render();
        }
    }

    // ------------------------------------------------------------------
    // Visibility helpers
    // ------------------------------------------------------------------

    /**
     * Set visibility for all objects in the Scene.
     *
     * @param visible the visibility state
     */
    public void setAllVisible(boolean visible) {
        for (SceneObject obj : objectManager.getAll()) {
            obj.setVisible(visible);
        }
    }

    /**
     * Set visibility for a specific object by ID.
     *
     * @param id      the object ID
     * @param visible the visibility state
     * @return true if the object was found
     */
    public boolean setVisibleById(String id, boolean visible) {
        SceneObject obj = findObjectById(id);
        if (obj != null) {
            obj.setVisible(visible);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Scene{objects=" + objectManager.size()
                + ", layers=" + layers.size()
                + ", selected=" + selectionManager.getSelectedCount() + "}";
    }
}
