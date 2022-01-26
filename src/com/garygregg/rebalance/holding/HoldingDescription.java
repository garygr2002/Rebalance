package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Price;
import com.garygregg.rebalance.countable.Purse;
import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.toolkit.Description;
import com.garygregg.rebalance.toolkit.HoldingKey;
import com.garygregg.rebalance.toolkit.HoldingLineType;
import org.jetbrains.annotations.NotNull;

public class HoldingDescription implements Description<Integer> {

    /*
     * The parent/child of the holding (parent key first, child key second);
     * examples: 1) default and portfolio; 2) portfolio and institution; 3)
     * institution and account, or; 4) account and ticker.
     */
    private final HoldingKey holdingParentChild;

    /*
     * The line number where the holding was observed (and the key of the
     * holding)
     */
    private final Integer lineNumber;

    // The shares, price and value of the holding
    private final Purse purse = new Purse();

    // The holding line type
    private HoldingLineType lineType;

    // The name associated with the holding
    private String name;

    // The rebalancing weight of the holding
    private Double weight;

    /**
     * Constructs the holding description.
     *
     * @param lineNumber         The line number where the holding was observed
     * @param holdingParentChild The parent/child of the holding
     */
    HoldingDescription(@NotNull Integer lineNumber,
                       @NotNull HoldingKey holdingParentChild) {

        // Set the member variables.
        this.holdingParentChild = holdingParentChild;
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the holding parent/child.
     *
     * @return The holding parent/child
     */
    public @NotNull HoldingKey getHoldingParentChild() {
        return holdingParentChild;
    }

    @Override
    public @NotNull Integer getKey() {
        return lineNumber;
    }

    /**
     * Gets the holding line type.
     *
     * @return The holding line type
     */
    public HoldingLineType getLineType() {
        return lineType;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the price of the holding.
     *
     * @return The price of the holding
     */
    public Price getPrice() {
        return purse.getPrice();
    }

    /**
     * Gets the shares in the holding.
     *
     * @return The shares in the holding
     */
    public Shares getShares() {
        return purse.getShares();
    }

    /**
     * Gets the value of the holding.
     *
     * @return The value of the holding
     */
    public Currency getValue() {
        return purse.getValue();
    }

    /**
     * Gets the rebalancing weight of the holding.
     *
     * @return The rebalancing weight of the holding
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the holding line type.
     *
     * @param lineType The holding line type
     */
    void setLineType(HoldingLineType lineType) {
        this.lineType = lineType;
    }

    /**
     * Sets the name of the holding.
     *
     * @param name The name of the holding
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the price of the holding.
     *
     * @param price The price of the holding
     */
    void setPrice(double price) {
        purse.setPrice(price);
    }

    /**
     * Sets the shares in the holding.
     *
     * @param shares The shares in the holding
     */
    void setShares(double shares) {
        purse.setShares(shares);
    }

    /**
     * Sets the value of the holding.
     *
     * @param value The value of the holding
     */
    void setValue(double value) {
        purse.setValueAdjustShares(value);
    }

    /**
     * Sets the rebalancing weight of the holding.
     *
     * @param weight The rebalancing weight of the holding
     */
    void setWeight(Double weight) {
        this.weight = weight;
    }
}
