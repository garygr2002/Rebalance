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

    /**
     * Constructs the level preferences dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     */
    public LevelPreferenceDispatch(@NotNull KeyType key,
                                   @NotNull Preferences preferences,
                                   @NotNull PrintStream stream) {
        super(key, preferences, stream, false);
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
    @SuppressWarnings("UnusedReturnValue")
    private static Level put(@NotNull Level level) {
        return levelMap.put(level.intValue(), level);
    }

    /**
     * Gets the level associated with a value.
     *
     * @param value The level associated with the given value
     * @return The level associated with the given value, or null if there is
     * no such associated level
     */
    private Level get(int value) {
        return getLevel(value);
    }

    @Override
    protected String get() {
        return get(Integer.parseInt(super.get())).getName();
    }

    @Override
    protected void put(@NotNull String value) throws CLAException {
        super.put(Integer.toString(
                Level.parse(value.toUpperCase()).intValue()));
    }
}
