package kelly.monitor.core;

/**
 * A function capable of aggregating multiple {@link DataPoints} together.
 * <p>
 * All aggregators must be stateless. All they can do is run through a sequence
 * of {@link Floats Floats} and return an aggregated value.
 */
public interface Aggregator {

    public interface Floats {

        boolean hasNextValue();

        float nextValue();
    }

    /**
     * Aggregates a sequence of values
     *
     * @param values The sequence to aggregate.
     * @return The aggregated value.
     */
    float run(Floats values);

    /**
     * Return interpolation type for aggregation.
     *
     * @return Interpolation type
     */
    Interpolation interpolation();
}
