package com.garygregg.rebalance.cla;

public class CLAException extends Exception {

    /**
     * Constructs the command line argument exception with a message.
     *
     * @param message A message for the exception
     */
    public CLAException(String message) {
        super(message);
    }

    /**
     * Constructs the command line argument exception with a cause.
     *
     * @param cause A cause for the exception
     */
    public CLAException(Throwable cause) {
        super(cause);
    }
}
