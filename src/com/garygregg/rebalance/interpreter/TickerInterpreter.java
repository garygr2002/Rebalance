package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class TickerInterpreter extends Interpreter<String> {

    /**
     * Constructs the ticker interpreter.
     */
    public TickerInterpreter() {
        super();
    }

    @Override
    protected @NotNull String doInterpret(@NotNull String string) {
        return string.toUpperCase();
    }
}
