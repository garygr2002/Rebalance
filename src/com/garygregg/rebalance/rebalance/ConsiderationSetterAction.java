package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

class ConsiderationSetterAction
        extends ContainerNodeAction<ReceiverDelegate<?>, Integer> {

    // Count of calls to 'doAction(ReceiverDelegate)'
    private int iteration;

    @Override
    public void doAction(@NotNull ReceiverDelegate<?> delegate) {
        delegate.setConsidered(
                (getConsiderationPattern() & (1 << (iteration++))) != 0);
    }

    /**
     * Gets the consideration pattern.
     *
     * @return The consideration pattern
     */
    protected int getConsiderationPattern() {
        return getContained();
    }

    @Override
    protected @NotNull Integer getInitialValue() {
        return 0;
    }

    @Override
    public void reset() {
        iteration = getInitialValue();
    }

    /**
     * Sets the consideration pattern.
     *
     * @param considerationPattern The consideration pattern
     */
    public void setConsiderationPattern(int considerationPattern) {

        /*
         * Set the consideration pattern as the contained object, and reset the
         * action.
         */
        setContained(considerationPattern);
        reset();
    }
}
