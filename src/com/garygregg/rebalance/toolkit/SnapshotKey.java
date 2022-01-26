package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SnapshotKey implements Comparable<SnapshotKey> {

    // The internal key
    private final int key;

    /**
     * Creates a snapshot key.
     *
     * @param key The internal key
     */
    SnapshotKey(int key) {
        this.key = key;
    }

    @Override
    public int compareTo(@NotNull SnapshotKey snapshotKey) {
        return key - snapshotKey.key;
    }

    @Override
    public boolean equals(@NotNull Object object) {

        // Declare the result. Is the given object the same as this one?
        boolean result;
        if (this == object) {

            // The given object is the same as this one.
            result = true;
        }

        /*
         * The given object is not the same as this one. Is the given object
         * not a snapshot key?
         */
        else if (!(object instanceof SnapshotKey)) {

            // The given object is not a snapshot key.
            result = false;
        }

        // The given object *is* a snapshot key, just not this one.
        else {

            /*
             * Cast the given snapshot key, and compare its internal key to
             * ours.
             */
            final SnapshotKey that = (SnapshotKey) object;
            result = (0 == compareTo(that));
        }

        // Return the result.
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
