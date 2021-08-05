package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.PreferenceManager;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
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

    /**
     * Resets preferences.
     */
    private static void resetPreferences() {

        // Get the preference manager, and reset the S&P 500 current level.
        final PreferenceManager manager = PreferenceManager.getInstance();
        manager.setCurrent(null);

        // Reset the backup destination, and the S&P 500 high level.
        manager.setDestination(null);
        manager.setHigh(null);

        /*
         * Reset the expected annual inflation rate, the desired logging level,
         * and the path for the data files.
         */
        manager.setInflation(null);
        manager.setLevel(null);
        manager.setSource(null);
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Throw a new CLA exception of the argument is not null. The reset
         * option does not take an argument.
         */
        if (null != argument) {
            throw new CLAException(String.format("The reset option does not " +
                    "take an argument; received %s.", argument));
        }

        // Null argument, as expected. Reset the preferences.
        resetPreferences();
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.RESET;
    }
}
