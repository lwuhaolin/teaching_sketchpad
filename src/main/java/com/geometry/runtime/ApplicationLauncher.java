package com.geometry.runtime;

import com.geometry.animation.AnimationManager;
import com.geometry.runtime.config.AppConfig;
import com.geometry.runtime.config.ConfigLoader;
import com.geometry.runtime.logging.CrashReporter;
import com.geometry.runtime.logging.Logger;
import com.geometry.runtime.resource.AssetLoader;
import com.geometry.runtime.resource.CacheManager;
import com.geometry.runtime.resource.ResourceManager;
import com.geometry.runtime.update.VersionManager;
import com.geometry.scene.Scene;
import com.geometry.tools.ToolManager;
import com.geometry.ui.ApplicationWindow;
import com.geometry.interaction.InteractionManager;
import com.geometry.renderer.Renderer;
import com.geometry.teaching.TeachingManager;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Phase 12 - Application launcher.
 *
 * The single entry point for starting the Geometry Teaching Engine.
 *
 * Startup sequence:
 *   1. Initialize logging
 *   2. Detect runtime environment
 *   3. Run system checks
 *   4. Load application configuration
 *   5. Initialize resource managers
 *   6. Create core engine components (Scene, ToolManager, etc.)
 *   7. Create UI window
 *   8. Install crash reporter
 *
 * After launch, the returned ApplicationLauncher provides access
 * to all engine components for the main render loop.
 *
 * Usage:
 *   ApplicationLauncher launcher = ApplicationLauncher.launch();
 *   launcher.getWindow().show();
 *   while (launcher.isRunning()) {
 *       launcher.update();
 *       launcher.render();
 *   }
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class ApplicationLauncher {

    /** Application version. */
    public static final VersionManager VERSION =
            new VersionManager("Geometry Teaching Engine", 1, 0, 0, "2026-08-10");

    /** Default config file name. */
    public static final String DEFAULT_CONFIG_FILE = "config.properties";

    /** Default log file name. */
    public static final String DEFAULT_LOG_FILE = "logs/application.log";

    /** Default crash log file name. */
    public static final String DEFAULT_CRASH_LOG = "logs/crash.log";

    // ------------------------------------------------------------------
    // Runtime state
    // ------------------------------------------------------------------

    private final Logger logger;
    private final CrashReporter crashReporter;
    private final RuntimeEnvironment environment;
    private final SystemCheckReport systemCheck;
    private final AppConfig config;
    private final ResourceManager resourceManager;
    private final AssetLoader assetLoader;
    private final CacheManager<String, Object> objectCache;

    // ------------------------------------------------------------------
    // Engine components
    // ------------------------------------------------------------------

    private final Scene scene;
    private final ToolManager toolManager;
    private final ApplicationWindow window;
    private boolean running;

    // ------------------------------------------------------------------
    // Construction
    // ------------------------------------------------------------------

    /**
     * Create a launcher with the given config.
     *
     * @param config the application configuration
     */
    public ApplicationLauncher(AppConfig config) {
        this.config = config != null ? config : new AppConfig();
        this.logger = new Logger(Logger.INFO, DEFAULT_LOG_FILE);
        this.crashReporter = new CrashReporter(DEFAULT_CRASH_LOG);
        this.environment = new RuntimeEnvironment();
        this.systemCheck = checkSystem();
        this.resourceManager = new ResourceManager();
        this.assetLoader = new AssetLoader(resourceManager);
        this.objectCache = new CacheManager<>(64);

        // Check system compatibility
        if (!systemCheck.isPassed()) {
            logger.warn("System checks had errors:\n" + systemCheck.toSummary());
        }

        // Install crash reporter
        crashReporter.install();

        // Initialize engine components
        this.scene = new Scene();
        this.toolManager = initializeToolManager();
        this.window = createApplicationWindow();
        this.running = true;

        logger.info(VERSION.getFullVersionString());
        logger.info("System check: " + (systemCheck.isPassed() ? "PASSED" : "FAILED WITH WARNINGS"));
        logger.info("Environment:\n" + environment.toSummary());
        logger.info("Config:\n" + config.toSummary());
    }

    /**
     * Create and return a new ApplicationLauncher with default config.
     */
    public static ApplicationLauncher create() {
        ConfigLoader configLoader = new ConfigLoader();
        AppConfig config = configLoader.load(DEFAULT_CONFIG_FILE);
        return new ApplicationLauncher(config);
    }

    /**
     * Create and return a new ApplicationLauncher with config from file.
     */
    public static ApplicationLauncher create(String configFile) {
        ConfigLoader configLoader = new ConfigLoader();
        AppConfig config = configLoader.load(configFile);
        return new ApplicationLauncher(config);
    }

    /**
     * Create and return a new ApplicationLauncher with explicit config.
     */
    public static ApplicationLauncher create(AppConfig config) {
        return new ApplicationLauncher(config);
    }

    // ------------------------------------------------------------------
    // System checks
    // ------------------------------------------------------------------

    private SystemCheckReport checkSystem() {
        SystemChecker checker = new SystemChecker();
        List<File> checkDirs = Arrays.asList(
                new File("."),
                new File("config"),
                new File("logs"),
                new File("resources")
        );
        return checker.check(environment, checkDirs);
    }

    // ------------------------------------------------------------------
    // Engine initialization
    // ------------------------------------------------------------------

    private ToolManager initializeToolManager() {
        ToolManager tm = new ToolManager();
        // Tool initialization will be done via the UI event bridge
        // or explicit tool registration by the caller
        return tm;
    }

    private ApplicationWindow createApplicationWindow() {
        InteractionManager interactionManager = new InteractionManager(scene);
        TeachingManager teachingManager = new TeachingManager(scene, (Renderer) null);

        return new ApplicationWindow(
                scene, toolManager, interactionManager,
                teachingManager, new AnimationManager());
    }

    // ------------------------------------------------------------------
    // Lifecycle
    // ------------------------------------------------------------------

    /**
     * Start the application.
     */
    public void start() {
        logger.info("Application starting...");
        window.create();
        window.show();
        running = true;
    }

    /**
     * Stop the application.
     */
    public void stop() {
        logger.info("Application stopping...");
        running = false;
        window.close();
        logger.close();
    }

    /**
     * Check if the application is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Update the application state (called each frame).
     */
    public void update() {
        if (!running) {
            return;
        }
        window.getWorkspace().dispatchEvents();
    }

    /**
     * Render the current frame.
     */
    public void render() {
        // Rendering is handled by the OpenGLRenderer
        // This method is a placeholder for the main loop
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public Logger getLogger() {
        return logger;
    }

    public CrashReporter getCrashReporter() {
        return crashReporter;
    }

    public RuntimeEnvironment getEnvironment() {
        return environment;
    }

    public SystemCheckReport getSystemCheck() {
        return systemCheck;
    }

    public AppConfig getConfig() {
        return config;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public AssetLoader getAssetLoader() {
        return assetLoader;
    }

    public CacheManager<String, Object> getObjectCache() {
        return objectCache;
    }

    public Scene getScene() {
        return scene;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }

    public ApplicationWindow getWindow() {
        return window;
    }

    public VersionManager getVersion() {
        return VERSION;
    }
}
