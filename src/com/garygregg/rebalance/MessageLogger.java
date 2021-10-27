package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageLogger {

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
     * Logs a message with the current logger.
     *
     * @param level   The level for the message
     * @param message The message to log
     * @return True if the level for this message flags it as a problem
     */
    public boolean logMessage(@NotNull Level level,
                              @NotNull String message) {

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
}
