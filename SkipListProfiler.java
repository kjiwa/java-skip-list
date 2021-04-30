/*
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SkipListProfiler {
  private static interface Operation {
    void setUp(Map<Integer, Integer> skipList);
    void execute(Map<Integer, Integer> skipList);
    void cleanUp(Map<Integer, Integer> skipList);
  }

  private static class PutIntegerOperation implements Operation {
    protected Random r;
    protected Integer keyToAdd = null;
    private Integer prevValue = null;

    public PutIntegerOperation(Random r) {
      this.r = r;
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      keyToAdd = r.nextInt();
    }

    @Override
    public void execute(Map<Integer, Integer> skipList) {
      prevValue = skipList.put(keyToAdd, keyToAdd);
    }

    @Override
    public void cleanUp(Map<Integer, Integer> skipList) {
      if (prevValue == null) {
        skipList.remove(keyToAdd);
        prevValue = null;
      }
    }
  }

  private static class PutNewIntegerOperation extends PutIntegerOperation {
    public PutNewIntegerOperation(Random r) {
      super(r);
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      do {
        keyToAdd = r.nextInt();
      } while (skipList.containsKey(keyToAdd));
    }
  }

  private static class PutExistingIntegerOperation extends PutIntegerOperation {
    private List<Integer> keys = null;

    public PutExistingIntegerOperation(Random r) {
      super(r);
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      if (keys == null) {
        keys = new ArrayList<>(skipList.keySet());
      }

      keyToAdd = keys.get(r.nextInt(keys.size()));
    }
  }

  public static class GetIntegerOperation implements Operation {
    protected Random r;
    protected Integer keyToGet = null;

    public GetIntegerOperation(Random r) {
      this.r = r;
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      keyToGet = r.nextInt();
    }

    @Override
    public void execute(Map<Integer, Integer> skipList) {
      skipList.get(keyToGet);
    }

    @Override
    public void cleanUp(Map<Integer, Integer> skipList) {
    }
  }

  public static class GetExistingIntegerOperation extends GetIntegerOperation {
    private List<Integer> keys = null;

    public GetExistingIntegerOperation(Random r) {
      super(r);
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      if (keys == null) {
        keys = new ArrayList<>(skipList.keySet());
      }

      keyToGet = keys.get(r.nextInt(keys.size()));
    }
  }

  public static class RemoveIntegerOperation implements Operation {
    protected Random r;
    protected Integer keyToRemove = null;
    private Integer prevValue = null;

    public RemoveIntegerOperation(Random r) {
      this.r = r;
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      keyToRemove = r.nextInt();
    }

    @Override
    public void execute(Map<Integer, Integer> skipList) {
      prevValue = skipList.remove(keyToRemove);
    }

    @Override
    public void cleanUp(Map<Integer, Integer> skipList) {
      if (prevValue != null) {
        skipList.put(keyToRemove, prevValue);
        prevValue = null;
      }
    }
  }

  public static class RemoveExistingIntegerOperation extends RemoveIntegerOperation {
    private List<Integer> keys = null;

    public RemoveExistingIntegerOperation(Random r) {
      super(r);
    }

    @Override
    public void setUp(Map<Integer, Integer> skipList) {
      if (keys == null) {
        keys = new ArrayList<>(skipList.keySet());
      }

      keyToRemove = keys.get(r.nextInt(keys.size()));
    }
  }

  private static final int NUM_RUNS = 5;
  private static final double PROBABILITY_STEP = 0.01;
  private static final int LIST_SIZE = 10000;
  private static final int NUM_SAMPLES = 10000;

  private static Map<Integer, Integer> createAndPopulateSkipList(Random r, double iterationProbability) {
    Map<Integer, Integer> skipList = new SkipList<>(iterationProbability);
    while (skipList.size() < LIST_SIZE) {
      skipList.put(r.nextInt(), 1);
    }

    return skipList;
  }

  private static double getAverageDuration(List<Long> durations) {
    long sum = 0;
    for (Long l : durations) {
      sum += l;
    }

    return (double) sum / NUM_SAMPLES;
  }

  private static void profile(Random r, Operation op) {
    Map<Double, List<Double>> results = new LinkedHashMap<>();
    for (int i = 0; i < NUM_RUNS; ++i) {
      for (double j = 0; j <= 1; j += PROBABILITY_STEP) {
        Map<Integer, Integer> skipList = createAndPopulateSkipList(r, j);
        List<Long> durations = new ArrayList<Long>(NUM_SAMPLES);
        for (int k = 0; k < NUM_SAMPLES; ++k) {
          op.setUp(skipList);
          Instant start = Instant.now();
          op.execute(skipList);
          durations.add(Duration.between(start, Instant.now()).toNanos());
          op.cleanUp(skipList);
        }

        if (!results.containsKey(j)) {
          results.put(j, new ArrayList<>(NUM_RUNS));
        }

        results.get(j).add(getAverageDuration(durations));
      }
    }

    for (Map.Entry<Double, List<Double>> entry : results.entrySet()) {
      System.out.print(entry.getKey() + ",");
      for (Double d : entry.getValue()) {
        System.out.print(d + ",");
      }

      System.out.println();
    }
  }

  public static void main(String[] args) {
    Random r = new Random();
    profile(r, new PutIntegerOperation(r));
    profile(r, new PutNewIntegerOperation(r));
    profile(r, new PutExistingIntegerOperation(r));
    profile(r, new GetIntegerOperation(r));
    profile(r, new GetExistingIntegerOperation(r));
    profile(r, new RemoveIntegerOperation(r));
    profile(r, new RemoveExistingIntegerOperation(r));
  }
}
