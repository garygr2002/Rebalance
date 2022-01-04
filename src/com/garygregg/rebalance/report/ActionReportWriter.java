package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

public class ActionReportWriter extends HierarchyWriter {

    // The common (but temporary) format string
    private static final String format = "Writing about %s '%s'.\n";

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Portfolio portfolio)
            throws IOException {

        // TODO: Write about the portfolio.
        writer.write(String.format(format, "portfolio", portfolio.getKey()));
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // TODO: Write about the institution.
        writer.write(String.format(format, "institution",
                institution.getKey()));
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        // TODO: Write about the account.
        writer.write(String.format(format, "account", account.getKey()));
    }

    @Override
    protected @NotNull String getPrefix() {
        return "action";
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // Write two newlines then call the superclass method.
        writer.write("\n\n");
        super.writeLines(writer, institution);
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        // Write a newline then call the superclass method.
        writer.write("\n");
        super.writeLines(writer, account);
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Ticker ticker)
            throws IOException {

        // TODO: Write about the ticker.
        writer.write(String.format(format, "ticker", ticker.getKey()));
    }
}
