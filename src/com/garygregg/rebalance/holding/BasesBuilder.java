package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.HoldingKey;
import com.garygregg.rebalance.HoldingType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class BasesBuilder extends HoldingsBuilder {

    /**
     * Tests this class.
     *
     * @param arguments Command line arguments
     */
    public static void main(String[] arguments) {

        /*
         * TODO: Delete this method.
         */
        try {

            /*
             * First perform initialization. Create an element processor. Read
             * lines from the file object.
             */
            HoldingsBuilder.initialize();
            final ElementReader<?> processor = new BasesBuilder();
            processor.readLines();

            // Get the basis library.
            final HoldingLibrary library =
                    HoldingLibrary.getInstance(HoldingType.BASIS);

            // The basis library should now be populated. Print its date.
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each holding description in the library.
            HoldingKey key;
            for (HoldingDescription description : library.getCatalog()) {

                // Display statistics for the first/next holding description.
                key = description.getHoldingParentChild();
                System.out.printf("Line Code: %-12s; " +
                                "Parent: %-32s; " +
                                "Child: %-32s; " +
                                "Name: %-45s; " +
                                "Shares: %-15s; " +
                                "Price: %-15s; " +
                                "Value: %-15s%n",
                        description.getLineType(),
                        getParent(description), key.getSecond(),
                        description.getName(),
                        description.getShares(),
                        description.getPrice(),
                        description.getValue());
            }

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor " +
                            "completed %s warning or error.%n",
                    (processor.hadFileProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    @Override
    protected @NotNull HoldingType getHoldingType() {
        return HoldingType.BASIS;
    }
}
