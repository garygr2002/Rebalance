package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.toolkit.CategoryType;
import com.garygregg.rebalance.toolkit.FundType;
import com.garygregg.rebalance.toolkit.TaxType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Queryable<KeyType, ChildType extends Queryable<?, ?>> {

    /**
     * Gets the collection of the children of this queryable.
     *
     * @return The collection of the children of this queryable (or null for a
     * queryable implementer that does not have children)
     */
    Collection<ChildType> getChildren();

    /**
     * Gets the value of the queryable that can be considered for rebalance.
     *
     * @return The value of the queryable that can be considered for rebalance
     */
    Currency getConsidered();

    /**
     * Gets the key of the queryable.
     *
     * @return The key of the queryable
     */
    @NotNull KeyType getKey();

    /**
     * Gets the value of the queryable that cannot be considered for rebalance.
     *
     * @return The value of the queryable that cannot be considered for
     * rebalance
     */
    Currency getNotConsidered();

    /**
     * Gets the proposed value of the queryable.
     *
     * @return The proposed value of the queryable, relative to the value of
     * the queryable that is considered for rebalance
     */
    Currency getProposed();

    /**
     * Determines if the queryable contains the indicated category type.
     *
     * @param type The indicated category type
     * @return True if the queryable contains the indicated category type,
     * false otherwise
     */
    boolean hasCategoryType(@NotNull CategoryType type);

    /**
     * Determines if the queryable contains the indicated fund type.
     *
     * @param type The indicated fund type
     * @return True if the queryable contains the indicated fund type, false
     * otherwise
     */
    boolean hasFundType(@NotNull FundType type);

    /**
     * Determines if the queryable contains the indicated tax type.
     *
     * @param type The indicated tax type
     * @return True if the queryable contains the indicated tax type, false
     * otherwise
     */
    boolean hasTaxType(@NotNull TaxType type);
}
