package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.*;
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
import com.garygregg.rebalance.holding.BasesBuilder;
import com.garygregg.rebalance.holding.HoldingLibrary;
import com.garygregg.rebalance.holding.ValuationsBuilder;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.portfolio.PortfoliosBuilder;
import com.garygregg.rebalance.rebalance.PortfolioRebalancer;
import com.garygregg.rebalance.report.ActionReportWriter;
import com.garygregg.rebalance.report.CurrentReportWriter;
import com.garygregg.rebalance.report.DifferenceReportWriter;
import com.garygregg.rebalance.report.ProposedReportWriter;
import com.garygregg.rebalance.tax.*;
import com.garygregg.rebalance.ticker.TickerLibrary;
import com.garygregg.rebalance.ticker.TickersBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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

    // A conductor instance
    private static final Conductor instance = new Conductor();

    // The action for determining maximum option description length
    private static final MaxLengthAction maxLengthAction =
            new MaxLengthAction();

    // A template message for a missing tax library
    private static final String missingTaxLibrary = "%s tax libraries are " +
            "missing for one or more filing statuses!";

    // The command line option list
    private static final List<Pair<String, String>> optionList =
            buildOptionList();

    // A preference manager instance
    private static final PreferenceManager preferenceManager =
            PreferenceManager.getInstance();

    // The root logger
    private static final Logger rootLogger = Logger.getLogger("");

    // The canonical name of this class
    private static String myCanonicalName;

    // Produces an account library
    private final Factory account = AccountLibrary::getInstance;

    // Produces a basis library
    private final Factory basis = () ->
            HoldingLibrary.getInstance(HoldingType.BASIS);

    // Produces a code library
    private final Factory code = CodeLibrary::getInstance;

    // Produces a detailed library
    private final Factory detailed = DetailedLibrary::getInstance;

    // Produces a distinguished library
    private final Factory distinguished =
            DistinguishedAccountLibrary::getInstance;

    // Produces a capital gains tax library for head-of-household filers
    private final Factory gainsHead = () ->
            GainsTaxLibrary.getLibrary(FilingStatus.HEAD);

    // Produces a capital gains tax library for married-filing-jointly filers
    private final Factory gainsJoint = () ->
            GainsTaxLibrary.getLibrary(FilingStatus.JOINT);

    /*
     * Produces a capital gains tax library for married-filing-separately
     * filers
     */
    private final Factory gainsSeparate = () ->
            GainsTaxLibrary.getLibrary(FilingStatus.SEPARATE);

    // Produces a capital gains tax library for single filers
    private final Factory gainsSingle = () ->
            GainsTaxLibrary.getLibrary(FilingStatus.SINGLE);

    // Produces an income tax library for head-of-household filers
    private final Factory incomeHead = () ->
            IncomeTaxLibrary.getLibrary(FilingStatus.HEAD);

    // Produces an income tax library for married-filing-jointly filers
    private final Factory incomeJoint = () ->
            IncomeTaxLibrary.getLibrary(FilingStatus.JOINT);

    // Produces an income tax library for married-filing-separately filers
    private final Factory incomeSeparate = () ->
            IncomeTaxLibrary.getLibrary(FilingStatus.SEPARATE);

    // Produces an income tax library for single filers
    private final Factory incomeSingle = () ->
            IncomeTaxLibrary.getLibrary(FilingStatus.SINGLE);

    // Our local message logger
    private final MessageLogger messageLogger = new MessageLogger();

    // Produces a portfolio library
    private final Factory portfolio = PortfolioLibrary::getInstance;

    // Produces a ticker library
    private final Factory ticker = TickerLibrary::getInstance;

    // Produces a valuation library
    private final Factory valuation = () ->
            HoldingLibrary.getInstance(HoldingType.VALUATION);

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
     * Builds the command option list.
     *
     * @return The command option list
     */
    private static @NotNull List<Pair<String, String>> buildOptionList() {

        // Declare automatic variables.
        String argumentClose;
        String argumentName;
        String argumentOpen;

        /*
         * Declare and initialize well-known strings. Declare and initialize
         * the return variable. Cycle for each command line ID.
         */
        final String closeBracket = "]", empty = "", openBracket = "[";
        final List<Pair<String, String>> optionList = new ArrayList<>();
        for (CommandLineId commandLineId : CommandLineId.values()) {

            // Do not add 'other'.
            if (!CommandLineId.OTHER.equals(commandLineId)) {

                /*
                 * Get the argument name. Is the option argument
                 * mandatory?
                 */
                argumentName = commandLineId.getArgumentName();
                if (commandLineId.isArgumentMandatory()) {

                    /*
                     * The 'options' argument is mandatory. Do not add open and
                     * close brackets.
                     */
                    argumentClose = argumentOpen = empty;
                }

                // The option argument is not mandatory.
                else {

                    // Give the option argument open and close brackets.
                    argumentClose = closeBracket;
                    argumentOpen = openBracket;
                }

                // Format the option, and add it to the list.
                optionList.add(new Pair<>(String.format("-%s%s",
                        commandLineId.toString().toLowerCase(),
                        (null == argumentName) ? empty :
                                String.format(" %s%s%s", argumentOpen,
                                        argumentName, argumentClose)),
                        commandLineId.getDescription()));
            }
        }

        // Return the list.
        return optionList;
    }

    /**
     * Checks that all required preferences have been set.
     *
     * @throws CLAException Indicates that one or more required preferences
     *                      have not yet been set
     */
    private static void checkPreferences() throws CLAException {

        /*
         * Get a preference manager instance. Create a list of missing
         * preference names.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        final List<String> missingPreferences = new ArrayList<>();

        /*
         * Currently, test only that the source preference has been set.
         * Other dispatchers or synthesizers can take appropriate action with
         * unset preferences as the need arises. Was there this one, missing
         * preference?
         */
        test(missingPreferences, CommandLineId.SOURCE, manager.getSource());
        if (!missingPreferences.isEmpty()) {

            /*
             * Get an iterator for missing preference(s). Initialize a string
             * builder with the first missing preference name.
             */
            final Iterator<String> iterator = missingPreferences.iterator();
            final StringBuilder buffer = new StringBuilder(
                    String.format("The following required preferences have " +
                            "not yet been set: %s", iterator.next()));

            // Append missing preference names while they exist.
            while (iterator.hasNext()) {
                buffer.append(String.format(", %s", iterator.next()));
            }

            /*
             * Append a period to the message, and throw a new CLA exception
             * with the names of the missing preferences.
             */
            buffer.append(".");
            throw new CLAException(buffer.toString());
        }
    }

    /**
     * Conducts the rebalancer.
     *
     * @param arguments An array of arguments
     */
    public static void conduct(@NotNull String[] arguments) {

        // Declare and initialize local variables.
        final List<Dispatch<CommandLineId>> dispatchList = new ArrayList<>();
        final Preferences preferences = preferenceManager.getPreferences();
        final PrintStream outputStream = MessageLogger.getOutputStream();

        // Add a dispatch for the S&P 500 current value.
        dispatchList.add(new DoublePreferenceDispatch<>(CommandLineId.CURRENT,
                preferences, outputStream, false));

        // Add a preference dispatch for the data directory backup.
        dispatchList.add(new PreferenceDispatch<>(CommandLineId.DESTINATION,
                preferences, outputStream));

        /*
         * Add a preference dispatch for the level of extraordinary
         * informational messages.
         */
        dispatchList.add(new LimitedPreferenceDispatch<>(
                CommandLineId.EXTRAORDINARY, preferences, outputStream));

        // Add a preference dispatch for the S&P 500 high value.
        dispatchList.add(new DoublePreferenceDispatch<>(CommandLineId.HIGH,
                preferences, outputStream, false));

        // Add a preference dispatch for the expected annual inflation.
        dispatchList.add(new DoublePreferenceDispatch<>(
                CommandLineId.INFLATION, preferences, outputStream, true));

        // Add a preference dispatch for the desired logging level.
        dispatchList.add(new LevelPreferenceDispatch<>(CommandLineId.LEVEL,
                preferences, outputStream));
        /*
         * Add a preference dispatch for the level of ordinary informational
         * messages.
         */
        dispatchList.add(new LimitedPreferenceDispatch<>(
                CommandLineId.ORDINARY, preferences, outputStream));


        // Add a preference dispatch for source data directory.
        dispatchList.add(new PathPreferenceDispatch<>(CommandLineId.SOURCE,
                preferences, outputStream));

        // Add a preference dispatch for limit of allowed receiver delegates.
        dispatchList.add(new IntPreferenceDispatch<>(CommandLineId.X,
                preferences, outputStream, false));

        /*
         * Add a preference dispatch for use expected prefix and suffix for
         * data directory backup and minimum settings.
         */
        dispatchList.add(new Use(preferences, outputStream));
        dispatchList.add(new Minimum(outputStream));

        // Add dispatches for reset, backup and preferences.
        dispatchList.add(new Reset(outputStream));
        dispatchList.add(new Backup(outputStream));
        dispatchList.add(new Preference(outputStream));

        /*
         * Create a command line arguments object with an instance of the
         * conductor.
         */
        final CommandLineArguments<CommandLineId> cla =
                new CommandLineArguments<>(dispatchList, getInstance());
        //noinspection SpellCheckingInspection
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
             * Stream the message of the exception as a severe error. Get the
             * program name system property. Note: To set this property on the
             * command line, use:
             *
             * -Dprogram.name = <name>
             *
             * In IntelliJ, select 'Add VM options' under 'Modify options'. Use
             * the same syntax as above. A properly configured alias for
             * starting the conductor should be able to added to a user login
             * file.
             */
            MessageLogger.stream(Level.SEVERE, exception.getMessage());
            final String programName = System.getProperty(defaultProgramName);

            /*
             * Use the program name system property to display a usage message
             * if the program name property is not null. Otherwise, use a
             * default program name.
             */
            displayUsage((null == programName) ? getDefaultProgramName() :
                    programName);
        }
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
            MessageLogger.stream(Level.SEVERE, String.format("This " +
                    "exception occurred while trying to add a file handler " +
                    "for logging: '%s'.", exception));
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

        // Build the usage line.
        buildOptionAction.resetBuffer(programName);
        iterate(optionList, buildOptionAction);

        // Output the usage line.
        MessageLogger.stream(MessageLogger.getExtraordinary(),
                buildOptionAction.getBuffer());

        // Calculate the length of the longest option.
        maxLengthAction.zeroMaxLength();
        iterate(optionList, maxLengthAction);

        // Describe the options.
        describeOptionAction.setLength(maxLengthAction.getMaxLength());
        iterate(optionList, describeOptionAction);
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
    @SuppressWarnings("SameParameterValue")
    private static <T> void iterate(@NotNull List<T> list,
                                    @NotNull Action<? super T> action) {

        // Cycle for each element in the list, and perform the action
        for (T element : list) {
            action.perform(element);
        }
    }

    /**
     * Conducts the rebalancer from a command line.
     *
     * @param arguments Command line arguments.
     */
    public static void main(@NotNull String[] arguments) {

        /*
         * Explicitly set the error and output streams in the message logger
         * and start conducting!
         */
        MessageLogger.setErrorStream(System.err);
        MessageLogger.setOutputStream(System.out);
        conduct(arguments);
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
     * Tests a preference against null.
     *
     * @param missingPreferences A list of missing preferences
     * @param commandLineId      A command line ID corresponding to the
     *                           preference
     * @param preference         The preference object
     */
    @SuppressWarnings("SameParameterValue")
    private static void test(@NotNull List<String> missingPreferences,
                             @NotNull CommandLineId commandLineId,
                             Object preference) {

        /*
         * Add the name of the command line ID to the list of missing
         * preferences if the preference object is null.
         */
        if (null == preference) {
            missingPreferences.add(commandLineId.name().toLowerCase());
        }
    }

    /**
     * Works with portfolios.
     */
    private static void workWithPortfolios() {

        // Configure logging.
        final Level level = MessageLogger.getExtraordinary();
        MessageLogger.stream(level, "I am " +
                "configuring logging (this message will not appear in the " +
                "log file)...");
        configureLogging();

        // Set inflation in the inflation caddy using the preference manager.
        InflationCaddy.getInstance().setPercent(
                PreferenceManager.getInstance().getInflation());

        // Get a conductor instance and its logger.
        final Conductor conductor = getInstance();
        final MessageLogger logger = conductor.getMessageLogger();

        // Build the libraries. Was the build not successful?
        logger.streamAndLog(level, "I am building libraries...");
        if (!conductor.buildLibraries()) {

            /*
             * Building libraries was not successful. Stream and log
             * information and return.
             */
            logger.streamAndLog(level, "I am canceling my work because I " +
                    "could not successfully build the libraries...");
            return;
        }

        /*
         * Return if either the valuation hierarchy or the basis hierarchy
         * could not be built.
         */
        if (!(conductor.buildHierarchy(HoldingType.VALUATION) &&
                conductor.buildHierarchy(HoldingType.BASIS))) {
            return;
        }

        try {

            /*
             * Try to write a report for current holdings for each portfolio in
             * the default hierarchy.
             */
            Hierarchy hierarchy = Hierarchy.getInstance();
            new CurrentReportWriter().writeLines(hierarchy, null);

            /*
             * Now rebalance the hierarchy by account. Try to write a report
             * for proposed holdings for each portfolio in the default
             * hierarchy.
             */
            PortfolioRebalancer.getInstance().rebalanceByAccount(hierarchy);
            new ProposedReportWriter().writeLines(hierarchy, null);

            /*
             * Try to write a report for the difference between proposed and
             * considered values for each line in the holding file. Then try to
             * write a report for actions to be taken to rebalance the
             * portfolio.
             */
            new DifferenceReportWriter().writeLines(hierarchy, null);
            new ActionReportWriter().writeLines(hierarchy, null);
        }

        // Oops, an I/O exception occurred while trying to write the reports.
        catch (IOException exception) {

            // Stream and log some information about the exception, and return.
            logger.streamAndLog(level, String.format("I received an I/O " +
                    "exception with message '%s' while attempting to write " +
                    "my reports, sorry.", exception.getMessage()));
            return;
        }

        // Stream and log a success message if we get this far.
        logger.streamAndLog(level, "Congratulations; it seems I have " +
                "completed my work correctly!");
    }

    /**
     * Builds the capital gains tax libraries.
     *
     * @return True if the build had no warnings or errors, false otherwise
     */
    private boolean buildGainsTaxLibraries(Date floor) throws IOException {

        /*
         * Use the date floor to build the capital gains tax builder for
         * head-of-household filers, and...
         */
        boolean result = buildLibrary(new GainsHeadTaxesBuilder(), floor,
                gainsHead);

        /*
         * ...the capital gains tax builder for married-filing-jointly filers,
         * and...
         */
        result = buildLibrary(new GainsJointTaxesBuilder(), floor, gainsJoint)
                && result;

        /*
         * ...the capital gains tax builder for married-filing-separate filers,
         * and...
         */
        result = buildLibrary(new GainsSeparateTaxesBuilder(), floor,
                gainsSeparate) && result;

        // ...the capital gains tax builder for single filers.
        result = buildLibrary(new GainsSingleTaxesBuilder(), floor,
                gainsSingle) && result;

        // It is a horrible problem if a capital gains tax library is missing.
        if (result && (!(result = GainsTaxLibrary.checkContract()))) {
            getMessageLogger().streamAndLog(Level.SEVERE,
                    String.format(missingTaxLibrary, "Capital gains"));
        }

        // Return the result.
        return result;
    }

    /**
     * Builds a hierarchy for a given holding type.
     *
     * @param holdingType The given holding type
     * @return True if the hierarchy build was successful; false otherwise
     */
    private boolean buildHierarchy(@NotNull HoldingType holdingType) {

        /*
         * Declare and initialize the result to success. Create a hierarchy for
         * the given holding type.
         */
        boolean result = true;
        final Hierarchy hierarchy = Hierarchy.getInstance(holdingType);

        // Get the name of the hierarchy holding type.
        final String hierarchyName =
                hierarchy.getHoldingType().toString().toLowerCase();

        /*
         * Get the logging level for extraordinary informational messages. Get
         * the message logger.
         */
        final Level level = MessageLogger.getExtraordinary();
        final MessageLogger logger = getMessageLogger();

        // Stream and log a message about what is going on.
        logger.streamAndLog(level, String.format("I am building the %s " +
                        "hierarchy and synthesizing required accounts...",
                hierarchyName));

        // Build the hierarchy. Was the build not successful?
        hierarchy.buildHierarchy();
        if (hierarchy.hadProblem()) {

            /*
             * Building the hierarchy was not successful. Stream and log
             * information and reset the return value.
             */
            logger.streamAndLog(level, String.format("I am canceling my " +
                    "work because I could not successfully build the %s " +
                    "hierarchy...", hierarchyName));
            result = false;
        }

        // Return the result.
        return result;
    }

    /**
     * Builds the income tax libraries.
     *
     * @return True if the build had no warnings or errors, false otherwise
     */
    private boolean buildIncomeTaxLibraries(Date floor) throws IOException {

        /*
         * Use the date floor to build the income tax builder for
         * head-of-household filers, and...
         */
        boolean result = buildLibrary(new IncomeHeadTaxesBuilder(), floor,
                incomeHead);

        // ...the income tax builder for married-filing-jointly filers, and...
        result = buildLibrary(new IncomeJointTaxesBuilder(), floor,
                incomeJoint) && result;

        // ...the income tax builder for married-filing-separate filers, and...
        result = buildLibrary(new IncomeSeparateTaxesBuilder(), floor,
                incomeSeparate) && result;

        // ...the income tax builder for single filers.
        result = buildLibrary(new IncomeSingleTaxesBuilder(), floor,
                incomeSingle) && result;

        // It is a horrible problem if an income tax library is missing.
        if (result && (!(result = IncomeTaxLibrary.checkContract()))) {
            getMessageLogger().streamAndLog(Level.SEVERE,
                    String.format(missingTaxLibrary, "Income"));
        }

        // Return the result.
        return result;
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
             * Initialize the parent tracker instance. Build the valuation
             * library with no date floor.
             */
            initialize();
            result = buildLibrary(new ValuationsBuilder(), null, valuation);

            /*
             * Get the date of the valuation library, and use it to build the
             * basis library.
             */
            final Date floor =
                    HoldingLibrary.getInstance(HoldingType.VALUATION).getDate();
            result = buildLibrary(new BasesBuilder(), floor, basis) && result;

            /*
             * Use the date floor to build the code library, the income tax
             * libraries, and the capital gains tax libraries.
             */
            result = buildLibrary(new CodesBuilder(), floor, code) && result;
            result = buildIncomeTaxLibraries(floor) && result;
            result = buildGainsTaxLibraries(floor) && result;

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
             * Stream a descriptive error message to system error. Clear the
             * return value.
             */
            MessageLogger.stream(Level.SEVERE, String.format("This " +
                    "exception occurred while trying to build the libraries " +
                    "and hierarchy: '%s'.", exception));
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
    private boolean buildLibrary(@NotNull ElementReader<?> processor,
                                 Date floor, @NotNull Factory factory)
            throws IOException {

        /*
         * Read the data lines using the date floor. The floor can be null,
         * indicating no date floor.
         */
        final boolean result = (null == floor) ? processor.readLines() :
                processor.readLines(floor);

        // Stream and log the date of the library.
        getMessageLogger().streamAndLog(MessageLogger.getExtraordinary(),
                String.format(dateMessageFormat, processor.getPrefix(),
                        DateUtilities.format(factory.produce().getDate())));

        // Return the result of reading the data lines.
        return result;
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Check that all required preferences have been set before working
         * with portfolios.
         */
        checkPreferences();
        workWithPortfolios();
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.OTHER;
    }

    /**
     * Gets our message logger.
     *
     * @return Our message logger
     */
    public @NotNull MessageLogger getMessageLogger() {
        return messageLogger;
    }

    /**
     * Initializes the parent tracker.
     */
    private void initialize() {

        // Get the parent tracker instance and clear its associations.
        final ParentTracker tracker = ParentTracker.getInstance();
        tracker.clearAssociations();

        /*
         * Add line code associations for the portfolio library. Add the single
         * line code for institutions.
         */
        tracker.addAssociations(PortfolioLibrary.getInstance(),
                HoldingLineType.PORTFOLIO);
        tracker.addAssociation('I', HoldingLineType.INSTITUTION);

        /*
         * Set the distinguished account library in the parent tracker
         * with the account library instance. Add line code associations
         * for the ticker library.
         */
        tracker.setAccountLibrary(AccountLibrary.getInstance());
        tracker.addAssociations(TickerLibrary.getInstance(),
                HoldingLineType.TICKER);
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
        @Contract(pure = true)
        public @NotNull String getBuffer() {
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
             * Declare and initialize elements of the 'options' description
             * lines.
             */
            prefix = space.repeat(4);
            suffix = String.format(":%s", space);
        }

        @Override
        public void perform(@NotNull Pair<String, String> argument) {
            MessageLogger.stream(MessageLogger.getExtraordinary(),
                    String.format((optionFormat), prefix, argument.getFirst(),
                            suffix, argument.getSecond()));
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
