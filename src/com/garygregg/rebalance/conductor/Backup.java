package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.PreferenceManager;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import com.garygregg.rebalance.cla.Informer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;

class Backup extends Informer implements Dispatch<CommandLineId> {

    /**
     * Constructs the backup dispatch.
     *
     * @param stream The output stream for messages
     */
    public Backup(@NotNull PrintStream stream) {
        super(stream);
    }

    @Override
    public void dispatch(String argument) throws CLAException {

        /*
         * Get the preference manager, and the source directory from the
         * preference manager.
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        final Path source = manager.getSource();

        // Ensure that the source path is not null.
        if (null == source) {
            throw new CLAException("Backup failed because the source " +
                    "directory is null.");
        }

        // Declare the destination path.
        final Path destination;
        try {

            /*
             * Use the destination from the preference manager if the argument
             * is null, otherwise use the argument as a destination. Is the
             * destination path null?
             */
            destination = (null == argument) ? manager.getDestination() :
                    Paths.get(argument);
            if (null == destination) {

                // The destination path is null. Throw a new CLA exception.
                throw new CLAException("Backup failed because the " +
                        "destination directory is null.");
            }

            /*
             * Create a new file visitor with the destination and the source
             * as strings.
             */
            final FileVisitor<Path> copyVisitor =
                    new CopyVisitor(destination.toString(), source.toString());

            // Walk the file tree of the source with the file visitor.
            Files.walkFileTree(source, copyVisitor);
            printNoException(getKey().toString());
        }

        /*
         * Catch any invalid path exceptions, or I/O exceptions that may occur.
         * Wrap them in a new CLA exception, and throw the CLA exception.
         */
        catch (@NotNull InvalidPathException | IOException exception) {
            throw new CLAException(exception);
        }
    }

    @Override
    public @NotNull CommandLineId getKey() {
        return CommandLineId.BACKUP;
    }
}
