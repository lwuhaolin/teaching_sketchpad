package com.geometry.runtime.resource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Phase 12 - LRU cache manager for frequently used data.
 *
 * Provides a fixed-size LRU (Least Recently Used) cache that
 * automatically evicts the oldest entry when full.
 *
 * Useful for caching:
 *   - Generated mesh data
 *   - Frequently accessed lesson data
 *   - Temporary computation results
 *
 * @param <K> the cache key type
 * @param <V> the cache value type
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class CacheManager<K, V> {

    /** Default maximum cache size. */
    public static final int DEFAULT_MAX_SIZE = 64;

    private final int maxSize;
    private final Map<K, V> cache;
    private int hits = 0;
    private int misses = 0;

    /**
     * Create a CacheManager with the default maximum size.
     */
    public CacheManager() {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Create a CacheManager with the given maximum size.
     *
     * @param maxSize maximum number of entries (must be > 0)
     */
    public CacheManager(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive, got: " + maxSize);
        }
        this.maxSize = maxSize;
        this.cache = new LinkedHashMap<K, V>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > CacheManager.this.maxSize;
            }
        };
    }

    // ------------------------------------------------------------------
    // Cache operations
    // ------------------------------------------------------------------

    /**
     * Get a value from the cache.
     *
     * @param key the key to look up
     * @return the value, or null if not found
     */
    public V get(K key) {
        V value = cache.get(key);
        if (value != null) {
            hits++;
        } else {
            misses++;
        }
        return value;
    }

    /**
     * Put a value into the cache.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        cache.put(key, value);
    }

    /**
     * Remove a value from the cache.
     *
     * @param key the key to remove
     * @return the removed value, or null if not found
     */
    public V remove(K key) {
        return cache.remove(key);
    }

    /**
     * Check if the cache contains the given key.
     */
    public boolean contains(K key) {
        return cache.containsKey(key);
    }

    /**
     * Clear the entire cache.
     */
    public void clear() {
        cache.clear();
        hits = 0;
        misses = 0;
    }

    // ------------------------------------------------------------------
    // Statistics
    // ------------------------------------------------------------------

    /**
     * Get the current number of entries in the cache.
     */
    public int size() {
        return cache.size();
    }

    /**
     * Get the maximum cache size.
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Get the number of cache hits.
     */
    public int getHits() {
        return hits;
    }

    /**
     * Get the number of cache misses.
     */
    public int getMisses() {
        return misses;
    }

    /**
     * Get the cache hit rate (0.0 to 1.0).
     */
    public double getHitRate() {
        int total = hits + misses;
        if (total == 0) {
            return 0.0;
        }
        return (double) hits / total;
    }

    /**
     * Get a summary string.
     */
    public String getSummary() {
        return String.format(
                "CacheManager{size=%d/%d, hits=%d, misses=%d, hitRate=%.1f%%}",
                cache.size(), maxSize, hits, misses,
                getHitRate() * 100);
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
