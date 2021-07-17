package com.garygregg.rebalance.cla;

public interface Dispatch<T extends Enum<T>> {

    /**
     * Dispatches a command line option.
     *
     * @param argument The argument for the command line option
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line argument
     */
    void dispatch(String argument) throws CLAException;

    /***
     * Gets the type that this dispatcher is meant to handle.
     * @return The type that this dispatcher is meant to handle
     */
    T getType();
}
