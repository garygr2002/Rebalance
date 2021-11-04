package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.CurrencyReceiver;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.Reallocator;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class RebalanceNode implements CurrencyReceiver {

    // For use when a rebalance cannot occur
    private static final OneParameterAction<ReceiverDelegate<?>>
            cannotSetAction = ReceiverDelegate::onCannotSet;

    // For adding value to a delegate
    private static final TwoParameterAction<ReceiverDelegate<?>,
            MutableCurrency> forAdd = (delegate, currency) ->
            delegate.add(currency.getImmutable());

    // For proposing value to a delegate
    private static final TwoParameterAction<ReceiverDelegate<?>,
            MutableCurrency> forProposed = (delegate, currency) ->
            delegate.setProposed(currency.getImmutable());

    // Our local message logger
    private static final MessageLogger messageLogger = new MessageLogger();

    // For use when setting whether a receiver delegate is to be considered
    private static final ConsiderationSetter setConsideredAction =
            new ConsiderationSetter();

    // For use when setting value in a receiver delegate
    private static final ValueSetter valueSetterAction =
            new ValueSetter(forAdd);

    // For use when counting weight in considered receiver delegates
    private static final WeightCounter weightCounterAction =
            new WeightCounter();

    // For use when building a weight list.
    private static final WeightList weightListAction = new WeightList();

    static {
        getLogger().setLogger(Logger.getLogger(
                RebalanceNode.class.getCanonicalName()));
    }

    // The children of the node
    private final SortedMap<WeightType, NodeDelegate> children =
            new TreeMap<>();

    // The tickers in the node
    private final SortedSet<TickerDelegate> tickerSet =
            new TreeSet<>(Comparator.comparing(tickerDelegate ->
                    tickerDelegate.getReceiver().getKey()));

    // The weight type assigned to the node
    private final WeightType type;

    // The weight of the node (set once, accessed by the parent node)
    private final double weight;

    // The value assigned to the node (set by the parent node, accessed here)
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
    }

    /**
     * Performs a one-parameter action on each object in a collection.
     *
     * @param collection The collection
     * @param action     The action to perform on each object in the collection
     * @param <T>        An arbitrary type
     */
    private static <T> void doAction(@NotNull Collection<? extends T> collection,
                                     @NotNull OneParameterAction<T> action) {

        // Cycle for each object in the collection, and perform the action.
        for (T object : collection) {
            action.doAction(object);
        }
    }

    /**
     * Performs a two-parameter action on each object in a collection.
     *
     * @param collection   The collection
     * @param action       The action to perform on each object in the
     *                     collection
     * @param <FirstType>> An arbitrary type
     */
    private static <FirstType> void doAction(
            @NotNull Collection<? extends FirstType> collection,
            @NotNull TwoParameterAction<FirstType, Integer> action) {

        /*
         * Initialize a position counter, and cycle for each object in the
         * collection.
         */
        int i = 0;
        for (FirstType object : collection) {

            /*
             * Call the action using the position counter as the second
             * argument.
             */
            action.doAction(object, i++);
        }
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

            // Reset the first element to the value to distribute.
            list.set(firstIndex, new MutableCurrency(valueToDistribute));
        }

        // Return the list.
        return list;
    }

    /**
     * Gets the message logger for the node.
     *
     * @return The message logger for the node
     */
    private static MessageLogger getLogger() {
        return messageLogger;
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
     * Interprets a receiver delegate.
     *
     * @param delegate A receiver delegate
     * @param <T>      A currency receiver type
     * @return The interpretation of the receiver delegate
     */
    private static <T extends CurrencyReceiver> T interpret(
            ReceiverDelegate<T> delegate) {
        return (null == delegate) ? null : delegate.getReceiver();
    }

    /**
     * Produces a set of integers sorted by descending count of set bits.
     *
     * @param n Integers in the resulting set will be less than 2 ^ (n - 1)
     * @return A set of integers sorted by descending count of set bits
     */
    @Contract(pure = true)
    public static @NotNull SortedSet<Integer> produce(int n) {

        // The argument cannot be negative.
        if (0 > n) {
            throw new IllegalArgumentException(
                    String.format("Negative argument %d not allowed.", n));
        }

        // The argument cannot be larger than the size of an integer.
        else if (Integer.SIZE <= n) {
            throw new IllegalArgumentException(
                    String.format("Argument %d is too big.", n));
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
    public static void reset() {
        getLogger().resetProblem();
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
         * previously mapped to the weight type. Return the interpretation of
         * the old delegate.
         */
        final NodeDelegate delegate = children.put(child.getType(),
                new NodeDelegate(child));
        return interpret(delegate);
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
     * Gets the weight of the node.
     *
     * @return The weight of the node
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Rebalances a collection of receiver delegates.
     *
     * @param receivers A collection of receiver delegates
     * @param <T>       A receiver delegate type
     * @return The value that could not be set
     */
    private <T extends ReceiverDelegate<?>> @NotNull Currency rebalance(
            @NotNull Collection<T> receivers) {

        // First reset the message logger, then try to do the rebalance.
        reset();
        try {

            /*
             * Produce a sorted set of receiver consideration patterns. Set the
             * initial action for the value setter.
             */
            final SortedSet<Integer> patterns = produce(receivers.size());
            valueSetterAction.setAction(forProposed);

            /*
             * Declare a variable to receive accumulated weight, a variable to
             * receive a reallocator, and a variable to receive a value list.
             */
            double accumulatedWeight;
            Reallocator reallocator;
            List<MutableCurrency> valueList;

            /*
             * Declare and initialize the value to distribute. Get the weight
             * list from the weight list action. Declare and initialize a flag
             * indicating zero difference.
             */
            Currency valueToDistribute = getValue();
            final List<Double> weightList = weightListAction.getList();
            boolean zeroDifference = false;

            /*
             * Get an iterator of consideration patterns. Cycle until there is
             * zero difference, or until each consideration pattern has been
             * exhausted.
             */
            final Iterator<Integer> iterator = patterns.iterator();
            while ((!zeroDifference) && iterator.hasNext()) {

                /*
                 * Set the first/next consideration pattern in the 'set
                 * considered' action. Perform the setter action on each
                 * receiver delegate.
                 */
                setConsideredAction.setConsiderationPattern(iterator.next());
                doAction(receivers, setConsideredAction);

                /*
                 * Clear the weight list, then rebuild it using each receiver
                 * delegate.
                 */
                weightList.clear();
                doAction(receivers, weightListAction);

                /*
                 * Reset the weight counter action, and accumulate the weight
                 * for each element of the weight list. Retrieve the
                 * accumulated weight from the weight counter action.
                 */
                weightCounterAction.resetWeight();
                doAction(weightList, weightCounterAction);
                accumulatedWeight = weightCounterAction.getWeight();

                /*
                 * Create a new reallocator with the weight list. Create a
                 * value list using the size of the weight list and the value
                 * to distribute.
                 */
                reallocator = new Reallocator(weightList);
                valueList = getInitialList(weightList.size(),
                        valueToDistribute);

                /*
                 * Reallocate the value list and set the value list in the
                 * value setter action.
                 */
                reallocator.reallocate(valueList);
                valueSetterAction.setList(valueList);

                /*
                 * Perform the value setter action on each receiver delegate.
                 * Reset the action in the value setter action.
                 */
                doAction(receivers, valueSetterAction);
                valueSetterAction.setAction(forAdd);

                /*
                 * TODO:
                 *
                 * 1. Accumulate differences from each delegate
                 *
                 * 2. Take a snapshot if first iteration, or if
                 * differences were less than on the last iteration
                 *
                 * 3. Set the differences in value-to-distribute
                 *
                 * 4. Set the zero difference flag if the difference is
                 * zero.
                 *
                 * 5. Find some way to return non-zero difference upward
                 * in the call stack.
                 */

                // Reset the zero-difference flag.
                zeroDifference = true;
            }
        }

        // Catch any exception that may occur.
        catch (@NotNull Exception exception) {

            // Log a warning saying a rebalance cannot be accomplished.
            getLogger().logMessage(Level.WARNING,
                    String.format("Rebalance cannot be accomplished " +
                                    "because for the following reason: %s",
                            exception.getMessage()));

            /*
             * Let each receiver delegate know that explicit values cannot be
             * set.
             */
            doAction(receivers, cannotSetAction);
        }

        // TODO: Fix this.
        return Currency.getZero();
    }

    @Override
    public @NotNull Currency setProposed(@NotNull Currency currency) {

        /*
         * Declare the return value. Set the value assigned to the group. Are
         * there no children?
         */
        Currency result;
        setValue(currency);
        if (children.isEmpty()) {

            // There are no children. Rebalance the tickers.
            result = rebalance(tickerSet);
        }

        // ...otherwise rebalance the children.
        else {
            result = rebalance(children.values());
        }

        // Return the result.
        return result;
    }

    /**
     * Sets the value assigned to the group.
     *
     * @param value The value assigned to the group
     */
    private void setValue(Currency value) {
        this.value = value;
    }

    private interface OneParameterAction<T> {

        /**
         * Performs the action.
         *
         * @param object The parameter
         */
        void doAction(@NotNull T object);
    }

    private interface TwoParameterAction<FirstType, SecondType> {

        /**
         * Performs the action.
         *
         * @param first  The first parameter
         * @param second The second parameter
         */
        void doAction(@NotNull FirstType first,
                      @NotNull SecondType second);
    }

    private static class ConsiderationSetter implements
            TwoParameterAction<ReceiverDelegate<?>, Integer> {

        // The consideration pattern
        private int considerationPattern;

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate,
                             @NotNull Integer position) {
            delegate.setConsidered((getConsiderationPattern() &
                    (1 << position)) < 0);
        }

        /**
         * Gets the consideration pattern.
         *
         * @return The consideration pattern
         */
        private int getConsiderationPattern() {
            return considerationPattern;
        }

        /**
         * Sets the consideration pattern.
         *
         * @param considerationPattern The consideration pattern
         */
        public void setConsiderationPattern(int considerationPattern) {
            this.considerationPattern = considerationPattern;
        }
    }

    private static class ElementContainer<ElementType> {

        // A two-parameter action taking the element as its second argument
        private TwoParameterAction<ReceiverDelegate<?>, ElementType> action;

        // An index into the element list
        private int index;

        // An element list
        private List<ElementType> list;

        /**
         * Constructs the element container.
         *
         * @param action A two-parameter action taking the element as its
         *               second argument
         */
        public ElementContainer(
                @NotNull TwoParameterAction<ReceiverDelegate<?>,
                        ElementType> action) {
            setAction(action);
        }

        public @NotNull TwoParameterAction<ReceiverDelegate<?>, ElementType>
        getAction() {
            return action;
        }

        /**
         * Gets the next element.
         *
         * @return The next element, or null if there are no more elements
         */
        public @Nullable ElementType getNextElement() {
            return ((null != list) && (index < list.size())) ?
                    list.get(index++) : null;
        }

        /**
         * Resets the element index.
         */
        public void resetIndex() {
            index = 0;
        }

        /**
         * Sets the action.
         *
         * @param action A two-parameter action taking the element as its
         *               second argument
         */
        public void setAction(@NotNull TwoParameterAction<ReceiverDelegate<?>,
                ElementType> action) {
            this.action = action;
        }

        /**
         * Sets the element list.
         *
         * @param list An element list
         */
        public void setList(List<ElementType> list) {

            // Set the list and reset the element index.
            this.list = list;
            resetIndex();
        }
    }

    private abstract static class
    MemberContainer<ParameterType, ContainedType> implements
            OneParameterAction<ParameterType> {

        // Our member variable
        private ContainedType member;

        /**
         * Gets the initial value of the member variable.
         *
         * @return The initial value of the member variable
         */
        protected abstract @NotNull ContainedType getInitialValue();

        /**
         * Gets the member variable.
         *
         * @return The member variable
         */
        protected @NotNull ContainedType getMember() {

            // Set the member variable if it is null.
            if (null == member) {
                member = getInitialValue();
            }

            // Return the member variable.
            return member;
        }

        /**
         * Sets the member variable.
         *
         * @param member The member variable
         */
        protected void setMember(@NotNull ContainedType member) {
            this.member = member;
        }
    }

    private static class ValueSetter extends
            MemberContainer<ReceiverDelegate<?>, ElementContainer<MutableCurrency>> {

        // The element container
        private final ElementContainer<MutableCurrency> container;

        /**
         * Constructs the value setter.
         *
         * @param action An action to set a receiver delegate
         */
        public ValueSetter(
                @NotNull TwoParameterAction<ReceiverDelegate<?>,
                        MutableCurrency> action) {
            container = new ElementContainer<>(action);
        }

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Is the delegate considered?
            if (delegate.isConsidered()) {

                /*
                 * The delegate is considered. Get the next value. Is the next
                 * value not null?
                 */
                final MutableCurrency value = getMember().getNextElement();
                if (null != value) {

                    /*
                     * The next value is not null. Use the action to set the
                     * value in the delegate.
                     */
                    getMember().getAction().doAction(delegate, value);
                }
            }
        }

        @Override
        protected @NotNull ElementContainer<MutableCurrency> getInitialValue() {
            return container;
        }

        /**
         * Sets the action in the value setter.
         *
         * @param action An action to set a receiver delegate
         */
        public void setAction(
                @NotNull TwoParameterAction<ReceiverDelegate<?>,
                        MutableCurrency> action) {
            container.setAction(action);
        }

        /**
         * Sets the currency list.
         *
         * @param list The currency list
         */
        public void setList(@NotNull List<MutableCurrency> list) {
            getMember().setList(list);
        }
    }

    private static class WeightCounter extends
            MemberContainer<Double, Double> {

        @Override
        public void doAction(@NotNull Double weight) {
            setMember(getWeight() + weight);
        }

        @Override
        protected @NotNull Double getInitialValue() {
            return 0.;
        }

        /**
         * Gets the accumulated weight.
         *
         * @return The accumulated weight
         */
        public double getWeight() {
            return getMember();
        }

        /**
         * Resets the weight.
         */
        public void resetWeight() {
            setMember(getInitialValue());
        }
    }

    private static class WeightList extends
            MemberContainer<ReceiverDelegate<?>, List<Double>> {

        // The weight list
        private static final List<Double> weightList = new ArrayList<>();

        @Override
        public void doAction(@NotNull ReceiverDelegate<?> delegate) {

            // Add the weight if the delegate is to be considered.
            if (delegate.isConsidered()) {
                getList().add(delegate.getWeight());
            }
        }

        @Override
        protected @NotNull List<Double> getInitialValue() {
            return weightList;
        }

        /**
         * Gets the weight list.
         *
         * @return The weight list
         */
        public @NotNull List<Double> getList() {
            return getMember();
        }
    }
}
