package ca.crimsonglow.skiplist.benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.Map;
import java.util.TreeMap;

@State(Scope.Thread)
public class TreeMapBenchmark extends AbstractMapBenchmarkBase {
    @Override
    public Map<Integer, Integer> newMap() {
        return new TreeMap<>();
    }
}
