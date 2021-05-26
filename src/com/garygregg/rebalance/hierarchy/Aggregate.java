package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
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

    /**
     * Creates a modifiable child map.
     *
     * @return A modifiable child map
     */
    private Map<Object, ChildType> createModifiableMap() {
        return new TreeMap<>();
    }

    @Override
    public Collection<ChildType> getChildren() {

        // Lock the children, and return the child collection.
        lockChildren();
        return collection;
    }

    @Override
    public Currency getConsidered() {
        return (null == considered) ? null : new Currency(considered);
    }

    @Override
    public Currency getNotConsidered() {
        return (null == notConsidered) ? null : new Currency(notConsidered);
    }

    @Override
    public Currency getProposed() {
        return getConsidered();
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
}
