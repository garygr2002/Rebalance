package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistinguishedsBuilder extends ElementReader {

    // The distinguished account library instance
    private final DistinguishedAccountLibrary accountLibrary =
            DistinguishedAccountLibrary.getInstance();

    // The element processor for accounts
    private final ElementProcessor<DistinguishedAccounts> accountProcessor =
            new ElementProcessor<>(DistinguishedAccounts.class) {

                @Override
                public DistinguishedDescription<DistinguishedAccounts, ?>
                process(@NotNull String keyString,
                        @NotNull Pair<String, String> value,
                        int lineNumber) {

                    /*
                     * Try to find the named distinguished account using the
                     * key string. Does the string not represent any known
                     * distinguished account key?
                     */
                    DistinguishedAccountDescription description = null;
                    final DistinguishedAccounts distinguishedAccount =
                            determineKey(keyString);
                    if (null == distinguishedAccount) {

                        /*
                         * The key string does not represent any known
                         * distinguished account. Log this as an error.
                         */
                        logMessage(Level.SEVERE, String.format("The account " +
                                        "key '%s' named at line %d does not " +
                                        "represent any known, distinguished account.",
                                keyString, lineNumber));

                    }

                    // The string represents a known distinguished account key.
                    else {

                        /*
                         * The distinguished account is recognized. But is it
                         * the default distinguished account?
                         */
                        if (DistinguishedAccounts.DEFAULT.equals(
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
                                                    accountNumber)));

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

    // The distinguished institution library instance
    private final DistinguishedInstitutionLibrary institutionLibrary =
            DistinguishedInstitutionLibrary.getInstance();

    // The element processor for institutions
    private final ElementProcessor<DistinguishedInstitutions>
            institutionProcessor = new ElementProcessor<>(
            DistinguishedInstitutions.class) {

        @Override
        public DistinguishedDescription<DistinguishedInstitutions, ?>
        process(@NotNull String keyString,
                @NotNull Pair<String, String> value,
                int lineNumber) {

            /*
             * Try to find the named distinguish institution using the key
             * string. Does the string not represent any known distinguished
             * institution key?
             */
            DistinguishedInstitutionDescription description = null;
            final DistinguishedInstitutions distinguishedInstitution =
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
                if (DistinguishedInstitutions.DEFAULT.equals(
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

    // The distinguished portfolio library instance
    private final DistinguishedPortfolioLibrary portfolioLibrary =
            DistinguishedPortfolioLibrary.getInstance();

    // The element processor for portfolios
    private final ElementProcessor<DistinguishedPortfolios>
            portfolioProcessor = new ElementProcessor<>(
            DistinguishedPortfolios.class) {

        @Override
        public DistinguishedDescription<DistinguishedPortfolios, ?>
        process(@NotNull String keyString,
                @NotNull Pair<String, String> value,
                int lineNumber) {

            /*
             * Try to find the named distinguish portfolio using the key
             * string. Does the string not represent any known distinguished
             * portfolio key?
             */
            DistinguishedPortfolioDescription description = null;
            final DistinguishedPortfolios distinguishedPortfolio =
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
                if (DistinguishedPortfolios.DEFAULT.equals(
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
                final String portfolio = value.getSecond();
                if (portfolioLibrary.areKeyElementsOkay(portfolio)) {

                    /*
                     * The key element is okay according to the distinguished
                     * portfolio library. Create a new distinguished
                     * portfolio description using the named portfolio.
                     */
                    description = new DistinguishedPortfolioDescription(
                            distinguishedPortfolio, portfolio);

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
    private final ParentTracker tracker = new ParentTracker();

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
    private static <T> void describeContents(@NotNull String type,
                                             @NotNull T[] keys,
                                             @NotNull DistinguishedLibrary<T, ?, ?>
                                                     library) {

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
            final DistinguishedsBuilder processor = new DistinguishedsBuilder();
            processor.readLines();

            // Describe the contents of the distinguished portfolio library.
            describeContents("portfolio", DistinguishedPortfolios.values(),
                    DistinguishedPortfolioLibrary.getInstance());

            // Describe the contents of the distinguished institution library.
            describeContents("institution", DistinguishedInstitutions.values(),
                    DistinguishedInstitutionLibrary.getInstance());

            // Describe the contents of the distinguished account library.
            describeContents("account", DistinguishedAccounts.values(),
                    DistinguishedAccountLibrary.getInstance());

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor " +
                            "completed %s warning or error.%n",
                    (processor.hadFileProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Processes a code.
     *
     * @param code The code
     * @return A processed code
     */
    private static @NotNull Character processCode(@NotNull String code) {
        return code.charAt(0);
    }

    /**
     * Processes a key string element.
     *
     * @param keyString The key string element
     * @return A processed key string element
     */
    private static @NotNull String processKeyString(
            @NotNull String keyString) {

        // Currently we just return the argument.
        return keyString;
    }

    /**
     * Processes a value element.
     *
     * @param value The value element
     * @return A processed value element
     */
    private static @NotNull String processValue(@NotNull String value) {

        // Currently we just return the argument.
        return value;
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
        } else {

            /*
             * Element processor located. Call it, and receive a distinguished
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
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(
                DistinguishedsBuilder.class.getCanonicalName());
    }

    @Override
    public int getMinimumFields() {
        return DistinguishedFields.values().length;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "distinguished";
    }

    @Override
    protected void processElements(@NotNull String[] elements, int lineNumber) {

        // Get the line code.
        final Character lineCode = processCode(preprocessField(
                elements[DistinguishedFields.LINE_TYPE.getPosition()]));

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
            final String keyString = processKeyString(preprocessField(
                    elements[DistinguishedFields.KEY.getPosition()]));

            /*
             * Create a new value. Note: the tracker is creating a 'key' for a
             * library other than one of the distinguished value libraries. For a
             * distinguished value library the key is actually a value.
             */
            final Pair<String, String> value = tracker.constructKey(lineCode,
                    processValue(preprocessField(
                            elements[DistinguishedFields.VALUE.getPosition()])));

            /*
             * Dispatch the processor for the line type, key string, value and
             * line number.
             */
            dispatchProcessor(lineType, keyString, value, lineNumber);
        }
    }

    @Override
    protected void startProcessing() {

        /*
         * Call the superclass method, and set the date in each of the
         * libraries.
         */
        super.startProcessing();
        setDate(accountLibrary);
        setDate(institutionLibrary);
        setDate(portfolioLibrary);
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
         * if the string does not represent any key of the type type of this
         * class
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
             */
            catch (@NotNull IllegalArgumentException exception) {
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
