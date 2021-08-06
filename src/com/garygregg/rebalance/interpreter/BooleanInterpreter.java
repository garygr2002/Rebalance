package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class BooleanInterpreter extends Interpreter<Boolean> {

    /**
     * Constructs the boolean interpreter.
     */
    public BooleanInterpreter() {
        super();
    }

    @Override
    protected @NotNull Boolean doInterpret(@NotNull String string) {
        return Boolean.parseBoolean(string);
    }
}
