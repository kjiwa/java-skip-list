package ca.crimsonglow.skiplist.benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.HashMap;
import java.util.Map;

@State(Scope.Thread)
public class HashMapBenchmark extends AbstractMapBenchmarkBase {
    @Override
    public Map<Integer, Integer> newMap() {
        return new HashMap<>();
    }
}
