package org.uma.jmetal.experiment.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.uma.jmetal.experiment.ExperimentConsumer;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.AlgorithmExecutionListener;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Context;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Type;

/**
 * {@link ContextBasedConsumer} is supposed to be used in combination with
 * {@link ContextBasedFeeder}, which requires to define {@link Type}s and
 * {@link Context}s. Because algorithms are then identified through the couple
 * ({@link Type}, {@link Context}), the simple {@link #watch(AlgorithmExecution)}
 * misses relevant information to properly identify the class of the algorithm
 * being run. Instead, one can use a {@link ContextBasedConsumer} which allows
 * to assign specific operations to algorithms of a given {@link Type} or
 * {@link Context}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 * @param <Algorithm>
 */
public class ContextBasedConsumer<Algorithm> implements ExperimentConsumer<Algorithm> {

	private final Collection<WatchDescriptor<?>> descriptors = new LinkedList<>();

	@Override
	public void watch(AlgorithmExecution<Algorithm> execution) {
		watchCloser(execution);
	}

	@SuppressWarnings("unchecked")
	private <A extends Algorithm> void watchCloser(AlgorithmExecution<Algorithm> execution) {
		for (WatchDescriptor<?> descriptor : descriptors) {
			WatchDescriptor<A> d = (WatchDescriptor<A>) descriptor;
			Algorithm algorithm = execution.getAlgorithm();
			if (d.algorithmSelector.test(algorithm)) {
				d.consumer.accept(new AlgorithmExecution<A>() {

					@Override
					public A getAlgorithm() {
						return (A) execution.getAlgorithm();
					}

					@Override
					public void run() throws AlreadyRunException {
						execution.run();
					}

					@Override
					public RunStatus getRunStatus() {
						return execution.getRunStatus();
					}

					@Override
					public void addRunListener(AlgorithmExecutionListener listener) {
						execution.addRunListener(listener);
					}

					@Override
					public void removeRunListener(AlgorithmExecutionListener listener) {
						execution.removeRunListener(listener);
					}
				});
			} else {
				// Algorithm and watcher incompatible
			}
		}
	}

	private class WatchDescriptor<A extends Algorithm> {
		private final Predicate<Algorithm> algorithmSelector;
		private final Consumer<AlgorithmExecution<A>> consumer;

		public WatchDescriptor(Predicate<Algorithm> algorithmSelector, Consumer<AlgorithmExecution<A>> consumer) {
			this.algorithmSelector = algorithmSelector;
			this.consumer = consumer;
		}
	}

	/**
	 * Use this method to watch algorithms of a particular {@link Type}.
	 * 
	 * @param type
	 *            the {@link Type} of the algorithms
	 */
	public <A extends Algorithm> StatusSelector<A> whenRunFor(Type<A> type) {
		return new StatusSelector<A>((algorithm) -> type.isTypeOf(algorithm));
	}

	/**
	 * Use this method to watch algorithms configured with a particular
	 * {@link Context}.
	 * 
	 * @param context
	 *            the {@link Context} of the algorithms
	 */
	public StatusSelector<Algorithm> whenRunFor(Context<?> context) {
		return new StatusSelector<Algorithm>((algorithm) -> context.isContextOf(algorithm));
	}

	/**
	 * Use this method to watch a specific algorithm instance, identified
	 * through its {@link Type} and {@link Context}.
	 * 
	 * @param type
	 *            the {@link Type} of the algorithm
	 * @param context
	 *            the {@link Context} of the algorithm
	 */
	public <A extends Algorithm> StatusSelector<A> whenRunFor(Type<A> type, Context<?> context) {
		return new StatusSelector<A>((algorithm) -> type.isTypeOf(algorithm) && context.isContextOf(algorithm));
	}

	/**
	 * Use this method to watch all the algorithms.
	 */
	public StatusSelector<Algorithm> whenRunForAnyAlgorithm() {
		return new StatusSelector<Algorithm>((algorithm) -> true);
	}

	public class StatusSelector<A extends Algorithm> {

		private final Predicate<Algorithm> selector;

		public StatusSelector(Predicate<Algorithm> selector) {
			this.selector = selector;
		}

		/**
		 * Apply the following operation as soon as the {@link AlgorithmExecution} is
		 * provided.
		 */
		public ExecutionSelector<A> isProvided() {
			return new ExecutionSelector<A>(selector, null);
		}

		/**
		 * Apply the following operation as soon as the {@link AlgorithmExecution}
		 * reaches a particular {@link RunStatus}.
		 */
		public ExecutionSelector<A> reaches(RunStatus status) {
			return new ExecutionSelector<A>(selector, status);
		}

	}

	public class ExecutionSelector<A extends Algorithm> {

		private final Predicate<Algorithm> selector;
		private final RunStatus status;

		public ExecutionSelector(Predicate<Algorithm> selector, RunStatus status) {
			this.selector = selector;
			this.status = status;
		}

		/**
		 * Provide the operation to apply on the {@link AlgorithmExecution}.
		 */
		public void execute(Consumer<AlgorithmExecution<A>> consumer) {
			if (status == null) {
				descriptors.add(new WatchDescriptor<A>(selector, consumer));
			} else {
				descriptors.add(new WatchDescriptor<A>(selector, new Consumer<AlgorithmExecution<A>>() {

					@Override
					public void accept(AlgorithmExecution<A> execution) {
						execution.addRunListener(new AlgorithmExecutionListener() {

							@Override
							public void hasChangedStatus(RunStatus newStatus) {
								if (newStatus == status) {
									consumer.accept(execution);
								} else {
									// Ignore
								}
							}
						});
					}
				}));
			}
		}

	}
}
