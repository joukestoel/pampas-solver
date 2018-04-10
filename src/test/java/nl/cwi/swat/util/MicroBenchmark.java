package nl.cwi.swat.util;

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
      if (i % (runs / 10) == 0) {
        System.out.print(".");
      }

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

      if (i % (warmupRuns / 10) == 0) {
        System.out.print(".");
      }

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

  public static long mean(List<Long> times) {
    return times.get(times.size() / 2);
  }

  public static long average(List<Long> times) {
    return times.parallelStream().mapToLong(Long::longValue).sum() / times.size();
  }

  public static long ninetyPercent(List<Long> times) {
    return times.get(Math.round((times.size() / 100f) * 90f));
  }

  public static long min(List<Long> times) {
    return times.get(0);
  }

  public static long max(List<Long> times) {
    return times.get(times.size()-1);
  }

}
