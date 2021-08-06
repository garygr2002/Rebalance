package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class IntegerInterpreter extends Interpreter<Integer> {

    /**
     * Constructs the integer interpreter.
     */
    public IntegerInterpreter() {
        super();
    }

    @Override
    protected @NotNull Integer doInterpret(@NotNull String string) {
        return Integer.parseInt(string);
    }
}
