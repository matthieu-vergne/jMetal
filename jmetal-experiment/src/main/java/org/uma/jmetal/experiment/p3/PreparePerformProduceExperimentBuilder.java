package org.uma.jmetal.experiment.p3;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;

import org.uma.jmetal.experiment.Experiment;

/**
 * <p>
 * This builder produces an {@link Experiment} which runs in 3 steps:
 * </p>
 * <ol>
 * <li><b>Prepare:</b> instantiate and configure the {@link Problem} and
 * {@link Algorithm} instances.</li>
 * <li><b>Perform:</b> execute each {@link Algorithm} on each relevant
 * {@link Problem}.</li>
 * <li><b>Produce:</b> format the results into relevant outputs.</li>
 * </ol>
 * <p>
 * The goal of such a sequence is to preserve the execution of the algorithms
 * from side effects. Indeed, peripheral tasks can be heavy, like generating
 * configuration data or loading them from an external source, or keeping up to
 * date a user interface with the current state of each {@link Algorithm}. Such
 * tasks add to the computation time, and thus can influence the final
 * evaluation of the performance of an {@link Algorithm}. Consequently,
 * <i>perform</i> focuses on running each {@link Algorithm} as reliably as
 * possible by delegating any extra task to the steps before (<i>prepare</i>)
 * and after (<i>produce</i>).
 * </p>
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <Algorithm>
 *            The kind of algorithm to execute during the <em>perform</em>
 *            phase.
 * @param <Problem>
 *            The kind of problem to solve with each algorithm.
 */
@SuppressWarnings("hiding")
public class PreparePerformProduceExperimentBuilder<Algorithm extends org.uma.jmetal.algorithm.Algorithm<?>, Problem> {
	private final Map<ProblemID<? extends Problem>, ProblemInstantiator<? extends Problem>> problemInstantiators = new HashMap<>();
	private final Map<AlgorithmID<? extends Algorithm>, AlgorithmInstantiator<? extends Algorithm, Problem>> algorithmInstantiators = new HashMap<>();
	private int independentRuns = 1;
	private Supplier<Performer> performerInstantiator = () -> new SequentialPerformer();
	private final Map<DataID<?>, RunDataDefinition<Algorithm, Problem, ?>> dataDefinitions = new HashMap<>();
	private final Map<DataID<?>, Generation> dataGenerationStrategies = new HashMap<>();
	private final Collection<RunEvent<Algorithm, Problem>> preRunEvents = new LinkedList<>();
	private final Collection<RunEvent<Algorithm, Problem>> postRunEvents = new LinkedList<>();
	private final Collection<Producer> producers = new LinkedList<>();

	/**
	 * <p>
	 * This method adds a problem to be solved to the experiment. This problem will
	 * be instantiated as much as required through the provided
	 * {@link ProblemInstantiator}. Anything related to an instance of this problem
	 * can be retrieved later by using the {@link ProblemID} returned by this
	 * method.
	 * </p>
	 * <p>
	 * It is possible to prepare several problems in exactly the same way.
	 * Nevertheless, they will each have their own {@link ProblemID}. This allows to
	 * prepare similar problems which differ only through their parameters, or even
	 * to experiment on strictly equivalent problems as if they were different ones,
	 * for example to evaluate some variance intrinsic to the experiment.
	 * </p>
	 * 
	 * @param instantiator
	 *            the way to instantiate such a problem
	 * @return a {@link ProblemID} to retrieve information related to an instance of
	 *         this problem
	 */
	public <P extends Problem> ProblemID<P> prepareProblem(ProblemInstantiator<P> instantiator) {
		Objects.requireNonNull(instantiator, "No instantiator provided");
		ProblemID<P> id = new ProblemID<P>();
		problemInstantiators.put(id, instantiator);
		return id;
	}

