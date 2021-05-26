package com.garygregg.rebalance.qualifier;

public class OrConjunction<T> extends Conjunction<T> {

    /**
     * Constructs the 'or' conjunction.
     */
    public OrConjunction() {
        super(true);
    }
}
