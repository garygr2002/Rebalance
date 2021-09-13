package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.RebalanceProcedure;
import org.jetbrains.annotations.NotNull;

public class RebalanceProcedureInterpreter
        extends Interpreter<RebalanceProcedure> {

    @Override
    protected @NotNull RebalanceProcedure doInterpret(@NotNull String string) {
        return RebalanceProcedure.valueOf(string.toUpperCase());
    }
}
