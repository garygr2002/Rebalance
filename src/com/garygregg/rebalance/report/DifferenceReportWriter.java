package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.ValueByProposed;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DifferenceReportWriter extends ReportWriter {

    /**
     * Constructs the difference report writer.
     */
    public DifferenceReportWriter() {
        super(ValueByProposed.getInstance());
    }

    @Override
    protected @NotNull String getPrefix() {
        return "difference";
    }

    @Override
    public boolean writeLines(@NotNull Portfolio portfolio,
                              Date date)
            throws IOException {

        /*
         * Create a file writer tailored to the portfolio key and the given
         * date.
         */
        final FileWriter fileWriter = getWriter(
                getDateUtilities().getTypeDirectory(),
                portfolio.getKey(), date);

        // Write a test line. TODO: Change this.
        fileWriter.write("This is a test line; delete it!\n");

        /*
         * Close the file writer and return the result to our caller.
         *
         * TODO: Do not forget to change the return value!
         */
        fileWriter.close();
        return true;
    }
}
