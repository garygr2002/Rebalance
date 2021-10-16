package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

public abstract class Library<KeyType, DescriptionType> {

    // The set of line codes
    private final Set<Character> lineCodes = new TreeSet<>();

    // The date of the library
    private Date date;

    /**
     * Gets the default string key.
     *
     * @return The default string key
     */
    public static @NotNull String getDefaultStringKey() {
        return "";
    }

    /**
     * Adds a line code to the library.
     *
     * @param lineCode A line code to add to the library
     * @return True if the library did not already contain the line code, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    protected boolean addLineCode(char lineCode) {
        return lineCodes.add(Character.toUpperCase(lineCode));
    }

    /**
     * Determines whether the string representations of key elements are okay.
     *
     * @param elements The key elements to test
     * @return True if the string representations of key elements are okay,
     * false otherwise
     */
    public boolean areKeyElementsOkay(String @NotNull ... elements) {

        /*
         * There must be exactly one element. The element must not be null, and
         * must not be equal to the default string key.
         */
        String element;
        int index = 1;
        return (elements.length == (index--)) &&
                (null != (element = elements[index])) &&
                (!element.equals(getDefaultStringKey()));
    }

    /**
     * Determines whether the descriptions have a total order.
     *
     * @return True if the descriptions in the catalog have a total order,
     * false otherwise
     */
    public abstract boolean areKeysSorted();

    /**
     * Clears descriptions.
     */
    protected abstract void clearDescriptions();

    /**
     * Gets the catalog of the library as an array.
     *
     * @return The catalog of the library as an array
     */
    public abstract DescriptionType[] getCatalog();

    /**
     * Gets the date of the library.
     *
     * @return The date of the library
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the default key for the library.
     *
     * @return The default key for the library
     */
    public abstract @NotNull KeyType getDefaultKey();

    /**
     * Gets a library object given a key.
     *
     * @param key The key of the library object
     * @return A library object associated with the key
     */
    public abstract DescriptionType getDescription(KeyType key);

    /**
     * Gets the count of elements in a line.
     *
     * @return The count of elements in a line
     */
    public abstract int getElementCount();

    /**
     * Gets the line codes of a description.
     *
     * @return The line codes of a description
     */
    public @NotNull Character[] getLineCodes() {
        return lineCodes.toArray(new Character[0]);
    }

    /**
     * Sets the date of the library.
     *
     * @param date The date of the library
     */
    public void setDate(Date date) {

        // All descriptions must have been added *after* date was set!
        clearDescriptions();
        this.date = date;
    }
}
