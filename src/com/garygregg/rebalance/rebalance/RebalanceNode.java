package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class RebalanceNode implements CurrencyReceiver {

    // For use when a rebalance cannot occur
    private static final Action<ReceiverDelegate<?>> cannotSetAction =
            ReceiverDelegate::onCannotSet;

    // The error stream we will use
    private static final PrintStream errorStream = System.err;

    // Our local message logger
    private static final MessageLogger messageLogger = new MessageLogger();

    // The output stream we will use
    private static final PrintStream outputStream = System.out;

    // For use when a snapshot recovery is needed
    private static final Action<ReceiverDelegate<?>> recoverAction =
            ReceiverDelegate::recover;

    // For use when a snapshot is needed
    private static final Action<ReceiverDelegate<?>> takeSnapshotAction =
            ReceiverDelegate::takeSnapshot;

    // The key of the account that is being rebalanced
    private static AccountKey accountKey;

    // The children of the node
    private final SortedMap<WeightType, NodeDelegate> children =
            new TreeMap<>();

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

    // The value of zero currency
    private final Currency zero = Currency.getZero();

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
     * Performs a one-parameter action on each object in a collection.
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
     * The error stream we will use.
     *
     * @return The error stream we will use
     */
    private static @NotNull PrintStream getErrorStream() {
        return errorStream;
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
     * Gets an initial currency list.
     *
     * @param size              The size of the desired list
     * @param valueToDistribute The value to distribute
     * @return An initial currency list
     */
    private static @NotNull List<MutableCurrency>
    getInitialList(int size, @NotNull Currency valueToDistribute) {

        // Create the list. Is the size greater than the first index?
        final List<MutableCurrency> list = new ArrayList<>();
        final int firstIndex = 0;
        if (firstIndex < size) {

            // The size is greater than the first index. Cycle for each index.
            final MutableCurrency zero = new MutableCurrency();
            for (int i = firstIndex; i < size; ++i) {

                // Add zero currency.
                list.add(zero);
            }

            /*
             * Reset the first element to absolute value of the value to
             * distribute.
             */
            list.set(firstIndex, new MutableCurrency(
                    Math.abs(valueToDistribute.getValue())));
        }

        // Return the list.
        return list;
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
     * Gets the logging level for ordinary, non-warning activity.
     *
     * @return The logging level for ordinary, non-warning activity
     */
    private static @NotNull Level getOrdinary() {
        return Level.FINE;
    }

    /**
     * Gets the output stream we will use.
     *
     * @return The output stream we will use
     */
    private static @NotNull PrintStream getOutputStream() {
        return outputStream;
    }

    /**
     * Returns whether there was a problem with a rebalance.
     *
     * @return True if there was a problem with a rebalance, false otherwise
     */
    public static boolean hadProblem() {

        /*
         * Get the message logger and return whether problem one or problem
         * two is set.
         */
        final MessageLogger logger = getLogger();
        return logger.hadProblem1() || logger.hadProblem2();
    }

    /**
     * Logs a message.
     *
     * @param level   The level of the message
     * @param message The message to log
     */
    @SuppressWarnings("SameParameterValue")
    private static void logMessage(@NotNull Level level,
                                   @NotNull String message) {

        // Identify the proper print stream for the message.
        final PrintStream printStream = (level.intValue() <
                Level.SEVERE.intValue()) ? getOutputStream() :
                getErrorStream();

        // Print the message to the print stream, then log the message.
        printStream.println(message);
        messageLogger.logMessage(level, message);
    }

    /**
     * Produces a set of integers sorted by descending count of set bits.
     *
     * @param n Integers in the resulting set will be less than 2 ^ (n - 1)
     * @return A set of integers sorted by descending count of set bits
     */
    @Contract(pure = true)
    private static @NotNull SortedSet<Integer> produce(int n) {

        // The argument cannot be negative.
        if (0 > n) {
            throw new IllegalArgumentException(
                    String.format("Negative argument %d not allowed", n));
        }

        // The argument cannot be larger than the size of an integer.
        else if (Integer.SIZE <= n) {
            throw new IllegalArgumentException(
                    String.format("Argument %d is too big", n));
        }

        // Create the sorted set.
        final SortedSet<Integer> set = new TreeSet<>((first, second) -> {

            // Calculate the bit count difference.
            final int bitCountDifference = Integer.bitCount(second) -
                    Integer.bitCount(first);

            /*
             * Return the difference in the integers of their bit counts are
             * the same. Otherwise, return the difference in their bit counts.
             */
            return (0 == bitCountDifference) ? second - first :
                    bitCountDifference;
        });

        // Calculate the count of integers, and cycle for each.
        final long count = 1L << n;
        for (long i = 1; i < count; ++i) {

            // Add the first/next integer.
            set.add((int) i);
        }

        // Return the sorted set.
        return set;
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
        logMessage(getExtraordinary(), String.format("Account key %s has " +
                "been set for rebalance...", getAccountKey()));
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

    /**
     * Gets a child by weight type.
     *
     * @param type The weight type
     * @return A child indexed by the weight type, or null if there is no child
     * mapped to the weight type
     */
    public RebalanceNode getChild(@NotNull WeightType type) {
        return children.get(type).getReceiver();
    }

    /**
     * Gets the weight type assigned to the node.
     *
     * @return The weight type assigned to the node
     */
    public WeightType getType() {
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
     * Rebalances a collection of receiver delegates.
     *
     * @param delegates A collection of receiver delegates
     * @param proposed  The proposed value of this receiver - this value should
     *                  be interpreted as a relative amount from what the
     *                  receiver already has set. On first call, the proposed
     *                  value set in the receiver is null and interpreted as
     *                  zero. Thus, <em>this</em> proposed value is then
     *                  absolute as well as relative
     * @param <T>       A receiver delegate type
     * @return The difference between proposed value and the value that this
     * receiver set
     */
    private <T extends ReceiverDelegate<?>> @NotNull Currency rebalance(
            @NotNull Collection<T> delegates, @NotNull Currency proposed) {

        /*
         * Declare a variable to hold the best accumulated difference.
         * Initialize it to the proposed value. Try to do the rebalance.
         */
        Currency bestDifference = proposed;
        try {

            /*
             * Declare a variable to hold a consideration pattern and a
             * variable to hold a reallocator.
             */
            int considerationPattern;
            Reallocator reallocator;

            /*
             * Declare a variable hold a value list. Get the weight list from
             * the weight allocator.
             */
            List<MutableCurrency> valueList;
            final List<Double> weightList = weightAccumulatorAction.getList();

            /*
             * Set the proposed value as the accumulated difference in the
             * value setter action. Declare a variable to hold the accumulated
             * difference, and initialize it to the accumulated difference in
             * the value setter action.
             */
            valueSetterAction.setAccumulated(proposed);
            Currency accumulatedDifference =
                    valueSetterAction.getAccumulated();

            /*
             * Produce a sorted set of receiver consideration patterns. Get an
             * iterator for the patterns. Cycle while the accumulated
             * difference is not zero, and patterns exist.
             */
            final SortedSet<Integer> patterns = produce(delegates.size());
            final Iterator<Integer> iterator = patterns.iterator();
            while (accumulatedDifference.isNotZero() && iterator.hasNext()) {

                /*
                 * Set the first/next consideration pattern in the
                 * consideration setter action. Perform the action on each
                 * receiver delegate.
                 */
                considerationSetterAction.setConsiderationPattern(
                        considerationPattern = iterator.next());
                doAction(delegates, considerationSetterAction);

                /*
                 * Set the negation flag in the value setter action based on
                 * the sign of the accumulated difference.
                 */
                valueSetterAction.setNegation(
                        accumulatedDifference.compareTo(zero) < 0);

                /*
                 * Create a new reallocator with the weight list. Create a
                 * value list using the size of the weight list and the
                 * accumulated difference.
                 */
                reallocator = new Reallocator(weightList);
                valueList = getInitialList(weightList.size(),
                        accumulatedDifference);

                /*
                 * Reallocate the value list and set the value list in the
                 * value setter action.
                 */
                reallocator.reallocate(valueList);
                valueSetterAction.setList(valueList);

                /*
                 * Perform the value setter action on each receiver delegate.
                 * Reset the accumulated difference.
                 */
                doAction(delegates, valueSetterAction);
                accumulatedDifference = valueSetterAction.getAccumulated();

                /*
                 * Log a message about this current combination of account key,
                 * weight type and consideration pattern.
                 */
                logMessage(getOrdinary(), String.format("For account key " +
                                "%s, weight type %s, and consideration " +
                                "pattern 0x%08x: Found accumulated " +
                                "difference of %s.", getAccountKey(),
                        getWeight(), considerationPattern,
                        accumulatedDifference));

                /*
                 * Does the absolute value of the accumulated difference
                 * compare less than that of the best difference?
                 */
                if (Double.compare(
                        Math.abs(accumulatedDifference.getValue()),
                        Math.abs(bestDifference.getValue())) < 0) {

                    /*
                     * The absolute value of the accumulated difference
                     * compares less than that of the best difference. Ask
                     * each receiver delegate to take a snapshot, and set the
                     * best difference as the accumulated difference.
                     */
                    doAction(delegates, takeSnapshotAction);
                    bestDifference = accumulatedDifference;
                }
            }

            /*
             * Either we ended because the accumulated difference dropped to
             * zero, or because we ran out of consideration patterns. Recover
             * the best snapshot.
             */
            doAction(delegates, recoverAction);
        }

        // Catch any exception that may occur.
        catch (@NotNull Exception exception) {

            // Log a warning saying a rebalance cannot be accomplished.
            logMessage(Level.WARNING, String.format("A rebalance cannot be " +
                            "accomplished for account key %s and weight " +
                            "type %s because of an exception containing the " +
                            "following message: '%s'.",
                    getAccountKey(), getWeight(),
                    exception.getMessage()));

            /*
             * Let each receiver delegate know that explicit values cannot be
             * set. Reset the accumulated differences and set the best
             * difference to zero.
             */
            doAction(delegates, cannotSetAction);
            bestDifference = zero;
        }

        // Do this unconditionally.
        finally {

            // Log a message identifying the best difference.
            logMessage(getExtraordinary(), String.format("For account key " +
                            "%s and weight type %s: I identified a best " +
                            "difference of %s.", getAccountKey(), getWeight(),
                    bestDifference));
        }

        // Calculate the new value of this node.
        final MutableCurrency newValue = new MutableCurrency(proposed);
        newValue.add(bestDifference);

        // Set the current value of this node, and return the best difference.
        setValue(newValue.getImmutable());
        return bestDifference;
    }

    @Override
    public @NotNull Currency setProposed(@NotNull Currency currency) {

        // Declare the return value. Are there children?
        Currency result;
        if (children.isEmpty()) {

            // There are no children. Rebalance the tickers.
            result = rebalance(tickerSet, currency);
        }

        // ...otherwise rebalance the children.
        else {
            result = rebalance(children.values(), currency);
        }

        // Return the result.
        return result;
    }

    /**
     * Sets the value assigned to the node.
     *
     * @param value The new value assigned to the node
     */
    private void setValue(@NotNull Currency value) {
        this.value = value;
    }

    private interface Action<T> {

        /**
         * Performs the action.
         *
         * @param object The parameter
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

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {
            delegate.setConsidered((getConsiderationPattern() &
                    (1 << getConsiderationPattern())) < 0);
        }

        /**
         * Gets the consideration pattern.
         *
         * @return The consideration pattern
         */
        public int getConsiderationPattern() {
            return getContained();
        }

        @Override
        protected @NotNull Integer getInitialValue() {
            return 0;
        }

        /**
         * Sets the consideration pattern.
         *
         * @param considerationPattern The consideration pattern
         */
        public void setConsiderationPattern(int considerationPattern) {
            setContained(considerationPattern);
        }
    }

    private static class SetValueUtility {

        // The accumulated difference between proposed values and set values
        private final MutableCurrency accumulated = new MutableCurrency();

        // The user interpreted flag
        private boolean flag;

        // An index into the currency list
        private int index;

        // A currency list
        private List<MutableCurrency> list;

        {
            resetAccumulated();
        }

        /**
         * Adds a difference to the accumulated difference.
         *
         * @param difference A difference
         */
        public void addDifference(@NotNull Currency difference) {
            accumulated.add(difference);
        }

        /**
         * Gets the accumulated difference between the proposed values and the
         * set values.
         *
         * @return The accumulated difference between the proposed values and
         * the set values
         */
        public @NotNull Currency getAccumulated() {
            return accumulated.getImmutable();
        }

        /**
         * Gets the user interpreted flag.
         *
         * @return The user interpreted flag
         */
        public boolean getFlag() {
            return flag;
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
         * Resets the accumulated difference between the proposed values and
         * the set values.
         */
        public void resetAccumulated() {
            accumulated.set(Currency.getZero());
        }

        /**
         * Resets the element index.
         */
        public void resetIndex() {
            index = 0;
        }

        /**
         * Sets the accumulated difference between the proposed values and the
         * set values.
         *
         * @param difference The accumulated difference between the proposed
         *                   values and the set values
         */
        public void setAccumulated(@NotNull Currency difference) {
            accumulated.set(difference);
        }

        /**
         * Sets the user interpreted flag.
         *
         * @param flag The flag to set
         */
        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        /**
         * Sets the currency list.
         *
         * @param list A currency list
         */
        public void setList(List<MutableCurrency> list) {

            // Set the list and reset the element index.
            this.list = list;
            resetIndex();
        }
    }

    private static class ValueSetterAction extends
            ActionWithContainer<SetValueUtility, ReceiverDelegate<?>> {

        // The value of minus one as currency
        private static final Currency minusOne = new Currency(-1.);

        // The value of one as currency
        private static final Currency one = Currency.getOne();

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Is the delegate considered?
            if (delegate.isConsidered()) {

                /*
                 * The delegate is considered. Get the utility from the
                 * container, and the next value from the utility. Is the next
                 * value not null?
                 */
                final SetValueUtility utility = getContained();
                final MutableCurrency value = utility.getNextElement();
                if (null != value) {

                    /*
                     * The next value is not null. Interpret a set flag in the
                     * utility as an indication that the values in the utility
                     * are meant to be interpreted as negative numbers, and a
                     * clear flag as an indication that they are meant to be
                     * interpreted as non-negative numbers. Apply a suitable
                     * factor to the value. Get any existing value from the
                     * delegate. Is the existing value not null?
                     */
                    value.multiply(utility.getFlag() ? minusOne : one);
                    final Currency existingValue = delegate.getProposed();
                    if (null != existingValue) {

                        /*
                         * The existing value is not null. Add it to the value
                         * from the utility.
                         */
                        value.add(existingValue);
                    }

                    /*
                     * Reset the proposed value of the delegate with the new
                     * value. Receive any returned difference, and add the
                     * difference to the difference in the utility.
                     */
                    utility.addDifference(
                            delegate.setProposed(value.getImmutable()));
                }
            }
        }

        /**
         * Gets the accumulated difference between the proposed values and the
         * set values.
         *
         * @return The accumulated difference between the proposed values and
         * the set values
         */
        public @NotNull Currency getAccumulated() {
            return getContained().getAccumulated();
        }

        @Override
        protected @NotNull SetValueUtility getInitialValue() {
            return new SetValueUtility();
        }

        /**
         * Sets the accumulated difference between the proposed values and the
         * set values.
         *
         * @param difference The accumulated difference between the proposed
         *                   values and the set values
         */
        public void setAccumulated(@NotNull Currency difference) {
            getContained().setAccumulated(difference);
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
         * Sets an indication that the values in the utility are meant to be
         * interpreted as negative numbers.
         *
         * @param negation True if the values in the utility are meant to be
         *                 interpreted as negative numbers; false otherwise
         */
        public void setNegation(boolean negation) {
            getContained().setFlag(negation);
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
    }
}
