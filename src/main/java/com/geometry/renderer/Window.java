package com.geometry.renderer;

import org.lwjgl.glfw.*;
import org.lwjgl.system.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Phase 03 - GLFW Window wrapper.
 *
 * Manages the native GLFW window handle and provides a clean dispose() method
 * to clean up native resources.
 *
 * This class is intentionally thin — all window operations go through GLFW.
 */
public class Window {

    private final long handle;

    /**
     * Create a Window from an existing GLFW window handle.
     *
     * @param handle the native GLFWwindow pointer
     */
    public Window(long handle) {
        this.handle = handle;
    }

    /**
     * Get the native GLFW window handle.
     */
    public long getHandle() {
        return handle;
    }

    /**
     * Check whether the user has requested the window to close.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    /**
     * Request the window to close.
     */
    public void close() {
        glfwSetWindowShouldClose(handle, true);
    }

    /**
     * Get the width of the window's content area in pixels.
     */
    public int getWidth() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(handle, width, height);
        return width[0];
    }

    /**
     * Get the height of the window's content area in pixels.
     */
    public int getHeight() {
        int[] width = new int[1];
        int[] height = new int[1];
        glfwGetWindowSize(handle, width, height);
        return height[0];
    }

    /**
     * Set the window title.
     */
    public void setTitle(String title) {
        glfwSetWindowTitle(handle, title);
    }

    /**
     * Clean up the native GLFW window.
     */
    public void dispose() {
        if (handle != 0L) {
            glfwDestroyWindow(handle);
        }
    }
}
