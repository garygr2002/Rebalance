package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

abstract class HierarchyWriter extends ReportWriter {

    // The action to take for each ticker
    private final Action<Ticker> tickerAction =
            this::writeLines;

    // The action to take for each account
    private final Action<Account> accountAction =
            this::writeLines;

    // The action to take for each institution
    private final Action<Institution> institutionAction =
            this::writeLines;

    /**
     * Iterates over an iterable.
     *
     * @param writer   The file writer to receive the report lines
     * @param iterable The iterable containing typed objects
     * @param action   The action to perform on a typed object
     * @param <T>      Any arbitrary type used by the iterable
     * @throws IOException Indicates an I/O exception occurred
     */
    protected static <T> void iterate(@NotNull FileWriter writer,
                                      @NotNull Iterable<T> iterable,
                                      @NotNull Action<T> action)
            throws IOException {

        /*
         * Iterate over the iterable, and call the indicated action with the
         * first/next object.
         */
        for (T object : iterable) {
            action.doAction(writer, object);
        }
    }

    /**
     * An action to be done with a portfolio after cycling over its
     * institutions.
     *
     * @param writer    The file writer to receive the report lines
     * @param portfolio A portfolio on which to report
     * @throws IOException Indicates an I/O exception occurred
     */
    protected abstract void doPostCycle(@NotNull FileWriter writer,
                                        @NotNull Portfolio portfolio)
            throws IOException;

    /**
     * An action to be done with a portfolio prior to cycling over its
     * institutions.
     *
     * @param writer    The file writer to receive the report lines
     * @param portfolio A portfolio on which to report
     * @throws IOException Indicates an I/O exception occurred
     */
    protected abstract void doPreCycle(@NotNull FileWriter writer,
                                       @NotNull Portfolio portfolio)
            throws IOException;

    /**
     * An action to be done with an institution prior to cycling over its
     * accounts.
     *
     * @param writer      The file writer to receive the report lines
     * @param institution An institution on which to report
     * @throws IOException Indicates an I/O exception occurred
     */
    protected abstract void doPreCycle(@NotNull FileWriter writer,
                                       @NotNull Institution institution)
            throws IOException;

    /**
     * An action to be done with an account prior to cycling over its tickers.
     *
     * @param writer  The file writer to receive the report lines
     * @param account An account on which to report
     * @throws IOException Indicates an I/O exception occurred
     */
    protected abstract void doPreCycle(@NotNull FileWriter writer,
                                       @NotNull Account account)
            throws IOException;

    /**
     * Writes lines for an account.
     *
     * @param writer  The file writer to receive the report lines
     * @param account The account for which to write lines
     * @throws IOException Indicates an I/O exception occurred
     */
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Account account) throws IOException {

        /*
         * Do the pre-cycle action for the account, then cycle for each
         * ticker in the account.
         */
        doPreCycle(writer, account);
        iterate(writer, account.getChildren(), tickerAction);
    }

    /**
     * Writes lines for a ticker.
     *
     * @param writer The file writer to receive the report lines
     * @param ticker The ticker for which to write lines
     * @throws IOException Indicates an I/O exception occurred
     */
    protected abstract void writeLines(@NotNull FileWriter writer,
                                       @NotNull Ticker ticker)
            throws IOException;

    /**
     * Writes lines for an institution.
     *
     * @param writer      The file writer to receive the report lines
     * @param institution The institution for which to write lines
     * @throws IOException Indicates an I/O exception occurred
     */
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        /*
         * Do the pre-cycle action for the institution, then cycle for each
         * account in the institution.
         */
        doPreCycle(writer, institution);
        iterate(writer, institution.getChildren(), accountAction);
    }

    @Override
    public boolean writeLines(@NotNull Portfolio portfolio, Date date)
            throws IOException {

        /*
         * Create a file writer tailored to the portfolio key and the given
         * date.
         */
        final String key = portfolio.getKey();
        final FileWriter writer = getWriter(
                getDateUtilities().getTypeDirectory(), key, date);

        /*
         * Do the pre-cycle action for the portfolio, then cycle for each
         * institution in the portfolio. Do the post-cycle action.
         */
        doPreCycle(writer, portfolio);
        iterate(writer, portfolio.getChildren(), institutionAction);
        doPostCycle(writer, portfolio);

        // Close the file writer and return success to our caller.
        writer.close();
        return true;
    }

    protected interface Action<T> {

        /**
         * Performs an action.
         *
         * @param writer The file writer to receive the report lines
         * @param object The object on which to perform the action
         * @throws IOException Indicates an I/O exception occurred
         */
        void doAction(@NotNull FileWriter writer, @NotNull T object)
                throws IOException;
    }
}
