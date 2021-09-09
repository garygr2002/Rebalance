package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public class CatchInterpreter extends IntegerInterpreter {

    // The default value used when interpretation fails
    private Integer defaultValue;

    // The exception thrown by failed interpretation
    private Exception exception;

    // The string that could not be interpreted
    private String string;

    /**
     * Constructs the catch interpreter.
     */
    public CatchInterpreter() {
        clear();
    }

    /**
     * Clears the member variables.
     */
    public void clear() {
        set(null, null, null);
    }

    /**
     * Gets the most recently set default value.
     *
     * @return The most recently set default value
     */
    public Integer getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the most recently set exception.
     *
     * @return The most recently set exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Gets the mostly recently set string that could not be interpreted.
     *
     * @return The most recently set string that could not be interpreted
     */
    public String getString() {
        return string;
    }

    /**
     * Determines if the member variables are set.
     *
     * @return True if the member variables are set; false otherwise
     */
    public boolean isSet() {
        return (null != exception);
    }

    @Override
    protected void receiveException(@NotNull Exception exception,
                                    @NotNull String string,
                                    Integer defaultValue) {
        set(exception, string, defaultValue);
    }

    /**
     * Sets the member variables.
     *
     * @param exception    The exception thrown by failed interpretation
     * @param string       The string that could not be interpreted
     * @param defaultValue The default used when interpretation fails
     */
    private void set(Exception exception,
                     String string,
                     Integer defaultValue) {

        // Set all the member variables.
        this.exception = exception;
        this.string = string;
        this.defaultValue = defaultValue;
    }
}
