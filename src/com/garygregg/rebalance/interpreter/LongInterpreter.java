package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class LongInterpreter extends Interpreter<Long> {

    /**
     * Constructs the long integer interpreter.
     */
    public LongInterpreter() {
        super();
    }

    @Override
    protected @NotNull Long doInterpret(@NotNull String string) {
        return Long.parseLong(string);
    }
}
