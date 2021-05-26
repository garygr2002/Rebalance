package com.garygregg.rebalance.code;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementProcessor;
import com.garygregg.rebalance.FundType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodesBuilder extends ElementProcessor {

    // The description processor
    private final FieldProcessor<CodeDescription> descriptionProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field,
                                         int lineNumber) {
                    getTarget().setDescription(
                            preprocessField(
                                    CodesBuilder.processDescription(field)));
                }
            };

    // The code library instance
    private final CodeLibrary library = CodeLibrary.getInstance();

    // The name processor
    private final FieldProcessor<CodeDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setName(preprocessField(
                            CodesBuilder.processName(field)));

                }
            };

    // The subcode processors
    private final MySubcodeProcessor[] subcodeProcessors =
            new MySubcodeProcessor[CodeDescription.getSubcodeCount()];

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(CodesBuilder.class.getCanonicalName()));

        // Initialize a field index, and add the code name processor.
        int fieldIndex = 0;
        addFieldProcessor(++fieldIndex, nameProcessor);

        /*
         * Declare and initialize a variable to hold a subcode processor. Cycle
         * for the number of subcodes.
         */
        MySubcodeProcessor subcodeProcessor;
        final int subcodeFieldCount = subcodeProcessors.length;
        for (int i = 0; i < subcodeFieldCount; ++i) {

            /*
             * Create a new subcode processor for the index. Set the index in
             * the processor, and add the processor.
             */
            subcodeProcessors[i] = subcodeProcessor = new MySubcodeProcessor();
            subcodeProcessor.setIndex(i);
            addFieldProcessor(++fieldIndex, subcodeProcessor);
        }

        // Lastly, add the description processor.
        addFieldProcessor(++fieldIndex, descriptionProcessor);
    }

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

            // Create an element processor. Read lines from the file object.
            final ElementProcessor processor = new CodesBuilder();
            processor.readLines();

            // The code library should now be populated. Print its date.
            final CodeLibrary library = CodeLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each code description in the library.
            for (CodeDescription description : library.getCatalog()) {

                // Display statistics for the first/next code description.
                System.out.printf("Code: %2s; " +
                                "Name: %20s; " +
                                "Subcode 1: %2s; Subcode 2: %2s;" +
                                "Subcode 3: %2s; Subcode 4: %2s; " +
                                "Subcode 5: %2s; Description: %s%n",
                        description.getCode(),
                        description.getName(),
                        description.getSubcode(0),
                        description.getSubcode(1),
                        description.getSubcode(2),
                        description.getSubcode(3),
                        description.getSubcode(4),
                        description.getDescription());
            }

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor completed %s warning " +
                            "or error.%n",
                    (processor.hadProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Processes a code.
     *
     * @param code The code
     * @return A processed code
     */
    private static @NotNull Character processCode(@NotNull String code) {
        return code.charAt(0);
    }

    /**
     * Processes a description.
     *
     * @param description The description to processes
     * @return A processed description
     */
    private static String processDescription(@NotNull String description) {
        return description.replace(";", ",");
    }

    /**
     * Processes a name.
     *
     * @param name The name to process
     * @return A processed name
     */
    private static String processName(@NotNull String name) {
        return name;
    }

    @Override
    public int getMinimumFields() {
        return 1;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "code";
    }

    @Override
    protected boolean processElements(@NotNull String[] elements, int lineNumber) {

        // Create a new code description with the code.
        final CodeDescription description = new CodeDescription(
                processCode(preprocessField(
                        elements[CodeFields.CODE.getPosition()])));

        /*
         * Check the key of the description against the default key in the
         * library. Try to add the new code description, receiving any
         * existing description with the same key.
         */
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate code description
             * occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing " +
                            "description with code '%s' at line number %d " +
                            "in code file.", description.getCode(),
                    lineNumber));
        }

        /*
         * Set the target in the field processors. Get the number of line
         * elements and the number of code fields.
         */
        setTarget(description);
        final int elementsLength = elements.length;
        final int numberOfCodeFields = CodeFields.values().length;

        /*
         * The fields-to-process is the minimum of the number of line elements
         * and the number of code fields.
         */
        final int fieldsToProcess = Math.min(elementsLength,
                numberOfCodeFields);

        /*
         * Log a warning if the fields-to-process is less than the number of
         * line elements.
         */
        if (fieldsToProcess < elementsLength) {
            logMessage(Level.WARNING, String.format("There are %d code line " +
                            "elements but only %d code fields at line number " +
                            "%d; you might want to check that.",
                    elementsLength, numberOfCodeFields, lineNumber));
        }

        /*
         * Or log a different warning if the fields-to-process is less than the
         * number of codes fields.
         */
        else if (fieldsToProcess < numberOfCodeFields) {
            logMessage(Level.WARNING, String.format("There are %d code " +
                            "fields but only %d code line elements at line " +
                            "number %d; you might want to check that.",
                    numberOfCodeFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining field-to-process.
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {

            // Process the first/next field.
            processField(i, elements[i], lineNumber);
        }

        // Log some information and return the result.
        logMessage(getOrdinary(), String.format("Successfully loaded " +
                        "metadata for code '%c' at line %d.",
                description.getCode(), lineNumber));
        return true;
    }

    /**
     * Processes a subcode.
     *
     * @param subcode The subcode to process
     * @return A processed subcode
     */
    private Character processSubcode(@NotNull String subcode) {
        return processCode(preprocessField(subcode));
    }

    /**
     * Sets the fund type in a code description.
     *
     * @param description The code description to set
     * @param type        The fund type to set in the code description
     */
    private void setFundType(@NotNull CodeDescription description,
                             FundType type) {

        // Is the fund type already set in the fund description?
        if (description.isFundTypeSet()) {

            /*
             * The fund type is already set. This should not be true. Log
             * a warning.
             */
            logMessage(Level.WARNING, String.format("Trying to set fund " +
                            "type %s for code description '%c'; discovered " +
                            "it was already set to %s.",
                    type.toString(), description.getCode(),
                    description.getCode()));
        } else {

            /*
             * The fund type is not already set. All is well. Log an
             * informational message, and set the fund type in the
             * description.
             */
            logMessage(getOrdinary(), String.format("Setting fund type %s " +
                            "for code description '%c'.", type.toString(),
                    description.getCode()));
            description.setType(type);
        }
    }

    /**
     * Sets the target in the field processors.
     *
     * @param description The target to set in the field processors
     */
    private void setTarget(@NotNull CodeDescription description) {

        // Cycle for the number of subcodes.
        final int subcodeFieldCount = CodeDescription.getSubcodeCount();
        for (int i = 0; i < subcodeFieldCount; ++i) {

            // Set the target in the first/next subcode processor.
            subcodeProcessors[i].setTarget(description);
        }

        // Set the target in the description and name processors.
        descriptionProcessor.setTarget(description);
        nameProcessor.setTarget(description);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }

    @Override
    protected void stopProcessing() {

        /*
         * Cycle for each fund type, and try to set the corresponding type for
         * each code description that has a fund type.
         */
        CodeDescription description;
        for (FundType type : FundType.values()) {

            // Get the code description. Is the description not null?
            description = library.getDescription(type.getCode());
            if (null != description) {

                // The description is not null. Set its type.
                setFundType(description, type);
            }
        }

        // Now cycle for each code description in the catalog.
        for (CodeDescription cd : library.getCatalog()) {

            /*
             * Based on the above logic, the fund type is not yet set if the
             * code does not correspond to a fund type.
             */
            if (!cd.isFundTypeSet()) {

                /*
                 * Log an information al message, and set the fund type of
                 * the code description to null.
                 */
                logMessage(getOrdinary(), String.format("Setting null fund " +
                        "type for code description '%c'.", cd.getCode()));
                cd.setType(null);
            }
        }

        // Call the superclass method.
        super.stopProcessing();
    }

    /**
     * A subcode processor for CodeDescription targets.
     */
    private class MySubcodeProcessor extends
            FieldProcessor<CodeDescription> {

        // The index for subcode fields
        private Integer index;

        /**
         * Builds the processor.
         *
         * @param index An index for the subcode field
         */
        public MySubcodeProcessor(Integer index) {
            setIndex(index);
        }

        /**
         * Builds the processor with a default subcode field index.
         */
        public MySubcodeProcessor() {
            this(null);
        }

        /**
         * Gets the index for subcode fields.
         *
         * @return The index for subcode fields
         */
        public Integer getIndex() {
            return index;
        }

        @Override
        public void processField(@NotNull String field, int lineNumber) {
            getTarget().setSubcode(CodesBuilder.this.processSubcode(field),
                    getIndex());
        }

        /**
         * Sets the index for subcode fields.
         *
         * @param index The index for subcode fields
         */
        public void setIndex(Integer index) {
            this.index = index;
        }
    }
}
