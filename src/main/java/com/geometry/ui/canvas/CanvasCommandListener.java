package com.geometry.ui.canvas;

import com.geometry.scene.SceneObject;

/** Receives semantic canvas gestures after the UI has resolved a target. */
public interface CanvasCommandListener {

    void onSelectionChanged(SceneObject object);

    void onMove(SceneObject object, float deltaX, float deltaY);

    void onRotate(SceneObject object, float angleDegrees);

    void onScale(SceneObject object, float scaleFactor);

    void onCut(SceneObject object);
}
