package com.garygregg.rebalance.interpreter;

import org.jetbrains.annotations.NotNull;

public abstract class Interpreter<T> {

    // The column
    private int column;

    // The row
    private int row;

    /**
     * Interprets a string.
     *
     * @param string The string to interpret
     * @return The interpreted string
     */
    protected abstract @NotNull T doInterpret(@NotNull String string);

    /**
     * Gets the column.
     *
     * @return The column
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets the row.
     *
     * @return The row
     */
    protected int getRow() {
        return row;
    }

    /**
     * Interprets a string with an explicit default value.
     *
     * @param string       The string to interpret
     * @param defaultValue The default value to use if the procedure throws an
     *                     exception
     * @return The result of the interpretation
     */
    public T interpret(@NotNull String string, T defaultValue) {

        // Declare the result, and initialize it with the default.
        T result = defaultValue;
        try {

            // Try to interpret the string using the given procedure.
            result = doInterpret(string);
        }

        // Catch any exception that the procedure may throw.
        catch (@NotNull Exception exception) {
            receiveException(exception, string, defaultValue);
        }

        // Return the result of the interpretation.
        return result;
    }

    /**
     * Interprets a string with an implied default value (null).
     *
     * @param string The string to interpret
     * @return The result of the interpretation
     */
    public T interpret(@NotNull String string) {
        return interpret(string, null);
    }

    /**
     * Receives any exception that may occur
     *
     * @param exception    The exception that was thrown
     * @param string       The string that caused the exception
     * @param defaultValue The default value to be used
     */
    protected void receiveException(@NotNull Exception exception,
                                    @NotNull String string,
                                    T defaultValue) {
        throw new RuntimeException(exception);
    }

    /**
     * Sets the column.
     *
     * @param column The column
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Sets the row.
     *
     * @param row The row
     */
    public void setRow(int row) {
        this.row = row;
    }
}
