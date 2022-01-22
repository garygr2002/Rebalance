package com.garygregg.rebalance.report;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.ValueByProposed;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

public class ProposedReportWriter extends ReportWriter {

    /**
     * Constructs the proposed report writer.
     */
    public ProposedReportWriter() {
        super(ValueByProposed.getInstance());
    }

    /**
     * Writes institution-specific information between the balanceable and
     * unbalanceable sections of the report.
     *
     * @param writer      The recipient for writer output
     * @param institution An institution
     * @return The number of accounts in the institution that have non-zero
     * rebalance residuals
     * @throws IOException Indicates an I/O exception occurred
     */
    private static int writeInstitution(@NotNull FileWriter writer,
                                        @NotNull Institution institution)
            throws IOException {

        // Declare local variables.
        AccountKey key;
        Currency residual;

        /*
         * Declare and initialize the number of accounts with non-zero
         * rebalance residuals. Cycle for each account in the given
         * institution.
         */
        int accountsWithNonZeroResiduals = 0;
        for (Account account : institution.getChildren()) {

            /*
             * Get the key of the account and its rebalance residual. Is the
             * rebalance residual for the first/next account null?
             */
            key = account.getKey();
            residual = account.getResidual();
            if (null == residual) {

                /*
                 * The rebalance residual for the first/next account is
                 * null. Increment the count of accounts with non-zero
                 * residuals, and write a line to the report describing the
                 * account and its null residual.
                 */
                ++accountsWithNonZeroResiduals;
                writer.write(String.format("WARNING! Account %s of " +
                                "institution %s has a null rebalance " +
                                "residual!\n",
                        AccountKey.format(key.getSecond()), key.getFirst()));
            }

            /*
             * The rebalance residual for the first/next account is not null,
             * but is it non-zero?
             */
            else if (residual.isNotZero()) {

                /*
                 * The rebalance residual for the first/next account is not
                 * null, but is non-zero. Increment the count of accounts with
                 * non-zero residuals, and write a line to the report
                 * describing the account and the amount of its non-zero
                 * residual.
                 */
                ++accountsWithNonZeroResiduals;
                writer.write(String.format("WARNING! Account %s of " +
                                "institution %s has a rebalance residual of " +
                                "%s!\n", AccountKey.format(key.getSecond()),
                        key.getFirst(), residual));
            }
        }

        // Return the number of accounts with non-zero residuals.
        return accountsWithNonZeroResiduals;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "proposed";
    }

    @Override
    protected boolean writeBetween(@NotNull FileWriter writer,
                                   @NotNull Portfolio portfolio)
            throws IOException {

        /*
         * Call the superclass method, receiving its result to return. Write a
         * newline.
         */
        final boolean result = super.writeBetween(writer, portfolio);
        writer.write("\n");

        /*
         * Declare and initialize the number of accounts with non-zero
         * rebalance residuals. Cycle for each institution in the given
         * portfolio.
         */
        int accountsWithNonZeroResiduals = 0;
        for (Institution institution : portfolio.getChildren()) {

            /*
             * Write institution-specific information for the first/next
             * institution, incrementing the number of accounts with non-zero
             * rebalance residuals as we go.
             */
            accountsWithNonZeroResiduals += writeInstitution(writer,
                    institution);
        }

        /*
         * Write a line describing a condition of no accounts with non-zero
         * rebalance residuals.
         */
        if (0 == accountsWithNonZeroResiduals) {
            writer.write("There are no accounts with rebalance residuals; " +
                    "good news!\n");
        }

        /*
         * Write a line describing a condition of one or more accounts with
         * non-zero rebalance residuals.
         */
        else {
            writer.write(String.format("\nThere are %s accounts with " +
                    "non-zero rebalance residuals; sorry about " +
                    "that.\n", accountsWithNonZeroResiduals));
        }

        // Declare the return value received from the superclass.
        return result;
    }
}
