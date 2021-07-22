package com.garygregg.rebalance.cla;

import com.garygregg.rebalance.Informer;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.prefs.Preferences;

public class PreferenceDispatch<KeyType extends Enum<KeyType>>
        extends Informer implements Dispatch<KeyType> {

    // The key for this dispatch
    private final KeyType key;

    // The name of the key
    private final String keyName;

    // The preferences object to use
    private final Preferences preferences;

    /**
     * Constructs the preferences dispatch.
     *
     * @param key         The key for this dispatch
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     */
    public PreferenceDispatch(@NotNull KeyType key,
                              @NotNull Preferences preferences,
                              @NotNull PrintStream stream) {

        // Call the superclass method and set the key.
        super(stream);
        this.key = key;

        // Set the name of the key, and the preferences object.
        this.keyName = getKey().name();
        this.preferences = preferences;
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * The 'get' and 'put' methods of this class may throw a variety of
         * exceptions.
         */
        try {

            /*
             * Output a message describing the current setting of the
             * preference if the argument is null...
             */
            if (null == argument) {
                getStream().printf("The current value for '%s' is set to " +
                        "'%s'.%n", getKeyName().toLowerCase(), get());
            }

            // ...otherwise set the non-null argument as the new preference.
            else {
                put(argument);
            }
        }

        // Catch any thrown exception, and wrap it in a CLA exception.
        catch (@NotNull Exception exception) {
            throw new CLAException(exception);
        }
    }

    /**
     * Gets the preference associated with the key name.
     *
     * @return The preference associated with the key name
     */
    protected String get() {
        return getPreferences().get(getKeyName(), null);
    }

    @Override
    public @NotNull KeyType getKey() {
        return key;
    }

    /**
     * Gets the name of the key.
     *
     * @return The name of the key
     */
    protected @NotNull String getKeyName() {
        return keyName;
    }

    /**
     * Gets the preferences object to use.
     *
     * @return The preferences object to use
     */
    protected Preferences getPreferences() {
        return preferences;
    }

    /**
     * Associates a new non-null preference with the key name.
     *
     * @param value The new non-null preference to associate with the key name
     * @throws CLAException Indicates that there is something wrong the value
     */
    protected void put(@NotNull String value) throws CLAException {
        getPreferences().put(getKeyName(), value);
    }
}
