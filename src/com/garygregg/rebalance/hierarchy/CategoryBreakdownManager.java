package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.toolkit.CategoryType;
import org.jetbrains.annotations.NotNull;

class CategoryBreakdownManager<HierarchyType extends Aggregate<?, ?, ?>>
        extends BreakdownManager<CategoryType, HierarchyType> {

    @Override
    protected @NotNull Breakdown<CategoryType, HierarchyType>
    createByConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(CategoryType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<CategoryType, HierarchyType>
    createByNotConsidered() {
        return new Breakdown<>() {

            @Override
            public void add(CategoryType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getNotConsidered(type));
            }
        };
    }

    @Override
    protected @NotNull Breakdown<CategoryType, HierarchyType>
    createByProposed() {
        return new Breakdown<>() {

            @Override
            public void add(CategoryType type,
                            @NotNull HierarchyType hierarchyObject) {
                getMutable(type).add(hierarchyObject.getProposed(type));
            }
        };
    }
}
