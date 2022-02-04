package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public abstract class OnPutPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends DoublePreferenceDispatch<KeyType> {

    /**
     * Constructs the 'on put' preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     * @param flag        The flag; true if negatives are okay, false otherwise
     */
    public OnPutPreferenceDispatch(@NotNull KeyType key,
                                   @NotNull Preferences preferences,
                                   @NotNull PrintStream stream, boolean flag) {
        super(key, preferences, stream, flag);
    }

    /**
     * Reacts to the successful completion of an 'on put' call.
     */
    protected abstract void onPut();

    @Override
    protected void put(@NotNull String value) throws CLAException {

        // First call the superclass method, then call 'on put'.
        super.put(value);
        onPut();
    }
}
