package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.MutableCountable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Reallocator {

    // A value extractor for mutable countables
    private final ValueExtractor<MutableCountable> forCountables =
            MutableCountable::getValue;

    // A value extractor for doubles
    private final ValueExtractor<Double> forDoubles =
            object -> object;

    // A list of weights
    private final List<Double> weights;

    /**
     * Constructs the reallocator.
     * @param weights A list of weights to apply to any reallocation operation;
     *                this list may be modified by the caller before any subsequent reallocation
     */
    public Reallocator(@NotNull List<Double> weights) {
        this.weights = weights;
    }

    /**
     * Sums a collection given the collection an a value extractor.
     * @param collection The collection
     * @param extractor A value extractor
     * @param <T> Any type from which value can be extracted
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
     * Reallocates amounts in a list of countables based on the weights
     * contained in the reallocator.
     *
     * @param countables A list of countables, all of the same type
     * @param <T>        The type of the countables
     */
    public <T extends MutableCountable>
    void reallocate(@NotNull List<T> countables) {

        /*
         * Determine the number of countables, the number of weights, and the
         * difference between the two. Note: We recalculate the number of
         * weights and their sums repeatedly because our caller may have access
         * to the weight list, and may have modified it.
         */
        final int countablesSize = countables.size();
        final int weightSize = weights.size();
        final int smallerSize = Math.min(countablesSize, weightSize);

        /*
         * Sum the value in the countables, and construct a list builder with
         * the calculated sum divided by the sum of the weights.
         */
        final double countablesSum = sum(countables, forCountables);
        final ListBuilder builder =
                new ListBuilder(countablesSum / sum(weights, forDoubles));

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
