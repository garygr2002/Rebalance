package com.garygregg.rebalance;

import com.garygregg.rebalance.interpreter.Interpreter;
import org.jetbrains.annotations.NotNull;

public class FilingStatusInterpreter extends Interpreter<FilingStatus> {

    @Override
    protected @NotNull FilingStatus doInterpret(@NotNull String string) {
        return FilingStatus.valueOf(string.toUpperCase());
    }
}
