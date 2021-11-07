package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageLogger {

    // The error stream we will use
    private static final PrintStream errorStream = System.err;

    // The logging level for extraordinary information
    private static final Level extraordinary;

    // The logging level for ordinary information
    private static final Level ordinary;

    // The output stream we will use
    private static final PrintStream outputStream = System.out;

    static {

        /*
         * Get the preference manager, and use it to set the logging levels for
         * extraordinary and ordinary information.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        extraordinary = manager.getExtraordinary();
        ordinary = manager.getOrdinary();
    }

    // The default logger in case the current logger is null.
    private final Logger defaultLogger;

    // The level threshold monitors
    private final Pair<ThresholdMonitor, ThresholdMonitor> monitors =
            new Pair<>(new ThresholdMonitor(), new ThresholdMonitor());

    // The current logger is null.
    private Logger logger;

    /**
     * Constructs the message logger.
     */
    public MessageLogger() {

        /*
         * Set the default logger followed by the current logger.
         */
        defaultLogger =
                Logger.getLogger(MessageLogger.class.getCanonicalName());

        /*
         * Set the current logger. Note: Setting the current logger to null
         * will automatically use the default as current. Initialize the
         * threshold monitors.
         */
        setLogger(null);
        resetProblem1();
        resetProblem2();
    }

    /**
     * Gets the error stream we will use.
     *
     * @return The error stream we will use
     */
    private static @NotNull PrintStream getErrorStream() {
        return errorStream;
    }

    /**
     * Gets the logging level for extraordinary information.
     *
     * @return The logging level for extraordinary information
     */
    public static @NotNull Level getExtraordinary() {
        return extraordinary;
    }

    /**
     * Gets the logging level for ordinary information.
     *
     * @return The logging level for ordinary information
     */
    public static @NotNull Level getOrdinary() {
        return ordinary;
    }

    /**
     * Gets the output stream we will use.
     *
     * @return The output stream we will use
     */
    private static @NotNull PrintStream getOutputStream() {
        return outputStream;
    }

    /**
     * Streams a message.
     *
     * @param level   The level of the message
     * @param message The message to stream
     */
    public static void stream(@NotNull Level level, @NotNull String message) {

        // Identify the proper print stream for the message.
        final PrintStream printStream = (level.intValue() <
                Level.SEVERE.intValue()) ? getOutputStream() :
                getErrorStream();

        // Print the message to the print stream.
        printStream.println(message);
    }

    /**
     * Gets the current logger.
     *
     * @return The current logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns whether there was a problem since the last problem reset.
     *
     * @return True if there was a problem since the last problem reset, false
     * otherwise
     */
    public boolean hadProblem1() {
        return monitors.getFirst().isThresholdReached();
    }

    /**
     * Returns whether there was a problem since the last problem reset.
     *
     * @return True if there was a problem since the last problem reset, false
     * otherwise
     */
    public boolean hadProblem2() {
        return monitors.getSecond().isThresholdReached();
    }

    /**
     * Logs a message.
     *
     * @param level   The level for the message
     * @param message The message to log
     * @return True if the level for this message flags it as a problem
     */
    public boolean log(@NotNull Level level, @NotNull String message) {

        // Log the message, and return the problem flag for this message.
        final ThresholdMonitor first = monitors.getFirst();
        getLogger().log(first.observe(monitors.getSecond().observe(level)),
                message);
        return first.isThresholdReached(level);
    }

    /**
     * Resets all problem flags.
     */
    public void resetProblem() {

        // Reset both the 1st and the 2nd problem flags.
        resetProblem1();
        resetProblem2();
    }

    /**
     * Resets the 1st problem flag.
     */
    public void resetProblem1() {
        monitors.getFirst().reset();
    }

    /**
     * Resets the 2nd problem flag.
     */
    public void resetProblem2() {
        monitors.getSecond().reset();
    }

    /**
     * Sets the current logger.
     *
     * @param logger The new current logger
     */
    public void setLogger(Logger logger) {
        this.logger = (null == logger) ? defaultLogger : logger;
    }

    /**
     * Streams and logs a message.
     *
     * @param level   The level of the message
     * @param message The message to stream and log
     * @return True if the level for this message flags it as a problem
     */
    public boolean streamAndLog(@NotNull Level level,
                                @NotNull String message) {

        // Print the message to the print stream, then log the message.
        stream(level, message);
        return log(level, message);
    }
}
