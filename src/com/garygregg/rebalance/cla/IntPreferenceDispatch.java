package com.garygregg.rebalance.cla;

import com.garygregg.rebalance.interpreter.CatchInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class IntPreferenceDispatch<KeyType extends Enum<KeyType>>
        extends FlaggedPreferenceDispatch<KeyType> {

    // Our catch interpreter
    private final CatchInterpreter catchInterpreter =
            new CatchInterpreter();

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

    /**
     * Gets the default value to use in case of no current preference.
     *
     * @return The default value to use in case of no current preference
     */
    protected static int getDefaultValue() {
        return Integer.MIN_VALUE;
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

    @Override
    protected void put(@NotNull String value) throws CLAException {

        /*
         * Clear the catch interpreter. Try to parse the integer. Was the catch
         * interpreter set, indicating it caught an exception?
         */
        catchInterpreter.clear();
        int intValue = catchInterpreter.interpret(value);
        if (catchInterpreter.isSet()) {

            /*
             * The catch interpreter is set, indicating it caught an
             * exception. This means the given string cannot be parsed as an
             * integer. Throw a new CLA exception with the details.
             */
            throw new CLAException(String.format("Unable to parse an " +
                            "integer value for option '%s' - %s.",
                    getKeyName().toLowerCase(),
                    catchInterpreter.getException().getMessage()));
        }

        /*
         * The catch interpreter was not set, but did it read a negative value
         * when negative values are not permitted?
         */
        else if (!(getFlag() || (0 <= intValue))) {

            /*
             * An impermissible negative value was received. Throw a new
             * CLA exception.
             */
            throw new CLAException(String.format("Negative values are " +
                            "not permitted for '%s'; %d received.",
                    getKeyName().toLowerCase(), intValue));
        }

        // Set the integer value as the preference under the key name.
        getPreferences().putInt(getKeyName(), intValue);
    }
}
