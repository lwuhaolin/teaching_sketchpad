package com.geometry.runtime;

import com.geometry.app.Main;
import com.geometry.runtime.config.AppConfig;
import com.geometry.runtime.config.ConfigLoader;
import com.geometry.runtime.logging.CrashReporter;
import com.geometry.runtime.logging.Logger;
import com.geometry.runtime.resource.AssetLoader;
import com.geometry.runtime.resource.CacheManager;
import com.geometry.runtime.resource.ResourceManager;
import com.geometry.runtime.update.VersionManager;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Phase 12 - Tests for the Runtime package.
 *
 * Tests cover:
 *   - RuntimeEnvironment
 *   - SystemChecker / SystemCheckReport / SystemCheckIssue
 *   - AppConfig / ConfigLoader
 *   - ResourceManager / AssetLoader / CacheManager
 *   - Logger / CrashReporter
 *   - VersionManager
 *   - ApplicationLauncher
 */
public class RuntimeTest {

    private File tempDir;
    private ConfigLoader configLoader;
    private Logger logger;

    @Before
    public void setUp() {
        // Create a temporary directory for test files
        tempDir = new File(System.getProperty("java.io.tmpdir"),
                "geometry-engine-test-" + System.currentTimeMillis());
        tempDir.mkdirs();
        configLoader = new ConfigLoader();
        logger = new Logger();
    }

    @Test
    public void tearDown() {
        // Clean up temp directory
        deleteRecursively(tempDir);
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    /** Java 8 compatible readAllBytes helper. */
    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toByteArray();
    }

    // ------------------------------------------------------------------
    // RuntimeEnvironment
    // ------------------------------------------------------------------

    @Test
    public void testRuntimeEnvironmentCreation() {
        RuntimeEnvironment env = new RuntimeEnvironment();
        assertNotNull(env.getOsName());
        assertNotNull(env.getJavaVersion());
        assertNotNull(env.getJavaVendor());
        assertTrue(env.getProcessorCount() > 0);
    }

    @Test
    public void testRuntimeEnvironmentToSummary() {
        RuntimeEnvironment env = new RuntimeEnvironment();
        String summary = env.toSummary();
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
        assertTrue(summary.contains("OS:"));
        assertTrue(summary.contains("Java:"));
    }

    @Test
    public void testRuntimeEnvironmentOpenGLInfo() {
        RuntimeEnvironment env = new RuntimeEnvironment();
        // OpenGL info may be empty in headless/test environment
        String glVersion = env.getOpenGlVersion();
        if (glVersion != null) {
            assertNotNull(env.getOpenGlVendor());
            assertNotNull(env.getOpenGlRenderer());
        }
    }

    @Test
    public void testRuntimeEnvironmentMemory() {
        RuntimeEnvironment env = new RuntimeEnvironment();
        long total = env.getTotalMemoryBytes();
        long available = env.getAvailableMemoryBytes();
        // At least one should be positive
        assertTrue(total >= 0 || available >= 0);
    }

    // ------------------------------------------------------------------
    // SystemCheckIssue
    // ------------------------------------------------------------------

    @Test
    public void testSystemCheckIssuePass() {
        SystemCheckIssue issue = new SystemCheckIssue(SystemCheckIssue.PASS, "test pass");
        assertEquals(SystemCheckIssue.PASS, issue.getResult());
        assertTrue(issue.isPass());
        assertFalse(issue.isWarning());
        assertFalse(issue.isError());
    }

    @Test
    public void testSystemCheckIssueWarning() {
        SystemCheckIssue issue = new SystemCheckIssue(SystemCheckIssue.WARNING, "test warn");
        assertTrue(issue.isWarning());
    }

