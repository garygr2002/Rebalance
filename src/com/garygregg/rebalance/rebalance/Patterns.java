package com.garygregg.rebalance.rebalance;

import java.util.Iterator;
import java.util.NoSuchElementException;

class Patterns implements Iterator<Integer> {

    // The current number of bubbles (or zeros, as they are usually known)
    private int bubbleCount;

    // True if the user wants to see zero; false otherwise
    private boolean iWantToSeeZero;

    // The initial 'next()' (based on the current number of slots)
    private int initialNext;

    /*
     * The next value returned by 'next()'; with this class there is always at
     * least one 'next()'
     */
    private Integer next;

    // The number of calls after 'setNextLimit(int)' was last called
    private int nextCalls;

    /*
     * The limit of the number of permissible 'next()' calls after
     * 'setNextLimit(int)' was last called (null means no limit)
     */
    private Integer nextLimit;

    // The current number of slots
    private int slotCount;


    /**
     * Constructs the patterns object.
     *
     * @param slotCount The initial slot count
     */
    public Patterns(int slotCount) {
        reset(slotCount);
    }

    /**
     * Checks that a bubble count/slot count pair is consistent.
     *
     * @param bubbleCount A bubble count
     * @param slotCount   A slot count
     */
    private static void check(int bubbleCount, int slotCount) {

        // Is the bubble count too low?
        final int lowEndLimit = 0;
        if (bubbleCount < lowEndLimit) {

            /*
             * The bubble count is too low. Throw an IllegalArgumentException
             * describing the problem.
             */
            throw new IllegalArgumentException(String.format("The bubble " +
                            "count, %d, may not be less than %d", bubbleCount,
                    lowEndLimit));
        }

        // Is the slot count too low?
        if (slotCount < lowEndLimit) {

            /*
             * The slot count is too low. Throw an IllegalArgumentException
             * describing the problem.
             */
            throw new IllegalArgumentException(String.format("The slot " +
                            "count, %d, may not be less than %d", slotCount,
                    lowEndLimit));
        }

        // Is the slot count too high?
        if (Integer.SIZE <= slotCount) {

            /*
             * The slot count is too high. Throw an IllegalArgumentException
             * describing the problem.
             */
            throw new IllegalArgumentException(String.format("The slot " +
                            "count, %d, may not be greater than or equal to %d",
                    slotCount, Integer.SIZE));
        }

        // Is the slot count less than the bubble count?
        if (slotCount < bubbleCount) {

            /*
             * The slot count is less than the bubble count. Throw an
             * IllegalArgumentException describing the problem.
             */
            throw new IllegalArgumentException(String.format("Requested " +
                    "slot count %d is smaller than current bubble " +
                    "count %d", slotCount, bubbleCount));
        }
    }

    /**
     * Pushes up a bubble.
     *
     * @return True if a bubble could be pushed up; false otherwise
     */
    private boolean bubble() {

        /*
         * This is the most complicated method in this class. I had a devil of
         * a time debugging it. Declare and initialize immutable parameters.
         */
        final int initialNext = getInitialNext();
        final int shift = 1;

        // Declare local variables, and initialize them as necessary.
        boolean result = false;
        int candidate, emptySlots = 0;
        int desiredPattern = 0x1, patternMask = 0x3;

        /*
         * Cycle for the number of slots where a movable bubble might exist, or
         * until we locate a movable bubble.
         */
        final int limit = getSlotCount() - 1;
        for (int i = 0; (i < limit) && (!result); ++i) {

            /*
             * Calculate a candidate by flipping two adjacent bits using the
             * mask. Was the flip a '10' to an '01'? If so, it means a bubble
             * moved.
             */
            candidate = next ^ patternMask;
            //noinspection AssignmentUsedAsCondition
            if (result = (desiredPattern == (candidate & patternMask))) {

                /*
                 * Okay, a bubble moved. Prepare the candidate by setting all
                 * its bits that are lower than the flipped bits. This clears
                 * all immovable bubbles. Then clear a number of low-end bits
                 * in the candidate equal to the number of bubbles that we had
                 * seen, but could not move. This has the effect of pushing the
                 * "immovable" bubbles back down to the bottom. Note: Our
                 * current iteration counter minus the number of empty slots is
                 * the number of immovable bubbles.
                 */
                candidate |= (desiredPattern - 1);
                next = candidate & (initialNext << (i - emptySlots));
            }

            /*
             * Okay, a bubble did not move, but did we locate a slot with no
             * bubble? If so, increment the empty slot count.
             */
            else if (0 != (next & desiredPattern)) {
                ++emptySlots;
            }

            /*
             * Right-shift the desired pattern and the pattern mask in
             * preparation for the next iteration.
             */
            desiredPattern <<= shift;
            patternMask <<= shift;
        }

        // Return the result to our caller.
        return result;
    }

    /***
     * Checks the number of previous calls to <pre>next()</pre>, and has the
     * side effect of incrementing the number of next calls if the check
     * succeeds.
     *
     * @return True if the number of previous calls to <pre>next()</pre> is
     * lower than the limit; false otherwise
     */
    private boolean checkAndAddIf() {

        // Check the limit. Did the check succeed?
        final boolean result = checkLimit();
        if (result) {

            // The check succeeded. Increment the number of 'next()' calls.
            ++nextCalls;
        }

        // Return the result.
        return result;
    }

