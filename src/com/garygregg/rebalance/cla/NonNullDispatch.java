package com.garygregg.rebalance.cla;

public abstract class NonNullDispatch<T extends Enum<T>>
        implements Dispatch<T> {

    @Override
    public void dispatch(String argument) throws CLAException {

        // Throw a new CLA exception if the argument is null.
        if (null == argument) {
            throw new CLAException("Expected argument is null.");
        }
    }
}
