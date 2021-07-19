package com.garygregg.rebalance.cla;

import com.garygregg.rebalance.PreferenceId;
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
     * @param defaultValue The default value to use in case of no current
     *                     preference
     */
    public LevelPreferenceDispatch(@NotNull KeyType key,
                                   @NotNull Preferences preferences,
                                   @NotNull PrintStream stream,
                                   Level defaultValue) {
        super(key, preferences, stream, defaultValue.intValue());
    }

    /**
     * Gets the level associated with a value.
     *
     * @param value The level associated with the given value
     * @return The level associated with the given value, or null if there is
     * no such associated level
     */
    private static Level get(int value) {
        return levelMap.get(value);
    }

    /**
     * Tests this preference dispatch.
     *
     * @param arguments Command line arguments
     */
    public static void main(@NotNull String[] arguments) {

        /*
         * TODO: Delete this method.
         *
         * Declare and initialize a level preference dispatch.
         */
        final LevelPreferenceDispatch<PreferenceId> dispatch =
                new LevelPreferenceDispatch<>(PreferenceId.LEVEL,
                Preferences.userRoot().node(
                        LevelPreferenceDispatch.class.getName()),
                System.out, Level.INFO);

        // Wrap all dispatch calls for exceptions.
        try {

            /*
             * Get the current preference, set a new preference, then make
             * sure the preference was set.
             */
            dispatch.dispatch(null);
            dispatch.dispatch(Level.CONFIG.toString());
            dispatch.dispatch(null);

        }

        // Report any CLA exceptions.
        catch (@NotNull CLAException exception) {
            System.err.println(exception.getMessage());
        }
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
    protected String get() {
        return get(Integer.parseInt(super.get())).getName();
    }

    @Override
    protected void put(@NotNull String value) {
        super.put(Integer.toString(
                Level.parse(value.toUpperCase()).intValue()));
    }
}
