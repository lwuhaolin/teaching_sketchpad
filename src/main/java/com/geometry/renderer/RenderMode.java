package com.geometry.renderer;

/**
 * Phase 03 - Rendering mode selector.
 *
 * Determines which projection system the Camera and Renderer use.
 *
 * MODE_2D: Orthographic projection, z=0 plane. Used for 2D geometry teaching.
 * MODE_3D: Perspective projection with depth. Used for 3D geometry teaching.
 */
public enum RenderMode {
    MODE_2D,
    MODE_3D
}
