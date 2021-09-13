package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class TickerInterpreter extends Interpreter<String> {

    @Override
    protected @NotNull String doInterpret(@NotNull String string) {
        return string.toUpperCase();
    }
}
