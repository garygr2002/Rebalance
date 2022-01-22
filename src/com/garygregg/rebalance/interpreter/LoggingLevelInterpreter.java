package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LoggingLevelInterpreter extends Interpreter<Level> {

    // A map of integer values to the levels with which they correspond
    private final static Map<Integer, Level> levelMap = new HashMap<>();

    static {

        // Load up the level map.
        put(Level.ALL);
        put(Level.CONFIG);
        put(Level.FINE);
        put(Level.FINER);
        put(Level.FINEST);
        put(Level.INFO);
        put(Level.OFF);
        put(Level.SEVERE);
        put(Level.WARNING);
    }

    // The integer interpreter delegate
    private final IntegerInterpreter delegate = new IntegerInterpreter() {

        @Override
        protected void receiveException(@NotNull Exception exception,
                                        @NotNull String string,
                                        Integer defaultValue) {

            // This enclosing class will deal with parse errors.
        }
    };

    /**
     * Gets a logging level corresponding to an integer value.
     *
     * @param value An integer value
     * @return The logging level corresponding to the integer value, or null if
     * no logging level corresponds to the value
     */
    public static Level getLevel(Integer value) {
        return levelMap.get(value);
    }

    /**
     * Puts a level in the level map.
     *
     * @param level The level to put in the level map
     * @return Any level previously in the map using the same integer value
     */
    @SuppressWarnings("UnusedReturnValue")
    private static Level put(@NotNull Level level) {
        return levelMap.put(level.intValue(), level);
    }

    @Override
    protected @NotNull Level doInterpret(@NotNull String string) {

        /*
         * Parse the string as an integer. Use the result to get the
         * corresponding logging level. Is there no corresponding logging
         * level?
         */
        final Level result = getLevel(delegate.interpret(string));
        if (null == result) {

            /*
             * There is no logging level corresponding to the parsed string.
             * Throw an illegal format exception.
             */
            throw new IllegalArgumentException(String.format("Unable to " +
                    "parse '%s' as a logging level", string));
        }

        // Return the result.
        return result;
    }
}