	/**
	 * A {@link ProblemInstantiator} produces a problem to solve. Each call to
	 * {@link ProblemInstantiator#create()} is assumed to return a different
	 * instance, not several times the same. Such independence allows to avoid side
	 * effects and thread safety. If such independence is not satisfied, the
	 * experimenter should ensure that it does not impact the experiment in some
	 * negative ways.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <Problem>
	 *            The kind of problem to instantiate.
	 */
	@FunctionalInterface
	public static interface ProblemInstantiator<Problem> {
		/**
		 * 
		 * @return a new problem instance
		 */
		public Problem create();
	}

	/**
	 * <p>
	 * A {@link ProblemID} represents a particular kind of problems. It is returned
	 * by
	 * {@link PreparePerformProduceExperimentBuilder#prepareProblem(ProblemInstantiator)}
	 * to represent the problem prepared, and used by various elements in order to
	 * retrieve properties related to this problem.
	 * </p>
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <P>
	 *            The kind of problem it represents.
	 */
	public static class ProblemID<P> {
		private ProblemID() {
			// Forbid to instantiate it without the builder
		}
	}

	/**
	 * <p>
	 * This method adds an algorithm to evaluate to the experiment. This algorithm
	 * will be instantiated as much as required through the provided
	 * {@link AlgorithmInstantiator}. Anything related to an instance of this
	 * algorithm can be retrieved later by using the {@link AlgorithmID} returned by
	 * this method.
	 * </p>
	 * <p>
	 * It is possible to prepare several algorithms in exactly the same way.
	 * Nevertheless, they will each have their own {@link AlgorithmID}. This allows
	 * to prepare similar algorithms which differ only through their parameters, or
	 * even to experiment on strictly equivalent algorithms as if they were
	 * different ones, for example to evaluate some variance intrinsic to the
	 * experiment.
	 * </p>
	 * <p>
	 * Although nothing forbid to consider <code>null</code> as a valid problem
	 * depending on the algorithm, an algorithm cannot be <code>null</code> because
	 * it must, at least, be executed. Consequently, if an
	 * {@link AlgorithmInstantiator} returns <code>null</code>, a
	 * {@link NullAlgorithmException} will be thrown when the {@link Experiment}
	 * will run the <em>prepare</em> phase.
	 * </p>
	 * 
	 * @param instantiator
	 *            the way to instantiate such an algorithm
	 * @return an {@link AlgorithmID} to retrieve information related to an instance
	 *         of this algorithm
	 */
	public <A extends Algorithm> AlgorithmID<A> prepareAlgorithm(AlgorithmInstantiator<A, Problem> instantiator) {
		Objects.requireNonNull(instantiator, "No instantiator provided");
		NullAlgorithmException exception = new NullAlgorithmException();
		AlgorithmID<A> id = new AlgorithmID<A>();
		algorithmInstantiators.put(id, new AlgorithmInstantiator<A, Problem>() {

			@Override
			public A createFor(Problem problem) {
				A algorithm = instantiator.createFor(problem);
				if (algorithm == null) {
					throw exception;
				} else {
					return algorithm;
				}
			}
		});
		return id;
	}

	@SuppressWarnings("serial")
	public static class NullAlgorithmException extends NullPointerException {
		public NullAlgorithmException() {
			super("The instantiator provided for generating an algorithm returns null");
		}
	}

	/**
	 * <p>
	 * An {@link AlgorithmInstantiator} produces an algorithm to run. Each call to
	 * {@link AlgorithmInstantiator#createFor(Object)} is assumed to return a
	 * different instance, not several times the same. Such independence allows to
	 * avoid side effects and thread safety. If such independence is not satisfied,
	 * the experimenter should ensure that it does not impact the experiment in some
	 * negative ways.
	 * </p>
	 * <p>
	 * Currently, an algorithm is assumed to be instantiated based on a problem to
	 * solve, which is why the problem instance is provided in argument. If the
	 * problem is not used by the algorithm at this level, then the experimenter
	 * must find a way to provide the problem instance through some other means.
	 * This setting may evolve in the future depending on how the
	 * <a href="https://github.com/jMetal/jMetal/issues/55">discussion about the
	 * algorithm-problem relationship</a> evolves.
	 * </p>
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <Algorithm>
	 *            The kind of algorithm to instantiate.
	 * @param <Problem>
	 *            The kind of problem to solve.
	 */
	@FunctionalInterface
	public static interface AlgorithmInstantiator<Algorithm, Problem> {
		public Algorithm createFor(Problem problem);
	}

