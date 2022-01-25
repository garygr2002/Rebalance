package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.TaxType;
import org.jetbrains.annotations.NotNull;

class TaxBreakdownManager<HierarchyType extends Aggregate<?, ?, ?>>
        extends BreakdownManager<TaxType, HierarchyType> {

    @Override
    protected @NotNull
    Breakdown<TaxType, HierarchyType> createByConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(TaxType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull
    Breakdown<TaxType, HierarchyType> createByNotConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(TaxType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getNotConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull
    Breakdown<TaxType, HierarchyType> createByProposed() {
        return new Breakdown<>() {

            @Override
            public void add(TaxType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getProposed(type));
            }
        };
    }
}
