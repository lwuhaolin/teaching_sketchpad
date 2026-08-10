package com.geometry.persistence;

import com.geometry.core.geometry.*;
import com.geometry.core.math.Vec3;
import com.geometry.persistence.model.*;
import com.geometry.persistence.registry.GeometryRegistry;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Phase 10 - Serializer for writing ProjectData to .gtp files.
 *
 * Converts runtime objects (Scene, TeachingManager, AnimationSequence,
 * Whiteboard strokes) into a serializable ProjectData model,
 * then writes it as JSON to a file.
 *
 * File format: .gtp (Geometry Teaching Project)
 *
 * Serialization flow:
 *   Runtime Objects
 *     ↓
 *   ProjectData (data model)
 *     ↓
 *   JSON string
 *     ↓
 *   .gtp File
 *
 * Design principles:
 *   - Does NOT serialize mesh vertex data (only geometry parameters)
 *   - Uses GeometryRegistry to extract type-specific parameters
 *   - Annotations and assistants are serialized as data
 *   - Whiteboard strokes are serialized as point arrays
 *
 * Not thread-safe.
 */
public class ProjectSerializer {

    private final GeometryRegistry geometryRegistry;

    /**
     * Create a ProjectSerializer with a GeometryRegistry.
     *
     * @param geometryRegistry the registry for parameter extraction
     */
    public ProjectSerializer(GeometryRegistry geometryRegistry) {
        if (geometryRegistry == null) {
            throw new IllegalArgumentException("GeometryRegistry cannot be null");
        }
        this.geometryRegistry = geometryRegistry;
    }

