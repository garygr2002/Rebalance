package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

class SumCurrentAction extends SumAction {

    @Override
    public void doAction(@NotNull ReceiverDelegate<?> delegate) {
        getMutableSum().add(delegate.getCurrent());
    }
}
