package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.CurrencyReceiver;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

abstract class ReceiverDelegate<T extends CurrencyReceiver> implements
        CurrencyReceiver {

    // The receiver from whom we are delegated
    private final T receiver;

    // The weight of the delegate
    private final double weight;

    // Consider this delegate to receive additional value
    private boolean considerMe;

    // The last difference between what was desired and what was actually set
    private Currency lastResidual = Currency.getZero();

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

    @Override
    public void clearSnapshot(@NotNull SnapshotType type) {
        getReceiver().clearSnapshot(type);
    }

    @Override
    public void clearSnapshots() {
        getReceiver().clearSnapshots();
    }

    @Override
    public @NotNull Currency getCurrent() {
        return getReceiver().getCurrent();
    }

    /**
     * Gets the last residual.
     *
     * @return The last residual
     */
    public @NotNull Currency getLastResidual() {
        return lastResidual;
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
     * Determines if this delegate is to be considered to receive additional
     * value.
     *
     * @return True if this delegate is to be considered to receive additional
     * value; false otherwise
     */
    public boolean isConsidered() {
        return considerMe;
    }

    /**
     * Performs an action if explicit value cannot be set in the receiver
     * delegate.
     */
    public void onCannotSet() {

        // The default is to do nothing.
    }

    @Override
    public void recoverSnapshot(@NotNull SnapshotType type) {
        getReceiver().recoverSnapshot(type);
    }

    /**
     * Sets whether this delegate is to be considered to receive additional
     * value.
     *
     * @param considerMe True if this delegate is to be considered to receive
     *                   additional value; false otherwise
     */
    public void setConsidered(boolean considerMe) {
        this.considerMe = considerMe;
    }

    /**
     * Sets the last residual.
     *
     * @param lastResidual The last residual
     * @return The last residual that was set
     */
    private @NotNull Currency setLastResidual(@NotNull Currency lastResidual) {

        // Set the last residual, then return the residual that was set.
        this.lastResidual = lastResidual;
        return getLastResidual();
    }

    @Override
    public @NotNull Currency setProposed(@NotNull Currency currency,
                                         boolean isRelative) {

        /*
         * Set the last residual received from setting the proposed value in
         * the receiver.
         */
        return setLastResidual(getReceiver().setProposed(
                currency, isRelative));
    }

    @Override
    public void takeSnapshot(@NotNull SnapshotType type) {
        getReceiver().takeSnapshot(type);
    }
}
