package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class NonNegativeInterpreter extends DoubleInterpreter {

    @Override
    protected @NotNull Double doInterpret(@NotNull String string) {

        // Call the superclass to interpret the value. Is the result negative?
        final Double result = super.doInterpret(string);
        if (!(0. <= result)) {

            // The result is negative. Throw a new illegal argument exception.
            throw new IllegalArgumentException(String.format("This value " +
                    "may not be negative; %f detected.", result));
        }

        // Return the result.
        return result;
    }
}
