package com.geometry.ui.panel;

import com.geometry.scene.SceneObject;

/**
 * Phase 11 - Property panel for displaying and editing a selected object's properties.
 *
 * Shows the properties of the currently selected SceneObject:
 *   - Geometry properties (dimensions based on type)
 *   - Position, scale, rotation
 *   - Visibility state
 *
 * The panel does NOT modify the core engine directly.
 * Changes are submitted via UIEventBridge.
 *
 * Not thread-safe.
 */
public class PropertyPanel {

    /** The currently selected object. */
    private SceneObject selectedObject;

    /**
     * Create a PropertyPanel.
     */
    public PropertyPanel() {
        this.selectedObject = null;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    /**
     * Get the preferred width of this panel in pixels.
     */
    public int getPreferredWidth() {
        return 240;
    }

    /**
     * Get the preferred height of this panel in pixels.
     */
    public int getPreferredHeight() {
        return 300;
    }

    /**
     * Get the height per property row in pixels.
     */
    public int getRowHeight() {
        return 28;
    }

    // ------------------------------------------------------------------
    // Selection
    // ------------------------------------------------------------------

    /**
     * Update the panel to show properties of the given object.
     *
     * @param obj the selected object, or null to clear
     */
    public void selectObject(SceneObject obj) {
        this.selectedObject = obj;
    }

    /**
     * Get the currently displayed object.
     */
    public SceneObject getSelectedObject() {
        return selectedObject;
    }

    // ------------------------------------------------------------------
    // Property display
    // ------------------------------------------------------------------

    /**
     * Get a list of property labels to display.
     * The number of rows depends on the object type.
     *
     * @return array of property label strings
     */
    public String[] getPropertyLabels() {
        if (selectedObject == null) {
            return new String[]{"No object selected"};
        }
        return buildPropertyLabels(selectedObject);
    }

    /**
     * Get the number of property rows.
     */
    public int getPropertyRowCount() {
        return getPropertyLabels().length;
    }

    /**
     * Get the property label at the given row index.
     *
     * @param index the 0-based row index
     * @return the label string, or null if index is out of range
     */
    public String getPropertyLabel(int index) {
        String[] labels = getPropertyLabels();
        if (index < 0 || index >= labels.length) {
            return null;
        }
        return labels[index];
    }

    // ------------------------------------------------------------------
    // Property building (type-aware, using instanceof)
    // ------------------------------------------------------------------

    private String[] buildPropertyLabels(SceneObject obj) {
        com.geometry.core.geometry.GeometryObject geo = obj.getGeometry();
        String type = geo.getClass().getSimpleName();
        com.geometry.core.math.Vec3 pos = obj.getEffectiveTransform().getPosition();

        switch (type) {
            case "Cube":
                com.geometry.core.geometry.Cube cube = (com.geometry.core.geometry.Cube) geo;
                return new String[]{
                        "Type: Cube",
                        "Width: " + cube.getWidth(),
                        "Height: " + cube.getHeight(),
                        "Depth: " + cube.getDepth(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Sphere":
                com.geometry.core.geometry.Sphere sphere = (com.geometry.core.geometry.Sphere) geo;
                return new String[]{
                        "Type: Sphere",
                        "Radius: " + sphere.getRadius(),
                        "Segments: " + sphere.getSegments(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Cylinder":
                com.geometry.core.geometry.Cylinder cyl = (com.geometry.core.geometry.Cylinder) geo;
                return new String[]{
                        "Type: Cylinder",
                        "Radius: " + cyl.getRadius(),
                        "Height: " + cyl.getHeight(),
                        "Segments: " + cyl.getSegments(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Cone":
                com.geometry.core.geometry.Cone cone = (com.geometry.core.geometry.Cone) geo;
                return new String[]{
                        "Type: Cone",
                        "Radius: " + cone.getRadius(),
                        "Height: " + cone.getHeight(),
                        "Segments: " + cone.getSegments(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Rectangle":
                com.geometry.core.geometry.Rectangle rect = (com.geometry.core.geometry.Rectangle) geo;
                return new String[]{
                        "Type: Rectangle",
                        "Width: " + rect.getWidth(),
                        "Height: " + rect.getHeight(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Circle":
                com.geometry.core.geometry.Circle circle = (com.geometry.core.geometry.Circle) geo;
                return new String[]{
                        "Type: Circle",
                        "Radius: " + circle.getRadius(),
                        "Segments: " + circle.getSegments(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            case "Polygon":
                com.geometry.core.geometry.Polygon polygon = (com.geometry.core.geometry.Polygon) geo;
                return new String[]{
                        "Type: Polygon",
                        "Vertices: " + polygon.getPoints().size(),
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
            default:
                return new String[]{
                        "Type: " + type,
                        "Position: " + formatVec3(pos),
                        "Visible: " + obj.isVisible()
                };
        }
    }

    private String formatVec3(com.geometry.core.math.Vec3 v) {
        if (v == null) {
            return "(0, 0, 0)";
        }
        return String.format("(%.1f, %.1f, %.1f)", v.x, v.y, v.z);
    }
}
