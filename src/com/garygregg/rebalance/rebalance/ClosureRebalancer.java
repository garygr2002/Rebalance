package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Logger;

class ClosureRebalancer extends WeightRebalancer {

    // An adjuster instance
    private static final Adjuster adjuster =
            PreferenceManager.getInstance().getAdjuster();

    // The default stock fraction at market high
    private static final double defaultStocksAtHigh = 0.45;

    // Zero currency
    private static final Currency zero = Currency.getZero();

    // Our message logger
    private final MessageLogger messageLogger = new MessageLogger();

    // The portfolio associated with the account to be rebalanced
    private Portfolio portfolio;

    {

        // Set the logger in the message logger.
        messageLogger.setLogger(Logger.getLogger(
                ClosureRebalancer.class.getCanonicalName()));
    }

    /**
     * Adjusts level one weights in a weight map to take into account the
     * considered value of a portfolio.
     *
     * @param weightMap  A weight map
     * @param considered The considered value of a portfolio
     */
    private static void adjust(@NotNull Map<WeightType, Double> weightMap,
                               double considered) {

        /*
         * Sum the existing level one weights in the weight map. Calculate the
         * level zero weight (sum of all investments). If the sum of the level
         * one weights is non-zero, then the level zero weight receives the
         * considered value of the portfolio divided by the level one weight
         * sum. Otherwise, the level zero weight receives zero. Checking the
         * sum of the level one weights against zero prevents the level zero
         * weight from receiving a NaN caused by divide-by-zero.
         */
        final double sum = WeightType.sumWeights(weightMap);
        final double levelZeroWeight = (0. < sum) ? (considered / sum) : 0.;

        /*
         * Declare a variable to receive a weight from the map. Cycle for each
         * level one type.
         */
        Double weight;
        for (WeightType type : getLevelOne()) {

            /*
             * Get the existing weight for the first/next level one type.
             * Replace (or add, if necessary) the mapping for the type by
             * multiplying the existing weight by the level zero weight.
             */
            weight = weightMap.get(type);
            weightMap.put(type, ((null == weight) ? 0. : weight) *
                    levelZeroWeight);
        }
    }

    /**
     * Checks for a non-null stock allocation on the path to setting the
     * y-values of the adjuster.
     *
     * @param description             A portfolio description
     * @param positiveLevelZeroWeight A level zero weight that is known to be
     *                                positive
     */
    private static void checkAllocation(
            @NotNull PortfolioDescription description,
            double positiveLevelZeroWeight) {

        // Get the allocation of weight to stock. Is the allocation not null?
        final Double stockAllocation =
                description.getAllocation(WeightType.STOCK);
        if (null != stockAllocation) {

            /*
             * The allocation of weight to stock is not null. Calculate the
             * fraction of stock desired when the market is at high, and check
             * that the market-zero stock adjustment is not null.
             */
            checkZero(description, stockAllocation / positiveLevelZeroWeight);
        }

        /*
         * The stock allocation is null. Set nearly-a-line characteristics with
         * the default stock fraction at market high.
         */
        else {
            adjuster.setNearlyALine(defaultStocksAtHigh);
        }
    }

    /**
     * Checks for a non-null portfolio description on the path to setting
     * y-values of the adjuster.
     *
     * @param description A portfolio description
     */
    private static void checkDescription(PortfolioDescription description) {

        /*
         * Check for positive level zero weight if the portfolio description is
         * not null.
         */
        if (null != description) {
            checkPositiveWeight(description, description.sumWeights());
        }

        /*
         * The portfolio description is null. Set nearly-a-line characteristics
         * with the default stock percentage at market high.
         */
        else {
            adjuster.setNearlyALine(defaultStocksAtHigh);
        }
    }

    /**
     * Checks for positive level zero weight on the path to setting the
     * y-values of the adjuster.
     *
     * @param description     A portfolio description
     * @param levelZeroWeight The summed level zero weight
     */
    private static void checkPositiveWeight(
            @NotNull PortfolioDescription description,
            double levelZeroWeight) {

        /*
         * Check the stock allocation if the level zero weight is greater than
         * zero.
         */
        if (0. < levelZeroWeight) {
            checkAllocation(description, levelZeroWeight);
        }

        /*
         * The level zero weight is zero or less. How did that happen? Sounds
         * like a bug. Set nearly-a-line characteristics with the default stock
         * fraction at market high.
         */
        else {
            adjuster.setNearlyALine(defaultStocksAtHigh);
        }
    }

