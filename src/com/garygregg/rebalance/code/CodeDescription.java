package com.garygregg.rebalance.code;

import com.garygregg.rebalance.toolkit.Description;
import com.garygregg.rebalance.toolkit.FundType;
import org.jetbrains.annotations.NotNull;

public class CodeDescription implements Description<Character> {

    // The code
    private final Character code;

    // Subcodes associated with the code
    private final Character[] subcodes = new Character[getSubcodeCount()];

    // A description of the code
    private String description;

    // Has the fund type been set?
    private boolean fundTypeSet;

    // The name associated with the code
    private String name;

    // The associated fund type, if any
    private FundType type;

    /**
     * Constructs the code description.
     *
     * @param code The code
     */
    CodeDescription(@NotNull Character code) {
        this.code = code;
    }

    /**
     * Gets the count of subcodes.
     *
     * @return The count of subcodes
     */
    public static int getSubcodeCount() {
        return 5;
    }

    /**
     * Gets the code.
     *
     * @return The code
     */
    public @NotNull Character getCode() {
        return code;
    }

    /**
     * Gets the description of the code.
     *
     * @return The description of the code
     */
    public String getDescription() {
        return description;
    }

    @Override
    public @NotNull Character getKey() {
        return getCode();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the subcode associated with the given index.
     *
     * @param index The given index
     * @return The subcode associated with the given index
     */
    @SuppressWarnings("unused")
    public Character getSubcode(int index) {
        return isIndexOkay(index) ? subcodes[index] : null;
    }

    /**
     * Gets the associated fund type, null if the code does to identify a fund
     * type.
     *
     * @return The associated fund type, if any
     */
    public FundType getType() {
        return type;
    }

    /**
     * Is the fund type set?
     *
     * @return True if the fund type is set, false otherwise
     */
    public boolean isFundTypeSet() {
        return fundTypeSet;
    }

    /**
     * Determines if a subcode index is okay.
     *
     * @param index The given subcode index
     * @return True if the subcode index is okay, false otherwise
     */
    private boolean isIndexOkay(int index) {
        return (0 <= index) && (index < subcodes.length);
    }

    /**
     * Sets the description of the code.
     *
     * @param description The description of the code
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the name associated with the code.
     *
     * @param name The name associated with the code
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the subcode associated with the given index.
     *
     * @param subcode The subcode to set
     * @param index   The given index
     * @return True if the subcode was successfully set, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setSubcode(Character subcode, int index) {

        // Set the subcode only if the given index is okay.
        final boolean result = isIndexOkay(index);
        if (result) {

            // The index is okay. Set the subcode.
            subcodes[index] = subcode;
        }

        // Return the result.
        return result;
    }

    /**
     * Sets the fund type.
     *
     * @param type The fund type to set
     * @return True if the fund type had not already been set, false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean setType(FundType type) {

        // Only set the fund type if it has not already been set.
        final boolean result = !isFundTypeSet();
        if (result) {

            // Set the fund type, and set that it has been set.
            this.type = type;
            fundTypeSet = true;
        }

        // Return whether the fund type had not already been set.
        return result;
    }
}
