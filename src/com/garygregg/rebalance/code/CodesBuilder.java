package com.garygregg.rebalance.code;

import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.interpreter.CodeInterpreter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CodesBuilder extends ElementReader<CodeDescription> {

    // The description processor
    private final FieldProcessor<CodeDescription> descriptionProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
                    getTarget().setDescription(
                            CodesBuilder.processDescription(field));
                }
            };

    // Our code interpreter
    private final CodeInterpreter interpreter = new CodeInterpreter();

    // The code library instance
    private final CodeLibrary library = CodeLibrary.getInstance();

    // The name processor
    private final FieldProcessor<CodeDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
                    getTarget().setName(field);

                }
            };

    // The subcode processors
    private final MySubcodeProcessor[] subcodeProcessors =
            new MySubcodeProcessor[CodeDescription.getSubcodeCount()];

    {

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
     * Processes a description.
     *
     * @param description The description to processes
     * @return A processed description
     */
    @Contract(pure = true)
    private static @NotNull String processDescription(@NotNull String description) {
        return description.replace(";", ",");
    }

    @Override
    public int getMinimumFields() {
        return 1;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "code";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(CodesBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String @NotNull [] elements,
                                   int lineNumber) {

        /*
         * Set the line number, get the code, and create a new code description
         * with the interpreted code.
         */
        setLineNumber(lineNumber);
        final CodeDescription description = new CodeDescription(
                interpreter.interpret(
                        elements[CodeFields.CODE.getPosition()]));

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
            logMessage(getInformationLevel(), String.format("Replacing " +
                            "description with code '%s' at line number %d " +
                            "in the code file.", description.getCode(),
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
            processField(i, elements[i]);
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "code '%c' at line %d was%s successful.",
                description.getCode(), lineNumber,
                hadLineProblem() ? " not" : ""));
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
        }

        // The fund type is not already set in the fund description.
        else {

            /*
             * Log an informational message, and set the fund type in the
             * description.
             */
            logMessage(getOrdinary(), String.format("Setting fund type %s " +
                            "for code description '%c'.", type.toString(),
                    description.getCode()));
            description.setType(type);
        }
    }

    @Override
    protected void setLineNumber(int lineNumber) {
        interpreter.setRow(lineNumber);
    }

    @Override
    protected void setTarget(@NotNull CodeDescription description) {

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
    private static class MySubcodeProcessor extends
            FieldProcessor<CodeDescription> {

        // Our code interpreter
        private final CodeInterpreter interpreter = new CodeInterpreter();

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
        public void processField(@NotNull String field) {

            /*
             * Interpret the field as a code, and set the code as a sub-code in
             * the target.
             */
            getTarget().setSubcode(interpreter.interpret(field), getIndex());
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
