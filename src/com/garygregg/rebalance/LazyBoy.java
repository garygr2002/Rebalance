package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

public class LazyBoy<T> {

    // A factory for producing objects of our type
    private final Factory<T> factory;

    // A cached object produced by the factory
    private T object;

    /**
     * Constructs the lazy boy.
     *
     * @param factory A factory for producing objects of our type
     */
    public LazyBoy(@NotNull Factory<T> factory) {

        // Set the factory, and clear this lazy boy.
        this.factory = factory;
        clear();
    }

    /**
     * Clears the lazy boy.
     */
    public void clear() {
        object = null;
    }

    /**
     * Gets the cached object, ensuring that it is not null.
     *
     * @return The non-null cached object
     */
    public @NotNull T getLazily() {

        // Create the cached object if it is null
        if (null == object) {
            object = factory.create();
        }

        // Return the cached object.
        return object;
    }
}
