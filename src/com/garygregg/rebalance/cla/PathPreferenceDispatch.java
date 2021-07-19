package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class PathPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends PreferenceDispatch<KeyType> {

    // The default value to use in case of no current preference
    private final String defaultValue;

    /**
     * Constructs the path preferences dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     * @param defaultValue The default value to use in case of no current
     *                     preference
     */
    public PathPreferenceDispatch(@NotNull KeyType key,
                                  @NotNull Preferences preferences,
                                  @NotNull PrintStream stream,
                                  @NotNull String defaultValue) {

        // Call the superclass constructor, and set the default value.
        super(key, preferences, stream);
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    private @NotNull String getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected void put(@NotNull String candidate) {

        /*
         * Throw an illegal argument exception if the candidate is not a
         * directory.
         */
        if (!Files.isDirectory(Paths.get(candidate))) {
            throw new IllegalArgumentException(String.format("'%s' is not " +
                    "a valid directory", candidate));
        }

        // The candidate is a directory. Set the preference.
        super.put(candidate);
    }
}
