package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public class ValueByNotConsidered implements Valuator {

    // An instance of the value-by-not-considered valuator
    private static final Valuator instance = new ValueByNotConsidered();

    /**
     * Constructs the value-by-not-considered valuator.
     */
    private ValueByNotConsidered() {
    }

    /**
     * Gets an instance of the value-by-not-considered valuator.
     *
     * @return An instance of the value-by-not-considered valuator
     */
    public static Valuator getInstance() {
        return instance;
    }

    @Override
    public Currency getValue(@NotNull Queryable<?, ?> queryable) {
        return queryable.getNotConsidered();
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull CategoryType type) {
        return account.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull CategoryType type) {
        return institution.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull CategoryType type) {
        return portfolio.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull TaxType type) {
        return account.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull TaxType type) {
        return institution.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull TaxType type) {
        return portfolio.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Ticker ticker, @NotNull WeightType type) {
        return ticker.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Account account, @NotNull WeightType type) {
        return account.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Institution institution, @NotNull WeightType type) {
        return institution.getNotConsidered(type);
    }

    @Override
    public @NotNull Currency getValue(
            @NotNull Portfolio portfolio, @NotNull WeightType type) {
        return portfolio.getNotConsidered(type);
    }
}
