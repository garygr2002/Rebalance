package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.CategoryType;
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

    // The 'considered' valuator
    private final Valuator byConsidered = ValueByConsidered.getInstance();

    // The 'not considered' valuator
    private final Valuator byNotConsidered =
            ValueByNotConsidered.getInstance();

    // The 'proposed' valuator
    private final Valuator byProposed = ValueByProposed.getInstance();

    // Breaks down values in each child
    private final Operation doBreakdown = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.breakdown();
        }
    };

    // Clears values in each child
    private final Operation doClear = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {
            child.clear();
        }
    };

    // Our breakdown manager for the weight type
    private final WeightBreakdownManager<ChildType> weightTypeManager =
            new WeightBreakdownManager<>();

    // Accumulates values by weight type in each child
    private final Operation weightTypeAccumulate = new Operation() {

        @Override
        public void perform(@NotNull ChildType child) {

            final WeightBreakdownManager<ChildType> weightTypeManager =
                    getWeightTypeManager();

            for (WeightType type : WeightType.values()) {
                weightTypeManager.add(type, child);
            }
        }
    };

    // A known zero that we will use repeatedly
    private final Currency zero = Currency.getZero();

    // The cached artificial child
    private ChildType artificialChild;

    // A collection of child hierarchy objects
    private Collection<ChildType> collection;

    // The 'considered' value of the hierarchy object
    private MutableCurrency considered;

    // A map of child keys to child hierarchy objects
    private Map<Object, ChildType> map = createModifiableMap();

    // The 'not considered' value of the hierarchy object
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

        // Break down child values, then accumulate values by weight type.
        breakdownChildren();
        accumulateWeightType();
    }

    /**
     * Breaks down values in each child.
     */
    protected void breakdownChildren() {
        doOperation(doBreakdown);
    }

    @Override
    void clear() {

        // Clear children, then clear the weight type manager.
        clearChildren();
        getWeightTypeManager().clear();
    }

    /**
     * Clears values in each child.
     */
    protected void clearChildren() {
        doOperation(doClear);
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
    protected void doOperation(@NotNull Operation operation) {

        // Get the collection of children. Is the collection empty?
        final Collection<ChildType> children = getChildren();
        if (children.isEmpty()) {

            /*
             * The collection is empty. Get the cached, artificial child,
             * transfer value from this queryable to the artificial child,
             * and perform the indicated operation on the artificial child.
             */
            final ChildType artificialChild = getArtificialChild();
            artificialChild.transferValue(this);
            operation.perform(artificialChild);
        }

        // There are one or more children.
        else {

            // Cycle for each child, and perform the indicated operation.
            for (ChildType child : getChildren()) {
                operation.perform(child);
            }
        }
    }

    /**
     * Gets an artificial child.
     *
     * @return An artificial child
     */
    @NotNull ChildType getArtificialChild() {

        /*
         * Create a new artificial child and cache it if the current artificial
         * child is null. We only need one, forever.
         */
        if (null == artificialChild) {
            artificialChild = getNewArtificialChild();
        }

        // Return the cached artificial child.
        return artificialChild;
    }

    @Override
    public @NotNull Collection<ChildType> getChildren() {

        // Lock the children, and return the child collection.
        lockChildren();
        return collection;
    }

    @Override
    public Currency getConsidered() {
        return (null == considered) ? null : considered.getImmutable();
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
        return CategoryType.ALL.equals(type) || hasCategoryType(type) ?
                getValue(byConsidered) : zero;
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
        return TaxType.ALL.equals(type) || hasTaxType(type) ?
                getValue(byConsidered) : zero;
    }

    @Override
    public @NotNull Currency getConsidered(@NotNull WeightType type) {
        return getWeightTypeManager().getConsidered(type);
    }

    /***
     * Gets a new artificial child; only use this if you want a new artificial
     * child every time, otherwise use <code>getCachedArtificialChild</code>.
     * @return A new artificial child
     */
    protected abstract @NotNull ChildType getNewArtificialChild();

    @Override
    public Currency getNotConsidered() {
        return (null == notConsidered) ? null : notConsidered.getImmutable();
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
        return CategoryType.ALL.equals(type) || hasCategoryType(type) ?
                getValue(byNotConsidered) : zero;
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
        return TaxType.ALL.equals(type) || hasTaxType(type) ?
                getValue(byNotConsidered) : zero;
    }

    @Override
    public @NotNull Currency getNotConsidered(@NotNull WeightType type) {
        return getWeightTypeManager().getNotConsidered(type);
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
        return CategoryType.ALL.equals(type) || hasCategoryType(type) ?
                getValue(byProposed) : zero;
    }

    @Override
    public Currency getProposed() {
        return getConsidered();
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
        return TaxType.ALL.equals(type) || hasTaxType(type) ?
                getValue(byProposed) : zero;
    }

    @Override
    public @NotNull Currency getProposed(@NotNull WeightType type) {
        return getWeightTypeManager().getProposed(type);
    }

    /**
     * Gets the value of this aggregate using a given valuator.
     *
     * @param valuator The given valuator
     * @return The value of this aggregate
     */
    private @NotNull Currency getValue(@NotNull Valuator valuator) {

        /*
         * Get the value of this aggregate. Return zero if the value is null,
         * otherwise return the value itself.
         */
        final Currency currency = valuator.getValue(this);
        return (null == currency) ? zero : currency;
    }

    /**
     * Gets the breakdown manager for the weight type.
     *
     * @return The breakdown manager for the weight type
     */
    protected @NotNull WeightBreakdownManager<ChildType>
    getWeightTypeManager() {
        return weightTypeManager;
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

    @Override
    void setCurrent() {
        getWeightTypeManager().setCurrent();
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

    @Override
    void setProposed() {
        getWeightTypeManager().setProposed();
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

    protected abstract class Operation {

        /**
         * Performs an operation on a child.
         *
         * @param child The child on which to perform the operation
         */
        public abstract void perform(@NotNull ChildType child);
    }
}
