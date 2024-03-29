package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.detailed.DetailedDescription;
import com.garygregg.rebalance.detailed.DetailedLibrary;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AccountRebalancer extends Rebalancer {

    // A collection of weight types to account valuation pairs
    private static final Collection<Pair<WeightType, ValueFromAccount>>
            accountCollection = new ArrayList<>();

    // Our factory for creating value-from-account objects
    private static final Factory<FundType, ValueFromAccount>
            accountValueFactory = ValueFromAccount::new;

    // A collection of weight types to detailed valuation pairs
    private static final Collection<Pair<WeightType, ValueFromDetailed>>
            detailedCollection = new ArrayList<>();

    // Our factory for creating value-from-detailed objects
    private static final Factory<WeightType, ValueFromDetailed>
            detailedValueFactory = ValueFromDetailed::new;

    // The distinguished value for everything
    private static final double everything =
            Percent.getOneHundred().getValue();

    // The extraordinary logging level
    private static final Level extraordinary =
            MessageLogger.getExtraordinary();

    // The level one weight types
    private static final List<WeightType> levelOne =
            WeightType.getLevelOne();

    // A preference manager instance
    private static final PreferenceManager manager =
            PreferenceManager.getInstance();

    // An adjuster instance
    private static final Adjuster adjuster = manager.getAdjuster();

    // The distinguished value for nothing
    private static final double nothing = Percent.getZero().getValue();

    // The percentage overlay procedure
    private static final OverlayProcedure percentage =
            new OverlayProcedure() {

                @Override
                public double adjustEquity(double ratio) {

                    /*
                     * Currently, we perform no adjustment at all here.
                     */
                    return 0.;
                }

                @Override
                public void overlay(@NotNull Map<WeightType, Double> weightMap,
                                    @NotNull Account account) {
                    AccountRebalancer.overlay(weightMap, account);
                }
            };

    // A list of weight types to portfolio valuation pairs
    private static final Collection<Pair<WeightType, ValueFromPortfolio>>
            portfolioList = new ArrayList<>();

    // The closure overlay procedure
    private static final OverlayProcedure closure =
            new OverlayProcedure() {

                @Override
                public double adjustEquity(double ratio) {
                    return adjuster.f(ratio) - adjuster.getYHigh();
                }

                @Override
                public void overlay(@NotNull Map<WeightType, Double> weightMap,
                                    @NotNull Account account) {

                    /*
                     * Overlay the weight map with weights from the account,
                     * including detailed weights as necessary. Set the
                     * description in the portfolio list. Is the description
                     * not null?
                     */
                    AccountRebalancer.overlay(weightMap, account);
                    if (setDescription(portfolioList,
                            account.getPortfolioDescription())) {

                        /*
                         * The description is not null. Overlay the weight map
                         * with portfolio weights.
                         */
                        AccountRebalancer.overlay(weightMap, portfolioList);
                    }
                }
            };

    // Our factory for creating value-from-portfolio objects
    private static final Factory<WeightType, ValueFromPortfolio>
            portfolioValueFactory = ValueFromPortfolio::new;

    // A map of fund types to account description valuation objects
    private static final Map<FundType, ValueFromAccount> valueFromAccountMap =
            createMap(FundType.values(), accountValueFactory);

    // A map of weight types to detailed description valuation objects
    private static final Map<WeightType, ValueFromDetailed>
            valueFromDetailedMap = createMap(WeightType.values(),
            detailedValueFactory);

    // A map of weight types to portfolio description valuation objects
    private static final Map<WeightType, ValueFromPortfolio>
            valueFromPortfolioMap = createMap(WeightType.values(),
            portfolioValueFactory);

    static {

        // Build the account list, the detailed list, and the portfolio list.
        buildAccountList();
        buildDetailedList();
        buildPortfolioList();
    }

    // Our message logger
    private final MessageLogger messageLogger = new MessageLogger();

    {

        // Set the logger in the message logger.
        messageLogger.setLogger(Logger.getLogger(
                AccountRebalancer.class.getCanonicalName()));
    }

    /**
     * Adjust a weight map for relative market valuation.
     *
     * @param weightMap A weight map
     * @param ratio     The ratio for adjustment, a value that is added to the
     *                  current percentage for equities in the weight map
     *                  (example: If 50.0% equities with a ratio equals 0.2,
     *                  the new equity percentage becomes 50.02%)
     */
    private static void adjust(@NotNull Map<? super WeightType, Double>
                                       weightMap, double ratio) {

        /*
         * Declare and initialize a sum for all the level one weight types.
         * Declare and initialize a variable to receive a weight type, and
         * initialize it to stock.
         */
        final double all = WeightType.sumWeights(weightMap);
        WeightType type = WeightType.STOCK;

        /*
         * Get the old stock weight, and calculate the new stock weight. Make
         * sure that the new stock weight is not less than zero, and not
         * greater than all the weight.
         */
        final double oldStock = weightMap.get(type);
        final double newStock = (0. < all) ? Math.max(0.,
                Math.min((oldStock / all + ratio) * all, all)) : 0.;

        /*
         * Put the new stock weight in the weight map. Calculate the old
         * non-stock weight. Is the old non-stock weight not equal to zero?
         */
        weightMap.put(type, newStock);
        final double oldNonStock = all - oldStock;
        if (0 != oldNonStock) {

            /*
             * The old non-stock weight is not equal to zero. Calculate the
             * non-stock ratio. Calculate the new bond weight, and put it in
             * the weight map.
             */
            final double nonStockRatio = (all - newStock) / oldNonStock;
            type = WeightType.BOND;
            weightMap.put(type, weightMap.get(type) * nonStockRatio);

            // Calculate the new cash weight, and put it in the weight map.
            type = WeightType.CASH;
            weightMap.put(type, weightMap.get(type) * nonStockRatio);

            /*
             * Calculate the new real-estate weight, and put it in the weight
             * map.
             */
            type = WeightType.REAL_ESTATE;
            weightMap.put(type, weightMap.get(type) * nonStockRatio);
        }
    }

    /**
     * Adjust a weight map for market valuation today versus last close.
     *
     * @param weightMap A weight map
     */
    private static void adjustVsClose(@NotNull Map<? super WeightType,
            Double> weightMap) {

        /*
         * Declare the weight type with which we are working. Get the ratio of
         * the S&P 500 today versus S&P 500 last close. Is the ratio infinite
         * (meaning: The value of equities at the last close was zero)?
         */
        final WeightType equities = WeightType.STOCK;
        final double ratio = manager.getRatioVersusClose();
        if (Double.isInfinite(ratio)) {

            /*
             * The value of equities at the last close was zero. Set the weight
             * of equities to zero in the weight map.
             */
            weightMap.put(equities, 0.);
        }

        /*
         * The ratio is not infinite. Is it something other than one (meaning,
         * the S&P 500 today is different from the S&P 500 at the last close)?
         */
        else if (1. != ratio) {

            /*
             * The S&P 500 today is different from what it was at the last
             * close. Declare and initialize the do-nothing constant. Sum the
             * level one weights. Is the level one weight sum different from
             * the do-nothing constant?
             */
            final double doNothingConstant = 0.;
            final double weightSum = WeightType.sumWeights(weightMap);
            if (doNothingConstant != weightSum) {

                /*
                 * The level one weight sum is different from the do-nothing
                 * constant. Calculate the percent of equities in the weight
                 * sum. Is the percent equities different from the do-nothing
                 * constant?
                 */
                final double percentEquities = weightMap.get(equities) /
                        weightSum;
                if (doNothingConstant != percentEquities) {

                    /*
                     * The percent of equities is different from the do-nothing
                     * constant. Adjust the percent of equities based on their
                     * current weight and the ratio the S&P 500 today versus
                     * last close.
                     */
                    adjust(weightMap, percentEquities / (percentEquities +
                            ratio - percentEquities * ratio) - percentEquities);
                }
            }
        }
    }

    /**
     * Builds the account list.
     */
    private static void buildAccountList() {

        // 1
        accountCollection.add(new Pair<>(WeightType.BOND,
                valueFromAccountMap.get(FundType.BOND)));

        // 2
        accountCollection.add(new Pair<>(WeightType.CASH,
                valueFromAccountMap.get(FundType.CASH)));

        // 3
        accountCollection.add(new Pair<>(WeightType.REAL_ESTATE,
                valueFromAccountMap.get(FundType.REAL_ESTATE)));

        // 4
        accountCollection.add(new Pair<>(WeightType.STOCK,
                valueFromAccountMap.get(FundType.STOCK)));
    }

    /**
     * Builds the detailed list.
     */
    private static void buildDetailedList() {

        // 1
        WeightType type = WeightType.BOND;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 2
        type = WeightType.BOND_CORPORATE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 3
        type = WeightType.BOND_FOREIGN;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 4
        type = WeightType.BOND_GOVERNMENT;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 5
        type = WeightType.BOND_HIGH;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 6
        type = WeightType.BOND_INFLATION;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 7
        type = WeightType.BOND_MORTGAGE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 8
        type = WeightType.BOND_MUNICIPAL;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 9
        type = WeightType.BOND_SHORT;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 10
        type = WeightType.BOND_UNCATEGORIZED;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 11
        type = WeightType.CASH;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 12
        type = WeightType.CASH_GOVERNMENT;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 13
        type = WeightType.CASH_UNCATEGORIZED;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 14
        type = WeightType.REAL_ESTATE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 15
        type = WeightType.STOCK;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 16
        type = WeightType.STOCK_DOMESTIC;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 17
        type = WeightType.STOCK_FOREIGN;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 18
        type = WeightType.STOCK_LARGE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 19
        type = WeightType.STOCK_GROWTH;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 20
        type = WeightType.STOCK_GROWTH_AND_VALUE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 21
        type = WeightType.STOCK_GROWTH_OR_VALUE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 22
        type = WeightType.STOCK_MEDIUM;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 23
        type = WeightType.STOCK_NOT_LARGE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 24
        type = WeightType.STOCK_SMALL;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));

        // 25
        type = WeightType.STOCK_VALUE;
        detailedCollection.add(new Pair<>(type,
                valueFromDetailedMap.get(type)));
    }

    /**
     * Builds the portfolio list.
     */
    private static void buildPortfolioList() {

        /*
         * Cycle for each level one weight type, and add a new pair to the
         * portfolio list.
         */
        for (WeightType type : getLevelOne()) {
            portfolioList.add(new Pair<>(type,
                    valueFromPortfolioMap.get(type)));
        }
    }

    /**
     * Creates a map of identifier types to description valuation objects.
     *
     * @param values           An array of all possible identifier values
     * @param factory          A factory for producing description valuation
     *                         objects
     * @param <IdentifierType> The identifier type
     * @param <ProductType>    The description valuation type
     * @return A map of identifier types to description valuation objects
     */
    private static <IdentifierType, ProductType> @NotNull
            Map<IdentifierType, ProductType> createMap(
            @NotNull IdentifierType @NotNull [] values,
            AccountRebalancer.@NotNull Factory<IdentifierType,
                    ? extends ProductType> factory) {

        // Create a new map, and cycle for each possible value.
        final Map<IdentifierType, ProductType> map = new HashMap<>();
        for (IdentifierType type : values) {

            /*
             * Add a new description valuation object to the map index by
             * identifier type.
             */
            map.put(type, factory.produce(type));
        }

        // Return the map.
        return map;
    }

    /**
     * Gets the level one weight types.
     *
     * @return The level one weight types
     */
    protected static @NotNull List<WeightType> getLevelOne() {
        return levelOne;
    }

    /**
     * Creates a weight map for an account.
     *
     * @param account      The account for which to create a weight map
     * @param procedure    The overlay procedure for the map
     * @param adjustVsHigh True if the map should be adjusted for relative
     *                     market valuation; false otherwise
     * @return A weight map for the account
     */
    private static @NotNull Map<WeightType, Double> getWeights(
            @NotNull Account account, @NotNull OverlayProcedure procedure,
            boolean adjustVsHigh) {

        // Declare an empty weight map.
        final Map<WeightType, Double> weightMap =
                new EnumMap<>(WeightType.class);

        /*
         * Initialize the weight map. Perform the given overlay procedure with
         * the initialized weight map and the account.
         */
        initializeMap(weightMap);
        procedure.overlay(weightMap, account);

        /*
         * Adjust the weight map for the market valuation today versus its last
         * close. Should the weight map be adjusted again for its valuation
         * today versus market high?
         */
        adjustVsClose(weightMap);
        if (adjustVsHigh) {

            /*
             * The weight map should be adjusted again for its valuation today
             * versus market high. Get the ratio of the market today versus
             * high. Is the ratio infinite (meaning: The value of equities at
             * market high was zero)?
             */
            final double ratio = manager.getRatioVersusHigh();
            if (Double.isInfinite(ratio)) {

                /*
                 * The value of equities at market high was zero. Set the
                 * weight of equities to zero in the weight map.
                 */
                weightMap.put(WeightType.STOCK, 0.);
            }

            /*
             * The value of equities at market high was not zero. If there is a
             * difference between the value of equities today versus the value
             * of equities at market high, do the following: Call the equity
             * adjustment procedure, and apply its results to the weight map.
             */
            else if (0. != ratio) {
                adjust(weightMap, procedure.adjustEquity(ratio));
            }
        }

        // Return the weight map.
        return weightMap;
    }

    /**
     * Creates a weight map for a closure rebalance.
     *
     * @param account The account for which to create a weight map
     * @param adjust  True if the map should be adjusted for relative market
     *                valuation; false otherwise
     * @return A weight map for the account
     */
    public static @NotNull Map<WeightType, Double> getWeightsForClosure(
            @NotNull Account account, boolean adjust) {
        return getWeights(account, closure, adjust);
    }

    /**
     * Creates a weight map for a percentage rebalance.
     *
     * @param account The account for which to create a weight map
     * @param adjust  True if the map should be adjusted for relative market
     *                valuation; false otherwise
     * @return A weight map for the account
     */
    public static @NotNull Map<WeightType, Double> getWeightsForPercentage(
            @NotNull Account account, boolean adjust) {
        return getWeights(account, percentage, adjust);
    }

    /**
     * Initializes a weight map to default values.
     *
     * @param weightMap A weight map
     */
    private static void initializeMap(@NotNull Map<? super WeightType,
            ? super Double> weightMap) {

        // This is the only place default weight values should be hard-coded.
        weightMap.put(WeightType.ALL, everything);
        weightMap.put(WeightType.BOND, 36.);
        weightMap.put(WeightType.BOND_CORPORATE, 12.5);
        weightMap.put(WeightType.BOND_FOREIGN, 7.);
        weightMap.put(WeightType.BOND_GOVERNMENT, nothing);
        weightMap.put(WeightType.BOND_HIGH, 5.);
        weightMap.put(WeightType.BOND_INFLATION, 5.);
        weightMap.put(WeightType.BOND_MORTGAGE, 8.);
        weightMap.put(WeightType.BOND_MUNICIPAL, nothing);
        weightMap.put(WeightType.BOND_UNCATEGORIZED, 12.5);
        weightMap.put(WeightType.BOND_SHORT, 50.);
        weightMap.put(WeightType.CASH, 10.);
        weightMap.put(WeightType.CASH_GOVERNMENT, 50.);
        weightMap.put(WeightType.CASH_UNCATEGORIZED, 50.);
        weightMap.put(WeightType.REAL_ESTATE, 4.);
        weightMap.put(WeightType.STOCK, 50.);
        weightMap.put(WeightType.STOCK_DOMESTIC, 60.);
        weightMap.put(WeightType.STOCK_FOREIGN, 40.);
        weightMap.put(WeightType.STOCK_GROWTH, 40.);
        weightMap.put(WeightType.STOCK_GROWTH_AND_VALUE, 50.);
        weightMap.put(WeightType.STOCK_GROWTH_OR_VALUE, 50.);
        weightMap.put(WeightType.STOCK_LARGE, 60.);
        weightMap.put(WeightType.STOCK_MEDIUM, 50.);
        weightMap.put(WeightType.STOCK_NOT_LARGE, 40.);
        weightMap.put(WeightType.STOCK_SMALL, 50.);
        weightMap.put(WeightType.STOCK_VALUE, 60.);
    }

    /**
     * Overlays a weight map, first with weights specified in an account
     * description, second with weights specified in a detailed description.
     *
     * @param weightMap The weight map receiving overlays
     * @param account   An account object
     */
    private static void overlay(@NotNull Map<WeightType, Double> weightMap,
                                @NotNull Account account) {

        /*
         * Set the description in the account collection and overlay the weight
         * map if the description is not null.
         */
        if (setDescription(accountCollection,
                account.getDescription())) {
            AccountRebalancer.overlay(weightMap,
                    accountCollection);
        }

        /*
         * Set the description in the detailed collection, and overlay the
         * weight map a second time if the description is not null.
         */
        if (setDescription(detailedCollection,
                DetailedLibrary.getInstance().
                        getDescription(account.getKey()))) {
            AccountRebalancer.overlay(weightMap,
                    detailedCollection);
        }
    }

    /**
     * Overlays values in a weight map.
     *
     * @param weightMap         The weight map to modify
     * @param pairs             A list of weight type to value wrapper pairs
     * @param <DescriptionType> The description type
     * @param <WrapperType>     The wrapper type
     */
    private static <DescriptionType extends Description<?>,
            WrapperType extends ValueWrapper<?, DescriptionType>>
    void overlay(@NotNull Map<? super WeightType, ? super Double> weightMap,
                 @NotNull Iterable<? extends Pair<WeightType,
                         WrapperType>> pairs) {

        // Cycle for each pair in the list, and overlay the value.
        for (Pair<WeightType, WrapperType> pair : pairs) {
            overlayValue(weightMap, pair.getFirst(), pair.getSecond());
        }
    }

    /**
     * Overlays value in a weight map.
     *
     * @param weightMap  The weight map to modify
     * @param weightType The weight type to modify
     * @param wrapper    A value wrapper
     */
    private static void overlayValue(
            @NotNull Map<? super WeightType, ? super Double> weightMap,
            @NotNull WeightType weightType,
            @NotNull ValueWrapper<?, ?> wrapper) {

        // Get value from the wrapper. Is the value not null?
        final Double value = wrapper.getValue();
        if (null != value) {

            /*
             * The value is not null. Modify the weight map using the weight
             * type and the value.
             */
            weightMap.put(weightType, value);
        }
    }

    /**
     * Sets the description in an iterable of value wrappers.
     *
     * @param pairs             An iterable of weight type to value wrapper
     *                          pairs
     * @param <DescriptionType> The description type
     * @param <WrapperType>     The wrapper type
     * @return True if the given description was not null; false otherwise
     */
    private static <DescriptionType extends Description<?>,
            WrapperType extends ValueWrapper<?, DescriptionType>>
    boolean setDescription(@NotNull Iterable<? extends Pair<WeightType,
            WrapperType>> pairs, DescriptionType description) {

        // Set the description in the second element of each pair.
        for (Pair<WeightType, WrapperType> pair : pairs) {
            pair.getSecond().setDescription(description);
        }

        // Return if the description was not null.
        return (null != description);
    }

    /**
     * Rebalances an account.
     *
     * @param account The account to rebalance
     * @return The residual of the rebalance operation if the rebalance was
     * successful; null otherwise
     */
    protected abstract Currency doRebalance(@NotNull Account account);

    /**
     * Gets the message logger for the account rebalancer.
     *
     * @return The message logger for the account rebalancer
     */
    private MessageLogger getLogger() {
        return messageLogger;
    }

    /**
     * Returns whether there was a problem with a rebalance.
     *
     * @return True if there was a problem with a rebalance, false otherwise
     */
    public boolean hadProblem() {

        /*
         * Get the message logger and return whether problem one or problem
         * two is set.
         */
        final MessageLogger logger = getLogger();
        return logger.hadProblem1() || logger.hadProblem2();
    }

    /**
     * Rebalances an account.
     *
     * @param account The account to rebalance
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    public boolean rebalance(@NotNull Account account) {

        // Declare and initialize local variables.
        final AccountKey key = account.getKey();
        final MessageLogger logger = getLogger();

        // Reset the message logger. Stream and log a rebalance start message.
        reset();
        logger.streamAndLog(extraordinary, String.format("Rebalancing has " +
                "commenced for account: %s.", key));

        /*
         * Rebalance the account, receiving a residual. The rebalance was
         * successful if the residual is not null. Was the operation
         * successful?
         */
        final Currency residual = doRebalance(account);
        final boolean result = (null != residual);
        if (result) {

            /*
             * The operation was successful, therefore the residual is not
             * null. Is it zero? It would be best if it were not zero.
             */
            if (residual.isNotZero()) {

                // Drat, the residual is not zero. Stream and log a message.
                logger.log(extraordinary, String.format("BAD NEWS...You " +
                        "will not like this: Rebalancing has discovered " +
                        "a non-zero residual %s for account %s; " +
                        "deal with it.", residual, account));
            }

            // Set the non-null residual in the account, zero or not.
            account.setResidual(residual);
        }

        // Stream and log a rebalance complete message.
        logger.streamAndLog(extraordinary, String.format("Rebalancing has " +
                        "completed for account: %s with%s error.", key,
                result ? " no" : ""));

        /*
         * Return true if this rebalancer had no problem, and the result from
         * the rebalance operation was successful.
         */
        return (!hadProblem()) && result;
    }

    /**
     * Resets the message logger.
     */
    public void reset() {
        getLogger().resetProblem();
    }

    @FunctionalInterface
    private interface Factory<IdentifierType, ProductType> {

        /**
         * Produces a product of the indicated type.
         *
         * @param identifierType The identifier for the product
         * @return A product of the indicated type
         */
        @NotNull ProductType produce(@NotNull IdentifierType identifierType);
    }

    private interface OverlayProcedure {

        /**
         * Adjusts an equity ratio.
         *
         * @param ratio The given equity ratio
         * @return An adjusted equity ratio
         */
        double adjustEquity(double ratio);

        /**
         * Overlays values in a weight map.
         *
         * @param weightMap The weight map in which to overlay values
         * @param account   An account object that supplies descriptors
         */
        void overlay(@NotNull Map<WeightType, Double> weightMap,
                     @NotNull Account account);
    }

    private static class ValueFromAccount extends
            ValueWrapper<FundType, AccountDescription> {

        /**
         * Constructs the value-from-account valuator.
         *
         * @param type A fund type for the valuator
         */
        public ValueFromAccount(@NotNull FundType type) {
            super(type);
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double getValue() {

            /*
             * Get the account description. Return null if the description is
             * null. Otherwise, return the allocation indicated by the
             * identifier.
             */
            final AccountDescription description = getDescription();
            return (null == description) ? null :
                    description.getAllocation(getIdentifier());
        }
    }

    private static class ValueFromDetailed extends
            ValueWrapper<WeightType, DetailedDescription> {

        /**
         * Constructs the value-from-detailed valuator.
         *
         * @param type A weight type for the valuator
         */
        public ValueFromDetailed(@NotNull WeightType type) {
            super(type);
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double getValue() {

            /*
             * Get the detailed description. Return null if the description is
             * null. Otherwise, return the allocation indicated by the
             * identifier.
             */
            final DetailedDescription description = getDescription();
            return (null == description) ? null :
                    description.getAllocation(getIdentifier());
        }
    }

    private static class ValueFromPortfolio extends ValueWrapper<WeightType,
            PortfolioDescription> {

        /**
         * Constructs the value-from-portfolio valuator.
         *
         * @param type A weight type for the valuator
         */
        protected ValueFromPortfolio(@NotNull WeightType type) {
            super(type);
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double getValue() {

            /*
             * Get the portfolio description. Return null if the description is
             * null. Otherwise, return the allocation indicated by the
             * identifier.
             */
            final PortfolioDescription description = getDescription();
            return (null == description) ? null :
                    description.getAllocation(getIdentifier());
        }
    }

    private abstract static class ValueWrapper<IdentifierType,
            DescriptionType extends Description<?>> {

        // The identifier for the wrapper
        private final IdentifierType identifier;

        // The wrapped description
        private DescriptionType description;

        /**
         * Constructs the value wrapper.
         *
         * @param identifier The identifier for the wrapper
         */
        protected ValueWrapper(@NotNull IdentifierType identifier) {
            this.identifier = identifier;
        }

        /**
         * Gets the wrapped description.
         *
         * @return The wrapped description
         */
        protected DescriptionType getDescription() {
            return description;
        }

        /**
         * Gets the identifier assigned to the wrapper.
         *
         * @return The identifier assigned to the wrapper
         */
        public @NotNull IdentifierType getIdentifier() {
            return identifier;
        }

        /**
         * Gets the value from the description.
         *
         * @return The value from the description
         */
        public abstract Double getValue();

        /**
         * Sets the description.
         *
         * @param description The description
         */
        public void setDescription(DescriptionType description) {
            this.description = description;
        }
    }
}
