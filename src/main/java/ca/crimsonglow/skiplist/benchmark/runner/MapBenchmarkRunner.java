package ca.crimsonglow.skiplist.benchmark.runner;

import ca.crimsonglow.skiplist.benchmark.*;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class MapBenchmarkRunner extends AbstractBenchmarkRunnerBase {
    private static final String RESULTS_CSV_FILENAME = "benchmarks-maps.csv";
    private static final String[] RESULTS_CSV_HEADER = {"Benchmark", "Time (ns)"};

    public static void main(String[] args) throws IOException, RunnerException {
        new MapBenchmarkRunner().run();
    }

    @Override
    public void run() throws RunnerException, IOException {
        Options opts = new OptionsBuilder()
                .forks(1)
                .include(SkipListBenchmark.class.getSimpleName())
                .include(ConcurrentHashMapBenchmark.class.getSimpleName())
                .include(ConcurrentSkipListMapBenchmark.class.getSimpleName())
                .include(HashMapBenchmark.class.getSimpleName())
                .include(HashtableBenchmark.class.getSimpleName())
                .include(LinkedHashMapBenchmark.class.getSimpleName())
                .include(TreeMapBenchmark.class.getSimpleName())
                .build();

        report(new Runner(opts).run());
    }

    @Override
    protected String getResultsCsvFilename() {
        return RESULTS_CSV_FILENAME;
    }

    @Override
    protected String[] getResultsCsvHeader() {
        return RESULTS_CSV_HEADER;
    }

    @Override
    protected Object[] getRecord(Map.Entry<String, Collection<RunResult>> entry, RunResult result) {
        return new Object[]{entry.getKey(), result.getPrimaryResult().getScore()};
    }
}
