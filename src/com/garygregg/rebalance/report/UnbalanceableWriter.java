package com.garygregg.rebalance.report;

import com.garygregg.rebalance.hierarchy.Portfolio;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;

class UnbalanceableWriter {

    // The recipient for writer output
    private final FileWriter writer;

    /**
     * Constructs the unbalance-able writer.
     *
     * @param writer The recipient for writer output
     */
    public UnbalanceableWriter(@NotNull FileWriter writer) {
        this.writer = writer;
    }

    /**
     * Gets the recipient for writer output.
     *
     * @return The recipient for writer output
     */
    private FileWriter getWriter() {
        return writer;
    }

    /**
     * Writes a portfolio summary.
     *
     * @param portfolio A portfolio that is the subject of the summary
     * @return True if the summary was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean writeSummary(@NotNull Portfolio portfolio)
            throws IOException {

        /*
         * Write a temporary message, and return to caller.
         *
         * TODO: Fill in this method some more.
         */
        getWriter().write("You need to code the unbalance-able writer, " +
                "okay?\n");
        return true;
    }
}
