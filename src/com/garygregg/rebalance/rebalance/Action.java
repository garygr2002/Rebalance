package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

interface Action<ParentType, ChildType> {

    /**
     * Gets the children of the parent.
     *
     * @param parent The parent
     * @return The children of the parent
     */
    @NotNull Collection<ChildType> getChildren(@NotNull ParentType parent);

    /**
     * Performs an action on a child.
     *
     * @param child The child
     * @return True if the action was successful; false otherwise
     */
    boolean perform(@NotNull ChildType child);
}
