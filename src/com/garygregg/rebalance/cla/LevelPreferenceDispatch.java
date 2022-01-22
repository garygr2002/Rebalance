package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class LevelPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends IntPreferenceDispatch<KeyType> {

    // A map of integer values to the levels with which they correspond
    private static final Map<Integer, Level> levelMap = new HashMap<>();

    static {

        // Load up the level map.
        putAndCheck(Level.ALL);
        putAndCheck(Level.CONFIG);
        putAndCheck(Level.FINE);
        putAndCheck(Level.FINER);
        putAndCheck(Level.FINEST);
        putAndCheck(Level.INFO);
        putAndCheck(Level.OFF);
        putAndCheck(Level.SEVERE);
        putAndCheck(Level.WARNING);
    }

    /**
     * Constructs the level preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     */
    public LevelPreferenceDispatch(@NotNull KeyType key,
                                   @NotNull Preferences preferences,
                                   @NotNull PrintStream stream) {
        super(key, preferences, stream, false);
    }

    /**
     * Checks to ensure that a level argument is not null.
     *
     * @param level A level argument
     */
    private static void checkNotNull(Level level) {

        // Throw a new illegal argument exception if the argument is not null.
        if (null != level) {
            throw new IllegalArgumentException(String.format("An attempt " +
                    "has been made to insert duplicate level '%s' in the " +
                    "level map.", level));
        }
    }

    /**
     * Gets the level associated with a value.
     *
     * @param value The level associated with the given value
     * @return The level associated with the given value, or null if there is
     * no such associated level
     */
    private static Level get(int value) {
        return getLevel(value);
    }

    /**
     * Gets a level corresponding to an integer value.
     *
     * @param value An integer value
     * @return The level corresponding to the integer value, or null if no
     * level corresponds to the value
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
    private static Level put(@NotNull Level level) {
        return levelMap.put(level.intValue(), level);
    }

    /**
     * Puts a level in the level map, and checks to ensure that the level
     * is not a duplicate.
     *
     * @param level The level to put in the level map
     */
    private static void putAndCheck(@NotNull Level level) {
        checkNotNull(put(level));
    }

    /**
     * Checks a level to ensure it is acceptable.
     *
     * @param level A level to check
     */
    protected void checkLevel(@NotNull Level level) {

        // Any level will do for this parent class. Override as needed.
    }

    @Override
    protected String get() {

        /*
         * Get the known integer (or null) from the superclass. Is the known
         * integer not null?
         */
        String result = super.get();
        if (null != result) {

            /*
             * The known integer is not null. Parse it as an integer, and use
             * the result to get a logging level. Reset the result to null if
             * the logging level is null, otherwise reset the result to the
             * name of the logging level.
             */
            final Level level = get(Integer.parseInt(result));
            result = (null == level) ? null : level.getName();
        }

        // Return the result.
        return result;
    }

    @Override
    protected void put(@NotNull String value) throws CLAException {

        // Declare a variable to receive a logging level.
        Level level;
        try {

            /*
             * Try to parse the given value as a logging level, then check the
             * result.
             */
            checkLevel(level = Level.parse(value.toUpperCase()));
        }

        /*
         * Catch any illegal argument exception that may occur, wrap it in a
         * new CLA exception.
         */
        catch (@NotNull IllegalArgumentException exception) {
            throw new CLAException(String.format("Unable to parse a logging " +
                            "level value for option '%s' - %s.",
                    getKeyName().toLowerCase(), exception.getMessage()));
        }

        // Call the superclass method to put the resulting logging level.
        super.put(Integer.toString(level.intValue()));
    }
}
