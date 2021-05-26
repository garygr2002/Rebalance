package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

public class HoldingKey extends Pair<String, String> implements
        Comparable<HoldingKey> {

    /**
     * Constructs a holding key from its constituents.
     *
     * @param parent The parent of the holder
     * @param holder The holding of the holding
     */
    public HoldingKey(String parent, String holder) {
        super(parent, holder);
    }

    /**
     * Constructs a holding key from a pair of strings.
     *
     * @param pair A pair of strings
     */
    public HoldingKey(@NotNull Pair<String, String> pair) {
        super(pair.getFirst(), pair.getSecond());
    }

    @Override
    public int compareTo(@NotNull HoldingKey holdingKey) {
        return compare((Pair<String, String>) this, holdingKey);
    }
}
