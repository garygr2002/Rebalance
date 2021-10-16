package com.garygregg.rebalance.distinguished;

import org.jetbrains.annotations.NotNull;

public class DistinguishedInstitutionLibrary extends
        DistinguishedLibrary<DistinguishedInstitution, DistinguishedInstitutionDescription, String> {

    // The singleton distinguished institution library
    private static final DistinguishedInstitutionLibrary library =
            new DistinguishedInstitutionLibrary();

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

    @Override
    public DistinguishedInstitutionDescription[] getCatalog() {
        return getDescriptions().toArray(
                new DistinguishedInstitutionDescription[0]);
    }

    @Override
    public @NotNull DistinguishedInstitution getDefaultKey() {
        return DistinguishedInstitution.DEFAULT;
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
