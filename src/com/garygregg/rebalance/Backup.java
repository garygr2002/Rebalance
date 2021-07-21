package com.garygregg.rebalance;

import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.CommandLineId;
import com.garygregg.rebalance.cla.Dispatch;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

class Backup extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the informer.
     *
     * @param stream The output stream for messages
     */
    public Backup(@NotNull PrintStream stream) {
        super(stream);
    }

    @Override
    public void dispatch(String argument) throws CLAException {
        getStream().printf("Backup command received with argument of '%s'.",
                argument);
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.BACKUP;
    }
}
