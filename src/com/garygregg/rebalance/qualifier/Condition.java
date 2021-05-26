package com.garygregg.rebalance.qualifier;

import org.jetbrains.annotations.NotNull;

public abstract class Condition<T> {

    /**
     * Clears the satisfaction set.
     */
    public abstract void clear();

    /**
     * Determines if a candidate satisfies a condition.
     *
     * @param candidate The candidate used to determine satisfaction
     * @return True if the candidate was added to the satisfaction set, false
     * otherwise
     */
    protected abstract boolean isQualified(@NotNull T candidate);

    /**
     * Returns true if the condition has a non-empty satisfaction set.
     *
     * @return True if the condition has non-empty satisfaction set, false
     * otherwise
     */
    public abstract boolean isSatisfied();

    /**
     * Adds a candidate to the satisfaction set if it qualifies.
     *
     * @param candidate The candidate to qualify
     */
    public abstract void qualify(@NotNull T candidate);

    /**
     * Removes a qualifier from the satisfaction set.
     *
     * @param qualifier The qualifier to remove from the satisfaction set
     */
    public abstract void remove(@NotNull T qualifier);
}
