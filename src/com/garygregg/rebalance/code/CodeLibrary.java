package com.garygregg.rebalance.code;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class CodeLibrary extends Library<Character, CodeDescription> {

    // The default key
    private static final Character defaultKey = '\0';

    // The singleton code library
    private static final CodeLibrary library = new CodeLibrary();

    // A map of codes to code description objects
    private final Map<Character, CodeDescription> codes = new TreeMap<>();

    /**
     * Constructs the code library.
     */
    private CodeLibrary() {

        // Nothing to add to the line codes.
    }

    /**
     * Gets a code library instance.
     *
     * @return A code library instance
     */
    public static @NotNull CodeLibrary getInstance() {
        return library;
    }

    /**
     * Adds a code description to the library.
     *
     * @param description The code description to add to the library
     * @return An existing code description that was displaced in the
     * library because it had the same code
     */
    CodeDescription addDescription(@NotNull CodeDescription description) {
        return codes.put(description.getCode(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (codes instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        codes.clear();
    }

    @Override
    public CodeDescription[] getCatalog() {
        return codes.values().toArray(new CodeDescription[0]);
    }

    @Override
    public @NotNull Character getDefaultKey() {
        return defaultKey;
    }

    /**
     * Gets a code description object given a code.
     *
     * @param key The given code
     * @return A code description object given a code
     */
    @Override
    public CodeDescription getDescription(Character key) {

        // The code may be null for the NOT_A_FUND fund type.
        return (null == key) ? null : codes.get(key);
    }

    @Override
    public int getElementCount() {
        return CodeFields.values().length;
    }
}