    /**
     * Checks for a non-null market-zero stock adjustment on the path to
     * setting the y-values of the adjuster.
     *
     * @param description A portfolio description
     * @param high        The desired allocation to stock at market high
     */
    private static void checkZero(@NotNull PortfolioDescription description,
                                  double high) {

        /*
         * Get the desired increase at market-zero. Is the desired increase at
         * market-zero not null?
         */
        final Double zero = description.getIncreaseAtZero();
        if (null != zero) {

            /*
             * The desired increase at market zero is not null. Set the
             * y-values of the adjuster.
             */
            setAdjuster(description, high, zero);
        }

        /*
         * The desired increase at market-zero is null. Set nearly-a-line
         * characteristics with the given market high.
         */
        else {
            adjuster.setNearlyALine(high);
        }
    }

    /**
     * Sets the y-values of the adjuster.
     *
     * @param description A portfolio description
     * @param high        The desired allocation to stocks at market high
     * @param zero        The desired adjustment to stocks at market zero
     */
    private static void setAdjuster(@NotNull PortfolioDescription description,
                                    double high, double zero) {

        /*
         * Get the desired adjustment to stocks at the bear market threshold.
         * Is the desired adjustment to stocks at the bear market threshold
         * null?
         */
        Double bear = description.getIncreaseAtBear();
        if (null == bear) {

            /*
             * The desired adjustment to stocks at the bear market threshold is
             * null. Use a default of half the adjustment to market zero.
             */
            bear = high / 2.;
        }

        /*
         * Add the desired adjustment for bear market, and market zero to high
         * to get the total desired allocations for these circumstances. Set
         * the y-values of the adjuster.
         */
        final double percentToFraction = 100.;
        adjuster.setY(high, bear / percentToFraction + high,
                zero / percentToFraction + high);
    }

    @Override
    protected Currency doRebalance(@NotNull Account account) {

        /*
         * Set the y-values of the adjuster if possible. Call the superclass to
         * do the rebalance, and receive the result.
         */
        checkDescription(account.getPortfolioDescription());
        final Currency currency = super.doRebalance(account);

        // Reset the adjuster, and return the result of the rebalance.
        adjuster.setNearlyALine(defaultStocksAtHigh);
        return currency;
    }

    /**
     * Gets the message logger for the closure rebalancer.
     *
     * @return The message logger for the closure rebalancer
     */
    private MessageLogger getLogger() {
        return messageLogger;
    }

    /**
     * Gets the portfolio associated with the account to be rebalanced.
     *
     * @return The portfolio associated with the account be rebalanced
     */
    public Portfolio getPortfolio() {
        return portfolio;
    }

    @Override
    protected @NotNull Map<WeightType, Double> getWeights(
            @NotNull Account account, boolean adjust) {

        // Get the weights-for-closure.
        final Map<WeightType, Double> weightMap = getWeightsForClosure(account,
                adjust);

        // Get the portfolio and its value.
        final Portfolio portfolio = getPortfolio();
        final Currency portfolioValue = (null == portfolio) ? zero :
                portfolio.getConsidered();

        /*
         * Adjust the portfolio map with the portfolio value. Is the current
         * portfolio not null?
         */
        adjust(weightMap, portfolioValue.getValue());
        if (null != portfolio) {

            /*
             * The current portfolio is not null. Break down the proposed
             * values of the portfolio, and subtract existing proposed values
             * from desired values for the level zero weight types.
             */
            portfolio.breakdown(BreakdownType.PROPOSED);
            subtract(weightMap, portfolio);
        }

        // Return the modified weight map.
        return weightMap;
    }

    /**
     * Sets the portfolio associated with the account to be rebalanced.
     *
     * @param portfolio The portfolio associated with the account to be
     *                  rebalanced
     */
    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Subtracts existing values in a portfolio from desired values for level
     * one weight types.
     *
     * @param weightMap The weight map to modify
     * @param portfolio A portfolio
     */
    private void subtract(@NotNull Map<? super WeightType, Double> weightMap,
                          @NotNull Portfolio portfolio) {

        /*
         * Declare desired and existing weights. Cycle for each level one
         * weight type.
         */
        double desired, existing;
        for (WeightType type : getLevelOne()) {

            /*
             * Get the desired and existing weights. Is the existing value less
             * than the desired value?
             */
            desired = weightMap.get(type);
            existing = portfolio.getProposed(type).getValue();
            if (existing < desired) {

                /*
                 * The existing value is less than the desired value. Subtract
                 * the existing value from the desired value, and replace the
                 * value in the weight map.
                 */
                weightMap.put(type, desired - existing);
            }

            // The existing value is *not* less than the desired value.
            else {

                /*
                 * Log and stream a message if the desired value is less than
                 * the existing value.
                 */
                if (desired < existing) {
                    getLogger().streamAndLog(MessageLogger.getExtraordinary(),
                            String.format("At rebalance closure, portfolio " +
                                            "'%s' already has %s for weight " +
                                            "type %s; desired value is %s.",
                                    portfolio.getKey(),
                                    Currency.format(existing), type,
                                    Currency.format(desired)));
                }

                // Replace the existing value in the weight map with zero.
                weightMap.put(type, zero.getValue());
            }
        }
    }
}
