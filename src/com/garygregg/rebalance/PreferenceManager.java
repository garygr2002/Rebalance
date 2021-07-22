package com.garygregg.rebalance;

import com.garygregg.rebalance.cla.LevelPreferenceDispatch;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class PreferenceManager {

    // A single instance of the preference manager
    private static final PreferenceManager instance = new PreferenceManager();

    // A preferences object for this manager
    private final Preferences preferences =
            Preferences.userRoot().node(
                    PreferenceManager.class.getName());

    /**
     * Gets the default double.
     *
     * @return The default double
     */
    private static double getDefaultDouble() {
        return Double.MIN_VALUE;
    }

    /**
     * Gets the default integer.
     *
     * @return The default integer
     */
    private static int getDefaultInt() {
        return Integer.MIN_VALUE;
    }

    /**
     * Gets the default path name.
     *
     * @return The default path name
     */
    private static String getDefaultPathName() {
        return "";
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
     * Gets the current value of the S&P 500.
     *
     * @return The current value of the S&P 500
     */
    public Double getCurrent() {
        return getDouble(CommandLineId.CURRENT);
    }

    /**
     * Gets the destination for data directory backup.
     *
     * @return The destination path for data directory backup
     */
    public Path getDestination() {
        return getSource(CommandLineId.DESTINATION);
    }

    /**
     * Gets a double value for a preference ID.
     *
     * @param id The ID for which to get a double preference
     * @return The double value for the given preference ID
     */
    private Double getDouble(@NotNull CommandLineId id) {

        /*
         * Get the default double. Get a preference for name of the command
         * line ID using the default. Return null if the default was returned,
         * otherwise return the result.
         */
        final double defaultDouble = getDefaultDouble();
        final double result = preferences.getDouble(id.name(), defaultDouble);
        return (0 == Double.compare(result, defaultDouble)) ? null : result;
    }

    /**
     * Gets the high value of the S&P 500.
     *
     * @return The high value of the S&P 500
     */
    public Double getHigh() {
        return getDouble(CommandLineId.HIGH);
    }

    /**
     * Gets the expected annual inflation rate.
     *
     * @return The expected annual inflation rate
     */
    public Double getInflation() {
        return getDouble(CommandLineId.INFLATION);
    }

    /**
     * Gets an integer value for a preference ID.
     *
     * @param id The ID for which to get an integer preference
     * @return The integer value for the given preference ID
     */
    @SuppressWarnings("SameParameterValue")
    private Integer getInt(@NotNull CommandLineId id) {

        /*
         * Get the default integer. Get a preference for name of the command
         * line ID using the default. Return null if the default was returned,
         * otherwise return the result.
         */
        final int defaultInt = getDefaultInt();
        final int result = preferences.getInt(id.name(), defaultInt);
        return (result == defaultInt) ? null : result;
    }

    /**
     * Gets the desired logging level.
     *
     * @return The desired logging level
     */
    public @NotNull Level getLevel() {

        /*
         * Do not let the returned level be null. If the currently set
         * preference is not representable as a level, then return a non-null
         * default.
         */
        final Level level = LevelPreferenceDispatch.getLevel(
                getInt(CommandLineId.LEVEL));
        return (null == level) ? Level.ALL : level;
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
     * Gets a path for a preference ID.
     *
     * @param id The ID for which to get a path preference
     * @return The path value for the given preference ID
     */
    private Path getSource(@NotNull CommandLineId id) {

        /*
         * Get the default path name. Get a preference for name of the command
         * line ID using the default. Return null if the default was returned,
         * otherwise return the result as a path.
         */
        final String defaultPathName = getDefaultPathName();
        final String result = preferences.get(id.name(), defaultPathName);
        return (result.equals(defaultPathName)) ? null : Paths.get(result);
    }

    /**
     * Gets the source data directory.
     *
     * @return The source data directory
     */
    public Path getSource() {
        return getSource(CommandLineId.SOURCE);
    }

    /**
     * Sets the current value of the S&P 500.
     *
     * @param value The current value of the S&P 500
     */
    public void setCurrent(Double value) {
        setDouble(CommandLineId.CURRENT, value);
    }

    /**
     * Sets the destination for data directory backup.
     *
     * @param value The destination path for data directory backup
     */
    public void setDestination(Path value) {
        setPath(CommandLineId.DESTINATION, value);
    }

    /**
     * Sets a double value for a preference ID.
     *
     * @param id    The ID for which to set a double preference
     * @param value The double value for the given preference ID
     */
    private void setDouble(@NotNull CommandLineId id, Double value) {
        getPreferences().putDouble(id.name(), (null == value) ?
                getDefaultDouble() : value);
    }

    /**
     * Gets the high value of the S&P 500.
     *
     * @param value The high value of the S&P 500
     */
    public void setHigh(Double value) {
        setDouble(CommandLineId.HIGH, value);
    }

    /**
     * Gets the expected annual inflation rate.
     *
     * @param value The expected annual inflation rate
     */
    public void setInflation(Double value) {
        setDouble(CommandLineId.INFLATION, value);
    }

    /**
     * Sets the desired logging level.
     *
     * @param value The desired logging level
     */
    public void setLevel(Level value) {
        getPreferences().putInt(CommandLineId.LEVEL.name(), (null == value) ?
                getDefaultInt() : value.intValue());
    }

    /**
     * Sets a path name value for a preference ID.
     *
     * @param id    The ID for which to set a path name preference
     * @param value The string value for the given preference ID
     */
    private void setPath(@NotNull CommandLineId id, Path value) {
        getPreferences().put(id.name(),
                (null == value) ? getDefaultPathName() : value.toString());
    }

    /**
     * Sets the source data directory.
     *
     * @param value The source data directory
     */
    public void setSource(Path value) {
        setPath(CommandLineId.SOURCE, value);
    }
}
