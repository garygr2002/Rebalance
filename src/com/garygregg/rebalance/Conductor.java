package com.garygregg.rebalance;

import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.account.AccountsBuilder;
import com.garygregg.rebalance.code.CodeLibrary;
import com.garygregg.rebalance.code.CodesBuilder;
import com.garygregg.rebalance.detailed.DetailedLibrary;
import com.garygregg.rebalance.detailed.DetailedsBuilder;
import com.garygregg.rebalance.distinguished.DistinguishedAccountLibrary;
import com.garygregg.rebalance.distinguished.DistinguishedsBuilder;
import com.garygregg.rebalance.hierarchy.Hierarchy;
import com.garygregg.rebalance.holding.HoldingLibrary;
import com.garygregg.rebalance.holding.HoldingsBuilder;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.portfolio.PortfoliosBuilder;
import com.garygregg.rebalance.report.ReportsBuilder;
import com.garygregg.rebalance.ticker.TickerLibrary;
import com.garygregg.rebalance.ticker.TickersBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.*;
import java.util.prefs.Preferences;

public class Conductor {

    // The format for reporting the date of a library
    private final static String dateMessageFormat =
            "The date of the %s library/libraries is/are: %s.";

    // The preferences for this class
    private static final Preferences preferences =
            Preferences.userRoot().node(Conductor.class.getName());

    // The root logger
    private static final Logger rootLogger = Logger.getLogger("");

    // Produces an account library
    private final Factory account = AccountLibrary::getInstance;

    // Produces a code library
    private final Factory code = CodeLibrary::getInstance;

    // Produces a detailed library
    private final Factory detailed = DetailedLibrary::getInstance;

    // Produces a distinguished library
    private final Factory distinguished =
            DistinguishedAccountLibrary::getInstance;

    // Produces a holding library
    private final Factory holding = HoldingLibrary::getInstance;

    // Our local message logger
    private final MessageLogger messageLogger = new MessageLogger();

    // Produces a portfolio library
    private final Factory portfolio = PortfolioLibrary::getInstance;

