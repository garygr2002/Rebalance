package com.garygregg.rebalance.query;

import com.garygregg.rebalance.hierarchy.Queryable;
import com.garygregg.rebalance.qualifier.Condition;
import org.jetbrains.annotations.NotNull;

public abstract class Query<T> {

    // The accumulator for this query
    private final Accumulator<T> accumulator;

    // The initial value for the accumulator
    private final T initial;

    /**
     * Constructs the query.
     *
     * @param accumulator The accumulator for this query
     * @param initial     The initial value for the accumulator
     */
    public Query(@NotNull Accumulator<T> accumulator, @NotNull T initial) {

        // Set the member variables.
        this.accumulator = accumulator;
        this.initial = initial;
    }

    /**
     * Performs the query.
     *
     * @param queryable  A queryable
     * @param condition A condition that must be satisfied
     * @return The result of the query
     */
    public T perform(Queryable<?, ?> queryable,
                     Condition<Queryable<?, ?>> condition) {

        /*
         * Initialize the result, and qualify the queryable. Is the condition
         * now satisfied?
         */
        T result = initial;
        condition.qualify(queryable);
        if (condition.isSatisfied()) {

            /*
             * The condition is satisfied. Accumulate the queryable in its
             * entirety.
             */
            accumulator.accumulate(result, queryable);
        } else {

            /*
             * The condition is not (yet) satisfied. Get an iterator for the
             * nested queryables, and cycle while nested queryables exist.
             */
//            Iterator<QueryableOld<?>> iterator = queryable.getNested();
//            while (iterator.hasNext()) {
//
//                /*
//                 * Perform the query on the first/next queryable. Transform
//                 * the result, and accumulate it.
//                 */
//                accumulator.accumulate(result,
//                        accumulator.transform(
//                                perform(iterator.next(), condition)));
//            }
        }

        // Remove the current queryable as a qualifier, and return the result.
        condition.remove(queryable);
        return result;
    }
}
