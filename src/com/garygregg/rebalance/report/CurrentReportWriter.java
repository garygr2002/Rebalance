package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByConsidered;

public class CurrentReportWriter extends ReportWriter {

    /**
     * Constructs the current report writer.
     */
    public CurrentReportWriter() {
        super(ValueByConsidered.getInstance());
    }
}
