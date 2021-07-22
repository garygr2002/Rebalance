package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

class FlaggedPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends PreferenceDispatch<KeyType> {

    // The flag
    private final boolean flag;

    /**
     * Constructs the flagged preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     * @param flag        The flag
     */
    public FlaggedPreferenceDispatch(@NotNull KeyType key,
                                     @NotNull Preferences preferences,
                                     @NotNull PrintStream stream, boolean flag) {

        // Call the superclass constructor, and set the flag.
        super(key, preferences, stream);
        this.flag = flag;
    }

    /**
     * Gets the flag.
     *
     * @return The flag
     */
    protected boolean getFlag() {
        return flag;
    }
}
