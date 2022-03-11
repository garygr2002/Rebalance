package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

public enum TaxType {

    // All types
    ALL(CategoryType.ALL),

    /*
     * A credit (loan, or credit card balance); Question: Is credit card debt
     * taxable, or unknown tax category?
     */
    CREDIT(CategoryType.CREDIT),

    // Health Savings Account
    HSA(CategoryType.TAX_DEFERRED),

    // An inherited IRA
    INHERITED_IRA(CategoryType.TAX_DEFERRED),

    // A regular 401k
    NON_ROTH_401K(CategoryType.TAX_DEFERRED),

    // A regular annuity
    NON_ROTH_ANNUITY(CategoryType.TAX_DEFERRED),

    // A regular contributory IRA
    NON_ROTH_IRA(CategoryType.TAX_DEFERRED),

    // For containers that are not accounts
    NOT_AN_ACCOUNT(CategoryType.NOT_AN_ACCOUNT),

    // Social Security, or a pension plan
    PENSION(CategoryType.TAXABLE),

    // A real estate holding
    REAL_ESTATE(CategoryType.TAX_DEFERRED),

    // A Roth 401k
    ROTH_401K(CategoryType.TAX_PAID),

    // A Roth annuity
    ROTH_ANNUITY(CategoryType.TAX_PAID),

    // A Roth contributory IRA
    ROTH_IRA(CategoryType.TAX_PAID),

    // A taxable account
    TAXABLE(CategoryType.TAXABLE),

    /*
     * A treasury account; Question: are treasury securities taxable, or tax
     * deferred?
     */
    TREASURY(CategoryType.TAXABLE);

    // The parents for the weight type
    private final CategoryType category;

    /**
     * Constructs the weight type.
     *
     * @param category The category for the tax type
     */
    TaxType(@NotNull CategoryType category) {
        this.category = category;
    }

    /**
     * Gets the category for the tax type.
     *
     * @return The category for the tax type
     */
    public CategoryType getCategory() {
        return category;
    }
}
