package org.uma.jmetal.experiment.impl;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution;
import org.uma.jmetal.experiment.ExperimentExecutor.AlgorithmExecution.RunStatus;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Context;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Parameter;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Type;
import org.uma.jmetal.experiment.testUtil.Computer;
import org.uma.jmetal.experiment.testUtil.Product;
import org.uma.jmetal.experiment.testUtil.Sum;

public class ContextBasedConsumerTest {

	@Test
	public void testTypeSpecificWatchersAreProperlyApplied() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Type<Product> product = feeder.addType(() -> new Product());

		Parameter<Float> parameter1 = feeder.createParameter(Float.class);
		feeder.assign(parameter1).to(sum).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter1).to(product).with((a, v) -> a.product1 = v);
		Parameter<Float> parameter2 = feeder.createParameter(Float.class);
		feeder.assign(parameter2).to(sum).with((a, v) -> a.sum2 = v);
		feeder.assign(parameter2).to(product).with((a, v) -> a.product2 = v);

		Context<float[]> context1 = feeder.addContext(new float[] { 1, 2 });
		feeder.retrieve(parameter1).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context1).with((p) -> p[1]);

		Context<float[]> context2 = feeder.addContext(new float[] { 1.5f, 2.5f });
		feeder.retrieve(parameter1).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context2).with((p) -> p[1]);

		ContextBasedConsumer<Computer> consumer = new ContextBasedConsumer<>();
		List<String> registered = new LinkedList<String>();
		consumer.whenRunFor(sum).isProvided().execute((execution) -> {
			Sum algorithm = execution.getAlgorithm();
			registered.add(algorithm.sum1 + "+" + algorithm.sum2);
		});
		consumer.whenRunFor(product).isProvided().execute((execution) -> {
			Product algorithm = execution.getAlgorithm();
			registered.add(algorithm.product1 + "*" + algorithm.product2);
		});

		SequentialExperimentExecutor<Computer> executor = new SequentialExperimentExecutor<>((a) -> a.compute());
		for (Computer algorithm : feeder) {
			AlgorithmExecution<Computer> execution = executor.add(algorithm);
			consumer.watch(execution);
		}

		assertEquals("" + registered, 4, registered.size());
		assertEquals("1.0+2.0", registered.get(0));
		assertEquals("1.0*2.0", registered.get(1));
		assertEquals("1.5+2.5", registered.get(2));
		assertEquals("1.5*2.5", registered.get(3));
	}

	@Test
	public void testContextSpecificWatchersAreProperlyApplied() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Type<Product> product = feeder.addType(() -> new Product());

		Parameter<Float> parameter1 = feeder.createParameter(Float.class);
		feeder.assign(parameter1).to(sum).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter1).to(product).with((a, v) -> a.product1 = v);
		Parameter<Float> parameter2 = feeder.createParameter(Float.class);
		feeder.assign(parameter2).to(sum).with((a, v) -> a.sum2 = v);
		feeder.assign(parameter2).to(product).with((a, v) -> a.product2 = v);

		Context<float[]> context1 = feeder.addContext(new float[] { 1, 2 });
		feeder.retrieve(parameter1).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context1).with((p) -> p[1]);

		Context<float[]> context2 = feeder.addContext(new float[] { 1.5f, 2.5f });
		feeder.retrieve(parameter1).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context2).with((p) -> p[1]);

		ContextBasedConsumer<Computer> consumer = new ContextBasedConsumer<>();
		List<String> registered = new LinkedList<String>();
		consumer.whenRunFor(context1).isProvided().execute((execution) -> {
			Computer algorithm = execution.getAlgorithm();
			registered.add("(1,2)=" + algorithm.compute());
		});
		consumer.whenRunFor(context2).isProvided().execute((execution) -> {
			Computer algorithm = execution.getAlgorithm();
			registered.add("(1.5,2.5)=" + algorithm.compute());
		});

		SequentialExperimentExecutor<Computer> executor = new SequentialExperimentExecutor<>((a) -> a.compute());
		for (Computer algorithm : feeder) {
			AlgorithmExecution<Computer> execution = executor.add(algorithm);
			consumer.watch(execution);
		}

		assertEquals("" + registered, 4, registered.size());
		assertEquals("(1,2)=3.0", registered.get(0));
		assertEquals("(1,2)=2.0", registered.get(1));
		assertEquals("(1.5,2.5)=4.0", registered.get(2));
		assertEquals("(1.5,2.5)=3.75", registered.get(3));
	}

	@Test
	public void testInstanceSpecificWatchersAreProperlyApplied() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Type<Product> product = feeder.addType(() -> new Product());

		Parameter<Float> parameter1 = feeder.createParameter(Float.class);
		feeder.assign(parameter1).to(sum).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter1).to(product).with((a, v) -> a.product1 = v);
		Parameter<Float> parameter2 = feeder.createParameter(Float.class);
		feeder.assign(parameter2).to(sum).with((a, v) -> a.sum2 = v);
		feeder.assign(parameter2).to(product).with((a, v) -> a.product2 = v);

		Context<float[]> context1 = feeder.addContext(new float[] { 1, 2 });
		feeder.retrieve(parameter1).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context1).with((p) -> p[1]);

		Context<float[]> context2 = feeder.addContext(new float[] { 1.5f, 2.5f });
		feeder.retrieve(parameter1).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context2).with((p) -> p[1]);

		ContextBasedConsumer<Computer> consumer = new ContextBasedConsumer<>();
		List<String> registered = new LinkedList<String>();
		consumer.whenRunFor(sum, context1).isProvided().execute((execution) -> {
			Sum algorithm = execution.getAlgorithm();
			registered.add("1+2=" + algorithm.compute());
		});
		consumer.whenRunFor(product, context2).isProvided().execute((execution) -> {
			Product algorithm = execution.getAlgorithm();
			registered.add("1.5*2.5=" + algorithm.compute());
		});

		SequentialExperimentExecutor<Computer> executor = new SequentialExperimentExecutor<>((a) -> a.compute());
		for (Computer algorithm : feeder) {
			AlgorithmExecution<Computer> execution = executor.add(algorithm);
			consumer.watch(execution);
		}

		assertEquals("" + registered, 2, registered.size());
		assertEquals("1+2=3.0", registered.get(0));
		assertEquals("1.5*2.5=3.75", registered.get(1));
	}

	@Test
	public void testGlobalWatchersAreProperlyApplied() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Type<Product> product = feeder.addType(() -> new Product());

		Parameter<Float> parameter1 = feeder.createParameter(Float.class);
		feeder.assign(parameter1).to(sum).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter1).to(product).with((a, v) -> a.product1 = v);
		Parameter<Float> parameter2 = feeder.createParameter(Float.class);
		feeder.assign(parameter2).to(sum).with((a, v) -> a.sum2 = v);
		feeder.assign(parameter2).to(product).with((a, v) -> a.product2 = v);

		Context<float[]> context1 = feeder.addContext(new float[] { 1, 2 });
		feeder.retrieve(parameter1).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context1).with((p) -> p[1]);

		Context<float[]> context2 = feeder.addContext(new float[] { 1.5f, 2.5f });
		feeder.retrieve(parameter1).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter2).from(context2).with((p) -> p[1]);

		ContextBasedConsumer<Computer> consumer = new ContextBasedConsumer<>();
		List<String> registered = new LinkedList<String>();
		consumer.whenRunForAnyAlgorithm().isProvided().execute((execution) -> {
			Computer algorithm = execution.getAlgorithm();
			registered.add("r=" + algorithm.compute());
		});

		SequentialExperimentExecutor<Computer> executor = new SequentialExperimentExecutor<>((a) -> a.compute());
		for (Computer algorithm : feeder) {
			AlgorithmExecution<Computer> execution = executor.add(algorithm);
			consumer.watch(execution);
		}

		assertEquals("" + registered, 4, registered.size());
		assertEquals("r=3.0", registered.get(0));
		assertEquals("r=2.0", registered.get(1));
		assertEquals("r=4.0", registered.get(2));
		assertEquals("r=3.75", registered.get(3));
	}

	@Test
	public void testWatchersForRunningAreProperlyApplied() throws InterruptedException {
		ContextBasedFeeder<Runnable> feeder = new ContextBasedFeeder<>();

		boolean[] flags = { false, false };
		feeder.<Runnable>addType(() -> new Runnable() {

			@Override
			public void run() {
				long start = System.currentTimeMillis();
				while (flags[0] == false) {
					if (System.currentTimeMillis() - start > 10000) {
						throw new RuntimeException("Too long");
					} else {
						// continue running
						System.gc();
					}
				}
			}
		});

		feeder.addContext(() -> null);

		ContextBasedConsumer<Runnable> consumer = new ContextBasedConsumer<>();
		consumer.whenRunForAnyAlgorithm().reaches(RunStatus.RUNNING).execute((execution) -> {
			flags[1] = true;
		});

		SequentialExperimentExecutor<Runnable> executor = new SequentialExperimentExecutor<>((algo) -> algo.run());

		Iterator<Runnable> iterator = feeder.iterator();
		Runnable algorithm = iterator.next();
		AlgorithmExecution<Runnable> execution = executor.add(algorithm);
		consumer.watch(execution);

		Thread.sleep(100);

		assertFalse(flags[1]);

		executor.start();
		Thread.sleep(100);

		assertTrue(flags[1]);

		flags[0] = true;
	}

	@Test
	public void testWatchersForTerminatedAreProperlyApplied() throws InterruptedException {
		ContextBasedFeeder<Runnable> feeder = new ContextBasedFeeder<>();

		boolean[] flags = { false, false };
		feeder.<Runnable>addType(() -> new Runnable() {

			@Override
			public void run() {
				long start = System.currentTimeMillis();
				while (flags[0] == false) {
					if (System.currentTimeMillis() - start > 10000) {
						throw new RuntimeException("Too long");
					} else {
						// continue running
						System.gc();
					}
				}
			}
		});

		feeder.addContext(() -> null);

		ContextBasedConsumer<Runnable> consumer = new ContextBasedConsumer<>();
		consumer.whenRunForAnyAlgorithm().reaches(RunStatus.TERMINATED).execute((execution) -> {
			flags[1] = true;
		});

		SequentialExperimentExecutor<Runnable> executor = new SequentialExperimentExecutor<>((algo) -> algo.run());

		Iterator<Runnable> iterator = feeder.iterator();
		Runnable algorithm = iterator.next();
		AlgorithmExecution<Runnable> execution = executor.add(algorithm);
		consumer.watch(execution);

		executor.start();
		Thread.sleep(100);

		assertFalse(flags[1]);

		flags[0] = true;
		Thread.sleep(100);

		assertTrue(flags[1]);
	}

	@Test
	public void testWatchersForInterruptedAreProperlyApplied() throws InterruptedException {
		ContextBasedFeeder<Runnable> feeder = new ContextBasedFeeder<>();

		boolean[] flags = { false, false };
		feeder.<Runnable>addType(() -> new Runnable() {

			@Override
			public void run() {
				while (flags[0] == false) {
					// continue running
					System.gc();
				}
				throw new RuntimeException();
			}
		});

		feeder.addContext(() -> null);

		ContextBasedConsumer<Runnable> consumer = new ContextBasedConsumer<>();
		consumer.whenRunForAnyAlgorithm().reaches(RunStatus.INTERRUPTED).execute((execution) -> {
			flags[1] = true;
		});

		SequentialExperimentExecutor<Runnable> executor = new SequentialExperimentExecutor<>((algo) -> algo.run());

		Iterator<Runnable> iterator = feeder.iterator();
		Runnable algorithm = iterator.next();
		AlgorithmExecution<Runnable> execution = executor.add(algorithm);
		consumer.watch(execution);

		executor.start();
		Thread.sleep(100);

		assertFalse(flags[1]);

		flags[0] = true;
		Thread.sleep(100);

		assertTrue(flags[1]);
	}
}
