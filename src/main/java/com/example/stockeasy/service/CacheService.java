package com.example.stockeasy.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Service for caching API responses to reduce external API calls and respect rate limits.
 */
@Service
public class CacheService {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    /**
     * Cache entry containing the data and expiration time.
     */
    private static class CacheEntry {
        private final Object data;
        private final LocalDateTime expiresAt;

        public CacheEntry(Object data, LocalDateTime expiresAt) {
            this.data = data;
            this.expiresAt = expiresAt;
        }

        public Object getData() {
            return data;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    /**
     * Retrieves data from cache if it exists and hasn't expired.
     *
     * @param key the cache key
     * @return the cached data, or null if not found or expired
     */
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            return entry.getData();
        }
        // Remove expired entry
        if (entry != null) {
            cache.remove(key);
        }
        return null;
    }

    /**
     * Stores data in cache with the specified TTL.
     *
     * @param key the cache key
     * @param data the data to cache
     * @param ttl the time-to-live duration
     */
    public void put(String key, Object data, Duration ttl) {
        LocalDateTime expiresAt = LocalDateTime.now().plus(ttl);
        cache.put(key, new CacheEntry(data, expiresAt));
    }

    /**
     * Removes data from cache.
     *
     * @param key the cache key
     */
    public void remove(String key) {
        cache.remove(key);
    }

    /**
     * Clears all cached data.
     */
    public void clear() {
        cache.clear();
    }

    /**
     * Returns the number of entries in the cache.
     *
     * @return the cache size
     */
    public int size() {
        // Clean up expired entries before returning size
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        return cache.size();
    }

    /**
     * Checks if a key exists in cache and hasn't expired.
     *
     * @param key the cache key
     * @return true if the key exists and is valid, false otherwise
     */
    public boolean containsKey(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                cache.remove(key);
                return false;
            }
            return true;
        }
        return false;
    }
}
