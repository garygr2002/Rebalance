package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

class Container<T> {

    // A factory for producing objects of our contained type
    private final Factory<? extends T> factory;

    // A contained object
    private T object;

    /**
     * Constructs the container.
     *
     * @param factory A factory for producing contained objects
     */
    public Container(@NotNull Factory<? extends T> factory) {

        // Set the factory, and clear the contained object
        this.factory = factory;
        clear();
    }

    /**
     * Clears the contained object.
     */
    void clear() {
        object = null;
    }

    /**
     * Gets the contained object.
     *
     * @return The contained object
     */
    public @NotNull T get() {

        /*
         * Product and set a new contained object if the current contained
         * object is null.
         */
        if (null == object) {
            set(factory.produce());
        }

        return object;
    }

    /**
     * Sets the contained object.
     *
     * @param object The new contained object
     */
    void set(@NotNull T object) {
        this.object = object;
    }
}
