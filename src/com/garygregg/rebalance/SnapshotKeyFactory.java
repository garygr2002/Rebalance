package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SnapshotKeyFactory {

    // A list of big primes
    private static final List<Long> bigPrimes =
            List.of(4294967291L, 4294967279L);

    // Big prime one
    private static final long bigPrime1 = bigPrimes.get(0);

    // Big prime two
    private static final long bigPrime2 = bigPrimes.get(1);

    // A mask to clear the high-end bits in a long integer
    private static final long mask = (1L << Integer.SIZE) - 1;

    // The internal key
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

        /*
         * Calculate the new internal key and return a new snapshot key with
         * it.
         */
        final long newKey = key * bigPrime1 + bigPrime2;
        key = (int) (newKey & mask);
        return new SnapshotKey(key);
    }
}
