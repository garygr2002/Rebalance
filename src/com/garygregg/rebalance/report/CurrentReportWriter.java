package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.ValueByConsidered;
import org.jetbrains.annotations.NotNull;

public class CurrentReportWriter extends ReportWriter {

    /**
     * Constructs the current report writer.
     */
    public CurrentReportWriter() {
        super(ValueByConsidered.getInstance());
    }

    @Override
    protected @NotNull String getPrefix() {
        return "report";
    }
}
