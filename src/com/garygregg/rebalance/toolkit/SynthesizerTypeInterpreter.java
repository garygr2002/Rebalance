package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.interpreter.Interpreter;
import org.jetbrains.annotations.NotNull;

public class SynthesizerTypeInterpreter extends Interpreter<SynthesizerType> {

    @Override
    protected @NotNull SynthesizerType doInterpret(@NotNull String string) {
        return SynthesizerType.valueOf(string.toUpperCase());
    }
}
