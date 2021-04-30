package ca.crimsonglow.skiplist.benchmark;

import ca.crimsonglow.skiplist.SkipList;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.Map;

@State(Scope.Thread)
public class SkipListBenchmark extends AbstractMapBenchmarkBase {
    private static final String DEFAULT_ITERATION_PROBABILITY = "0.2";
    @Param({DEFAULT_ITERATION_PROBABILITY})
    private double iterationProbability;

    @Override
    public Map<Integer, Integer> newMap() {
        return new SkipList<>(iterationProbability);
    }
}
