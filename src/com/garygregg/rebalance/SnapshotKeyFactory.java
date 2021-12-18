package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SnapshotKeyFactory {

    /*
     * The two largest prime numbers that are less than or equal to
     * 2^(Integer.SIZE - 1), or Integer.MAX_VALUE
     */
    private static final List<Long> bigPrimes = List.of(2147483647L, 2147483629L);

    // The value to add to the current key
    private static final long addend = bigPrimes.get(0);

    // A modulus for clearing bits beyond Integer.SIZE - 1
    private static final long mod = (1L << Integer.SIZE);

    // The value to multiply to the sum of the current key and the addend
    private static final long multiplicand = bigPrimes.get(1);

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
        return new SnapshotKey(key = ((int) (((Integer.toUnsignedLong(key) +
                addend) * multiplicand) % mod)));
    }
}
