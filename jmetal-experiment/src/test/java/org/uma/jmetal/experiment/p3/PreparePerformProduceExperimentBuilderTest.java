package org.uma.jmetal.experiment.p3;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.AlgorithmID;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.DataID;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Generation;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.NoAlgorithmException;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.NoProblemException;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.NullAlgorithmException;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.NullPerformerException;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.ProblemID;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.When;

// TODO Add tests for independence between experiment instances (performer, algorithms, problems, etc.)
public class PreparePerformProduceExperimentBuilderTest {

	/* CLASSES USED FOR TESTING */

	class Problem {
	}

	@SuppressWarnings("serial")
	class Algorithm implements org.uma.jmetal.algorithm.Algorithm<Void> {

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public void run() {
			// Do nothing
		}

		@Override
		public Void getResult() {
			return null;
		}

	}

	/* BUILD */

	@Test
	public void testBuildInstantiatesRunnableExperimentWithProblemAndAlgorithmOnly() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());
		Experiment experiment = builder.build();
		experiment.run();
	}

	@Test
	public void testSeveralExperimentsCanBeBuiltAndRunWithoutThrowingException() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		Experiment reference = builder.build();
		Experiment builtBeforeRun = builder.build();
		reference.run();
		Experiment builtAfterRun = builder.build();
		builtBeforeRun.run();
		builtAfterRun.run();
	}

	@Test
	public void testBuildFailsToInstantiateExperimentWithProblemWithoutAlgorithm() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		try {
			builder.build();
			fail("No exception thrown");
		} catch (NoAlgorithmException cause) {
			// OK
		}
	}

	@Test
	public void testBuildFailsToInstantiateExperimentWithAlgorithmWithoutProblem() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareAlgorithm((problem) -> new Algorithm());
		try {
			builder.build();
			fail("No exception thrown");
		} catch (NoProblemException cause) {
			// OK
		}
	}

	@Test
	public void testBuildFailsToInstantiateExperimentWithoutProblemNorAlgorithm() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.build();
			fail("No exception thrown");
		} catch (NoProblemException | NoAlgorithmException cause) {
			// OK
		}
	}

	@Test
	public void testBuildFailsToInstantiateExperimentWithNullPerformer() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());
		builder.performWith(() -> null);
		try {
			builder.build();
			fail("No exception thrown");
		} catch (NullPerformerException cause) {
			// OK
		}
	}

	@Test
	public void testExperimentRunFailsOnNullAlgorithm() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> null);

		Experiment experiment = builder.build();
		try {
			experiment.run();
			fail("No exception thrown");
		} catch (NullAlgorithmException cause) {
			// OK
		}
	}

	/* PREPARE PHASE */

	@Test
	public void testAlgorithmsInstantiatedWhenExperimentRuns() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		LinkedList<Algorithm> instances1 = new LinkedList<>();
		builder.prepareAlgorithm((problem) -> {
			Algorithm instance = new Algorithm();
			instances1.add(instance);
			return instance;
		});

		LinkedList<Algorithm> instances2 = new LinkedList<>();
		builder.prepareAlgorithm((problem) -> {
			Algorithm instance = new Algorithm();
			instances2.add(instance);
			return instance;
		});

		builder.prepareProblem(() -> new Problem());
		builder.prepareProblem(() -> new Problem());
		builder.prepareProblem(() -> new Problem());

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(3, instances1.size());
		assertEquals(3, instances2.size());
	}

	@Test
	public void testAlgorithmsExecutedWhenExperimentRuns() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		Map<Algorithm, Boolean> executed = new HashMap<>();
		builder.prepareAlgorithm((problem) -> {
			@SuppressWarnings("serial")
			Algorithm algorithm = new Algorithm() {
				@Override
				public void run() {
					super.run();
					executed.put(this, true);
				}
			};
			executed.put(algorithm, false);
			return algorithm;
		});

		builder.prepareProblem(() -> new Problem());
		builder.prepareProblem(() -> new Problem());
		builder.prepareProblem(() -> new Problem());

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(3, executed.size());
		assertEquals(Arrays.asList(true, true, true), new LinkedList<>(executed.values()));
	}

	@Test
	public void testProblemsProperlyInstantiatedWhenExperimentRun() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		LinkedList<Problem> instances1 = new LinkedList<>();
		builder.prepareProblem(() -> {
			Problem instance = new Problem();
			instances1.add(instance);
			return instance;
		});

		LinkedList<Problem> instances2 = new LinkedList<>();
		builder.prepareProblem(() -> {
			Problem instance = new Problem();
			instances2.add(instance);
			return instance;
		});

		builder.prepareAlgorithm((problem) -> new Algorithm());
		builder.prepareAlgorithm((problem) -> new Algorithm());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(3, instances1.size());
		assertEquals(3, instances2.size());
	}

	@Test
	public void testRunIndexProperlyIncrementedForEachAlgorithmProblemCouple() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		ProblemID<Problem> p1 = builder.prepareProblem(() -> new Problem());
		ProblemID<Problem> p2 = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> a1 = builder.prepareAlgorithm((problem) -> new Algorithm());
		AlgorithmID<Algorithm> a2 = builder.prepareAlgorithm((problem) -> new Algorithm());
		List<Object> couple1 = Arrays.asList(a1, p1);
		List<Object> couple2 = Arrays.asList(a1, p2);
		List<Object> couple3 = Arrays.asList(a2, p1);
		List<Object> couple4 = Arrays.asList(a2, p2);

		Map<List<Object>, List<Integer>> runs = new HashMap<>();
		runs.put(couple1, new LinkedList<>());
		runs.put(couple2, new LinkedList<>());
		runs.put(couple3, new LinkedList<>());
		runs.put(couple4, new LinkedList<>());

		builder.prepareIndependentRuns(5);

		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			runs.get(Arrays.asList(context.getAlgorithmID(), context.getProblemID())).add(context.getRun());
		});

		Experiment experiment = builder.build();
		experiment.run();

		/*
		 * The increment is not necessarily done in order, so we only check the list
		 * contains them, not that they are in incremental order.
		 */
		assertTrue(runs.get(couple1).contains(0));
		assertTrue(runs.get(couple1).contains(1));
		assertTrue(runs.get(couple1).contains(2));
		assertTrue(runs.get(couple1).contains(3));
		assertTrue(runs.get(couple1).contains(4));
		assertEquals(5, runs.get(couple1).size());

		assertTrue(runs.get(couple2).contains(0));
		assertTrue(runs.get(couple2).contains(1));
		assertTrue(runs.get(couple2).contains(2));
		assertTrue(runs.get(couple2).contains(3));
		assertTrue(runs.get(couple2).contains(4));
		assertEquals(5, runs.get(couple2).size());

		assertTrue(runs.get(couple3).contains(0));
		assertTrue(runs.get(couple3).contains(1));
		assertTrue(runs.get(couple3).contains(2));
		assertTrue(runs.get(couple3).contains(3));
		assertTrue(runs.get(couple3).contains(4));
		assertEquals(5, runs.get(couple3).size());

		assertTrue(runs.get(couple4).contains(0));
		assertTrue(runs.get(couple4).contains(1));
		assertTrue(runs.get(couple4).contains(2));
		assertTrue(runs.get(couple4).contains(3));
		assertTrue(runs.get(couple4).contains(4));
		assertEquals(5, runs.get(couple4).size());
	}

	@Test
	public void testRejectsNullProblemInstantiator() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.prepareProblem(null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	@Test
	public void testRejectsNullAlgorithmInstantiator() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.prepareAlgorithm(null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	/* DATA DEFINITION */

	@Test
	public void testDataGeneratedIfIDUsed() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		@SuppressWarnings("serial")
		class LocalException extends RuntimeException {
		}

		DataID<Problem> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			throw new LocalException();
		});
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
		});

		Experiment experiment = builder.build();
		try {
			experiment.run();
			fail("Data not generated");
		} catch (LocalException cause) {
			// OK
		}
	}

	@Test
	public void testDataNotGeneratedIfIDNotUsed() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		@SuppressWarnings("serial")
		class LocalException extends RuntimeException {
		}

		builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			throw new LocalException();
		});

		Experiment experiment = builder.build();
		experiment.run();
	}

	@Test
	public void testDataProperlyRetrievedInRunEvent() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> "OK");
		Collection<String> data = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			data.add(context.getData(id));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(Arrays.asList("OK"), data);
	}

	@Test
	public void testDataProperlyRetrievedInRunDefinition() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		DataID<String> delegate = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> "OK");
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> context.getData(delegate));
		Collection<String> data = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			data.add(context.getData(id));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(Arrays.asList("OK"), data);
	}

	@Test
	public void testDataProperlyRetrievedInProducePhase() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> "OK");
		Collection<String> data = new LinkedList<>();
		builder.produceWith((context) -> {
			data.add(context.getData(algorithmID, problemID, 0, id));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(Arrays.asList("OK"), data);
	}

	@Test
	public void testDataProperlyBackupWhenRequested() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		int[] generationCounter = { 0 };
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			generationCounter[0]++;
			return "OK";
		});
		Collection<String> data = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			data.add(context.getData(id));
			data.add(context.getData(id));
			data.add(context.getData(id));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(Arrays.asList("OK", "OK", "OK"), data);
		assertEquals(1, generationCounter[0]);
	}

	@Test
	public void testDataProperlyRecreatedWhenRequested() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		int[] generationCounter = { 0 };
		DataID<String> id = builder.defineRunData(Generation.EVERY_CALL, (context) -> {
			generationCounter[0]++;
			return "OK";
		});
		Collection<String> data = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			data.add(context.getData(id));
			data.add(context.getData(id));
			data.add(context.getData(id));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(Arrays.asList("OK", "OK", "OK"), data);
		assertEquals(3, generationCounter[0]);
	}

	@Test
	public void testAlgorithmInstanceRetrievedThroughContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Algorithm> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getAlgorithm());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getAlgorithm());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getAlgorithm());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getAlgorithm(algorithmID, problemID, 0));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertNotNull(checks.get(DEFINITION));
		assertNotNull(checks.get(PRE_RUN));
		assertNotNull(checks.get(POST_RUN));
		assertNotNull(checks.get(PRODUCE));
	}

	@Test
	public void testAlgorithmInstanceCommonThroughCommonContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Algorithm> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getAlgorithm());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getAlgorithm());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getAlgorithm());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getAlgorithm(algorithmID, problemID, 0));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(checks.get(DEFINITION), checks.get(PRE_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(POST_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(PRODUCE));
	}

	@Test
	public void testAlgorithmIDCommonThroughCommonContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, AlgorithmID<?>> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getAlgorithmID());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getAlgorithmID());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getAlgorithmID());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getRunContext(algorithmID, problemID, 0).getAlgorithmID());
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(algorithmID, checks.get(DEFINITION));
		assertEquals(algorithmID, checks.get(PRE_RUN));
		assertEquals(algorithmID, checks.get(POST_RUN));
		assertEquals(algorithmID, checks.get(PRODUCE));
	}

	@Test
	public void testProblemInstanceRetrievedThroughContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Problem> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getProblem());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getProblem());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getProblem());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getProblem(algorithmID, problemID, 0));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertNotNull(checks.get(DEFINITION));
		assertNotNull(checks.get(PRE_RUN));
		assertNotNull(checks.get(POST_RUN));
		assertNotNull(checks.get(PRODUCE));
	}

	@Test
	public void testProblemInstanceCommonThroughCommonContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Problem> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getProblem());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getProblem());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getProblem());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getProblem(algorithmID, problemID, 0));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(checks.get(DEFINITION), checks.get(PRE_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(POST_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(PRODUCE));
	}

	@Test
	public void testProblemIDCommonThroughCommonContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, ProblemID<?>> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getProblemID());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getProblemID());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getProblemID());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getRunContext(algorithmID, problemID, 0).getProblemID());
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(problemID, checks.get(DEFINITION));
		assertEquals(problemID, checks.get(PRE_RUN));
		assertEquals(problemID, checks.get(POST_RUN));
		assertEquals(problemID, checks.get(PRODUCE));
	}

	@Test
	public void testRunIndexRetrievedThroughContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Integer> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getRun());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getRun());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getRun());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getRunContext(algorithmID, problemID, 0).getRun());
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertNotNull(checks.get(DEFINITION));
		assertNotNull(checks.get(PRE_RUN));
		assertNotNull(checks.get(POST_RUN));
		assertNotNull(checks.get(PRODUCE));
	}

	@Test
	public void testRunIndexCommonThroughCommonContexts() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		ProblemID<Problem> problemID = builder.prepareProblem(() -> new Problem());
		AlgorithmID<Algorithm> algorithmID = builder.prepareAlgorithm((problem) -> new Algorithm());

		class Location {
		}
		final Location DEFINITION = new Location();
		Map<Location, Integer> checks = new HashMap<>();
		DataID<String> id = builder.defineRunData(Generation.ONCE_AND_BACKUP, (context) -> {
			checks.put(DEFINITION, context.getRun());
			return "OK";
		});
		final Location PRE_RUN = new Location();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			context.getData(id);
			checks.put(PRE_RUN, context.getRun());
		});
		final Location POST_RUN = new Location();
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			checks.put(POST_RUN, context.getRun());
		});
		final Location PRODUCE = new Location();
		builder.produceWith((context) -> {
			checks.put(PRODUCE, context.getRunContext(algorithmID, problemID, 0).getRun());
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(checks.get(DEFINITION), checks.get(PRE_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(POST_RUN));
		assertEquals(checks.get(DEFINITION), checks.get(PRODUCE));
	}

	@Test
	public void testRejectsNullDataDefinitionInstantiator() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.defineRunData(Generation.ONCE_AND_BACKUP, null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	@Test
	public void testRejectsNullDataGeneration() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.defineRunData(null, (context) -> "OK");
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	/* RUN EVENTS */

	@SuppressWarnings("serial")
	@Test
	public void testRunEventsRunAtRightInstants() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());

		boolean[] hasRun = { false, false, false };
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			assertFalse(hasRun[0]);
			assertFalse(hasRun[1]);
			assertFalse(hasRun[2]);
			hasRun[0] = true;
		});
		builder.prepareAlgorithm((problem) -> new Algorithm() {
			@Override
			public void run() {
				assertTrue(hasRun[0]);
				assertFalse(hasRun[1]);
				assertFalse(hasRun[2]);
				hasRun[1] = true;
			}
		});
		builder.createRunEvent(When.AFTER_RUN, (context) -> {
			assertTrue(hasRun[0]);
			assertTrue(hasRun[1]);
			assertFalse(hasRun[2]);
			hasRun[2] = true;
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertTrue(hasRun[0]);
		assertTrue(hasRun[1]);
		assertTrue(hasRun[2]);
	}

	@Test
	public void testRejectsNullRunEvent() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.createRunEvent(When.BEFORE_RUN, null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	@Test
	public void testRejectsNullRunInstant() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.createRunEvent(null, (context) -> {
			});
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	/* PERFORM PHASE */

	@Test
	public void testPrepareTheRightRuns() {
		class P1 extends Problem {
		}
		class P2 extends Problem {
		}
		class P3 extends Problem {
		}
		@SuppressWarnings("serial")
		class A1 extends Algorithm {
		}
		@SuppressWarnings("serial")
		class A2 extends Algorithm {
		}
		@SuppressWarnings("serial")
		class A3 extends Algorithm {
		}

		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		ProblemID<P1> p1 = builder.prepareProblem(() -> new P1());
		ProblemID<P2> p2 = builder.prepareProblem(() -> new P2());
		ProblemID<P3> p3 = builder.prepareProblem(() -> new P3());

		AlgorithmID<A1> a1 = builder.prepareAlgorithm((problem) -> new A1());
		AlgorithmID<A2> a2 = builder.prepareAlgorithm((problem) -> new A2());
		AlgorithmID<A3> a3 = builder.prepareAlgorithm((problem) -> new A3());

		builder.prepareIndependentRuns(2);

		Collection<List<Object>> runs = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			runs.add(Arrays.asList(context.getAlgorithmID(), context.getProblemID(), context.getRun()));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(18, runs.size());
		assertTrue(runs.contains(Arrays.asList(a1, p1, 0)));
		assertTrue(runs.contains(Arrays.asList(a1, p1, 1)));
		assertTrue(runs.contains(Arrays.asList(a1, p2, 0)));
		assertTrue(runs.contains(Arrays.asList(a1, p2, 1)));
		assertTrue(runs.contains(Arrays.asList(a1, p3, 0)));
		assertTrue(runs.contains(Arrays.asList(a1, p3, 1)));
		assertTrue(runs.contains(Arrays.asList(a2, p1, 0)));
		assertTrue(runs.contains(Arrays.asList(a2, p1, 1)));
		assertTrue(runs.contains(Arrays.asList(a2, p2, 0)));
		assertTrue(runs.contains(Arrays.asList(a2, p2, 1)));
		assertTrue(runs.contains(Arrays.asList(a2, p3, 0)));
		assertTrue(runs.contains(Arrays.asList(a2, p3, 1)));
		assertTrue(runs.contains(Arrays.asList(a3, p1, 0)));
		assertTrue(runs.contains(Arrays.asList(a3, p1, 1)));
		assertTrue(runs.contains(Arrays.asList(a3, p2, 0)));
		assertTrue(runs.contains(Arrays.asList(a3, p2, 1)));
		assertTrue(runs.contains(Arrays.asList(a3, p3, 0)));
		assertTrue(runs.contains(Arrays.asList(a3, p3, 1)));
	}

	@Test
	public void testPrepareTheRightRunsEvenWithEquivalentAlgorithmsOrProblems() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		ProblemID<Problem> p1 = builder.prepareProblem(() -> new Problem());
		ProblemID<Problem> p2 = builder.prepareProblem(() -> new Problem());

		AlgorithmID<Algorithm> a1 = builder.prepareAlgorithm((problem) -> new Algorithm());
		AlgorithmID<Algorithm> a2 = builder.prepareAlgorithm((problem) -> new Algorithm());

		builder.prepareIndependentRuns(2);

		Collection<List<Object>> runs = new LinkedList<>();
		builder.createRunEvent(When.BEFORE_RUN, (context) -> {
			runs.add(Arrays.asList(context.getAlgorithmID(), context.getProblemID(), context.getRun()));
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(8, runs.size());
		assertTrue(runs.contains(Arrays.asList(a1, p1, 0)));
		assertTrue(runs.contains(Arrays.asList(a1, p1, 1)));
		assertTrue(runs.contains(Arrays.asList(a1, p2, 0)));
		assertTrue(runs.contains(Arrays.asList(a1, p2, 1)));
		assertTrue(runs.contains(Arrays.asList(a2, p1, 0)));
		assertTrue(runs.contains(Arrays.asList(a2, p1, 1)));
		assertTrue(runs.contains(Arrays.asList(a2, p2, 0)));
		assertTrue(runs.contains(Arrays.asList(a2, p2, 1)));
	}

	@Test
	public void testRejectsNullPerformerInstantiator() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.performWith(null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

	/* PRODUCE PHASE */

	@Test
	public void testProducerCalledWhenPerformerTerminated() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();

		builder.prepareProblem(() -> new Problem());
		builder.prepareProblem(() -> new Problem());

		builder.prepareAlgorithm((problem) -> new Algorithm());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		boolean[] hasRun = { false, false };
		builder.performWith(() -> new SequentialPerformer() {

			@Override
			public void runUntilAllRequestsAreTerminated() {
				super.runUntilAllRequestsAreTerminated();
				assertFalse(hasRun[0]);
				assertFalse(hasRun[1]);
				hasRun[0] = true;
			}
		});

		builder.produceWith((context) -> {
			assertTrue(hasRun[0]);
			assertFalse(hasRun[1]);
			hasRun[1] = true;
		});

		Experiment experiment = builder.build();
		experiment.run();

		assertTrue(hasRun[0]);
		assertTrue(hasRun[1]);
	}

	@Test
	public void testAllProducersAreRun() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		builder.prepareProblem(() -> new Problem());
		builder.prepareAlgorithm((problem) -> new Algorithm());

		int[] hasRun = { 0 };
		int occurrences = 10;
		for (int i = 0; i < occurrences; i++) {
			builder.produceWith((context) -> {
				synchronized (hasRun) {
					hasRun[0]++;
				}
			});
		}

		Experiment experiment = builder.build();
		experiment.run();

		assertEquals(occurrences, hasRun[0]);
	}

	@Test
	public void testRejectsNullProducerInstantiator() {
		PreparePerformProduceExperimentBuilder<Algorithm, Problem> builder = new PreparePerformProduceExperimentBuilder<>();
		try {
			builder.produceWith(null);
			fail("No exception thrown");
		} catch (NullPointerException cause) {
			// OK
		}
	}

}
