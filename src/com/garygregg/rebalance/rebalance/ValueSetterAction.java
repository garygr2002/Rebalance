package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class ValueSetterAction extends ResidualProducerAction {

    @Override
    protected boolean isConsidered(@NotNull ReceiverDelegate<?> delegate) {
        return delegate.isConsidered();
    }

    @Override
    protected @NotNull Currency produceResidual(
            @NotNull ReceiverDelegate<?> delegate,
            @NotNull Currency currency, boolean isRelative) {
        return delegate.setProposed(currency, isRelative);
    }

    @Override
    public void setRelative(boolean relative) {
        super.setRelative(relative);
    }
}
