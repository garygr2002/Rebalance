package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class NodeDelegate extends ReceiverDelegate<RebalanceNode> {

    /**
     * Constructs the node delegate.
     *
     * @param node The node from whom we are delegated
     */
    public NodeDelegate(@NotNull RebalanceNode node) {
        super(node, node.getWeight());
    }

    @Override
    public Currency getProposed() {
        return getReceiver().getValue();
    }
}
