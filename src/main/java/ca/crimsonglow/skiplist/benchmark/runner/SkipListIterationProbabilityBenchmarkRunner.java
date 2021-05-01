package ca.crimsonglow.skiplist.benchmark.runner;

import ca.crimsonglow.skiplist.benchmark.SkipListBenchmark;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class SkipListIterationProbabilityBenchmarkRunner extends AbstractBenchmarkRunnerBase {
    private static final String ITERATION_PROBABILITY_PARAM_NAME = "iterationProbability";
    private static final double ITERATION_PROBABILITY_MIN = 0;
    private static final double ITERATION_PROBABILITY_MAX = 1;
    private static final double ITERATION_PROBABILITY_STEP = 0.01;
    private static final String RESULTS_CSV_FILENAME = "benchmarks-iterationProbability.csv";
    private static final String[] RESULTS_CSV_HEADER = {"Benchmark", "Iteration Probability", "Time (ns)"};

    public static void main(String[] args) throws RunnerException, IOException {
        new SkipListIterationProbabilityBenchmarkRunner().run();
    }

    @Override
    protected void run() throws RunnerException, IOException {
        createReport();
        for (double p = ITERATION_PROBABILITY_MIN; p <= ITERATION_PROBABILITY_MAX; p += ITERATION_PROBABILITY_STEP) {
            Options opts = new OptionsBuilder()
                    .forks(1)
                    .include(SkipListBenchmark.class.getSimpleName())
                    .param(ITERATION_PROBABILITY_PARAM_NAME, new String[]{Double.toString(p)})
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
        return new Object[]{entry.getKey(), result.getParams().getParam(ITERATION_PROBABILITY_PARAM_NAME), result.getPrimaryResult().getScore()};
    }
}
