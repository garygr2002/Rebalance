package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByProposed;
import org.jetbrains.annotations.NotNull;

public class ProposedReportWriter extends ReportWriter {

    /**
     * Constructs the proposed report writer.
     */
    public ProposedReportWriter() {
        super(ValueByProposed.getInstance());
    }

    @Override
    protected @NotNull String getPrefix() {
        return "proposed";
    }
}
