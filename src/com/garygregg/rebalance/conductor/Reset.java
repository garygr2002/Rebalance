package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.Informer;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

class Reset extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the reset dispatch.
     *
     * @param stream The output stream for messages
     */
    public Reset(@NotNull PrintStream stream) {
        super(stream);
    }

    @Override
    public void dispatch(String argument) throws CLAException {
        getStream().printf("Reset command received with argument of '%s'.",
                argument);
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.RESET;
    }
}
