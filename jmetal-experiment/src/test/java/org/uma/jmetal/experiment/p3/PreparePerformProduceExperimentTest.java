package org.uma.jmetal.experiment.p3;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.PreparePerformProduceExperiment;

public class PreparePerformProduceExperimentTest {

	enum Step {
		PREPARE, PERFORM, PRODUCE
	}

	@Test
	public void testAllStepsAreExecuted() {
		Collection<Step> stepExecuted = new LinkedList<>();
		PreparePerformProduceExperiment<Collection<Algorithm<?>>, Void> experiment = new PreparePerformProduceExperiment<Collection<Algorithm<?>>, Void>() {

			@Override
			protected Collection<Algorithm<?>> prepare() {
				stepExecuted.add(Step.PREPARE);
				return Collections.emptyList();
			}

			@Override
			protected Void perform(Collection<Algorithm<?>> algorithms) {
				stepExecuted.add(Step.PERFORM);
				return null;
			}

			@Override
			protected void produce(Void dataset) {
				stepExecuted.add(Step.PRODUCE);
			}

		};

		experiment.run();
		assertTrue(stepExecuted.contains(Step.PREPARE));
		assertTrue(stepExecuted.contains(Step.PERFORM));
		assertTrue(stepExecuted.contains(Step.PRODUCE));
	}

	@Test
	public void testStepsAreExecutedInOrder() {
		Step[] lastStepExecuted = { null };
		PreparePerformProduceExperiment<Collection<Algorithm<?>>, Void> experiment = new PreparePerformProduceExperiment<Collection<Algorithm<?>>, Void>() {

			@Override
			protected Collection<Algorithm<?>> prepare() {
				assertEquals(null, lastStepExecuted[0]);
				lastStepExecuted[0] = Step.PREPARE;
				return Collections.emptyList();
			}

			@Override
			protected Void perform(Collection<Algorithm<?>> algorithms) {
				assertEquals(Step.PREPARE, lastStepExecuted[0]);
				lastStepExecuted[0] = Step.PERFORM;
				return null;
			}

			@Override
			protected void produce(Void dataset) {
				assertEquals(Step.PERFORM, lastStepExecuted[0]);
				lastStepExecuted[0] = Step.PRODUCE;
			}
		};

		experiment.run();
	}

}
