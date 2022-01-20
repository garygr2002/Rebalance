package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

public class Purse {

    // A currency factory
    private static final Factory<MutableCurrency> currencyFactory =
            MutableCurrency::new;

    // The default price
    private static final double defaultPrice = 1.;

    // The default number of shares
    private static final double defaultShares = 1.;

    // A price factory
    private static final Factory<MutablePrice> priceFactory =
            MutablePrice::new;

    // A shares factory
    private static final Factory<MutableShares> sharesFactory =
            MutableShares::new;

    // The price of the purse
    private MutablePrice price;

    // The shares of the purse
    private MutableShares shares;

    // The value of the purse
    private MutableCurrency value;

    // The reset for price
    private final Reset forPrice = new Reset() {

        @Override
        public boolean okayToReset() {
            return (null == shares) || shares.isNotZero();
        }

        @Override
        public void performReset() {

            // Make sure there is at least one share, then set the price.
            shares = set(shares, defaultShares, sharesFactory, false);
            price = set(price, value.getValue() / shares.getValue(),
                    priceFactory, true);
        }
    };

    /**
     * Produces a new countable with a given value if the existing countable is
     * null, or optionally sets an existing countable with the given value if
     * the existing countable is <i>not</i> null.
     *
     * @param countable    The existing countable
     * @param newValue     The value to use
     * @param factory      A factory for producing a new countable, if
     *                     necessary
     * @param setIfNotNull True to set an existing countable, false otherwise
     * @param <T>          Any type that extends mutable countable
     * @return The existing countable if it had not been null, or the newly
     * created countable
     */
    private static <T extends MutableCountable>
    @NotNull T set(T countable, double newValue,
                   @NotNull Factory<? extends T> factory,
                   boolean setIfNotNull) {

        // Produce a new countable if the existing countable is null...
        if (null == countable) {
            countable = factory.produce(newValue);
        }

        // ...otherwise set the existing countable if the flag so indicates.
        else if (setIfNotNull) {
            countable.set(newValue);
        }

        // Return the countable.
        return countable;
    }    // The reset for shares

    private final Reset forShares = new Reset() {

        @Override
        public boolean okayToReset() {
            return (null == price) || price.isNotZero();
        }

        @Override
        public void performReset() {

            /*
             * Make sure there is a price of at least 1.00, then set the
             * shares.
             */
            price = set(price, defaultPrice, priceFactory, false);
            shares = set(shares, calculateShares(value.getImmutable()),
                    sharesFactory, true);
        }
    };

    /**
     * Calculates the number of shares required to result in a given value,
     * considering the current price.
     *
     * @param value The given value
     * @return The number of shares required to result in a given value,
     * considering the current price
     */
    public Double calculateShares(@NotNull Currency value) {

        /*
         * Get the price. Return the value divided by the price if it is okay
         * to reset the shares. Otherwise, return null.
         */
        final Price price = getPrice();
        return forShares.okayToReset() ?
                (value.getValue() /
                        ((null == price) ? 0. : price.getValue())) : null;
    }

    /**
     * Gets the price.
     *
     * @return The price
     */
    public Price getPrice() {
        return (null == price) ? null : price.getImmutable();
    }

    /**
     * Gets the shares.
     *
     * @return The shares
     */
    public Shares getShares() {
        return (null == shares) ? null : shares.getImmutable();
    }

    /**
     * Gets the value.
     *
     * @return The value
     */
    public Currency getValue() {
        return (null == value) ? null : value.getImmutable();
    }

    /**
     * Resets value if there has been a change in either price or shares.
     */
    private void resetValue() {

        // Are both shares and price not null?
        if (!((null == shares) || (null == price))) {

            /*
             * Shares and price are both not null. Set the value as the product
             * of shares and price.
             */
            value = set(value, shares.getValue() * price.getValue(),
                    currencyFactory, true);
        }
    }

    /**
     * Sets the price.
     *
     * @param newPrice The new value for price
     */
    public void setPrice(double newPrice) {

        // Set the price, then reset the value.
        price = set(price, newPrice, priceFactory, true);
        resetValue();
    }

    /**
     * Set the shares.
     *
     * @param newShares The new value for shares
     */
    public void setShares(double newShares) {

        // Set the shares, then reset the value.
        shares = set(shares, newShares, sharesFactory, true);
        resetValue();
    }

    /**
     * Sets the value.
     *
     * @param newValue The new value
     * @param reset    An adjustment to perform for the changed value
     */
    private void setValue(double newValue,
                          @NotNull Reset reset) {

        // Does the adjustment allow a reset?
        if (reset.okayToReset()) {

            /*
             * The adjustment allows a reset. Set the new value, and perform
             * the reset.
             */
            value = set(value, newValue, currencyFactory, true);
            reset.performReset();
        }
    }

    /**
     * Sets the value, adjusting price.
     *
     * @param newValue The new value to set
     */
    @SuppressWarnings("unused")
    public void setValueAdjustPrice(double newValue) {
        setValue(newValue, forPrice);
    }

    /**
     * Sets the value, adjusting shares.
     *
     * @param newValue The new value to set
     */
    @SuppressWarnings("unused")
    public void setValueAdjustShares(double newValue) {
        setValue(newValue, forShares);
    }

    private interface Factory<T> {

        /**
         * Produces an object using a floating point value.
         *
         * @param value The value to use
         * @return The object that the factory produced
         */
        @NotNull T produce(double value);
    }

    private interface Reset {

        /**
         * True if it is okay to do a reset.
         *
         * @return True if it is okay to do a reset, false otherwise
         */
        boolean okayToReset();

        /**
         * Performs the required reset.
         */
        void performReset();
    }
}
