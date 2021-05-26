package com.garygregg.rebalance.query;

public abstract class ShouldHaveQuery extends StartAtZeroQuery {

    // The summer for this class
    private static final Summer summer = new ShouldHaveSummer();

    /**
     * Constructs the query.
     */
    public ShouldHaveQuery() {
        super(getSummer());
    }

    /**
     * Gets the summer for this class.
     *
     * @return The summer for this class
     */
    public static Summer getSummer() {
        return summer;
    }
}
