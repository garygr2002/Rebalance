package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.interpreter.Interpreter;
import org.jetbrains.annotations.NotNull;

public class TaxTypeInterpreter extends Interpreter<TaxType> {

    @Override
    protected @NotNull TaxType doInterpret(@NotNull String string) {
        return TaxType.valueOf(string.toUpperCase());
    }
}
