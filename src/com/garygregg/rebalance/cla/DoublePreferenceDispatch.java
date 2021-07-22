package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class DoublePreferenceDispatch<KeyType extends Enum<KeyType>>
        extends FlaggedPreferenceDispatch<KeyType> {

    /**
     * Constructs the double preference dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     * @param flag        The flag; true if negatives are okay, false otherwise
     */
    public DoublePreferenceDispatch(@NotNull KeyType key,
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
        final double defaultValue = getDefaultValue();
        final double preference = getPreferences().getDouble(getKeyName(),
                getDefaultValue());

        /*
         * Return null if the default was returned as a preference, otherwise
         * return the preference as a string.
         */
        return (0 == Double.compare(preference, defaultValue)) ? null :
                Double.toString(preference);
    }

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    public double getDefaultValue() {
        return Double.MIN_VALUE;
    }

    @Override
    protected void put(@NotNull String value) throws CLAException {

        // Declare a variable to receive a parsed double.
        double doubleValue;
        try {

            /*
             * Try to parse the double. Was a negative double received when
             * it is not permitted?
             */
            doubleValue = Double.parseDouble(value);
            if (!(getFlag() || (0. <= doubleValue))) {

                /*
                 * An impermissible negative value was received. Throw a new
                 * CLA exception.
                 */
                throw new CLAException(String.format("Negative values are " +
                                "not permitted for '%s'; %f received.",
                        getKeyName().toLowerCase(), doubleValue));
            }
        }

        // The received value cannot be parsed as a double.
        catch (@NotNull NumberFormatException exception) {
            throw new CLAException(String.format("Unable to parse a " +
                            "floating point value for option '%s' - %s.",
                    getKeyName().toLowerCase(), exception.getMessage()));
        }

        // Set the double value as the preference under the key name.
        getPreferences().putDouble(getKeyName(), doubleValue);
    }
}
