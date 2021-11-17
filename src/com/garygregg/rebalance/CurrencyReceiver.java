package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface CurrencyReceiver {

    /**
     * Clears the last snapshot.
     *
     * @param type The snapshot type to clear
     */
    void clearSnapshot(@NotNull SnapshotType type);

    /**
     * Clears snapshots of all types.
     */
    void clearSnapshots();

    /**
     * Recovers a snapshot.
     *
     * @param type The snapshot type to recover
     */
    void recoverSnapshot(@NotNull SnapshotType type);

    /**
     * Sets the proposed value of the receiver.
     *
     * @param currency   A value to adjust the proposed value of the receiver
     * @param isRelative True if the incoming value is relative to the value
     *                   already set in the receiver; false if it is absolute
     * @return The difference between what was desired and what was actually
     * set (the 'residual'); this will be positive if a receiver took too
     * little, negative if it took too much
     */
    @NotNull Currency setProposed(@NotNull Currency currency,
                                  boolean isRelative);

    /**
     * Takes a snapshot.
     *
     * @param type The snapshot type to take
     */
    void takeSnapshot(@NotNull SnapshotType type);

    public enum SnapshotType {

        // The optimal rebalance snapshot
        BEST,

        // The first rebalance snapshot
        FIRST
    }
}