    @Test
    public void testSystemCheckIssueError() {
        SystemCheckIssue issue = new SystemCheckIssue(SystemCheckIssue.ERROR, "test error");
        assertTrue(issue.isError());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSystemCheckIssueNullResult() {
        new SystemCheckIssue(null, "test");
    }

    // ------------------------------------------------------------------
    // SystemCheckReport
    // ------------------------------------------------------------------

    @Test
    public void testSystemCheckReportEmpty() {
        SystemCheckReport report = new SystemCheckReport();
        assertTrue(report.isPassed());
        assertTrue(report.getIssues().isEmpty());
        assertTrue(report.getErrors().isEmpty());
        assertTrue(report.getWarnings().isEmpty());
    }

    @Test
    public void testSystemCheckReportWithPass() {
        SystemCheckReport report = new SystemCheckReport();
        report.addIssue(SystemCheckIssue.PASS, "Java OK");
        assertTrue(report.isPassed());
        assertEquals(1, report.getIssues().size());
        assertTrue(report.getIssues().get(0).isPass());
    }

    @Test
    public void testSystemCheckReportWithError() {
        SystemCheckReport report = new SystemCheckReport();
        report.addIssue(SystemCheckIssue.PASS, "Java OK");
        report.addIssue(SystemCheckIssue.ERROR, "OpenGL missing");
        assertFalse(report.isPassed());
        assertEquals(2, report.getIssues().size());
        assertEquals(1, report.getErrors().size());
        assertEquals(0, report.getWarnings().size());
    }

    @Test
    public void testSystemCheckReportWithWarning() {
        SystemCheckReport report = new SystemCheckReport();
        report.addIssue(SystemCheckIssue.WARNING, "Low memory");
        assertTrue(report.isPassed()); // warnings don't fail
        assertEquals(1, report.getWarnings().size());
    }

    @Test
    public void testSystemCheckReportToSummary() {
        SystemCheckReport report = new SystemCheckReport();
        report.addIssue(SystemCheckIssue.PASS, "Java OK");
        String summary = report.toSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("PASSED"));
        assertTrue(summary.contains("Java OK"));
    }

    // ------------------------------------------------------------------
    // SystemChecker
    // ------------------------------------------------------------------

    @Test
    public void testSystemCheckerJavaCheck() {
        SystemChecker checker = new SystemChecker();
        RuntimeEnvironment env = new RuntimeEnvironment();
        SystemCheckReport report = checker.check(env);
        assertNotNull(report);
        // Should have at least the Java version check
        assertFalse(report.getIssues().isEmpty());
    }

    @Test
    public void testSystemCheckerWithDirectories() {
        SystemChecker checker = new SystemChecker();
        RuntimeEnvironment env = new RuntimeEnvironment();

        // Create a temp directory for checking
        File testDir = new File(tempDir, "test-check");
        testDir.mkdirs();

        SystemCheckReport report = checker.check(env, Collections.singletonList(testDir));
        assertNotNull(report);
        // Should contain a PASS for the directory
        boolean foundDirPass = false;
        for (SystemCheckIssue issue : report.getIssues()) {
            if (issue.isPass() && issue.getMessage().contains("Directory OK")) {
                foundDirPass = true;
                break;
            }
        }
        assertTrue("Expected a 'Directory OK' pass", foundDirPass);
    }

    @Test
    public void testSystemCheckerNullDir() {
        SystemChecker checker = new SystemChecker();
        RuntimeEnvironment env = new RuntimeEnvironment();
        SystemCheckReport report = checker.check(env,
                Collections.singletonList((File) null));
        assertNotNull(report);
    }

    // ------------------------------------------------------------------
    // AppConfig
    // ------------------------------------------------------------------

    @Test
    public void testAppConfigDefaults() {
        AppConfig config = new AppConfig();
        assertEquals(AppConfig.DEFAULT_WINDOW_WIDTH, config.getWindowWidth());
        assertEquals(AppConfig.DEFAULT_WINDOW_HEIGHT, config.getWindowHeight());
        assertEquals(AppConfig.DEFAULT_FPS, config.getTargetFPS());
        assertEquals(AppConfig.DEFAULT_QUALITY, config.getRenderQuality());
        assertEquals(AppConfig.DEFAULT_LANGUAGE, config.getLanguage());
        assertEquals(AppConfig.DEFAULT_THEME, config.getTheme());
        assertEquals(AppConfig.DEFAULT_PERFORMANCE_MODE, config.getPerformanceMode());
        assertTrue(config.isWindowResizable());
    }

    @Test
    public void testAppConfigWithSize() {
        AppConfig config = new AppConfig(1280, 720);
        assertEquals(1280, config.getWindowWidth());
        assertEquals(720, config.getWindowHeight());
    }

