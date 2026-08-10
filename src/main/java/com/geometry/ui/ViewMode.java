package com.geometry.ui;

/**
 * Phase 11 - View mode for the UI system.
 *
 * Controls the rendering mode of the 3D viewport:
 *   - MODE_2D: Orthographic projection, z=0 plane
 *   - MODE_3D: Perspective projection with camera
 *
 * Switching view mode changes the camera projection but does not
 * modify the scene geometry.
 *
 * Not thread-safe.
 */
public enum ViewMode {

    /**
     * 2D orthographic mode. Suitable for plane geometry teaching.
     * Camera is fixed at z=5 looking along negative z-axis.
     */
    MODE_2D,

    /**
     * 3D perspective mode. Suitable for solid geometry teaching.
     * Camera can be rotated and zoomed freely.
     */
    MODE_3D
}
