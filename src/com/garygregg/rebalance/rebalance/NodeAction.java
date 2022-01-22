package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
interface NodeAction<ArgumentType> {

    /**
     * Performs the node action.
     *
     * @param object The node action argument
     */
    void doAction(@NotNull ArgumentType object);
}
