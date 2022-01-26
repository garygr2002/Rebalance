package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.toolkit.SnapshotKey;
import org.jetbrains.annotations.NotNull;

abstract class SnapshotAction
        extends ContainerNodeAction<ReceiverDelegate<?>, SnapshotKey> {

    // The initial snapshot type
    private final SnapshotKey initialKey;

    /**
     * Constructs the snapshot action.
     *
     * @param initialKey The initial snapshot key for the action
     */
    public SnapshotAction(@NotNull SnapshotKey initialKey) {
        this.initialKey = initialKey;
    }

    @Override
    protected @NotNull SnapshotKey getInitialValue() {
        return initialKey;
    }

    @Override
    public void reset() {
        setContained(initialKey);
    }

    /**
     * Sets the snapshot key for the action.
     *
     * @param key The snapshot key for the action
     */
    public void setKey(@NotNull SnapshotKey key) {
        setContained(key);
    }
}
