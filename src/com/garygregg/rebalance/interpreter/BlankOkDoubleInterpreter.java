package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class BlankOkDoubleInterpreter extends BlankOkInterpreter<Double> {

    // The delegate for double interpretation
    private final DoubleInterpreter delegate = new DoubleInterpreter();

    /**
     * Constructs the "blank okay" double interpreter.
     */
    public BlankOkDoubleInterpreter() {
        super();
    }

    @Override
    protected @NotNull Double doInterpret(@NotNull String string) {
        return delegate.doInterpret(string);
    }
}
