package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class FilingStatusInterpreter extends Interpreter<FilingStatus> {

    @Override
    protected @NotNull FilingStatus doInterpret(@NotNull String string) {
        return FilingStatus.valueOf(string.toUpperCase());
    }
}
