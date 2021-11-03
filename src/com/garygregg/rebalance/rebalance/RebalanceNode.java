package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.CurrencyReceiver;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class RebalanceNode implements CurrencyReceiver {

    // The children of the node
    private final Map<WeightType, RebalanceNode> children = new HashMap<>();

    // The can-accept for tickers
    private final CanAccept<Ticker> forTickers =
            Ticker::acceptAnyPositiveValue;

    // The tickers in the node
    private final Set<Ticker> tickerSet =
            new TreeSet<>(Comparator.comparing(Ticker::getKey));

    // The can-accept for nodes
    private final CanAccept<RebalanceNode> forNodes =
            RebalanceNode::acceptAnyPositiveValue;

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
     * Determines if any iterated object can accept any positive value.
     *
     * @param iterator  An iterator
     * @param canAccept A can-accept
     * @param <T>       An arbitrary type
     * @return True if any iterated object can accept any positive value;
     * false otherwise
     */
    private static <T>
    boolean canAcceptAnyPositiveValue(@NotNull Iterator<T> iterator,
                                      @NotNull CanAccept<T> canAccept) {

        /*
         * Declare and initialize the return value. Cycle while iterators
         * exist, and while one has not indicated that it can accept any
         * positive value.
         */
        boolean result = false;
        while (iterator.hasNext() && (!result)) {

            // Reset the result according to the first/next iterated object.
            result = canAccept.acceptAnyPositiveValue(iterator.next());
        }

        // Return the result.
        return true;
    }

    /**
     * Determines if the node can accept any positive currency value.
     *
     * @return True if the ticker can accept any positive currency value;
     * false otherwise
     */
    private boolean acceptAnyPositiveValue() {

        // Return true if any child can accept a positive value, or...
        return canAcceptAnyPositiveValue(children.values().iterator(),
                forNodes) ||

                // ...any ticker can accept a positive value.
                canAcceptAnyPositiveValue(tickerSet.iterator(), forTickers);
    }

    /**
     * Adds a child to the node.
     *
     * @param child The child to add to the node
     * @return Any child previously mapped to the weight type of the node
     */
    @SuppressWarnings("UnusedReturnValue")
    public RebalanceNode addChild(@NotNull RebalanceNode child) {
        return children.put(child.getType(), child);
    }

    /**
     * Adds a ticker to the node.
     *
     * @param ticker The ticker to add to the node
     * @return True if the group did not already contain the specified ticker
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean addTicker(@NotNull Ticker ticker) {
        return tickerSet.add(ticker);
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
        return children.get(type);
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

    @Override
    public void setProposed(@NotNull Currency currency,
                            boolean okayToTakeMore) {

        // TODO: Fill this in.
    }

    /**
     * Sets the value assigned to the group.
     *
     * @param value The value assigned to the group
     */
    public void setValue(Currency value) {
        this.value = value;
    }

    private interface CanAccept<T> {

        /**
         * Determines whether its argument can accept any positive value.
         *
         * @param object The argument
         * @return True if the argument can accept any positive value; false
         * otherwise
         */
        boolean acceptAnyPositiveValue(@NotNull T object);
    }
}