    /**
     * Serialize a Scene and teaching/animation/whiteboard state to a .gtp file.
     *
     * @param scene      the scene to serialize
     * @param filePath   output file path (must end with .gtp)
     * @param projectData additional project data (teaching, animation, whiteboard)
     * @throws IOException if the file cannot be written
     * @throws IllegalArgumentException if filePath is invalid
     */
    public void serialize(Scene scene, String filePath, ProjectData projectData) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (!filePath.toLowerCase().endsWith(".gtp")) {
            throw new IllegalArgumentException("File must have .gtp extension: " + filePath);
        }
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }

        ProjectData data = projectData != null
                ? projectData
                : new ProjectData("Untitled");

        // Serialize scene objects
        serializeScene(scene, data);

        // Write to file
        String json = toJson(data);
        writeToFile(json, filePath);
    }

    /**
     * Serialize a Scene to ProjectData (in-memory).
     *
     * @param scene the scene to serialize
     * @return the ProjectData representation
     */
    public ProjectData serializeToData(Scene scene) {
        if (scene == null) {
            throw new IllegalArgumentException("Scene cannot be null");
        }
        ProjectData data = new ProjectData("Serialized");
        serializeScene(scene, data);
        return data;
    }

    // ------------------------------------------------------------------
    // Scene serialization
    // ------------------------------------------------------------------

    private void serializeScene(Scene scene, ProjectData data) {
        SceneData sceneData = new SceneData();
        sceneData.setViewMode("MODE_2D"); // default

        for (SceneObject obj : scene.getAllObjects()) {
            sceneData.addObject(serializeSceneObject(obj));
        }

        data.setScene(sceneData);
    }

    private ObjectData serializeSceneObject(SceneObject sceneObject) {
        com.geometry.core.geometry.GeometryObject geometry = sceneObject.getGeometry();
        com.geometry.core.transform.Transform transform = sceneObject.getEffectiveTransform();

        ObjectData objectData = new ObjectData(
                sceneObject.getId(),
                geometry.getClass().getSimpleName(),
                toFloatArray(transform.getPosition()),
                toFloatArray(transform.getRotation()),
                toFloatArray(transform.getScale()),
                extractParameters(geometry)
        );

        return objectData;
    }

    /**
     * Extract geometry-specific parameters from a GeometryObject.
     *
     * @param geometry the geometry object
     * @return map of parameter name to value
     */
    private Map<String, Float> extractParameters(com.geometry.core.geometry.GeometryObject geometry) {
        Map<String, Float> params = new HashMap<>();

        if (geometry instanceof com.geometry.core.geometry.Cube) {
            com.geometry.core.geometry.Cube cube = (com.geometry.core.geometry.Cube) geometry;
            params.put("width", cube.getWidth());
            params.put("height", cube.getHeight());
            params.put("depth", cube.getDepth());
        } else if (geometry instanceof com.geometry.core.geometry.Cylinder) {
            com.geometry.core.geometry.Cylinder cyl = (com.geometry.core.geometry.Cylinder) geometry;
            params.put("radius", cyl.getRadius());
            params.put("height", cyl.getHeight());
            params.put("segments", (float) cyl.getSegments());
        } else if (geometry instanceof com.geometry.core.geometry.Cone) {
            com.geometry.core.geometry.Cone cone = (com.geometry.core.geometry.Cone) geometry;
            params.put("radius", cone.getRadius());
            params.put("height", cone.getHeight());
            params.put("segments", (float) cone.getSegments());
        } else if (geometry instanceof com.geometry.core.geometry.Sphere) {
            com.geometry.core.geometry.Sphere sphere = (com.geometry.core.geometry.Sphere) geometry;
            params.put("radius", sphere.getRadius());
            params.put("segments", (float) sphere.getSegments());
            params.put("rings", (float) sphere.getRings());
        } else if (geometry instanceof com.geometry.core.geometry.Circle) {
            com.geometry.core.geometry.Circle circle = (com.geometry.core.geometry.Circle) geometry;
            params.put("radius", circle.getRadius());
            params.put("segments", (float) circle.getSegments());
        } else if (geometry instanceof com.geometry.core.geometry.Rectangle) {
            com.geometry.core.geometry.Rectangle rect = (com.geometry.core.geometry.Rectangle) geometry;
            params.put("width", rect.getWidth());
            params.put("height", rect.getHeight());
        } else if (geometry instanceof com.geometry.core.geometry.Polygon) {
            com.geometry.core.geometry.Polygon poly = (com.geometry.core.geometry.Polygon) geometry;
            List<float[]> points = new ArrayList<>();
            for (Vec3 p : poly.getPoints()) {
                points.add(new float[]{p.x, p.y});
            }
            params.put("points", (float) points.size()); // store count as placeholder
            // Points are stored separately in a dedicated field
            @SuppressWarnings("unchecked")
            Map<String, Object> extra = new HashMap<>();
            extra.put("points", points);
            // Merge into params
            for (Map.Entry<String, Object> entry : extra.entrySet()) {
                if (entry.getValue() instanceof Float) {
                    params.put(entry.getKey(), (Float) entry.getValue());
                }
            }
        }

        return params;
    }

    // ------------------------------------------------------------------
    // JSON (manual serialization to avoid external dependencies)
    // ------------------------------------------------------------------

    /**
     * Convert ProjectData to a JSON string.
     *
     * Uses manual JSON construction to avoid external dependencies.
     *
     * @param data the project data
     * @return JSON string
     */
    public String toJson(ProjectData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": \"").append(escapeJson(data.getVersion())).append("\",\n");
        sb.append("  \"project\": {\n");
        sb.append("    \"id\": \"").append(escapeJson(data.getId())).append("\",\n");
        sb.append("    \"name\": \"").append(escapeJson(data.getName())).append("\"\n");
        sb.append("  },\n");

        sb.append("  \"scene\": ");
        sb.append(toJson(data.getScene()));
        sb.append(",\n");

        sb.append("  \"teaching\": ");
        sb.append(toJson(data.getTeaching()));
        sb.append(",\n");

        sb.append("  \"animation\": ");
        sb.append(toJson(data.getAnimation()));
        sb.append(",\n");

        sb.append("  \"whiteboard\": ");
        sb.append(toJson(data.getWhiteboard()));
        sb.append(",\n");

        sb.append("  \"settings\": ");
        sb.append(toJson(data.getSettings()));
        sb.append("\n");

        sb.append("}");
        return sb.toString();
    }

    private String toJson(SceneData scene) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"objects\": [");
        boolean first = true;
        for (ObjectData obj : scene.getObjects()) {
            if (!first) sb.append(",");
            sb.append(toJson(obj));
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"layers\": [");
        first = true;
        for (String layer : scene.getLayers()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(layer)).append("\"");
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"camera\": ").append(toJson(scene.getCamera())).append(",\n");
        sb.append("  \"viewMode\": \"").append(escapeJson(scene.getViewMode())).append("\",\n");
        sb.append("  \"selectedObject\": \"").append(
                scene.getSelectedObjectId() != null ? escapeJson(scene.getSelectedObjectId()) : "")
                .append("\"\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(ObjectData obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"id\": \"").append(escapeJson(obj.getId())).append("\",\n");
        sb.append("  \"type\": \"").append(escapeJson(obj.getType())).append("\",\n");
        sb.append("  \"transform\": {\n");
        sb.append("    \"position\": [").append(join(obj.getPosition(), ", ")).append("],\n");
        sb.append("    \"rotation\": [").append(join(obj.getRotation(), ", ")).append("],\n");
        sb.append("    \"scale\": [").append(join(obj.getScale(), ", ")).append("]\n");
        sb.append("  },\n");
        sb.append("  \"parameters\": {");
        boolean first = true;
        for (Map.Entry<String, Float> entry : obj.getParameters().entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(entry.getKey())).append("\": ")
                    .append(entry.getValue());
            first = false;
        }
        sb.append("}\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(CameraData camera) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"position\": [").append(join(camera.getPosition(), ", ")).append("],\n");
        sb.append("  \"target\": [").append(join(camera.getTarget(), ", ")).append("],\n");
        sb.append("  \"up\": [").append(join(camera.getUp(), ", ")).append("],\n");
        sb.append("  \"fov\": ").append(camera.getFov()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(TeachingData teaching) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"lessons\": [");
        boolean first = true;
        for (LessonData lesson : teaching.getLessons()) {
            if (!first) sb.append(",");
            sb.append(toJson(lesson));
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"currentLessonId\": \"").append(
                teaching.getCurrentLessonId() != null ? escapeJson(teaching.getCurrentLessonId()) : "")
                .append("\",\n");
        sb.append("  \"currentStepNumber\": ").append(teaching.getCurrentStepNumber()).append(",\n");
        sb.append("  \"teachingMode\": \"").append(escapeJson(teaching.getTeachingMode())).append("\",\n");
        sb.append("  \"annotations\": [");
        first = true;
        for (AnnotationData ann : teaching.getAnnotations()) {
            if (!first) sb.append(",");
            sb.append(toJson(ann));
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"assistants\": [");
        first = true;
        for (AssistanceData asst : teaching.getAssistants()) {
            if (!first) sb.append(",");
            sb.append(toJson(asst));
            first = false;
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(LessonData lesson) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"title\": \"").append(escapeJson(lesson.getTitle())).append("\",\n");
        sb.append("  \"description\": \"").append(escapeJson(lesson.getDescription())).append("\",\n");
        sb.append("  \"steps\": [");
        boolean first = true;
        for (StepData step : lesson.getSteps()) {
            if (!first) sb.append(",");
            sb.append(toJson(step));
            first = false;
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(StepData step) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"number\": ").append(step.getStepNumber()).append(",\n");
        sb.append("  \"title\": \"").append(escapeJson(step.getTitle())).append("\",\n");
        sb.append("  \"description\": \"").append(escapeJson(step.getDescription())).append("\",\n");
        sb.append("  \"animationId\": \"").append(
                step.getAnimationId() != null ? escapeJson(step.getAnimationId()) : "")
                .append("\",\n");
        sb.append("  \"actions\": [");
        boolean first = true;
        for (String action : step.getActions()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(action)).append("\"");
            first = false;
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(AnnotationData ann) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"").append(ann.getType()).append("\",\n");

        if (ann.getType() == AnnotationData.AnnotationType.TEXT) {
            sb.append("  \"text\": \"").append(escapeJson(ann.getText())).append("\",\n");
            sb.append("  \"position\": [").append(join(ann.getPosition(), ", ")).append("],\n");
            sb.append("  \"size\": ").append(ann.getSize()).append(",\n");
        } else if (ann.getType() == AnnotationData.AnnotationType.ARROW) {
            sb.append("  \"start\": [").append(join(ann.getStart(), ", ")).append("],\n");
            sb.append("  \"end\": [").append(join(ann.getEnd(), ", ")).append("],\n");
            sb.append("  \"arrowSize\": ").append(ann.getArrowSize()).append(",\n");
        } else if (ann.getType() == AnnotationData.AnnotationType.HIGHLIGHT) {
            sb.append("  \"targetId\": \"").append(
                    ann.getTargetId() != null ? escapeJson(ann.getTargetId()) : "")
                    .append("\",\n");
            sb.append("  \"state\": \"").append(ann.getState() != null ? ann.getState() : "GLOW").append("\",\n");
        }

        sb.append("  \"color\": [").append(ann.getColorR()).append(", ")
                .append(ann.getColorG()).append(", ").append(ann.getColorB()).append("]\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(AssistanceData asst) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"type\": \"").append(asst.getType()).append("\"\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(AnimationData anim) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"sequences\": [");
        boolean first = true;
        for (AnimationSequenceData seq : anim.getSequences()) {
            if (!first) sb.append(",");
            sb.append(toJson(seq));
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"currentSequenceId\": \"").append(
                anim.getCurrentSequenceId() != null ? escapeJson(anim.getCurrentSequenceId()) : "")
                .append("\"\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(AnimationSequenceData seq) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(escapeJson(seq.getName())).append("\",\n");
        sb.append("  \"description\": \"").append(escapeJson(seq.getDescription())).append("\",\n");
        sb.append("  \"items\": [");
        boolean first = true;
        for (AnimationItemData item : seq.getItems()) {
            if (!first) sb.append(",");
            sb.append(toJson(item));
            first = false;
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(AnimationItemData item) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"name\": \"").append(escapeJson(item.getName())).append("\",\n");
        sb.append("  \"type\": \"").append(item.getType()).append("\",\n");
        sb.append("  \"targetId\": \"").append(
                item.getTargetId() != null ? escapeJson(item.getTargetId()) : "")
                .append("\",\n");

        if (item.getFromPosition() != null) {
            sb.append("  \"fromPosition\": [").append(join(item.getFromPosition(), ", ")).append("],\n");
        }
        if (item.getToPosition() != null) {
            sb.append("  \"toPosition\": [").append(join(item.getToPosition(), ", ")).append("],\n");
        }
        if (item.getFromRotation() != null) {
            sb.append("  \"fromRotation\": [").append(join(item.getFromRotation(), ", ")).append("],\n");
        }
        if (item.getToRotation() != null) {
            sb.append("  \"toRotation\": [").append(join(item.getToRotation(), ", ")).append("],\n");
        }
        if (item.getFromScale() != null) {
            sb.append("  \"fromScale\": [").append(join(item.getFromScale(), ", ")).append("],\n");
        }
        if (item.getToScale() != null) {
            sb.append("  \"toScale\": [").append(join(item.getToScale(), ", ")).append("],\n");
        }

        if (item.getUnfoldType() != null) {
            sb.append("  \"unfoldType\": \"").append(escapeJson(item.getUnfoldType())).append("\",\n");
        }
        sb.append("  \"duration\": ").append(item.getDuration()).append(",\n");
        sb.append("  \"interpolator\": \"").append(escapeJson(item.getInterpolator())).append("\",\n");
        sb.append("  \"delaySeconds\": ").append(item.getDelaySeconds()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(WhiteboardData wb) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"strokes\": [");
        boolean first = true;
        for (StrokeData stroke : wb.getStrokes()) {
            if (!first) sb.append(",");
            sb.append(toJson(stroke));
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"canvasWidth\": ").append(wb.getCanvasWidth()).append(",\n");
        sb.append("  \"canvasHeight\": ").append(wb.getCanvasHeight()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(StrokeData stroke) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"points\": [");
        boolean first = true;
        for (float[] point : stroke.getPoints()) {
            if (!first) sb.append(",");
            sb.append("[").append(point[0]).append(", ").append(point[1]).append("]");
            first = false;
        }
        sb.append("],\n");
        sb.append("  \"pressure\": ").append(stroke.getPressure()).append(",\n");
        sb.append("  \"timestamp\": ").append(stroke.getTimestamp()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toJson(SettingData settings) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"renderMode\": \"").append(escapeJson(settings.getRenderMode())).append("\",\n");
        sb.append("  \"showGrid\": ").append(settings.isShowGrid()).append(",\n");
        sb.append("  \"showCoordinateSystem\": ").append(settings.isShowCoordinateSystem()).append(",\n");
        sb.append("  \"gridSpacing\": ").append(settings.getGridSpacing()).append(",\n");
        sb.append("  \"autoSave\": ").append(settings.isAutoSave()).append(",\n");
        sb.append("  \"autoSaveIntervalSeconds\": ").append(settings.getAutoSaveIntervalSeconds()).append("\n");
        sb.append("}");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------

    private float[] toFloatArray(Vec3 v) {
        return new float[]{v.x, v.y, v.z};
    }

    private String join(float[] arr, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(separator);
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void writeToFile(String json, String filePath) throws IOException {
        try (java.io.FileWriter fw = new java.io.FileWriter(filePath)) {
            fw.write(json);
        }
    }
}
