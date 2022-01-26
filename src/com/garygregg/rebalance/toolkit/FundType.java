package com.garygregg.rebalance.toolkit;

public enum FundType {

    // All bonds
    BOND('B'),

    // Cash and near cash
    CASH('C'),

    // Corporate bonds
    CORPORATE('T'),

    // Domestic instruments
    DOMESTIC('D'),

    // Foreign bonds
    FOREIGN('O'),

    // Growth stocks
    GROWTH('G'),

    // Growth & value stocks
    GROWTH_AND_VALUE('K'),

    // High-yield bonds
    HIGH('Y'),

    // Inflation protected securities
    INFLATION('H'),

    // Large cap stocks
    LARGE('L'),

    // Medium cap stocks
    MEDIUM('M'),

    // Mortgage backed securities
    MORTGAGE('E'),

    // The new flair-out growth project
    NEW_FLAIR_OUT_GROWTH('P'),

    // For containers that are not ETFs or funds
    NOT_A_FUND('_'),

    // Not-large stocks (medium & small)
    NOT_LARGE('N'),

    // Real estate
    REAL_ESTATE('R'),

    // Short term bonds
    SHORT('Z'),

    // Small cap stocks
    SMALL('W'),

    // All stocks
    STOCK('S'),

    // Treasury bonds
    TREASURY('U'),

    // Value stocks
    VALUE('V');

    // The code associated with the fund type
    private final Character code;

    /**
     * Constructs the fund type.
     *
     * @param code The code associated with the fund type
     */
    FundType(Character code) {
        this.code = (null == code) ? null : Character.toUpperCase(code);
    }

    /**
     * Gets the code associated with the fund type.
     *
     * @return The code associated with the fund type
     */
    public Character getCode() {
        return code;
    }
}
