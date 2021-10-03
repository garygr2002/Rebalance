package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByProposed;

public class ProposedReportWriter extends ReportWriter {

    /**
     * Constructs the proposed report writer.
     */
    public ProposedReportWriter() {
        super(ValueByProposed.getInstance());
    }
}
