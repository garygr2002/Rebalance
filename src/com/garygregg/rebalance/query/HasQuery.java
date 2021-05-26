package com.garygregg.rebalance.query;

public abstract class HasQuery extends StartAtZeroQuery {

    // The summer for this class
    private static final Summer summer = new HasSummer();

    /**
     * Constructs the query.
     */
    public HasQuery() {
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
