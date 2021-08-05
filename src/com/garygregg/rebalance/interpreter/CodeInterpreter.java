package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class CodeInterpreter extends Interpreter<Character> {

    /**
     * Constructs the code interpreter.
     */
    public CodeInterpreter() {
        super();
    }

    @Override
    protected @NotNull Character doInterpret(@NotNull String string) {
        return Character.toUpperCase(string.charAt(0));
    }
}
