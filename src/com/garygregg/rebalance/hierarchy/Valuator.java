package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.toolkit.CategoryType;
import com.garygregg.rebalance.toolkit.TaxType;
import com.garygregg.rebalance.toolkit.WeightType;
import org.jetbrains.annotations.NotNull;

public interface Valuator {

    /**
     * Gets the value of a queryable.
     *
     * @param queryable A queryable
     * @return The value of a queryable
     */
    Currency getValue(@NotNull Queryable<?, ?> queryable);

    /**
     * Gets the value of an account by category type.
     *
     * @param account An account
     * @param type    The category type
     * @return The value of an account by category type
     */
    @NotNull Currency getValue(@NotNull Account account,
                               @NotNull CategoryType type);

    /**
     * Gets the value of an institution by category type.
     *
     * @param institution An institution
     * @param type        The category type
     * @return The value of an institution by category type
     */
    @NotNull Currency getValue(@NotNull Institution institution,
                               @NotNull CategoryType type);

    /**
     * Gets the value of a portfolio by category type.
     *
     * @param portfolio A portfolio
     * @param type      The category type
     * @return The value of a portfolio by category type
     */
    @NotNull Currency getValue(@NotNull Portfolio portfolio,
                               @NotNull CategoryType type);

    /**
     * Gets the value of an account by tax type.
     *
     * @param account An account
     * @param type    The tax type
     * @return The value of an account by tax type
     */
    @NotNull Currency getValue(@NotNull Account account,
                               @NotNull TaxType type);

    /**
     * Gets the value of an institution by tax type.
     *
     * @param institution An institution
     * @param type        The tax type
     * @return The value of an institution by tax type
     */
    @NotNull Currency getValue(@NotNull Institution institution,
                               @NotNull TaxType type);

    /**
     * Gets the value of a portfolio by tax type.
     *
     * @param portfolio A portfolio
     * @param type      The tax type
     * @return The value of a portfolio by tax type
     */
    @NotNull Currency getValue(@NotNull Portfolio portfolio,
                               @NotNull TaxType type);

    /**
     * Gets the value of a ticker by weight type.
     *
     * @param ticker A ticker
     * @param type   The weight type
     * @return The value of a ticker by weight type
     */
    @NotNull Currency getValue(@NotNull Ticker ticker,
                               @NotNull WeightType type);

    /**
     * Gets the value of an account by weight type.
     *
     * @param account An account
     * @param type    The weight type
     * @return The value of an account by weight type
     */
    @NotNull Currency getValue(@NotNull Account account,
                               @NotNull WeightType type);

    /**
     * Gets the value of an institution by weight type.
     *
     * @param institution An institution
     * @param type        The weight type
     * @return The value of an institution by weight type
     */
    @NotNull Currency getValue(@NotNull Institution institution,
                               @NotNull WeightType type);

    /**
     * Gets the value of a portfolio by weight type.
     *
     * @param portfolio A portfolio
     * @param type      The weight type
     * @return The value of a portfolio by weight type
     */
    @NotNull Currency getValue(@NotNull Portfolio portfolio,
                               @NotNull WeightType type);
}
