package com.garygregg.rebalance.rebalance;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

class Patterns implements Iterator<Integer> {

    // The slots that bubbles are currently occupying
    private final ArrayList<Integer> slots = new ArrayList<>();

    // The initial 'next()' (based on the number of slots)
    private int initialNext;

    // The value returned by 'next()'
    private Integer next;

    // The number of calls after 'setNextLimit(int)' was last called
    private int nextCalls;

    /*
     * The limit to the number of permissible 'next()' calls after
     * 'setNextLimit(int)' was last called
     */
    private Integer nextLimit;

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
     * @throws IllegalArgumentException Indicates that the bubble count/slot
     *                                  count pair is inconsistent
     */
    private static void check(int bubbleCount, int slotCount) {

        // Is the slot count less than the bubble count?
        if (slotCount < bubbleCount) {

            /*
             * The slot count is less than the bubble count. This is
             * impermissible. Throw an IllegalArgumentException describing the
             * problem.
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
         * Declare local variables, and initialize them as needed. Declare and
         * initialize the return value.
         */
        int ceiling, lastSlot = getSlotCount();
        boolean result = false;

        /*
         * Get the bubble count, and declare a variable to receive the current
         * bubble.
         */
        final int bubbleCount = getBubbleCount();
        int currentBubble;

        // Cycle for each bubble, or until we locate a bubble that can move up.
        for (currentBubble = 0; (currentBubble < bubbleCount) && (!result);
             ++currentBubble) {

            /*
             * The slot of the current bubble represents a ceiling for the
             * previous bubble, if any. Push up the previous bubble, if
             * possible, and adjust the last slot.
             */
            ceiling = slots.get(currentBubble);
            result = setSlot(lastSlot, ceiling, currentBubble - 1);
            lastSlot = ceiling;
        }

        /*
         * Try to push up the last bubble if no previous bubble could be pushed
         * up.
         */
        if (!result) {
            result = setSlot(lastSlot, getSlotCount(), currentBubble - 1);
        }

        // Return the result
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

        /*
         * Clear the slots, and add one back that re-initializes the slot count
         * to zero.
         */
        slots.clear();
        slots.add(0);
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

        // Clear the next limit, and clear the number of calls to 'next()'.
        nextLimit = null;
        clearNextCalls();
    }

    /**
     * Constructs the value to be returned by <pre>next()</pre>.
     *
     * @return The value to be returned by <pre>next<</pre>
     */
    private @NotNull Integer constructNext() {

        // Declare local variables, and initialize them as necessary.
        int cumulativeShifts = 0, mask = 0x1, result = getInitialNext(),
                shifts;

        // Get the bubble count, and cycle for each bubble.
        final int bubbleCount = getBubbleCount();
        for (int bubble = 0; bubble < bubbleCount; ++bubble) {

            // Get the shift differential, and move the mask.
            shifts = slots.get(bubble) - cumulativeShifts;
            mask <<= shifts;

            /*
             * Clear bits in the result, and update the cumulative shifts with
             * the shift differential.
             */
            result ^= mask;
            cumulativeShifts += shifts;
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the bubble count.
     *
     * @return The bubble count
     */
    private int getBubbleCount() {
        return slots.size() - 1;
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
    private int getSlotCount() {
        return slots.get(getBubbleCount());
    }

    @Override
    public boolean hasNext() {
        return (null != next) && checkLimit();
    }

    /**
     * Increments the number of bubbles.
     */
    private void incrementBubbles() {

        /*
         * Get the current bubble count and slot count, and check to be sure it
         * would be okay to increment the number of bubbles.
         */
        final int bubbleCount = getBubbleCount();
        final int slotCount = getSlotCount();
        check(bubbleCount + 1, slotCount);

        /*
         * Add a slot for the new bubble. Re-initialize the bubbles, and reset
         * the slot count.
         */
        slots.add(0);
        initialize(bubbleCount);
        setSlotCount(slotCount);
    }

    /**
     * Initializes the slot of a bubble.
     *
     * @param bubble The number of the bubble
     */
    private void initialize(int bubble) {

        // Is the bubble greater than zero?
        if (0 <= bubble) {

            /*
             * The bubble is greater than zero. Set the slot of the bubble
             * to its own ordinal number. Initialize any previous bubbles.
             */
            slots.set(bubble, bubble);
            initialize(bubble - 1);
        }
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
         * Construct the next number to return, if possible. Return the
         * existing next number.
         */
        next = hasNext ? constructNext() : null;
        return result;
    }

    /**
     * Resets the slot count.
     *
     * @param slotCount The new slot count
     */
    public void reset(int slotCount) {

        // Clear the bubble and set the slot count.
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
     * Resets the patterns.
     */
    public void reset() {
        reset(getSlotCount());
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
    @SuppressWarnings("unused")
    public void setNextLimit(int nextLimit) {

        // Clear the number of calls to 'next()' after setting the limit.
        this.nextLimit = nextLimit;
        clearNextCalls();
    }

    /**
     * Sets the slot for a bubble if the slot will be less than a ceiling after
     * the slot is incremented.
     *
     * @param candidate The candidate slot
     * @param ceiling   The ceiling
     * @param bubble    The bubble receiving a new slot
     * @return True if the candidate will be less than the ceiling after being
     * incremented; false otherwise
     */
    private boolean setSlot(int candidate, int ceiling, int bubble) {

        // Will the slot be less than the ceiling after being incremented?
        ++candidate;
        final boolean result = candidate < ceiling;
        if (result) {

            /*
             * The slot will be less than the ceiling after being incremented.
             * Set the slot of the bubble, and re-initialize all previous
             * bubbles.
             */
            slots.set(bubble, candidate);
            initialize(bubble - 1);
        }

        // Return the result.
        return result;
    }

    /**
     * Sets the slot count.
     *
     * @param slotCount The new slot count
     */
    private void setSlotCount(int slotCount) {

        /*
         * Get the existing bubble count, and ensure that the new slot count is
         * consistent with the existing bubble count.
         */
        final int bubbleCount = getBubbleCount();
        check(bubbleCount, slotCount);

        // Set the slot count, and set the initial next.
        slots.set(bubbleCount, slotCount);
        setInitialNext();
    }
}
