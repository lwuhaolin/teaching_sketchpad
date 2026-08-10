package com.geometry.persistence;

import com.geometry.persistence.model.*;
import com.geometry.persistence.registry.AnimationRegistry;
import com.geometry.persistence.registry.GeometryRegistry;
import com.geometry.scene.Scene;
import com.geometry.scene.SceneObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Phase 10 - Deserializer for reading .gtp project files.
 *
 * Reads a .gtp JSON file and reconstructs:
 *   - Scene with all GeometryObjects (regenerated from parameters)
 *   - Teaching data (lessons, steps, annotations)
 *   - Animation sequences
 *   - Whiteboard strokes
 *   - Settings
 *
 * Deserialization flow:
 *   .gtp File
 *     ↓
 *   JSON string
 *     ↓
 *   ProjectData (data model)
 *     ↓
 *   GeometryRegistry → GeometryObjects
 *     ↓
 *   Scene + Teaching + Animation + Whiteboard
 *
 * Design principles:
 *   - Geometry objects are recreated from parameters (not from mesh data)
 *   - Version migration is applied before deserialization
 *   - Missing or invalid data results in graceful degradation
 *
 * Not thread-safe.
 */
public class ProjectDeserializer {

    private final GeometryRegistry geometryRegistry;
    private final AnimationRegistry animationRegistry;

    /**
     * Create a ProjectDeserializer with registries.
     *
     * @param geometryRegistry the registry for creating geometry objects
     * @param animationRegistry the registry for creating animations
     */
    public ProjectDeserializer(GeometryRegistry geometryRegistry, AnimationRegistry animationRegistry) {
        if (geometryRegistry == null) {
            throw new IllegalArgumentException("GeometryRegistry cannot be null");
        }
        if (animationRegistry == null) {
            throw new IllegalArgumentException("AnimationRegistry cannot be null");
        }
        this.geometryRegistry = geometryRegistry;
        this.animationRegistry = animationRegistry;
    }

    // ------------------------------------------------------------------
    // Main entry points
    // ------------------------------------------------------------------

    /**
     * Deserialize a .gtp file into a Scene and ProjectData.
     */
    public ProjectData deserialize(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (!filePath.toLowerCase().endsWith(".gtp")) {
            throw new IllegalArgumentException("File must have .gtp extension: " + filePath);
        }
        String json = readFile(filePath);
        ProjectData data = parseJson(json);
        return VersionMigration.migrate(data);
    }

    /**
     * Deserialize a .gtp file directly into a Scene.
     */
    public Scene deserializeToScene(String filePath) throws IOException {
        ProjectData data = deserialize(filePath);
        return rebuildScene(data.getScene());
    }

