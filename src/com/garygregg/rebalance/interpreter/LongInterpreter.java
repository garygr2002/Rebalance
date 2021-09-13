package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class LongInterpreter extends Interpreter<Long> {

    @Override
    protected @NotNull Long doInterpret(@NotNull String string) {
        return Long.parseLong(string);
    }
}
