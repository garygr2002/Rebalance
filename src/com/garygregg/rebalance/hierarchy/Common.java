package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

abstract class Common<KeyType,
        ChildType extends Common<?, ?, ?>,
        DescriptionType extends Description<? extends KeyType>>
        implements Queryable<KeyType, ChildType> {

    // The key of this common hierarchy object
    private final KeyType key;

    // The description of this hierarchy object
    private DescriptionType description;

    /**
     * Constructs this common hierarchy object.
     *
     * @param key The key uniquely identifying the hierarchy object
     */
    public Common(@NotNull KeyType key) {
        this.key = key;
    }

    /**
     * Gets the value from currency.
     *
     * @param currency The currency from which to get value
     * @return The value of the currency object, or a default if the currency
     * object is null
     */
    private static double getValue(Currency currency) {

        // Set the currency object to zero if it is null.
        if (null == currency) {
            currency = Currency.getZero();
        }

        // Return the value of the currency object.
        return currency.getValue();
    }

    /**
     * Breaks down the hierarchy object valuation by category.
     */
    abstract void breakdown();

    /**
     * Clears breakdown values in the hierarchy object.
     */
    abstract void clear();

    /**
     * Gets the value of the hierarchy object that can be considered for
     * rebalance specific to the given weight type.
     *
     * @param type A weight type
     * @return The value of the hierarchy object that can be considered for
     * rebalance, specific to the given weight type
     */
    public abstract @NotNull Currency getConsidered(@NotNull WeightType type);

    /**
     * Gets the description of the hierarchy object.
     *
     * @return The description of the hierarchy object
     */
    public DescriptionType getDescription() {
        return description;
    }

    @Override
    public @NotNull KeyType getKey() {
        return key;
    }

    /**
     * Gets the holding line type for this hierarchy object.
     *
     * @return The holding line type for this hierarchy object
     */
    public abstract @NotNull HoldingLineType getLineType();

    /**
     * Gets the value of the hierarchy object that cannot be considered for
     * rebalance specific to the given weight type.
     *
     * @param type A weight type
     * @return The value of the hierarchy object that cannot be considered for
     * rebalance, specific to the given weight type
     */
    public abstract @NotNull Currency getNotConsidered(@NotNull WeightType type);

    /**
     * Gets the proposed value of the hierarchy object specific to the given
     * weight type.
     *
     * @param type A weight type
     * @return The proposed value of the hierarchy object, relative to the
     * value in the hierarchy object that is considered for rebalance and
     * specific to the given weight type
     */
    public abstract @NotNull Currency getProposed(@NotNull WeightType type);

    /**
     * Sets the value of the hierarchy object that is available for
     * rebalancing.
     *
     * @param value The value that is available for rebalancing
     */
    abstract void setConsidered(double value);

    /**
     * Sets the breakdown managers to work with current values.
     */
    abstract void setCurrent();

    /**
     * Sets the description of the hierarchy object.
     *
     * @param description The description of the hierarchy object
     */
    void setDescription(DescriptionType description) {
        this.description = description;
    }

    /**
     * Sets the value of the hierarchy object that is not available for
     * rebalancing.
     *
     * @param value The value that is not available for rebalancing
     */
    abstract void setNotConsidered(double value);

    /**
     * Sets the breakdown managers to work with proposed values.
     */
    abstract void setProposed();

    @Override
    public String toString() {
        return getKey().toString();
    }

    /**
     * Transfers value from a passed queryable to this object.
     *
     * @param queryable The queryable from which to obtain value
     */
    void transferValue(@NotNull Queryable<?, ?> queryable) {

        /*
         * Set the considered and not considered values to the same values as
         * those contained in the passed hierarchy object.
         */
        setConsidered(getValue(queryable.getConsidered()));
        setNotConsidered(getValue(queryable.getNotConsidered()));
    }
}
