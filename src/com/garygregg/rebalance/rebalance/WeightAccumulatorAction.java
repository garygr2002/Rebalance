package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WeightAccumulatorAction
        extends ContainerNodeAction<ReceiverDelegate<?>, List<Double>> {

    @Override
    public void doAction(@NotNull ReceiverDelegate<?> delegate) {

        // Add the weight if the delegate is to be considered.
        if (delegate.isConsidered()) {
            getList().add(delegate.getWeight());
        }
    }

    @Override
    protected @NotNull List<Double> getInitialValue() {
        return new ArrayList<>();
    }

    /**
     * Gets the weight list.
     *
     * @return The weight list
     */
    public @NotNull List<Double> getList() {
        return getContained();
    }

    @Override
    public void reset() {
        getList().clear();
    }
}
