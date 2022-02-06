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
    /*
     * A fixer for S&P 500 high less than S&P 500 today when today has been
     * most recently set
     */
    private final ProblemFixer onSetToday = (high, today) ->
            setDouble(CommandLineId.HIGH, today);

    // The ratio of the S&P 500 'today' divided by S&P 500 last close
    private double ratioTodayToClose = calculateRatio(getClose());

    /*
     * The difference between the S&P 500 last close and the S&P 500 today
     * divided by the S&P 500 last close
     */
    private double fractionTodayOfClose = calculateFraction(ratioTodayToClose);

    /*
     * A fixer for S&P 500 high less than S&P 500 today when the high has been
     * most recently set
     */
    private final ProblemFixer onSetHigh = (high, today) -> setClose(high);

    // The ratio of the S&P 500 'today' of the S&P 500 high
    private double ratioTodayToHigh = calculateRatio(getHigh());

    /*
     * The difference between the S&P 500 high and the S&P 500 today divided by
     * the S&P 500 last high
     */
    private double fractionTodayOfHigh = calculateFraction(ratioTodayToHigh);

    /**
     * Calculates a fraction from a ratio.
     *
     * @param ratio The ratio
     * @return A fraction calculated from the ratio
     */
    private static double calculateFraction(double ratio) {
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
     * Calculates a ratio.
     *
     * @param denominator The denominator of the ratio
     * @return The ratio of the S&P 500 today divided the denominator
     */
    private double calculateRatio(Double denominator) {

        // Declare and initialize the result. Is the denominator not null?
        double result = 1.;
        if (denominator != null) {

            /*
             * The denominator is not null. Reinitialize the result to a
             * maximum double if the denominator is zero.
             */
            if (0. == denominator) {
                result = Double.MAX_VALUE;
            }

            // The denominator is neither null nor zero.
            else {

                /*
                 * Get the value for the S&P 500 today. Is the S&P 500 today
                 * not null?
                 */
                final Double today = getToday();
                if (null != today) {

                    /*
                     * The S&P 500 today is not null. Reinitialize the result
                     * to the ratio of the ratio of the S&P 500 today and the
                     * denominator.
                     */
                    result = today / denominator;
                }
            }
        }

        // Return the result.
        return result;
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
     * Gets the difference between the S&P 500 last close and the S&P 500 today
     * divided by the S&P 500 last close.
     *
     * @return The difference between the S&P 500 last close and the S&P 500
     * today divided by the S&P 500 last close
     */
    @SuppressWarnings("unused")
    public double getFractionClose() {
        return fractionTodayOfClose;
    }

    /**
     * Gets the difference between the S&P 500 high and the S&P 500 today
     * divided by the S&P 500 high.
     *
     * @return The difference between the S&P 500 high and the S&P 500 today
     * divided by the S&P 500 high
     */
    public double getFractionHigh() {
        return fractionTodayOfHigh;
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
     * Gets the preferences object used by the preference manager.
     *
     * @return The preferences object used by the preference manager
     */
    public @NotNull Preferences getPreferences() {
        return preferences;
    }

    /**
     * Gets the ratio of the S&P 500 today divided by the S&P 500 last close
     *
     * @return The ratio of the S&P 500 today divided by the S&P 500 last close
     */
    public double getRatioVersusClose() {
        return ratioTodayToClose;
    }

    /**
     * Gets the ratio of the S&P 500 today divided by the S&P 500 high
     *
     * @return The ratio of the S&P 500 today divided by the S&P 500 high
     */
    @SuppressWarnings("unused")
    public double getRatioVersusHigh() {
        return ratioTodayToHigh;
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
        return getDouble(CommandLineId.TODAY);
    }

    /**
     * Recalculates high versus today.
     */
    private void recalculateHighVersusToday() {

        /*
         * Recalculate the S&P 500 high to today ratio, and the S&P 500 high to
         * today fraction.
         */
        ratioTodayToHigh = calculateRatio(getHigh());
        fractionTodayOfHigh = calculateFraction(ratioTodayToHigh);
    }

    /**
     * Sets the last close of the S&P 500.
     *
     * @param value The last close of the S&P 500
     */
    public void setClose(Double value) {

        /*
         * Set the new value for the S&P 500 last close, and signal that the
         * value has changed.
         */
        setDouble(CommandLineId.CLOSE, value);
        signalChangeClose();
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

        /*
         * Set the new value for the S&P 500 high, and signal that the value
         * has changed.
         */
        setDouble(CommandLineId.HIGH, value);
        signalChangeHigh();
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

        /*
         * Set the new value for the S&P 500 today, and signal that the value
         * has changed.
         */
        setDouble(CommandLineId.TODAY, value);
        signalChangeToday();
    }

    /**
     * Signals that the S&P 500 last close has changed.
     */
    public void signalChangeClose() {

        /*
         * Set the S&P 500 today the same as the S&P 500 last close, and signal
         * that the S&P 500 today has changed.
         */
        setDouble(CommandLineId.TODAY, getClose());
        signalChangeToday();
    }

    /**
     * Signals that the S&P 500 high has changed.
     */
    public void signalChangeHigh() {

        /*
         * Test the new S&P 500 high against the S&P 500 today, and take a
         * corrective action if high is less than today. Was the corrective
         * action not required?
         */
        if (!testHighVsToday(onSetHigh)) {

            /*
             * The corrective action was not required. Recalculate the S&P 500
             * high versus today.
             */
            recalculateHighVersusToday();
        }
    }

    /**
     * Signals that the S&P 500 today has changed.
     */
    public void signalChangeToday() {

        /*
         * Test the new S&P 500 today against the S&P 500 high, and take a
         * corrective action if high is less than today. Recalculate the S&P
         * 500 high to last close ratio, and the S&P 500 high to last close
         * fraction.
         */
        testHighVsToday(onSetToday);
        ratioTodayToClose = calculateRatio(getClose());
        fractionTodayOfClose = calculateFraction(ratioTodayToClose);
    }

    /**
     * Tests the S&P 500 high versus the S&P 500 today, and calls a procedure
     * to fix the problem if the high is less than today.
     *
     * @param fixer The fixer to use if there is a problem
     * @return True if a problem was detected and fixed; false otherwise
     */
    private boolean testHighVsToday(@NotNull ProblemFixer fixer) {

        // Get the S&P 500 high and the S&P 500 today.
        final Double high = getHigh();
        final Double today = getToday();

        /*
         * Determine if there is a problem that needs to be fixed. Does a
         * problem need to be fixed?
         */
        final boolean fixed = (!((null == high) || (null == today))) &&
                (high < today);
        if (fixed) {

            /*
             * A problem needs to be fixed. Fix the problem, and recalculate
             * the S&P 500 high versus today.
             */
            fixer.fixIt(high, today);
            recalculateHighVersusToday();
        }

        // Return whether there was a problem detected, and fixed.
        return fixed;
    }

    private interface ProblemFixer {

        /**
         * Fixes a problem if the S&P 500 high is less than the S&P 500 today.
         *
         * @param high  The S&P 500 high
         * @param today The S&P 500 today
         */
        void fixIt(double high, double today);
    }
}
