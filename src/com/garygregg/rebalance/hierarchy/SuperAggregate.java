package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

abstract class SuperAggregate<KeyType,
        ChildType extends Aggregate<?, ?, ?>,
        DescriptionType extends Description<? extends KeyType>>
        extends Aggregate<KeyType, ChildType, DescriptionType> {

    // Our breakdown manager for the category type
    private final CategoryBreakdownManager<ChildType> categoryTypeManager =
            new CategoryBreakdownManager<>();

    // Accumulates values by category type in each child
    private final Operation categoryTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            // The breakdown manager for the category type
            final CategoryBreakdownManager<ChildType> categoryTypeManager =
                    getCategoryTypeManager();

            /*
             * Cycle for each category type, and add the value specified for
             * the child for that category type.
             */
            for (CategoryType type : CategoryType.values()) {
                categoryTypeManager.add(type, child);
            }
        }
    };

    // Our breakdown manager for the tax type
    private final TaxBreakdownManager<ChildType> taxTypeManager =
            new TaxBreakdownManager<>();

    // Accumulates values by tax type in each child
    private final Operation taxTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            // The breakdown manager for the tax type
            final TaxBreakdownManager<ChildType> taxTypeManager =
                    getTaxTypeManager();

            /*
             * Cycle for each tax type, and add the value specified for the
             * child for that tax type.
             */
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
     * Accumulates values by category type in each child.
     */
    protected void accumulateCategoryType() {
        doOperation(categoryTypeAccumulate);
    }

    /**
     * Accumulates values by tax type in each child.
     */
    protected void accumulateTaxType() {
        doOperation(taxTypeAccumulate);
    }

    @Override
    void breakdown() {

        /*
         * Call the superclass method, then accumulate values by tax type and
         * category type.
         */
        super.breakdown();
        accumulateTaxType();
        accumulateCategoryType();
    }

    @Override
    void clear() {

        /*
         * Call the superclass method, then clear both the category and tax
         * type managers.
         */
        super.clear();
        getCategoryTypeManager().clear();
        getTaxTypeManager().clear();
    }

    /**
     * Gets the breakdown manager for the category type.
     *
     * @return The breakdown manager for the category type
     */
    protected @NotNull CategoryBreakdownManager<ChildType>
    getCategoryTypeManager() {
        return categoryTypeManager;
    }

    /**
     * Gets the value of the hierarchy object that can be considered for
     * rebalance specific to the given category type.
     *
     * @param type A category type
     * @return The value of the hierarchy object that can be considered for
     * rebalance, specific to the given category type
     */
    public @NotNull Currency getConsidered(@NotNull CategoryType type) {
        return getCategoryTypeManager().getConsidered(type);
    }

    /**
     * Gets the value of the hierarchy object that can be considered for
     * rebalance specific to the given tax type.
     *
     * @param type A tax type
     * @return The value of the hierarchy object that can be considered for
     * rebalance, specific to the given tax type
     */
    public @NotNull Currency getConsidered(@NotNull TaxType type) {
        return getTaxTypeManager().getConsidered(type);
    }

    /**
     * Gets the value of the hierarchy object that cannot be considered for
     * rebalance specific to the given category type.
     *
     * @param type A category type
     * @return The value of the hierarchy object that cannot be considered for
     * rebalance, specific to the given category type
     */
    public @NotNull Currency getNotConsidered(@NotNull CategoryType type) {
        return getCategoryTypeManager().getNotConsidered(type);
    }

    /**
     * Gets the value of the hierarchy object that cannot be considered for
     * rebalance specific to the given tax type.
     *
     * @param type A tax type
     * @return The value of the hierarchy object that cannot be considered for
     * rebalance, specific to the given tax type
     */
    public @NotNull Currency getNotConsidered(@NotNull TaxType type) {
        return getTaxTypeManager().getNotConsidered(type);
    }

    /**
     * Gets the proposed value of the hierarchy object specific to the given
     * category type.
     *
     * @param type A category type
     * @return The proposed value of the hierarchy object, relative to the
     * value in the hierarchy object that is considered for rebalance and specific
     * to the given category type
     */
    public @NotNull Currency getProposed(@NotNull CategoryType type) {
        return getCategoryTypeManager().getProposed(type);
    }

    /**
     * Gets the proposed value of the hierarchy object specific to the given
     * tax type.
     *
     * @param type A tax type
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

    @Override
    public boolean hasCategoryType(@NotNull CategoryType type) {
        return type.equals(CategoryType.NOT_AN_ACCOUNT);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {
        return type.equals(TaxType.NOT_AN_ACCOUNT);
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
