package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface CurrencyReceiver {

    /**
     * Clears a snapshot.
     *
     * @param key The key of the snapshot to clear
     */
    void clearSnapshot(@NotNull SnapshotKey key);

    /**
     * Gets the current value of the receiver.
     *
     * @return The current value of the receiver
     */
    @NotNull Currency getCurrent();

    /**
     * Determines if the receiver has no snapshots.
     *
     * @return True if the receiver has no snapshots; false if it has one or
     * more
     */
    boolean hasNoSnapshots();

    /**
     * Recovers a snapshot.
     *
     * @param key The key of the snapshot to recover
     */
    void recoverSnapshot(@NotNull SnapshotKey key);

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
     * @param key The key to be associated with the snapshot
     */
    void takeSnapshot(@NotNull SnapshotKey key);
}
