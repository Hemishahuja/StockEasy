package com.example.stockeasy.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CacheServiceTest {

    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    @Test
    void testPutAndGet() {
        // Given
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(5);

        // When
        cacheService.put(key, value, ttl);
        Object result = cacheService.get(key);

        // Then
        assertEquals(value, result);
    }

    @Test
    void testGetNonExistentKey() {
        // When
        Object result = cacheService.get("nonExistentKey");

        // Then
        assertNull(result);
    }

    @Test
    void testExpiredEntry() throws InterruptedException {
        // Given
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMillis(100); // Very short TTL

        // When
        cacheService.put(key, value, ttl);
        Thread.sleep(150); // Wait for expiration
        Object result = cacheService.get(key);

        // Then
        assertNull(result);
    }

    @Test
    void testRemove() {
        // Given
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(5);

        cacheService.put(key, value, ttl);
        assertNotNull(cacheService.get(key));

        // When
        cacheService.remove(key);
        Object result = cacheService.get(key);

        // Then
        assertNull(result);
    }

    @Test
    void testClear() {
        // Given
        cacheService.put("key1", "value1", Duration.ofMinutes(5));
        cacheService.put("key2", "value2", Duration.ofMinutes(5));
        assertEquals(2, cacheService.size());

        // When
        cacheService.clear();

        // Then
        assertEquals(0, cacheService.size());
        assertNull(cacheService.get("key1"));
        assertNull(cacheService.get("key2"));
    }

    @Test
    void testContainsKey() {
        // Given
        String key = "testKey";
        String value = "testValue";
        Duration ttl = Duration.ofMinutes(5);

        // When & Then
        assertFalse(cacheService.containsKey(key));

        cacheService.put(key, value, ttl);
        assertTrue(cacheService.containsKey(key));

        cacheService.remove(key);
        assertFalse(cacheService.containsKey(key));
    }

    @Test
    void testSize() {
        // Given
        assertEquals(0, cacheService.size());

        // When
        cacheService.put("key1", "value1", Duration.ofMinutes(5));
        cacheService.put("key2", "value2", Duration.ofMinutes(5));

        // Then
        assertEquals(2, cacheService.size());
    }

    @Test
    void testSizeWithExpiredEntries() throws InterruptedException {
        // Given
        cacheService.put("key1", "value1", Duration.ofMinutes(5));
        cacheService.put("key2", "value2", Duration.ofMillis(100)); // Short TTL

        assertEquals(2, cacheService.size());

        // When
        Thread.sleep(150); // Wait for second entry to expire

        // Then
        assertEquals(1, cacheService.size()); // Expired entry should be cleaned up
        assertNull(cacheService.get("key2"));
    }

    @Test
    void testOverwriteExistingKey() {
        // Given
        String key = "testKey";
        String value1 = "value1";
        String value2 = "value2";
        Duration ttl = Duration.ofMinutes(5);

        // When
        cacheService.put(key, value1, ttl);
        assertEquals(value1, cacheService.get(key));

        cacheService.put(key, value2, ttl);
        Object result = cacheService.get(key);

        // Then
        assertEquals(value2, result);
    }
}
