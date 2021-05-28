package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class BreakdownManager<T extends Enum<T>> implements IBreakdown<T> {

    // Our operation to add value to a breakdown
    private final AddOperation addOperation = new AddOperation();

    // Our breakdown list for current values
    private final List<Breakdown<T>> breakdownCurrent;

    // Our list of breakdown lists
    private final List<List<Breakdown<T>>> breakdownLists;

    // Our breakdown list for proposed values
    private final List<Breakdown<T>> breakdownProposed;

    // Our operation to clear a breakdown
    private final Operation<T> clearOperation = Breakdown::clear;

    // Our breakdown for 'considered' value
    private final Breakdown<T> considered;

    // Our breakdown for 'not considered' value
    private final Breakdown<T> notConsidered;

    // Our breakdown for proposed values
    private final Breakdown<T> proposed;

    // The index of the active breakdown list
    private int index = getBreakdownIndex(BreakdownType.CURRENT);

    /**
     * Constructs a breakdown manager with default valuators.
     */
    public BreakdownManager() {
        this(new ValueByConsidered(),
                new ValueByNotConsidered(),
                new ValueByProposed());
    }

    /**
     * Constructs a breakdown manager.
     *
     * @param consideredValuator    The valuator for 'considered' values
     * @param notConsideredValuator The valuator for 'not considered' values
     * @param proposedValuator      The valuator for proposed values
     */
    public BreakdownManager(@NotNull Valuator consideredValuator,
                            @NotNull Valuator notConsideredValuator,
                            @NotNull Valuator proposedValuator) {

        // Create breakdowns for each valuator.
        considered = new Breakdown<>(consideredValuator);
        notConsidered = new Breakdown<>(notConsideredValuator);
        proposed = new Breakdown<>(proposedValuator);

        // Create the lists.
        breakdownCurrent = List.of(considered, notConsidered);
        breakdownProposed = List.of(proposed);
        breakdownLists = List.of(breakdownCurrent, breakdownProposed);
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
    public void add(T type, @NotNull Queryable<?, ?> queryable) {

        /*
         * Set the queryable and type in the 'add' operation, and perform the
         * operation for each active breakdown.
         */
        addOperation.setQueryable(queryable);
        addOperation.setType(type);
        doOperation(addOperation);
    }

    @Override
    public void clear() {
        doOperation(clearOperation);
    }

    /**
     * Perform an operation for each active breakdown.
     *
     * @param operation The operation to perform
     */
    private void doOperation(@NotNull Operation<T> operation) {

        /*
         * Get the list of active breakdowns. Cycle for each breakdown in the
         * list.
         */
        final List<Breakdown<T>> breakdownList = breakdownLists.get(index);
        for (Breakdown<T> breakdown : breakdownList) {

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
    public @NotNull Currency getConsidered(T type) {
        return considered.get(type);
    }

    /**
     * Gets the 'not considered' value for the given type.
     *
     * @param type A type
     * @return The 'considered' value for the given type
     */
    public @NotNull Currency getNotConsidered(T type) {
        return notConsidered.get(type);
    }

    /**
     * Gets the proposed value for the given type.
     *
     * @param type A type
     * @return The 'considered' value for the given type
     */
    public @NotNull Currency getProposed(T type) {
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

    private interface Operation<S extends Enum<S>> {

        /**
         * Performs an operation on a breakdown.
         *
         * @param breakdown The breakdown on which to perform the operation
         */
        void perform(@NotNull Breakdown<S> breakdown);
    }

    private class AddOperation implements Operation<T> {

        // A queryable
        private Queryable<?, ?> queryable;

        // A type value
        private T type;

        @Override
        public void perform(@NotNull Breakdown<T> breakdown) {

            // Perform the operation if the queryable is not null.
            if (null != queryable) {
                breakdown.add(type, queryable);
            }
        }

        /**
         * Sets the queryable.
         *
         * @param queryable The queryable to set
         */
        public void setQueryable(Queryable<?, ?> queryable) {
            this.queryable = queryable;
        }

        /**
         * Sets the type value.
         *
         * @param type The type value to set
         */
        public void setType(T type) {
            this.type = type;
        }
    }
}
