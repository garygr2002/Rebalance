package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class FullValueBreakdownManager<HierarchyType extends Common<?, ?, ?>>
        extends BreakdownManager<WeightType, HierarchyType> {

    /**
     * Checks for null currency; returns zero if null, the argument itself if
     * not.
     *
     * @param currency The given currency
     * @return Zero if the argument is null, the argument itself if not
     */
    private static @NotNull Currency checkNull(Currency currency) {
        return (null == currency) ? Currency.getZero() : currency;
    }

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType>
    createByConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(checkNull(hierarchyObject.getConsidered()));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType>
    createByNotConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(checkNull(hierarchyObject.getNotConsidered()));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType>
    createByProposed() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(checkNull(hierarchyObject.getProposed()));
            }
        };
    }
}
