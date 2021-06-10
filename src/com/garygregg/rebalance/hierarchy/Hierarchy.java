package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.account.AccountsBuilder;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.ICountable;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.holding.HoldingDescription;
import com.garygregg.rebalance.holding.HoldingLibrary;
import com.garygregg.rebalance.holding.HoldingsBuilder;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.portfolio.PortfoliosBuilder;
import com.garygregg.rebalance.ticker.TickerDescription;
import com.garygregg.rebalance.ticker.TickerLibrary;
import com.garygregg.rebalance.ticker.TickersBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Hierarchy {

    // A function that returns 'considered' value
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> allConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "all considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {

                    // Return zero if the valuation is null.
                    final Currency result = aggregate.getConsidered();
                    return (null == result) ? Currency.getZero() : result;
                }
            };

    // A function that returns 'not considered' value
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> allNotConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "all not-considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {

                    // Return zero if the valuation is null.
                    final Currency result = aggregate.getNotConsidered();
                    return (null == result) ? Currency.getZero() : result;
                }
            };

    // A function that returns proposed value
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> allProposed =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "all proposed";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {

                    // Return zero if the valuation is null.
                    final Currency result = aggregate.getProposed();
                    return (null == result) ? Currency.getZero() : result;
                }
            };

    // The singleton hierarchy
    private static final Hierarchy instance = new Hierarchy();

    // A function that returns 'considered' value for all tax types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> taxConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "tax type considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getConsidered(TaxType.ALL);
                }
            };

    // A function that returns 'not considered' value for all tax types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> taxNotConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "tax type not-considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getNotConsidered(TaxType.ALL);
                }
            };

    // A function that returns proposed value for all tax types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> taxProposed =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "tax type proposed";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getProposed(TaxType.ALL);
                }
            };

    // A function that returns 'considered' value for all weight types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> weightConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "weight type considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getConsidered(WeightType.ALL);
                }
            };

    // A function that returns 'not considered' value for all weight types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> weightNotConsidered =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "weight type not-considered";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getNotConsidered(WeightType.ALL);
                }
            };

    // A function that returns proposed value for all weight types
    private static final OneParameterFunction<Currency, Aggregate<?, ?, ?>> weightProposed =
            new OneParameterFunction<>() {

                @Override
                public @NotNull String getDescription() {
                    return "weight type proposed";
                }

                @Override
                public @NotNull Currency invoke(@NotNull Aggregate<?, ?, ?> aggregate) {
                    return aggregate.getProposed(WeightType.ALL);
                }
            };

    // An account map
    private final Map<AccountKey, Account> accounts = new HashMap<>();

    // A map of holding line types to add actions
    private final HashMap<HoldingLineType,
            OneParameterAction<HoldingDescription>> addMap = new HashMap<>();

    // A stack of aggregates
    private final Stack<Aggregate<?, ?, ?>> aggregates = new Stack<>();

    // All of the holding line types
    private final HoldingLineType[] lineTypes = HoldingLineType.values();

    // The message logger
    private final MessageLogger logger = new MessageLogger();

    // A portfolio map
    private final Map<String, Portfolio> portfolios = new HashMap<>();

    // Sets the 'considered' value of a hierarchy object given a non-null value
    private final TwoParameterAction<Common<?, ?, ?>, ICountable> setConsidered =
            (argument1, argument2) -> argument1.setConsidered(argument2.getValue());

    // Sets the 'considered' shares of a ticker given a non-null value
    private final TwoParameterAction<Ticker, ICountable> setConsideredShares =
            (argument1, argument2) -> argument1.setConsideredShares(argument2.getValue());

    /*
     * Sets the 'not considered' value of a hierarchy object given a non-null
     * value
     */
    private final TwoParameterAction<Common<?, ?, ?>, ICountable> setNotConsidered =
            (argument1, argument2) -> argument1.setNotConsidered(argument2.getValue());

    // An action to add an account
    private final OneParameterAction<HoldingDescription> addAccountAction =
            this::addAccount;

    // An action to add an institution
    private final OneParameterAction<HoldingDescription> addInstitutionAction =
            this::addInstitution;

    // An action to add a portfolio
    private final OneParameterAction<HoldingDescription> addPortfolioAction =
            this::addPortfolio;

    // Sets the 'not considered' shares of a ticker given a non-null value
    private final TwoParameterAction<Ticker, ICountable> setNotConsideredShares =
            (argument1, argument2) -> argument1.setNotConsideredShares(argument2.getValue());

    // Sets the price of a ticker given a non-null value
    private final TwoParameterAction<Ticker, ICountable> setPrice =
            (argument1, argument2) -> argument1.setPrice(argument2.getValue());

    // An action to add a ticker
    private final OneParameterAction<HoldingDescription> addTickerAction =
            this::addTicker;

    // The date of the hierarchy
    private Date date;

    {

        // Build up the 'add' map.
        addMap.put(HoldingLineType.ACCOUNT, addAccountAction);
        addMap.put(HoldingLineType.INSTITUTION, addInstitutionAction);
        addMap.put(HoldingLineType.PORTFOLIO, addPortfolioAction);
        addMap.put(HoldingLineType.TICKER, addTickerAction);

        // Set the message logger, and clear the hierarchy.
        logger.setLogger(
                Logger.getLogger(Hierarchy.class.getCanonicalName()));
        clearHierarchy();
    }

    /**
     * Constructs the hierarchy.
     */
    private Hierarchy() {

        // Nothing to do here right now.
    }

    /**
     * Adds a currency addend to a sum.
     *
     * @param sum    The sum
     * @param addend The currency addend
     */
    private static void add(@NotNull MutableCurrency sum, Currency addend) {

        // Add the addend to the sum if it is not null.
        if (null != addend) {
            sum.add(addend);
        }
    }

    /**
     * Checks whether two functions that take an aggregate as an argument
     * return the same currency value.
     *
     * @param first     The first function
     * @param second    The second function
     * @param aggregate The aggregate argument
     * @return True if the functions return the same value, false otherwise
     */
    private static boolean check(@NotNull OneParameterFunction<Currency, Aggregate<?, ?, ?>> first,
                                 @NotNull OneParameterFunction<Currency, Aggregate<?, ?, ?>> second,
                                 @NotNull Aggregate<?, ?, ?> aggregate) {
        return first.invoke(aggregate).equals(second.invoke(aggregate));
    }

    /**
     * Checks whether the proposed value of an aggregate matches the sum of
     * value by tax type.
     *
     * @param aggregate The aggregate in question
     * @return True if the proposed value of an aggregate matches the sum of
     * value by tax type, false otherwise
     */
    public static boolean checkTaxType(@NotNull Aggregate<?, ?, ?> aggregate) {
        return check(allProposed, weightProposed, aggregate);
    }

    /**
     * Checks whether the proposed value of an aggregate matches the sum of
     * value by weight type.
     *
     * @param aggregate The aggregate in question
     * @return True if the proposed value of an aggregate matches the sum of
     * value by weight type, false otherwise
     */
    public static boolean checkWeightType(@NotNull Aggregate<?, ?, ?> aggregate) {
        return check(allProposed, weightProposed, aggregate);
    }

    /**
     * Gets the logging level for extraordinary, non-warning activity.
     *
     * @return The logging level for extraordinary, non-warning activity
     */
    private static @NotNull Level getExtraordinary() {
        return Level.INFO;
    }

    /**
     * Gets a hierarchy instance.
     *
     * @return A hierarchy instance
     */
    public static @NotNull Hierarchy getInstance() {
        return instance;
    }

    /**
     * Gets the logging level for ordinary, non-warning activity.
     *
     * @return The logging level for ordinary, non-warning activity
     */
    private static @NotNull Level getOrdinary() {
        return Level.FINEST;
    }

    /**
     * Locks all aggregate hierarchy objects in a collection.
     *
     * @param collection A collection of aggregate hierarchy objects
     */
    private static void lock(@NotNull Collection<? extends Aggregate<?, ?, ?>>
                                     collection) {

        // Cycle for each aggregate, and lock it.
        for (Aggregate<?, ?, ?> aggregate : collection) {
            aggregate.lockChildren();
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

            // Create a holdings builder. Read available holding lines.
            final ElementReader holdings = new HoldingsBuilder();
            holdings.readLines();

            // The holding library should now be populated. Get its date.
            final HoldingLibrary library = HoldingLibrary.getInstance();
            final Date date = library.getDate();

            /*
             * Print the date of the holding library, and whether problems
             * were detected.
             */
            System.out.printf("The date of the holding library is: %s; " +
                            "problems were%s detected.%n",
                    DateUtilities.format(date),
                    (holdings.hadProblem() ? "" : " not"));

            /*
             * Create a portfolios builder. Read lines from a portfolio
             * description file with date less than or equal to our holding
             * library date.
             */
            final ElementReader portfolios = new PortfoliosBuilder();
            portfolios.readLines(date);

            /*
             * Print the date of the portfolio library, and whether problems
             * were detected.
             */
            System.out.printf("The date of the portfolio library is: %s; " +
                            "problems were%s detected.%n",
                    DateUtilities.format(
                            PortfolioLibrary.getInstance().getDate()),
                    (portfolios.hadProblem() ? "" : " not"));

            /*
             * Create an accounts builder. Read lines from an account
             * description file with date less than or equal to our holding
             * library date.
             */
            final ElementReader accounts = new AccountsBuilder();
            accounts.readLines(date);

            /*
             * Print the date of the account library, and whether problems
             * were detected.
             */
            System.out.printf("The date of the account library is: %s; " +
                            "problems were%s detected.%n",
                    DateUtilities.format(
                            AccountLibrary.getInstance().getDate()),
                    (accounts.hadProblem() ? "" : " not"));

            /*
             * Create a ticker builder. Read lines from a ticker description
             * file with date less than or equal to our holding library date.
             */
            final ElementReader tickers = new TickersBuilder();
            tickers.readLines(date);

            /*
             * Print the date of the ticker library, and whether problems were
             * detected.
             */
            System.out.printf("The date of the ticker library is: %s; " +
                            "problems were%s detected.%n",
                    DateUtilities.format(
                            TickerLibrary.getInstance().getDate()),
                    (tickers.hadProblem() ? "" : " not"));

            // Get a hierarchy instance and build it.
            final Hierarchy hierarchy = Hierarchy.getInstance();
            hierarchy.buildHierarchy();

            /*
             * Say whether there was a problem loading the hierarchy, then
             * clear the hierarchy.
             */
            System.out.printf("Problems were%s detected while loading " +
                            "holdings into the hierarchy.%n",
                    hierarchy.hadProblem() ? "" : " not");
            hierarchy.clearHierarchy();

        } catch (@NotNull IOException exception) {

            // Catch and report any I/O exception that may occur.
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Performs an action on a hierarchy object and a countable if the
     * countable is not null.
     *
     * @param action          The action to perform
     * @param hierarchyObject A hierarchy object
     * @param countable       A countable
     * @param <T>             A hierarchy object type
     */
    private static <T extends Common<?, ?, ?>> void set(
            @NotNull TwoParameterAction<T, ICountable> action,
            @NotNull T hierarchyObject,
            ICountable countable) {

        // Perform the action if the countable is not null.
        if (null != countable) {
            action.perform(hierarchyObject, countable);
        }
    }

    /**
     * Sums two currency values.
     *
     * @param first  The first value
     * @param second The second value
     * @return The sum of the values, or null if both values were null
     */
    private static Currency sum(Currency first, Currency second) {

        // Declare and initialize the result. Is the first value not null?
        MutableCurrency result = null;
        if (null != first) {

            /*
             * The first value is not null. Re-initialize the result with the
             * first value.
             */
            result = new MutableCurrency(first);
        }

        // Is the second value not null?
        if (null != second) {

            /*
             * The second value is not null. Re-initialize the result with the
             * second value if the result is not yet set...
             */
            if (null == result) {
                result = new MutableCurrency(second);
            }

            // ...otherwise add the second value to the first.
            else {
                result.add(second);
            }
        }

        // Return the result.
        return (null == result) ? null : result.getImmutable();
    }

    /**
     * Adds an account from a holding description.
     *
     * @param holdingDescription A holding description
     */
    private void addAccount(@NotNull HoldingDescription holdingDescription) {

        // Get the holding key from the description.
        final HoldingKey holdingKey =
                holdingDescription.getHoldingParentChild();

        // Log entry information.
        logMessage(getOrdinary(), String.format("Observed account with " +
                "holding key: '%s'.", holdingKey));

        /*
         * Get the number (as a string) and the institution mnemonic from the
         * account.
         */
        final String accountNumber = holdingKey.getSecond();
        final String institutionMnemonic = holdingKey.getFirst();

        /*
         * Get an instance of the account library. Is the combination of
         * institution mnemonic and account number not okay according to the
         * account library?
         */
        final AccountLibrary library = AccountLibrary.getInstance();
        if (!library.areKeyElementsOkay(institutionMnemonic, accountNumber)) {

            /*
             * The combination of institution mnemonic and account number are
             * not okay according to the account library. We cannot continue to
             * add the account.
             */
            logMessage(Level.SEVERE, String.format("The combination of " +
                            "institution mnemonic '%s' and account number " +
                            "'%s' are not acceptable to the account " +
                            "library; skipping addition of account.",
                    institutionMnemonic, accountNumber));
            return;
        }

        /*
         * We should - with certainty - be able to parse the account number as
         * a number, and combine it with the institution mnemonic into a single
         * account key. Do that.
         */
        final AccountKey accountKey = new AccountKey(institutionMnemonic,
                AccountKey.parseLong(accountNumber));

        /*
         * Create a new account with the account key. Try to add and push the
         * account. Could this not be accomplished?
         */
        final Account account = new Account(accountKey);
        if (!addAndPush(account)) {

            // The account could not be added and pushed. Do not continue.
            logMessage(Level.SEVERE, String.format("Could not add an " +
                    "account with key '%s'.", accountKey));
            return;
        }

        // Get any account description for the account.
        final AccountDescription accountDescription =
                library.getDescription(accountKey);

        /*
         * Set the account description in the account object. Was the account
         * description null?
         */
        account.setDescription(accountDescription);
        if (null == accountDescription) {

            // The account description was null. Log a warning.
            logMessage(Level.WARNING, String.format("A description was not " +
                            "available for the account with key '%s'.",
                    accountKey));
        }

        /*
         * Set the 'not considered' value of the account using the value in the
         * holding description. Put the account in the account map.
         */
        set(setNotConsidered, account, holdingDescription.getValue());
        accounts.put(account.getKey(), account);

        // Log exit information.
        logMessage(getOrdinary(), String.format("Processed, and added " +
                "account with key: '%s'.", accountKey));
    }

    /**
     * Adds a hierarchy object to a parent, and pushes the object onto the
     * aggregate stack if it is an aggregate.
     *
     * @param hierarchyObject A hierarchy object
     * @return True if the hierarchy can be added and pushed, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean addAndPush(@NotNull Common<?, ?, ?> hierarchyObject) {

        /*
         * Get the highest expected holding line type (portfolio is currently
         * lowest; ticker, highest).
         */
        final HoldingLineType highestExpected = getHighestExpected();
        final HoldingLineType received = hierarchyObject.getLineType();

        /*
         * Get the difference between the ordinals of the highest expected,
         * and received holding line types. If the difference is positive, we
         * need to pop and sum some aggregates.
         */
        final int difference = highestExpected.ordinal() - received.ordinal();
        popAndSum(difference);

        /*
         * An unexpected holding line type occurred if the difference is
         * negative. We cannot accommodate the given hierarchy object if that
         * is the case. Is the difference positive? Remember, in the loop
         * above we have effectively reduced any positive difference to zero.
         */
        final boolean result = (0 <= difference);
        if (result) {

            /*
             * We can accommodate this type of hierarchy object. Is the
             * aggregate stack not empty?
             */
            if (!aggregates.empty()) {

                /*
                 * The aggregate stack is not empty. Peek at the aggregate at
                 * the top of the stack.
                 */
                final Aggregate<?, ?, ?> aggregate = aggregates.peek();
                try {

                    /*
                     * Try to add the given hierarchy object as a child of the
                     * aggregate.
                     */
                    aggregate.addChild(hierarchyObject);
                }

                // The given hierarchy object is not the correct type.
                catch (ClassCastException exception) {

                    /*
                     * This basically should not have happened if the logic in
                     * this class is correct. Log an error.
                     */
                    logMessage(Level.SEVERE, String.format("A hierarchy " +
                                    "object of type '%s' and key '%s' cannot be " +
                                    "made a child of an aggregate of type '%s' and " +
                                    "key '%s'.",
                            hierarchyObject.getClass().getSimpleName(),
                            hierarchyObject.getKey(),
                            aggregate.getClass().getSimpleName(),
                            aggregate.getKey()));
                }
            }

            /*
             * Push the hierarchy object onto the stack if it is itself an
             * aggregate.
             */
            if (hierarchyObject instanceof Aggregate) {
                aggregates.push((Aggregate<?, ?, ?>) hierarchyObject);
            }
        }

        // Return the result to our caller.
        return result;
    }

    /**
     * Adds an institution from a holding description.
     *
     * @param holdingDescription A holding description
     */
    private void addInstitution(@NotNull HoldingDescription
                                        holdingDescription) {

        // Get the holding key from the description, and log information.
        final HoldingKey key = holdingDescription.getHoldingParentChild();
        logMessage(getOrdinary(), String.format("Observed institution with " +
                "holding key: '%s'.", key));

        /*
         * Get the mnemonic of the institution. Create a new institution
         * object with the given mnemonic. Try to add and push the institution.
         * Could this not be accomplished?
         */
        final String institutionMnemonic = key.getSecond();
        final Institution institution = new Institution(institutionMnemonic);
        if (!addAndPush(institution)) {

            // The institution could not be added and pushed. Do not continue.
            logMessage(Level.SEVERE, String.format("Could not add an " +
                    "institution with mnemonic '%s'.", institutionMnemonic));
            return;
        }

        /*
         * Set the 'not considered' value of the institution using the value in
         * the holding description. Log exit information.
         */
        set(setNotConsidered, institution, holdingDescription.getValue());
        logMessage(getOrdinary(), String.format("Processed, and added " +
                "institution with mnemonic: '%s'.", institutionMnemonic));
    }

    /**
     * Adds a portfolio from a holding description.
     *
     * @param holdingDescription A holding description
     */
    private void addPortfolio(@NotNull HoldingDescription holdingDescription) {

        // Get the holding key from the description, and log information.
        final HoldingKey key = holdingDescription.getHoldingParentChild();
        logMessage(getOrdinary(), String.format("Observed portfolio with " +
                "holding key: '%s'.", key));

        /*
         * Get the mnemonic of the portfolio, and an instance of the portfolio
         * library. Is the mnemonic of the portfolio not okay according to the
         * portfolio library?
         */
        final String portfolioMnemonic = key.getSecond();
        final PortfolioLibrary library = PortfolioLibrary.getInstance();
        if (!library.areKeyElementsOkay(portfolioMnemonic)) {

            /*
             * The mnemonic for the portfolio is not okay according to the
             * portfolio library. We cannot continue to add the portfolio.
             */
            logMessage(Level.SEVERE, String.format("The portfolio mnemonic " +
                    "'%s' is not acceptable to the portfolio library; " +
                    "skipping addition of portfolio.", portfolioMnemonic));
            return;
        }

        /*
         * Create a new portfolio object with the portfolio mnemonic. Try to
         * add and push the portfolio. Could this not be accomplished?
         */
        final Portfolio portfolio = new Portfolio(portfolioMnemonic);
        if (!addAndPush(portfolio)) {

            // The portfolio could not be added and pushed. Do not continue.
            logMessage(Level.SEVERE, String.format("Could not add a " +
                    "portfolio with mnemonic '%s'.", portfolioMnemonic));
            return;
        }

        // Get any portfolio description for the portfolio.
        final PortfolioDescription portfolioDescription =
                library.getDescription(portfolioMnemonic);

        /*
         * Set the portfolio description in the portfolio object. Was the
         * portfolio description null?
         */
        portfolio.setDescription(portfolioDescription);
        if (null == portfolioDescription) {

            // The portfolio description was null. Log a warning.
            logMessage(Level.WARNING, String.format("A description was not " +
                            "available for the portfolio with mnemonic '%s'.",
                    portfolioMnemonic));
        }

        /*
         * Set the 'not considered' value of the portfolio using the value in
         * the holding description. Put the portfolio in the portfolio map.
         */
        set(setNotConsidered, portfolio, holdingDescription.getValue());
        portfolios.put(portfolio.getKey(), portfolio);

        // Log exit information.
        logMessage(getOrdinary(), String.format("Processed, and added " +
                "portfolio with mnemonic: '%s'.", portfolioMnemonic));
    }

    /**
     * Adds a ticker from a holding description.
     *
     * @param holdingDescription A holding description
     */
    private void addTicker(@NotNull HoldingDescription holdingDescription) {

        // Get the holding key from the description. Build a key description.
        final HoldingKey holdingKey = holdingDescription.getHoldingParentChild();
        final String keyDescription = Pair.combine(
                AccountKey.createKey(holdingKey.getFirst()), holdingKey.getSecond());

        // Log information.
        logMessage(getOrdinary(), String.format("Observed ticker with " +
                "holding key: '%s'.", keyDescription));

        /*
         * Get the symbol of the ticker, and an instance of the ticker library.
         * Is the symbol of the ticker not okay according to the ticker
         * library?
         */
        final String tickerSymbol = holdingKey.getSecond();
        final TickerLibrary library = TickerLibrary.getInstance();
        if (!library.areKeyElementsOkay(tickerSymbol)) {

            /*
             * The symbol of the ticker is not okay according to the ticker
             * library. We cannot continue to add the ticker.
             */
            logMessage(Level.SEVERE, String.format("The ticker symbol " +
                    "'%s' is not acceptable to the ticker library; " +
                    "skipping addition of ticker.", tickerSymbol));
            return;
        }

        /*
         * Create a new ticker object with the ticker symbol. Try to add and
         * push the ticker. Could this not be accomplished?
         */
        final Ticker ticker = new Ticker(tickerSymbol);
        if (!addAndPush(ticker)) {

            // The ticker could not be added and pushed. Do not continue.
            logMessage(Level.SEVERE, String.format("Could not add a " +
                    "ticker with symbol '%s'.", tickerSymbol));
            return;
        }

        // Get any ticker description for the ticker.
        final TickerDescription tickerDescription =
                library.getDescription(tickerSymbol);

        /*
         * Set the ticker description in the ticker object. Was the ticker
         * description null?
         */
        ticker.setDescription(tickerDescription);
        if (null == tickerDescription) {

            // The ticker description was null. Log a warning.
            logMessage(Level.WARNING, String.format("A description was not " +
                            "available for the ticker with symbols '%s'.",
                    tickerSymbol));

            // Set 'not considered' values.
            setNotConsidered(ticker, holdingDescription);
        }

        /*
         * The ticker description is not null. Set 'considered' values if the
         * ticker description so indicates.
         */
        else if (tickerDescription.isConsidered()) {
            setConsidered(ticker, holdingDescription);
        }

        /*
         * The ticker description does not indicate that the values are
         * considered for rebalancing.
         */
        else {
            setNotConsidered(ticker, holdingDescription);
        }

        // Log exit information.
        logMessage(getOrdinary(), String.format("Processed, and added " +
                "ticker with holding key: '%s'.", keyDescription));
    }

    /**
     * Builds the hierarchy.
     */
    public void buildHierarchy() {

        // Clear any existing hierarchy. Add the holding descriptions.
        clearHierarchy();
        dispatchAction(addMap);

        // Pop and set any remaining sum checkers, and lock the hierarchy.
        popAndSum();
        setLocked();
    }

    /**
     * Checks whether two functions that take an aggregate as an argument
     * return the same currency value, and reports if they do not
     *
     * @param first     The first function
     * @param second    The second function
     * @param aggregate The aggregate argument
     */
    private void checkAndReport(@NotNull OneParameterFunction<Currency, Aggregate<?, ?, ?>> first,
                                @NotNull OneParameterFunction<Currency, Aggregate<?, ?, ?>> second,
                                @NotNull Aggregate<?, ?, ?> aggregate) {

        // Does the check fail?
        if (!check(first, second, aggregate)) {

            // The check fails. Log a warning with specifics.
            logMessage(Level.WARNING, String.format("Mismatch in value: %s " +
                            "for '%s' and %s for '%s' in aggregate with key '%s'.",
                    first.invoke(aggregate), first.getDescription(),
                    second.invoke(aggregate), second.getDescription(),
                    aggregate.getKey()));
        }
    }

    /**
     * Checks weather an aggregate has consistent current valuations for all
     * tax types and weight types.
     *
     * @param aggregate The aggregate
     */
    private void checkAndReport(@NotNull Aggregate<?, ?, ?> aggregate) {

        // Check and report on the tax type.
        checkAndReport(allConsidered, taxConsidered, aggregate);
        checkAndReport(allNotConsidered, taxNotConsidered, aggregate);

        // Check and report on the weight type.
        checkAndReport(allConsidered, weightConsidered, aggregate);
        checkAndReport(allNotConsidered, weightNotConsidered, aggregate);
    }

    /**
     * Clears the hierarchy.
     */
    public void clearHierarchy() {

        // Clear the date and the portfolios.
        setDate(null);
        portfolios.clear();

        // Clear the accounts and reset the problem flag.
        accounts.clear();
        resetProblem();
    }

    /**
     * Dispatches an appropriate action for a holding line given the type of
     * the holding line.
     *
     * @param map A map of holding line types to actions (these each take a
     *            holding description as an argument)
     */
    private void dispatchAction(@NotNull Map<HoldingLineType,
            @NotNull OneParameterAction<HoldingDescription>> map) {

        /*
         * Declare a variable to contain a holding line type. Get the holding
         * library and cycle for each holding.
         */
        HoldingLineType lineType;
        final HoldingLibrary library = HoldingLibrary.getInstance();
        for (HoldingDescription description : library.getCatalog()) {

            // Get the line type from the description. Is the line type null?
            lineType = description.getLineType();
            if (null == lineType) {

                /*
                 * The line type is null. There is nothing to be done with
                 * this holding description. Log an error.
                 */
                logMessage(Level.SEVERE, String.format("Holding description " +
                                "with key '%s' has a null line type; skipping.",
                        description.getKey()));
            }

            // The line type is not null.
            else {

                // Get an action from the provided map. Is the action null?
                final OneParameterAction<HoldingDescription> action =
                        map.get(lineType);
                if (null == action) {

                    /*
                     * The action is null. We only get here if there is a line
                     * type that is not known to this class. Log an error.
                     */
                    logMessage(Level.SEVERE, String.format("Holding " +
                                    "description with key '%s' has " +
                                    "unknown line type %s; skipping.",
                            description.getKey(), lineType));
                }

                /*
                 * The action is not null. Perform the action using the
                 * first/next holding description.
                 */
                else {
                    action.perform(description);
                }
            }
        }

        // Reflect the date of the holdings library in the hierarchy.
        setDate(library.getDate());
    }

    /**
     * Gets an account.
     *
     * @param key The key of the account
     * @return An account matching the key, or null if there was no match
     * account
     */
    public Account getAccount(@NotNull AccountKey key) {
        return accounts.get(key);
    }

    /**
     * Gets an account.
     *
     * @param institutionMnemonic An institution mnemonic
     * @param accountNumber       An account number
     * @return An account matching the unique combination of institution
     * mnemonic and account number, or null if there was no match
     */
    public Account getAccount(@NotNull String institutionMnemonic,
                              @NotNull Long accountNumber) {
        return getAccount(new AccountKey(institutionMnemonic, accountNumber));
    }

    /**
     * Gets a collection of accounts in the hierarchy.
     *
     * @return A collection of accounts in the hierarchy.
     */
    public @NotNull Collection<Account> getAccounts() {
        return accounts.values();
    }

    /**
     * Gets the date of the hierarchy (based on the contents of the holding
     * library).
     *
     * @return The date of the library (based on the contents of the holding
     * library).
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the highest expected holding line type (portfolio is lowest;
     * ticker, highest).
     *
     * @return The highest expected holding line type
     */
    private @NotNull HoldingLineType getHighestExpected() {
        return lineTypes[aggregates.size()];
    }

    /**
     * Gets a portfolio.
     *
     * @param portfolioMnemonic The mnemonic of the portfolio
     * @return A portfolio matching the mnemonic, or null if there was no
     * matching portfolio
     */
    public Portfolio getPortfolio(@NotNull String portfolioMnemonic) {
        return portfolios.get(portfolioMnemonic);
    }

    /**
     * Gets a collection of portfolios in the hierarchy.
     *
     * @return A collection of portfolios in the hierarchy.
     */
    public @NotNull Collection<Portfolio> getPortfolios() {
        return portfolios.values();
    }

    /**
     * Returns whether there was a problem processing holdings into the
     * hierarchy.
     *
     * @return True if there was a problem processing holdings into the
     * hierarchy, false otherwise
     */
    public boolean hadProblem() {
        return logger.hadProblem();
    }

    /**
     * Logs messages.
     *
     * @param level   The level for the message
     * @param message The message to log
     */
    private void logMessage(@NotNull Level level,
                            @NotNull String message) {
        logger.logMessage(level, message);
    }

    /**
     * Pops and sums aggregates on the aggregates stack.
     *
     * @param count The number of aggregates to pop and set
     * @return True if the requested number of aggregates were popped and
     * set, false if fewer were popped and set
     */
    @SuppressWarnings({"UnusedReturnValue"})
    private boolean popAndSum(int count) {

        /*
         * Cycle for the required number of aggregates, or until the
         * aggregates stack is empty.
         */
        int index;
        for (index = 0; (index < count) && (!aggregates.empty()); ++index) {

            // Sum the first/next aggregate if it has children.
            sumIfChildren(aggregates.pop());
        }

        /*
         * Return whether we were able to pop and sum all the requested
         * aggregates.
         */
        return (count <= index);
    }

    /**
     * Pops and sets all the aggregates on the aggregates stack.
     */
    private void popAndSum() {
        popAndSum(aggregates.size());
    }

    /**
     * Resets the problem flag.
     */
    private void resetProblem() {
        logger.resetProblem();
    }

    /**
     * Sets the 'considered' values in a ticker.
     *
     * @param ticker             The ticker to set
     * @param holdingDescription The holding description containing the values
     */
    private void setConsidered(@NotNull Ticker ticker,
                               @NotNull HoldingDescription
                                       holdingDescription) {

        /*
         * Set the 'considered' shares, price and 'considered' value of the
         * ticker.
         */
        set(setConsideredShares, ticker, holdingDescription.getShares());
        set(setPrice, ticker, holdingDescription.getPrice());
        set(setConsidered, ticker, holdingDescription.getValue());
    }

    /**
     * Sets the date of the hierarchy.
     *
     * @param date The new date of the hierarchy
     */
    private void setDate(Date date) {
        this.date = date;
    }

    /**
     * Locks the hierarchy library.
     */
    private void setLocked() {

        // Lock the aggregate maps.
        lock(accounts.values());
        lock(portfolios.values());
    }

    /**
     * Sets the 'not considered' values in a ticker.
     *
     * @param ticker             The ticker to set
     * @param holdingDescription The holding description containing the values
     */
    private void setNotConsidered(@NotNull Ticker ticker,
                                  @NotNull HoldingDescription
                                          holdingDescription) {

        /*
         * Set the 'not considered' shares, price and 'not considered' value
         * of the ticker.
         */
        set(setNotConsideredShares, ticker, holdingDescription.getShares());
        set(setPrice, ticker, holdingDescription.getPrice());
        set(setNotConsidered, ticker, holdingDescription.getValue());
    }

    /**
     * Sums and sets the 'considered' and 'not considered' values using the
     * children of an aggregate.
     *
     * @param aggregate An aggregate
     * @param <T>       The type of the children of the aggregate
     * @return True if the sum of the children ('considered' plus 'not
     * considered' together) matches the currently set sum in the aggregate;
     * false otherwise
     */
    @SuppressWarnings({"UnusedReturnValue"})
    private <T extends Common<?, ?, ?>>
    boolean sum(@NotNull Aggregate<?, T, ?> aggregate) {

        /*
         * Declare and initialize a variable to receive the 'considered'
         * values of the children of the aggregate.
         */
        final MutableCurrency considered =
                new MutableCurrency(Currency.getZero());

        /*
         * Declare and initialize a variable to receive the 'not considered'
         * values of the children of the aggregate.
         */
        final MutableCurrency notConsidered =
                new MutableCurrency(Currency.getZero());

        // Get the children of the aggregate, and cycle for each.
        final Collection<T> children = aggregate.getChildren();
        for (T child : children) {

            /*
             * Add the 'considered' and 'not considered' values of the
             * first/next child to the running total.
             */
            add(considered, child.getConsidered());
            add(notConsidered, child.getNotConsidered());
        }

        // Sum the current values, if any, set it in the aggregate.
        final Currency expectedTotal =
                sum(aggregate.getConsidered(), aggregate.getNotConsidered());

        /*
         * Declare and initialize the return value to true if there is no
         * expected total. Was there an expected total?
         */
        boolean result = (null == expectedTotal);
        if (!result) {

            // There is an expected total. Calculate the actual total.
            final MutableCurrency actualTotal =
                    new MutableCurrency(considered);
            actualTotal.add(notConsidered);

            // Does the actual total not match the expected total?
            //noinspection EqualsBetweenInconvertibleTypes
            if (result = (!actualTotal.equals(expectedTotal))) {

                /*
                 * The actual total does not match the expected total. Log a
                 * warning.
                 */
                logMessage(Level.WARNING, String.format("The sum %s of the " +
                                "children of aggregate of type '%s' and key '%s' " +
                                "does not match the expected value, %s.", actualTotal,
                        aggregate.getClass().getSimpleName(),
                        aggregate.getKey(), expectedTotal));
            }
        }

        // Set the actual values in the aggregate, and return to caller.
        aggregate.setConsidered(considered.getValue());
        aggregate.setNotConsidered(notConsidered.getValue());
        return result;
    }

    /**
     * Sums and sets the 'considered' and 'not considered' values using the
     * children of an aggregate (if the aggregate has children).
     *
     * @param aggregate An aggregate
     * @param <T>       The type of the children of the aggregate
     * @return True if the aggregate has no children, or if the sum of the
     * children ('considered' plus 'not considered' together) matches the
     * currently set sum in the aggregate; false otherwise
     */
    @SuppressWarnings({"UnusedReturnValue"})
    private <T extends Common<?, ?, ?>>
    boolean sumIfChildren(@NotNull Aggregate<?, T, ?> aggregate) {

        /*
         * Synthesize this aggregate, as necessary, and receive a status of
         * that activity. Sum the aggregate if it has children.
         * aggregate a portfolio?
         */
        final boolean result = aggregate.synthesizeIf() &&
                (aggregate.getChildren().isEmpty() || sum(aggregate));
        if (HoldingLineType.PORTFOLIO.equals(aggregate.getLineType())) {

            /*
             * The aggregate is a portfolio. Set the aggregate to work with
             * current values, then breakdown the holdings by category: weight
             * type and tax type. Check on the resulting valuations.
             */
            aggregate.setCurrent();
            aggregate.breakdown();
            checkAndReport(aggregate);
        }

        // Return the result.
        return result;
    }

    private interface OneParameterAction<T> {

        /**
         * Performs an action.
         *
         * @param argument The argument for the action
         */
        void perform(@NotNull T argument);
    }

    private interface OneParameterFunction<S, T> {

        /**
         * Gets a description of the function.
         *
         * @return A description of the function
         */
        @NotNull String getDescription();

        /**
         * Invokes a function.
         *
         * @param argument The argument for the function
         * @return The result of the function
         */
        @NotNull S invoke(@NotNull T argument);
    }

    private interface TwoParameterAction<S, T> {

        /**
         * Performs an action.
         *
         * @param argument1 The first argument for the action
         * @param argument2 The second argument for the action
         */
        void perform(@NotNull S argument1, @NotNull T argument2);
    }
}
