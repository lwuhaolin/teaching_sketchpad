package com.geometry.runtime.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Phase 12 - Resource manager with caching.
 *
 * Manages loading and caching of application resources such as
 * shader files, model data, lesson files, etc.
 *
 * Resources are loaded from the classpath and cached in memory.
 * Subsequent loads of the same resource return the cached instance.
 *
 * Thread-safe for read operations.
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class ResourceManager {

    /** Internal cache: resource name -> loaded bytes. */
    private final Map<String, byte[]> resourceCache = new HashMap<>();

    /** Total bytes loaded. */
    private long totalBytesLoaded = 0;

    /** Number of cache hits. */
    private long cacheHits = 0;

    /** Number of cache misses (actual file reads). */
    private long cacheMisses = 0;

    /**
     * Create a ResourceManager with no pre-loaded resources.
     */
    public ResourceManager() {
    }

    // ------------------------------------------------------------------
    // Loading
    // ------------------------------------------------------------------

    /**
     * Load a resource from the classpath by name.
     * Results are cached for subsequent calls.
     *
     * @param resourceName the resource name (e.g. "shaders/vertex.glsl")
     * @return the resource bytes, or null if not found
     */
    public byte[] loadResource(String resourceName) {
        if (resourceName == null || resourceName.isEmpty()) {
            return null;
        }
        if (resourceCache.containsKey(resourceName)) {
            cacheHits++;
            return resourceCache.get(resourceName);
        }
        cacheMisses++;
        byte[] data = loadFromClasspath(resourceName);
        if (data != null) {
            resourceCache.put(resourceName, data);
            totalBytesLoaded += data.length;
        }
        return data;
    }

    /**
     * Load a resource as a String from the classpath.
     *
     * @param resourceName the resource name
     * @return the resource as a String, or null if not found
     */
    public String loadResourceString(String resourceName) {
        byte[] data = loadResource(resourceName);
        if (data == null) {
            return null;
        }
        return new String(data);
    }

    /**
     * Load a resource from the classpath without caching.
     *
     * @param resourceName the resource name
     * @return the resource bytes, or null if not found
     */
    public byte[] loadResourceUncached(String resourceName) {
        if (resourceName == null || resourceName.isEmpty()) {
            return null;
        }
        return loadFromClasspath(resourceName);
    }

    // ------------------------------------------------------------------
    // Caching
    // ------------------------------------------------------------------

    /**
     * Manually add a resource to the cache.
     *
     * @param resourceName the resource name
     * @param data         the resource bytes
     */
    public void cacheResource(String resourceName, byte[] data) {
        if (resourceName == null || data == null) {
            return;
        }
        resourceCache.put(resourceName, data);
        totalBytesLoaded += data.length;
    }

    /**
     * Remove a resource from the cache.
     *
     * @param resourceName the resource name
     * @return true if the resource was in the cache
     */
    public boolean evictResource(String resourceName) {
        return resourceCache.remove(resourceName) != null;
    }

    /**
     * Clear all cached resources.
     */
    public void clearCache() {
        resourceCache.clear();
        totalBytesLoaded = 0;
        cacheHits = 0;
        cacheMisses = 0;
    }

    // ------------------------------------------------------------------
    // Stats
    // ------------------------------------------------------------------

    /**
     * Get the number of cached resources.
     */
    public int getCacheSize() {
        return resourceCache.size();
    }

    /**
     * Get total bytes loaded (including cache).
     */
    public long getTotalBytesLoaded() {
        return totalBytesLoaded;
    }

    /**
     * Get cache hit count.
     */
    public long getCacheHits() {
        return cacheHits;
    }

    /**
     * Get cache miss count.
     */
    public long getCacheMisses() {
        return cacheMisses;
    }

    /**
     * Get cache hit rate (0.0 to 1.0).
     */
    public double getCacheHitRate() {
        long total = cacheHits + cacheMisses;
        if (total == 0) {
            return 0.0;
        }
        return (double) cacheHits / total;
    }

    /**
     * Get a summary of resource usage.
     */
    public String getSummary() {
        return String.format(
                "ResourceManager{cache=%d, hits=%d, misses=%d, hitRate=%.1f%%, bytes=%d}",
                resourceCache.size(), cacheHits, cacheMisses,
                getCacheHitRate() * 100, totalBytesLoaded);
    }

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    private byte[] loadFromClasspath(String resourceName) {
        InputStream is = ResourceManager.class.getClassLoader()
                .getResourceAsStream(resourceName);
        if (is == null) {
            return null;
        }
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while ((n = is.read(chunk)) != -1) {
                buffer.write(chunk, 0, n);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }
}
