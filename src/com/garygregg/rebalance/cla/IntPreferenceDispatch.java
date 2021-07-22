package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class IntPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends FlaggedPreferenceDispatch<KeyType> {

    /**
     * Constructs the integer preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     * @param flag        The flag; true if negatives are okay, false otherwise
     */
    public IntPreferenceDispatch(@NotNull KeyType key,
                                 @NotNull Preferences preferences,
                                 @NotNull PrintStream stream,
                                 boolean flag) {
        super(key, preferences, stream, flag);
    }

    @Override
    protected String get() {

        /*
         * Get the default value. Use the default to get the preference based
         * on the key name.
         */
        final int defaultValue = getDefaultValue();
        final int preference = getPreferences().getInt(getKeyName(),
                getDefaultValue());

        /*
         * Return null if the default was returned as a preference, otherwise
         * return the preference as a string.
         */
        return (preference == defaultValue) ? null :
                Integer.toString(preference);
    }

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    protected int getDefaultValue() {
        return Integer.MIN_VALUE;
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
            throw new CLAException(String.format("Unable to parse an " +
                            "integer value for option '%s' - %s.",
                    getKeyName().toLowerCase(), exception.getMessage()));
        }

        // Set the integer value as the preference under the key name.
        getPreferences().putInt(getKeyName(), intValue);
    }
}
