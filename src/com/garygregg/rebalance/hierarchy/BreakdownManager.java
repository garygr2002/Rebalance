package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

abstract class BreakdownManager<EnumType extends Enum<EnumType>,
        HierarchyType extends Common<?, ?, ?>>
        implements IBreakdown<EnumType, HierarchyType> {

    // Our operation to add value to a breakdown
    private final AddOperation addOperation = new AddOperation();

    // Our list of breakdown lists
    private final List<List<IBreakdown<EnumType, HierarchyType>>>
            breakdownLists = new ArrayList<>();

    // Our operation to clear a breakdown
    private final Operation<EnumType, HierarchyType> clearOperation =
            IBreakdown::clear;

    // A breakdown for 'considered' values
    private final Breakdown<EnumType, HierarchyType> considered;

    // A breakdown for 'not considered' values
    private final Breakdown<EnumType, HierarchyType> notConsidered;

    // A breakdown for proposed values
    private final Breakdown<EnumType, HierarchyType> proposed;

    // The index of the active breakdown list
    private int index = getBreakdownIndex(BreakdownType.CURRENT);

    {

        /*
         * Create a list to receive current values, and add the 'considered'
         * values breakdown.
         */
        List<IBreakdown<EnumType, HierarchyType>> list = new ArrayList<>();
        list.add(considered = createByConsidered());

        /*
         * Add the 'not considered' values breakdown to the list, and add the
         * list to the breakdown lists for the current breakdown type.
         */
        list.add(notConsidered = createByNotConsidered());
        breakdownLists.add(getBreakdownIndex(BreakdownType.CURRENT), list);

        /*
         * Create a new list to receive the proposed values, and the proposed
         * breakdown of values. Add the list to the breakdown lists for the
         * proposed breakdown type.
         */
        list = new ArrayList<>();
        list.add(proposed = createByProposed());
        breakdownLists.add(getBreakdownIndex(BreakdownType.PROPOSED), list);
    }

    /**
     * Gets the breakdown index for a breakdown type.
     *
     * @param type The given breakdown type
     * @return The breakdown index associated with the breakdown type
     */
    private static int getBreakdownIndex(@NotNull BreakdownType type) {
        return type.ordinal();
    }

    @Override
    public void add(EnumType type, @NotNull HierarchyType hierarchyObject) {

        /*
         * Set the hierarchy object and type in the 'add' operation, and perform
         * the operation for each active breakdown.
         */
        addOperation.setHierarchyObject(hierarchyObject);
        addOperation.setType(type);
        doOperation(addOperation);
    }

    @Override
    public void clear() {
        doOperation(clearOperation);
    }

    /**
     * Creates the breakdown for 'considered' values.
     *
     * @return The breakdown for 'considered' values
     */
    protected abstract @NotNull Breakdown<EnumType, HierarchyType> createByConsidered();

    /**
     * Creates the breakdown for 'not considered' values.
     *
     * @return The breakdown for 'not considered' values
     */
    protected abstract @NotNull Breakdown<EnumType, HierarchyType> createByNotConsidered();

    /**
     * Creates the breakdown for proposed values.
     *
     * @return The breakdown for proposed values
     */
    protected abstract @NotNull Breakdown<EnumType, HierarchyType> createByProposed();

    /**
     * Perform an operation for each active breakdown.
     *
     * @param operation The operation to perform
     */
    private void doOperation(@NotNull Operation<EnumType,
            @NotNull HierarchyType> operation) {

        /*
         * Get the list of active breakdowns. Cycle for each breakdown in the
         * list.
         */
        final List<IBreakdown<EnumType, HierarchyType>> breakdownList =
                breakdownLists.get(index);
        for (IBreakdown<EnumType, HierarchyType> breakdown : breakdownList) {

            // Perform the indicated operation on the first/next breakdown.
            operation.perform(breakdown);
        }
    }

    /**
     * Gets the 'considered' value for the given type.
     *
     * @param type A type
     * @return The 'considered' value for the given type
     */
    public @NotNull Currency getConsidered(EnumType type) {
        return considered.get(type);
    }

    /**
     * Gets the 'not considered' value for the given type.
     *
     * @param type A type
     * @return The 'considered' value for the given type
     */
    public @NotNull Currency getNotConsidered(EnumType type) {
        return notConsidered.get(type);
    }

    /**
     * Gets the proposed value for the given type.
     *
     * @param type A type
     * @return The 'considered' value for the given type
     */
    public @NotNull Currency getProposed(EnumType type) {
        return proposed.get(type);
    }

    /**
     * Sets the current breakdown list as active.
     */
    public void setCurrent() {
        index = getBreakdownIndex(BreakdownType.CURRENT);
    }

    /**
     * Sets the proposed breakdown list as active.
     */
    public void setProposed() {
        index = getBreakdownIndex(BreakdownType.PROPOSED);
    }

    private enum BreakdownType {

        // The current values
        CURRENT,

        // The proposed values
        PROPOSED
    }

    private interface Operation<S extends Enum<S>, T extends Common<?, ?, ?>> {

        /**
         * Performs an operation on a breakdown.
         *
         * @param breakdown The breakdown on which to perform the operation
         */
        void perform(@NotNull IBreakdown<S, T> breakdown);
    }

    private class AddOperation implements Operation<EnumType, HierarchyType> {

        // A hierarchy object
        private HierarchyType hierarchyObject;

        // An enumerated type value
        private EnumType type;

        @Override
        public void perform(@NotNull IBreakdown<EnumType, HierarchyType> breakdown) {

            // Perform the operation if the hierarchy object is not null.
            if (null != hierarchyObject) {
                breakdown.add(type, hierarchyObject);
            }
        }

        /**
         * Sets the hierarchy object.
         *
         * @param hierarchyObject The hierarchy object to set
         */
        public void setHierarchyObject(HierarchyType hierarchyObject) {
            this.hierarchyObject = hierarchyObject;
        }

        /**
         * Sets the type value.
         *
         * @param type The type value to set
         */
        public void setType(EnumType type) {
            this.type = type;
        }
    }
}
