package ca.crimsonglow.skiplist.benchmark.runner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.RunnerException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBenchmarkRunnerBase {
    protected abstract String getResultsCsvFilename();

    protected abstract String[] getResultsCsvHeader();

    protected abstract Object[] getRecord(Map.Entry<String, Collection<RunResult>> entry, RunResult result);

    protected abstract void run() throws RunnerException, IOException;

    protected void report(Collection<RunResult> results) throws IOException {
        // Aggregate results by benchmark.
        Map<String, Collection<RunResult>> resultsByBenchmark = new HashMap<>();
        for (RunResult result : results) {
            String benchmark = result.getParams().getBenchmark();
            if (!resultsByBenchmark.containsKey(benchmark)) {
                resultsByBenchmark.put(benchmark, new ArrayList<>());
            }

            resultsByBenchmark.get(benchmark).add(result);
        }

        // Write results to CSV.
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(getResultsCsvFilename()));
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(getResultsCsvHeader()));
        for (Map.Entry<String, Collection<RunResult>> entry : resultsByBenchmark.entrySet()) {
            for (RunResult result : entry.getValue()) {
                printer.printRecord(getRecord(entry, result));
            }
        }

        printer.flush();
    }
}
