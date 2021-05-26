package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.HoldingLineType;
import org.jetbrains.annotations.NotNull;

abstract class Common<KeyType,
        ChildType extends Queryable<?, ?>,
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
     * Sets the value of the hierarchy object that is available for
     * rebalancing.
     *
     * @param value The value that is available for rebalancing
     */
    abstract void setConsidered(double value);

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

    @Override
    public String toString() {
        return getKey().toString();
    }
}
