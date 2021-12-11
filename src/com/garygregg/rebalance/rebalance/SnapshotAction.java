package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.CurrencyReceiver.SnapshotType;
import org.jetbrains.annotations.NotNull;

abstract class SnapshotAction
        extends ContainerNodeAction<ReceiverDelegate<?>, SnapshotType> {

    // The initial snapshot type
    private static final SnapshotType initialType = SnapshotType.FIRST;

    @Override
    protected @NotNull SnapshotType getInitialValue() {
        return SnapshotType.FIRST;
    }

    @Override
    public void reset() {
        setContained(initialType);
    }

    /**
     * Sets the snapshot type for the action.
     *
     * @param type The snapshot type for the action
     */
    public void setType(@NotNull SnapshotType type) {
        setContained(type);
    }
}