    // Produces a ticker library
    private final Factory ticker = TickerLibrary::getInstance;

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(Conductor.class.getCanonicalName()));
    }

    /**
     * Adds a file handler to the root logger.
     *
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void addFileHandler() throws IOException {

        /*
         * Build a date utilities object using the logging prefix, and a text
         * filetype. Construct the path to a log file using today's date.
         */
        final DateUtilities utilities = new DateUtilities("log", "txt");
        final Path path = Paths.get(utilities.getTypeDirectory().getPath(),
                utilities.constructFilename(new Date()));

        /*
         * Get the directory name of the log file path. Does this file exist,
         * and is it not a directory?
         */
        final Path parent = path.getParent();
        if (Files.exists(parent) && (!Files.isDirectory(parent))) {

            /*
             * The log file directory exists, but it is not a directory.
             * Unceremoniously delete it.
             */
            Files.delete(parent);
        }

        // Create the log file directory if it does not exist.
        if (!Files.isDirectory(parent)) {
            Files.createDirectory(parent);
        }

        // Create a new file handler, and give it a simple format.
        final FileHandler handler = new FileHandler(path.toString());
        handler.setFormatter(new SimpleFormatter() {

            // Our logging format
            private static final String format =
                    "[%1$tF %1$tT] [%2$s: %3$s]%n%4$s%n";

            @Override
            public synchronized String format(@NotNull LogRecord logRecord) {

                /*
                 * Split the logger name into elements and get the number of
                 * elements.
                 */
                final String[] nameElements =
                        logRecord.getLoggerName().split("\\.");
                final int elementNumber = nameElements.length;

                /*
                 * Format and return the logging string using the last element
                 * from the logger name.
                 */
                return String.format(format,
                        new Date(logRecord.getMillis()),
                        (0 < elementNumber) ?
                                nameElements[elementNumber - 1] : "",
                        logRecord.getLevel().getLocalizedName(),
                        logRecord.getMessage());
            }
        });

        // Get the desired logging level, and set it in the handler.
        final Level level = getLevel();
        handler.setLevel(level);

        // Get the root logger. Add the handler, and set the logging level.
        final Logger root = getRootLogger();
        root.addHandler(handler);
        root.setLevel(level);
    }

    /**
     * Configures logging for conductors.
     *
     * @return True if logging was successfully configures, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    private static boolean configureLogging() {

        /*
         * Remove any console handlers. Declare and initialize the return
         * value.
         */
        removeConsoleHandlers();
        boolean result = false;
        try {

            /*
             * Add a file handler for logging, and set the return false if
             * successful.
             */
            addFileHandler();
            result = true;

        }

        // Catch any I/O exception that may have occurred.
        catch (@NotNull IOException exception) {

            // Print a descriptive error message to system error.
            System.err.printf("This exception occurred while trying to add " +
                    "a file handler for logging: '%s'.%n", exception);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the logging level for a given key.
     *
     * @param key The logging key
     * @return The logging level for the given key
     */
    private static @NotNull String getLevel(@SuppressWarnings("SameParameterValue") @NotNull String key) {

        /*
         * Declare and initialize the result. Are we reading preferences to
         * produce the result?
         */
        String result = Level.INFO.getName();
        final boolean readPreference = true;
        //noinspection ConstantConditions
        if (readPreference) {

            // We are reading preferences to produce the result. So do it.
            result = preferences.get(key, result);
        }

        /*
         * We are not reading preferences for the result. Instead, write
         * the hardcoded result as the preference.
         */
        else {
            preferences.put(key, result);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the logging level.
     *
     * @return The logging level
     */
    private static Level getLevel() {

        /*
         * Declare and initialize the result. Declare and initialize the
         * default value of the logging level, and its key.
         */
        Level result = Level.INFO;
        final String defaultValue = result.getName();
        final String key = "LoggingLevel";

        // Get the logging level preference.
        final String value = getLevel(key);
        try {

            // Try to parse the logging level preference.
            result = Level.parse(value);
        }

        // Catch any illegal argument exception.
        catch (IllegalArgumentException exception) {

            /*
             * The logging level value is un-parseable. Write the default value
             * as the new preference, and tell about what happened.
             */
            preferences.put(key, defaultValue);
            System.out.printf("The preference value for the key '%s' " +
                    "contains the un-parseable logging level '%s'; I am " +
                    "using the default '%s', and have saved the same for " +
                    "future use.%n", key, value, defaultValue);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the root logger.
     *
     * @return The root logger
     */
    public static @NotNull Logger getRootLogger() {
        return rootLogger;
    }

    /**
     * Conducts the rebalancer.
     *
     * @param arguments Command line arguments.
     */
    public static void main(@NotNull String[] arguments) {

        /*
         * TODO:
         *
         * Put in a way to manage preferences, plus other activities unrelated
         * to generating portfolio reports and rebalancing portfolios. Do this
         * by parsing the command line arguments.
         */
        workWithPortfolios();
    }

    /**
     * Removes any console handlers from the root logger.
     */
    private static void removeConsoleHandlers() {

        // Get the root logger and its handlers. Cycle for each handler.
        final Logger root = getRootLogger();
        final Handler[] handlers = root.getHandlers();
        for (Handler handler : handlers) {

            // Remove the first/next handler if it is a console handler.
            if (handler instanceof ConsoleHandler) {
                root.removeHandler(handler);
            }
        }
    }

    /**
     * Works with portfolios.
     */
    private static void workWithPortfolios() {

        // Configure logging.
        System.out.println("I am configuring logging (this message will not " +
                "appear in the log file)...");
        configureLogging();

        /*
         * Create a conductor, and build the libraries. Was the build not
         * successful?
         */
        final Conductor conductor = new Conductor();
        conductor.logMessage(Level.INFO, "I am building libraries...");
        if (!conductor.buildLibraries()) {

            /*
             * Building libraries was not successful. Log information and
             * return.
             */
            conductor.logMessage(Level.INFO, "I am canceling my work " +
                    "because I could not successfully build the libraries...");
            return;
        }

        // Create a hierarchy.
        final Hierarchy hierarchy = Hierarchy.getInstance();
        conductor.logMessage(Level.INFO, "I am building the hierarchy and " +
                "synthesizing required accounts...");

        // Build the hierarchy. Was the build not successful?
        hierarchy.buildHierarchy();
        if (hierarchy.hadProblem()) {

            /*
             * Building the hierarchy was not successful. Log information and
             * return.
             */
            conductor.logMessage(Level.INFO, "I am canceling my work " +
                    "because I could not successfully build the hierarchy...");
            return;
        }

        try {

            // Try to write a report for each portfolio in the hiearchy.
            new ReportsBuilder().writeLines(hierarchy, null);
        }

        // Oops, I/O exception while trying to write the reports.
        catch (IOException exception) {

            // Log some information about the exception, and return.
            conductor.logMessage(Level.INFO, String.format("I received an " +
                    "I/O exception with message '%s' while attempting to " +
                    "write my reports, sorry.", exception.getMessage()));
            return;
        }

        /*
         * TODO:
         *
         * 1. Create a report based on 'considered' and 'not considered'
         * portfolio values.
         *
         * 2. Rebalance portfolio(s).
         *
         * 3. Create a differences file between actual and proposed values.
         *
         * 4. Create a report based on 'proposed' and 'not considered'
         * portfolio values.
         */

        conductor.logMessage(Level.INFO, "Congratulations; it seems I " +
                "have completed my work correctly!");
    }

    /**
     * Builds the libraries.
     *
     * @return True if the build had no warnings or errors, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    private boolean buildLibraries() {

        // Declare the return value.
        boolean result;
        try {

            /*
             * Build the holdings library with no date floor. Get the date of
             * the library, and use it to build the code library.
             */
            result = buildLibrary(new HoldingsBuilder(), null, holding);
            final Date floor = HoldingLibrary.getInstance().getDate();
            result = buildLibrary(new CodesBuilder(), floor, code) && result;

            // Use the date floor to build the portfolio library.
            result = buildLibrary(new PortfoliosBuilder(), floor, portfolio) &&
                    result;

            // Use the date floor to build the account library.
            result = buildLibrary(new AccountsBuilder(), floor, account) &&
                    result;

            // Use the date floor to build the detailed library.
            result = buildLibrary(new DetailedsBuilder(), floor, detailed) &&
                    result;

            // Use the date floor to build the ticker library.
            result = buildLibrary(new TickersBuilder(), floor, ticker) &&
                    result;

            // Use the date floor to build the distinguished libraries.
            result = buildLibrary(new DistinguishedsBuilder(), floor,
                    distinguished) && result;

        }

        // Catch any I/O exception that may have occurred.
        catch (IOException exception) {

            /*
             * Print a descriptive error message to system error. Clear the
             * return value.
             */
            System.err.printf("This exception occurred while trying to " +
                    "build the libraries and hierarchy: '%s'.%n", exception);
            result = false;
        }

        // Return the result to caller.
        return result;
    }

    /**
     * Builds a library.
     *
     * @param processor The element processor that builds the library
     * @param floor     The date floor for the data file
     * @param factory   A factory for producing the filled library
     * @return True if the build had no warnings or errors, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    private boolean buildLibrary(@NotNull ElementReader processor, Date floor,
                                 @NotNull Factory factory) throws IOException {

        /*
         * Read the data lines using the date floor. The floor can be null,
         * indicating no date floor.
         */
        final boolean result = (null == floor) ? processor.readLines() :
                processor.readLines(floor);

        // Log the date of the library.
        logMessage(Level.INFO, String.format(dateMessageFormat,
                processor.getPrefix(),
                DateUtilities.format(factory.produce().getDate())));

        // Return the result of reading the data lines.
        return result;
    }

    /**
     * Logs a message.
     *
     * @param level   The level of the message
     * @param message The message to log
     */
    @SuppressWarnings("SameParameterValue")
    private void logMessage(@NotNull Level level, @NotNull String message) {

        // Set the message to system 'out' if its level is less than severe...
        if (level.intValue() < Level.SEVERE.intValue()) {
            System.out.println(message);
        }

        // ...otherwise send the message to system 'err'.
        else {
            System.err.println(message);
        }

        // Log the message.
        messageLogger.logMessage(level, message);
    }

    /**
     * Sets the specific class/subclass logger.
     *
     * @param logger The specific class/subclass logger
     */
    private void setLogger(Logger logger) {
        messageLogger.setLogger(logger);
    }

    private interface Factory {

        /**
         * Produces a library.
         *
         * @return A library
         */
        @NotNull Library<?, ?> produce();
    }
}
