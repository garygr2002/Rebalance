package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Purse;
import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.ticker.TickerDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class Ticker extends
        Common<String, Queryable<?, ?>, TickerDescription> {

    // The considered value of the ticker
    private final Purse considered = new Purse();

    // The "not considered" value of the ticker
    private final Purse notConsidered = new Purse();

    // The proposed value of the ticker
    private final Purse proposed = new Purse();

    /**
     * Creates the ticker hierarchy object.
     *
     * @param ticker The key of the ticker hierarchy object
     */
    Ticker(@NotNull String ticker) {
        super(ticker);
    }

    /**
     * Gets the preferred balance rounding.
     *
     * @return The preferred balance rounding
     */
    public Shares getBalanceRounding() {

        /*
         * Get the ticker description. Return the minimum number of shares if
         * the ticker description is null. Otherwise return the balance
         * balance rounding of the ticker description.
         */
        final TickerDescription description = getDescription();
        return (null == description) ? Shares.getMinimum() :
                description.getBalanceRounding();
    }

    @Override
    public Collection<Queryable<?, ?>> getChildren() {
        return null;
    }

    @Override
    public Currency getConsidered() {
        return considered.getValue();
    }

    public @NotNull Shares getConsideredShares() {
        return considered.getShares();
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.TICKER;
    }

    @Override
    public Currency getNotConsidered() {
        return notConsidered.getValue();
    }

    public @NotNull Shares getNotConsideredShares() {
        return notConsidered.getShares();
    }

    public @NotNull Currency getPrice() {
        return considered.getPrice();
    }

    @Override
    public Currency getProposed() {
        return proposed.getValue();
    }

    public Shares getProposedShares() {
        return proposed.getShares();
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {

        /*
         * Return true if the ticker description is not null, and also contains
         * the indicated fund type.
         */
        final TickerDescription description = getDescription();
        return (null != description) && description.hasType(type);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {
        return type.equals(TaxType.NOT_AN_ACCOUNT);
    }

    @Override
    void setConsidered(double value) {
        considered.setValueAdjustShares(value);
    }

    /**
     * Sets the the number of shares of the ticker holding that is available
     * for rebalancing.
     *
     * @param shares The of shares of the ticker holding that is available for
     *               rebalancing
     */
    void setConsideredShares(double shares) {
        considered.setShares(shares);
    }

    @Override
    void setNotConsidered(double value) {
        notConsidered.setValueAdjustShares(value);
    }

    /**
     * Sets the the number of shares of the ticker holding that is not
     * available for rebalancing.
     *
     * @param shares The of shares of the ticker holding that is not available
     *               for rebalancing
     */
    void setNotConsideredShares(double shares) {
        notConsidered.setShares(shares);
    }

    /**
     * Sets the price of the ticker holding.
     *
     * @param price The price of the ticker holding
     */
    void setPrice(double price) {

        // Keep price consistent in all purses.
        considered.setPrice(price);
        notConsidered.setPrice(price);
        proposed.setPrice(price);
    }

    /**
     * Sets the proposed value of the ticker.
     *
     * @param value The proposed value of the ticker, relative to the value of
     *              the ticker that is considered for rebalance
     */
    void setProposed(double value) {
        proposed.setValueAdjustShares(value);
    }

    /**
     * Sets the proposed number of shares of the ticker holding.
     */
    public void setProposedShares(double shares) {
        proposed.setShares(shares);
    }
}
