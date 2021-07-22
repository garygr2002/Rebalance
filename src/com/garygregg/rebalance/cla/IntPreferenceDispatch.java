package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class IntPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends FlaggedPreferenceDispatch<KeyType> {

    // The default value to use in case of no current preference
    private final int defaultValue;

    /**
     * Constructs the integer preference dispatch.
     *
     * @param key          The key for this dispatch
     * @param preferences  The preferences object to use
     * @param stream       The output stream for messages
     * @param flag         The flag; true if negatives are okay, false otherwise
     * @param defaultValue The default value to use in case of no current
     *                     preference
     */
    public IntPreferenceDispatch(@NotNull KeyType key,
                                 @NotNull Preferences preferences,
                                 @NotNull PrintStream stream,
                                 boolean flag,
                                 int defaultValue) {

        // Call the superclass constructor, and set the default value.
        super(key, preferences, stream, flag);
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
    protected void put(@NotNull String value) throws CLAException {

        // Declare a variable to receive a parsed integer.
        int intValue;
        try {

            /*
             * Try to parse the integer. Was a negative integer received when
             * it is not permitted?
             */
            intValue = Integer.parseInt(value);
            if (!(getFlag() || (0 <= intValue))) {

                /*
                 * An impermissible negative value was received. Throw a new
                 * CLA exception.
                 */
                throw new CLAException(String.format("Negative values are " +
                                "not permitted for '%s'; %d received.",
                        getKeyName().toLowerCase(), intValue));
            }
        }

        // The received value cannot be parsed as an integer.
        catch (@NotNull NumberFormatException exception) {
            throw new CLAException(exception.getMessage());
        }

        // Set the integer value as the preference under the key name.
        getPreferences().putInt(getKeyName(), intValue);
    }
}
