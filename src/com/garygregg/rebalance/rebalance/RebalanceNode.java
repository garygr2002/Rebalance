package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class RebalanceNode implements CurrencyReceiver {

    // An action to notify delegates that a rebalance cannot occur.
    private static final Action<ReceiverDelegate<?>> cannotRebalanceAction =
            ReceiverDelegate::onCannotSet;

    // An action to notify delegates to clear snapshots
    private static final SnapshotAction clearSnapshotAction =

            new SnapshotAction() {
                @Override
                public void doAction(@NotNull ReceiverDelegate<?> delegate) {
                    delegate.clearSnapshot(getContained());
                }
            };

    // An action to notify delegates to clear their snapshots
    private static final Action<ReceiverDelegate<?>> clearSnapshotsAction =
            ReceiverDelegate::clearSnapshots;

    // The format for reporting deviation values
    private static final DecimalFormat deviationFormat =
            new DecimalFormat("0.00");

    // The logging level for extraordinary informational messages
    private final static Level extraordinary = MessageLogger.getOrdinary();

    // An action to calculate initial residual and deviation
    private static final InitialScoreAction initialScoreAction =
            new InitialScoreAction();

    // The preference manager
    private static final PreferenceManager manager =
            PreferenceManager.getInstance();

    // The limit of allowed receiver delegates
    private static final int limit = calculateLimit();

    // Our local message logger
    private static final MessageLogger messageLogger = new MessageLogger();

    // The logging level for ordinary informational messages
    private final static Level ordinary = MessageLogger.getOrdinary();

    // An action to notify delegates to recover their snapshots
    private static final SnapshotAction recoverSnapshotAction =
            new SnapshotAction() {

                @Override
                public void doAction(@NotNull ReceiverDelegate<?> delegate) {
                    delegate.recoverSnapshot(getContained());
                }
            };

    // An action to notify delegates to take snapshots
    private static final SnapshotAction takeSnapshotAction =
            new SnapshotAction() {
                @Override
                public void doAction(@NotNull ReceiverDelegate<?> delegate) {
                    delegate.takeSnapshot(getContained());
                }
            };

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    // The key of the account that is being rebalanced
    private static AccountKey accountKey;

    // The children of the node
    private final SortedMap<WeightType, NodeDelegate> children =
            new TreeMap<>();

    // The child values
    private final Collection<NodeDelegate> childValues = children.values();

    // The consideration setter action
    private final ConsiderationSetterAction considerationSetterAction =
            new ConsiderationSetterAction();

    // The sum current action
    private final SumCurrentAction sumCurrentAction = new SumCurrentAction();

    // The tickers in the node
    private final SortedSet<TickerDelegate> tickerSet =
            new TreeSet<>(Comparator.comparing(tickerDelegate ->
                    tickerDelegate.getReceiver().getKey()));

    // The weight type assigned to the node
    private final WeightType type;

    // The value setter action
    private final ValueSetterAction valueSetterAction =
            new ValueSetterAction();

    // The weight of the node
    private final double weight;

    // The weight accumulator action
    private final WeightAccumulatorAction weightAccumulatorAction =
            new WeightAccumulatorAction();

    // The value assigned to the node
    private Currency value;

    /**
     * Constructs a rebalance node.
     *
     * @param type   The weight type assigned to the node
     * @param weight The weight of the node
     */
    public RebalanceNode(@NotNull WeightType type, double weight) {

        // Set the weight type and weight.
        this.type = type;
        this.weight = weight;

        // Set the logger inside the message logger.
        getLogger().setLogger(Logger.getLogger(
                RebalanceNode.class.getCanonicalName()));
    }

    /**
     * Does the function this class will use when accepting a deviation from a
     * desired rebalanced quantity.
     *
     * @param deviation A deviation from a desired rebalanced quantity
     * @return The result of the function
     */
    public static double calculateDeviation(double deviation) {
        return Math.pow(deviation, 2.);
    }

    /**
     * Does the function this class will use when calculating a deviation score.
     *
     * @param deviationAggregate An aggregate of deviations
     * @return The result of the function
     */
    public static double calculateDeviationScore(double deviationAggregate) {
        return Math.sqrt(deviationAggregate);
    }

    /**
     * Calculates the limit of allowed receiver delegates.
     *
     * @return The limit of allowed receiver delegates
     */
    private static int calculateLimit() {

        /*
         * Get the limit of allowed receiver delegates from the preference
         * manager. Is the preference null?
         */
        Integer limit = manager.getLimit();
        if (null == limit) {

            /*
             * The preference is null. As a default, use one more than the
             * number of children of the weight type that has the most
             * children.
             */
            limit = WeightType.getMaxChildren() + 1;
        }

        /*
         * Return the limit as the smallest of the calculated limit and the
         * size of an integer.
         */
        return Integer.min(limit, Integer.SIZE);
    }

    /**
     * Performs an action on each object in a collection.
     *
     * @param collection The collection
     * @param action     The action to perform on each object in the collection
     * @param <T>        An arbitrary type
     */
    private static <T> void doAction(@NotNull Collection<? extends T> collection,
                                     @NotNull Action<T> action) {

        // Cycle for each object in the collection, and perform the action.
        for (T object : collection) {
            action.doAction(object);
        }
    }

    /**
     * Gets the key of the account being rebalanced.
     *
     * @return The key of the account being rebalanced
     */
    public static AccountKey getAccountKey() {
        return accountKey;
    }

    /**
     * Gets an initial currency list.
     *
     * @param size              The size of the desired list
     * @param valueToDistribute The value to distribute
     * @return An initial currency list
     */
    private static @NotNull List<MutableCurrency>
    getInitialList(int size, @NotNull Currency valueToDistribute) {

        // Create the list. Is the size greater than zero?
        final List<MutableCurrency> list = new ArrayList<>();
        if (0 < size) {

            /*
             * The size is greater than zero. Add the first element to the
             * list using the absolute value of the value to distribute.
             */
            int i = 0;
            list.add(i, new MutableCurrency(
                    Math.abs(valueToDistribute.getValue())));

            // Cycle for any remaining positions, and add zero currency.
            for (++i; i < size; ++i) {
                list.add(new MutableCurrency(zero));
            }
        }

        // Return the list.
        return list;
    }

    /**
     * Gets the limit of allowed receiver delegates.
     *
     * @return The limit of allowed receiver delegates
     */
    private static int getLimit() {
        return limit;
    }

    /**
     * Gets the message logger for the node.
     *
     * @return The message logger for the node
     */
    private static @NotNull MessageLogger getLogger() {
        return messageLogger;
    }

    /**
     * Returns whether there was a problem with a rebalance.
     *
     * @return True if there was a problem with a rebalance, false otherwise
     */
    public static boolean hadProblem() {

        /*
         * Get the message logger and return whether the problem one or problem
         * two flags are set.
         */
        final MessageLogger logger = getLogger();
        return logger.hadProblem1() || logger.hadProblem2();
    }

    /**
     * Determines if any receiver delegate in a collection has positive weight.
     *
     * @param delegates A collection of delegates
     * @return True if any receiver delegate in the collection has positive
     * weight; false otherwise
     */
    private static <T extends ReceiverDelegate<?>> boolean hasAnyWeight(
            @NotNull Collection<T> delegates) {

        /*
         * Declare and initialize the result. Get an iterator for the receiver
         * delegates. Cycle while no delegate has weight, and delegates exist.
         */
        boolean hasWeight = false;
        final Iterator<T> iterator = delegates.iterator();
        while ((!hasWeight) && iterator.hasNext()) {

            // Reinitialize the result.
            hasWeight = (0. < iterator.next().getWeight());
        }

        // Return the result.
        return hasWeight;
    }

    /**
     * Produces a list of integers sorted by descending count of set bits.
     *
     * @param n Integers in the resulting list will be less than 2 ^ (n - 1)
     * @return A list of integers sorted by descending count of set bits
     */
    @Contract(pure = true)
    private static @NotNull @UnmodifiableView List<Integer> produce(int n) {

        // The argument cannot be negative.
        if (0 > n) {
            throw new IllegalArgumentException(
                    String.format("Negative argument %d not allowed", n));
        }

        /*
         * The argument cannot be larger than the limit of allowed receiver
         * delegates.
         */
        else if (getLimit() <= n) {
            throw new IllegalArgumentException(
                    String.format("Argument %d is too big", n));
        }

        /*
         * Create the list. Calculate the count of integers, and cycle for
         * each.
         */
        final List<Integer> list = new ArrayList<>();
        final long count = 1L << n;
        for (long i = 1; i < count; ++i) {

            // Add the first/next integer to the list.
            list.add((int) i);
        }

        // Sort the list.
        list.sort((first, second) -> {

            // Calculate the bit count difference.
            final int bitCountDifference = Integer.bitCount(second) -
                    Integer.bitCount(first);

            /*
             * Return the difference of the integers if their bit counts are
             * the same. Otherwise, return the difference in their bit counts.
             */
            return (0 == bitCountDifference) ? second - first :
                    bitCountDifference;
        });

        // Return an unmodifiable copy of the sorted list.
        return Collections.unmodifiableList(list);
    }

    /**
     * Resets the message logger.
     */
    private static void reset() {
        getLogger().resetProblem();
    }

    /**
     * Sets the key of the account being rebalanced.
     *
     * @param accountKey The key of the account being rebalanced
     */
    public static void setAccountKey(AccountKey accountKey) {

        // Set the account key, and reset the message logger.
        RebalanceNode.accountKey = accountKey;
        reset();

        // Log an informational message about the incoming account key.
        getLogger().log(ordinary, String.format("Account key %s has been " +
                "set for rebalance...", getAccountKey()));
    }

    /**
     * Adds a child to the node.
     *
     * @param child The child to add to the node
     * @return Any child previously mapped to the weight type of the node
     */
    @SuppressWarnings("UnusedReturnValue")
    public RebalanceNode addChild(@NotNull RebalanceNode child) {

        /*
         * Add a new node delegate for the child, receiving any delegate
         * previously mapped to the weight type. Return null if the delegate
         * is null. Otherwise, return the receiver of the delegate.
         */
        final NodeDelegate delegate = children.put(child.getType(),
                new NodeDelegate(child));
        return (null == delegate) ? null : delegate.getReceiver();
    }

    /**
     * Adds a ticker to the node.
     *
     * @param ticker The ticker to add to the node
     * @return True if the group did not already contain the specified ticker
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addTicker(@NotNull Ticker ticker) {
        return tickerSet.add(new TickerDelegate(ticker));
    }

    /**
     * Calculates the initial reallocation score.
     *
     * @return The initial reallocation score
     */
    private @NotNull ReallocationScore calculateInitialScore() {

        // Create a weight list using all possible receiver delegates.
        final List<Double> weightList = createWeightList(getCollection(),
                Integer.MAX_VALUE);

        /*
         * Create an allocator with the weight list. Is the sum of the weights
         * greater than zero?
         */
        final Reallocator reallocator = new Reallocator(weightList);
        if (0. < reallocator.getWeightSum()) {

            /*
             * The sum of the weights is greater than zero. Get the current
             * value of this node. Create a value list using the size of the
             * weight list and the current value of this node.
             */
            final Currency current = getCurrent();
            final List<MutableCurrency> valueList = getInitialList(
                    weightList.size(), current);

            /*
             * Reallocate the value list using the reallocator. Initialize the
             * residual of the initial score action to the current value of
             * this node.
             */
            reallocator.reallocate(valueList);
            initialScoreAction.setResidual(current);

            /*
             * Set the reallocated value list in the initial score action, and
             * perform the action.
             */
            initialScoreAction.setList(valueList);
            doAction(getCollection(), initialScoreAction);
        }

        // Return the score obtained from the initial score action.
        return initialScoreAction.getScore();
    }

    /**
     * Clears the node.
     */
    public void clear() {

        // Clear both the children and the tickers.
        children.clear();
        tickerSet.clear();
    }

    @Override
    public void clearSnapshot(@NotNull SnapshotType type) {

        // Set the given snapshot type before performing the action.
        clearSnapshotAction.setType(type);
        doAction(getCollection(), clearSnapshotAction);
    }

    @Override
    public void clearSnapshots() {
        doAction(getCollection(), clearSnapshotsAction);
    }

    /**
     * Creates a weight list.
     *
     * @param delegates            A collection of receiver delegates from
     *                             which to gather weights
     * @param considerationPattern A bit pattern determining from which receiver
     *                             delegates weights are gathered: the least
     *                             significant bit refers to the first delegate
     *                             enumerated in the given collection
     * @param <T>                  An arbitrary receiver delegate type
     * @return A weight list gathered from the receiver delegates
     */
    private <T extends ReceiverDelegate<?>> @NotNull List<Double>
    createWeightList(@NotNull Collection<T> delegates,
                     int considerationPattern) {

        /*
         * Reset the consideration setter action, then set the consideration
         * pattern in the action.
         */
        considerationSetterAction.reset();
        considerationSetterAction.setConsiderationPattern(
                considerationPattern);

        /*
         * Perform the consideration setter action on each receiver
         * delegate. Reset the weight accumulator action.
         */
        doAction(delegates, considerationSetterAction);
        weightAccumulatorAction.reset();

        /*
         * Perform the weight accumulator action on each receiver delegate.
         * Get the resulting weight list from the weight accumulator action.
         */
        doAction(delegates, weightAccumulatorAction);
        return weightAccumulatorAction.getList();
    }

    /**
     * Notifies all receiver delegates that a rebalance cannot occur.
     */
    private void declareCannotRebalance() {
        doAction(getCollection(), cannotRebalanceAction);
    }

    /**
     * Gets a child by weight type.
     *
     * @param type The weight type
     * @return A child indexed by the weight type, or null if there is no child
     * mapped to the weight type
     */
    public RebalanceNode getChild(@NotNull WeightType type) {

        /*
         * Get any existing node delegate present for the given weight type.
         * Return null if the delegate is null. Otherwise, return the receiver
         * in the delegate.
         */
        final NodeDelegate delegate = children.get(type);
        return (null == delegate) ? null : delegate.getReceiver();
    }

    /**
     * Gets the relevant collection of receiver delegates.
     *
     * @return The relevant collection of receiver delegates
     */
    private @NotNull Collection<? extends ReceiverDelegate<?>>
    getCollection() {
        return childValues.isEmpty() ? tickerSet : childValues;
    }

    @Override
    public @NotNull Currency getCurrent() {

        /*
         * Reset the sum current action. Perform the action on the collection
         * of receiver delegates, then return the sum contained in the action.
         */
        sumCurrentAction.reset();
        doAction(getCollection(), sumCurrentAction);
        return sumCurrentAction.getSum();
    }

    /**
     * Gets the weight type assigned to the node.
     *
     * @return The weight type assigned to the node
     */
    public @NotNull WeightType getType() {
        return type;
    }

    /**
     * Gets the value assigned to the node.
     *
     * @return The value assigned to the node
     */
    public Currency getValue() {
        return value;
    }

    /**
     * Gets the weight assigned to the node.
     *
     * @return The weight assigned to the node
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Rebalances a collection of receiver delegate(s).
     *
     * @param delegates            A collection of receiver delegates
     * @param proposed             A value with which to adjust the proposed
     *                             value of the delegate(s)
     * @param considerationPattern A bit flag which describes which delegates
     *                             are participating - 1 means yes, 0 means no;
     *                             the lowest order bit represents the first
     *                             delegate in the collection
     * @param <T>                  A receiver delegate type
     * @return The score resulting from the reallocation/rebalance
     */
    private <T extends ReceiverDelegate<?>>
    @NotNull ReallocationScore rebalance(
            @NotNull Collection<T> delegates, @NotNull Currency proposed,
            int considerationPattern) {

        // Create the weight list. Declare an uninitialized reallocation score.
        final List<Double> weightList = createWeightList(delegates,
                considerationPattern);
        ReallocationScore score;

        /*
         * Create a new allocator with the weight list. Is the sum of the
         * weights greater than zero?
         */
        final Reallocator reallocator = new Reallocator(weightList);
        if (0. < reallocator.getWeightSum()) {

            /*
             * The sum of the weights is greater than zero. Create a value list
             * using the size of the weight list and the best residual. Note:
             * 'getInitialList(int, Currency)' is expected to use the absolute
             * value of the proposed value when creating the list. Reallocate
             * the value list using the reallocator.
             */
            final List<MutableCurrency> valueList =
                    getInitialList(weightList.size(), proposed);
            reallocator.reallocate(valueList);

            // Set the residual and the value list in the value setter action.
            valueSetterAction.setResidual(proposed);
            valueSetterAction.setList(valueList);

            /*
             * Perform the value setter action on each receiver delegate. Get
             * the reallocation score from the value setter action.
             * Reinitialize the 'relative' flag in the value setter action for
             * subsequent calls.
             */
            doAction(delegates, valueSetterAction);
            score = valueSetterAction.getScore();
            valueSetterAction.setRelative(true);

            /*
             * Log a message about this current combination of account key,
             * weight type, consideration pattern, residual, and proposed
             * value.
             */
            getLogger().log(ordinary, String.format("For account key %s, " +
                            "weight type %s, and consideration pattern " +
                            "0x%08x: Found accumulated residual of %s when " +
                            "trying to set proposed value %s.",
                    getAccountKey(), getWeight(), considerationPattern,
                    score.getResidual(), proposed));
        }

        /*
         * The sum of the weights is zero. Initialize the reallocation score
         * appropriately.
         */
        else {
            score = new ReallocationScore(proposed,
                    ReallocationScore.getIdealDeviation());
        }

        // Return the reallocation score.
        return score;
    }

    /**
     * Rebalances a collection of receiver delegates.
     *
     * @param delegates  A collection of receiver delegates
     * @param proposed   A value with which to adjust the proposed value of
     *                   this node
     * @param isRelative True if the incoming value is relative to the value
     *                   already set in the node; false if it is absolute
     * @param <T>        A receiver delegate type
     * @return The difference between proposed value and the value that this
     * node set (the "residual")
     */
    private <T extends ReceiverDelegate<?>> @NotNull Currency rebalance(
            @NotNull Collection<T> delegates, @NotNull Currency proposed,
            boolean isRelative) {

        /*
         * Get the message logger. Initialize the best score to the initial
         * score.
         */
        final MessageLogger logger = getLogger();
        ReallocationScore bestScore = calculateInitialScore();

        // Clear all snapshots. Try to rebalance this node.
        clearSnapshots();
        try {

            /*
             * Get a list of consideration patterns. Initialize the value of
             * the 'relative' flag in the value setter action.
             */
            final List<Integer> patterns = produce(delegates.size());
            valueSetterAction.setRelative(isRelative);

            /*
             * Get an iterator for the consideration patterns. Try an initial
             * rebalance using the first pattern, which uses all delegates.
             * A precondition for calling this method is that the delegate
             * collection passed to it has non-zero weight. Non-zero weight
             * implies at least one delegate, so it will be okay to call 'next'
             * on the pattern iterator at least once without checking for a
             * true 'hasNext'. Get the residual from the first score.
             */
            final Iterator<Integer> iterator = patterns.iterator();
            bestScore = rebalance(delegates, proposed, iterator.next());
            final Currency residual = bestScore.getResidual();

            /*
             * Initially, the best snapshot is the first snapshot. Initialize
             * both first and best snapshots to the current rebalance.
             */
            takeSnapshot(SnapshotType.FIRST);
            takeSnapshot(SnapshotType.BEST);

            /*
             * Initialize a constant for the 'ideal' score, and a variable to
             * receive the current score.
             */
            final ReallocationScore idealScore =
                    ReallocationScore.getIdealScore();
            ReallocationScore currentScore;

            /*
             * Cycle while there continue to be consideration patterns, and
             * while the ideal score is less than the best score. If *all*
             * delegates had no residual on first rebalance, then the best
             * score will be ideal without any iterations of the following
             * loop. Our rebalancing problem will have been equivalent to a
             * fractional knapsack problem. If we do enter the loop, however,
             * I postulate that the following loop will never exit due to the
             * best score becoming ideal. Why? Even if the residual component
             * of the best score reaches zero, there will always be a non-zero
             * deviation from an ideal rebalance. It cannot be helped.
             */
            while (iterator.hasNext() &&
                    (idealScore.compareTo(bestScore) < 0)) {

                /*
                 * Okay, we are inside the loop. This means two things: 1)
                 * there are two or more delegates, and; 2) and at least one of
                 * the delegates had a residual. Our rebalancing problem is now
                 * a 0/1 knapsack problem. Rebalance again, using the next
                 * consideration pattern and the residual from the first
                 * rebalance attempt. Receive the current score. Is the current
                 * score better than the best score?
                 *
                 * (Note: We are not currently performing the most efficient
                 * algorithm to find an exact solution to 0/1 knapsack problem,
                 * which involves dynamic programming. Fix it later.)
                 */
                currentScore = rebalance(delegates, residual, iterator.next());
                if (currentScore.compareTo(bestScore) < 0) {

                    /*
                     * The current reallocation score is the best seen so far.
                     * Reset the best snapshot, and set the best score to the
                     * current score.
                     */
                    takeSnapshot(SnapshotType.BEST);
                    bestScore = currentScore;
                }

                /*
                 * Recover the first snapshot in case we require any further
                 * rebalance attempts.
                 */
                recoverSnapshot(SnapshotType.FIRST);
            }

            // Done. Recover the best snapshot.
            recoverSnapshot(SnapshotType.BEST);
        }

        // Catch any exception that may occur.
        catch (@NotNull Exception exception) {

            // Log a warning saying a rebalance cannot be accomplished.
            logger.log(Level.WARNING, String.format("A rebalance cannot be " +
                            "accomplished for account key %s and weight " +
                            "type %s because of an exception containing the " +
                            "following message: '%s'.",
                    getAccountKey(), getType(), exception.getMessage()));

            // Declare that we cannot perform a rebalance.
            declareCannotRebalance();
        }

        // Do this unconditionally.
        finally {

            /*
             * Log a message identifying the best reallocation score
             * characteristics.
             */
            logger.log(extraordinary, String.format("For account key %s and " +
                            "weight type %s: I have identified the best " +
                            "residual of %s (deviation of %s) when trying " +
                            "to set %s proposed value %s.",
                    getAccountKey(), getType(), bestScore.getResidual(),
                    deviationFormat.format(bestScore.getDeviation()),
                    isRelative ? "relative" : "absolute", proposed));
        }

        /*
         * Get the residual from the best reallocation score. Calculate the new
         * value of this node by adding the residual to proposed value.
         */
        final Currency residual = bestScore.getResidual();
        final MutableCurrency myNewValue = new MutableCurrency(proposed);
        myNewValue.add(residual);

        // Set the current value of this node and return the residual.
        setValue(myNewValue.getImmutable());
        return residual;
    }

    @Override
    public void recoverSnapshot(@NotNull SnapshotType type) {

        // Set the given snapshot type before performing the action.
        recoverSnapshotAction.setType(type);
        doAction(getCollection(), recoverSnapshotAction);
    }

    @Override
    public @NotNull Currency setProposed(@NotNull Currency currency,
                                         boolean isRelative) {

        /*
         * Declare and initialize the residual. Does any child have positive
         * weight?
         */
        Currency residual = currency;
        if (hasAnyWeight(childValues)) {

            /*
             * One or more children have positive weight. Rebalance using
             * the child values, and reset the residual.
             */
            residual = rebalance(childValues, currency, isRelative);
        }

        /*
         * No child has positive weight. Rebalance using the tickers if any has
         * weight.
         */
        else if (hasAnyWeight(tickerSet)) {
            residual = rebalance(tickerSet, currency, isRelative);
        }

        // Return the residual.
        return residual;
    }

    /**
     * Sets the value assigned to the node.
     *
     * @param value The new value assigned to the node
     */
    private void setValue(@NotNull Currency value) {
        this.value = value;
    }

    @Override
    public void takeSnapshot(@NotNull SnapshotType type) {

        // Set the given snapshot type before performing the action.
        takeSnapshotAction.setType(type);
        doAction(getCollection(), takeSnapshotAction);
    }

    private interface Action<T> {

        /**
         * Performs the action.
         *
         * @param object The action argument
         */
        void doAction(@NotNull T object);
    }

    private static abstract class
    ActionWithContainer<ContainedType, ParameterType> implements
            Action<ParameterType> {

        // Our member variable
        private ContainedType contained;

        /**
         * Gets the member variable.
         *
         * @return The member variable
         */
        protected @NotNull ContainedType getContained() {

            // Set the member variable if it is null.
            if (null == contained) {
                contained = getInitialValue();
            }

            // Return the member variable.
            return contained;
        }

        /**
         * Gets the initial value of the member variable.
         *
         * @return The initial value of the member variable
         */
        protected abstract @NotNull ContainedType getInitialValue();

        /**
         * Resets the container.
         */
        public abstract void reset();

        /**
         * Sets the member variable.
         *
         * @param contained The member variable
         */
        protected void setContained(@NotNull ContainedType contained) {
            this.contained = contained;
        }
    }

    private static class ConsiderationSetterAction extends
            ActionWithContainer<Integer, ReceiverDelegate<?>> {

        // Count of calls to 'doAction(ReceiverDelegate)'
        private int iteration;

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {
            delegate.setConsidered(
                    (getConsiderationPattern() & (1 << (iteration++))) != 0);
        }

        /**
         * Gets the consideration pattern.
         *
         * @return The consideration pattern
         */
        protected int getConsiderationPattern() {
            return getContained();
        }

        @Override
        protected @NotNull Integer getInitialValue() {
            return 0;
        }

        @Override
        public void reset() {
            iteration = getInitialValue();
        }

        /**
         * Sets the consideration pattern.
         *
         * @param considerationPattern The consideration pattern
         */
        public void setConsiderationPattern(int considerationPattern) {

            /*
             * Set the consideration pattern as the contained object, and
             * reset the action.
             */
            setContained(considerationPattern);
            reset();
        }
    }

    private static class InitialScoreAction extends
            ResidualProducerAction {

        // The residual of the action
        private Currency residual;

        /**
         * Constructs the initial score action.
         */
        public InitialScoreAction() {
            setRelative(false);
        }

        @Override
        protected @NotNull Currency getResidual(
                ReceiverDelegate<?> delegate) {
            return residual;
        }

        @Override
        protected boolean isConsidered(@NotNull ReceiverDelegate<?> delegate) {
            return true;
        }

        @Override
        protected @NotNull Currency produceResidual(
                @NotNull ReceiverDelegate<?> delegate,
                @NotNull Currency currency, boolean isRelative) {

            /*
             * Declare and initialize mutable currency to hold the incoming
             * value. Is the incoming value not relative?
             */
            final MutableCurrency incoming = new MutableCurrency(currency);
            if (!isRelative) {

                /*
                 * The incoming value is not relative (a.k.a., it is absolute).
                 * Subtract the current value of the delegate from the incoming
                 * value.
                 */
                incoming.subtract(delegate.getCurrent());
            }

            // Set last residual, and return it.
            residual = incoming.getImmutable();
            return residual;
        }
    }

    private static abstract class ResidualProducerAction extends
            ActionWithContainer<SetValueUtility, ReceiverDelegate<?>> {

        // The value of minus one as currency
        private static final Currency minusOne = new Currency(-1.);

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

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Increment the action call count. Is the delegate considered?
            incrementCount();
            if (isConsidered(delegate)) {

                /*
                 * The delegate is considered. Get the incoming value from the
                 * utility. Is the incoming value not null?
                 */
                final MutableCurrency incoming = utility.getNextElement();
                if (null != incoming) {

                    /*
                     * The incoming value is not null. Get the 'relative' flag
                     * from the utility. Does the utility indicate that the
                     * incoming value is a negative number?
                     */
                    final boolean isRelative = utility.isRelative();
                    if (utility.isNegative()) {

                        /*
                         * The utility indicates that the incoming value is a
                         * negative number. Multiply the incoming value by
                         * minus one.
                         */
                        incoming.multiply(getMinusOne());
                    }

                    /*
                     * Ask the delegate to produce residual using the incoming
                     * value. Is the residual not zero?
                     */
                    final Currency residual =
                            produceResidual(delegate, incoming.getImmutable(),
                                    isRelative);
                    if (residual.isNotZero()) {

                        /*
                         * The residual is not zero. Add the residual to the
                         * proposed value if the 'relative' flag is set.
                         */
                        if (isRelative) {
                            incoming.add(residual);
                        }

                        /*
                         * ...otherwise subtract the residual if the 'relative'
                         * flag is not set.
                         */
                        else {
                            incoming.subtract(residual);
                        }
                    }

                    /*
                     * Subtract the modified incoming value from the residual
                     * in the utility.
                     */
                    utility.subtractResidual(incoming.getImmutable());
                }
            }

            /*
             * Add the residual from the delegate to the deviation in the
             * utility even if the delegate is not currently considered.
             */
            utility.addDeviation(getResidual(delegate).getValue());
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
         * Gets the residual from a receiver delegate.
         *
         * @param delegate A receiver delegate
         * @return The residual from the receiver delegate
         */
        protected abstract @NotNull Currency getResidual(
                ReceiverDelegate<?> delegate);

        /**
         * Gets the residual.
         *
         * @return The residual
         */
        public @NotNull Currency getResidual() {
            return utility.getResidual();
        }

        /**
         * Gets the reallocation score.
         *
         * @return The reallocation score
         */
        public @NotNull ReallocationScore getScore() {
            return new ReallocationScore(
                    new Currency(getResidual().getValue()),
                    calculateDeviationScore(utility.getDeviation() /
                            getCount()));
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
         * @return True if the delegate is to be considered for an action;
         * false otherwise
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
         * Sets the currency list.
         *
         * @param list The currency list
         */
        public void setList(@NotNull List<MutableCurrency> list) {
            utility.setList(list);
        }

        /**
         * Sets the 'relative' flag.
         *
         * @param relative True if the values in the currency list are to be
         *                 interpreted as relative values; false if they are
         *                 to be interpreted as absolute values
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
             * Set the negative flag in the utility if the residual is less
             * than zero. Set the residual in the container, and reset the
             * action.
             */
            utility.setNegative(residual.compareTo(zero) < 0);
            utility.setResidual(residual);
            reset();
        }
    }

    private static class SetValueUtility {

        // The value of zero currency
        private static final Currency zero = Currency.getZero();

        // The residual component
        private final MutableCurrency residual = new MutableCurrency();

        // The deviation component
        private double deviation;

        // An index into the currency list
        private int index;

        // A currency list
        private List<MutableCurrency> list;

        /*
         * True if the values in the currency list are to be interpreted as
         * negative values; false if they are to be interpreted as
         * non-negative values
         */
        private boolean negative;
        /*
         * True if the value in the currency list are to be interpreted as
         * relative values; false if they are to be interpreted as absolute
         * values
         */
        private boolean relative;

        {
            resetResidual();
        }

        /**
         * Adds deviation.
         *
         * @param deviation The deviation to add
         */
        public void addDeviation(double deviation) {
            this.deviation += calculateDeviation(deviation);
        }

        /**
         * Gets the deviation.
         *
         * @return The deviation
         */
        public double getDeviation() {
            return deviation;
        }

        /**
         * Gets the next element.
         *
         * @return The next element, or null if there are no more elements
         */
        public @Nullable MutableCurrency getNextElement() {
            return ((null != list) && (index < list.size())) ?
                    list.get(index++) : null;
        }

        /**
         * Gets the residual.
         *
         * @return The residual
         */
        public @NotNull Currency getResidual() {
            return residual.getImmutable();
        }

        /**
         * Gets the 'negative' flag.
         *
         * @return True if the value in the currency list are to be interpreted
         * as negative values; false if they are to be interpreted as non-negative
         * values
         */
        public boolean isNegative() {
            return negative;
        }

        /**
         * Gets the 'relative' flag.
         *
         * @return True if the values in the currency list are to be
         * interpreted as relative values; false if they are to be interpreted
         * as absolute values
         */
        public boolean isRelative() {
            return relative;
        }

        /**
         * Resets the element index.
         */
        public void resetIndex() {
            index = 0;
        }

        /**
         * Resets the residual.
         */
        public void resetResidual() {
            setResidual(zero);
        }

        /**
         * Sets the currency list.
         *
         * @param list A currency list
         */
        public void setList(@NotNull List<MutableCurrency> list) {

            // Set the list and reset the element index.
            this.list = list;
            resetIndex();
        }

        /**
         * Sets the 'negative' flag.
         *
         * @param negative True if the values in the currency list are to be
         *                 interpreted as negative values; false if they are
         *                 to be interpreted as non-negative values
         */
        public void setNegative(boolean negative) {
            this.negative = negative;
        }

        /**
         * Sets the 'relative' flag.
         *
         * @param relative True if the values in the currency list are to be
         *                 interpreted as relative values; false if they are
         *                 to be interpreted as absolute values
         */
        public void setRelative(boolean relative) {
            this.relative = relative;
        }

        /**
         * Sets the residual.
         *
         * @param residual The residual
         */
        public void setResidual(@NotNull Currency residual) {

            // Reinitialize the deviation to the ideal, and set the residual.
            this.deviation = ReallocationScore.getIdealDeviation();
            this.residual.set(residual);
        }

        /**
         * Subtracts currency from the residual.
         *
         * @param currency The currency to subtract
         */
        public void subtractResidual(@NotNull Currency currency) {
            residual.subtract(currency);
        }
    }

    private abstract static class SnapshotAction extends
            ActionWithContainer<SnapshotType, ReceiverDelegate<?>> {

        // The initial snapshot type
        private static final SnapshotType initialType = SnapshotType.FIRST;

        @Override
        protected @NotNull SnapshotType getInitialValue() {
            return SnapshotType.FIRST;
        }

        @Override
        public void reset() {
            setContained(initialType);
        }

        /**
         * Sets the snapshot type for the action.
         *
         * @param type The snapshot type for the action
         */
        public void setType(@NotNull SnapshotType type) {
            setContained(type);
        }
    }

    private static class SumCurrentAction extends
            ActionWithContainer<MutableCurrency, ReceiverDelegate<?>> {

        // The initial value for the container in the action
        private static final double initialValue = 0.;

        // The sum of the current values of the delegates
        private final MutableCurrency sum = getContained();

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {
            sum.add(delegate.getCurrent());
        }

        @Override
        protected @NotNull MutableCurrency getInitialValue() {
            return new MutableCurrency(initialValue);
        }

        public @NotNull Currency getSum() {
            return sum.getImmutable();
        }

        @Override
        public void reset() {
            sum.set(initialValue);
        }
    }

    private static class ValueSetterAction extends ResidualProducerAction {

        @Override
        protected @NotNull Currency getResidual(
                @NotNull ReceiverDelegate<?> delegate) {
            return delegate.getLastResidual();
        }

        @Override
        protected boolean isConsidered(@NotNull ReceiverDelegate<?> delegate) {
            return delegate.isConsidered();
        }

        @Override
        protected @NotNull Currency produceResidual(
                @NotNull ReceiverDelegate<?> delegate,
                @NotNull Currency currency, boolean isRelative) {
            return delegate.setProposed(currency, isRelative);
        }

        @Override
        public void setRelative(boolean relative) {
            super.setRelative(relative);
        }
    }

    private static class WeightAccumulatorAction extends
            ActionWithContainer<List<Double>, ReceiverDelegate<?>> {

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Add the weight if the delegate is to be considered.
            if (delegate.isConsidered()) {
                getList().add(delegate.getWeight());
            }
        }

        @Override
        protected @NotNull List<Double> getInitialValue() {
            return new ArrayList<>();
        }

        /**
         * Gets the weight list.
         *
         * @return The weight list
         */
        public @NotNull List<Double> getList() {
            return getContained();
        }

        @Override
        public void reset() {
            getList().clear();
        }
    }
}
