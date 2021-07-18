package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

abstract class ParseableDispatch<T extends Enum<T>, U>
        extends NonNullDispatch<T> {

    /**
     * Dispatches a command line option.
     *
     * @param argument The argument for the command line option
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line argument
     */
    public abstract void dispatch(@NotNull U argument) throws CLAException;

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Call the superclass method. Parse the string argument, and dispatch
         * the non-null result.
         */
        super.dispatch(argument);
        dispatch(parse(argument));
    }

    /**
     * Parses a string argument.
     *
     * @param argument The string argument to parse.
     * @return The string argument parsed to the type of this class
     * @throws CLAException Indicates that the argument could not be parsed
     */
    protected abstract @NotNull U parse(@NotNull String argument)
            throws CLAException;
}
