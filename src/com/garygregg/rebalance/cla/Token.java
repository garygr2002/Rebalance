package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

class Token<T extends Enum<T>> {

    // The ID of the token
    private final T id;

    // The value of the token
    private final String value;

    /**
     * Constructs the token.
     *
     * @param id    The ID of the token
     * @param value The value of the token
     */
    public Token(T id, @NotNull String value) {

        // Set the member variables.
        this.id = id;
        this.value = value;
    }

    /**
     * Gets the ID of the token.
     *
     * @return The ID of the token
     */
    public T getId() {
        return id;
    }

    /**
     * Gets the value of the token.
     *
     * @return The value of the token
     */
    public @NotNull String getValue() {
        return value;
    }
}
