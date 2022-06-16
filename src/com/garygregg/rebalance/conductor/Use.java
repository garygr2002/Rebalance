package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.PreferenceDispatch;
import com.garygregg.rebalance.toolkit.CommandLineId;
import com.garygregg.rebalance.toolkit.PreferenceManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

class Use extends PreferenceDispatch<CommandLineId> {

    /**
     * Constructs the use dispatch.
     *
     * @param preferences The preferences object to use
     * @param stream      The output stream for messages
     */
    public Use(@NotNull Preferences preferences, @NotNull PrintStream stream) {
        super(CommandLineId.USE, preferences, stream);
    }

    /**
     * Processes elements from a data source path.
     *
     * @param elements The elements from a data source path
     * @return Processed elements from the data source path
     */
    @Contract(value = "_ -> param1", pure = true)
    private static @NotNull String @NotNull @Unmodifiable [] processSource(
            @NotNull String @NotNull [] elements) {

        /*
         * Isolate the last element. Return the last element as a single
         * element array if any element exists. Return an empty string if no
         * element exists. Note: Formerly this class operated by using all the
         * elements, i.e., simply returning the argument to this method.
         */
        final int elementsLength = elements.length;
        return new String[]{(0 < elementsLength) ?
                elements[elementsLength - 1] : ""};
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        // Is the argument not null?
        if (null != argument) {

            /*
             * The argument is not null. Get the source from the preference
             * manager. Is the source not null?
             */
            final Path source = PreferenceManager.getInstance().getSource();
            if (null != source) {

                /*
                 * The source is not null. Create a list to build destination
                 * path elements, and add 'user.home'.
                 */
                final List<String> destinationElementList = new ArrayList<>();
                destinationElementList.add(System.getProperty("user.home"));
                destinationElementList.add(argument);

                /*
                 * Get the source elements by splitting on the file separator.
                 * Process the elements, and cycle for each source element.
                 */
                final String[] sourceElements =
                        processSource(source.toString().split(File.separator));
                for (String sourceElement : sourceElements) {

                    /*
                     * Add the first/next source element to the destination
                     * element list if the source element is not empty (this
                     * means not null nor blank)
                     */
                    if (!sourceElement.isEmpty()) {
                        destinationElementList.add(sourceElement);
                    }
                }

                // Make an array from the destination elements.
                final String[] destinationElements =
                        destinationElementList.toArray(new String[0]);

                // Reformat the argument.
                argument = Paths.get(File.separator,
                        destinationElements).toString();
            }
        }

        // Call the superclass method to finish up.
        super.dispatch(argument);
    }

    @Override
    protected void put(@NotNull String value) {
        getPreferences().put(getKeyName(CommandLineId.DESTINATION), value);
    }
}
