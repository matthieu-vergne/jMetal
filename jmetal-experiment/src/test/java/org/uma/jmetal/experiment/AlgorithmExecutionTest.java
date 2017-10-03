package org.uma.jmetal.experiment;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.AlreadyRunException;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.AlgorithmExecutionListener;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;

public abstract class AlgorithmExecutionTest {

	public abstract <Algorithm extends Runnable> AlgorithmExecution<Algorithm> createAlgorithmExecution(Algorithm algorithm);

	@Test
	public void testAlgorithmIsRetrieved() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});

		assertNotNull(execution.getAlgorithm());
	}

	@Test
	public void testRunStatusStartsWaiting() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});

		assertEquals(RunStatus.WAITING, execution.getRunStatus());
	}

	@Test
	public void testRunStatusWhenRunning() {
		final RunStatus[] statusWhenRunning = { null };
		final AlgorithmExecution<?>[] executions = { null };
		Runnable algorithm = new Runnable() {

			@Override
			public void run() {
				statusWhenRunning[0] = executions[0].getRunStatus();
			}
		};
		AlgorithmExecution<?> experimentRun = createAlgorithmExecution(algorithm);
		executions[0] = experimentRun;

		experimentRun.run();
		assertEquals(RunStatus.RUNNING, statusWhenRunning[0]);
	}

	@Test
	public void testRunStatusFinishOnTerminatedIfAlgorithmRunsSmoothly() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});

		execution.run();
		assertEquals(RunStatus.TERMINATED, execution.getRunStatus());
	}

	@Test
	public void testRunStatusFinishOnInterruptedIfAlgorithmFails() {
		AlgorithmExecution<?> execution = createAlgorithmExecution(new Runnable() {

			@Override
			public void run() {
				throw new RuntimeException();
			}
		});

		try {
			execution.run();
		} catch (Exception e) {
		}
		assertEquals(RunStatus.INTERRUPTED, execution.getRunStatus());
	}

	@Test
	public void testRunTwiceThrowsException() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});

		execution.run();
		try {
			execution.run();
			fail("No exception thrown");
		} catch (AlreadyRunException e) {
		}
	}

	@Test
	public void testRunStatusNotificationsAreProvided() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});
		final List<RunStatus> states = new LinkedList<>();
		AlgorithmExecutionListener listener = (newStatus) -> states.add(newStatus);
		execution.addRunListener(listener);

		execution.run();
		assertEquals(RunStatus.RUNNING, states.remove(0));
		while (states.size() > 1) {
			assertEquals(RunStatus.PAUSED, states.remove(0));
			assertEquals(RunStatus.RUNNING, states.remove(0));
		}
		List<RunStatus> acceptable = Arrays.asList(RunStatus.TERMINATED, RunStatus.INTERRUPTED);
		assertTrue(acceptable.contains(states.remove(0)));
	}

	@Test
	public void testRunStatusNotificationsCorrespondsToCurrentStatus() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});
		final boolean[] listenerCalled = { false };
		AlgorithmExecutionListener listener = (newStatus) -> {
			assertEquals(execution.getRunStatus(), newStatus);
			listenerCalled[0] = true;
		};
		execution.addRunListener(listener);

		execution.run();
		assertTrue("The test is not valid because the listener has not been properly used.", listenerCalled[0]);
	}

	@Test
	public void testRunStatusNotificationsNotProvidedAfterUnregistering() {
		final AlgorithmExecution<Runnable> execution = createAlgorithmExecution(() -> {
			/* Do nothing */});
		final boolean[] listenerCalled = { false };
		AlgorithmExecutionListener listener = (newStatus) -> listenerCalled[0] = true;
		execution.addRunListener(listener);
		execution.removeRunListener(listener);

		execution.run();
		assertFalse(listenerCalled[0]);
	}
}