	/**
	 * <p>
	 * An {@link AlgorithmID} represents a particular kind of algorithms. It is
	 * returned by
	 * {@link PreparePerformProduceExperimentBuilder#prepareAlgorithm(AlgorithmInstantiator)}
	 * to represent the algorithm prepared, and used by various elements in order to
	 * retrieve properties related to this algorithm.
	 * </p>
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <A>
	 *            The kind of algorithm it represents.
	 */
	public static class AlgorithmID<A> {
		private AlgorithmID() {
			// Forbid to instantiate it without the builder
		}
	}

	/**
	 * This method allows to specify how many times each algorithm should be run on
	 * each problem. Each run is assumed to be independent, which allows to execute
	 * them in any order. By default, it is configured for 1 run, which is the
	 * minimal value.
	 * 
	 * @param runs
	 *            the number of times each algorithm should solve each problem
	 */
	public void prepareIndependentRuns(int runs) {
		if (runs < 1) {
			throw new IllegalArgumentException("It should perform at least 1 run, not " + runs);
		} else {
			this.independentRuns = runs;
		}
	}

	/**
	 * Define a specific piece of data to be generated for each run. Each piece of
	 * data generated can be retrieved in various places through the {@link DataID}
	 * returned by this method.
	 * 
	 * @param generation
	 *            the generation strategy to use
	 * @param definition
	 *            the definition of the piece of data
	 * @return a {@link DataID} representing this piece of data
	 */
	public <D> DataID<D> defineRunData(Generation generation, RunDataDefinition<Algorithm, Problem, D> definition) {
		Objects.requireNonNull(generation, "No generation strategy provided");
		Objects.requireNonNull(definition, "No definition provided");
		DataID<D> id = new DataID<>();
		this.dataDefinitions.put(id, definition);
		this.dataGenerationStrategies.put(id, generation);
		return id;
	}

