package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByConsidered;

public class CurrentReportWriter extends ReportWriter {

    /**
     * Constructs the current reports writer.
     */
    public CurrentReportWriter() {
        super(ValueByConsidered.getInstance());
    }
}
