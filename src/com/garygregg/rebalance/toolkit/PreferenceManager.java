package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.cla.LevelPreferenceDispatch;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    // The ratio of the S&P 500 last close divided by S&P 500 high
    private double changeLastClose = calculateChange(getClose(), getHigh());

    // The percent change from the S&P 500 high to the S&P 500 last close
    private double percentLastClose = calculatePercent(changeLastClose);

    // The ratio of the S&P 500 today divided by S&P 500 last close
    private double changeToday = calculateChange(getToday(), getClose());

    // The percent change from the S&P 500 last close to the S&P 500 today
    private double percentToday = calculatePercent(changeToday);

    /**
     * Calculates a ratio.
     *
     * @param numerator   The numerator of the ratio
     * @param denominator The denominator of the ratio
     * @return The ratio of the numerator divided the denominator, or a default
     * if either the numerator or denominator are null
     */
    @Contract(pure = true)
    private static @NotNull Double calculateChange(Double numerator,
                                                   Double denominator) {
        return ((null == numerator)) || (null == denominator) ?
                1. : numerator / denominator;
    }

    /**
     * Calculates a percent from a ratio.
     *
     * @param ratio The ratio
     * @return A percent calculated from the ratio
     */
    private static double calculatePercent(double ratio) {
        return 1. - ratio;
    }

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
    @Contract(pure = true)
    private static @NotNull String getDefaultPathName() {
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
     * Gets the ratio of the S&P 500 last close divided by the S&P 500 high
     *
     * @return The ratio of the S&P 500 last close divided by the S&P 500 high
     */
    @SuppressWarnings("unused")
    public double getChangeLastClose() {
        return changeLastClose;
    }

    /**
     * Gets the ratio of the S&P 500 today divided by the S&P 500 last close
     *
     * @return The ratio of the S&P 500 today divided by the S&P 500 last close
     */
    @SuppressWarnings("unused")
    public double getChangeToday() {
        return changeToday;
    }

    /**
     * Gets the last close of the S&P 500.
     *
     * @return The last close of the S&P 500
     */
    public Double getClose() {
        return getDouble(CommandLineId.CLOSE);
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
    private @Nullable Double getDouble(@NotNull CommandLineId id) {

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
     * Gets the logging level for extraordinary informational messages.
     *
     * @return The logging level for extraordinary information messages
     */
    public Level getExtraordinary() {
        return getLevel(CommandLineId.EXTRAORDINARY);
    }

    /**
     * Gets the high of the S&P 500.
     *
     * @return The high of the S&P 500
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
    private @Nullable Integer getInt(@NotNull CommandLineId id) {

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
     * Gets a logging level using a given command line ID.
     *
     * @param id The command lind ID
     * @return The logging level identified by the command ID, or a default if
     * the logging level identified by the command ID is not set
     */
    private @NotNull Level getLevel(@NotNull CommandLineId id) {

        /*
         * Do not let the returned level be null. If the currently set
         * preference is not representable as a level, then return a non-null
         * default.
         */
        final Level level = LevelPreferenceDispatch.getLevel(getInt(id));
        return (null == level) ? Level.ALL : level;
    }

    /**
     * Gets the desired logging level.
     *
     * @return The desired logging level
     */
    public @NotNull Level getLevel() {
        return getLevel(CommandLineId.LEVEL);
    }

    /**
     * Gets the limit of allowed receiver delegates
     *
     * @return The limit of allowed receiver delegates
     */
    public @Nullable Integer getLimit() {
        return getInt(CommandLineId.X);
    }

    /**
     * Gets the logging level for ordinary informational messages.
     *
     * @return The logging level for ordinary information messages
     */
    public @NotNull Level getOrdinary() {
        return getLevel(CommandLineId.ORDINARY);
    }

    /**
     * Gets the percent change from the S&P 500 high to the S&P 500 last close
     *
     * @return The percent change from the S&P 500 high to the S&P 500 last
     * close
     */
    public double getPercentLastClose() {
        return percentLastClose;
    }

    /**
     * Gets the percent change from the S&P 500 last close to the S&P 500
     * today.
     *
     * @return The percent change from the S&P 500 last close to the S&P 500
     * today
     */
    public double getPercentToday() {
        return percentToday;
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
    private @Nullable Path getSource(@NotNull CommandLineId id) {

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
     * Gets the S&P 500 today.
     *
     * @return The S&P 500 today
     */
    public Double getToday() {

        /*
         * Get the value of the S&P 500 today. Return the value of the S&P 500
         * last close if the value today is null. Otherwise, return the
         * explicit value of the S&P 500 today.
         */
        final Double today = getDouble(CommandLineId.TODAY);
        return (null == today) ? getClose() : today;
    }

    /**
     * Sets both the ratio of the S&P 500 last close divided by the S&P 500
     * high, and the ratio of the S&P 500 today divided by the S&P 500 last
     * close.
     */
    public void setChangeBoth() {

        /*
         * Clear the value of the S&P 500 today. Set the change last close and
         * the change today.
         */
        setDouble(CommandLineId.TODAY, null);
        setChangeLastClose();
        setChangeToday();
    }

    /**
     * Sets the ratio of the S&P 500 last close divided by the S&P 500 high.
     */
    public void setChangeLastClose() {

        // Calculate the S&P last close change and percentage.
        changeLastClose = calculateChange(getClose(), getHigh());
        percentLastClose = calculatePercent(changeLastClose);
    }

    /**
     * Sets the ratio of the S&P 500 today divided by the S&P 500 last close.
     */
    public void setChangeToday() {

        // Calculate the S&P 500 today change and percentage.
        changeToday = calculateChange(getToday(), getClose());
        percentToday = calculatePercent(changeToday);
    }

    /**
     * Sets the last close of the S&P 500.
     *
     * @param value The last close of the S&P 500
     */
    public void setClose(Double value) {

        /*
         * Set the S&P 500 close. Set both the change last close and the change
         * today.
         */
        setDouble(CommandLineId.CLOSE, value);
        setChangeBoth();
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
     * Sets the logging level for extraordinary informational messages.
     *
     * @param value The logging level for extraordinary informational messages
     */
    public void setExtraordinary(Level value) {
        setLevel(CommandLineId.EXTRAORDINARY, value);
    }

    /**
     * Sets the high of the S&P 500.
     *
     * @param value The high of the S&P 500
     */
    public void setHigh(Double value) {

        // Set the S&P 500 high, then set the change last close.
        setDouble(CommandLineId.HIGH, value);
        setChangeLastClose();
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
     * Sets an integer value for a preference ID.
     *
     * @param id    The ID for which to set an integer preference
     * @param value The double value for the given preference ID
     */
    private void setInt(@NotNull CommandLineId id, Integer value) {
        getPreferences().putInt(id.name(), (null == value) ? getDefaultInt() :
                value);
    }

    /**
     * Sets the desired logging level.
     *
     * @param value The desired logging level
     */
    public void setLevel(Level value) {
        setLevel(CommandLineId.LEVEL, value);
    }

    /**
     * Sets a logging level using a given command line ID.
     *
     * @param id    The command line ID
     * @param value The logging level to set
     */
    private void setLevel(@NotNull CommandLineId id, Level value) {
        setInt(id, (null == value) ? null : value.intValue());
    }

    /**
     * Sets the limit of allowed receiver delegates
     *
     * @param limit The limit of allowed receiver delegates
     */
    public void setLimit(Integer limit) {
        setInt(CommandLineId.X, limit);
    }

    /**
     * Sets the logging level for ordinary informational messages.
     *
     * @param value The logging level for ordinary informational messages
     */
    public void setOrdinary(Level value) {
        setLevel(CommandLineId.ORDINARY, value);
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

    /**
     * Sets the S&P 500 today.
     *
     * @param value The S&P 500 today
     */
    public void setToday(Double value) {

        // Set the S&P 500 today, then set the change today.
        setDouble(CommandLineId.TODAY, value);
        setChangeToday();
    }
}
