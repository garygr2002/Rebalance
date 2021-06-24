package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByProposed;

public class ProposedReportWriter extends ReportWriter {

    /**
     * Constructs the proposed reports writer.
     */
    public ProposedReportWriter() {
        super(ValueByProposed.getInstance());
    }
}
