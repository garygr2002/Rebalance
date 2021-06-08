package com.garygregg.rebalance.hierarchy;

import org.jetbrains.annotations.NotNull;

interface IBreakdown<EnumType extends Enum<EnumType>,
        HierarchyType extends Common<?, ?, ?>> {

    /**
     * Adds value from a hierarchy object.
     *
     * @param type            The type for which to add value
     * @param hierarchyObject A hierarchy object
     */
    void add(EnumType type, @NotNull HierarchyType hierarchyObject);

    /**
     * Clears the breakdown map.
     */
    void clear();
}
