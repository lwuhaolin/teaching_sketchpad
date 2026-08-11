package com.geometry.renderer;

/** Optional renderer capability used by the mode controller. */
public interface ModeSwitchableRenderer {
    void setRenderMode(RenderMode mode);
    RenderMode getRenderMode();
}
