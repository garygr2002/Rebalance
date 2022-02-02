package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
import com.garygregg.rebalance.toolkit.CommandLineId;
import com.garygregg.rebalance.toolkit.PreferenceManager;
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

        // Get the preference manager, and reset the S&P 500 last close.
        final PreferenceManager manager = PreferenceManager.getInstance();
        manager.setClose(null);

        /*
         * Set the backup destination and the logging level for extraordinary
         * informational messages.
         */
        manager.setDestination(null);
        manager.setExtraordinary(null);

        // Set the S&P 500 high and the expected annual inflation rate.
        manager.setHigh(null);
        manager.setInflation(null);

        /*
         * Set the desired logging level and the limit of allowed receiver
         * delegates.
         */
        manager.setLevel(null);
        manager.setLimit(null);

        /*
         * Set the logging level for ordinary informational messages, the path
         * of the data files, and the S&P 500 today.
         */
        manager.setOrdinary(null);
        manager.setSource(null);
        manager.setToday(null);
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Throw a new CLA exception if the argument is not null. The reset
         * option does not take an argument.
         */
        if (null != argument) {
            throw new CLAException(String.format("The reset option does not " +
                    "take an argument; received %s.", argument));
        }

        // Null argument, as expected. Reset the preferences.
        resetPreferences();
        printNoException(getKey().toString());
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.RESET;
    }
}
