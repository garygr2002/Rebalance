package com.garygregg.rebalance;

import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.account.AccountsBuilder;
import com.garygregg.rebalance.cla.*;
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
import com.garygregg.rebalance.report.CurrentReportWriter;
import com.garygregg.rebalance.ticker.TickerLibrary;
import com.garygregg.rebalance.ticker.TickersBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.*;
import java.util.prefs.Preferences;

public class Conductor implements Dispatch<CommandLineId> {

    // The action for building command line options
    private static final BuildOptionAction buildOptionAction =
            new BuildOptionAction();

    // The format for reporting the date of a library
    private final static String dateMessageFormat =
            "The date of the %s library/libraries is/are: %s.";

    // The default name of this program
    private static final String defaultProgramName = "program.name";

    // The action for describing options
    private static final DescribeOptionAction describeOptionAction =
            new DescribeOptionAction();

    // The error stream we will use
    private static final PrintStream errorStream = System.err;

    // A conductor instance
    private static final Conductor instance = new Conductor();

    // The action for determining maximum option description length
    private static final MaxLengthAction maxLengthAction =
            new MaxLengthAction();

    // The output stream we will use
    private static final PrintStream outputStream = System.out;

    // A preference manager instance
    private static final PreferenceManager preferenceManager =
            PreferenceManager.getInstance();

    // The root logger
    private static final Logger rootLogger = Logger.getLogger("");