    /**
     * Parse a JSON string into ProjectData.
     */
    public ProjectData parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON cannot be null or empty");
        }
        Scanner scanner = new Scanner(json);
        ProjectData data = new ProjectData();

        // Expect top-level object
        scanner.expect('{');

        while (scanner.peek() != '}' && !scanner.done()) {
            String key = scanner.nextStringKey();
            if (key.equals("version")) {
                data.setVersion(scanner.nextString());
            } else if (key.equals("project")) {
                scanner.skip('{');
                while (scanner.peek() != '}' && !scanner.done()) {
                    String pKey = scanner.nextStringKey();
                    if (pKey.equals("id")) {
                        // project id — not stored in ProjectData
                        scanner.skipValue();
                    } else if (pKey.equals("name")) {
                        data.setName(scanner.nextString());
                    } else {
                        scanner.skipValue();
                    }
                    if (scanner.peek() == ',') scanner.next();
                }
                scanner.expect('}');
            } else if (key.equals("scene")) {
                data.setScene(parseScene(scanner));
            } else if (key.equals("teaching")) {
                data.setTeaching(parseTeaching(scanner));
            } else if (key.equals("animation")) {
                data.setAnimation(parseAnimation(scanner));
            } else if (key.equals("whiteboard")) {
                data.setWhiteboard(parseWhiteboard(scanner));
            } else if (key.equals("settings")) {
                data.setSettings(parseSettings(scanner));
            } else {
                scanner.skipValue();
            }
            if (scanner.peek() == ',') scanner.next();
        }
        scanner.expect('}');
        return data;
    }

    // ------------------------------------------------------------------
    // JSON Scanner (minimal token-based parser)
    // ------------------------------------------------------------------

    private static class Scanner {
        private final String s;
        private int pos;

        Scanner(String s) {
            this.s = s;
            this.pos = 0;
        }

        private void skipWhitespace() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }

        private char peek() {
            skipWhitespace();
            return pos < s.length() ? s.charAt(pos) : '\0';
        }

        private char next() {
            skipWhitespace();
            if (pos >= s.length()) throw new IllegalStateException("Unexpected end of JSON");
            return s.charAt(pos++);
        }

        private void expect(char c) {
            char got = next();
            if (got != c) {
                throw new IllegalArgumentException("Expected '" + c + "' but got '" + got + "' at position " + (pos - 1));
            }
        }

        private boolean done() {
            skipWhitespace();
            return pos >= s.length();
        }

        private String nextStringKey() {
            skipWhitespace();
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '\\') {
                    if (pos < s.length()) sb.append(s.charAt(pos++));
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                }
            }
            expect(':');
            return sb.toString();
        }

        private String nextString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char c = s.charAt(pos++);
                if (c == '\\') {
                    if (pos < s.length()) {
                        char esc = s.charAt(pos++);
                        switch (esc) {
                            case 'n': sb.append('\n'); break;
                            case 'r': sb.append('\r'); break;
                            case 't': sb.append('\t'); break;
                            case '"': sb.append('"'); break;
                            case '\\': sb.append('\\'); break;
                            default: sb.append(esc); break;
                        }
                    }
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private float nextFloat() {
            skipWhitespace();
            int start = pos;
            while (pos < s.length() && "0123456789.eE+-".indexOf(s.charAt(pos)) >= 0) pos++;
            try {
                return Float.parseFloat(s.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid float at position " + start);
            }
        }

        private int nextInt() {
            skipWhitespace();
            int start = pos;
            while (pos < s.length() && "0123456789-".indexOf(s.charAt(pos)) >= 0) pos++;
            try {
                return Integer.parseInt(s.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid int at position " + start);
            }
        }

        private long nextLong() {
            skipWhitespace();
            int start = pos;
            while (pos < s.length() && "0123456789-".indexOf(s.charAt(pos)) >= 0) pos++;
            try {
                return Long.parseLong(s.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid long at position " + start);
            }
        }

        private boolean nextBoolean() {
            skipWhitespace();
            if (s.startsWith("true", pos)) { pos += 4; return true; }
            if (s.startsWith("false", pos)) { pos += 5; return false; }
            throw new IllegalArgumentException("Expected boolean at position " + pos);
        }

        private void nextNull() {
            skipWhitespace();
            if (!s.startsWith("null", pos)) {
                throw new IllegalArgumentException("Expected null at position " + pos);
            }
            pos += 4;
        }

        /** Skip a complete value (string, number, bool, null, array, object). */
        private void skipValue() {
            skipWhitespace();
            if (pos >= s.length()) return;
            char c = s.charAt(pos);
            if (c == '"') {
                pos++; // skip opening quote
                while (pos < s.length()) {
                    if (s.charAt(pos) == '\\') { pos += 2; continue; }
                    if (s.charAt(pos) == '"') { pos++; break; }
                    pos++;
                }
            } else if (c == '{') {
                int depth = 1; pos++;
                while (pos < s.length() && depth > 0) {
                    if (s.charAt(pos) == '\\') { pos += 2; continue; }
                    if (s.charAt(pos) == '"') { pos++; continue; } // skip inside string
                    if (s.charAt(pos) == '{') depth++;
                    else if (s.charAt(pos) == '}') depth--;
                    pos++;
                }
            } else if (c == '[') {
                int depth = 1; pos++;
                while (pos < s.length() && depth > 0) {
                    if (s.charAt(pos) == '\\') { pos += 2; continue; }
                    if (s.charAt(pos) == '"') { pos++; continue; }
                    if (s.charAt(pos) == '[') depth++;
                    else if (s.charAt(pos) == ']') depth--;
                    pos++;
                }
            } else {
                // number, bool, null
                while (pos < s.length()) {
                    char ch = s.charAt(pos);
                    if (ch == ',' || ch == '}' || ch == ']' || Character.isWhitespace(ch)) break;
                    pos++;
                }
            }
        }

        /** Skip past '{' and return scanner positioned inside. */
        private void skip(char open) {
            expect(open);
        }
    }

    // ------------------------------------------------------------------
    // Sub-parser methods
    // ------------------------------------------------------------------

    private SceneData parseScene(Scanner sc) {
        sc.skip('{');
        SceneData data = new SceneData();

        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("objects")) {
                data.setObjects(parseObjectArray(sc));
            } else if (key.equals("layers")) {
                parseStringArray(sc, data::addLayer);
            } else if (key.equals("camera")) {
                data.setCamera(parseCamera(sc));
            } else if (key.equals("viewMode")) {
                data.setViewMode(sc.nextString());
            } else if (key.equals("selectedObject")) {
                data.setSelectedObjectId(sc.nextString());
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return data;
    }

    private List<ObjectData> parseObjectArray(Scanner sc) {
        sc.skip('[');
        List<ObjectData> list = new ArrayList<>();
        while (!sc.done() && sc.peek() != ']') {
            list.add(parseObject(sc));
            if (sc.peek() == ',') sc.next();
        }
        sc.expect(']');
        return list;
    }

    private ObjectData parseObject(Scanner sc) {
        sc.skip('{');
        String id = null, type = null;
        float[] position = {0f, 0f, 0f}, rotation = {0f, 0f, 0f}, scale = {1f, 1f, 1f};
        Map<String, Float> params = new HashMap<>();

        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("id")) { id = sc.nextString(); }
            else if (key.equals("type")) { type = sc.nextString(); }
            else if (key.equals("transform")) {
                sc.skip('{');
                while (!sc.done() && sc.peek() != '}') {
                    String tk = sc.nextStringKey();
                    float[] arr = parseFloatArray(sc);
                    if (tk.equals("position")) position = arr;
                    else if (tk.equals("rotation")) rotation = arr;
                    else if (tk.equals("scale")) scale = arr;
                    else sc.skipValue();
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect('}');
            } else if (key.equals("parameters")) {
                params = parseFloatMap(sc);
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');

        if (id == null || type == null) {
            throw new IllegalArgumentException("ObjectData missing id or type");
        }
        return new ObjectData(id, type, position, rotation, scale, params);
    }

    private float[] parseFloatArray(Scanner sc) {
        sc.skip('[');
        List<Float> vals = new ArrayList<>();
        while (!sc.done() && sc.peek() != ']') {
            vals.add(sc.nextFloat());
            if (sc.peek() == ',') sc.next();
        }
        sc.expect(']');
        float[] result = new float[vals.size()];
        for (int i = 0; i < vals.size(); i++) result[i] = vals.get(i);
        return result;
    }

    private Map<String, Float> parseFloatMap(Scanner sc) {
        sc.skip('{');
        Map<String, Float> map = new HashMap<>();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            map.put(key, sc.nextFloat());
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return map;
    }

    private CameraData parseCamera(Scanner sc) {
        sc.skip('{');
        float[] pos = {0f, 0f, 10f}, target = {0f, 0f, 0f}, up = {0f, 1f, 0f};
        float fov = 45f;
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("position")) pos = parseFloatArray(sc);
            else if (key.equals("target")) target = parseFloatArray(sc);
            else if (key.equals("up")) up = parseFloatArray(sc);
            else if (key.equals("fov")) fov = sc.nextFloat();
            else sc.skipValue();
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return new CameraData(pos, target, up, fov);
    }

    private TeachingData parseTeaching(Scanner sc) {
        sc.skip('{');
        TeachingData data = new TeachingData();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("lessons")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    data.addLesson(parseLesson(sc));
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else if (key.equals("currentLessonId")) {
                data.setCurrentLessonId(sc.nextString());
            } else if (key.equals("currentStepNumber")) {
                data.setCurrentStepNumber(sc.nextInt());
            } else if (key.equals("teachingMode")) {
                data.setTeachingMode(sc.nextString());
            } else if (key.equals("annotations")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    AnnotationData a = parseAnnotation(sc);
                    if (a != null) data.addAnnotation(a);
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else if (key.equals("assistants")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    AssistanceData aid = parseAssistance(sc);
                    if (aid != null) data.addAssistant(aid);
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return data;
    }

    private LessonData parseLesson(Scanner sc) {
        sc.skip('{');
        String title = null, desc = null;
        LessonData lesson = new LessonData();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("title")) title = sc.nextString();
            else if (key.equals("description")) desc = sc.nextString();
            else if (key.equals("steps")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    lesson.addStep(parseStep(sc));
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        lesson.setTitle(title != null ? title : "");
        lesson.setDescription(desc != null ? desc : "");
        return lesson;
    }

    private StepData parseStep(Scanner sc) {
        sc.skip('{');
        int num = 0;
        String title = null, desc = null, animId = null;
        List<String> actions = new ArrayList<>();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("number")) num = sc.nextInt();
            else if (key.equals("title")) title = sc.nextString();
            else if (key.equals("description")) desc = sc.nextString();
            else if (key.equals("animationId")) animId = sc.nextString();
            else if (key.equals("actions")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    actions.add(sc.nextString());
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return new StepData(num, title, desc, animId, actions);
    }

    private AnnotationData parseAnnotation(Scanner sc) {
        sc.skip('{');
        String typeStr = null, text = null, targetId = null, state = null;
        float[] position = null, start = null, end = null;
        float size = 0.5f, arrowSize = 0.3f, alpha = 0.4f;
        int r = 0, g = 0, b = 0;

        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("type")) { typeStr = sc.nextString(); }
            else if (key.equals("text")) { text = sc.nextString(); }
            else if (key.equals("position")) { position = parseFloatArray(sc); }
            else if (key.equals("size")) { size = sc.nextFloat(); }
            else if (key.equals("start")) { start = parseFloatArray(sc); }
            else if (key.equals("end")) { end = parseFloatArray(sc); }
            else if (key.equals("arrowSize")) { arrowSize = sc.nextFloat(); }
            else if (key.equals("targetId")) { targetId = sc.nextString(); }
            else if (key.equals("state")) { state = sc.nextString(); }
            else if (key.equals("alpha")) { alpha = sc.nextFloat(); }
            else if (key.equals("color")) {
                sc.skip('[');
                r = sc.nextInt(); if (sc.peek() == ',') sc.next();
                g = sc.nextInt(); if (sc.peek() == ',') sc.next();
                b = sc.nextInt();
                sc.expect(']');
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');

        if (typeStr == null) return null;
        if ("TEXT".equals(typeStr)) {
            return new AnnotationData(text, position, size, r, g, b);
        } else if ("ARROW".equals(typeStr)) {
            return new AnnotationData(start, end, arrowSize, r, g, b);
        } else if ("HIGHLIGHT".equals(typeStr)) {
            if (state == null) state = "GLOW";
            return new AnnotationData(targetId, state, r, g, b, alpha);
        }
        return null;
    }

    private AssistanceData parseAssistance(Scanner sc) {
        sc.skip('{');
        String typeStr = null;
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("type")) typeStr = sc.nextString();
            else sc.skipValue();
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        if (typeStr == null) return null;
        try {
            return new AssistanceData(AssistanceData.AssistanceType.valueOf(typeStr), null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private AnimationData parseAnimation(Scanner sc) {
        sc.skip('{');
        AnimationData data = new AnimationData();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("sequences")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    data.addSequence(parseAnimationSequence(sc));
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else if (key.equals("currentSequenceId")) {
                data.setCurrentSequenceId(sc.nextString());
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return data;
    }

    private AnimationSequenceData parseAnimationSequence(Scanner sc) {
        sc.skip('{');
        String name = null, desc = null;
        List<AnimationItemData> items = new ArrayList<>();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("name")) { name = sc.nextString(); }
            else if (key.equals("description")) { desc = sc.nextString(); }
            else if (key.equals("items")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    items.add(parseAnimationItem(sc));
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        if (name == null) name = "";
        AnimationSequenceData seq = new AnimationSequenceData(name);
        seq.setDescription(desc);
        for (AnimationItemData item : items) {
            seq.addItem(item);
        }
        return seq;
    }

    private AnimationItemData parseAnimationItem(Scanner sc) {
        sc.skip('{');
        String name = null, typeStr = null, targetId = null;
        String unfoldType = null, interpolator = "LINEAR";
        float duration = 1f, delay = 0f;
        float[] fromPos = null, toPos = null, fromRot = null, toRot = null;
        float[] fromScale = null, toScale = null;

        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("name")) name = sc.nextString();
            else if (key.equals("type")) typeStr = sc.nextString();
            else if (key.equals("targetId")) targetId = sc.nextString();
            else if (key.equals("fromPosition")) fromPos = parseFloatArray(sc);
            else if (key.equals("toPosition")) toPos = parseFloatArray(sc);
            else if (key.equals("fromRotation")) fromRot = parseFloatArray(sc);
            else if (key.equals("toRotation")) toRot = parseFloatArray(sc);
            else if (key.equals("fromScale")) fromScale = parseFloatArray(sc);
            else if (key.equals("toScale")) toScale = parseFloatArray(sc);
            else if (key.equals("unfoldType")) unfoldType = sc.nextString();
            else if (key.equals("duration")) duration = sc.nextFloat();
            else if (key.equals("interpolator")) interpolator = sc.nextString();
            else if (key.equals("delaySeconds")) delay = sc.nextFloat();
            else sc.skipValue();
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');

        if (name == null || typeStr == null) return null;
        AnimationItemData.AnimationItemType type;
        try { type = AnimationItemData.AnimationItemType.valueOf(typeStr); }
        catch (IllegalArgumentException e) { return null; }

        return new AnimationItemData(name, type, targetId,
                fromPos, toPos, fromRot, toRot, fromScale, toScale,
                unfoldType, duration, interpolator, delay);
    }

    private WhiteboardData parseWhiteboard(Scanner sc) {
        sc.skip('{');
        WhiteboardData data = new WhiteboardData();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("strokes")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    data.addStroke(parseStroke(sc));
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else if (key.equals("canvasWidth")) {
                data.setCanvasWidth(sc.nextInt());
            } else if (key.equals("canvasHeight")) {
                data.setCanvasHeight(sc.nextInt());
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return data;
    }

    private StrokeData parseStroke(Scanner sc) {
        sc.skip('{');
        List<float[]> points = new ArrayList<>();
        float pressure = 0.5f;
        long timestamp = System.currentTimeMillis();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("points")) {
                sc.skip('[');
                while (!sc.done() && sc.peek() != ']') {
                    float[] p = parseFloatArray(sc);
                    if (p.length >= 2) points.add(new float[]{p[0], p[1]});
                    if (sc.peek() == ',') sc.next();
                }
                sc.expect(']');
            } else if (key.equals("pressure")) {
                pressure = sc.nextFloat();
            } else if (key.equals("timestamp")) {
                timestamp = sc.nextLong();
            } else {
                sc.skipValue();
            }
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        if (points.isEmpty()) return null;
        return new StrokeData(points, pressure, timestamp);
    }

    private SettingData parseSettings(Scanner sc) {
        sc.skip('{');
        SettingData data = new SettingData();
        while (!sc.done() && sc.peek() != '}') {
            String key = sc.nextStringKey();
            if (key.equals("renderMode")) data.setRenderMode(sc.nextString());
            else if (key.equals("showGrid")) data.setShowGrid(sc.nextBoolean());
            else if (key.equals("showCoordinateSystem")) data.setShowCoordinateSystem(sc.nextBoolean());
            else if (key.equals("gridSpacing")) data.setGridSpacing(sc.nextFloat());
            else if (key.equals("autoSave")) data.setAutoSave(sc.nextBoolean());
            else if (key.equals("autoSaveIntervalSeconds")) data.setAutoSaveIntervalSeconds(sc.nextInt());
            else sc.skipValue();
            if (sc.peek() == ',') sc.next();
        }
        sc.expect('}');
        return data;
    }

    private void parseStringArray(Scanner sc, java.util.function.Consumer<String> consumer) {
        sc.skip('[');
        while (!sc.done() && sc.peek() != ']') {
            consumer.accept(sc.nextString());
            if (sc.peek() == ',') sc.next();
        }
        sc.expect(']');
    }

    // ------------------------------------------------------------------
    // Scene rebuild
    // ------------------------------------------------------------------

    public Scene rebuildScene(SceneData sceneData) {
        Scene scene = new Scene();
        for (ObjectData objData : sceneData.getObjects()) {
            try {
                com.geometry.core.geometry.GeometryObject geometry =
                        geometryRegistry.create(objData);
                SceneObject so = new SceneObject(objData.getId(), geometry);
                scene.addSceneObject(so);
            } catch (Exception e) {
                System.err.println("Warning: Could not create object '" + objData.getId()
                        + "' of type '" + objData.getType() + "': " + e.getMessage());
            }
        }
        if (sceneData.getSelectedObjectId() != null) {
            scene.selectById(sceneData.getSelectedObjectId());
        }
        return scene;
    }

    // ------------------------------------------------------------------
    // Static helpers
    // ------------------------------------------------------------------

    private int toInt(Object v, int def) {
        if (v == null) return def;
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Float) return ((Float) v).intValue();
        if (v instanceof Number) return ((Number) v).intValue();
        return def;
    }

    private float toFloat(Object v, float def) {
        if (v == null) return def;
        if (v instanceof Float) return (Float) v;
        if (v instanceof Number) return ((Number) v).floatValue();
        return def;
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Legacy compatibility: parse a pre-split JSON fragment.
     * Kept for callers that pass already-extracted fragments.
     */
    public ProjectData parseJsonFragment(String json) {
        return parseJson(json);
    }
}
