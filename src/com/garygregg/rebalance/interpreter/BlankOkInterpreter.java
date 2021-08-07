package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

abstract class BlankOkInterpreter<T> extends Interpreter<T> {

    /**
     * Constructs the "blank okay" interpreter.
     */
    public BlankOkInterpreter() {
        super();
    }

    @Override
    public T interpret(@NotNull String string, T defaultValue) {

        /*
         * Declare the result, and initialize it with the default. Is the
         * argument blank?
         */
        T result = defaultValue;
        if (string.isBlank()) {

            // The argument is blank. Take an appropriate action.
            onBlank();
        }

        /*
         * The argument is not blank. Use the superclass to try to interpret
         * it.
         */
        else {
            result = super.interpret(string, defaultValue);
        }

        // Return the result of the interpretation.
        return result;
    }

    /**
     * Receives notification that an attempt was made to interpret a blank
     * object.
     */
    protected void onBlank() {

        // The default is to do nothing. A subclass may override this action.
    }
}
