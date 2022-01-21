package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.PreferenceManager;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.PreferenceDispatch;
import org.jetbrains.annotations.NotNull;

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
                 * path elements. Add 'home', the user's name, and the
                 * argument.
                 */
                final List<String> destinationElementList = new ArrayList<>();
                destinationElementList.add("home");
                destinationElementList.add(System.getProperty("user.name"));
                destinationElementList.add(argument);

                /*
                 * Get the source elements by splitting on the file separator.
                 * Cycle for each source element.
                 */
                final String[] sourceElements =
                        source.toString().split(File.separator);
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
