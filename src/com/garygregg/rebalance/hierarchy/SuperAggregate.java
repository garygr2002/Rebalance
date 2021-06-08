package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

abstract class SuperAggregate<KeyType,
        ChildType extends Aggregate<?, ?, ?>,
        DescriptionType extends Description<? extends KeyType>>
        extends Aggregate<KeyType, ChildType, DescriptionType> {

    // Our breakdown manager for the tax type
    private final TaxBreakdownManager<ChildType> taxTypeManager =
            new TaxBreakdownManager<>();

    // Accumulates values by tax type in each child
    private final Operation taxTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            final TaxBreakdownManager<ChildType> taxTypeManager =
                    getTaxTypeManager();

            for (TaxType type : TaxType.values()) {
                taxTypeManager.add(type, child);
            }
        }
    };

    /**
     * Constructs the super-aggregate hierarchy object.
     *
     * @param key The key uniquely identifying the super-aggregate object
     */
    public SuperAggregate(@NotNull KeyType key) {
        super(key);
    }

    /**
     * Accumulates values by tax type in each child.
     */
    protected void accumulateTaxType() {

        /*
         * Note: This method must be overridden in the Account class to add
         * only the single tax type.
         */
        doOperation(taxTypeAccumulate);
    }

    @Override
    void breakdown() {

        // Call the superclass method, then accumulate values by tax type.
        super.breakdown();
        accumulateTaxType();
    }

    @Override
    void clear() {

        // Call the superclass method, then clear the tax type manager.
        super.clear();
        getTaxTypeManager().clear();
    }

    /**
     * Gets the value of the hierarchy object that can be considered for
     * rebalance specific to the given tax type.
     *
     * @param type A tax type (null for all types)
     * @return The value of the hierarchy object that can be considered for
     * rebalance, specific to the given tax type
     */
    public @NotNull Currency getConsidered(@NotNull TaxType type) {
        return getTaxTypeManager().getConsidered(type);
    }

    /**
     * Gets the value of the hierarchy object that cannot be considered for
     * rebalance specific to the given tax type.
     *
     * @param type A tax type (null for all types)
     * @return The value of the hierarchy object that cannot be considered for
     * rebalance, specific to the given tax type
     */
    public @NotNull Currency getNotConsidered(@NotNull TaxType type) {
        return getTaxTypeManager().getNotConsidered(type);
    }

    /**
     * Gets the proposed value of the hierarchy object specific to the given
     * tax type.
     *
     * @param type A tax type (null for all types)
     * @return The proposed value of the hierarchy object, relative to the
     * value in the hierarchy object that is considered for rebalance and specific
     * to the given tax type
     */
    public @NotNull Currency getProposed(@NotNull TaxType type) {
        return getTaxTypeManager().getProposed(type);
    }

    /**
     * Gets the breakdown manager for the tax type.
     *
     * @return The breakdown manager for the tax type
     */
    protected @NotNull TaxBreakdownManager<ChildType> getTaxTypeManager() {
        return taxTypeManager;
    }

    /**
     * Sets the breakdown managers to work with current values.
     */
    public void setCurrent() {

        /*
         * Set the tax type manager to work with current values, then call the
         * superclass method.
         */
        getTaxTypeManager().setCurrent();
        super.setCurrent();
    }

    /**
     * Sets the breakdown managers to work with proposed values.
     */
    public void setProposed() {

        /*
         * Set the tax type manager to work with proposed values, then call the
         * superclass method.
         */
        getTaxTypeManager().setCurrent();
        super.setCurrent();
    }
}
