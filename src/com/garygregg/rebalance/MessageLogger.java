package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageLogger {

    // The default logger in case the current logger is null.
    private final Logger defaultLogger;

    // The current logger is null.
    private Logger logger;

    // True if there was a problem processing lines, false otherwise
    private boolean problem;

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
         * problem flag.
         */
        setLogger(null);
        resetProblem();
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
    public boolean hadProblem() {
        return problem;
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

        /*
         * Determine the problem value for this message, and set the object
         * flag as required.
         */
        final boolean problem = Level.WARNING.intValue() <= level.intValue();
        setProblem(problem);

        // Log the message, and return the problem flag for this message.
        getLogger().log(level, message);
        return problem;
    }

    /**
     * Resets the problem flag.
     */
    public void resetProblem() {
        problem = false;
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
     * Sets the problem flag.
     *
     * @param problem True if a problem was encountered, false otherwise
     */
    private void setProblem(boolean problem) {

        // Keep the problem flag true until it is explicitly reset.
        this.problem |= problem;
    }
}
