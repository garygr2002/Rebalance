package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class IntPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends PreferenceDispatch<KeyType> {

    // The default value to use in case of no current preference
    private final int defaultValue;

    /**
     * Constructs the integer preferences dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     * @param defaultValue The default value to use in case of no current
     *                     preference
     */
    public IntPreferenceDispatch(@NotNull KeyType key,
                                 @NotNull Preferences preferences,
                                 @NotNull PrintStream stream,
                                 int defaultValue) {

        // Call the superclass constructor, and set the default value.
        super(key, preferences, stream);
        this.defaultValue = defaultValue;
    }

    @Override
    protected String get() {
        return Integer.toString(getPreferences().getInt(getKeyName(),
                getDefaultValue()));
    }

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    protected int getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected void put(@NotNull String value) {
        getPreferences().putInt(getKeyName(), Integer.parseInt(value));
    }
}
