package com.geometry.app;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Phase 00 - Unit tests for Main entry point.
 */
public class MainTest {

    @Test
    public void testMainClassExists() {
        assertNotNull(Main.class);
    }

    @Test
    public void testMainMethodExists() {
        try {
            assertNotNull(Main.class.getMethod("main", String[].class));
        } catch (NoSuchMethodException e) {
            fail("main method not found: " + e.getMessage());
        }
    }

    @Test
    public void testMainDoesNotThrow() throws Exception {
        Main.main(new String[]{});
    }
}
