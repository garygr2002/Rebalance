package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

class Rebalancer {

    /**
     * Performs an action on the children of a parent.
     *
     * @param parent       The parent
     * @param action       The action to perform
     * @param <ParentType> The type of the parent
     * @param <ChildType>  The type of the child
     * @return True if the action succeeded; false otherwise
     */
    protected static <ParentType, ChildType> boolean perform(
            @NotNull ParentType parent,
            @NotNull Action<ParentType, ChildType> action) {

        /*
         * Declare and initialize the result. Declare a variable to hold a
         * child.
         */
        boolean result = true;
        ChildType child;

        // Get an iterator for the children. Is there at least one child?
        final Iterator<ChildType> iterator =
                action.getChildren(parent).iterator();
        if (iterator.hasNext()) {

            /*
             * There is at least one child. Get the first child and cycle
             * while additional children exist.
             */
            child = iterator.next();
            while (iterator.hasNext()) {

                /*
                 * Perform the action on the current, non-last child, and get
                 * the next child.
                 */
                result = action.perform(child, false) && result;
                child = iterator.next();
            }

            // Perform the action on the last child.
            result = action.perform(child, true) && result;
        }

        // Return the result.
        return result;
    }
}
