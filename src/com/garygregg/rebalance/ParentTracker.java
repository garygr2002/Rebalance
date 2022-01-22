package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ParentTracker {

    // An instance of the parent tracker.
    private static ParentTracker instance;

    // A stack of potential, active parent keys
    private final Stack<String> actives = new Stack<>();

    // An association of line codes to their holding line types
    private final Map<Character, HoldingLineType> association =
            new HashMap<>();

    // A hierarchy of expected line types
    private final Expecting expecting = new Expecting();

    // The distinguished account library
    private Library<?, ?> accountLibrary;

    /**
     * Constructs the parent tracker.
     */
    private ParentTracker() {
        reset();
    }

    /**
     * Gets an instance of the parent tracker.
     *
     * @return An instance of the parent tracker
     */
    public static @NotNull ParentTracker getInstance() {
        return (null == instance) ? (instance = new ParentTracker()) : instance;
    }

    /**
     * Adds a line code association.
     *
     * @param lineCode The line code
     * @param lineType The holding line type associated with the line code
     */
    public void addAssociation(@NotNull Character lineCode,
                               @NotNull HoldingLineType lineType) {
        association.put(lineCode, lineType);
    }

    /**
     * Adds all line codes for a given library.
     *
     * @param library  The library
     * @param lineType The holding line type associated with the line code
     */
    public void addAssociations(@NotNull Library<?, ?> library,
                                @NotNull HoldingLineType lineType) {

        // Add an association for each line code declared by the library.
        for (Character lineCode : library.getLineCodes()) {
            addAssociation(lineCode, lineType);
        }
    }

    /**
     * Determines if key elements are okay with the distinguished account
     * library.
     *
     * @param elements The key elements
     * @return True if the given key elements are okay with the distinguished
     * account library; false otherwise
     */
    private boolean areKeyElementsOkay(String... elements) {

        /*
         * Get the distinguished account library. Return true if the library is
         * null, or if the elements are okay with the non-null library.
         */
        final Library<?, ?> accountLibrary = getAccountLibrary();
        return (null == accountLibrary) ||
                accountLibrary.areKeyElementsOkay(elements);
    }

    /**
     * Clears the association of line codes to their holding types.
     */
    public void clearAssociations() {
        association.clear();
    }

    /**
     * Constructs a holding key given a line code and a child key.
     *
     * @param lineCode The line code
     * @param child    The child key
     * @return An appropriate holding key
     */
    public HoldingKey constructKey(@NotNull Character lineCode,
                                   @NotNull String child) {

        /*
         * Declare and initialize the result. Get the line type for the given
         * line code. Is there a line type for the code?
         */
        HoldingKey result = null;
        final HoldingLineType received = getAssociation(lineCode);
        if (null != received) {

            /*
             * There is a line type for the given line code. Re-initialize the
             * active types stack. Assume that the current line is a child of
             * the previous parent, and increment the expected line type. Push
             * the child key as the new candidate parent for subsequent lines,
             * and in return receive the candidate parent key for this child.
             */
            reinitializeActives();
            expecting.increment();
            String parent = peekAndPush(child);

            /*
             * Check now if our assumption that the current line be a child of
             * the previous parent had been incorrect. Decrement the expected
             * line type until we find the one we seek, or until we can
             * decrement no longer.
             */
            while ((!expecting.isCurrent(received)) &&
                    expecting.decrement()) {

                /*
                 * The expected line type is not current. Perform a correction
                 * for our previous wrong assumption that the immediate last
                 * key was the parent of the current key. Instead, it was the
                 * sibling of some previous key.
                 */
                parent = doCorrection(child);
            }

            // Construct the new parent/child pair.
            result = new HoldingKey(parent, child);
        }

        // Return the result.
        return result;
    }

    /**
     * Performs a correction for the assumption that current key was a child
     * of its immediate predecessor. Instead, this key was a sibling of the
     * previous key, and their parent is further down the stack.
     *
     * @param newActive The top of the new actives stack
     * @return A previous top of the actives stack
     */
    private @NotNull String doCorrection(@NotNull String newActive) {

        /*
         * Try to pop the actives stack twice, removing the bad assumption
         * and a sibling of the bad assumption.
         */
        for (int i = 0; !actives.isEmpty() && (2 > i); ++i) {
            actives.pop();
        }

        // Push and peek the new top of the active stack.
        return peekAndPush(newActive);
    }

    /**
     * Gets the account library.
     *
     * @return The account library
     */
    public Library<?, ?> getAccountLibrary() {
        return accountLibrary;
    }

    /**
     * Gets a holding line type given a line code.
     *
     * @param lineCode The line code
     * @return A holding line type associated with the line code
     */
    public HoldingLineType getAssociation(@NotNull Character lineCode) {
        return association.get(lineCode);
    }

    /**
     * Peeks at the top of the active stack, pushes a new active, and returns
     * the previous top of the stack.
     *
     * @param newActive The new top of the actives stack
     * @return The previous top of the actives stack
     */
    private @NotNull String peekAndPush(@NotNull String newActive) {

        /*
         * If there is no current top-of-stack, use the default string key.
         * Otherwise, use a formatted version of a unique account string
         * representation if we are expecting a ticker. Use the unmodified
         * top-of-stack if expecting anything else.
         *
         * Note: I decided not to do things this way, because in all
         * circumstances an unformatted string should be written into the
         * holding key. If a client wants to format the parent portion of the
         * ticker key for legibility, that is up to them.
         *
         * final String lastActive = actives.isEmpty() ?
         *     Library.getDefaultStringKey() :
         *     (expecting.isCurrent(HoldingLineType.TICKER) ?
         *     AccountKey.toString(actives.peek()) : actives.peek());
         */
        final String lastActive = actives.isEmpty() ?
                Library.getDefaultStringKey() : actives.peek();

        /*
         * Is the combination of last top-of-stack and new top-of-stack
         * acceptable as a key for the account library? And are we seeking an
         * account line?
         */
        if (areKeyElementsOkay(lastActive, newActive) &&
                expecting.isCurrent(HoldingLineType.ACCOUNT)) {

            /*
             * The combination of last and new tops-of-stack make acceptable
             * keys for the account library...and we are also seeking an account
             * line. Push a unique key consisting of the last top-of-stack (as
             * an institution mnemonic), and the new top-of-stack (as an
             * account number).
             */
            actives.push(AccountKey.createUniqueKey(lastActive,
                    AccountKey.parseLong(newActive)));
        }

        /*
         * Either the last and new tops-of-stack do not make an acceptable key
         * for the account library. Either that, or we are currently seeking
         * something other than an account line. Just push the new
         * top-of-stack.
         */
        else {
            actives.push(newActive);
        }

        // Return the last active top-of-stack.
        return lastActive;
    }

    /**
     * Re-initializes (or simply initializes) the actives stack.
     */
    private void reinitializeActives() {

        /*
         * Cycle while the number of elements on the actives stack is less than
         * the number of line types.
         */
        final int lineTypeCount = HoldingLineType.values().length;
        while (actives.size() < lineTypeCount) {

            // Push the default string key onto the actives stack.
            actives.push(Library.getDefaultStringKey());
        }
    }

    /**
     * Resets the object.
     */
    public void reset() {

        /*
         * Clear the stack of active parent keys, and reset the 'expecting'
         * object.
         */
        actives.clear();
        expecting.reset();
    }

    /**
     * Sets the account library.
     *
     * @param accountLibrary The account library
     */
    public void setAccountLibrary(Library<?, ?> accountLibrary) {
        addAssociations(this.accountLibrary = accountLibrary,
                HoldingLineType.ACCOUNT);
    }

    private static class Expecting {

        // The hierarchy of holding line types
        private final HoldingLineType[] hierarchy = {
                HoldingLineType.PORTFOLIO,
                HoldingLineType.INSTITUTION,
                HoldingLineType.ACCOUNT,
                HoldingLineType.TICKER,
                null
        };

        // An index into the holding line types hierarchy
        private int currentlyExpecting;

        // Test-and-act for decrement
        private final TestAndAct forDecrement = new TestAndAct() {

            @Override
            public void act() {
                --currentlyExpecting;
            }

            @Override
            public boolean test() {
                return (0 < currentlyExpecting);
            }
        };

        // Test-and-act for increment
        private final TestAndAct forIncrement = new TestAndAct() {

            @Override
            public void act() {
                ++currentlyExpecting;
            }

            @Override
            public boolean test() {
                return (currentlyExpecting < hierarchy.length - 1);
            }
        };

        /**
         * Constructs the object.
         */
        public Expecting() {
            reset();
        }

        /**
         * Decrements the currently expected holding line.
         *
         * @return True if the currently expected holding line could be
         * decremented, false otherwise
         */
        public boolean decrement() {
            return doTestAndAct(forDecrement);
        }

        /**
         * Performs a test, and if the test is valid performs an action.
         *
         * @param testAndAct A test and act object
         * @return True if the test was true, false otherwise
         */
        private boolean doTestAndAct(@NotNull TestAndAct testAndAct) {

            // Perform the test. Was the test valid?
            final boolean result = testAndAct.test();
            if (result) {

                // The test was valid. Perform the action.
                testAndAct.act();
            }

            // Return the result of the test.
            return result;
        }

        /**
         * Increments the currently expected holding line.
         *
         * @return True if the currently expected holding line could be
         * incremented, false otherwise
         */
        @SuppressWarnings("UnusedReturnValue")
        private boolean increment() {
            return doTestAndAct(forIncrement);
        }

        /**
         * Determines if the given holding line type is current.
         *
         * @param holdingLineType The given holding line type
         * @return True if the given holding line type is current, false
         * otherwise
         */
        public boolean isCurrent(@NotNull HoldingLineType holdingLineType) {

            /*
             * Get the current holding line type, and return true only if it
             * is non-null, and equals the given holding line type.
             */
            final HoldingLineType current = hierarchy[currentlyExpecting];
            return (null != current) && current.equals(holdingLineType);
        }

        /**
         * Resets the object.
         */
        public void reset() {
            currentlyExpecting = 0;
        }

        private interface TestAndAct {

            /**
             * Unconditionally performs an action.
             */
            void act();

            /**
             * Performs a test.
             *
             * @return The result of the test
             */
            boolean test();
        }
    }
}
