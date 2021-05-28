package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

abstract class Aggregate<KeyType,
        ChildType extends Common<?, ?, ?>,
        DescriptionType extends Description<? extends KeyType>>
        extends Common<KeyType, ChildType, DescriptionType> {

    // Breaks down values in each child
    private final Operation doBreakdown = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.breakdown();
        }
    };

    // Clears breakdown values in each child
    private final Operation doClear = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.clear();
        }
    };

    // Sets breakdown managers in a child to work with current values
    private final Operation setCurrent = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.setCurrent();
        }
    };

    // Sets breakdown managers in a child to work with proposed values
    private final Operation setProposed = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.setProposed();
        }
    };

    // Our breakdown manager for the tax type
    private final BreakdownManager<TaxType> taxTypeManager =
            new BreakdownManager<>();

    // Accumulates values by tax type in each child
    private final Operation taxTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            final BreakdownManager<TaxType> taxTypeManager =
                    getTaxTypeManager();

            for (TaxType type : TaxType.values()) {
                taxTypeManager.add(type, child);
            }
        }
    };

    // Accumulates values by weight type in each child
    private final Operation weightTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            final BreakdownManager<WeightType> weightTypeManager =
                    getWeightTypeManager();

            for (WeightType type : WeightType.values()) {
                weightTypeManager.add(type, child);
            }
        }
    };

    // A collection of child hierarchy objects
    private Collection<ChildType> collection;

    // The considered value of the hierarchy object
    private MutableCurrency considered;

    // A map of child keys to child hierarchy objects
    private Map<Object, ChildType> map = createModifiableMap();

    // The "not considered" value of the hierarchy object
    private MutableCurrency notConsidered;

    /**
     * Constructs the aggregate hierarchy object.
     *
     * @param key The key uniquely identifying the aggregate object
     */
    public Aggregate(@NotNull KeyType key) {

        // Call the superclass method, and make sure the children are unlocked.
        super(key);
        unlockChildren();
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

    /**
     * Accumulates values by weight type in each child.
     */
    protected void accumulateWeightType() {
        doOperation(weightTypeAccumulate);
    }

    /**
     * Attempts to add a child to the aggregate.
     *
     * @param hierarchyObject A hierarchy object to attempt to add as a child
     *                        of the aggregate
     * @return Any child that was previously mapped using the same key
     * @throws ClassCastException If the hierarchy object is not castable to
     *                            the child type of this aggregate
     */
    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    ChildType addChild(@NotNull Common<?, ?, ?> hierarchyObject)
            throws ClassCastException {
        return map.put(hierarchyObject.getKey(), (ChildType) hierarchyObject);
    }

    @Override
    void breakdown() {

        /*
         * Do the following for each child: Break down values; accumulate
         * values by weight type; accumulate values by tax type.
         */
        breakdownChildren();
        accumulateWeightType();
        accumulateTaxType();
    }

    /**
     * Breaks down values in each child.
     */
    protected void breakdownChildren() {
        doOperation(doBreakdown);
    }

    @Override
    void clear() {

        /*
         * Do the operation for each child. Clear breakdown values in the tax
         * type manager, then call the superclass method.
         */
        doOperation(doClear);
        getTaxTypeManager().clear();
        super.clear();
    }

    /**
     * Creates a modifiable child map.
     *
     * @return A modifiable child map
     */
    private Map<Object, ChildType> createModifiableMap() {
        return new TreeMap<>();
    }

    /**
     * Performs an operation on each child.
     *
     * @param operation An operation to perform on each child
     */
    private void doOperation(@NotNull Operation operation) {

        // Cycle for each child, and perform the indicated operation.
        for (ChildType child : getChildren()) {
            operation.perform(child);
        }
    }

    @Override
    public Collection<ChildType> getChildren() {

        // Lock the children, and return the child collection.
        lockChildren();
        return collection;
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

    @Override
    public Currency getConsidered() {
        return (null == considered) ? null : new Currency(considered);
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

    @Override
    public Currency getNotConsidered() {
        return (null == notConsidered) ? null : new Currency(notConsidered);
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

    @Override
    public Currency getProposed() {
        return getConsidered();
    }

    /**
     * Gets the breakdown manager for the tax type.
     *
     * @return The breakdown manager for the tax type
     */
    protected @NotNull BreakdownManager<TaxType> getTaxTypeManager() {
        return taxTypeManager;
    }

    /**
     * Determines whether the aggregate has children.
     *
     * @return True if the aggregate has children, false otherwise
     */
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    /**
     * Determines whether value has been set in the aggregate.
     *
     * @return True if value has been set in the aggregate, false otherwise
     */
    public boolean hasValueBeenSet() {
        return (null != getConsidered()) || (null != getNotConsidered());
    }

    /**
     * Locks the children of this common hierarchy object.
     */
    void lockChildren() {

        // Only lock the children if they have not already been locked.
        if (null == collection) {

            /*
             * The children have not already been locked. Replace the map with
             * an unmodifiable map, and create a collection of children from
             * the values of the map.
             */
            map = Collections.unmodifiableMap(map);
            collection =
                    Collections.unmodifiableCollection(map.values());
        }
    }

    @Override
    void setConsidered(double value) {

        /*
         * Set a new 'considered' object with the given value if the current
         * considered object is null...
         */
        if (null == getConsidered()) {
            considered = new MutableCurrency(value);
        }

        // ... otherwise just set the value in the existing object.
        else {
            considered.set(value);
        }
    }

    /**
     * Sets the breakdown managers to work with current values.
     */
    void setCurrent() {

        /*
         * Do the operation for each child. Set the tax type manager to work
         * with current values, then call the superclass method.
         */
        doOperation(setCurrent);
        getTaxTypeManager().setCurrent();
        super.setCurrent();
    }

    @Override
    void setNotConsidered(double value) {

        /*
         * Set a new 'not considered' object with the given value if the
         * current considered object is null...
         */
        if (null == getNotConsidered()) {
            notConsidered = new MutableCurrency(value);
        }

        // ... otherwise just set the value in the existing object.
        else {
            notConsidered.set(value);
        }
    }

    /**
     * Sets the breakdown managers to work with proposed values.
     */
    void setProposed() {

        /*
         * Do the operation for each child. Set the tax type manager to work
         * with proposed values, then call the superclass method.
         */
        doOperation(setProposed);
        getTaxTypeManager().setProposed();
        super.setProposed();
    }

    /**
     * Synthesizes the aggregate if conditions warrant.
     *
     * @return False if conditions warranted synthesis and it could not be
     * performed; true otherwise
     */
    boolean synthesizeIf() {

        // The default is that conditions do not warrant synthesis.
        return true;
    }

    /**
     * Unlocks the children of this common hierarchy object.
     */
    void unlockChildren() {

        // Only unlock the children if they have not already been unlocked.
        if (null != collection) {

            /*
             * The children have not already been unlocked. Clear the child
             * collection, and create a new, empty child map.
             */
            collection = null;
            map = createModifiableMap();
        }
    }

    private abstract class Operation {

        /**
         * Performs an operation on a child.
         *
         * @param child The child on which to perform the operation
         */
        public abstract void perform(@NotNull ChildType child);
    }
}
