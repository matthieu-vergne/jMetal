package org.uma.jmetal.experiment.p3;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Performer;

public class SequentialPerformerTest extends PerformerTest {

	@Override
	protected Performer createPerformer() {
		return new SequentialPerformer();
	}

	@Test
	public void testPerformsSequentially() {
		int occurrences = 100;
		int millisecondsPerOccurrence = 10;

		Collection<Runnable> runnables = new LinkedList<>();
		final Runnable[] running = { null };
		for (int i = 0; i < occurrences; i++) {
			runnables.add(new Runnable() {
				@Override
				public void run() {
					assertNull(running[0]);
					running[0] = this;
					try {
						Thread.sleep(millisecondsPerOccurrence);
					} catch (InterruptedException cause) {
						throw new RuntimeException(cause);
					}
					assertEquals(this, running[0]);
					running[0] = null;
				}
			});
		}

		SequentialPerformer performer = new SequentialPerformer();
		for (Runnable runnable : runnables) {
			performer.request(runnable);
		}
		performer.runUntilAllRequestsAreTerminated();
	}

}
