package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.PreferenceManager;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
import com.garygregg.rebalance.cla.PreferenceDispatch;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

class Preference extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the preference dispatch.
     *
     * @param stream The output stream for messages
     */
    public Preference(@NotNull PrintStream stream) {
        super(stream);
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Get the preference manager. Displays preferences for logging level
         * and inflation.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        displayPreference(CommandLineId.LEVEL, manager.getLevel());
        displayPreference(CommandLineId.INFLATION, manager.getInflation());

        // Display preferences for S&P 500 high and S&P 500 current.
        displayPreference(CommandLineId.HIGH, manager.getHigh());
        displayPreference(CommandLineId.CURRENT, manager.getCurrent());

        // Display preferences for source and destination.
        displayPreference(CommandLineId.SOURCE, manager.getSource());
        displayPreference(CommandLineId.DESTINATION, manager.getDestination());
    }

    /**
     * Display a preference setting.
     *
     * @param commandLineId The command line ID of the preference
     * @param setting       The setting of the preference
     */
    private void displayPreference(@NotNull CommandLineId commandLineId,
                                   Object setting) {
        PreferenceDispatch.displayPreference(
                commandLineId.toString().toLowerCase(), setting, getStream());
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.PREFERENCE;
    }
}
