package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.RebalanceProcedure;
import com.garygregg.rebalance.TaxType;
import org.jetbrains.annotations.NotNull;

public class RebalanceProcedureInterpreter
        extends Interpreter<RebalanceProcedure> {

    /**
     * Constructs the re-balance procedure interpreter.
     */
    public RebalanceProcedureInterpreter() {
        super();
    }

    @Override
    protected @NotNull RebalanceProcedure doInterpret(@NotNull String string) {
        return RebalanceProcedure.valueOf(string);
    }
}
