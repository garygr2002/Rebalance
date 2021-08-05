package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class Informer {

    // The output stream for messages
    private final PrintStream stream;

    /**
     * Constructs the informer.
     *
     * @param stream The output stream for messages
     */
    public Informer(@NotNull PrintStream stream) {
        this.stream = stream;
    }

    /**
     * Gets the output stream for messages.
     *
     * @return The output stream for messages
     */
    protected @NotNull PrintStream getStream() {
        return stream;
    }
}
