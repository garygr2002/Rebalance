package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
import com.garygregg.rebalance.toolkit.CommandLineId;
import com.garygregg.rebalance.toolkit.PreferenceManager;
import com.garygregg.rebalance.toolkit.WeightType;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.logging.Level;

class Minimum extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the minimum dispatch.
     *
     * @param stream The output stream for messages
     */
    public Minimum(@NotNull PrintStream stream) {
        super(stream);
    }

    /**
     * Sets minimum running settings with expected values.
     */
    private static void setMinimum() {

        /*
         * Get the preference manager. Set the logging level for extraordinary
         * informational messages.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        manager.setExtraordinary(Level.INFO);

        /*
         * Set the expected annual inflation rate to the known average
         * inflation rate over time. Set the logging level.
         */
        manager.setInflation(3.22);
        manager.setLevel(Level.INFO);

        /*
         * Set the limit of reallocation iterations, the logging level for
         * ordinary informational messages, and the path for the data files.
         */
        manager.setLimit(1 << WeightType.getMaxChildren());
        manager.setOrdinary(Level.FINE);
        manager.setSource(Paths.get("data"));
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Throw a new CLA exception if the argument is not null. The minimum
         * option does not take an argument.
         */
        if (null != argument) {
            throw new CLAException(String.format("The minimum option does " +
                    "not take an argument; received %s.", argument));
        }

        // Null argument, as expected. Set the minimum preferences.
        setMinimum();
        printNoException(getKey().toString());
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.MINIMUM;
    }
}
