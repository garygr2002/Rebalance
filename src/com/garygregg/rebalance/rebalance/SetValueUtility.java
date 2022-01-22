package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class SetValueUtility {

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    // The residual component
    private final MutableCurrency residual = new MutableCurrency();

    // An index into the currency list
    private int index;

    // A currency list
    private @NotNull List<? extends MutableCurrency> list;

    /*
     * True if the values in the currency list are to be interpreted as
     * negative values; false if they are to be interpreted as non-negative
     * values
     */
    private boolean negative;

    /*
     * True if the value in the currency list are to be interpreted as relative
     * values; false if they are to be interpreted as absolute values
     */
    private boolean relative;

    {
        resetResidual();
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
     * @return True if the value in the currency list are to be interpreted as
     * negative values; false if they are to be interpreted as non-negative
     * values
     */
    public boolean isNegative() {
        return negative;
    }

    /**
     * Gets the 'relative' flag.
     *
     * @return True if the values in the currency list are to be interpreted as
     * relative values; false if they are to be interpreted as absolute values
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
    public void setList(@NotNull List<? extends MutableCurrency> list) {

        // Set the list and reset the element index.
        this.list = list;
        resetIndex();
    }

    /**
     * Sets the 'negative' flag.
     *
     * @param negative True if the values in the currency list are to be
     *                 interpreted as negative values; false if they are to be
     *                 interpreted as non-negative values
     */
    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    /**
     * Sets the 'relative' flag.
     *
     * @param relative True if the values in the currency list are to be
     *                 interpreted as relative values; false if they are to be
     *                 interpreted as absolute values
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
