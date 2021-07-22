package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.Informer;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

class Backup extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the backup dispatch.
     *
     * @param stream The output stream for messages
     */
    public Backup(@NotNull PrintStream stream) {
        super(stream);
    }

    @Override
    public void dispatch(String argument) throws CLAException {
        getStream().printf("Backup command received with argument of '%s'.%n",
                argument);
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.BACKUP;
    }
}