    @Test
    public void testAppConfigSetters() {
        AppConfig config = new AppConfig();
        config.setWindowWidth(800);
        config.setWindowHeight(600);
        config.setTargetFPS(30);
        config.setRenderQuality("HIGH");
        config.setLanguage("en");
        config.setTheme("dark");
        config.setPerformanceMode("COMPATIBILITY");
        config.setWindowResizable(false);

        assertEquals(800, config.getWindowWidth());
        assertEquals(600, config.getWindowHeight());
        assertEquals(30, config.getTargetFPS());
        assertEquals("HIGH", config.getRenderQuality());
        assertEquals("en", config.getLanguage());
        assertEquals("DARK", config.getTheme());
        assertEquals("COMPATIBILITY", config.getPerformanceMode());
        assertFalse(config.isWindowResizable());
    }

    @Test
    public void testAppConfigBounds() {
        AppConfig config = new AppConfig();
        config.setWindowWidth(-100); // should clamp to 400
        config.setWindowHeight(-50); // should clamp to 300
        assertEquals(400, config.getWindowWidth());
        assertEquals(300, config.getWindowHeight());
    }

    @Test
    public void testAppConfigFPSClamping() {
        AppConfig config = new AppConfig();
        config.setTargetFPS(0); // should clamp to 1
        assertEquals(1, config.getTargetFPS());
        config.setTargetFPS(200); // should clamp to 120
        assertEquals(120, config.getTargetFPS());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppConfigNullRenderQuality() {
        new AppConfig().setRenderQuality(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppConfigInvalidRenderQuality() {
        new AppConfig().setRenderQuality("ULTRA");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppConfigNullLanguage() {
        new AppConfig().setLanguage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppConfigNullTheme() {
        new AppConfig().setTheme(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppConfigNullPerformanceMode() {
        new AppConfig().setPerformanceMode(null);
    }

    @Test
    public void testAppConfigToSummary() {
        AppConfig config = new AppConfig(1280, 720);
        String summary = config.toSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("1280"));
        assertTrue(summary.contains("720"));
        assertTrue(summary.contains("AppConfig"));
    }

    // ------------------------------------------------------------------
    // ConfigLoader
    // ------------------------------------------------------------------

    @Test
    public void testConfigLoaderNullFile() {
        AppConfig config = configLoader.load((File) null);
        assertEquals(AppConfig.DEFAULT_WINDOW_WIDTH, config.getWindowWidth());
    }

    @Test
    public void testConfigLoaderMissingFile() {
        AppConfig config = configLoader.load(new File(tempDir, "nonexistent.properties"));
        assertEquals(AppConfig.DEFAULT_WINDOW_WIDTH, config.getWindowWidth());
    }

    @Test
    public void testConfigLoaderFromString() {
        AppConfig config = configLoader.load("");
        assertEquals(AppConfig.DEFAULT_WINDOW_WIDTH, config.getWindowWidth());
    }

    @Test
    public void testConfigLoaderFromClasspath() {
        // Should return defaults since no classpath resource exists
        AppConfig config = configLoader.loadFromClasspath("nonexistent.properties");
        assertEquals(AppConfig.DEFAULT_WINDOW_WIDTH, config.getWindowWidth());
    }

    @Test
    public void testConfigLoaderSaveAndLoad() throws IOException {
        File configFile = new File(tempDir, "test-config.properties");

        AppConfig original = new AppConfig(1280, 720);
        original.setRenderQuality("HIGH");
        original.setTargetFPS(30);
        original.setLanguage("en");
        original.setTheme("dark");
        original.setPerformanceMode("COMPATIBILITY");
        original.setWindowResizable(false);

        boolean saved = configLoader.save(original, configFile);
        assertTrue("Config save should succeed", saved);
        assertTrue("Config file should exist", configFile.exists());

        AppConfig loaded = configLoader.load(configFile);
        assertEquals(1280, loaded.getWindowWidth());
        assertEquals(720, loaded.getWindowHeight());
        assertEquals("HIGH", loaded.getRenderQuality());
        assertEquals(30, loaded.getTargetFPS());
        assertEquals("en", loaded.getLanguage());
        assertEquals("DARK", loaded.getTheme());
        assertEquals("COMPATIBILITY", loaded.getPerformanceMode());
        assertFalse(loaded.isWindowResizable());
    }

    @Test
    public void testConfigLoaderParseFile() throws IOException {
        File configFile = new File(tempDir, "parse-test.properties");
        String content =
                "# Comment line\n" +
                "window.width=1920\n" +
                "window.height=1080\n" +
                "render.quality=LOW\n" +
                "render.fps=30\n" +
                "ui.language=zh\n" +
                "ui.theme=dark\n" +
                "performance.mode=HIGH\n" +
                "window.resizable=false\n";
        FileWriter writer = new FileWriter(configFile);
        writer.write(content);
        writer.close();

        AppConfig config = configLoader.load(configFile);
        assertEquals(1920, config.getWindowWidth());
        assertEquals(1080, config.getWindowHeight());
        assertEquals("LOW", config.getRenderQuality());
        assertEquals(30, config.getTargetFPS());
        assertEquals("zh", config.getLanguage());
        assertEquals("DARK", config.getTheme());
        assertEquals("HIGH", config.getPerformanceMode());
        assertFalse(config.isWindowResizable());
    }

    @Test
    public void testConfigLoaderSaveToFile() throws IOException {
        File configFile = new File(tempDir, "save-test.properties");
        AppConfig config = new AppConfig();
        boolean saved = configLoader.save(config, configFile);
        assertTrue(saved);
        assertTrue(configFile.exists());
    }

    @Test
    public void testConfigLoaderSaveNull() {
        assertFalse(configLoader.save(null, (File) null));
        assertFalse(configLoader.save(new AppConfig(), (String) null));
    }

    // ------------------------------------------------------------------
    // ResourceManager
    // ------------------------------------------------------------------

    @Test
    public void testResourceManagerLoadMissing() {
        ResourceManager rm = new ResourceManager();
        assertNull(rm.loadResource("nonexistent.txt"));
    }

    @Test
    public void testResourceManagerNullResource() {
        ResourceManager rm = new ResourceManager();
        assertNull(rm.loadResource(null));
        assertNull(rm.loadResource(""));
    }

    @Test
    public void testResourceManagerCacheAndEvict() {
        ResourceManager rm = new ResourceManager();
        byte[] data = "test data".getBytes(StandardCharsets.UTF_8);
        rm.cacheResource("test.txt", data);
        assertEquals(data, rm.loadResource("test.txt"));
        assertEquals(1, rm.getCacheSize());
        assertEquals(0, rm.getCacheMisses()); // already cached, so it's a hit

        // Evict
        assertTrue(rm.evictResource("test.txt"));
        assertNull(rm.loadResource("test.txt"));
        assertEquals(0, rm.getCacheSize());
        assertEquals(1, rm.getCacheMisses()); // now it's a miss (not found)
    }

    @Test
    public void testResourceManagerClear() {
        ResourceManager rm = new ResourceManager();
        rm.cacheResource("a.txt", "a".getBytes());
        rm.cacheResource("b.txt", "b".getBytes());
        assertEquals(2, rm.getCacheSize());
        rm.clearCache();
        assertEquals(0, rm.getCacheSize());
        assertEquals(0, rm.getTotalBytesLoaded());
        assertEquals(0, rm.getCacheHits());
        assertEquals(0, rm.getCacheMisses());
    }

    @Test
    public void testResourceManagerStats() {
        ResourceManager rm = new ResourceManager();
        rm.cacheResource("x.txt", "x".getBytes());
        rm.loadResource("x.txt"); // cache hit (already cached)
        rm.loadResource("y.txt"); // cache miss (not found)
        rm.loadResource("x.txt"); // another cache hit

        assertEquals(2, rm.getCacheHits());
        assertEquals(1, rm.getCacheMisses()); // only y.txt is a miss
        double hitRate = rm.getCacheHitRate();
        assertTrue(hitRate >= 0.0 && hitRate <= 1.0);
        assertNotNull(rm.getSummary());
    }

    @Test
    public void testResourceManagerLoadUncached() {
        ResourceManager rm = new ResourceManager();
        assertNull(rm.loadResourceUncached("nonexistent.txt"));
    }

    @Test
    public void testResourceManagerLoadString() {
        ResourceManager rm = new ResourceManager();
        assertNull(rm.loadResourceString("nonexistent.txt"));
    }

    // ------------------------------------------------------------------
    // AssetLoader
    // ------------------------------------------------------------------

    @Test
    public void testAssetLoaderNullManager() {
        try {
            new AssetLoader((ResourceManager) null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testAssetLoaderLoadMissing() {
        AssetLoader loader = new AssetLoader();
        assertNull(loader.load("nonexistent.txt"));
        assertNull(loader.loadString("nonexistent.txt"));
        assertNull(loader.loadModel("test"));
        assertNull(loader.loadLesson("test"));
        assertNull(loader.loadVertexShader("test"));
        assertNull(loader.loadFragmentShader("test"));
        assertNull(loader.loadShaderPair("test"));
    }

    @Test
    public void testAssetLoaderCache() {
        AssetLoader loader = new AssetLoader();
        assertEquals(0, loader.getResourceManager().getCacheSize());
    }

    @Test
    public void testAssetLoaderClearCache() {
        AssetLoader loader = new AssetLoader();
        loader.clearCache(); // should not throw
    }

    @Test
    public void testAssetLoaderStats() {
        AssetLoader loader = new AssetLoader();
        assertNotNull(loader.getStats());
    }

    @Test
    public void testAssetLoaderIsCached() {
        AssetLoader loader = new AssetLoader();
        assertFalse(loader.isCached("nonexistent.txt"));
    }

    // ------------------------------------------------------------------
    // CacheManager
    // ------------------------------------------------------------------

    @Test
    public void testCacheManagerCreate() {
        CacheManager<String, String> cache = new CacheManager<>();
        assertEquals(CacheManager.DEFAULT_MAX_SIZE, cache.getMaxSize());
        assertEquals(0, cache.size());
    }

    @Test
    public void testCacheManagerCustomSize() {
        CacheManager<String, String> cache = new CacheManager<>(10);
        assertEquals(10, cache.getMaxSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCacheManagerInvalidSize() {
        new CacheManager<>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCacheManagerNegativeSize() {
        new CacheManager<>(-1);
    }

    @Test
    public void testCacheManagerPutAndGet() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("key1", "value1");
        assertEquals("value1", cache.get("key1"));
        assertEquals(1, cache.size());
    }

    @Test
    public void testCacheManagerMiss() {
        CacheManager<String, String> cache = new CacheManager<>();
        assertNull(cache.get("missing"));
        assertEquals(0, cache.getHits());
        assertEquals(1, cache.getMisses());
    }

    @Test
    public void testCacheManagerHitThenMiss() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("key", "val");
        assertEquals("val", cache.get("key")); // hit
        assertNull(cache.get("other")); // miss
        assertEquals(1, cache.getHits());
        assertEquals(1, cache.getMisses());
    }

    @Test
    public void testCacheManagerRemove() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("key", "val");
        assertEquals("val", cache.remove("key"));
        assertNull(cache.get("key"));
        assertEquals(0, cache.size());
    }

    @Test
    public void testCacheManagerContains() {
        CacheManager<String, String> cache = new CacheManager<>();
        assertFalse(cache.contains("key"));
        cache.put("key", "val");
        assertTrue(cache.contains("key"));
    }

    @Test
    public void testCacheManagerClear() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("a", "1");
        cache.put("b", "2");
        cache.clear();
        assertEquals(0, cache.size());
        assertEquals(0, cache.getHits());
        assertEquals(0, cache.getMisses());
    }

    @Test
    public void testCacheManagerEviction() {
        CacheManager<String, String> cache = new CacheManager<>(2);
        cache.put("a", "1");
        cache.put("b", "2");
        cache.put("c", "3"); // should evict "a"
        assertEquals(2, cache.size());
        assertNull(cache.get("a"));
        assertNotNull(cache.get("b"));
        assertNotNull(cache.get("c"));
    }

    @Test
    public void testCacheManagerNullKey() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put(null, "value"); // should not throw, but not stored
        assertEquals(0, cache.size());
    }

    @Test
    public void testCacheManagerHitRate() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("k", "v");
        cache.get("k"); // hit
        cache.get("missing"); // miss
        assertEquals(0.5, cache.getHitRate(), 0.01);
    }

    @Test
    public void testCacheManagerSummary() {
        CacheManager<String, String> cache = new CacheManager<>();
        cache.put("a", "1");
        cache.get("a");
        String summary = cache.getSummary();
        assertNotNull(summary);
        assertTrue(summary.contains("CacheManager"));
    }

    // ------------------------------------------------------------------
    // Logger
    // ------------------------------------------------------------------

    @Test
    public void testLoggerCreate() {
        Logger log = new Logger();
        assertNotNull(log);
    }

    @Test
    public void testLoggerLevels() {
        Logger log = new Logger();
        // These should not throw
        log.info("info message");
        log.warn("warn message");
        log.error("error message");
        log.debug("debug message");
        log.log(Logger.INFO, "direct info");
        log.log(Logger.ERROR, "direct error" + "\n" + new RuntimeException("test").toString());
        log.close();
    }

    @Test
    public void testLoggerWithFile() throws IOException {
        File logFile = new File(tempDir, "test.log");
        Logger log = new Logger(Logger.DEBUG, logFile.getAbsolutePath());
        log.info("test info");
        log.warn("test warn");
        log.close();

        assertTrue(logFile.exists());
        byte[] logBytes = readAllBytes(new FileInputStream(logFile));
        String content = new String(logBytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("test info"));
        assertTrue(content.contains("test warn"));
    }

    @Test
    public void testLoggerLevelFiltering() throws IOException {
        File logFile = new File(tempDir, "filter-test.log");
        // Only log ERROR and above
        Logger log = new Logger(Logger.ERROR, logFile.getAbsolutePath());
        log.info("should not appear");
        log.warn("should not appear");
        log.error("should appear");
        log.close();

        byte[] logBytes = readAllBytes(new FileInputStream(logFile));
        String content = new String(logBytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("should appear"));
        assertFalse(content.contains("should not appear"));
    }

    @Test
    public void testLoggerNoFile() {
        Logger log = new Logger(Logger.INFO, null);
        log.info("no file test");
        log.close();
    }

    // ------------------------------------------------------------------
    // CrashReporter
    // ------------------------------------------------------------------

    @Test
    public void testCrashReporterCreate() {
        CrashReporter reporter = new CrashReporter();
        assertFalse(reporter.isInstalled());
        assertEquals(CrashReporter.DEFAULT_CRASH_LOG, reporter.getCrashLogPath());
    }

    @Test
    public void testCrashReporterCustomPath() {
        CrashReporter reporter = new CrashReporter("custom_crash.log");
        assertEquals("custom_crash.log", reporter.getCrashLogPath());
    }

    @Test
    public void testCrashReporterReportCrash() {
        File crashLogFile = new File(tempDir, "test-crash.log");
        CrashReporter reporter = new CrashReporter(crashLogFile.getAbsolutePath(),
                logger, new RuntimeEnvironment());
        reporter.reportCrash(new RuntimeException("test crash"));
        assertTrue(crashLogFile.exists());
    }

    @Test
    public void testCrashReporterReportNull() {
        CrashReporter reporter = new CrashReporter();
        reporter.reportCrash((Throwable) null); // should not throw
    }

    @Test
    public void testCrashReporterInstall() {
        CrashReporter reporter = new CrashReporter();
        reporter.install();
        assertTrue(reporter.isInstalled());
        // Already installed, should be idempotent
        reporter.install();
        assertTrue(reporter.isInstalled());
    }

    @Test
    public void testCrashReporterReportWithStackTrace() throws IOException {
        File crashLogFile = new File(tempDir, "stacktrace-crash.log");
        CrashReporter reporter = new CrashReporter(crashLogFile.getAbsolutePath(),
                logger, new RuntimeEnvironment());

        RuntimeException exception = new RuntimeException("test crash with stack");
        reporter.reportCrash(Thread.currentThread(), exception);

        assertTrue(crashLogFile.exists());
        byte[] crashBytes = readAllBytes(new FileInputStream(crashLogFile));
        String content = new String(crashBytes, StandardCharsets.UTF_8);
        assertTrue(content.contains("test crash with stack"));
        assertTrue(content.contains("Stack Trace"));
        assertTrue(content.contains("System Information"));
    }

    // ------------------------------------------------------------------
    // VersionManager
    // ------------------------------------------------------------------

    @Test
    public void testVersionManagerCreate() {
        VersionManager v = new VersionManager("Test App", 1, 2, 3);
        assertEquals(1, v.getMajor());
        assertEquals(2, v.getMinor());
        assertEquals(3, v.getPatch());
        assertEquals("Test App", v.getAppName());
        assertEquals("1.2.3", v.getVersionString());
        assertEquals("1.2", v.getShortVersion());
        assertTrue(v.getFullVersionString().contains("Test App"));
    }

    @Test
    public void testVersionManagerWithBuildDate() {
        VersionManager v = new VersionManager("App", 1, 0, 0, "2026-08-10");
        assertTrue(v.getFullVersionString().contains("2026-08-10"));
    }

    @Test
    public void testVersionManagerParse() {
        VersionManager v = VersionManager.parse("2.3.4");
        assertEquals(2, v.getMajor());
        assertEquals(3, v.getMinor());
        assertEquals(4, v.getPatch());
    }

    @Test
    public void testVersionManagerParseTwoParts() {
        VersionManager v = VersionManager.parse("1.5");
        assertEquals(1, v.getMajor());
        assertEquals(5, v.getMinor());
        assertEquals(0, v.getPatch());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerParseNull() {
        VersionManager.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerParseEmpty() {
        VersionManager.parse("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerParseInvalid() {
        VersionManager.parse("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerNullAppName() {
        new VersionManager(null, 1, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerEmptyAppName() {
        new VersionManager("", 1, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testVersionManagerNegativeParts() {
        new VersionManager("App", -1, 0, 0);
    }

    @Test
    public void testVersionManagerComparison() {
        VersionManager v1 = new VersionManager("App", 1, 0, 0);
        VersionManager v2 = new VersionManager("App", 1, 0, 1);
        VersionManager v3 = new VersionManager("App", 2, 0, 0);

        assertTrue(v1.isOlderThan(v2));
        assertTrue(v2.isNewerThan(v1));
        assertTrue(v3.isNewerThan(v1));
        assertTrue(v1.isBreakingChange(v3));
        assertFalse(v1.isBreakingChange(v2));
        assertFalse(v2.isBreakingChange(v1));
    }

    @Test
    public void testVersionManagerEquals() {
        VersionManager v1 = new VersionManager("App", 1, 0, 0);
        VersionManager v2 = new VersionManager("Other", 1, 0, 0);
        assertTrue(v1.equals(v2)); // same version, different name
        assertFalse(v1.equals(null));
    }

    @Test
    public void testVersionManagerHashCode() {
        VersionManager v1 = new VersionManager("App", 1, 0, 0);
        VersionManager v2 = new VersionManager("App", 1, 0, 0);
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    // ------------------------------------------------------------------
    // ApplicationLauncher
    // ------------------------------------------------------------------

    @Test
    public void testApplicationLauncherCreate() {
        ApplicationLauncher launcher = ApplicationLauncher.create();
        assertNotNull(launcher);
        assertNotNull(launcher.getVersion());
        assertNotNull(launcher.getEnvironment());
        assertNotNull(launcher.getSystemCheck());
        assertNotNull(launcher.getConfig());
        assertNotNull(launcher.getResourceManager());
        assertNotNull(launcher.getAssetLoader());
        assertNotNull(launcher.getObjectCache());
        assertNotNull(launcher.getScene());
        assertNotNull(launcher.getToolManager());
        assertNotNull(launcher.getWindow());
        assertNotNull(launcher.getLogger());
        assertNotNull(launcher.getCrashReporter());
    }

    @Test
    public void testApplicationLauncherWithConfig() {
        AppConfig config = new AppConfig(1920, 1080);
        config.setRenderQuality("HIGH");
        ApplicationLauncher launcher = ApplicationLauncher.create(config);
        assertEquals(1920, launcher.getConfig().getWindowWidth());
        assertEquals(1080, launcher.getConfig().getWindowHeight());
        assertEquals("HIGH", launcher.getConfig().getRenderQuality());
    }

    @Test
    public void testApplicationLauncherLifecycle() {
        ApplicationLauncher launcher = ApplicationLauncher.create();
        assertTrue(launcher.isRunning());
        launcher.start();
        assertTrue(launcher.isRunning());
        launcher.update(); // should not throw
        launcher.render(); // should not throw
        launcher.stop();
        assertFalse(launcher.isRunning());
    }

    @Test
    public void testApplicationLauncherVersion() {
        assertEquals("Geometry Teaching Engine", ApplicationLauncher.VERSION.getAppName());
        assertEquals(1, ApplicationLauncher.VERSION.getMajor());
        assertEquals(0, ApplicationLauncher.VERSION.getMinor());
        assertEquals(0, ApplicationLauncher.VERSION.getPatch());
    }

    @Test
    public void testApplicationLauncherScene() {
        ApplicationLauncher launcher = ApplicationLauncher.create();
        assertNotNull(launcher.getScene());
        assertEquals(0, launcher.getScene().getObjectCount());
    }

    @Test
    public void testApplicationLauncherStopMultipleTimes() {
        ApplicationLauncher launcher = ApplicationLauncher.create();
        launcher.stop();
        launcher.stop(); // should not throw
    }

    // ------------------------------------------------------------------
    // Main class
    // ------------------------------------------------------------------

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
}
