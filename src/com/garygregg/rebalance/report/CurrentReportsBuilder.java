package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByConsidered;

public class CurrentReportsBuilder extends ReportsBuilder {

    /**
     * Constructs the current reports builder.
     */
    public CurrentReportsBuilder() {
        super(ValueByConsidered.getInstance());
    }
}
