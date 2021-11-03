package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.CurrencyReceiver;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

abstract class ReceiverDelegate<T extends CurrencyReceiver>
        implements CurrencyReceiver {

    // What is zero?
    private static final Currency zero = Currency.getZero();

    // The receiver from whom we are delegated
    private final T receiver;

    // The weight of the delegate
    private final double weight;

    // The last value that was commanded as set
    private MutableCurrency last;

    /**
     * Constructs the receiver delegate.
     *
     * @param receiver The receiver from whom we are delegated
     * @param weight   The weight of the delegate
     */
    protected ReceiverDelegate(@NotNull T receiver, double weight) {

        // Set the member variables.
        this.receiver = receiver;
        this.weight = weight;
    }

    /**
     * Adds proposed value to the receiver.
     *
     * @param currency The additional value to add
     */
    public void add(@NotNull Currency currency) {

        // Set the proposed value to zero if it is currently null.
        if (null == last) {
            last = new MutableCurrency(zero);
        }

        /*
         * Add the given value to the last value that was commanded set, and
         * pass the proposed value on to our receiver along with an indication
         * that it is *not* okay to take more.
         */
        last.add(currency);
        setProposed(last.getImmutable(), false);
    }

    /**
     * Gets the difference between the last value that was commanded as set
     * and the current value set in the receiver.
     *
     * @return Negative if the current value is greater than the last value,
     * zero if the values are the same, positive if the current value is less
     * than the last value
     */
    public MutableCurrency getDifference() {

        // Declare the result. Is the last value null?
        final MutableCurrency result;
        if (null == last) {

            // The last value is null, so the result is null too.
            result = null;
        }

        // The last value is not null.
        else {

            // Get the current value. Is the current value null?
            final Currency current = getProposed();
            if (null == current) {

                // The current value is null, so the result is null too.
                result = null;
            }

            // The last value is not null and the current value is not null.
            else {

                /*
                 * Create a new mutable result with the last value, and
                 * subtract the current value.
                 */
                result = new MutableCurrency(last);
                result.subtract(current);
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the weight of the delegate.
     *
     * @return The weight of the delegate
     */
    public MutableCurrency getLast() {
        return last;
    }

    /**
     * Gets the proposed value of the receiver.
     *
     * @return The proposed value of the receiver
     */
    public abstract Currency getProposed();

    /**
     * Gets the receiver from whom we are delegated.
     *
     * @return The receiver from whom we are delegated
     */
    protected @NotNull T getReceiver() {
        return receiver;
    }

    /**
     * Gets the weight of the delegate.
     *
     * @return The weight of the delegate
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Determines if there is no difference between the last value that was
     * commanded set and the current value set in the receiver.
     *
     * @return True if there is no difference between the last value that was
     * commanded set and the current value set in the receiver
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean noDifference() {
        return zero.equals(getDifference());
    }

    @Override
    public void setProposed(@NotNull Currency currency,
                            boolean okayToTakeMore) {
        getReceiver().setProposed(last.getImmutable(), okayToTakeMore);
    }

    /**
     * Gets the proposed value of the receiver.
     *
     * @param currency The proposed value of the receiver
     */
    public void setProposed(@NotNull Currency currency) {

        /*
         * Set the last value that was commanded set, and pass the proposed
         * value on to our receiver along with an indication that it is okay to
         * take more.
         */
        last = new MutableCurrency(currency);
        setProposed(last.getImmutable(), true);
    }
}
