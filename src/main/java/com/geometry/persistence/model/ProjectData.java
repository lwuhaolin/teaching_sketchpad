package com.geometry.persistence.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Phase 10 - Root data model for a Geometry Teaching Project.
 *
 * Represents the top-level structure of a .gtp (Geometry Teaching Project) file.
 * Contains all subsystem data: scene, teaching, animation, whiteboard, and settings.
 *
 * Design:
 *   - version field ensures future format migrations
 *   - id is auto-generated UUID for uniqueness
 *   - name is human-readable project title
 *
 * Not thread-safe.
 */
public class ProjectData {

    private static final String CURRENT_VERSION = "2.0";

    private final String id;
    private String version;
    private String name;
    private SceneData scene;
    private TeachingData teaching;
    private AnimationData animation;
    private WhiteboardData whiteboard;
    private SettingData settings;

    /**
     * Create an empty ProjectData with auto-generated ID.
     */
    public ProjectData() {
        this.id = UUID.randomUUID().toString();
        this.version = CURRENT_VERSION;
        this.name = "Untitled";
        this.scene = new SceneData();
        this.teaching = new TeachingData();
        this.animation = new AnimationData();
        this.whiteboard = new WhiteboardData();
        this.settings = new SettingData();
    }

    /**
     * Create a ProjectData with the given name.
     *
     * @param name human-readable project name
     */
    public ProjectData(String name) {
        this();
        this.name = name;
    }

    // ------------------------------------------------------------------
    // Properties
    // ------------------------------------------------------------------

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SceneData getScene() {
        return scene;
    }

    public void setScene(SceneData scene) {
        this.scene = scene != null ? scene : new SceneData();
    }

    public TeachingData getTeaching() {
        return teaching;
    }

    public void setTeaching(TeachingData teaching) {
        this.teaching = teaching != null ? teaching : new TeachingData();
    }

    public AnimationData getAnimation() {
        return animation;
    }

    public void setAnimation(AnimationData animation) {
        this.animation = animation != null ? animation : new AnimationData();
    }

    public WhiteboardData getWhiteboard() {
        return whiteboard;
    }

    public void setWhiteboard(WhiteboardData whiteboard) {
        this.whiteboard = whiteboard != null ? whiteboard : new WhiteboardData();
    }

    public SettingData getSettings() {
        return settings;
    }

    public void setSettings(SettingData settings) {
        this.settings = settings != null ? settings : new SettingData();
    }

    // ------------------------------------------------------------------
    // Object equality
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectData that = (ProjectData) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ProjectData{id='" + id + "', name='" + name + "', version='" + version
                + "', objects=" + (scene != null ? scene.getObjectCount() : 0)
                + ", lessons=" + (teaching != null ? teaching.getLessonCount() : 0)
                + ", sequences=" + (animation != null ? animation.getSequenceCount() : 0)
                + ", strokes=" + (whiteboard != null ? whiteboard.getStrokeCount() : 0)
                + "}";
    }
}
