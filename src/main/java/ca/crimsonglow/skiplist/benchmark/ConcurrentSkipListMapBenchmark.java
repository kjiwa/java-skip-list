package ca.crimsonglow.skiplist.benchmark;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@State(Scope.Thread)
public class ConcurrentSkipListMapBenchmark extends AbstractMapBenchmarkBase {
    @Override
    public Map<Integer, Integer> newMap() {
        return new ConcurrentSkipListMap<>();
    }
}
