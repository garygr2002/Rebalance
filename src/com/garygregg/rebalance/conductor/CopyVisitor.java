package com.garygregg.rebalance.conductor;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

class CopyVisitor extends SimpleFileVisitor<Path> {

    // The destination directory.
    private final String destinationDirectory;

    // The source directory.
    private final String sourceDirectory;

    /**
     * Constructs the copy visitor.
     *
     * @param destinationDirectory The destination directory
     * @param sourceDirectory      The source directory
     */
    public CopyVisitor(@NotNull String destinationDirectory,
                       @NotNull String sourceDirectory) {

        // Set the destination and source directories.
        this.destinationDirectory = destinationDirectory;
        this.sourceDirectory = sourceDirectory;
    }

    /**
     * Gets a destination path given a source path.
     *
     * @param source A source path
     * @return A destination path
     */
    private @NotNull Path getDestination(@NotNull Path source) {
        return Paths.get(getDestinationDirectory(),
                source.toString().substring(getSourceDirectory().length()));
    }

    /**
     * Gets the destination directory.
     *
     * @return The destination directory
     */
    private @NotNull String getDestinationDirectory() {
        return destinationDirectory;
    }

    /**
     * Gets the source directory.
     *
     * @return The source directory
     */
    private @NotNull String getSourceDirectory() {
        return sourceDirectory;
    }

    @Override
    public @NotNull FileVisitResult preVisitDirectory(@NotNull Path source,
                                                      @NotNull BasicFileAttributes
                                                              attributes)
            throws IOException {

        /*
         * Get the destination directory. Return 'continue' if the destination
         * already exists or can be created, otherwise return 'skip subtree.'
         */

        /*
         * Get the destination directory. Does the destination directory not
         * exist?
         */
        final File destination = getDestination(source).toFile();
        if (!destination.exists()) {

            /*
             * The destination directory does not exist. Try to create it, and
             * throw a new I/O exception if the attempt fails.
             */
            if (!destination.mkdir()) {
                throw new IOException(String.format("The directory '%s' " +
                                "does not exist and cannot be created.",
                        destination));
            }
        }

        // Ready to continue copying ordinary files.
        return FileVisitResult.CONTINUE;
    }

    @Override
    public @NotNull FileVisitResult visitFile(@NotNull Path source,
                                              @NotNull BasicFileAttributes
                                                      attributes)
            throws IOException {

        // Is the source path a directory?
        if (!attributes.isDirectory()) {

            /*
             * The source path is a directory. Copy the source to the
             * destination while not following symbolic links and replacing
             * existing files.
             */
            Files.copy(source, getDestination(source),
                    LinkOption.NOFOLLOW_LINKS,
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // Return 'continue.'
        return FileVisitResult.CONTINUE;
    }
}
