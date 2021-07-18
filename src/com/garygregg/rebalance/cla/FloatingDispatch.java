package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

public abstract class FloatingDispatch<T extends Enum<T>>
        extends ParseableDispatch<T, Double> {

    @Override
    protected @NotNull Double parse(@NotNull String argument)
            throws CLAException {

        // Declare a variable to receive a double.
        double level;
        try {

            // Parse the argument as a double.
            level = Double.parseDouble(argument);
        }

        // Oops. The argument is not parseable as a double.
        catch (NumberFormatException exception) {
            throw new CLAException(exception.getMessage());
        }

        // Return the double.
        return level;
    }
}
