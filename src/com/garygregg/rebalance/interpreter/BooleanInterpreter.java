package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class BooleanInterpreter extends Interpreter<Boolean> {

    /**
     * Constructs the boolean interpreter.
     */
    public BooleanInterpreter() {
        super();
    }

    @Override
    protected @NotNull Boolean doInterpret(@NotNull String string) {

        /*
         * Convert the argument to lower case, and parse it as a boolean value.
         * If the argument were a lowercase equivalent of 'false' or 'true',
         * then the string conversion of the parse result should now equal the
         * argument. If not, we have a problem.
         */
        final String candidate = string.toLowerCase();
        final boolean result = Boolean.parseBoolean(candidate);
        if (!Boolean.toString(result).equals(candidate)) {

            /*
             * The string conversion of the parse result does not equal the
             * lowercase conversion of the argument. Throw a new illegal
             * argument exception detailing the bad news.
             */
            throw new IllegalArgumentException(String.format("'%s' is " +
                    "cannot be parsed as a boolean value", string));
        }

        // Return the boolean result.
        return result;
    }
}
