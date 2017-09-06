package org.uma.jmetal.experiment.p3;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Performer;

public class ThreadPerformerTest extends PerformerTest {

	@Override
	protected Performer createPerformer() {
		return new ThreadPerformer();
	}

	@Test
	public void testPerformsInParallel() {
		int occurrences = 100;
		int millisecondsPerOccurrence = 10;

		Collection<Runnable> runnables = new LinkedList<>();
		final int[] running = { 0 };
		final int[] max = { 0 };
		for (int i = 0; i < occurrences; i++) {
			runnables.add(new Runnable() {
				@Override
				public void run() {
					synchronized (running) {
						running[0]++;
						synchronized (max) {
							max[0] = Math.max(max[0], running[0]);
						}
					}
					try {
						Thread.sleep(millisecondsPerOccurrence);
					} catch (InterruptedException cause) {
						throw new RuntimeException(cause);
					}
					synchronized (running) {
						running[0]--;
					}
				}
			});
		}

		ThreadPerformer performer = new ThreadPerformer(occurrences);
		for (Runnable runnable : runnables) {
			performer.request(runnable);
		}
		performer.runUntilAllRequestsAreTerminated();

		assertTrue("At most " + max[0] + " threads at once", max[0] >= 2);
	}

}
