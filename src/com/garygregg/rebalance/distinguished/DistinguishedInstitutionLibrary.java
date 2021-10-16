package com.garygregg.rebalance.distinguished;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DistinguishedInstitutionLibrary extends
        DistinguishedLibrary<DistinguishedInstitution, DistinguishedInstitutionDescription, String> {

    // The singleton distinguished institution library
    private static final DistinguishedInstitutionLibrary library =
            new DistinguishedInstitutionLibrary();

    // A map of distinguished institutions to their descriptions
    private final Map<DistinguishedInstitution, DistinguishedInstitutionDescription>
            distinguishedInstitutions = new TreeMap<>();

    /**
     * Constructs the distinguished institution library.
     */
    private DistinguishedInstitutionLibrary() {

        // Nothing to add to the line codes.
    }

    /**
     * Gets a distinguished institution library instance.
     *
     * @return A distinguished institution library instance
     */
    public static @NotNull DistinguishedInstitutionLibrary getInstance() {
        return library;
    }

    /**
     * Adds a distinguished institution description to the library.
     *
     * @param description The distinguished institution description to add to
     *                    the library
     * @return An existing distinguished institution description that was
     * displaced in the library because it had the same key
     */
    @SuppressWarnings("UnusedReturnValue")
    DistinguishedInstitutionDescription addDescription(
            @NotNull DistinguishedInstitutionDescription description) {

        /*
         * Get the key from the description. Map the value to the key, and add
         * the description to the distinguished institutions.
         */
        final DistinguishedInstitution institution = description.getKey();
        addValue(institution, description.getValue());
        return distinguishedInstitutions.put(institution, description);
    }

    @Override
    public boolean areKeysSorted() {
        return (distinguishedInstitutions instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        distinguishedInstitutions.clear();
    }

    @Override
    public DistinguishedInstitutionDescription[] getCatalog() {
        return distinguishedInstitutions.values().toArray(
                new DistinguishedInstitutionDescription[0]);
    }

    @Override
    public @NotNull DistinguishedInstitution getDefaultKey() {
        return DistinguishedInstitution.DEFAULT;
    }

    @Override
    public DistinguishedInstitutionDescription getDescription(DistinguishedInstitution
                                                                      key) {
        return distinguishedInstitutions.get(key);
    }

    @Override
    public int getElementCount() {
        return DistinguishedFields.values().length;
    }

    @Override
    public String getValue(@NotNull DistinguishedInstitution key) {

        /*
         * Get the description mapped to the key. Return null if there is no
         * such description, otherwise return the value of the description.
         */
        final DistinguishedInstitutionDescription description = getDescription(key);
        return (null == description) ? null : description.getValue();
    }
}
