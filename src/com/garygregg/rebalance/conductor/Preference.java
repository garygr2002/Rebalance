package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
import com.garygregg.rebalance.cla.PreferenceDispatch;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.toolkit.CommandLineId;
import com.garygregg.rebalance.toolkit.PreferenceManager;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.text.DecimalFormat;

class Preference extends Informer implements Dispatch<CommandLineId> {

    // A format for rounding to two places
    private static final DecimalFormat decimalFormat =
            new DecimalFormat("0.00");

    /**
     * Constructs the preference dispatch.
     *
     * @param stream The output stream for messages
     */
    public Preference(@NotNull PrintStream stream) {
        super(stream);
    }

    /**
     * Formats a floating point preference.
     *
     * @param preference A floating point preference
     * @return A formatted floating point preference, or null if the preference
     * was null
     */
    private static String formatPreference(Double preference) {
        return (null == preference) ? null : decimalFormat.format(preference);
    }

    @Override
    public void dispatch(String argument) {

        /*
         * Get the preference manager. Display the preference for logging
         * level.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        displayPreference(CommandLineId.LEVEL, manager.getLevel());

        /*
         * Display the preference for display of ordinary informational
         * messages.
         */
        displayPreference(CommandLineId.ORDINARY,
                manager.getOrdinary());

        /*
         * Display the preference for display of extraordinary informational
         * messages.
         */
        displayPreference(CommandLineId.EXTRAORDINARY,
                manager.getExtraordinary());

        // Display the preference for inflation.
        displayPreference(CommandLineId.INFLATION,
                Percent.format(manager.getInflation()));

        // Display the preference for S&P 500 high.
        displayPreference(CommandLineId.HIGH,
                formatPreference(manager.getHigh()));

        // Display the preference for S&P 500 last close.
        displayPreference(CommandLineId.CLOSE,
                formatPreference(manager.getClose()));

        // Display the preference for S&P 500 today.
        displayPreference(CommandLineId.TODAY,
                formatPreference(manager.getToday()));

        /*
         * Display the preference for the limit of reallocation iterations.
         * Display the preferences for source and destination.
         */
        displayPreference(CommandLineId.X, manager.getLimit());
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
