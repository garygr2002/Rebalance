package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

abstract class Action<ParentType, ChildType> {

    // The parent on which 'getChildren' was most recently called
    private ParentType parent;

    /**
     * Gets the children of the parent.
     *
     * @param parent The parent
     * @return The children of the parent
     */
    protected abstract @NotNull Collection<ChildType> doGetChildren(
            @NotNull ParentType parent);

    /**
     * Gets the children of the parent.
     *
     * @param parent The parent
     * @return The children of the parent
     */
    public final @NotNull Collection<ChildType> getChildren(
            @NotNull ParentType parent) {

        // Set the parent, then called the overridden method.
        this.parent = parent;
        return doGetChildren(getParent());
    }

    /**
     * Gets the parent on which 'getChildren' was most recently called.
     *
     * @return The parent on which 'getChildren' was most recently called
     */
    public ParentType getParent() {
        return parent;
    }

    /**
     * Receives notification that the action has been performed on each child.
     */
    public void onComplete() {

        // The default is to do nothing.
    }

    /**
     * Performs an action on a child.
     *
     * @param child  The child
     * @param isLast True if this is the last child; false otherwise
     * @return True if the action was successful; false otherwise
     */
    public abstract boolean perform(@NotNull ChildType child, boolean isLast);
}
