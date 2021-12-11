package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

abstract class ContainerNodeAction<ArgumentType, ContainedType>
        implements NodeAction<ArgumentType> {

    // Our member variable
    private ContainedType contained;

    /**
     * Gets the member variable.
     *
     * @return The member variable
     */
    protected @NotNull ContainedType getContained() {

        // Set the member variable if it is null.
        if (null == contained) {
            contained = getInitialValue();
        }

        // Return the member variable.
        return contained;
    }

    /**
     * Gets the initial value of the member variable.
     *
     * @return The initial value of the member variable
     */
    protected abstract @NotNull ContainedType getInitialValue();

    /**
     * Resets the container.
     */
    public abstract void reset();

    /**
     * Sets the member variable.
     *
     * @param contained The member variable
     */
    protected void setContained(@NotNull ContainedType contained) {
        this.contained = contained;
    }
}
