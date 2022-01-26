package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.interpreter.Interpreter;
import org.jetbrains.annotations.NotNull;

public class RebalanceProcedureInterpreter
        extends Interpreter<RebalanceProcedure> {

    @Override
    protected @NotNull RebalanceProcedure doInterpret(@NotNull String string) {
        return RebalanceProcedure.valueOf(string.toUpperCase());
    }
}
