package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class DoublePreferenceDispatch<KeyType extends Enum<KeyType>>
        extends PreferenceDispatch<KeyType> {

    // The default value to use in case of no current preference
    private final double defaultValue;

    /**
     * Constructs the double preferences dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     * @param defaultValue The default value to use in case of no current
     *                     preference
     */
    public DoublePreferenceDispatch(@NotNull KeyType key,
                                    @NotNull Preferences preferences,
                                    @NotNull PrintStream stream,
                                    double defaultValue) {

        // Call the superclass constructor, and set the default value.
        super(key, preferences, stream);
        this.defaultValue = defaultValue;
    }

    @Override
    protected String get() {
        return Double.toString(getPreferences().getDouble(getKeyName(),
                getDefaultValue()));
    }

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    private double getDefaultValue() {
        return defaultValue;
    }

    @Override
    protected void put(@NotNull String value) {
        getPreferences().putDouble(getKeyName(), Double.parseDouble(value));
    }
}
