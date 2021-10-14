package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class RebalanceNode {

    // The children of the node
    private final Map<WeightType, RebalanceNode> children = new HashMap<>();

    // The tickers in the node
    private final Set<Ticker> tickerSet =
            new TreeSet<>(Comparator.comparing(Ticker::getKey));

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
     * Adds a child to the node.
     *
     * @param child The child to add to the node
     * @return Any child previously mapped to the weight type of the node
     */
    @SuppressWarnings("UnusedReturnValue")
    RebalanceNode addChild(@NotNull RebalanceNode child) {
        return children.put(child.getType(), child);
    }

    /**
     * Adds a ticker to the node.
     *
     * @param ticker The ticker to add to the node
     * @return True if the group did not already contain the specified ticker
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean addTicker(@NotNull Ticker ticker) {
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

    /**
     * Sets the value assigned to the group.
     *
     * @param value The value assigned to the group
     */
    public void setValue(Currency value) {
        this.value = value;
    }
}
