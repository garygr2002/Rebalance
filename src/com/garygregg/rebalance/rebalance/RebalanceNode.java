package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class RebalanceNode implements CurrencyReceiver {

    // An action to notify delegates that a rebalance cannot occur.
    private static final NodeAction<ReceiverDelegate<?>>
            cannotRebalanceAction = ReceiverDelegate::onCannotSet;

    // The format for reporting deviation values
    private static final DecimalFormat deviationFormat =
            new DecimalFormat("0.00");

    // The logging level for extraordinary informational messages
    private static final Level extraordinary = MessageLogger.getOrdinary();

    // An action to calculate initial residual and deviation
    private static final InitialScoreAction initialScoreAction =
            new InitialScoreAction();

    // Our snapshot key factory
    private static final SnapshotKeyFactory keyFactory =
            new SnapshotKeyFactory(new Random());

    // An action to notify delegates to clear snapshots
    private static final SnapshotAction clearSnapshotAction =

            new SnapshotAction(keyFactory.produce()) {
                @Override
                public void doAction(@NotNull ReceiverDelegate<?> delegate) {
                    delegate.clearSnapshot(getContained());
                }
            };

    // The preference manager
    private static final PreferenceManager manager =
            PreferenceManager.getInstance();

    // The limit of allowed receiver delegates
    private static final int limit = calculateLimit();

    // Our local message logger
    private static final MessageLogger messageLogger = new MessageLogger();

    // The logging level for ordinary informational messages
    private static final Level ordinary = MessageLogger.getOrdinary();

    // An action to notify delegates to recover their snapshots
    private static final SnapshotAction recoverSnapshotAction =
            new SnapshotAction(keyFactory.produce()) {

                @Override
                public void doAction(@NotNull ReceiverDelegate<?> delegate) {
                    delegate.recoverSnapshot(getContained());
                }
            };

    // An action to notify delegates to take snapshots
    private static final SnapshotAction takeSnapshotAction =
            new SnapshotAction(keyFactory.produce()) {
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

    // A map of snapshot keys to the value of the snapshot
    private final Map<SnapshotKey, Currency> snapshotMap = new HashMap<>();

    // The sum current action
    private final SumAction sumCurrentAction = new SumCurrentAction();

    // The sum proposed action
    private final SumAction sumProposedAction = new SumProposedAction();

    // The tickers in the node
    private final Collection<TickerDelegate> tickers =
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
     * Performs an action on each object in an iterable.
     *
     * @param iterable The iterable
     * @param action   The action to perform on each object in the iterable
     * @param <T>      An arbitrary type
     */
    private static <T> void doAction(@NotNull Iterable<? extends T> iterable,
                                     @NotNull NodeAction<T> action) {

        // Cycle for each object in the iterable, and perform the action.
        for (T object : iterable) {
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
     * Determines if any receiver delegate in an iterable object has positive
     * weight.
     *
     * @param delegates An iterable of delegates
     * @return True if any receiver delegate in the iterable has positive
     * weight; false otherwise
     */
    private static <T extends ReceiverDelegate<?>> boolean hasAnyWeight(
            @NotNull Iterable<T> delegates) {

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
     * Determines if no receiver delegate in an iterable object has a snapshot.
     *
     * @return True if no receiver delegate has a snapshot; false if any have
     * one or more snapshots.
     */
    private static <T extends ReceiverDelegate<?>>
    boolean hasNoSnapshots(@NotNull Iterable<T> delegates) {

        /*
         * Initialize the result. Get a receiver delegate iterator. Cycle while
         * receiver delegates exist, and while no receiver has a snapshot.
         */
        boolean result = true;
        final Iterator<T> iterator = delegates.iterator();
        while (iterator.hasNext() && result) {

            // Reinitialize the result.
            result = iterator.next().hasNoSnapshots();
        }

        // Return the result.
        return result;
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
        return tickers.add(new TickerDelegate(ticker));
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
             * Reallocate the value list using the reallocator. Clear the maps
             * in the initial score action. Initialize the residual of the
             * initial score action to the current value of this node.
             */
            reallocator.reallocate(valueList);
            initialScoreAction.clearMaps();
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
        tickers.clear();
    }

    @Override
    public void clearSnapshot(@NotNull SnapshotKey key) {

        /*
         * Set the given snapshot key before performing the action. Remove the
         * key from the snapshot map.
         */
        clearSnapshotAction.setKey(key);
        doAction(getCollection(), clearSnapshotAction);
        snapshotMap.remove(key);
    }

    /**
     * Creates a weight list.
     *
     * @param delegates            An iterable of receiver delegates from which
     *                             to gather weights
     * @param considerationPattern A bit pattern determining from which
     *                             receiver delegates weights are gathered: the
     *                             least significant bit refers to the first
     *                             delegate enumerated in the given collection
     * @param <T>                  An arbitrary receiver delegate type
     * @return A weight list gathered from the receiver delegates
     */
    private <T extends ReceiverDelegate<?>> @NotNull List<Double>
    createWeightList(@NotNull Iterable<T> delegates,
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
        return childValues.isEmpty() ? tickers : childValues;
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
     * Gets the proposed value of the node, with an optional extra added in.
     *
     * @return The proposed value of the node, with the extra added in if it
     * is not null
     */
    private @NotNull Currency getProposed(Currency extra) {

        // Get the value of the extra (if given), as mutable currency.
        final MutableCurrency currency = new MutableCurrency((null == extra) ?
                zero : extra);

        /*
         * Reset the sum proposed action. Perform the action on the collection
         * of receiver delegates.
         */
        sumProposedAction.reset();
        doAction(getCollection(), sumProposedAction);

        /*
         * Add the sum contained in the action to the existing value. Return
         * the result as immutable currency.
         */
        currency.add(sumProposedAction.getSum());
        return currency.getImmutable();
    }

    /**
     * Gets a value given a snapshot key.
     *
     * @param key A snapshot key
     * @return A value associated with the snapshot key, or zero if there is no
     * current association for the key
     */
    private @NotNull Currency getSnapshot(@NotNull SnapshotKey key) {

        /*
         * Get any value associated with the key in the snapshot map. Return
         * zero if there is no current association. Otherwise, return the
         * associated value.
         */
        final Currency currency = snapshotMap.get(key);
        return (null == currency) ? zero : currency;
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

    @Override
    public boolean hasNoSnapshots() {

        /*
         * Declare and initialize the result according to whether the snapshot
         * map is empty. Is the snapshot map empty?
         */
        boolean result = snapshotMap.isEmpty();
        if (result) {

            /*
             * The snapshot map is empty. Check the child values for snapshots
             * if one or more children have positive weight.
             */
            if (hasAnyWeight(childValues)) {
                result = hasNoSnapshots(childValues);
            }

            /*
             * No child has positive weight. Check the tickers for snapshots if
             * any has weight.
             */
            else if (hasAnyWeight(tickers)) {
                result = hasNoSnapshots(tickers);
            }
        }

        // Return the result.
        return result;
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
         * Create a new allocator with the weight list. Can a reallocation be
         * performed?
         */
        final Reallocator reallocator = new Reallocator(weightList);
        if (reallocator.canReallocate()) {

            /*
             * A reallocation can be performed. Create a value list using the
             * size of the weight list and the best residual. Note:
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
         * A reallocation cannot be performed. Initialize the reallocation
         * score appropriately.
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
         * Get the message logger. Determine the proposed holdings prior to
         * any rebalance.
         */
        final MessageLogger logger = getLogger();
        final Currency beforeRebalance = getProposed(proposed);

        // Create snapshot keys for the best snapshot and the first snapshot.
        final SnapshotKey bestKey = keyFactory.produce();
        final SnapshotKey firstKey = keyFactory.produce();

        /*
         * Initialize the best score to the initial score. Try to rebalance
         * this node.
         */
        ReallocationScore bestScore = calculateInitialScore();
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
             * both best and first snapshots to the current rebalance.
             */
            takeSnapshot(bestKey);
            takeSnapshot(firstKey);

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
                    (0 > idealScore.compareTo(bestScore))) {

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
                if (0 > currentScore.compareTo(bestScore)) {

                    /*
                     * The current reallocation score is the best seen so far.
                     * Reset the best snapshot, and set the best score to the
                     * current score.
                     */
                    takeSnapshot(bestKey);
                    bestScore = currentScore;
                }

                /*
                 * Recover the first snapshot in case we require any further
                 * rebalance attempts.
                 */
                recoverSnapshot(firstKey);
            }

            // Done. Recover the best snapshot.
            recoverSnapshot(bestKey);
        }

        // Catch illegal argument exceptions.
        catch (@NotNull IllegalArgumentException exception) {

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

            // Clear snapshots, if any.
            clearSnapshot(firstKey);
            clearSnapshot(bestKey);

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
         * Get the residual from the best reallocation score. Get the proposed
         * value of this node with the residual added in. As a post-condition,
         * the resulting sum should be equal to that taken prior to any
         * rebalance. Does the post-condition not hold?
         */
        final Currency residual = bestScore.getResidual();
        final Currency afterRebalance = getProposed(residual);
        if (!afterRebalance.isEqual(beforeRebalance)) {

            /*
             * The resulting sum after rebalance is not equal to the sum before
             * rebalance. Log a message.
             */
            logger.streamAndLog(Level.WARNING, String.format("For account " +
                            "key %s and weight type %s: The after-rebalance " +
                            "sum of %s does not equal the before-rebalance " +
                            "sum of %s.", getAccountKey(), getType(),
                    afterRebalance, beforeRebalance));
        }

        // Set the current value of this node and return the residual.
        setValue(sumProposedAction.getSum());
        return residual;
    }

    @Override
    public void recoverSnapshot(@NotNull SnapshotKey key) {

        /*
         * Set the given snapshot key before performing the action. Restore
         * the value from the snapshot map.
         */
        recoverSnapshotAction.setKey(key);
        doAction(getCollection(), recoverSnapshotAction);
        setValue(getSnapshot(key));
    }

    @Override
    public @NotNull Currency setProposed(@NotNull Currency currency,
                                         boolean isRelative) {

        /*
         * Declare and initialize the residual. Clear the maps in the value
         * setter action. Does any child have positive weight?
         */
        Currency residual = currency;
        valueSetterAction.clearMaps();
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
        else if (hasAnyWeight(tickers)) {
            residual = rebalance(tickers, currency, isRelative);
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
    public void takeSnapshot(@NotNull SnapshotKey key) {

        /*
         * Set the given snapshot key before performing the action. Put the
         * current value in the snapshot map.
         */
        takeSnapshotAction.setKey(key);
        doAction(getCollection(), takeSnapshotAction);
        snapshotMap.put(key, getValue());
    }
}
