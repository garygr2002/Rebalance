package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.interpreter.CodeInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistinguishedsBuilder extends
        ElementReader<DistinguishedDescription<?, ?>> {

    // The distinguished account library instance
    private final DistinguishedAccountLibrary accountLibrary =
            DistinguishedAccountLibrary.getInstance();

    // The distinguished institution library instance
    private final DistinguishedInstitutionLibrary institutionLibrary =
            DistinguishedInstitutionLibrary.getInstance();

    // The element processor for institutions
    private final ElementProcessor<DistinguishedInstitution>
            institutionProcessor = new ElementProcessor<>(
            DistinguishedInstitution.class) {

        @Override
        public DistinguishedDescription<DistinguishedInstitution, ?>
        process(@NotNull String keyString,
                @NotNull Pair<String, String> value,
                int lineNumber) {

            /*
             * Try to find the named distinguish institution using the key
             * string. Does the string not represent any known distinguished
             * institution key?
             */
            DistinguishedInstitutionDescription description = null;
            final DistinguishedInstitution distinguishedInstitution =
                    determineKey(keyString);
            if (null == distinguishedInstitution) {

                /*
                 * The key string does not represent any known distinguished
                 * institution. Log this as an error.
                 */
                logMessage(Level.SEVERE, String.format("The institution key " +
                                "'%s' named at line %d does not represent any " +
                                "known, distinguished institution.", keyString,
                        lineNumber));
            }

            // The string represents a known distinguished institution key.
            else {

                /*
                 * The distinguished institution is recognized. But is it
                 * the default distinguished institution?
                 */
                if (DistinguishedInstitution.DEFAULT.equals(
                        distinguishedInstitution)) {

                    /*
                     * The distinguished institution is the default, which
                     * is not meant to be set. Log a warning.
                     */
                    logMessage(Level.WARNING, String.format("The " +
                            "default distinguished institution is not " +
                            "meant to be set at line %d; you may " +
                            "wish to check this.", lineNumber));
                }

                /*
                 * Get the name from the given value. Is the key element
                 * okay according to the distinguished institution library?
                 */
                final String institution = value.getSecond();
                if (institutionLibrary.areKeyElementsOkay(institution)) {

                    /*
                     * The key element is okay according to the distinguished
                     * institution library. Create a new distinguished
                     * institution description using the named institution.
                     */
                    description = new DistinguishedInstitutionDescription(
                            distinguishedInstitution, institution);

                    /*
                     * Add the new description to the distinguished institution
                     * library. Log exit information.
                     */
                    institutionLibrary.addDescription(description);
                    logMessage(getOrdinary(), String.format("Load of " +
                                    "metadata for distinguished institution " +
                                    "with key '%s' and value '%s' at line " +
                                    "%d was%s successful.",
                            description.getKey(),
                            description.getValue(), lineNumber,
                            hadLineProblem() ? " not" : ""));
                }

                /*
                 * The key is not okay according to the distinguished
                 * institution library. There is something wrong with the given
                 * value.
                 */
                else {

                    // Log an error message.
                    logMessage(Level.SEVERE, String.format("There is " +
                            "something wrong with key '%s' in its " +
                            "distinguished institution description " +
                            "at line %d.", value, lineNumber));
                }
            }

            // Return the description.
            return description;
        }
    };

    // Our code interpreter
    private final CodeInterpreter interpreter = new CodeInterpreter();

    // The distinguished portfolio library instance
    private final DistinguishedPortfolioLibrary portfolioLibrary =
            DistinguishedPortfolioLibrary.getInstance();

    // The processor map
    private final Map<HoldingLineType, ElementProcessor<?>> processorMap =
            new HashMap<>();

    // The element processor for portfolios
    private final ElementProcessor<DistinguishedTickers>
            tickerProcessor = new ElementProcessor<>(
            DistinguishedTickers.class) {

        @Override
        public DistinguishedDescription<DistinguishedTickers, ?>
        process(@NotNull String keyString,
                @NotNull Pair<String, String> value,
                int lineNumber) {

            /*
             * We currently do not supported distinguished ticker symbols. Log
             * a warning.
             */
            logMessage(Level.WARNING, String.format("Distinguished ticker " +
                    "specification with key '%s' and value '%s' read at " +
                    "line %d; I do not currently supported distinguished " +
                    "ticker symbols.", keyString, value, lineNumber));
            return null;
        }
    };
    // The parent tracker
    private final ParentTracker tracker = ParentTracker.getInstance();

    // The most recent portfolio key
    private String portfolioKey;

    // The element processor for accounts
    private final ElementProcessor<DistinguishedAccount> accountProcessor =
            new ElementProcessor<>(DistinguishedAccount.class) {

                @Override
                public DistinguishedDescription<DistinguishedAccount, ?>
                process(@NotNull String keyString,
                        @NotNull Pair<String, String> value,
                        int lineNumber) {

                    /*
                     * Declare and initialize the return value. Get the most
                     * recent portfolio key. Is the most recent portfolio key
                     * null?
                     */
                    DistinguishedAccountDescription description = null;
                    final String portfolioKey = getPortfolioKey();
                    if (null == portfolioKey) {

                        /*
                         * The most recent portfolio key is null. Log this as
                         * an error and return a null distinguished account
                         * description.
                         */
                        logMessage(Level.SEVERE, String.format("Cannot " +
                                "create a distinguished account without a " +
                                "corresponding distinguished portfolio at " +
                                "line %d.", lineNumber));
                        return description;
                    }

                    /*
                     * Try to find the named distinguished account using the
                     * key string. Does the string not represent any known
                     * distinguished account key?
                     */
                    final DistinguishedAccount distinguishedAccount =
                            determineKey(keyString);
                    if (null == distinguishedAccount) {

                        /*
                         * The key string does not represent any known
                         * distinguished account key. Log this as an error.
                         */
                        logMessage(Level.SEVERE, String.format("The account " +
                                "key '%s' named at line %d does not " +
                                "represent any known, distinguished " +
                                "account.", keyString, lineNumber));

                    }

                    // The string represents a known distinguished account key.
                    else {

                        /*
                         * The distinguished account is recognized. But is it
                         * the default distinguished account?
                         */
                        if (DistinguishedAccount.DEFAULT.equals(
                                distinguishedAccount)) {

                            /*
                             * The distinguished account is the default, which
                             * is not meant to be set. Log a warning.
                             */
                            logMessage(Level.WARNING, String.format("The " +
                                    "default distinguished account is not " +
                                    "meant to be set at line %d; you may " +
                                    "wish to check this.", lineNumber));
                        }

                        /*
                         * Get the institution and account number from the
                         * given value.
                         */
                        final String institution = value.getFirst();
                        final String accountNumber = value.getSecond();

                        /*
                         * Are the key elements okay according to the distinguished
                         * account library?
                         */
                        if (accountLibrary.areKeyElementsOkay(institution,
                                accountNumber)) {

                            /*
                             * The key elements are okay according to the
                             * distinguished account library. Create a new
                             * distinguished account description using the
                             * named institution, and account number converted
                             * to a long integer.
                             */
                            description =
                                    new DistinguishedAccountDescription(
                                            distinguishedAccount, new AccountKey(
                                            institution,
                                            AccountKey.parseLong(
                                                    accountNumber)), portfolioKey);

                            /*
                             * Add the new description to the distinguished account
                             * library. Log exit information.
                             */
                            accountLibrary.addDescription(description);
                            logMessage(getOrdinary(),
                                    String.format("Load of metadata for " +
                                                    "distinguished account " +
                                                    "with key '%s' and " +
                                                    "value '%s' at line %d " +
                                                    "was%s successful.",
                                            description.getKey(),
                                            description.getValue(), lineNumber,
                                            hadLineProblem() ? " not" : ""));
                        }

                        /*
                         * The key elements are not okay according to the
                         * distinguished account library. There is something
                         * wrong with the given value.
                         */
                        else {

                            // Log an error message.
                            logMessage(Level.SEVERE, String.format("There is " +
                                    "something wrong with key '%s' in its " +
                                    "distinguished account description " +
                                    "at line %d.", value, lineNumber));
                        }
                    }

                    // Return the description.
                    return description;
                }
            };

    // The element processor for portfolios
    private final ElementProcessor<DistinguishedPortfolio>
            portfolioProcessor = new ElementProcessor<>(
            DistinguishedPortfolio.class) {

        @Override
        public DistinguishedDescription<DistinguishedPortfolio, ?>
        process(@NotNull String keyString,
                @NotNull Pair<String, String> value,
                int lineNumber) {

            /*
             * Try to find the named distinguish portfolio using the key
             * string. Does the string not represent any known distinguished
             * portfolio key?
             */
            DistinguishedPortfolioDescription description = null;
            final DistinguishedPortfolio distinguishedPortfolio =
                    determineKey(keyString);
            if (null == distinguishedPortfolio) {

                /*
                 * The key string does not represent any known distinguished
                 * portfolio. Log this as an error.
                 */
                logMessage(Level.SEVERE, String.format("The portfolio key " +
                                "'%s' named at line %d does not represent any " +
                                "known, distinguished portfolio.", keyString,
                        lineNumber));
            }

            // The string represents a known distinguished portfolio key.
            else {

                /*
                 * The distinguished portfolio is recognized. But is it
                 * the default distinguished portfolio?
                 */
                if (DistinguishedPortfolio.DEFAULT.equals(
                        distinguishedPortfolio)) {

                    /*
                     * The distinguished portfolio is the default, which
                     * is not meant to be set. Log a warning.
                     */
                    logMessage(Level.WARNING, String.format("The " +
                            "default distinguished portfolio is not " +
                            "meant to be set at line %d; you may " +
                            "wish to check this.", lineNumber));
                }

                /*
                 * Get the name from the given value. Is the key element
                 * okay according to the distinguished portfolio library?
                 */
                portfolioKey = value.getSecond();
                if (portfolioLibrary.areKeyElementsOkay(portfolioKey)) {

                    /*
                     * The key element is okay according to the distinguished
                     * portfolio library. Create a new distinguished
                     * portfolio description using the named portfolio.
                     */
                    description = new DistinguishedPortfolioDescription(
                            distinguishedPortfolio, portfolioKey);

                    /*
                     * Add the new description to the distinguished portfolio
                     * library. Log exit information.
                     */
                    portfolioLibrary.addDescription(description);
                    logMessage(getOrdinary(), String.format("Load of " +
                                    "metadata for distinguished portfolio " +
                                    "with key '%s' and value '%s' at line " +
                                    "%d was%s successful.",
                            description.getKey(),
                            description.getValue(), lineNumber,
                            hadLineProblem() ? " not" : ""));
                }

                /*
                 * The key is not okay according to the distinguished
                 * portfolio library. There is something wrong with the given
                 * value.
                 */
                else {

                    // Log an error message.
                    logMessage(Level.SEVERE, String.format("There is " +
                            "something wrong with key '%s' in its " +
                            "distinguished portfolio description " +
                            "at line %d.", value, lineNumber));
                }
            }

            // Return the description.
            return description;
        }
    };

    {

        // Build out the processor map.
        processorMap.put(HoldingLineType.ACCOUNT, accountProcessor);
        processorMap.put(HoldingLineType.INSTITUTION, institutionProcessor);
        processorMap.put(HoldingLineType.PORTFOLIO, portfolioProcessor);
        processorMap.put(HoldingLineType.TICKER, tickerProcessor);
    }

    /**
     * Describes the contents of a distinguished library.
     *
     * @param type    The type descriptor for the library
     * @param keys    The keys to describe
     * @param library The distinguished library
     */
    private static <T extends Comparable<T>>
    void describeContents(@NotNull String type, @NotNull T @NotNull [] keys,
                          @NotNull DistinguishedLibrary<T, ?, ?> library) {

        /*
         * TODO: Delete this method.
         *
         * Print the date of the library. Cycle for each key.
         */
        System.out.printf("The date of the %s library is %s.%n",
                type, DateUtilities.format(library.getDate()));

        final String prefix = String.format("Value of %s:", type);
        for (T key : keys) {

            // Describe the first/next key.
            System.out.printf("%-21s key = %-30s; value = %s.%n",
                    prefix, key, library.getValue(key));
        }
    }

    /**
     * Tests this class.
     *
     * @param arguments Command line arguments
     */
    public static void main(String[] arguments) {

        /*
         * TODO: Delete this method.
         */
        try {

            // Create an element processor. Read lines from the file object.
            final ElementReader<?> processor = new DistinguishedsBuilder();
            processor.readLines();

            // Describe the contents of the distinguished portfolio library.
            describeContents("portfolio", DistinguishedPortfolio.values(),
                    DistinguishedPortfolioLibrary.getInstance());

            // Describe the contents of the distinguished institution library.
            describeContents("institution", DistinguishedInstitution.values(),
                    DistinguishedInstitutionLibrary.getInstance());

            // Describe the contents of the distinguished account library.
            describeContents("account", DistinguishedAccount.values(),
                    DistinguishedAccountLibrary.getInstance());

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor " +
                            "completed %s warning or error.%n",
                    (processor.hadFileProblem() ? "with a" : "without"));
        }

        // Catch any I/O exception that may occur.
        catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Dispatches an element processor.
     *
     * @param holdingLineType The holding line type, which governs which
     *                        processor to call
     * @param keyString       The key string
     * @param value           The value
     * @param lineNumber      The line number
     */
    private void dispatchProcessor(@NotNull HoldingLineType holdingLineType,
                                   @NotNull String keyString,
                                   @NotNull Pair<String, String> value,
                                   int lineNumber) {

        /*
         * Discover an element processor for the holding line type. Is the
         * element processor null?
         */
        final ElementProcessor<?> processor =
                processorMap.get(holdingLineType);
        if (null == processor) {

            /*
             * The element processor is null. It was intended that each holding
             * line type has an element processor, so this problem is a design
             * error. Log the error.
             */
            logMessage(Level.SEVERE, String.format("I was unable to locate " +
                            "an element processor for line type %s at line " +
                            "number %d (given key: %s; given value %s); " +
                            "there should be an element processor for each " +
                            "line type.",
                    holdingLineType, lineNumber, keyString, value));
        }

        // The element processor is not null, meaning that it was located.
        else {

            /*
             * Call the element processor, and receive a distinguished
             * description in return. Is the description null?
             */
            final DistinguishedDescription<?, ?> description =
                    processor.process(keyString, value, lineNumber);
            if (null == description) {

                // The description is null. This is an error. Log it.
                logMessage(Level.SEVERE, String.format("Could not create " +
                                "description with key '%s', and value '%s' for line " +
                                "holding type %s at line number %d.", keyString, value,
                        holdingLineType, lineNumber));
            }

            // The description is not null.
            else {

                /*
                 * Log information about successfully processing the key and
                 * value.
                 */
                logMessage(getOrdinary(), String.format("Successfully " +
                                "created description with key '%s', and " +
                                "value '%s' for line holding type %s at " +
                                "line number %d.", keyString, value,
                        holdingLineType, lineNumber));
            }
        }
    }

    @Override
    public int getMinimumFields() {
        return DistinguishedFields.values().length;
    }

    /**
     * Gets the most recent portfolio key.
     *
     * @return The most recent portfolio key
     */
    private String getPortfolioKey() {
        return portfolioKey;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "distinguished";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(
                DistinguishedsBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String @NotNull [] elements,
                                   int lineNumber) {

        // Set the line number and get the line code.
        setLineNumber(lineNumber);
        final Character lineCode = interpreter.interpret(
                elements[DistinguishedFields.LINE_TYPE.getPosition()]);

        // Determine the line type from the code. Is the line type known?
        final HoldingLineType lineType =
                tracker.getAssociation(lineCode);
        if (null == lineType) {

            // The line type is not know. Log a warning.
            logMessage(Level.WARNING, String.format("Line code '%s' is not " +
                    "recognized at line number %d in distinguished " +
                    "file.", lineCode, lineNumber));
        } else {

            // The line type is recognized. Get the key string.
            final String keyString =
                    elements[DistinguishedFields.KEY.getPosition()];

            /*
             * Create a new value. Note: the tracker is creating a 'key' for a
             * library other than one of the distinguished value libraries. For a
             * distinguished value library the key is actually a value.
             */
            final Pair<String, String> value = tracker.constructKey(lineCode,
                    elements[DistinguishedFields.VALUE.getPosition()]);

            /*
             * Dispatch the processor for the line type, key string, value and
             * line number.
             */
            dispatchProcessor(lineType, keyString, value, lineNumber);
        }
    }

    /**
     * Resets the most recent portfolio key.
     */
    private void reset() {
        portfolioKey = null;
    }

    @Override
    protected void setLineNumber(int lineNumber) {
        interpreter.setRow(lineNumber);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method and reset the parent tracker.
        super.startProcessing();
        tracker.reset();

        // Set the date in account library and the institution library.
        setDate(accountLibrary);
        setDate(institutionLibrary);

        /*
         * Set the date in the portfolio library and reset the most recent
         * portfolio key.
         */
        setDate(portfolioLibrary);
        reset();
    }

    private enum DistinguishedTickers {

        /*
         * This enumerator is a stand-in for distinguished tickers, which do
         * not yet exist.
         */
        DEFAULT
    }

    private abstract static class ElementProcessor<T extends Enum<T>> {

        // The class of the element processor
        private final Class<T> t;

        /**
         * Constructs an element processor.
         *
         * @param t The class of the element processor
         */
        public ElementProcessor(@NotNull Class<T> t) {
            this.t = t;
        }

        /**
         * Determines a key from its string representation.
         *
         * @param keyString The string representation of a key
         * @return The key determined from its string representation, or null
         * if the string does not represent any key of the type of this class
         */
        protected T determineKey(@NotNull String keyString) {

            // Declare the result.
            T result;
            try {

                // Try to parse the result from the argument.
                result = T.valueOf(t, keyString);
            }

            /*
             * Catch any illegal argument exception that may occur (meaning:
             * the argument does not represent any key of the type of this
             * class). Initialize the result to null;
             */ catch (@NotNull IllegalArgumentException exception) {
                result = null;
            }

            // Return the result.
            return result;
        }

        /**
         * Processes elements.
         *
         * @param keyString  A string representation of the key
         * @param value      The value for the key
         * @param lineNumber The line number from which the elements were read
         * @return The description created by the processor
         */
        public abstract DistinguishedDescription<T, ?> process(
                @NotNull String keyString, @NotNull Pair<String, String> value,
                int lineNumber);
    }
}
