package ca.crimsonglow.skiplist.benchmark.runner;

import ca.crimsonglow.skiplist.benchmark.HashMapBenchmark;
import ca.crimsonglow.skiplist.benchmark.SkipListBenchmark;
import ca.crimsonglow.skiplist.benchmark.TreeMapBenchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class MapSizeBenchmarkRunner extends AbstractBenchmarkRunnerBase {
    private static final String RESULTS_CSV_FILENAME = "benchmarks-mapSize.csv";
    private static final String[] RESULTS_CSV_HEADER = {"Benchmark", "Map Size", "Time (ns)"};
    private static final int MAP_SIZE_MIN = 0;
    private static final int MAP_SIZE_MAX = 25000;
    private static final int MAP_SIZE_STEP = 1000;
    private static final String MAP_SIZE_PARAM_NAME = "mapSize";

    public static void main(String[] args) throws RunnerException, IOException {
        new MapSizeBenchmarkRunner().run();
    }

    @Override
    protected void run() throws RunnerException, IOException {
        createReport();
        for (int i = MAP_SIZE_MIN; i <= MAP_SIZE_MAX; i += MAP_SIZE_STEP) {
            Options opts = new OptionsBuilder()
                    .forks(1)
                    .include(HashMapBenchmark.class.getCanonicalName())
                    .include(SkipListBenchmark.class.getCanonicalName())
                    .include(TreeMapBenchmark.class.getCanonicalName())
                    .param(MAP_SIZE_PARAM_NAME, new String[]{Integer.toString(i)})
                    .build();

            appendResults(new Runner(opts).run());
        }
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
        return new Object[]{entry.getKey(), result.getParams().getParam(MAP_SIZE_PARAM_NAME), result.getPrimaryResult().getScore()};
    }
}
