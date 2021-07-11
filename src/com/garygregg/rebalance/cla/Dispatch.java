package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

public interface Dispatch {

    /**
     * Dispatches a command line option.
     *
     * @param argument The argument for the command line option
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line argument
     */
    void dispatch(@NotNull String argument) throws CLAException;
}