    // The canonical name of this class
    private static String myCanonicalName;

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
        setLogger(Logger.getLogger(getMyCanonicalName()));
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
        final Level level = preferenceManager.getLevel();
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
            getErrorStream().printf("This exception occurred while trying to " +
                    "add a file handler for logging: '%s'.%n", exception);
        }

        // Return the result.
        return result;
    }

    /**
     * Displays a usage line.
     *
     * @param programName The name of this program.
     */
    private static void displayUsage(@NotNull String programName) {

        // Declare and initialize a list of options and their descriptions.
        final List<Pair<String, String>> options = List.of(
                new Pair<>("-level vl", "ALL, CONFIG, FINE, FINER, FINEST, " +
                        "INFO, OFF, SEVERE or WARNING"),
                new Pair<>("-inflation fltn", "annual inflation rate"),
                new Pair<>("-high sph", "S&P 500 high"),
                new Pair<>("-current spc", "S&P 500 current"),
                new Pair<>("-p datpth", "data path"),
                new Pair<>("-d bckpth", "backup path"),
                new Pair<>("-backup", "perform backup")
        );

        // Build the usage line.
        buildOptionAction.resetBuffer(programName);
        iterate(options, buildOptionAction);

        // Get the output stream, and output the usage line.
        final PrintStream stream = getOutputStream();
        stream.println(buildOptionAction.getBuffer());

        // Calculate the length of the longest option.
        maxLengthAction.zeroMaxLength();
        iterate(options, maxLengthAction);

        // Describe the options.
        describeOptionAction.setLength(maxLengthAction.getMaxLength());
        iterate(options, describeOptionAction);
    }

    /**
     * Gets the default program name.
     *
     * @return The default program name
     */
    private static @NotNull String getDefaultProgramName() {

        /*
         * Declare a hard-coded default in case the package parsing regular
         * expression is bad. Get the package elements of this class, and split
         * it by the name separator character. (Is this character declared
         * somewhere?)
         */
        String result = defaultProgramName;
        final String[] packageElements = getMyCanonicalName().split("\\.");

        /*
         * Get the length of package elements. If there is not at least one
         * element then there is something wrong with the regular expression
         * used to do the split. Leave it to the user to figure that out when
         * she receives the default name. Is there more than one package
         * element?
         */
        final int length = packageElements.length;
        if (0 < length) {

            /*
             * There is more than one package element. Choose an element
             * depending on whether there are two or more elements, or there is
             * only one.
             */
            result = (1 < length) ? packageElements[length - 2] :
                    packageElements[0];
        }

        // Return the result.
        return result;
    }

    /**
     * The error stream we will use.
     *
     * @return The error stream we will use
     */
    private static @NotNull PrintStream getErrorStream() {
        return errorStream;
    }

    /**
     * Gets a conductor instance.
     *
     * @return A conductor instance
     */
    private static Conductor getInstance() {
        return instance;
    }

    /**
     * Gets the canonical name of this class.
     *
     * @return The canonical name of this class
     */
    public static String getMyCanonicalName() {

        // Set the canonical name if it is null.
        if (null == myCanonicalName) {
            myCanonicalName = Conductor.class.getCanonicalName();
        }

        // Return the canonical name.
        return myCanonicalName;
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
     * Gets the root logger.
     *
     * @return The root logger
     */
    public static @NotNull Logger getRootLogger() {
        return rootLogger;
    }

    /**
     * Iterates over a list, and performs an action on each element.
     *
     * @param list   The list
     * @param action The action to perform on each element in the list
     * @param <T>    The type of elements in the list
     */
    private static <T> void iterate(@NotNull List<T> list,
                                    @NotNull Action<? super T> action) {

        // Cycle for each element in the list, and perform the action
        for (T element : list) {
            action.perform(element);
        }
    }

    /**
     * Conducts the rebalancer.
     *
     * @param arguments Command line arguments.
     */
    public static void main(@NotNull String[] arguments) {

        // Declare local and initialize local variables.
        final List<Dispatch<CommandLineId>> dispatchList = new ArrayList<>();
        final Preferences preferences = preferenceManager.getPreferences();
        final PrintStream outputStream = getOutputStream();

        // Add a preference dispatch for the S&P 500 current value.
        dispatchList.add(new DoublePreferenceDispatch<>(CommandLineId.CURRENT,
                preferences, outputStream,
                PreferenceManager.getCurrentDefault()));

        // Add a preference dispatch for the data backup path.
        dispatchList.add(new PathPreferenceDispatch<>(CommandLineId.DESTINATION,
                preferences, outputStream,
                PreferenceManager.getDestinationNameDefault()));

        // Add a preference dispatch for the S&P 500 high value.
        dispatchList.add(new DoublePreferenceDispatch<>(CommandLineId.HIGH,
                preferences, outputStream,
                PreferenceManager.getHighDefault()));

        // Add a preference dispatch for the expected annual inflation.
        dispatchList.add(new DoublePreferenceDispatch<>(CommandLineId.INFLATION,
                preferences, outputStream,
                PreferenceManager.getInflationDefault()));

        // Add a preference dispatch for the desired logging level.
        dispatchList.add(new LevelPreferenceDispatch<>(CommandLineId.LEVEL,
                preferences, outputStream,
                PreferenceManager.getLevelDefault()));

        /*
         * Add a preference dispatch for the data location path, and a dispatch
         * for data backup.
         */
        dispatchList.add(new PathPreferenceDispatch<>(CommandLineId.PATH,
                preferences, outputStream,
                PreferenceManager.getPathNameDefault()));
        dispatchList.add(new Backup(outputStream));

        /*
         * Create a command line arguments object with an instance of the
         * conductor.
         */
        final CommandLineArguments<CommandLineId> cla =
                new CommandLineArguments<>(dispatchList, getInstance());
        try {

            // Try to process the command line arguments, if any.
            cla.process(arguments);
        }

        /*
         * Catch any command line exception, and print the exception message
         * to the error stream.
         */
        catch (@NotNull CLAException exception) {

            /*
             * Get the error stream and display the message of the exception.
             * Get the program name system property.
             */
            getErrorStream().println(exception.getMessage());
            final String programName = System.getProperty(defaultProgramName);

            /*
             * Use the program name system property to display a usage message
             * if the program name property is not null. Otherwise use a default
             * program name.
             */
            displayUsage((null == programName) ? getDefaultProgramName() :
                    programName);
        }
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
        getOutputStream().println("I am configuring logging (this message " +
                "will not appear in the log file)...");
        configureLogging();

        /*
         * Get a conductor instance and build the libraries. Was the build not
         * successful?
         */
        final Conductor conductor = getInstance();
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
            new CurrentReportWriter().writeLines(hierarchy, null);
        }

        // Oops, I/O exception while trying to write the reports.
        catch (IOException exception) {

            // Log some information about the exception, and return.
            conductor.logMessage(Level.INFO, String.format("I received an " +
                    "I/O exception with message '%s' while attempting to " +
                    "write my reports, sorry.", exception.getMessage()));
            return;
        }

        // Log a success message if we get this far.
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
            getErrorStream().printf("This exception occurred while trying " +
                            "to build the libraries and hierarchy: '%s'.%n",
                    exception);
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

    @Override
    public void dispatch(String argument) {
        workWithPortfolios();
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.OTHER;
    }

    /**
     * Logs a message.
     *
     * @param level   The level of the message
     * @param message The message to log
     */
    @SuppressWarnings("SameParameterValue")
    private void logMessage(@NotNull Level level, @NotNull String message) {

        // Identify the proper print stream for the message.
        final PrintStream printStream = (level.intValue() <
                Level.SEVERE.intValue()) ? getOutputStream() :
                getErrorStream();

        // Print the message to the print stream, then log the message.
        printStream.println(message);
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

    private interface Action<T> {

        /**
         * Performs an action on an argument.
         *
         * @param argument The argument for the action
         */
        void perform(@NotNull T argument);
    }

    private interface Factory {

        /**
         * Produces a library.
         *
         * @return A library
         */
        @NotNull Library<?, ?> produce();
    }

    private static class BuildOptionAction
            implements Action<Pair<String, String>> {

        // The buffer for formatting the usage line
        private final StringBuffer buffer = new StringBuffer();

        /**
         * Gets the formatted usage line.
         *
         * @return The formatted usage line
         */
        public String getBuffer() {
            return buffer.toString();
        }

        @Override
        public void perform(@NotNull Pair<String, String> argument) {
            buffer.append(String.format(" [%s]", argument.getFirst()));
        }

        /**
         * Resets the buffer.
         *
         * @param programName The argument for the program name
         */
        public void resetBuffer(@NotNull String programName) {
            buffer.replace(0, buffer.length(), String.format("usage: %s",
                    programName));
        }
    }

    private static class DescribeOptionAction
            implements Action<Pair<String, String>> {

        // The prefix of the description
        private final String prefix;

        // The suffix of the description
        private final String suffix;

        // The format for displaying option syntax
        private String optionFormat;

        {

            /*
             * Initialize the length of the option name field. Declare
             * what a space is.
             */
            setLength(0);
            final String space = " ";

            /*
             * Declare and initialize elements of the options description
             * lines.
             */
            prefix = space.repeat(4);
            suffix = String.format(":%s", space);
        }

        @Override
        public void perform(@NotNull Pair<String, String> argument) {
            getOutputStream().printf((optionFormat) + "%n", prefix,
                    argument.getFirst(), suffix, argument.getSecond());
        }

        /**
         * Sets the length of the option name field.
         *
         * @param length The length of the option name field
         */
        public void setLength(int length) {
            optionFormat = String.format("%%s%%%ds%%s%%s", length);
        }
    }

    private static class MaxLengthAction
            implements Action<Pair<String, String>> {

        // The calculated maximum length
        private int maxLength;

        {

            // Zero the maximum length upon construction.
            zeroMaxLength();
        }

        /**
         * Gets the maximum length.
         *
         * @return The maximum length
         */
        public int getMaxLength() {
            return maxLength;
        }

        @Override
        public void perform(@NotNull Pair<String, String> argument) {
            maxLength = Math.max(maxLength, argument.getFirst().length());
        }

        /**
         * Zeros the maximum length.
         */
        public void zeroMaxLength() {
            maxLength = 0;
        }
    }
}
