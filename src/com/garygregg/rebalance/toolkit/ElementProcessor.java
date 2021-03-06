package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ElementProcessor {

    // The logging level for information
    private static final Level informationLevel =
            MessageLogger.getExtraordinary();

    // Our date utilities object
    private final DateUtilities dateUtilities =
            new DateUtilities(getPrefix(), getFileType());

    // The logger stack
    private final Stack<Logger> loggers = new Stack<>();

    // Our message logger
    private final MessageLogger messageLogger = new MessageLogger();

    {

        /*
         * Set the logger in the message logger. Currently, all subclasses of
         * the ElementProcessor set their loggers in initialization blocks, and
         * never restore an old logger. Considering deleting the 'loggers'
         * stack, above.
         */
        setLogger(Logger.getLogger(ElementProcessor.class.getCanonicalName()));
    }

    /**
     * Gets the logging level for information.
     *
     * @return The logging level for information
     */
    protected static @NotNull Level getInformationLevel() {
        return informationLevel;
    }

    /**
     * Gets the date utilities object.
     *
     * @return The date utilities object
     */
    protected DateUtilities getDateUtilities() {
        return dateUtilities;
    }

    /**
     * Gets the file type.
     *
     * @return The file type
     */
    protected abstract String getFileType();

    /**
     * Gets the prefix for configuration files of a subclass.
     *
     * @return The prefix for configuration files of a subclass
     */
    protected abstract @NotNull String getPrefix();

    /**
     * Returns whether there was a problem processing a file.
     *
     * @return True if there was a problem processing a file, false otherwise
     */
    @SuppressWarnings("unused")
    public boolean hadFileProblem() {
        return messageLogger.hadProblem1();
    }

    /**
     * Returns whether there was a problem processing a line
     *
     * @return True if there was a problem processing a line, false otherwise
     */
    public boolean hadLineProblem() {
        return messageLogger.hadProblem2();
    }

    /**
     * Logs a message with a logger from the top of the logger stack.
     *
     * @param level   The level for the message
     * @param message The message to log
     * @return True if the level for this message flags it as a problem
     */
    @SuppressWarnings("UnusedReturnValue")
    protected boolean logMessage(@NotNull Level level,
                                 @NotNull String message) {
        return messageLogger.log(level, message);
    }

    /**
     * Resets a file problem.
     */
    protected void resetFileProblem() {
        messageLogger.resetProblem1();
    }

    /**
     * Resets a line problem.
     */
    protected void resetLineProblem() {
        messageLogger.resetProblem2();
    }

    /**
     * Restores the previous class/subclass logger.
     */
    @SuppressWarnings({"UnusedReturnValue", "unused"})
    protected Logger restoreLogger() {

        /*
         * Pop a logger if the logger stack is not null. Was the logger stack
         * not null?
         */
        final Logger logger = loggers.isEmpty() ? null : loggers.pop();
        if (null != logger) {

            // The logger stack was not null. Set the popped logger.
            setLogger(logger);
        }

        // Return the popped logger, if any.
        return logger;
    }

    /**
     * Sets the specific class/subclass logger.
     *
     * @param logger The specific class/subclass logger
     */
    protected void setLogger(@NotNull Logger logger) {

        // Is there an existing logger in the message logger?
        final Logger existing = messageLogger.getLogger();
        if (null != existing) {

            /*
             * There is an existing logger in the message logger. Push it onto
             * the loggers stack.
             */
            loggers.push(existing);
        }

        // Set the logger in the message logger.
        messageLogger.setLogger(logger);
    }
}
