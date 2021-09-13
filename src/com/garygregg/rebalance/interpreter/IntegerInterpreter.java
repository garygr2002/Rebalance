package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class IntegerInterpreter extends Interpreter<Integer> {

    @Override
    protected @NotNull Integer doInterpret(@NotNull String string) {
        return Integer.parseInt(string);
    }
}
