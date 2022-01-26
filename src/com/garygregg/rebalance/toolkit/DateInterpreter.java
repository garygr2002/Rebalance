package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.interpreter.Interpreter;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.Date;

public class DateInterpreter extends Interpreter<Date> {

    @Override
    protected @NotNull Date doInterpret(@NotNull String string) {

        // Declare a variable to receive a date.
        Date date;
        try {

            // Try to parse the given string as a date.
            date = DateUtilities.parse(string);
        }

        /*
         * Catch any parse exception that may occur. Wrap the parse exception
         * in a new runtime exception, and throw the runtime exception.
         */
        catch (@NotNull ParseException exception) {

            /*
             * It would be nice to call 'receiveException' directly, but the
             * default value required as an argument is not currently available
             * to use in this class.
             */
            throw new RuntimeException(exception);
        }

        // Return the parsed date.
        return date;
    }
}
