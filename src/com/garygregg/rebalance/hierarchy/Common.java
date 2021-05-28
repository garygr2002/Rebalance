package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

abstract class Common<KeyType,
        ChildType extends Queryable<?, ?>,
        DescriptionType extends Description<? extends KeyType>>
        implements Queryable<KeyType, ChildType> {

    // The key of this common hierarchy object
    private final KeyType key;

    // Our breakdown manager for the weight type
    private final BreakdownManager<WeightType> weightTypeManager =
            new BreakdownManager<>();

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
     * Causes the breakdown managers to perform their calculations.
     */
    abstract void breakdown();

    /**
     * Clears the breakdown managers.
     */
    void clear() {
        getWeightTypeManager().clear();
    }

    /**
     * Gets the value of the hierarchy object that can be considered for
     * rebalance specific to the given weight type.
     *
     * @param type A weight type (null for all types)
     * @return The value of the hierarchy object that can be considered for
     * rebalance, specific to the given weight type
     */
    public @NotNull Currency getConsidered(@NotNull WeightType type) {
        return getWeightTypeManager().getConsidered(type);
    }

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
     * @param type A weight type (null for all types)
     * @return The value of the hierarchy object that cannot be considered for
     * rebalance, specific to the given weight type
     */
    public @NotNull Currency getNotConsidered(@NotNull WeightType type) {
        return getWeightTypeManager().getNotConsidered(type);
    }

    /**
     * Gets the proposed value of the hierarchy object specific to the given
     * weight type.
     *
     * @param type A weight type (null for all types)
     * @return The proposed value of the hierarchy object, relative to the
     * value in the hierarchy object that is considered for rebalance and
     * specific to the given weight type
     */
    public @NotNull Currency getProposed(@NotNull WeightType type) {
        return getWeightTypeManager().getProposed(type);
    }

    /**
     * Gets the breakdown manager for the weight type.
     *
     * @return The breakdown manager for the weight type
     */
    protected @NotNull BreakdownManager<WeightType> getWeightTypeManager() {
        return weightTypeManager;
    }

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
    void setCurrent() {
        getWeightTypeManager().setCurrent();
    }

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
    void setProposed() {
        getWeightTypeManager().setProposed();
    }

    @Override
    public String toString() {
        return getKey().toString();
    }
}
