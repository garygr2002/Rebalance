package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.prefs.Preferences;

public class LimitedPreferenceDispatch<KeyType extends Enum<KeyType>> extends
        LevelPreferenceDispatch<KeyType> {

    // The collection of acceptable levels
    private static final Collection<Level> acceptableLevels = new HashSet<>();

    static {

        // Load up the acceptable level set.
        put(Level.ALL);
        put(Level.CONFIG);
        put(Level.FINE);
        put(Level.FINER);
        put(Level.FINEST);
        put(Level.INFO);
        put(Level.OFF);
    }

    /**
     * Constructs the limited level preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     */
    public LimitedPreferenceDispatch(@NotNull KeyType key,
                                     @NotNull Preferences preferences,
                                     @NotNull PrintStream stream) {
        super(key, preferences, stream);
    }

    /**
     * Puts a level in the acceptable level set.
     *
     * @param level The level to put in the acceptable level set
     */
    private static void put(@NotNull Level level) {

        /*
         * Try to add the given level to the acceptable level set, and throw a
         * new illegal argument exception if the level is already present.
         */
        if (!acceptableLevels.add(level)) {
            throw new IllegalArgumentException(String.format("An attempt " +
                    "has been made to insert duplicate level '%s' in the " +
                    "set of limited logging levels.", level));
        }
    }

    @Override
    protected void checkLevel(@NotNull Level level) {

        /*
         * Call the superclass method. Is the given logging level not
         * contained in the acceptable level set?
         */
        super.checkLevel(level);
        if (!acceptableLevels.contains(level)) {

            /*
             * The given logging level is not contained in the acceptable level
             * set. Throw a new illegal argument exception describing the
             * problem.
             */
            throw new IllegalArgumentException(String.format("The level " +
                    "'%s' is recognized, but is not permitted here.", level));
        }
    }
}
