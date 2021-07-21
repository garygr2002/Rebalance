package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class PreferenceManager {

    // A single instance of the preference manager
    private static final PreferenceManager instance = new PreferenceManager();

    // A map of integer values to the levels with which they correspond
    private final Map<Integer, Level> levelMap = new HashMap<>();

    // A preferences object for this manager
    private final Preferences preferences =
            Preferences.userRoot().node(
                    PreferenceManager.class.getName());

    /**
     * Constructs a preference manager instance.
     */
    private PreferenceManager() {

        // Load up the level map.
        put(Level.ALL);
        put(Level.CONFIG);
        put(Level.FINE);
        put(Level.FINER);
        put(Level.FINEST);
        put(Level.INFO);
        put(Level.OFF);
        put(Level.SEVERE);
        put(Level.WARNING);
    }

    /**
     * Gets the default current value of the S&P 500.
     *
     * @return The default current value of the S&P 500.
     */
    public static double getCurrentDefault() {
        return 0.;
    }

    /**
     * Gets the default destination path for data file backup.
     *
     * @return The default destination path for data file backup
     */
    public static @NotNull String getDestinationNameDefault() {
        return "backup";
    }

    /**
     * Gets the default high value of the S&P 500.
     *
     * @return The default high value of the S&P 500.
     */
    public static double getHighDefault() {
        return 0.;
    }

    /**
     * Gets the default expected annual inflation rate.
     *
     * @return The default expected annual inflation rate
     */
    public static double getInflationDefault() {
        return 0.;
    }

    /**
     * Gets a preference manager instance.
     *
     * @return A preference manager instance
     */
    public static @NotNull PreferenceManager getInstance() {
        return instance;
    }

    /**
     * Gets the default desired logging level.
     *
     * @return The default desired logging level
     */
    public static @NotNull Level getLevelDefault() {
        return Level.INFO;
    }

    /**
     * Gets the default path for the data files.
     *
     * @return The default path for the data files
     */
    public static @NotNull String getPathNameDefault() {
        return "data";
    }

    /**
     * Gets the level associated with a value.
     *
     * @param value The level associated with the given value
     * @return The level associated with the given value, or null if there is
     * no such associated level
     */
    public Level get(int value) {
        return levelMap.get(value);
    }

    /**
     * Gets the current value of the S&P 500.
     *
     * @return The current value of the S&P 500.
     */
    public double getCurrent() {
        return getDouble(CommandLineId.CURRENT, getCurrentDefault());
    }

    /**
     * Gets the destination path for data file backup.
     *
     * @return The destination path for data file backup
     */
    public @NotNull Path getDestination() {
        return getPath(CommandLineId.DESTINATION, getDestinationNameDefault());
    }

    /**
     * Gets a double value for a preference ID.
     *
     * @param id           The ID for which to get a double preference
     * @param defaultValue The default value to use in the event the
     *                     preference is not set
     * @return The double value for the given preference ID
     */
    private double getDouble(@NotNull CommandLineId id, double defaultValue) {
        return preferences.getDouble(id.name(), defaultValue);
    }

    /**
     * Gets the high value of the S&P 500.
     *
     * @return The high value of the S&P 500.
     */
    public double getHigh() {
        return getDouble(CommandLineId.HIGH, getHighDefault());
    }

    /**
     * Gets the expected annual inflation rate.
     *
     * @return The expected annual inflation rate
     */
    public double getInflation() {
        return getDouble(CommandLineId.INFLATION, getInflationDefault());
    }

    /**
     * Gets an integer value for a preference ID.
     *
     * @param id           The ID for which to get an integer preference
     * @param defaultValue The default value to use in the event the
     *                     preference is not set
     * @return The integer value for the given preference ID
     */
    @SuppressWarnings("SameParameterValue")
    private int getInt(@NotNull CommandLineId id, int defaultValue) {
        return preferences.getInt(id.name(), defaultValue);
    }

    /**
     * Gets the desired logging level.
     *
     * @return The desired logging level
     */
    public @NotNull Level getLevel() {

        /*
         * Declare and initialize the default logging level. Get the preference
         * for logging level as an integer. Return the default value if there
         * is no logging level for the set integer preference; otherwise return
         * the logging level corresponding to the preference.
         */
        final Level defaultValue = getLevelDefault();
        final Level result = get(getInt(CommandLineId.LEVEL,
                defaultValue.intValue()));
        return (null == result) ? defaultValue : result;
    }

    /**
     * Gets the path for the data files.
     *
     * @return The path for the data files
     */
    public @NotNull Path getPath() {
        return getPath(CommandLineId.PATH, getPathNameDefault());
    }

    /**
     * Gets a path for a preference ID.
     *
     * @param id           The ID for which to get a path preference
     * @param defaultValue The default path name value to use in the event the
     *                     preference is not set
     * @return The path value for the given preference ID
     */
    private @NotNull Path getPath(@NotNull CommandLineId id,
                                  @NotNull String defaultValue) {
        return Paths.get(preferences.get(id.name(), defaultValue));
    }

    /**
     * Gets the preferences object used by the preference manager.
     *
     * @return The preferences object used by the preference manager
     */
    public @NotNull Preferences getPreferences() {
        return preferences;
    }

    /**
     * Puts a level in the level map.
     *
     * @param level The level to put in the level map
     * @return Any level previously in the map using the same integer value
     */
    @SuppressWarnings("UnusedReturnValue")
    private Level put(@NotNull Level level) {
        return levelMap.put(level.intValue(), level);
    }
}
