package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class PathPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends PreferenceDispatch<KeyType> {

    /**
     * Constructs the path preferences dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     */
    public PathPreferenceDispatch(@NotNull KeyType key,
                                  @NotNull Preferences preferences,
                                  @NotNull PrintStream stream) {
        super(key, preferences, stream);
    }

    @Override
    protected void put(@NotNull String candidate) throws CLAException {

        /*
         * Throw an illegal argument exception if the candidate is not a
         * directory.
         */
        if (!Files.isDirectory(Paths.get(candidate))) {
            throw new CLAException(String.format("'%s' is not " +
                    "a valid directory", candidate));
        }

        // The candidate is a directory. Set the preference.
        super.put(candidate);
    }
}
