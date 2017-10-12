package util;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;

public final class TerseThreadFactory implements ForkJoinWorkerThreadFactory
{
  @Override           
  public ForkJoinWorkerThread newThread(ForkJoinPool pool)
  {
      final ForkJoinWorkerThread worker = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
      worker.setName("thread-" + worker.getPoolIndex());
      return worker;
  }
}