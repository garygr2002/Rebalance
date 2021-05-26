package com.garygregg.rebalance.qualifier;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public abstract class Qualifier<T> extends Condition<T> {

    // The qualifier satisfaction set
    private final Set<T> qualifiers = new HashSet<>();

    @Override
    public void clear() {
        qualifiers.clear();
    }

    @Override
    protected abstract boolean isQualified(@NotNull T candidate);

    @Override
    public boolean isSatisfied() {
        return !qualifiers.isEmpty();
    }

    @Override
    public void qualify(@NotNull T candidate) {

        // Only add the candidate if it qualifies.
        if (isQualified(candidate)) {
            qualifiers.add(candidate);
        }
    }

    @Override
    public final void remove(@NotNull T qualifier) {
        qualifiers.remove(qualifier);
    }
}
