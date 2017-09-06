package org.uma.jmetal.experiment.p3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Performer;

/**
 * A {@link ThreadPerformer} is a {@link Performer} which executes its
 * {@link Runnable} instances in parallel through separate threads.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public class ThreadPerformer implements Performer {

	private final ExecutorService pool;

	/**
	 * Create a {@link ThreadPerformer} with a customized level of parallelism.
	 * 
	 * @param maxThreads
	 *            the number of threads to run at once
	 */
	public ThreadPerformer(int maxThreads) {
		this.pool = Executors.newFixedThreadPool(maxThreads);
	}

	/**
	 * Create a {@link ThreadPerformer} with a number of threads determined
	 * automatically. The value used is the number of cores, as returned by
	 * <code>Runtime.getRuntime().availableProcessors()</code>.
	 */
	public ThreadPerformer() {
		this(Runtime.getRuntime().availableProcessors());
	}

	@Override
	public void request(Runnable runnable) {
		pool.submit(() -> runnable.run());
	}

	@Override
	public void runUntilAllRequestsAreTerminated() {
		pool.shutdown();
		try {
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
		} catch (InterruptedException cause) {
			throw new RuntimeException(cause);
		}
	}

}
