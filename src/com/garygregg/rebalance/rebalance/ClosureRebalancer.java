package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.toolkit.BreakdownType;
import com.garygregg.rebalance.toolkit.MessageLogger;
import com.garygregg.rebalance.toolkit.WeightType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.logging.Logger;

class ClosureRebalancer extends WeightRebalancer {

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
         * Sum the existing level one weights in the weight map. Declare a
         * variable to receive a weight from the map.
         */
        final double sum = sum(weightMap);
        Double weight;
        for (WeightType type : getLevelOne()) {

            /*
             * Get the existing weight for the first/next level one type.
             * Replace (or add, if necessary) the mapping for the type by
             * multiplying the existing weight by the considered value of the
             * portfolio, and dividing by the calculated weight sum.
             */
            weight = weightMap.get(type);
            weightMap.put(type, ((null == weight) ? 0. : weight) *
                    considered / sum);
        }
    }

    /**
     * Sums the level one weights in a weight map.
     *
     * @param weightMap The weight map
     * @return The sum of the level one weights in the map
     */
    private static double sum(@NotNull Map<WeightType, Double> weightMap) {

        /*
         * Declare and initialize the sum. Declare a variable to receive a
         * weight. Cycle for each level one weight type.
         */
        double sum = 0.;
        Double weight;
        for (WeightType type : getLevelOne()) {

            // Get the weight for the first/next type. Is the weight not null?
            weight = weightMap.get(type);
            if (null != weight) {

                // The weight is not null. Add it to the sum.
                sum += weight;
            }
        }

        // Return the sum.
        return sum;
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
