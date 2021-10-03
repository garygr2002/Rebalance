package com.garygregg.rebalance.qualifier;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Conjunction<T> extends Condition<T> {

    // A list of nested conditions
    private final List<Condition<T>> conditions = new ArrayList<>();

    // The operation for the 'isSatisfied' method
    private final Operation forIsSatisfied = new Operation() {

        @Override
        public boolean perform(Condition<T> condition, T predicate) {
            return condition.isSatisfied();
        }
    };

    // True if this is an 'or' conjunction, false otherwise
    private final boolean or;

    // The operation for the 'clear' method
    private final Operation forClear = new Operation() {

        @Override
        public boolean perform(Condition<T> condition, T predicate) {

            // Clear the condition, and return 'isAnd()' to keep going.
            condition.clear();
            return isAnd();
        }
    };

    // The operation for the 'isQualified' method
    private final Operation forIsQualified = new Operation() {

        @Override
        public boolean perform(Condition<T> condition, T predicate) {

            /*
             * Ask if the predicate is qualified, and 'isAnd()' to keep
             * going. Note: The return value is not meaningful to determine
             * qualification of the predicate for a conjunction.
             */
            condition.isQualified(predicate);
            return isAnd();
        }
    };

    // The operation for the 'qualify' method
    private final Operation forQualify = new Operation() {

        @Override
        public boolean perform(Condition<T> condition, T predicate) {

            // Qualify the predicate, and return 'isAnd()' to keep going.
            condition.qualify(predicate);
            return isAnd();
        }
    };

    // The operation for the 'remove' method
    private final Operation forRemove = new Operation() {

        @Override
        public boolean perform(Condition<T> condition, T predicate) {

            // Remove the predicate, and return 'isAnd()' to keep going.
            condition.remove(predicate);
            return isAnd();
        }
    };

    /**
     * Constructs the conjunction.
     *
     * @param or True if this is an 'or' conjunction, false for an 'and'
     *           conjunction
     */
    protected Conjunction(boolean or) {
        this.or = or;
    }

    /**
     * Adds a condition to the conjunction.
     *
     * @param condition The condition to add
     * @return True if the condition was not already present in the conjunction
     */
    public boolean add(Condition<T> condition) {
        return conditions.add(condition);
    }

    @Override
    public void clear() {
        forClear.doForAll(null);
    }

    /**
     * Is this an 'and' conjunction?
     *
     * @return True if this is an 'and' conjunction, false if an 'or' conjunction
     */
    public boolean isAnd() {
        return !isOr();
    }

    /**
     * Is this an 'or' conjunction?
     *
     * @return True if this is an 'or' conjunction, false if an 'and' conjunction
     */
    public boolean isOr() {
        return or;
    }

    @Override
    protected boolean isQualified(@NotNull T candidate) {
        return forIsQualified.doForAll(candidate);
    }

    @Override
    public boolean isSatisfied() {
        return forIsSatisfied.doForAll(null);
    }

    @Override
    public void qualify(@NotNull T candidate) {
        forQualify.doForAll(candidate);
    }

    @Override
    public void remove(@NotNull T qualifier) {
        forRemove.doForAll(qualifier);
    }

    /**
     * Removes a condition from the conjunction.
     *
     * @param condition The condition to remove
     * @return True if the condition was present, false otherwise
     */
    public boolean remove(Condition<T> condition) {
        return conditions.remove(condition);
    }

    /**
     * Performs operations on each contained condition.
     */
    private abstract class Operation {

        /**
         * Performs an operation for each contained condition.
         *
         * @param predicate The predicate for the operation
         * @return True if a return value from the 'perform' method differs
         * from that of the 'isOr' method
         */
        public boolean doForAll(T predicate) {

            /*
             * We initialize the operation result to the negation of the 'isOr'
             * method.
             */
            final boolean orConjunction = isOr();
            boolean operationResult = !orConjunction;

            /*
             * Iterate over the conditions while the operation result differs
             * from the result of the 'isOr' method, and while conditions
             * exist.
             */
            final Iterator<Condition<T>> iterator = conditions.iterator();
            while ((operationResult ^ orConjunction) && iterator.hasNext()) {

                // Perform the first/next operation, and receive its result.
                operationResult = perform(iterator.next(), predicate);
            }

            // Return the last performed operation result.
            return operationResult;
        }

        /**
         * Performs an operation a predicate.
         *
         * @param condition The condition to receive the predicate
         * @param predicate The predicate to pass to the condition
         * @return Status of the operation
         */
        public abstract boolean perform(Condition<T> condition, T predicate);
    }
}
