package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.TaxType;
import org.jetbrains.annotations.NotNull;

public class TaxTypeInterpreter extends Interpreter<TaxType> {

    @Override
    protected @NotNull TaxType doInterpret(@NotNull String string) {
        return TaxType.valueOf(string.toUpperCase());
    }
}
