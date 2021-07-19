package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

public interface Dispatch<KeyType extends Enum<KeyType>> {

    /**
     * Dispatches a command line option.
     *
     * @param argument The argument for the command line option
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line argument
     */
    void dispatch(String argument) throws CLAException;

    /***
     * Gets the key that this dispatcher is meant to handle.
     * @return The key that this dispatcher is meant to handle
     */
    @NotNull KeyType getKey();
}
