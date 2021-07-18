package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public abstract class LoggingLevelDispatch<T extends Enum<T>>
        extends ParseableDispatch<T, Level> {

    @Override
    protected @NotNull Level parse(@NotNull String argument)
            throws CLAException {

        // Declare a variable to receive a logging level.
        Level level;
        try {

            // Parse the argument as a logging level.
            level = Level.parse(argument.toUpperCase());
        }

        // Oops. The argument is not parseable as a logging level.
        catch (IllegalArgumentException exception) {
            throw new CLAException(exception.getMessage());
        }

        // Return the logging level.
        return level;
    }
}
