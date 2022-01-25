package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public final class ValueByConsidered implements Valuator {

    // An instance of the value-by-considered valuator
    private static final Valuator instance = new ValueByConsidered();

    /**
     * Constructs the value-by-considered valuator.
     */
    private ValueByConsidered() {
    }

    /**
     * Gets an instance of the value-by-considered valuator.
     *
     * @return An instance of the value-by-considered valuator
     */
    public static Valuator getInstance() {
        return instance;
    }

    @Override
    public Currency getValue(@NotNull Queryable<?, ?> queryable) {
        return queryable.getConsidered();
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull CategoryType type) {
        return account.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull CategoryType type) {
        return institution.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull CategoryType type) {
        return portfolio.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull TaxType type) {
        return account.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull TaxType type) {
        return institution.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull TaxType type) {
        return portfolio.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Ticker ticker, @NotNull WeightType type) {
        return ticker.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull WeightType type) {
        return account.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull WeightType type) {
        return institution.getConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull WeightType type) {
        return portfolio.getConsidered(type);
    }
}
