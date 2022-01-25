package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public class ValueByProposed implements Valuator {

    // An instance of the value-by-proposed valuator
    private static final Valuator instance = new ValueByProposed();

    /**
     * Constructs the value-by-proposed valuator.
     */
    private ValueByProposed() {
    }

    /**
     * Gets an instance of the value-by-proposed valuator.
     *
     * @return An instance of the value-by-proposed valuator
     */
    public static Valuator getInstance() {
        return instance;
    }

    @Override
    public Currency getValue(@NotNull Queryable<?, ?> queryable) {
        return queryable.getProposed();
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull CategoryType type) {
        return account.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull CategoryType type) {
        return institution.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull CategoryType type) {
        return portfolio.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull TaxType type) {
        return account.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull TaxType type) {
        return institution.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull TaxType type) {
        return portfolio.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Ticker ticker, @NotNull WeightType type) {
        return ticker.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull WeightType type) {
        return account.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull WeightType type) {
        return institution.getProposed(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull WeightType type) {
        return portfolio.getProposed(type);
    }
}
