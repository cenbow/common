package kelly.monitor.core;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Utility class that provides common, generally useful aggregators.
 */
public final class Aggregators {

    /** Aggregator that sums up all the data points. */
    public static final Aggregator SUM = new Sum();

    /** Aggregator that returns the minimum data point. */
    public static final Aggregator MIN = new Min();

    /** Aggregator that returns the maximum data point. */
    public static final Aggregator MAX = new Max();

    /** Aggregator that returns the average value of the data point. */
    public static final Aggregator AVG = new Avg();

    /** Aggregator that returns the Standard Deviation of the data points. */
    public static final Aggregator STDDEV = new StdDev();

    /** Maps an aggregator name to its instance. */
    private static final Aggregator[] aggregators = new Aggregator[] { SUM, MIN, MAX, AVG, STDDEV };

    private Aggregators() {
        // Can't create instances of this utility class.
    }

    /**
     * Returns the set of the names that can be used with {@link #get get}.
     */
    public static Set<String> set() {
        Set<String> set = new HashSet<String>(AggregatorType.values().length);
        for (AggregatorType type : AggregatorType.values()) {
            set.add(type.name());
        }
        return set;
    }

    /**
     * Returns the aggregator corresponding to the given name.
     *
     * @param type The name of the aggregator to get.
     * @throws NoSuchElementException if the given name doesn't exist.
     * @see #set
     */
    public static Aggregator get(AggregatorType type) {
        return aggregators[type.ordinal()];
    }

    private static abstract class InterpolationAggregator implements Aggregator {

        protected final Interpolation interpolation;

        public InterpolationAggregator(Interpolation interpolation) {
            this.interpolation = interpolation;
        }

        @Override
        public Interpolation interpolation() {
            return interpolation;
        }
    }

    private static final class Sum extends InterpolationAggregator {

        public Sum() {
            super(Interpolation.LERP);
        }

        @Override
        public float run(Floats values) {
            float result = values.nextValue();
            while (values.hasNextValue()) {
                result += values.nextValue();
            }
            return result;
        }

        @Override
        public String toString() {
            return "SUM";
        }
    }

    private static final class Min extends InterpolationAggregator {

        public Min() {
            super(Interpolation.LERP);
        }

        @Override
        public float run(Floats values) {
            float min = values.nextValue();
            while (values.hasNextValue()) {
                float val = values.nextValue();
                if (val < min) {
                    min = val;
                }
            }
            return min;
        }

        @Override
        public String toString() {
            return "MIN";
        }
    }

    private static final class Max extends InterpolationAggregator {

        public Max() {
            super(Interpolation.LERP);
        }

        @Override
        public float run(Floats values) {
            float max = values.nextValue();
            while (values.hasNextValue()) {
                float val = values.nextValue();
                if (val > max) {
                    max = val;
                }
            }
            return max;
        }

        @Override
        public String toString() {
            return "MAX";
        }

    }

    private static final class Avg extends InterpolationAggregator {
        public Avg() {
            super(Interpolation.LERP);
        }

        @Override
        public float run(Floats values) {
            float result = values.nextValue();
            int n = 1;
            while (values.hasNextValue()) {
                result += values.nextValue();
                n++;
            }
            return result / n;
        }

        @Override
        public String toString() {
            return "AVG";
        }
    }

    /**
     * Standard Deviation aggregator. Can compute without storing all of the
     * data points in memory at the same time. This implementation is based upon
     * a <a href="http://www.johndcook.com/standard_deviation.html">paper by
     * John D. Cook</a>, which itself is based upon a method that goes back to a
     * 1962 paper by B. P. Welford and is presented in Donald Knuth's Art of
     * Computer Programming, Vol 2, page 232, 3rd edition
     */
    private static final class StdDev extends InterpolationAggregator {

        public StdDev() {
            super(Interpolation.LERP);
        }

        @Override
        public float run(Floats values) {
            float oldMean = values.nextValue();
            if (!values.hasNextValue()) {
                return 0;
            }
            long n = 2;
            float newMean = 0;
            float variance = 0;
            do {
                final float x = values.nextValue();
                newMean = oldMean + (x - oldMean) / n;
                variance += (x - oldMean) * (x - newMean);
                oldMean = newMean;
                n++;
            } while (values.hasNextValue());

            return (float) Math.sqrt(variance / (n - 1));
        }

        @Override
        public String toString() {
            return "STDDEV";
        }
    }
}
