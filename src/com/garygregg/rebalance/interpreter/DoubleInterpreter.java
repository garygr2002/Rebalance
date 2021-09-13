package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class DoubleInterpreter extends Interpreter<Double> {

    @Override
    protected @NotNull Double doInterpret(@NotNull String string) {
        return Double.parseDouble(string);
    }
}
