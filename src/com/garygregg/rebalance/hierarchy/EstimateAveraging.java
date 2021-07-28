package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.distinguished.DistinguishedAccountLibrary;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class EstimateAveraging extends Synthesizer {

    // Value by 'considered'
    private final static Valuator byConsidered =
            ValueByConsidered.getInstance();

    // Value by 'not considered'
    private final static Valuator byNotConsidered =
            ValueByNotConsidered.getInstance();

    /**
     * Sums the value of a collection of hierarchy objects.
     *
     * @param hierarchyObjects A collection of hierarchy objects
     * @param valuator         A valuator for hierarchy objects
     * @return The sum of the values in the collection
     */
    private static double sum(@NotNull Collection<Common<?, ?, ?>> hierarchyObjects,
                              @NotNull Valuator valuator) {

        // Declare and initialize the sum. Are there any hierarchy objects?
        double value = 0.;
        if (!hierarchyObjects.isEmpty()) {

            /*
             * There are hierarchy objects. Declare and initialize mutable
             * currency initialized with the empty sum. Cycle for each
             * hierarchy object.
             */
            final MutableCurrency currency = new MutableCurrency(value);
            for (Common<?, ?, ?> hierarchyObject : hierarchyObjects) {

                /*
                 * Subtract the value of the first/next hierarchy object from
                 * the sum.
                 */
                currency.subtract(valuator.getValue(hierarchyObject));
            }

            /*
             * Get the summed value, and divide by the number of hierarchy
             * objects.
             */
            value = currency.getValue() / hierarchyObjects.size();
        }

        // Return the sum.
        return value;
    }

    @Override
    public @NotNull DistinguishedAccounts getAccount() {
        return DistinguishedAccounts.ESTIMATE_AVERAGING;
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // Call the superclass method. Was this successful?
        boolean result = super.synthesize(account);
        if (result) {

            /*
             * Calling the superclass method was successful. Get the
             * distinguished account library.
             */
            final DistinguishedAccountLibrary library =
                    DistinguishedAccountLibrary.getInstance();

            /*
             * Get the known estimate account keys from the distinguished
             * account library.
             */
            final AccountKey[] estimateKeys = {
                    library.getValue(DistinguishedAccounts.HOUSE_ESTIMATE_1),
                    library.getValue(DistinguishedAccounts.HOUSE_ESTIMATE_2)
            };

            /*
             * Declare a variable to receive an estimate. Get a hierarchy
             * instance and the message logger.
             */
            Account estimate;
            final Hierarchy hierarchy = Hierarchy.getInstance();
            final MessageLogger logger = getLogger();

            /*
             * Declare a list to receive house price estimates. Cycle for each
             * estimate account key.
             */
            final List<Common<?, ?, ?>> estimateList = new ArrayList<>();
            for (AccountKey estimateKey : estimateKeys) {

                /*
                 * Get the estimate for the first/next key. Is the estimate
                 * null?
                 */
                estimate = hierarchy.getAccount(estimateKey);
                if (null == estimate) {

                    // The estimate is null. Log a warning.
                    logger.logMessage(Level.WARNING, String.format("Unable " +
                            "to retrieve house estimate with key '%s'; its " +
                            "account is missing.", estimateKey));
                }

                // The estimate is not null. Add it to the account list.
                else {
                    estimateList.add(estimate);
                }
            }

            /*
             * Set the 'considered' and 'not considered' values in the target
             * account by average the estimates. Re-initialize the result.
             */
            account.setConsidered(sum(estimateList, byConsidered));
            account.setNotConsidered(sum(estimateList, byNotConsidered));
            result = !logger.hadProblem1();
        }

        /*
         * TODO:
         *
         * Take into account capital gains tax by subtracting the house basis
         * account value from the valuation calculation (above), and applying
         * a suitable capital gains tax rate.
         */

        // Return the result.
        return result;
    }
}