	/**
	 * {@link Generation} provides strategies to use to generate pieces of data
	 * defined through
	 * {@link PreparePerformProduceExperimentBuilder#defineRunData(Generation, RunDataDefinition)}.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	public static enum Generation {
		/**
		 * Generate the piece of data once and backup it for future recall. This
		 * strategy is particularly suited to static data, which should remain the same
		 * independently of when we generate it.
		 */
		ONCE_AND_BACKUP,
		/**
		 * Generate the piece of data every time we request it. Do not store it for
		 * reuse. This strategy is particularly suited to dynamic data.
		 */
		EVERY_CALL
	}

	/**
	 * A {@link RunDataDefinition} defines how to generate a given piece of data
	 * during a single run. The provided {@link RunContext} allows to use properties
	 * related to the current run, including other pieces of data.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <A>
	 *            The kind of algorithm executed during the run
	 * @param <P>
	 *            The kind of problem solved during the run
	 * @param <D>
	 *            The kind of data produced by this definition
	 */
	@FunctionalInterface
	public static interface RunDataDefinition<A, P, D> {
		public D createFrom(RunContext<? extends A, ? extends P> context);
	}

	/**
	 * <p>
	 * A {@link DataID} represents a particular kind of data. It is returned by
	 * {@link PreparePerformProduceExperimentBuilder#defineRunData(Generation, RunDataDefinition)}
	 * to represent the data generated, and used by various elements in order to
	 * retrieve this data in various places.
	 * </p>
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <D>
	 *            The kind of data it represents.
	 */
	public static class DataID<D> {
		private DataID() {
			// Forbid to instantiate it without the builder
		}
	}

	/**
	 * This method adds a specific piece of code to execute at each run, in addition
	 * to the execution of the algorithm. The piece of code can be executed before
	 * or after the algorithm depending on the parameters provided. Because this
	 * piece of code is part of the <em>perform</em> phase, it should be as light as
	 * possible and focus on run-specific computation. Such an event is particularly
	 * well suited for logging the run.
	 * 
	 * @param when
	 *            the instant at which the event should occur
	 * @param event
	 *            the event to execute
	 */
	public void createRunEvent(When when, RunEvent<Algorithm, Problem> event) {
		Objects.requireNonNull(when, "No instant provided");
		Objects.requireNonNull(event, "No event provided");
		if (when == When.BEFORE_RUN) {
			this.preRunEvents.add(event);
		} else if (when == When.AFTER_RUN) {
			this.postRunEvents.add(event);
		} else {
			throw new RuntimeException("Not managed yet: " + when);
		}
	}

	/**
	 * {@link When} specify the instant at which a {@link RunEvent} should be
	 * executed.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	public static enum When {
		/**
		 * The {@link RunEvent} should be executed <strong>before</strong> to execute
		 * the algorithm.
		 */
		BEFORE_RUN,
		/**
		 * The {@link RunEvent} should be executed <strong>after</strong> the algorithm
		 * has been executed.
		 */
		AFTER_RUN
	}

	/**
	 * A {@link RunEvent} consists in a piece of code to be executed for each run.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <A>
	 *            The kind of algorithm executed during the run
	 * @param <P>
	 *            The kind of problem solved during the run
	 */
	@FunctionalInterface
	public static interface RunEvent<A, P> {
		public void runFor(RunContext<? extends A, ? extends P> context);
	}

	/**
	 * <p>
	 * This method allows to customize the execution strategy of the
	 * <em>perform</em> phase. Various atomic, executable elements are produced
	 * during the <em>prepare</em> phase, which are configured to run the
	 * algorithms, solve problems, and generate raw data for the <em>produce</em>
	 * phase. The remaining work is to run these atomic elements through a
	 * {@link Performer}, which can be customized through this method. By default, a
	 * {@link SequentialPerformer} is used.
	 * </p>
	 * <p>
	 * It is worth to note that this method does not take directly a
	 * {@link Performer} instance, but a {@link Supplier} of such instances. This is
	 * what allows {@link #build()} to return independent {@link Experiment}
	 * instances, each with their own {@link Performer}. Indeed, a
	 * {@link Performer#runUntilAllRequestsAreTerminated()} is not supposed to be
	 * called twice, which is what would happen if it is reused. If one needs to use
	 * a common {@link Performer} for several experiment, the {@link Supplier}
	 * should always return the same instance instead of a new one.
	 * </p>
	 * <p>
	 * If the {@link Supplier} provided returns <code>null</code>, an
	 * {@link NullPointerException} will be thrown when instantiating the
	 * {@link Experiment} with {@link #build()}.
	 * </p>
	 * 
	 * @param performerInstantiator
	 *            the {@link Performer} to use to execute the algorithms
	 */
	public void performWith(Supplier<Performer> performerInstantiator) {
		Objects.requireNonNull(performerInstantiator, "No performer provided");
		NullPerformerException exception = new NullPerformerException();
		this.performerInstantiator = () -> {
			Performer performer = performerInstantiator.get();
			if (performer == null) {
				throw exception;
			} else {
				return performer;
			}
		};
	}

	/**
	 * A {@link Performer} aims at running blocks of code already configured to run
	 * algorithms on specific problems and generate raw data. Thus, the
	 * {@link Performer} should only care about executing these blocks of code,
	 * without corrupting them.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	public static interface Performer {
		/**
		 * This method is called when a piece of code has been fully prepared and is
		 * ready to be executed. Each piece is assumed to be independent, so the
		 * {@link Performer} is free to choose when this piece of code should be run.
		 * 
		 * @param runnable
		 *            a piece of code to run
		 */
		public void request(Runnable runnable);

		/**
		 * This method is called when all the pieces have been provided to the
		 * {@link Performer}, which should then terminate. This method must return only
		 * when all the pieces provided through {@link #request(Runnable)} have been
		 * executed and have terminated.
		 */
		public void runUntilAllRequestsAreTerminated();
	}

	/**
	 * This method enrich the <em>produce</em> phase. The provided {@link Producer}
	 * must tell how the raw data generated during the <em>perform</em> phase is
	 * formatted into exploitable results. several {@link Producer} instances can be
	 * provided, each being considered independent and executed in an undefined
	 * order.
	 * 
	 * @param producer
	 *            the {@link Producer} to use
	 */
	public void produceWith(Producer producer) {
		Objects.requireNonNull(producer, "No producer provided");
		this.producers.add(producer);
	}

	/**
	 * A {@link Producer} aims at generating exploitable experiment results from raw
	 * data.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	@FunctionalInterface
	public static interface Producer {
		/**
		 * 
		 * @param context
		 *            the whole context from which raw data can be retrieved, among
		 *            other things
		 */
		public void produceFrom(GlobalContext context);
	}

	/**
	 * A {@link GlobalContext} provides access to all the raw data produced during
	 * the experiment. It is progressively filled during the <em>perform</em> phase
	 * and used mainly during the <em>produce</em> phase to generate exploitable
	 * results. It can also be used during the <em>perform</em> phase itself,
	 * although it requires some attention to not try to use data which has not been
	 * generated yet.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 */
	public static interface GlobalContext {
		/**
		 * @return the algorithm instance of a specific run, identified through the
		 *         provided parameters.
		 */
		public <A, P> A getAlgorithm(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run);

		/**
		 * @return the problem instance of a specific run, identified through the
		 *         provided parameters.
		 */
		public <A, P> P getProblem(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run);

		/**
		 * @return a specific piece of data of a specific run, identified through the
		 *         provided parameters.
		 */
		public <A, P, D> D getData(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run, DataID<D> dataID);

		/**
		 * If one needs to retrieve several elements of a specific run, he can use this
		 * method to specify the run only once. The returned object gives a simplified
		 * API to retrieve the various elements of this run.
		 * 
		 * @return The context referring to a specific run
		 */
		default <A, P> RunContext<A, P> getRunContext(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run) {
			return new RunContext<A, P>() {

				@Override
				public AlgorithmID<A> getAlgorithmID() {
					return algorithmID;
				}

				@Override
				public ProblemID<P> getProblemID() {
					return problemID;
				}

				@Override
				public int getRun() {
					return run;
				}

				@Override
				public A getAlgorithm() {
					return GlobalContext.this.getAlgorithm(algorithmID, problemID, run);
				}

				@Override
				public P getProblem() {
					return GlobalContext.this.getProblem(algorithmID, problemID, run);
				}

				@Override
				public <D> D getData(DataID<D> id) {
					return GlobalContext.this.getData(algorithmID, problemID, run, id);
				}

				@Override
				public GlobalContext getGlobalContext() {
					return GlobalContext.this;
				}
			};
		}
	}

	/**
	 * A {@link RunContext} is a simplified version of {@link GlobalContext}, which
	 * gives access to various elements of a run without needing to specify each
	 * time which run to target.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <A>
	 *            The kind of algorithm executed during the run
	 * @param <P>
	 *            The kind of problem solved during the run
	 */
	public static interface RunContext<A, P> {
		/**
		 * @return the algorithm ID of the run.
		 */
		public AlgorithmID<A> getAlgorithmID();

		/**
		 * @return the problem ID of the run.
		 */
		public ProblemID<P> getProblemID();

		/**
		 * @return the run index of the run.
		 */
		public int getRun();

		/**
		 * @return the algorithm instance of the run.
		 */
		public A getAlgorithm();

		/**
		 * @return the problem instance of the run.
		 */
		public P getProblem();

		/**
		 * @return a specific piece of data of the run.
		 */
		public <D> D getData(DataID<D> id);

		/**
		 * @return the global context containing this specific context.
		 */
		public GlobalContext getGlobalContext();
	}

	/**
	 * This method instantiates a 3-step {@link Experiment}
	 * (prepare-perform-produce) based on the various data provided so far. For any
	 * check that can be done before instantiation, an exception is thrown if
	 * something has been badly configured.
	 * 
	 * @return a complete {@link Experiment}, ready to be executed.
	 * @throws NoProblemException
	 *             if no problem has been prepared
	 * @throws NoAlgorithmException
	 *             if no algorithm has been prepared
	 * @throws NullPerformerException
	 *             if a <code>null</code> performer is generated
	 */
	public Experiment build() {
		if (problemInstantiators.isEmpty()) {
			throw new NoProblemException();
		} else if (algorithmInstantiators.isEmpty()) {
			throw new NoAlgorithmException();
		} else {
			final Map<ProblemID<? extends Problem>, ProblemInstantiator<? extends Problem>> problemInstantiators = new HashMap<>(
					this.problemInstantiators);
			final Map<AlgorithmID<? extends Algorithm>, AlgorithmInstantiator<? extends Algorithm, Problem>> algorithmInstantiators = new HashMap<>(
					this.algorithmInstantiators);
			final int independentRuns = this.independentRuns;
			final Performer performer = this.performerInstantiator.get();
			final Map<DataID<?>, RunDataDefinition<Algorithm, Problem, ?>> dataDefinitions = new HashMap<>(
					this.dataDefinitions);
			final Map<DataID<?>, Generation> dataGenerationStrategies = new HashMap<>(this.dataGenerationStrategies);
			final Collection<RunEvent<Algorithm, Problem>> preRunEvents = new LinkedList<>(this.preRunEvents);
			final Collection<RunEvent<Algorithm, Problem>> postRunEvents = new LinkedList<>(this.postRunEvents);
			final Collection<Producer> producers = new LinkedList<>(this.producers);

			return new PreparePerformProduceExperiment<Collection<RunDescriptor<? extends Algorithm, ? extends Problem>>, GlobalContext>() {

				@Override
				protected Collection<RunDescriptor<? extends Algorithm, ? extends Problem>> prepare() {
					Collection<RunDescriptor<? extends Algorithm, ? extends Problem>> runs = new LinkedList<>();
					for (Entry<ProblemID<? extends Problem>, ProblemInstantiator<? extends Problem>> entryProblem : problemInstantiators
							.entrySet()) {
						for (Entry<AlgorithmID<? extends Algorithm>, AlgorithmInstantiator<? extends Algorithm, Problem>> entryAlgorithm : algorithmInstantiators
								.entrySet()) {
							for (int run = 0; run < independentRuns; run++) {
								runs.add(createDescriptor(entryProblem.getKey(), entryProblem.getValue(),
										entryAlgorithm.getKey(), entryAlgorithm.getValue(), run));
							}
						}
					}
					return runs;
				}

				@SuppressWarnings("unchecked")
				private <A extends Algorithm, P extends Problem> RunDescriptor<A, P> createDescriptor(
						ProblemID<P> problemID, ProblemInstantiator<? extends Problem> problemInstantiator,
						AlgorithmID<A> algorithmID,
						AlgorithmInstantiator<? extends Algorithm, Problem> algorithmInstantiator, int run) {
					P problem = ((ProblemInstantiator<P>) problemInstantiator).create();
					A algorithm = ((AlgorithmInstantiator<A, Problem>) algorithmInstantiator).createFor(problem);
					RunDescriptor<A, P> descriptor = new RunDescriptor<A, P>() {

						@Override
						public AlgorithmID<A> getAlgorithmID() {
							return algorithmID;
						}

						@Override
						public A getAlgorithm() {
							return algorithm;
						}

						@Override
						public ProblemID<P> getProblemID() {
							return problemID;
						}

						@Override
						public P getProblem() {
							return problem;
						}

						@Override
						public int getRun() {
							return run;
						}
					};
					return descriptor;
				}

				/**
				 * Structure used to identify backup data. While {@link DataID} is a common ID
				 * to all the pieces of data generated through the same
				 * {@link RunDataDefinition}, each {@link BackupDataID} identifies a piece of
				 * data generated for a specific run. {@link BackupDataID} instances are equals
				 * if they refer to the same piece of data for the same run, which is required
				 * to retrieve backups from a map by generating new ID instances.
				 * 
				 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
				 *
				 * @param <Algorithm>
				 * @param <Problem>
				 * @param <Data>
				 */
				/*
				 * Practically, the generics are not necessary and could be removed to simplify
				 * the code. However, the compiler does not like it: in equals(), the instanceof
				 * operator rejects the non-generic class but accepts the generic one. Using a
				 * non-generic class seems to require a rather complex type for the instanceof.
				 * Rather than concentrating all the complexity on a single line, the current
				 * design choice is to distribute it by making it more verbose with generics,
				 * which is not fundamentally wrong.
				 */
				class BackupDataID<Algorithm, Problem, Data> {
					private final AlgorithmID<Algorithm> algorithmID;
					private final ProblemID<Problem> problemID;
					private final int run;
					private final DataID<Data> dataID;

					public BackupDataID(AlgorithmID<Algorithm> algorithmID, ProblemID<Problem> problemID, int run,
							DataID<Data> dataID) {
						this.algorithmID = algorithmID;
						this.problemID = problemID;
						this.run = run;
						this.dataID = dataID;
					}

					@Override
					public boolean equals(Object obj) {
						if (obj == this) {
							return true;
						} else if (obj instanceof BackupDataID) {
							@SuppressWarnings("unchecked")
							BackupDataID<?, ?, ?> id = (BackupDataID<?, ?, ?>) obj;
							return id.algorithmID.equals(algorithmID) && id.problemID.equals(problemID) && id.run == run
									&& id.dataID.equals(dataID);
						} else {
							return false;
						}
					}

					@Override
					public int hashCode() {
						return algorithmID.hashCode() + problemID.hashCode() + run + dataID.hashCode();
					}
				}

				@Override
				protected GlobalContext perform(
						Collection<RunDescriptor<? extends Algorithm, ? extends Problem>> descriptors) {
					GlobalContext globalContext = new GlobalContext() {
						Map<BackupDataID<?, ?, ?>, Object> runDataBackup = new HashMap<>();

						@SuppressWarnings("unchecked")
						private <A, P> RunDescriptor<A, P> searchDescriptor(
								Collection<RunDescriptor<? extends Algorithm, ? extends Problem>> descriptors,
								AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run) {
							for (RunDescriptor<?, ?> descriptor : descriptors) {
								if (descriptor.getAlgorithmID().equals(algorithmID)
										&& descriptor.getProblemID().equals(problemID) && descriptor.getRun() == run) {
									return (RunDescriptor<A, P>) descriptor;
								} else {
									continue;
								}
							}
							throw new IllegalArgumentException(
									"No descriptor found for (" + algorithmID + ", " + problemID + ", " + run + ")");
						}

						@Override
						public <A, P> A getAlgorithm(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run) {
							return searchDescriptor(descriptors, algorithmID, problemID, run).getAlgorithm();
						}

						@Override
						public <A, P> P getProblem(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run) {
							return searchDescriptor(descriptors, algorithmID, problemID, run).getProblem();
						}

						@Override
						public <A, P, D> D getData(AlgorithmID<A> algorithmID, ProblemID<P> problemID, int run,
								DataID<D> id) {
							BackupDataID<A, P, D> backupID = new BackupDataID<>(algorithmID, problemID, run, id);

							@SuppressWarnings("unchecked")
							D data = (D) runDataBackup.get(backupID);
							if (data != null) {
								// Reuse backup data
							} else {
								@SuppressWarnings("unchecked")
								RunDataDefinition<A, P, D> definition = (RunDataDefinition<A, P, D>) dataDefinitions
										.get(id);
								data = definition.createFrom(getRunContext(algorithmID, problemID, run));

								Generation generation = dataGenerationStrategies.get(id);
								switch (generation) {
								case EVERY_CALL:
									// Do not store as backup
									break;
								case ONCE_AND_BACKUP:
									runDataBackup.put(backupID, data);
									break;
								default:
									throw new RuntimeException("Unmanaged generation strategy:" + generation);
								}
							}
							return data;
						}
					};
					for (RunDescriptor<? extends Algorithm, ? extends Problem> descriptor : descriptors) {
						perform(globalContext, descriptor);
					}
					performer.runUntilAllRequestsAreTerminated();
					return globalContext;
				}

				private <A extends Algorithm, P extends Problem> void perform(GlobalContext globalContext,
						RunDescriptor<A, P> descriptor) {
					performer.request(() -> {
						RunContext<A, P> runContext = globalContext.getRunContext(descriptor.getAlgorithmID(),
								descriptor.getProblemID(), descriptor.getRun());
						for (RunEvent<Algorithm, Problem> consumer : preRunEvents) {
							consumer.runFor(runContext);
						}
						descriptor.getAlgorithm().run();
						for (RunEvent<Algorithm, Problem> consumer : postRunEvents) {
							consumer.runFor(runContext);
						}
					});
				}

				@Override
				protected void produce(GlobalContext globalContext) {
					for (Producer producer : producers) {
						producer.produceFrom(globalContext);
					}
				}
			};
		}
	}

	@SuppressWarnings("serial")
	public static class NoProblemException extends IllegalStateException {
		public NoProblemException() {
			super("No problem prepared");
		}
	}

	@SuppressWarnings("serial")
	public static class NoAlgorithmException extends IllegalStateException {
		public NoAlgorithmException() {
			super("No algorithm prepared");
		}
	}

	@SuppressWarnings("serial")
	public static class NullPerformerException extends NullPointerException {
		public NullPerformerException() {
			super("The supplier provided for generating a performer returns null");
		}
	}

	/**
	 * <p>
	 * {@link PreparePerformProduceExperiment} is a
	 * <a href="https://en.wikipedia.org/wiki/Pipeline_(software)">pipeline</a> used
	 * by a {@link PreparePerformProduceExperimentBuilder} to instantiate its
	 * 3-steps procedure "prepare-perform-produce". As a pipeline, the first step
	 * creates the elements to run in the second step, which creates the data to be
	 * exploited in the third step.
	 * </p>
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <Executables>
	 *            The elements prepared during the <em>prepare</em>phase and to
	 *            execute during the <em>perform</em> phase. Usually algorithms or
	 *            wrappers able to run them.
	 * @param <RawData>
	 *            The data generated during the <em>perform</em> phase and to be
	 *            refined and formatted during the <em>produce</em> phase. It can be
	 *            results stored internally (e.g. populations, exceptions) or
	 *            descriptors telling which external resources to use to retrieve
	 *            them (e.g. files, URLs).
	 */
	public static abstract class PreparePerformProduceExperiment<Executables, RawData> implements Experiment {

		/**
		 * This method must manage instantiation and configuration in order to let
		 * {@link #perform(Object)} focus on the run.
		 * 
		 * @return the elements to be run during the next phase
		 */
		protected abstract Executables prepare();

		/**
		 * This method must focus on running the elements provided, and let them produce
		 * the data to be returned at the end.
		 * 
		 * @param executables
		 *            the elements to execute in order to produce data
		 * @return the raw data produced by running the provided elements
		 */
		protected abstract RawData perform(Executables executables);

		/**
		 * This method must format the raw data produced by {@link #perform(Object)} in
		 * order to make exploitable results for the experiment.
		 * 
		 * @param rawData
		 *            the data to be formatted for the experiment
		 */
		protected abstract void produce(RawData rawData);

		/**
		 * Executes the pipeline "prepare-perform-produce".
		 */
		@Override
		public void run() {
			Executables executables = prepare();
			RawData rawData = perform(executables);
			produce(rawData);
		}
	}

	/**
	 * Executable structure used when instantiating a
	 * {@link PreparePerformProduceExperiment} through the
	 * {@link PreparePerformProduceExperimentBuilder}.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 *
	 * @param <A>
	 *            The kind of algorithm to execute.
	 * @param <P>
	 *            The kind of problem to solve.
	 */
	private interface RunDescriptor<A, P> {
		public AlgorithmID<A> getAlgorithmID();

		public A getAlgorithm();

		public ProblemID<P> getProblemID();

		public P getProblem();

		public int getRun();
	}
}
