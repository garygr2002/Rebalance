package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.TaxType;
import org.jetbrains.annotations.NotNull;

public class TaxTypeInterpreter extends Interpreter<TaxType> {

    /**
     * Constructs the tax type interpreter.
     */
    public TaxTypeInterpreter() {
        super();
    }

    @Override
    protected @NotNull TaxType doInterpret(@NotNull String string) {
        return TaxType.valueOf(string);
    }
}
