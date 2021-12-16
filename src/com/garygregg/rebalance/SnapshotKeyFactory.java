package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SnapshotKeyFactory {

    // The largest prime less than 2^(Integer.SIZE - 1)
    private static final long bigPrime = 2147483647L;

    // A modulus for clearing bits beyond Integer.SIZE - 1
    private static final long mod = (1L << Integer.SIZE);

    // The current internal key
    private int key;

    /**
     * Creates a snapshot key factory.
     *
     * @param key The internal key
     */
    public SnapshotKeyFactory(int key) {
        this.key = key;
    }

    /**
     * Creates a snapshot key factory.
     *
     * @param generator A generator for internal keys
     */
    public SnapshotKeyFactory(@NotNull Random generator) {
        this(generator.nextInt());
    }

    /**
     * Creates a snapshot key factory.
     */
    public SnapshotKeyFactory() {
        this(new Random());
    }

    /**
     * Produces a new snapshot key.
     *
     * @return A new snapshot key
     */
    public @NotNull SnapshotKey produce() {
        return new SnapshotKey(key = ((int) ((Integer.toUnsignedLong(key) +
                bigPrime) % mod)));
    }
}
