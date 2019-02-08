package nl.cwi.swat.benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MicroBenchmark {
  public final List<Long> runBenchmark(int warmupRuns, int runs) {
    setup();

    System.out.printf("Warming up (%d runs) ", warmupRuns);
    beforeWarmup();
    doWarmup(warmupRuns);
    afterWarmup();
    System.out.print(" done\n");

    System.out.printf("Benchmarking (%d runs) ", runs);
    beforeRun();
    List<Long> benchmarkTimes = doRun(runs);
    afterRun();
    System.out.print(" done\n");

    return benchmarkTimes;
  }

  protected void setup() {}

  protected void afterRun() {}

  private final List<Long> doRun(int runs) {
    List<Long> times = new ArrayList<>();

    for (int i = 0; i < runs; i++) {
      System.out.print(".");

      beforeRun();

      long startTime = getCpuTime();
      singleRun(i);
      long endTime = getCpuTime();

      times.add((endTime - startTime) / 1000000);

      afterRun();
    }

    times.sort(Comparator.comparingLong(a -> a));

    return times;
  }

  protected abstract void singleRun(int currentRunNr);

  protected void beforeRun() {}

  protected void afterWarmup() {};

  private final void doWarmup(int warmupRuns) {
    for (int i = 0; i < warmupRuns; i++) {
      beforeWarmup();

      System.out.print(".");

      warmup();

      afterWarmup();
    }
  }

  protected abstract void warmup();

  protected void beforeWarmup() {};

  protected static long getCpuTime( ) {
    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
    return bean.isCurrentThreadCpuTimeSupported( ) ?
            bean.getCurrentThreadCpuTime( ) : 0L;
  }

  private static boolean even(List<?> times) {
    return even(times.size());
  }

  private static boolean even(int n) {
    return n % 2 == 0;
  }

  public static long mean(List<Long> times) {
    int middle = times.size() / 2;

    if (even(times)) {
      // Even number of measurements, mean is between size/2 - 1 and size/2
      return (times.get(middle - 1) + times.get(middle)) / 2;
    } else {
      // Odd number of measurements, mean is size/2
      return times.get(middle);
    }
  }

  public static long average(List<Long> times) {
    return times.parallelStream().mapToLong(Long::longValue).sum() / times.size();
  }

  public static long ninetyPercent(List<Long> times) {
    return times.get((int)((times.size() / 100f) * 90f));
  }

  public static long min(List<Long> times) {
    return times.get(0);
  }

  public static long max(List<Long> times) {
    return times.get(times.size()-1);
  }

  /**
   * Calculates the first quartile based on the 'TI-83' method.
   * This means that if the list has a odd number of measurements, the measurement set of the first quartile will NOT include the mean
   *
   * @param times - Sorted list of measurements
   * @return the measurement representing the first quartile bound
   */
  public static long firstQuartile(List<Long> times) {
    if (times.size() < 2) {
      return times.get(0);
    }

    int middle = times.size() / 2;
    int quarter = middle / 2;

    if (even(middle)) {
      return (times.get(quarter - 1) + times.get(quarter)) / 2;
    } else {
      return times.get(quarter); // make use of the default int flooring
    }
  }

  public static long thirdQuartile(List<Long> times) {
    if (times.size() < 2) {
      return times.get(0);
    }

    int middle = times.size() / 2;
    int quarter = middle / 2;

    if (even(middle)) {
      return (times.get(middle + quarter - 1) + times.get(middle + quarter)) / 2;
    } else {
      return times.get(middle + quarter); // make use of the default int flooring
    }
  }

}
