package com.garygregg.rebalance.ticker;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.interpreter.CodeInterpreter;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.IntegerInterpreter;
import com.garygregg.rebalance.interpreter.TickerInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TickersBuilder extends ElementReader<TickerDescription> {

    // A ticker must contain exactly one of these base types
    private static final FundType[] base = {FundType.BOND, FundType.CASH,
            FundType.NEW_FLAIR_OUT_GROWTH, FundType.NOT_A_FUND,
            FundType.REAL_ESTATE, FundType.STOCK};

    // A list for base fund types
    private static final List<FundType> baseList = Arrays.asList(base);

    // A map of character codes to fund types
    private static final Map<Character, FundType> baseTypeMap = new HashMap<>();

    // A bond ticker may contain one of these bond subtypes
    private static final FundType[] bond = {FundType.CORPORATE,
            FundType.INFLATION, FundType.MORTGAGE, FundType.FOREIGN,
            FundType.TREASURY};

    // A list for bond fund subtypes
    private static final List<FundType> bondList = Arrays.asList(bond);

    // Non-bond tickers must not contain these bond subtypes
    private static final FundType[] bondMinusForeign = {FundType.CORPORATE,
            FundType.INFLATION, FundType.MORTGAGE};

    // A list for bond-minus-foreign fund subtypes
    private static final List<FundType> bondMinusForeignList =
            Arrays.asList(bondMinusForeign);

    // A cash ticker may contain one of these cash subtypes
    private static final FundType[] cash = {FundType.TREASURY};

    // A list for cash fund subtypes
    private static final List<FundType> cashList = Arrays.asList(cash);

    // The logger level for consistency checks
    private static final Level inconsistencyLevel = Level.WARNING;

    // Checks for zero
    private static final CountChecker none = count -> 0 >= count;

    // Base fund types without bond
    private static final FundType[] notBond = {FundType.CASH,
            FundType.NEW_FLAIR_OUT_GROWTH, FundType.NOT_A_FUND,
            FundType.REAL_ESTATE, FundType.STOCK};

    // A list for base without bond fund type
    private static final List<FundType> notBondList = Arrays.asList(notBond);

    // Base fund types without cash
    private static final FundType[] notCash = {FundType.BOND,
            FundType.NEW_FLAIR_OUT_GROWTH, FundType.NOT_A_FUND,
            FundType.REAL_ESTATE, FundType.STOCK};

    // A list for base without cash fund type
    private static final List<FundType> notCashList = Arrays.asList(notCash);

    // Base types without new-flair-out-growth
    private static final FundType[] notNFOG = {FundType.BOND, FundType.CASH,
            FundType.NOT_A_FUND, FundType.REAL_ESTATE, FundType.STOCK};

    // A list for base types without new-flair-out-growth fund type
    private static final List<FundType> notNFOGList = Arrays.asList(notNFOG);

    // Base types without real estate
    private static final FundType[] notRealEstate = {FundType.BOND,
            FundType.CASH, FundType.NEW_FLAIR_OUT_GROWTH, FundType.NOT_A_FUND,
            FundType.STOCK};

    // A list for base types without real estate fund type
    private static final List<FundType> notRealEstateList =
            Arrays.asList(notRealEstate);

    // Base types without stock
    private static final FundType[] notStock = {FundType.BOND, FundType.CASH,
            FundType.NEW_FLAIR_OUT_GROWTH, FundType.NOT_A_FUND,
            FundType.REAL_ESTATE};

    // A list for base types without stock fund type
    private static final List<FundType> notStockList = Arrays.asList(notStock);

    // Checks for only one
    private static final CountChecker onlyOne = count -> 1 == count;

    // A stock ticker may contain no more than one of these
    private static final FundType[] size = {FundType.LARGE, FundType.MEDIUM,
            FundType.NOT_LARGE, FundType.SMALL};

    // A list for stock subtypes involving company size
    private static final List<FundType> sizeList = Arrays.asList(size);

    // A stock ticker must contain exactly one of these
    private static final FundType[] valuation = {FundType.GROWTH,
            FundType.GROWTH_AND_VALUE, FundType.VALUE};

    // A list for stock subtypes that involve company valuation
    private static final List<FundType> valuationList =
            Arrays.asList(valuation);

    // What zero currency looks like
    private static final Currency zero = Currency.getZero();

    // Checks for zero or one
    private static final CountChecker zeroOrOne = count -> 2 > count;

    static {

        // Build the base fund type map.
        for (FundType type : FundType.values()) {
            baseTypeMap.put(type.getCode(), type);
        }
    }

    // Our code interpreter
    private final CodeInterpreter codeInterpreter = new CodeInterpreter();

    // A map of base fund types to consistency checkers
    private final Map<FundType, ConsistencyChecker> consistencyCheckers =
            new HashMap<>();

    // The ETF ticker factory
    private final TickerFactory etfFactory = (ticker, number, name, minimum, balanceRounding, lineNumber) -> {

        // Check for non-zero minimum.
        if (zero.areNotEqual(minimum)) {

            // Non-zero minimum detected. Log a warning.
            logMessage(Level.WARNING, String.format("Non-zero minimums " +
                            "for ETFs are not currently supported - received %f " +
                            "at line number %d; using a default.",
                    minimum, lineNumber));
        }

        // Return a new ETF description with a default minimum.
        return new ETFDescription(ticker, number, name, minimum, balanceRounding);
    };

    // A map of character codes to ticker factories
    private final Map<Character, TickerFactory> factoryMap = new HashMap<>();

    // The fund ticker factory
    private final TickerFactory fundFactory =
            (ticker, number, name, minimum, balanceRounding, lineNumber) ->
                    new FundDescription(ticker, number, name, minimum,
                            balanceRounding);

    // A field processor for fund types
    private final FieldProcessor<TickerDescription> fundTypeProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {

                    // Process the fund type. Is the fund type null?
                    final FundType fundType =
                            baseTypeMap.get(codeInterpreter.interpret(field));
                    if (null == fundType) {

                        // The fund type is null. Issue a warning, and skip the code.
                        logMessage(Level.WARNING, String.format("No fund " +
                                        "type for unrecognized code '%s' at " +
                                        "line number %d in the ticker file; " +
                                        "skipping this code.",
                                field, 0));
                    }

                    // Add to the target any type other than not-a-fund.
                    else if (!FundType.NOT_A_FUND.equals(fundType)) {
                        getTarget().addType(fundType);
                    }
                }
            };

    // The code library instance
    private final TickerLibrary library = TickerLibrary.getInstance();

    // All tickers except NOT_A_FUND must contain one of these
    private final FundType[] location = {FundType.DOMESTIC, FundType.FOREIGN};

    // A list of location subtypes
    private final List<FundType> locationList = Arrays.asList(location);

    // The cash checker
    private final ConsistencyChecker cashChecker = this::checkCash;

    // The bond checker
    private final ConsistencyChecker bondChecker = this::checkBond;

    // The new-flair-out growth checker
    private final ConsistencyChecker nfogChecker = this::checkNFOG;

    // The real estate checker
    private final ConsistencyChecker realEstateChecker = this::checkRealEstate;

    // Our minimum balance interpreter
    private final DoubleInterpreter minimumInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Ticker " +
                                    "minimum '%s' at line number %d in " +
                                    "ticker file cannot be parsed; using %s.",
                            string, getRow(), Currency.format(defaultValue)));
                }
            };

    // The not-a-fund checker
    private final ConsistencyChecker notAFundChecker = this::checkNotAFund;

    // The not-considered factory
    private final TickerFactory notConsideredFactory =
            (ticker, number, name, minimum, balanceRounding, lineNumber) ->
                    new NotConsideredDescription(ticker, number, name, minimum,
                            balanceRounding);

    // Our fund number interpreter
    private final IntegerInterpreter numberInterpreter =
            new IntegerInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Integer defaultValue) {
                    logMessage(Level.WARNING, String.format("Ticker number " +
                                    "'%s' at line number %d in ticker file " +
                                    "cannot be parsed; using %s.", string,
                            getRow(), defaultValue));
                }
            };

    // Our balance rounding interpreter
    private final DoubleInterpreter roundingInterpreter =
            new DoubleInterpreter() {

                @Override
                public Double interpret(@NotNull String string,
                                        Double defaultValue) {

                    /*
                     * Use the default value if the string is empty, otherwise
                     * use superclass to interpret the string. Get the minimum
                     * number of shares.
                     */
                    Double result = string.isEmpty() ? defaultValue :
                            super.interpret(string, defaultValue);
                    final Shares minimum = Shares.getMinimum();

                    /*
                     * Get the value of the minimum number of shares. Is the
                     * interpreted balance rounding not null, and is it less
                     * than the minimum value?
                     */
                    final double minimumValue = minimum.getValue();
                    if ((null != result) && (result < minimumValue)) {

                        /*
                         * The parsed balance rounding is not null, and it is
                         * less than the minimum value. Log information saying
                         * that the minimum will be used for re-balancing
                         * purposes.
                         */
                        logMessage(getExtraordinary(),
                                String.format("Balance rounding %s at line " +
                                                "number %d is less than " +
                                                "minimum of %s; the " +
                                                "preference is noted, but " +
                                                "the minimum will be used.",
                                        result, getRow(), minimum));

                        // Set the result to the minimum value.
                        result = minimumValue;
                    }

                    // Return the result.
                    return result;
                }

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Balance " +
                                    "rounding '%s' at line number %d in " +
                                    "the ticker file cannot be parsed; " +
                                    "using %s.", string, getRow(),
                            Shares.format(defaultValue)));
                }
            };

    // The stock checker
    private final ConsistencyChecker stockChecker = this::checkStock;

    // The stock ticker factory
    private final TickerFactory stockFactory = (ticker, number, name, minimum, balanceRounding, lineNumber) -> {

        // Check for non-zero minimum.
        if (zero.areNotEqual(minimum)) {

            // Non-zero minimum detected. Log a warning.
            logMessage(Level.WARNING, String.format("Non-zero minimums " +
                            "for stocks are not currently supported - " +
                            "received %f at line number %d; using a default.",
                    minimum, lineNumber));
        }

        // Return a new stock description with a default minimum.
        return new StockDescription(ticker, number, name, minimum, balanceRounding);
    };

    // Our ticker interpreter
    private final TickerInterpreter tickerInterpreter =
            new TickerInterpreter();

    {
        // Populate the consistency checker map.
        consistencyCheckers.put(FundType.BOND, bondChecker);
        consistencyCheckers.put(FundType.CASH, cashChecker);
        consistencyCheckers.put(FundType.NEW_FLAIR_OUT_GROWTH, nfogChecker);
        consistencyCheckers.put(FundType.NOT_A_FUND, notAFundChecker);
        consistencyCheckers.put(FundType.REAL_ESTATE, realEstateChecker);
        consistencyCheckers.put(FundType.STOCK, stockChecker);

        // Populate the factory map.
        factoryMap.put(TickerLibrary.getFundCode(), fundFactory);
        factoryMap.put(TickerLibrary.getNotConsideredCode(),
                notConsideredFactory);
        factoryMap.put(TickerLibrary.getStockCode(), stockFactory);
        factoryMap.put(TickerLibrary.getETFCode(), etfFactory);

        // Cycle for each subcode field.
        for (int i = TickerFields.SUBCODE_1.getPosition();
             i <= TickerFields.SUBCODE_4.getPosition();
             ++i) {

            /*
             * Add the fund type field processor for the first/next subcode
             * field.
             */
            addFieldProcessor(i, fundTypeProcessor);
        }
    }

    /**
     * Counts the number of fund types that a ticker description contains.
     *
     * @param description The ticker description
     * @param iterator    An iterator of fund types
     * @return A count of the number of fund types that the ticker
     * description contains
     */
    private static int count(@NotNull TickerDescription description,
                             @NotNull Iterator<FundType> iterator) {

        // Declare and initialize the result. Cycle while fund types exist.
        int result = 0;
        while (iterator.hasNext()) {

            /*
             * Increment the result if the description contains the first/next
             * fund type.
             */
            if (description.hasType(iterator.next())) {
                ++result;
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the logging level used for inconsistency reports.
     *
     * @return The logging level used for inconsistency reports
     */
    public static Level getInconsistencyLevel() {
        return inconsistencyLevel;
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
            final ElementReader<?> processor = new TickersBuilder();
            processor.readLines();

            // The ticker library should now be populated. Print its date.
            final TickerLibrary library = TickerLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each ticker description in the library.
            for (TickerDescription description : library.getCatalog()) {

                // Display statistics for the first/next ticker description.
                System.out.printf("Ticker number: %5s; " +
                                "Name: %45s; " +
                                "Minimum is: %12s; " +
                                "Preferred rounding is : %7s; " +
                                "Has stocks: %5s; " +
                                "Has bonds: %5s; " +
                                "Has cash: %5s; " +
                                "Has real estate: %5s%n",
                        description.getTicker(),
                        description.getName(),
                        description.getMinimum().toString(),
                        description.getBalanceRounding().toString(),
                        description.hasType(FundType.STOCK),
                        description.hasType(FundType.BOND),
                        description.hasType(FundType.CASH),
                        description.hasType(FundType.REAL_ESTATE));
            }

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor " +
                            "completed %s warning or error.%n",
                    (processor.hadFileProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Checks a ticker description.
     *
     * @param description A ticker description
     * @param types       Counts the types in this list that are contained in
     *                    the ticker description
     * @param checker     A count checker for this method to use
     * @return True if the check passes, false otherwise
     */
    private boolean check(@NotNull TickerDescription description,
                          @NotNull List<FundType> types,
                          @NotNull CountChecker checker,
                          @NotNull String message) {

        // Check the count. Is the count not okay?
        final boolean result = checker.check(count(description,
                types.iterator()));
        if (!result) {

            // The count is not okay. Log a warning with the given message.
            logMessage(getInconsistencyLevel(), message);
        }

        // Return the result.
        return result;
    }

    /**
     * Checks a bond ticker.
     *
     * @param description A bond ticker
     * @return True if the bond ticker is consistent, false otherwise
     */
    private boolean checkBond(@NotNull TickerDescription description) {

        /*
         * Declare the target fund type (bond). Get the ticker from the
         * description.
         */
        final FundType target = FundType.BOND;
        final String ticker = description.getTicker();

        // Check for non-target base fund types.
        boolean result = check(description, notBondList, none, String.format(
                "Ticker '%s' of %s type contains other base types.",
                ticker, target));

        // Check for no more than one bond subtype.
        result = check(description, bondList, zeroOrOne,
                String.format("Ticker '%s' of %s type contains more than " +
                        "one subtype.", ticker, target))
                && result;

        // High-yield bond tickers must also be corporate.
        if ((!description.hasType(FundType.CORPORATE) &&
                description.hasType(FundType.HIGH))) {

            // Non-corporate high-yield ticker.
            logMessage(Level.WARNING, String.format("Ticker '%s' of %s type " +
                    "contains a high-yield subtype without a corporate " +
                    "subtype.", ticker, target));
            result = false;
        }

        // Mortgage bond tickers must not be short-term.
        if (description.hasType(FundType.MORTGAGE) &&
                description.hasType(FundType.SHORT)) {

            // Inflation protected, or mortgage security is also short term.
            logMessage(Level.WARNING, String.format("Ticker '%s' of %s type " +
                    "contains a mortgage subtype, but is also " +
                    "short-term.", ticker, target));
            result = false;
        }

        /*
         * Check for a location subtype and no stock subtypes. Return the
         * result.
         */
        result = checkLocation(description, target) && result;
        return checkNoStock(description, target) && result;
    }

    /**
     * Checks a cash ticker.
     *
     * @param description A cash ticker
     * @return True if the cash ticker is consistent, false otherwise
     */
    private boolean checkCash(@NotNull TickerDescription description) {

        /*
         * Declare the target fund type (cash). Get the ticker from the
         * description.
         */
        final FundType target = FundType.CASH;
        final String ticker = description.getTicker();

        // Check for non-target base fund types.
        boolean result = check(description, notCashList, none, String.format(
                "Ticker '%s' of %s type contains other base types.",
                ticker, target));

        // Check for no more than one cash subtype.
        result = check(description, cashList, zeroOrOne,
                String.format("Ticker '%s' of %s type contains more than " +
                        "one subtype.", ticker, target))
                && result;

        /*
         * Check for a location subtype, and no bond or stock subtypes. Return
         * the result.
         */
        result = checkLocation(description, target) && result;
        result = checkNoBond(description, target) && result;
        return checkNoStock(description, target) && result;
    }

    /**
     * Checks the consistency of a ticker description.
     *
     * @param description The ticker description for which to check consistency
     * @return True if the description is consistent
     */
    private boolean checkConsistency(@NotNull TickerDescription description) {

        // Declare and initialize the result. Declare other local variables.
        boolean result = false;
        ConsistencyChecker checker = null;
        FundType type;

        /*
         * Cycle through the fund type base types until we find one that is
         * contained in the ticker description.
         */
        final Iterator<FundType> iterator = baseList.iterator();
        while ((!result) && iterator.hasNext()) {

            /*
             * Try to find a consistency checker for the first/next fund type
             * base type. Is there no such checker?
             */
            type = iterator.next();
            checker = consistencyCheckers.get(type);
            if (null == checker) {

                /*
                 * There is no consistency checker for this type. Log a
                 * warning.
                 */
                logMessage(Level.WARNING, String.format("No consistency " +
                        "checker found for base fund type %s; you may want " +
                        "to check that.", type));
            }

            // Determine if the description has this base fund type.
            result = description.hasType(type);
        }

        /*
         * Found a base fund type, and a consistency checker. Perform the base
         * type consistency check.
         */
        if (result && (null != checker)) {
            result = checker.check(description);
        }

        // Return the result.
        return result;
    }

    /**
     * Checks a description to ensure that it has exactly one location subtype.
     *
     * @param description The ticker description for which to check consistency
     * @param target      The target base fund type
     * @return True if the description contains exactly one location subtype,
     * false otherwise
     */
    private boolean checkLocation(TickerDescription description,
                                  FundType target) {

        // Check for only one location subtype.
        return check(description, locationList, onlyOne,
                String.format("Ticker '%s' of %s type is neither domestic " +
                                "nor foreign.", description.getTicker(),
                        target));
    }

    /**
     * Checks a new-flair-out growth ticker.
     *
     * @param description A new-flair-out growth ticker
     * @return True if the new-flair-out growth ticker is consistent, false
     * otherwise
     */
    private boolean checkNFOG(@NotNull TickerDescription
                                      description) {

        /*
         * Declare the target fund type (new flair-out-growth). Create a list
         * of base fund types that are not new flair-out-growth.
         */
        // Declare the target fund type (new flair-out-growth).
        final FundType target = FundType.NEW_FLAIR_OUT_GROWTH;
        final String ticker = description.getTicker();

        // Check for non-target base fund types.
        boolean result = check(description, notNFOGList, none, String.format(
                "Ticker '%s' of %s type contains other base types.",
                ticker, target));

        /*
         * Check for a location subtype, and no bond or stock subtypes. Return
         * the result.
         */
        result = checkLocation(description, target) && result;
        result = checkNoBond(description, target) && result;
        return checkNoStock(description, target) && result;
    }

    /**
     * Checks to be certain that a description contains no bond subtypes.
     *
     * @param description A bond ticker
     * @param target      The target base fund type
     * @return True if the description checked out okay, false otherwise
     */
    private boolean checkNoBond(@NotNull TickerDescription description,
                                FundType target) {

        // Get the ticker from the description. Check for no bond subtypes.
        final String ticker = description.getTicker();
        boolean result = check(description, bondMinusForeignList,
                none, String.format("Ticker '%s' of %s type contains one or " +
                        "more bond types.", ticker, target));

        // Neither a high-yield nor short-term designation may be present.
        if (description.hasType(FundType.HIGH) ||
                description.hasType(FundType.SHORT)) {

            // High-yield or short-term designation is present.
            logMessage(getInconsistencyLevel(), String.format("Ticker '%s' " +
                    "of %s type contains a high-yield or short-term " +
                    "designation.", ticker, target));
            result = false;
        }

        // Return the result.
        return result;
    }

    /**
     * Checks to be certain that a description contains no stock subtypes.
     *
     * @param description A bond ticker
     * @param target      The target base fund type
     * @return True if the description checked out okay, false otherwise
     */
    private boolean checkNoStock(@NotNull TickerDescription description,
                                 FundType target) {

        /*
         * Get the ticker from the description. Check for no stock size
         * subtypes.
         */
        final String ticker = description.getTicker();
        boolean result = check(description, sizeList, none,
                String.format("Ticker '%s' of %s type contains one or more " +
                        "stock size types.", ticker, target));

        // Check for no stock valuation subtypes, and return.
        result = check(description, valuationList, none,
                String.format("Ticker '%s' of %s type contains one or more " +
                        "stock valuation types.", ticker, target)) && result;
        return result;
    }

    /**
     * Checks a not-a-fund ticker.
     *
     * @param description A not-a-fund ticker
     * @return True if the not-a-fund ticker is consistent, false otherwise
     */
    private boolean checkNotAFund(@NotNull TickerDescription description) {

        // This fund type should not be present at all.
        logMessage(getInconsistencyLevel(), String.format("Ticker '%s' " +
                        "should not contain a %s fund type.",
                description.getTicker(), FundType.NOT_A_FUND));
        return false;
    }

    /**
     * Checks a real estate ticker.
     *
     * @param description A real estate ticker
     * @return True if the real estate ticker is consistent, false otherwise
     */
    private boolean checkRealEstate(@NotNull TickerDescription description) {

        /*
         * Declare the target fund type (real estate). Get the ticker from the
         * description.
         */
        final FundType target = FundType.REAL_ESTATE;
        final String ticker = description.getTicker();

        // Check for non-target base fund types.
        boolean result = check(description, notRealEstateList, none,
                String.format("Ticker '%s' of %s type contains other base " +
                        "types.", ticker, target));

        /*
         * Check for a location subtype, and no bond or stock subtypes. Return
         * the result.
         */
        result = checkLocation(description, target) && result;
        result = checkNoBond(description, target) && result;
        return checkNoStock(description, target) && result;
    }

    /**
     * Checks a stock ticker.
     *
     * @param description A stock ticker
     * @return True if the stock ticker is consistent, false otherwise
     */
    private boolean checkStock(@NotNull TickerDescription description) {

        /* --
         *
         * No other base types
         *
         * --
         */

        /*
         * Declare the target fund type (stock). Get the ticker from the
         * description.
         */
        final FundType target = FundType.STOCK;
        final String ticker = description.getTicker();

        // Check for non-target base fund types.
        boolean result = check(description, notStockList, none, String.format(
                "Ticker '%s' of %s type contains other base types.",
                ticker, target));

        /* --
         *
         * Domestic or foreign subtype
         *
         * --
         */

        // Stock tickers must be domestic, or foreign.
        if (!(description.hasType(FundType.DOMESTIC) ||
                description.hasType(FundType.FOREIGN))) {

            // Stock ticker neither domestic nor foreign.
            logMessage(Level.WARNING, String.format("Ticker '%s' of %s type " +
                            "is neither domestic nor foreign.",
                    ticker, target));
            result = false;
        }

        /* --
         *
         * At most one size subtype
         *
         * --
         */

        // Check for no more than one size subtype
        result = check(description, sizeList, zeroOrOne,
                String.format("Ticker '%s' of %s type contains more than " +
                        "one size subtype.", ticker, target)) && result;

        /* --
         *
         * Exactly one valuation subtype
         *
         * --
         */

        // Check for exactly one valuation subtype
        result = check(description, valuationList, onlyOne,
                String.format("Ticker '%s' of %s type does not contains a " +
                        "valuation subtype.", ticker, target)) && result;

        /* --
         *
         * No bond subtypes
         *
         * --
         */

        // Check for no bond subtypes, and return the result.
        return checkNoBond(description, target) && result;
    }

    @Override
    public int getMinimumFields() {
        return 5;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "ticker";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(TickersBuilder.class.getCanonicalName());
    }

    @Override
    public void processElements(String[] elements, int lineNumber) {

        // Set the line number, and get the line code.
        setLineNumber(lineNumber);
        final Character tickerCode = codeInterpreter.interpret(
                elements[TickerFields.CODE.getPosition()]);

        /*
         * Get the ticker factory based on the ticker type. Is a factory
         * not available for the given type?
         */
        final TickerFactory factory = factoryMap.get(tickerCode);
        if (null == factory) {

            /*
             * A factory is not available for the given ticker type. Log a
             * warning and skip this line.
             */
            logMessage(Level.WARNING, String.format("No ticker factory " +
                            "available for code '%c' at line number %d; " +
                            "skipping creation of a fund description.",
                    tickerCode, lineNumber));
        }

        // A factory is available for the given ticker type.
        else {

            /*
             * Use the factory for the given ticker type to create a new ticker
             * description with the interpreted ticker, number, name, minimum
             * investment, and preferred rounding.
             */
            final TickerDescription description = factory.createDescription(

                    // Ticker...
                    tickerInterpreter.interpret(
                            elements[TickerFields.TICKER.getPosition()]),

                    // ...number...
                    numberInterpreter.interpret(
                            elements[TickerFields.NUMBER.getPosition()],
                            null),

                    // ...name and minimum investment...
                    elements[TickerFields.NAME.getPosition()],
                    minimumInterpreter.interpret(
                            elements[TickerFields.MINIMUM.getPosition()], 0.),

                    // ... and preferred rounding.
                    roundingInterpreter.interpret(elements[
                                    TickerFields.PREFERRED_ROUNDING.getPosition()],
                            Shares.getMinimum().getValue()), lineNumber);

            /*
             * Check the key of the description against the default key in the
             * library. Try to add the new ticker description, receiving any
             * existing description with the same key.
             */
            checkKey(library, description, lineNumber);
            if (null != library.addDescription(description)) {

                /*
                 * Log a message describing where the duplicate account description
                 * occurs.
                 */
                logMessage(getExtraordinary(), String.format("Replacing " +
                        "ticker with symbol '%s' at line number %d in " +
                        "ticker file.", description.getTicker(), lineNumber));
            }

            /*
             * Set the target in the field processors. Get the number of line
             * elements and the number of account fields.
             */
            setTarget(description);
            final int elementsLength = elements.length;
            final int numberOfTickerFields = TickerFields.values().length;

            /*
             * The fields-to-process is the minimum of the number of line elements
             * and the number of ticker fields.
             */
            final int fieldsToProcess = Math.min(elementsLength,
                    numberOfTickerFields);

            /*
             * Log a warning if the fields-to-process is less than the number of
             * line elements.
             */
            if (fieldsToProcess < elementsLength) {
                logMessage(Level.WARNING, String.format("There are %d ticker " +
                                "line elements but only %d ticker fields at " +
                                "line number %d; you might want to check " +
                                "that.", elementsLength, numberOfTickerFields,
                        lineNumber));
            }

            /*
             * Or log a different warning if the fields-to-process is less than the
             * number of ticker fields.
             */
            else if (fieldsToProcess < numberOfTickerFields) {
                logMessage(Level.WARNING, String.format("There are %d " +
                                "ticker fields but only %d target line " +
                                "elements at line number %d; you might want " +
                                "to check that.",
                        numberOfTickerFields, elementsLength, lineNumber));
            }

            // Cycle for each remaining field-to-process.
            for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {

                // Process the first/next field.
                processField(i, elements[i]);
            }

            // Log some exit information.
            logMessage(getOrdinary(), String.format("Load of metadata for " +
                            "ticker with symbol '%s' at line %d was%s " +
                            "successful.", description.getTicker(), lineNumber,
                    hadLineProblem() ? " not" : ""));
        }
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the code interpreter, the ticker
         * interpreter and the number interpreter.
         */
        codeInterpreter.setRow(lineNumber);
        tickerInterpreter.setRow(lineNumber);
        numberInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the minimum balance interpreter
         * and the balance rounding interpreter.
         */
        minimumInterpreter.setRow(lineNumber);
        roundingInterpreter.setRow(lineNumber);
    }

    @Override
    protected void setTarget(@NotNull TickerDescription description) {
        fundTypeProcessor.setTarget(description);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }

    @Override
    protected void stopProcessing() {

        // Check the consistency of each ticker description.
        boolean result = true;
        for (TickerDescription description : library.getCatalog()) {

            // All checks must be consistent to report no error.
            result = checkConsistency(description) && result;
        }

        // Log an informational message, and call the superclass method.
        logMessage(getExtraordinary(), String.format("Checks of the fund " +
                "types in the ticker library indicated %s. ", result ?
                "no inconsistencies" : "one or more inconsistencies"));
        super.stopProcessing();
    }

    private interface ConsistencyChecker {

        /**
         * Checks a ticker description for consistency.
         *
         * @param description The ticker description to check
         * @return True if the ticker is consistent, false otherwise
         */
        boolean check(@NotNull TickerDescription description);
    }

    private interface CountChecker {

        /**
         * Checks to see if a count is okay.
         *
         * @param count A count
         * @return True if the count is okay, false otherwise
         */
        boolean check(int count);
    }

    private interface TickerFactory {

        /**
         * Creates a ticker description.
         *
         * @param ticker          The ticker tag
         * @param number          The number
         * @param name            The name
         * @param minimum         The minimum investment in the ticker
         * @param lineNumber      The line number from the builder read the
         *                        ticker information
         * @param balanceRounding The preferred round number of shares to hold
         * @return A ticker description with the described features
         */
        TickerDescription createDescription(@NotNull String ticker,
                                            @NotNull Integer number,
                                            @NotNull String name,
                                            double minimum,
                                            double balanceRounding,
                                            int lineNumber);
    }
}
