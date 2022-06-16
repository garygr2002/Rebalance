package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class PositiveInterpreter extends DoubleInterpreter {

    @Override
    protected @NotNull Double doInterpret(@NotNull String string) {

        /*
         * Call the superclass to interpret the value. Is the result not
         * positive?
         */
        final Double result = super.doInterpret(string);
        if (!(0. < result)) {

            /*
             * The result is not positive. Throw a new illegal argument
             * exception.
             */
            throw new IllegalArgumentException(String.format("This value " +
                    "must be positive; %f detected.", result));
        }

        // Return the result.
        return result;
    }
}
