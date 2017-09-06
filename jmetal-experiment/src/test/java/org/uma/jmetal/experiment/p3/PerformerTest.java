package org.uma.jmetal.experiment.p3;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Performer;

public abstract class PerformerTest {
	
	protected abstract Performer createPerformer();

	@Test
	public void testExecuteWhatIsRequested() {
		Collection<Runnable> runnables = new HashSet<>();
		Collection<Runnable> run = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			runnables.add(new Runnable() {
				@Override
				public void run() {
					run.add(this);
				}
			});
		}

		Performer performer = createPerformer();
		for (Runnable runnable : runnables) {
			performer.request(runnable);
		}
		performer.runUntilAllRequestsAreTerminated();

		assertEquals(runnables, run);
	}

}
