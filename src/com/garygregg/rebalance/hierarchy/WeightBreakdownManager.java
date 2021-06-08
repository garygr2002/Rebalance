package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.WeightType;
import org.jetbrains.annotations.NotNull;

class WeightBreakdownManager<HierarchyType extends Common<?, ?, ?>>
        extends BreakdownManager<WeightType, HierarchyType> {

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType> createByConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType> createByNotConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getNotConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<WeightType, HierarchyType> createByProposed() {
        return new Breakdown<>() {

            @Override
            public void add(WeightType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getProposed(type));
            }
        };
    }
}
