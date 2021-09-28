package com.garygregg.rebalance.interpreter;

import com.garygregg.rebalance.SynthesizerType;
import org.jetbrains.annotations.NotNull;

public class SynthesizerTypeInterpreter extends Interpreter<SynthesizerType> {

    @Override
    protected @NotNull SynthesizerType doInterpret(@NotNull String string) {
        return SynthesizerType.valueOf(string.toUpperCase());
    }
}
