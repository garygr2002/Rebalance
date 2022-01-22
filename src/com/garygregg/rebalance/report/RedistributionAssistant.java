package com.garygregg.rebalance.report;

import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

class RedistributionAssistant {

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    // An iterator retreating from tickers needing to donate the most value
    private final ListIterator<Pair<MutableCurrency, Ticker>> fromIterator;

    // An iterator advancing from tickers needing to accept the most value
    private final ListIterator<Pair<MutableCurrency, Ticker>> toIterator;

    /*
     * A ticker needing less proposed value, and the current value that it
     * needs to donate
     */
    private Pair<MutableCurrency, Ticker> from;

    /*
     * A ticker needing more proposed value, and the current value that it
     * needs to accept
     */
    private Pair<MutableCurrency, Ticker> to;

    /**
     * Constructs the redistribution assistant.
     *
     * @param list A list containing pairs: the difference between proposed and
     *             considered values, and the ticker having that difference
     */
    public RedistributionAssistant(
            @NotNull List<Pair<MutableCurrency, Ticker>> list) {

        /*
         * Create a copy of the given list and sort it from the lowest
         * difference to highest.
         */
        final List<Pair<MutableCurrency, Ticker>> localList =
                new ArrayList<>(list);
        localList.sort(Comparator.comparing(Pair::getFirst));

        /*
         * A precondition is that the given list has one element. Get an
         * iterator to the front of the list, and use it to get the first
         * pair.
         */
        fromIterator = localList.listIterator();
        from = fromIterator.next();

        /*
         * Get an iterator to the back of the list, and use it to get the last
         * pair.
         */
        toIterator = localList.listIterator(list.size());
        to = toIterator.previous();
    }

    /**
     * Gets the amount to transfer.
     *
     * @param first  The first amount
     * @param second The second amount
     * @return The minimum of the absolute values of the two arguments
     */
    private static @NotNull Currency min(@NotNull MutableCurrency first,
                                         @NotNull MutableCurrency second) {
        return new Currency(Math.min(Math.abs(first.getValue()),
                Math.abs(second.getValue())));
    }

    /**
     * Advances the front iterator (tickers needing to accept value).
     *
     * @return True if the iterator could be advanced, i.e., there are more
     * tickers
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean advance() {

        // Can the front iterator be advanced?
        final boolean result = fromIterator.hasNext();
        if (result) {

            // The front iterator can be advanced. Advance it.
            from = fromIterator.next();
        }

        // Return the result.
        return result;
    }

    /**
     * Determines if the current donor can donate currency.
     *
     * @return True if the current donor can donate currency; false otherwise
     */
    public boolean canDonate() {
        return (0 < to.getFirst().getImmutable().compareTo(zero));
    }

    /**
     * Determines if the current receiver can accept currency.
     *
     * @return True if the current receiver can accept currency; false
     * otherwise
     */
    public boolean canReceive() {
        return (0 > from.getFirst().getImmutable().compareTo(zero));
    }

    /**
     * Gets the difference from the current donor.
     *
     * @return The difference from the current donor
     */
    public @NotNull Currency getFrom() {
        return getFromDifference().getImmutable();
    }

    /**
     * Gets the difference from the current donor.
     *
     * @return The difference from the current donor
     */
    private @NotNull MutableCurrency getFromDifference() {
        return from.getFirst();
    }

    /**
     * Gets the current donor.
     *
     * @return The current donor
     */
    public @NotNull Ticker getFromTicker() {
        return from.getSecond();
    }

    /**
     * Gets the next transfer amount.
     *
     * @return The next transfer amount, or null if no amount can be
     * transferred
     */
    public Currency getNextTransfer() {

        /*
         * Declare and initialize the result. Can transfers be both received
         * and accepted?
         */
        Currency result = null;
        if (canReceive() && canDonate()) {

            /*
             * Transfers can be both received and accepted. Get the current
             * differences from both the receiver and the donor.
             */
            final MutableCurrency from = getFromDifference();
            final MutableCurrency to = getToDifference();

            /*
             * Determine the minimum of the two differences. Add the minimum to
             * the donor, and subtract it from the receiver.
             */
            result = min(from, to);
            from.add(result);
            to.subtract(result);

            /*
             * Retreat from the current donor if the current donor has nothing
             * left to transfer.
             */
            if (!canDonate()) {
                retreat();
            }

            /*
             * Advance from the current receiver if current receiver has all
             * that it needs.
             */
            if (!canReceive()) {
                advance();
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the difference from the current receiver.
     *
     * @return The difference from the current receiver
     */
    public @NotNull Currency getTo() {
        return getToDifference().getImmutable();
    }

    /**
     * Gets the difference from the current receiver.
     *
     * @return The difference from the current receiver
     */
    private @NotNull MutableCurrency getToDifference() {
        return to.getFirst();
    }

    /**
     * Gets the current receiver.
     *
     * @return The current receiver
     */
    public @NotNull Ticker getToTicker() {
        return to.getSecond();
    }

    /**
     * Retreats the back iterator (tickers needing to donate value).
     *
     * @return True if the iterator could be retreated, i.e., there are more
     * tickers
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean retreat() {

        // Can the back iterator be retreated?
        final boolean result = toIterator.hasPrevious();
        if (result) {

            // The back iterator can be retreated. Retreat it.
            to = toIterator.previous();
        }

        // Return the result.
        return result;
    }
}
