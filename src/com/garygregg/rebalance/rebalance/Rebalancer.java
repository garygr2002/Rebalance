package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

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

        // Declare and initialize the result. Cycle for each child.
        boolean result = true;
        for (ChildType child : action.getChildren(parent)) {

            // Perform the action and re-initialize the result.
            result = action.perform(child) && result;
        }

        // Return the result.
        return result;
    }
}
