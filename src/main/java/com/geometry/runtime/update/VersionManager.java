package com.geometry.runtime.update;

/**
 * Phase 12 - Semantic version manager.
 *
 * Manages application version information in the format:
 *   MAJOR.MINOR.PATCH
 *
 * Examples:
 *   1.0.0  - Initial release
 *   1.1.0  - Feature release (backward compatible)
 *   1.1.1  - Bug fix (backward compatible)
 *   2.0.0  - Breaking change
 *
 * @author Geometry Teaching Engine
 * @version 1.0.0
 */
public class VersionManager {

    /** The current application version. */
    private final int major;
    private final int minor;
    private final int patch;

    /** The application name. */
    private final String appName;

    /** Build date (optional). */
    private final String buildDate;

    /**
     * Create a VersionManager with the given version.
     *
     * @param appName   application name
     * @param major     major version
     * @param minor     minor version
     * @param patch     patch version
     */
    public VersionManager(String appName, int major, int minor, int patch) {
        this(appName, major, minor, patch, null);
    }

    /**
     * Create a VersionManager with the given version and build date.
     */
    public VersionManager(String appName, int major, int minor, int patch, String buildDate) {
        if (appName == null || appName.isEmpty()) {
            throw new IllegalArgumentException("appName cannot be null or empty");
        }
        if (major < 0 || minor < 0 || patch < 0) {
            throw new IllegalArgumentException("Version numbers must be non-negative");
        }
        this.appName = appName;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.buildDate = buildDate;
    }

    /**
     * Parse a version string in "MAJOR.MINOR.PATCH" format.
     *
     * @param versionString the version string
     * @return a VersionManager
     * @throws IllegalArgumentException if the format is invalid
     */
    public static VersionManager parse(String versionString) {
        if (versionString == null || versionString.isEmpty()) {
            throw new IllegalArgumentException("Version string cannot be null or empty");
        }
        String[] parts = versionString.split("\\.", 3);
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Invalid version format: " + versionString + " (expected MAJOR.MINOR[.PATCH])");
        }
        try {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return new VersionManager("App", major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid version format: " + versionString, e);
        }
    }

    // ------------------------------------------------------------------
    // Version comparison
    // ------------------------------------------------------------------

    /**
     * Compare this version to another version.
     *
     * @param other the other version
     * @return negative if this < other, zero if equal, positive if this > other
     */
    public int compareTo(VersionManager other) {
        if (other == null) {
            return 1;
        }
        if (this.major != other.major) {
            return this.major - other.major;
        }
        if (this.minor != other.minor) {
            return this.minor - other.minor;
        }
        return this.patch - other.patch;
    }

    /**
     * Check if this version is equal to another.
     */
    public boolean equals(VersionManager other) {
        return compareTo(other) == 0;
    }

    /**
     * Check if this version is older than another.
     */
    public boolean isOlderThan(VersionManager other) {
        return compareTo(other) < 0;
    }

    /**
     * Check if this version is newer than another.
     */
    public boolean isNewerThan(VersionManager other) {
        return compareTo(other) > 0;
    }

    /**
     * Check if this is a breaking (major) version change.
     */
    public boolean isBreakingChange(VersionManager other) {
        return this.major != other.major;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getAppName() {
        return appName;
    }

    public String getBuildDate() {
        return buildDate;
    }

    /**
     * Get the version string (e.g. "1.2.3").
     */
    public String getVersionString() {
        return major + "." + minor + "." + patch;
    }

    /**
     * Get the full application name with version.
     */
    public String getFullVersionString() {
        StringBuilder sb = new StringBuilder();
        sb.append(appName).append(" v").append(getVersionString());
        if (buildDate != null && !buildDate.isEmpty()) {
            sb.append(" (").append(buildDate).append(")");
        }
        return sb.toString();
    }

    /**
     * Get a short version identifier.
     */
    public String getShortVersion() {
        return major + "." + minor;
    }

    @Override
    public String toString() {
        return getFullVersionString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VersionManager other = (VersionManager) obj;
        return major == other.major && minor == other.minor && patch == other.patch;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * major + minor) + patch;
    }
}
