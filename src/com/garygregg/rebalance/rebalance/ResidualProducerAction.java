package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

abstract class ResidualProducerAction
        extends ContainerNodeAction<ReceiverDelegate<?>, SetValueUtility> {

    // The value of minus one
    private static final Currency minusOne = Currency.getMinusOne();

    // The value of zero
    private static final Currency zero = Currency.getZero();

    // A map of call-count indices to actual values
    private final Map<Integer, Currency> actualValues = new TreeMap<>();

    // A map of call-count indices to adjustments
    private final Map<Integer, MutableCurrency> adjustments = new TreeMap<>();

    // A map of call-count indices to means
    private final Map<Integer, Currency> means = new TreeMap<>();

    // The contained set-value utility
    private final SetValueUtility utility = getContained();

    // The count of calls to 'doAction' since last reset
    private int actionCallCount;

    /**
     * Constructs the residual producer action.
     */
    public ResidualProducerAction() {
        reset();
    }

    /**
     * Gets the value of minus one as currency.
     *
     * @return The value of minus one as currency
     */
    protected static @NotNull Currency getMinusOne() {
        return minusOne;
    }

    /**
     * Calculates the deviation between actual values and means.
     *
     * @return The deviation between actual values and means
     */
    private double calculateDeviation() {

        /*
         * Declare and initialize a variable to perform currency calculations.
         * Declare and initialize the accumulation. Cycle for each key in the
         * actual values set.
         */
        final MutableCurrency currency = new MutableCurrency(zero);
        double accumulation = 0.;
        for (Integer key : actualValues.keySet()) {

            /*
             * Get the first/next actual value. Add the adjustment associated
             * with the key.
             */
            currency.set(getActual(key));
            currency.add(getAdjustment(key));

            /*
             * Subtract the mean associated with the key. Square the result,
             * and add it to the accumulation.
             */
            currency.subtract(getMean(key));
            accumulation += Math.pow(currency.getValue(), 2.);
        }

        // Return the square root of the accumulated differences.
        return Math.sqrt(accumulation / actualValues.size());
    }

    /**
     * Clears the actual values map.
     */
    private void clearActualValues() {
        actualValues.clear();
    }

    /**
     * Clears both the actual values map and the means map.
     */
    public void clearMaps() {

        // Clear both the actual values map and the means map.
        clearActualValues();
        clearMeans();
    }

    /**
     * Clears the means map.
     */
    private void clearMeans() {
        means.clear();
    }

    @Override
    public void doAction(@NotNull ReceiverDelegate<?> delegate) {

        /*
         * Increment the action call count, then get the count. Is the delegate
         * considered?
         */
        incrementCount();
        final int index = getCount();
        if (isConsidered(delegate)) {

            /*
             * The delegate is considered. Get the incoming value from the
             * utility. Is the incoming value not null?
             */
            final MutableCurrency incoming = utility.getNextElement();
            if (null != incoming) {

                /*
                 * The incoming value is not null. Multiply the incoming value
                 * by minus one if the utility indicates that the incoming
                 * value is a negative number.
                 */
                if (utility.isNegative()) {
                    incoming.multiply(getMinusOne());
                }

                /*
                 * Get the current count to use as an index. Use the utility to
                 * determine if the incoming value is relative. Is the incoming
                 * value not relative?
                 */

                final boolean isRelative = utility.isRelative();
                if (!isRelative) {

                    /*
                     * The incoming value is not relative. Use the incoming
                     * value to set a mean for the index.
                     */
                    setMean(index, incoming.getImmutable());
                }

                /*
                 * Ask the delegate to produce residual using the incoming
                 * value.
                 */
                final Currency residual =
                        produceResidual(delegate, incoming.getImmutable(),
                                isRelative);

                /*
                 * Subtract the residual from the incoming value, and get its
                 * (possibly modified) value. Is the incoming value relative?
                 */
                incoming.subtract(residual);
                final Currency modifiedIncoming = incoming.getImmutable();
                if (isRelative) {

                    /*
                     * The incoming value is relative. Set the modified
                     * incoming value to the adjustment associated with the
                     * index.
                     */
                    setAdjustment(index, modifiedIncoming);
                }

                /*
                 * The incoming value is absolute. Set the modified incoming
                 * value to the actual value associated with the index.
                 */
                else {
                    setActual(index, modifiedIncoming);
                }

                /*
                 * Subtract the modified incoming value from the residual in
                 * the utility.
                 */
                utility.subtractResidual(modifiedIncoming);
            }
        }

        // The delegate is not considered.
        else {

            /*
             * Set the adjustment for the current index to zero, and clear the
             * last residual of the delegate.
             */
            setAdjustment(index, zero);
            delegate.clearLastResidual();
        }
    }

    /**
     * Gets the actual value associated with an index.
     *
     * @param index An index
     * @return The actual value associated with the index, or a default if no
     * actual value had been set since the last time the actual value map was
     * cleared
     */
    private @NotNull Currency getActual(int index) {

        /*
         * Put a default in the actual values map if an entry is absent for the
         * index. Return the non-null entry associated with the index.
         */
        actualValues.putIfAbsent(index, zero);
        return actualValues.get(index);
    }

    /**
     * Gets the adjustment associated with an index.
     *
     * @param index An index
     * @return The adjustment associated with the index, or a default if no
     * adjustment had been set since the last time the adjustment value map was
     * cleared
     */
    private @NotNull MutableCurrency getAdjustment(int index) {

        /*
         * Put a default in the adjustment map if an entry is absent for the
         * index. Return the non-null entry associated with the index.
         */
        adjustments.putIfAbsent(index, new MutableCurrency(zero));
        return adjustments.get(index);
    }

    /**
     * Gets the action call count.
     *
     * @return The action call count
     */
    private int getCount() {
        return actionCallCount;
    }

    @Override
    protected @NotNull SetValueUtility getInitialValue() {
        return new SetValueUtility();
    }

    /**
     * Gets the mean associated with an index.
     *
     * @param index An index
     * @return The mean associated with the index, or a default if no mean had
     * been set since the last time the means map was cleared
     */
    private @NotNull Currency getMean(int index) {

        /*
         * Put a default in the means map if an entry is absent for the index.
         * Return the non-null entry associated with the index.
         */
        means.putIfAbsent(index, zero);
        return means.get(index);
    }

    /**
     * Gets the reallocation score.
     *
     * @return The reallocation score
     */
    public @NotNull ReallocationScore getScore() {
        return new ReallocationScore(
                new Currency(utility.getResidual().getValue()),
                calculateDeviation());
    }

    /**
     * Increments the action call count.
     */
    private void incrementCount() {
        ++actionCallCount;
    }

    /**
     * Is the delegate to be considered for an action?
     *
     * @param delegate The delegate to consider
     * @return True if the delegate is to be considered for an action false
     * otherwise
     */
    protected abstract boolean isConsidered(
            @NotNull ReceiverDelegate<?> delegate);

    /**
     * Asks a receiver delegate to produce residual.
     *
     * @param delegate   The receiver delegate
     * @param currency   Incoming currency
     * @param isRelative True if the incoming currency is relative to any
     *                   existing value in the delegate; false otherwise
     * @return Residual from the operation
     */
    protected abstract @NotNull Currency produceResidual(
            @NotNull ReceiverDelegate<?> delegate,
            @NotNull Currency currency,
            boolean isRelative);

    @Override
    public void reset() {

        /*
         * Reset the index in the set-value utility. Reset the action call
         * count.
         */
        utility.resetIndex();
        resetCount();
    }

    /**
     * Resets the action call count.
     */
    private void resetCount() {
        actionCallCount = 0;
    }

    /**
     * Associates an actual value to an index.
     *
     * @param index An index
     * @param mean  An actual value to associate to the index
     * @return Any actual value previously associated with the index
     */
    @SuppressWarnings("UnusedReturnValue")
    private Currency setActual(int index, @NotNull Currency mean) {

        /*
         * Remove the adjustment associated with the index before setting the
         * actual value.
         */
        adjustments.remove(index);
        return actualValues.put(index, mean);
    }

    /**
     * Sets the adjustment value associated with an index.
     *
     * @param index      An index
     * @param adjustment The new adjustment value to associate with the index
     */
    private void setAdjustment(int index, @NotNull Currency adjustment) {

        // Get the adjustment value associated with the index, and set it.
        final MutableCurrency currency = getAdjustment(index);
        currency.set(adjustment);
    }

    /**
     * Sets the currency list.
     *
     * @param list The currency list
     */
    public void setList(@NotNull List<? extends MutableCurrency> list) {
        utility.setList(list);
    }

    /**
     * Associates a mean to an index.
     *
     * @param index An index
     * @param mean  A mean to associate to the index
     * @return Any mean previously associated with the index
     */
    @SuppressWarnings("UnusedReturnValue")
    private Currency setMean(int index, @NotNull Currency mean) {

        /*
         * Remove the adjustment associated with the index before setting the
         * mean.
         */
        adjustments.remove(index);
        return means.put(index, mean);
    }

    /**
     * Sets the 'relative' flag.
     *
     * @param relative True if the values in the currency list are to be
     *                 interpreted as relative values; false if they are to be
     *                 interpreted as absolute values
     */
    protected void setRelative(boolean relative) {
        utility.setRelative(relative);
    }

    /**
     * Sets the residual.
     *
     * @param residual The residual
     */
    public void setResidual(@NotNull Currency residual) {

        /*
         * Set the negative flag in the utility if the residual is less than
         * zero. Set the residual in the container, and reset the action.
         */
        utility.setNegative(0 > residual.compareTo(zero));
        utility.setResidual(residual);
        reset();
    }
}
