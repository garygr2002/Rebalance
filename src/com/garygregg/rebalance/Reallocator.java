package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCountable;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Reallocator {

    // A value extractor for mutable countables
    private final ValueExtractor<MutableCountable> forCountables =
            MutableCountable::getValue;

    // The sum of the weights
    private final double weightSum;

    // A list of weights
    private final List<Double> weights;

    /**
     * Constructs the reallocator.
     *
     * @param weights A list of weights to apply to any reallocation operation;
     *                this list may be modified by the caller before any
     *                subsequent reallocation
     */
    public Reallocator(@NotNull List<Double> weights) {

        // Set the member variables.
        this.weights = weights;

        // Create the list of weights.
        final ValueExtractor<Double> forDoubles = object -> object;
        this.weightSum = sum(weights, forDoubles);
    }

    /***
     * Checks a certain number of currency values from an iterator against a
     * target.
     *
     * @param iterator The currency iterator
     * @param target The target value for currency
     * @param count The count of values to test
     * @return True if the number of values matches the target, false otherwise
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    private static boolean check(@NotNull Iterator<MutableCurrency> iterator,
                                 @NotNull Currency target,
                                 int count) {

        /*
         * Declare and initialize the result. Cycle for the given count, or
         * until we hit a false result.
         */
        boolean result = true;
        for (int i = 0; result && (i < count); ++i) {

            // Compare the first/next currency value.
            result = iterator.hasNext() && iterator.next().equals(target);
        }

        // Return the result to our caller.
        return result;
    }

    /**
     * Checks that no element in a double list is negative.
     *
     * @param list A double list
     */
    private static void checkNoNegatives(@NotNull List<Double> list) {

        /*
         * Declare a variable to receive list elements. Get the list size and
         * cycle for each list element.
         */
        double element;
        final int listSize = list.size();
        for (int i = 0; i < listSize; ++i) {

            /*
             * Throw a new illegal argument exception if the first/next list
             * element is negative.
             */
            if ((element = list.get(i)) < 0.) {
                throw new IllegalArgumentException(String.format("Detected " +
                        "negative element %f at index %d in list.", element, i));
            }
        }
    }

    /***
     * Performs a test of the reallocator.
     *
     * @param zerosFirst True if zeros should go first in the test; false if
     *                   they should go last
     * @return True if the test was successful; false otherwise
     */
    public static boolean doATest(boolean zerosFirst) {

        // Get a currency cent and a currency zero.
        final Currency cent = Currency.getCent();
        final Currency zero = Currency.getZero();

        // What is ten? What is one one-hundredth of a cent?
        final double ten = 10.;
        final double hundredth = Currency.getCent().getValue() /
                Math.pow(ten, 2.);

        /*
         * What is one tenth of a cent? What is half a cent? Initialize a
         * weight value variable based on the zeros-first flag.
         */
        final double tenth = hundredth * ten;
        final double half = tenth * 5.;
        double value = zerosFirst ? half : half - hundredth;

        // Declare and initialize a weight list. Declare an index.
        final List<Double> weights = new ArrayList<>();
        int i;

        /*
         * Cycle for some arbitrary weight count (note the '<=' used here as
         * well as the variable name).
         */
        final int oneLessThanWeightCount = 7;
        for (i = 0; i <= oneLessThanWeightCount; ++i) {

            /*
             * Add a new value to the weight list, then update the value for
             * the next iteration.
             */
            weights.add(value);
            value += zerosFirst ? hundredth : -hundredth;
        }

        /*
         * Declare a new re-allocator with the calculated weight list. Declare
         * and initialize a currency list. Cycle for one less than the weight
         * count (note '<' used here).
         */
        final Reallocator reallocator = new Reallocator(weights);
        final List<MutableCurrency> currencies = new ArrayList<>();
        for (i = 0; i < oneLessThanWeightCount; ++i) {

            // Add a new mutable currency to the list using zero.
            currencies.add(new MutableCurrency(zero));
        }

        /*
         * There is one more mutable currency to add to the list. Do that, but
         * initialize it to a value which will leave only a certain number of
         * zeros and a certain number of cents when the list is reallocated.
         * Add that last currency.
         */
        final int zeros = 2;
        final int cents = oneLessThanWeightCount + 1 - zeros;
        currencies.add(new MutableCurrency(cents * half * 2.));

        /*
         * Reallocate the currencies, and get an iterator for the currency
         * list.
         */
        reallocator.reallocate(currencies);
        final Iterator<MutableCurrency> iterator = currencies.iterator();

        // Declare a boolean to receive the test result. Should zeros go first?
        boolean looksGood;
        if (zerosFirst) {

            // Zeros should go first. Check for them, then check for cents.
            looksGood = check(iterator, zero, zeros);
            looksGood = check(iterator, cent, cents) && looksGood;
        }

        // Zeros should go last.
        else {

            // Check for cents first, then check for zeros.
            looksGood = check(iterator, cent, cents);
            looksGood = check(iterator, zero, zeros) && looksGood;
        }

        // Return the test result to our caller.
        return looksGood;
    }

    /**
     * Tests the reallocator.
     *
     * @param arguments Command line arguments
     */
    public static void main(@NotNull String[] arguments) {

        // Do a test with zeros first. Did it succeed?
        boolean looksGood = doATest(true);
        if (looksGood) {

            // The test succeeded. Do a test with zeros last.
            looksGood = doATest(false);
        }

        // Print the result of the test(s).
        //noinspection SpellCheckingInspection
        System.out.printf("Things %slook good.\n", looksGood ? "" : "don't ");
    }

    /**
     * Sums a collection given the collection and a value extractor.
     *
     * @param collection The collection
     * @param extractor  A value extractor
     * @param <T>        Any type from which value can be extracted
     * @return The sum of value in the given collection
     */
    private static <T> double sum(@NotNull Collection<? extends T> collection,
                                  @NotNull ValueExtractor<T> extractor) {

        // Initialize the result and cycle for each element in the collection.
        double result = 0.;
        for (T element : collection) {

            /*
             * Use the value extractor to extract value from the first/next
             * element. Add the value to the sum.
             */
            result += extractor.getValue(element);
        }

        // Return the result.
        return result;
    }

    /**
     * Determines whether a reallocation can be performed.
     *
     * @return True if a reallocation can be performed; false otherwise
     */
    public boolean canReallocate() {
        return (0. < getWeightSum());
    }

    /**
     * Gets the sum of the weights.
     *
     * @return The sum of the weights
     */
    public double getWeightSum() {
        return weightSum;
    }

    /**
     * Reallocates amounts in a list of countables based on the weights
     * contained in the reallocator.
     *
     * @param countables A list of countables, all the same type
     * @param <T>        The type of the countables
     */
    public <T extends MutableCountable>
    void reallocate(@NotNull List<T> countables) {

        /*
         * Check that the weights array has no negatives. Note: We check this
         * on every reallocation because our caller has access to the weight
         * list, and may have modified it. Sum the weights. Is the sum of the
         * weights zero?
         */
        checkNoNegatives(weights);
        final double weightsSum = getWeightSum();
        if (0. == weightsSum) {

            /*
             * The sum of the weights is zero. Throw a new illegal argument
             * exception.
             */
            throw new IllegalArgumentException("The sum of weights may not " +
                    "be zero.");
        }

        /*
         * Determine the number of countables. Track the smallest of this and
         * the number of weights.
         */
        final int countablesSize = countables.size();
        final int smallerSize = Math.min(countablesSize, weights.size());

        /*
         * Sum the countables, and construct a list builder with the
         * sum of countables divided by the sum of weights.
         */
        final double countablesSum = sum(countables, forCountables);
        final ListBuilder builder = new ListBuilder(countablesSum /
                weightsSum);

        // Cycle for each countable that has a corresponding weight.
        int i;
        for (i = 0; i < smallerSize; ++i) {

            /*
             * Add the first/next countable to the list builder along with the
             * corresponding weight for the countable.
             */
            builder.add(countables.get(i), weights.get(i));
        }

        // Add any remaining countables, if any, giving them zero weight.
        for (; i < countablesSize; ++i) {
            builder.add(countables.get(i), 0.);
        }

        // Get the list from the list builder. Is it not empty?
        final List<Pair<Double, MutableCountable>> list = builder.getList();
        if (!list.isEmpty()) {

            /*
             * The list from the list builder is not empty. Get the factor of
             * the first countable. The factor will be the same for all the
             * countables since the countable type of this method is
             * parameterized. Calculate the unit from the factor.
             */
            final double factor = list.get(0).getSecond().getFactor();
            final double unit = 1. / factor;

            /*
             * Use the factor to calculate the number of countables that need
             * augmentation (positive difference), or diminution (negative
             * difference). Is the difference not zero?
             */
            int difference = (int) ((countablesSum - sum(countables,
                    forCountables)) * factor);
            if (0 != difference) {

                /*
                 * The difference is not zero. Determine if we need to add, or
                 * subtract value.
                 */
                final int addOrSubtract = (difference < 0.) ? -1 : 1;

                /*
                 * Sort the list based on how much was added, or taken away
                 * during rounding of each countable. If the difference is
                 * negative, it means we need to subtract value. So sort the
                 * values so that those with most added occur first.
                 */
                list.sort((addOrSubtract < 0.) ?
                        (first, second) -> (int) Math.signum(first.getFirst() -
                                second.getFirst()) :

                        /*
                         * ...or if the difference is positive, it means we
                         * need to add value. Sort so that values so that those
                         * with most taken away occur first.
                         */
                        (first, second) -> (int) Math.signum(second.getFirst() -
                                first.getFirst()));

                /*
                 * Declare a variable to receive the first/next countable to
                 * receive more (or less) value. Reinitialize the list index.
                 * Cycle while difference remains.
                 */
                MutableCountable countable;
                i = 0;
                while (0 != difference) {

                    /*
                     * Get the first/next countable (making sure to increment
                     * the index), then add or subtract value.
                     */
                    countable = list.get(i++).getSecond();
                    countable.set(countable.getValue() + (unit * addOrSubtract));
                    difference -= addOrSubtract;
                }
            }
        }
    }

    private interface ValueExtractor<T> {

        /**
         * Gets value from the argument.
         *
         * @param object An argument of the indicated type
         * @return Value derived from the argument
         */
        double getValue(@NotNull T object);
    }

    private static class ListBuilder {

        // A factor to apply to each weight when setting value in a countable
        private final double factor;

        /*
         * A list to receive a pair of values: First the amount added, or
         * subtracted from the countable; second the countable itself
         */
        private final List<Pair<Double, MutableCountable>> list =
                new ArrayList<>();

        /**
         * Constructs the list builder.
         *
         * @param factor A factor to apply to each weight when setting value in
         *               a countable
         */
        public ListBuilder(double factor) {
            this.factor = factor;
        }

        /**
         * Adds a countable with a given weight.
         *
         * @param countable The countable to add
         * @param weight    The weight for the countable
         */
        public void add(@NotNull MutableCountable countable, double weight) {

            /*
             * Calculate the new value for the countable as the given weight
             * multiplied by the factor. Set the value, then add a pair
             * consisting of: 1) The difference of what we tried to set, and
             * the value actually set in the countable, and; 2) The countable
             * itself.
             */
            final double newValue = weight * factor;
            countable.set(newValue);
            list.add(new Pair<>(newValue - countable.getValue(), countable));
        }

        /**
         * Gets the list.
         *
         * @return The list
         */
        public @NotNull List<Pair<Double, MutableCountable>> getList() {
            return list;
        }
    }
}
