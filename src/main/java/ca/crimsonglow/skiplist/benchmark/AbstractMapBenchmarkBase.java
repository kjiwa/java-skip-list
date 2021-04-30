package ca.crimsonglow.skiplist.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public abstract class AbstractMapBenchmarkBase {
    private static final String DEFAULT_MAP_SIZE = "10000";
    protected final Random random = new Random();
    @Param({DEFAULT_MAP_SIZE})
    protected int mapSize;
    protected Map<Integer, Integer> map = null;
    private Integer nextKey = 0;

    protected abstract Map<Integer, Integer> newMap();

    @Setup(Level.Iteration)
    public void initializeMap() {
        map = newMap();
        while (map.size() < mapSize) {
            map.put(random.nextInt(), 1);
        }
    }

    @Setup(Level.Invocation)
    public void selectKey() {
        nextKey = random.nextInt();
    }

    @TearDown(Level.Invocation)
    public void resetMap() {
        while (map.size() < mapSize) {
            map.put(random.nextInt(), 1);
        }

        while (map.size() > mapSize) {
            map.remove(random.nextInt());
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void measurePut() {
        map.put(nextKey, 1);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void measureGet() {
        map.get(nextKey);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void measureRemove() {
        map.remove(nextKey);
    }
}