    /***
     * Checks the number of previous calls to <pre>next()</pre>.
     * @return True if the number of previous calls to <pre>next()</pre> is
     * lower than the limit; false otherwise
     */
    private boolean checkLimit() {

        // The result is true if the next limit has not been set.
        boolean result = (null == nextLimit);
        if (!result) {

            /*
             * The next limit has been set. Reinitialize the result to the
             * test of the number of 'next()' calls being below the limit.
             */
            result = (nextCalls < nextLimit);
        }

        // Return the result.
        return result;
    }

    /**
     * Clears the existing bubbles.
     */
    private void clearBubbles() {

        // Clear the bubbles and the bubble count.
        next = getInitialNext();
        bubbleCount = 0;
    }

    /**
     * Clears the count of calls to <pre>next().</pre>
     */
    public void clearNextCalls() {
        nextCalls = 0;
    }

    /**
     * Clears the limit of <pre>next()</pre> calls.
     */
    @SuppressWarnings("unused")
    public void clearNextLimit() {

        // Clear the number of calls to 'next()', then clear the next limit.
        clearNextCalls();
        nextLimit = null;
    }

    /**
     * Gets the bubble count.
     *
     * @return The bubble count
     */
    private int getBubbleCount() {
        return bubbleCount;
    }

    /**
     * Gets the initial next.
     *
     * @return The initial next
     */
    private int getInitialNext() {
        return initialNext;
    }

    /**
     * Gets the slot count.
     *
     * @return The slot count
     */
    public int getSlotCount() {
        return slotCount;
    }

    @Override
    public boolean hasNext() {
        return (null != next) && checkLimit();
    }

    /**
     * Gets I-want-to-see-zero.
     *
     * @return True if the user wants to see zero; false otherwise
     */
    public boolean iWantToSeeZero() {
        return iWantToSeeZero;
    }

    /**
     * Increments the number of bubbles.
     */
    private void incrementBubbles() {

        /*
         * Calculate the new bubble count, and check to make sure that it is
         * okay with the existing slot count.
         */
        final int newBubbleCount = getBubbleCount() + 1;
        check(newBubbleCount, getSlotCount());

        // Calculate the new 'next()', and set the new bubble count.
        final int initialNext = getInitialNext();
        next = (initialNext << newBubbleCount) & initialNext;
        bubbleCount = newBubbleCount;
    }

    @Override
    public Integer next() {

        // Throw an exception if the next number to return is null.
        if (null == next) {
            throw new NoSuchElementException("The last element I returned " +
                    "should have been 0; there are no more");
        }

        /*
         * Throw an exception if there have already been too many calls to
         * 'next()' since the last call to 'setNextLimit(int)'.
         */
        if (!checkAndAddIf()) {
            throw new NoSuchElementException(String.format("There have " +
                    "already been %d calls to next() after the limit set " +
                    "to %d", nextCalls, nextLimit));
        }

        /*
         * Declare and initialize the result, and try to push up an existing
         * bubble. Was it not possible to push up an existing bubble?
         */
        final Integer result = next;
        boolean hasNext = bubble();
        if (!hasNext) {

            /*
             * It was not possible to push up an existing bubble. Increment
             * the number of bubbles if there is a slot for another bubble.
             */
            //noinspection AssignmentUsedAsCondition
            if (hasNext = (getBubbleCount() < getSlotCount())) {
                incrementBubbles();
            }
        }

        /*
         * Set the next number to null if: 1) there is no next number, or; 2)
         * the caller does not want to see zero and the next number is zero.
         */
        if (!(hasNext && (iWantToSeeZero() || (0 != next)))) {
            next = null;
        }

        // Return the saved next number.
        return result;
    }

    /**
     * Resets with a new slot count.
     *
     * @param slotCount The new slot count
     */
    public void reset(int slotCount) {

        // Clear the bubbles and set the slot count.
        clearBubbles();
        setSlotCount(slotCount);

        /*
         * Set the first value to be returned by 'next()', and clear the number
         * of calls to 'next()'.
         */
        next = getInitialNext();
        clearNextCalls();
    }

    /**
     * Resets with the existing slot count.
     */
    public void reset() {
        reset(getSlotCount());
    }

    /**
     * Sets I want-to-see-zero
     *
     * @param iWantToSeeZero True if the user wants to see zero; false
     *                       otherwise
     */
    @SuppressWarnings("unused")
    public void setIWantToSeeZero(boolean iWantToSeeZero) {
        this.iWantToSeeZero = iWantToSeeZero;
    }

    /**
     * Sets the initial next number.
     */
    private void setInitialNext() {

        /*
         * Set the initial next to a number of set bits equal to the slot
         * count.
         */
        final long result = 1L << getSlotCount();
        initialNext = (int) (result - 1);
    }

    /**
     * Sets the limit of calls to <pre>next().</pre>
     *
     * @param nextLimit The new limit of calls to <pre>next()</pre>
     */
    public void setNextLimit(int nextLimit) {

        // Clear the number of calls to 'next()' after setting the limit.
        this.nextLimit = nextLimit;
        clearNextCalls();
    }

    /**
     * Sets the slot count.
     *
     * @param slotCount The new slot count
     */
    private void setSlotCount(int slotCount) {

        // Check the new slot count before setting it. Set the initial next.
        check(getBubbleCount(), slotCount);
        this.slotCount = slotCount;
        setInitialNext();
    }
}
