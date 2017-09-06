package org.uma.jmetal.experiment.p3;

import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Performer;

/**
 * A {@link SequentialPerformer} is a {@link Performer} which executes the
 * received runnable one after the other, in the order of their reception.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public class SequentialPerformer implements Performer {

	@Override
	public void request(Runnable runnable) {
		/*
		 * We run them immediately to avoid storing many objects in a Collection.
		 * Because each Runnable should be independent, there should have no issue. If
		 * there is, it might be better to store them here and run them only in the
		 * other method.
		 */
		runnable.run();
	}

	@Override
	public void runUntilAllRequestsAreTerminated() {
		// All requests are already terminated when the other method returns.
	}

}
