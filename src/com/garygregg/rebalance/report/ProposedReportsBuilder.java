package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByProposed;

public class ProposedReportsBuilder extends ReportsBuilder {

    /**
     * Constructs the proposed reports builder.
     */
    public ProposedReportsBuilder() {
        super(new ValueByProposed());
    }
}
