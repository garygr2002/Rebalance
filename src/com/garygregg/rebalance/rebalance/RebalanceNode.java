package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class RebalanceNode implements CurrencyReceiver {

    // An action to notify delegates that a rebalance cannot occur.
    private static final Action<ReceiverDelegate<?>> cannotSetAction =
            ReceiverDelegate::onCannotSet;

    // An action to notify delegates to clear their snapshots
    private static final Action<ReceiverDelegate<?>> clearSnapshotAction =
            ReceiverDelegate::clearSnapshot;

    // The logging level for extraordinary informational messages
    private final static Level extraordinary = MessageLogger.getOrdinary();

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
    private static final Action<ReceiverDelegate<?>> recoverSnapshotAction =
            ReceiverDelegate::recoverSnapshot;

    // An action to notify delegates to take snapshots
    private static final Action<ReceiverDelegate<?>> takeSnapshotAction =
            ReceiverDelegate::takeSnapshot;

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
     * Clears the node.
     */
    public void clear() {

        // Clear both the children and the tickers.
        children.clear();
        tickerSet.clear();
    }

    @Override
    public void clearSnapshot() {
        doAction(getCollection(), clearSnapshotAction);
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
        final List<Double> weightList = weightAccumulatorAction.getList();

        /*
         * Declare an uninitialized reallocation score. Create a new allocator
         * with the weight list. Is the sum of the weights greater than zero?
         */
        ReallocationScore score;
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
         * The sum of the weights is not zero. Initialize the reallocation
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

        // Get the message logger. Initialize the best score.
        final MessageLogger logger = getLogger();
        ReallocationScore bestScore = new ReallocationScore(proposed,
                ReallocationScore.getIdealDeviation());

        // Try to rebalance this node.
        try {

            /*
             * Get a list of consideration patterns. Initialize a constant with
             * the ideal reallocation score.
             */
            final List<Integer> patterns = produce(delegates.size());
            final ReallocationScore idealScore =
                    ReallocationScore.getIdealScore();

            /*
             * Declare a variable to receive the current reallocation score.
             * Initialize the value of the 'relative' flag in the value setter
             * action. Take an initial snapshot.
             */
            ReallocationScore currentScore;
            valueSetterAction.setRelative(isRelative);
            takeSnapshot();

            /*
             * Get an iterator for the consideration patterns. Cycle while
             * consideration patterns exist, and while the best score is not as
             * good as the ideal score.
             */
            final Iterator<Integer> iterator = patterns.iterator();
            while (iterator.hasNext() &&
                    (idealScore.compareTo(bestScore) < 0)) {

                /*
                 * Rebalance using the first/next consideration pattern,
                 * receiving a reallocation score. Is the current reallocation
                 * score the best seen so far?
                 */
                currentScore = rebalance(delegates, bestScore.getResidual(),
                        iterator.next());
                if (currentScore.compareTo(bestScore) < 0) {

                    /*
                     * The current reallocation score is the best seen so far.
                     * Take a snapshot, and set the best score to the current
                     * score.
                     */
                    takeSnapshot();
                    bestScore = currentScore;
                }

                /*
                 * The current reallocation score is not the best seen so far.
                 * Recover the best snapshot.
                 */
                else {
                    recoverSnapshot();
                }
            }
        }

        // Catch any exception that may occur.
        catch (@NotNull Exception exception) {

            // Log a warning saying a rebalance cannot be accomplished.
            logger.log(Level.WARNING, String.format("A rebalance cannot be " +
                            "accomplished for account key %s and weight " +
                            "type %s because of an exception containing the " +
                            "following message: '%s'.",
                    getAccountKey(), getType(), exception.getMessage()));

            /*
             * Let each receiver delegate know that explicit values cannot be
             * set.
             */
            doAction(delegates, cannotSetAction);
        }

        // Do this unconditionally.
        finally {

            /*
             * Log a message identifying the best reallocation score
             * characteristics.
             */
            logger.streamAndLog(extraordinary, String.format("For account " +
                            "key %s and weight type %s: I have identified " +
                            "the best residual of %s (average deviation %f) " +
                            "when trying to set %s proposed value %s.",
                    getAccountKey(), getType(), bestScore.getResidual(),
                    bestScore.getDeviation(), isRelative ? "relative" :
                            "absolute", proposed));
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
    public void recoverSnapshot() {
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
    public void takeSnapshot() {
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
            this.deviation += Math.pow(deviation, 2.);
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
         * @param currency Currency to subtract
         */
        public void subtractResidual(@NotNull Currency currency) {
            residual.subtract(currency);
        }
    }

    private static class ValueSetterAction extends
            ActionWithContainer<SetValueUtility, ReceiverDelegate<?>> {

        // The value of minus one as currency
        private static final Currency minusOne = new Currency(-1.);

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Is the delegate considered?
            if (delegate.isConsidered()) {

                /*
                 * The delegate is considered. Get the utility from the
                 * container, and the incoming value from the utility. Is the
                 * incoming value not null?
                 */
                final SetValueUtility utility = getContained();
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
                        incoming.multiply(minusOne);
                    }

                    /*
                     * Set the proposed value in the delegate, receiving any
                     * residual. Is the residual not zero?
                     */
                    final Currency residual = delegate.setProposed(
                            incoming.getImmutable(), isRelative);
                    if (residual.isNotZero()) {

                        /*
                         * The residual is not zero. Add deviation to the
                         * utility using the ratio of the residual and the
                         * value that was actually set in the delegate. Note:
                         * this will produce a not-a-number (NaN) if the
                         * proposed value had been set to zero. However, in
                         * that event we would also expect that the residual
                         * would then also be zero. We would then not be inside
                         * this block. A NaN appearing in the deviation,
                         * therefore, indicates a design flaw.
                         */
                        utility.addDeviation(residual.getValue() /
                                delegate.getProposed().getValue());

                        /*
                         * Add the residual to the proposed value if the
                         * 'relative' flag is set.
                         */
                        if (isRelative) {
                            incoming.add(residual);
                        }

                        /*
                         * ...otherwise subtract the residual if the flag is
                         * not set.
                         */
                        else {
                            incoming.subtract(residual);
                        }
                    }

                    // Subtract the modified incoming value from the residual.
                    utility.subtractResidual(incoming.getImmutable());
                }
            }
        }

        @Override
        protected @NotNull SetValueUtility getInitialValue() {
            return new SetValueUtility();
        }

        /**
         * Gets the residual.
         *
         * @return The residual
         */
        public @NotNull Currency getResidual() {
            return getContained().getResidual();
        }

        /**
         * Gets the reallocation score.
         *
         * @return The reallocation score
         */
        public @NotNull ReallocationScore getScore() {
            return new ReallocationScore(
                    new Currency(Math.abs(getResidual().getValue())),
                    getContained().getDeviation());
        }

        @Override
        public void reset() {
            getContained().resetIndex();
        }

        /**
         * Sets the currency list.
         *
         * @param list The currency list
         */
        public void setList(@NotNull List<MutableCurrency> list) {
            getContained().setList(list);
        }

        /**
         * Sets the 'relative' flag.
         *
         * @param relative True if the values in the currency list are to be
         *                 interpreted as relative values; false if they are
         *                 to be interpreted as absolute values
         */
        public void setRelative(boolean relative) {
            getContained().setRelative(relative);
        }

        /**
         * Sets the residual.
         *
         * @param residual The residual
         */
        public void setResidual(@NotNull Currency residual) {

            /*
             * Get the set value utility from the container. Set the negative
             * flag in the utility if the residual is less than zero.
             */
            final SetValueUtility utility = getContained();
            utility.setNegative(residual.compareTo(zero) < 0);

            // Set the residual in the container. Reset the action.
            utility.setResidual(residual);
            reset();
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
