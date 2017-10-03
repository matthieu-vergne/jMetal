package org.uma.jmetal.experiment.impl;

import static org.junit.Assert.*;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.uma.jmetal.experiment.ExperimentFeeder;
import org.uma.jmetal.experiment.ExperimentFeederTest;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Context;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Parameter;
import org.uma.jmetal.experiment.impl.ContextBasedFeeder.Type;
import org.uma.jmetal.experiment.testUtil.Computer;
import org.uma.jmetal.experiment.testUtil.Product;
import org.uma.jmetal.experiment.testUtil.Sum;

public class ContextBasedFeederTest extends ExperimentFeederTest {

	@Override
	protected <Algorithm> ExperimentFeeder<Algorithm> generateExperimentFeeder(Collection<Algorithm> algorithms) {
		ContextBasedFeeder<Algorithm> feeder = new ContextBasedFeeder<Algorithm>();
		for (Algorithm algorithm : algorithms) {
			feeder.addType(() -> algorithm);
		}
		feeder.addContext(() -> new Object());
		return feeder;
	}

	@Test
	public void testNoContextLeadsToEmptyFeeder() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addType(() -> new Sum());

		assertEquals(0, feeder.size());
	}

	@Test
	public void testNoTypeLeadsToEmptyFeeder() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addContext(() -> new Object());

		assertEquals(0, feeder.size());
	}

	@Test
	public void testSingleContextWithSingleTypeLeadsToSingleItem() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addContext(() -> new Object());
		feeder.addType(() -> new Sum());

		assertEquals(1, feeder.size());
	}

	@Test
	public void testNContextsWithSingleTypeLeadsToNInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addContext(() -> new Object());
		feeder.addContext(() -> new Object());
		feeder.addContext(() -> new Object());
		feeder.addType(() -> new Sum());

		assertEquals(3, feeder.size());
	}

	@Test
	public void testSingleContextWithNTypesLeadsToNInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addContext(() -> new Object());
		feeder.addType(() -> new Sum());
		feeder.addType(() -> new Sum());
		feeder.addType(() -> new Sum());
		feeder.addType(() -> new Sum());

		assertEquals(4, feeder.size());
	}

	@Test
	public void testNContextsWithMTypesLeadsToNxMInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();
		feeder.addContext(() -> new Object());
		feeder.addContext(() -> new Object());
		feeder.addType(() -> new Sum());
		feeder.addType(() -> new Sum());

		assertEquals(4, feeder.size());
	}

	@Test
	public void testTypeRecognisesProperAlgorithmInstance() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<Computer>();

		Type<Sum> sum = feeder.<Sum>addType(() -> new Sum());
		Type<Product> product = feeder.<Product>addType(() -> new Product());

		feeder.addContext(() -> new Object());

		Iterator<Computer> iterator = feeder.iterator();
		Computer algorithm1 = iterator.next();
		Computer algorithm2 = iterator.next();
		assertTrue(sum.isTypeOf(algorithm1));
		assertFalse(sum.isTypeOf(algorithm2));
		assertFalse(product.isTypeOf(algorithm1));
		assertTrue(product.isTypeOf(algorithm2));
	}

	@Test
	public void testTypeIsWeaklyLinkedToAlgorithmInstance() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<Computer>();

		@SuppressWarnings("unused")
		Type<Sum> sum = feeder.<Sum>addType(() -> new Sum());

		feeder.addContext(() -> new Object());

		Iterator<Computer> iterator = feeder.iterator();
		WeakReference<Computer> algorithm1 = new WeakReference<Computer>(iterator.next());
		System.gc();
		assertNull(algorithm1.get());
	}

	@Test
	public void testComposedTypeRecognisesInstancesOfTypesItIsComposedOf() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> type1 = feeder.addType(() -> new Sum());
		Type<Sum> type2 = feeder.addType(() -> new Sum());

		Type<Sum> composedType = Type.compose(type1, type2);

		feeder.addContext(() -> new Object());
		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = iterator.next();
			assertTrue(type1.isTypeOf(algorithm));
			assertFalse(type2.isTypeOf(algorithm));
			assertTrue(composedType.isTypeOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(type1.isTypeOf(algorithm));
			assertTrue(type2.isTypeOf(algorithm));
			assertTrue(composedType.isTypeOf(algorithm));
		}
	}

	@Test
	public void testComposedTypeRecognisesOnlyInstancesOfTypesItIsComposedOf() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> type1 = feeder.addType(() -> new Sum());
		Type<Sum> type2 = feeder.addType(() -> new Sum());
		Type<Sum> type3 = feeder.addType(() -> new Sum());

		Type<Sum> composedType = Type.compose(type1, type3);

		feeder.addContext(() -> new Object());
		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = iterator.next();
			assertTrue(type1.isTypeOf(algorithm));
			assertFalse(type2.isTypeOf(algorithm));
			assertFalse(type3.isTypeOf(algorithm));
			assertTrue(composedType.isTypeOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(type1.isTypeOf(algorithm));
			assertTrue(type2.isTypeOf(algorithm));
			assertFalse(type3.isTypeOf(algorithm));
			assertFalse(composedType.isTypeOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(type1.isTypeOf(algorithm));
			assertFalse(type2.isTypeOf(algorithm));
			assertTrue(type3.isTypeOf(algorithm));
			assertTrue(composedType.isTypeOf(algorithm));
		}
	}

	@Test
	public void testContextRecognisesProperAlgorithm() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());

		Context<Object> context1 = feeder.addContext(() -> new Object());
		Context<Object> context2 = feeder.addContext(() -> new Object());

		Iterator<Sum> iterator = feeder.iterator();
		Sum algorithm1 = iterator.next();
		Sum algorithm2 = iterator.next();
		assertTrue(context1.isContextOf(algorithm1));
		assertFalse(context1.isContextOf(algorithm2));
		assertFalse(context2.isContextOf(algorithm1));
		assertTrue(context2.isContextOf(algorithm2));
	}

	@Test
	public void testContextIsWeaklyLinkedToAlgorithmInstance() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());
		@SuppressWarnings("unused")
		Context<Object> context = feeder.addContext(() -> new Object());

		Iterator<Sum> iterator = feeder.iterator();
		WeakReference<Sum> algorithm1 = new WeakReference<Sum>(iterator.next());
		System.gc();
		assertNull(algorithm1.get());
	}

	@Test
	public void testComposedContextRecognisesInstancesOfContextsItIsComposedOf() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());

		Context<Object> context1 = feeder.addContext(() -> new Object());
		Context<Object> context2 = feeder.addContext(() -> new Object());
		Context<Object> composedContext = Context.compose(context1, context2);

		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = iterator.next();
			assertTrue(context1.isContextOf(algorithm));
			assertFalse(context2.isContextOf(algorithm));
			assertTrue(composedContext.isContextOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(context1.isContextOf(algorithm));
			assertTrue(context2.isContextOf(algorithm));
			assertTrue(composedContext.isContextOf(algorithm));
		}
	}

	@Test
	public void testComposedContextRecognisesOnlyInstancesOfContextsItIsComposedOf() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());

		Context<Object> context1 = feeder.addContext(() -> new Object());
		Context<Object> context2 = feeder.addContext(() -> new Object());
		Context<Object> context3 = feeder.addContext(() -> new Object());
		Context<Object> composedContext = Context.compose(context1, context3);

		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = iterator.next();
			assertTrue(context1.isContextOf(algorithm));
			assertFalse(context2.isContextOf(algorithm));
			assertFalse(context3.isContextOf(algorithm));
			assertTrue(composedContext.isContextOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(context1.isContextOf(algorithm));
			assertTrue(context2.isContextOf(algorithm));
			assertFalse(context3.isContextOf(algorithm));
			assertFalse(composedContext.isContextOf(algorithm));
		}

		{
			Sum algorithm = iterator.next();
			assertFalse(context1.isContextOf(algorithm));
			assertFalse(context2.isContextOf(algorithm));
			assertTrue(context3.isContextOf(algorithm));
			assertTrue(composedContext.isContextOf(algorithm));
		}
	}

	@Test
	public void testInstancesAreConfiguredWithRightContexts() {
		ContextBasedFeeder<Computer> feeder = new ContextBasedFeeder<Computer>();

		Type<Sum> sum = feeder.<Sum>addType(() -> new Sum());
		Type<Product> product = feeder.<Product>addType(() -> new Product());

		Context<float[]> context1 = feeder.addContext(() -> new float[] { 1, 2 });
		Context<float[]> context2 = feeder.addContext(() -> new float[] { 5, -1 });

		Parameter<Float> p1 = feeder.createParameter(Float.class);
		feeder.retrieve(p1).from(context1).with((data) -> data[0]);
		feeder.retrieve(p1).from(context2).with((data) -> data[0]);
		feeder.assign(p1).to(sum).with((algorithm, value) -> algorithm.sum1 = value);
		feeder.assign(p1).to(product).with((algorithm, value) -> algorithm.product1 = value);

		Parameter<Float> p2 = feeder.createParameter(Float.class);
		feeder.retrieve(p2).from(context1).with((data) -> data[1]);
		feeder.retrieve(p2).from(context2).with((data) -> data[1]);
		feeder.assign(p2).to(sum).with((algorithm, value) -> algorithm.sum2 = value);
		feeder.assign(p2).to(product).with((algorithm, value) -> algorithm.product2 = value);

		assertEquals(4, feeder.size());
		Iterator<Computer> iterator = feeder.iterator();

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(1, algorithm.sum1, 0.0);
			assertEquals(2, algorithm.sum2, 0.0);
		}

		{
			Product algorithm = (Product) iterator.next();
			assertEquals(1, algorithm.product1, 0.0);
			assertEquals(2, algorithm.product2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(5, algorithm.sum1, 0.0);
			assertEquals(-1, algorithm.sum2, 0.0);
		}

		{
			Product algorithm = (Product) iterator.next();
			assertEquals(5, algorithm.product1, 0.0);
			assertEquals(-1, algorithm.product2, 0.0);
		}
	}

	@Test
	public void testInstancesAreConfiguredWithRightContextsWithComposedTypeAssignment() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.<Sum>addType(() -> new Sum());
		Type<Sum> sum2 = feeder.<Sum>addType(() -> new Sum());

		Context<float[]> context1 = feeder.addContext(() -> new float[] { 1, 2 });
		Context<float[]> context2 = feeder.addContext(() -> new float[] { 5, -1 });

		Parameter<Float> p1 = feeder.createParameter(Float.class);
		feeder.retrieve(p1).from(context1).with((data) -> data[0]);
		feeder.retrieve(p1).from(context2).with((data) -> data[0]);
		Type<Sum> composedType = Type.compose(sum1, sum2);
		feeder.assign(p1).to(composedType).with((algorithm, value) -> algorithm.sum1 = value);

		Parameter<Float> p2 = feeder.createParameter(Float.class);
		feeder.retrieve(p2).from(context1).with((data) -> data[1]);
		feeder.retrieve(p2).from(context2).with((data) -> data[1]);
		feeder.assign(p2).to(sum1).with((algorithm, value) -> algorithm.sum2 = value);
		feeder.assign(p2).to(sum2).with((algorithm, value) -> algorithm.sum2 = value);

		assertEquals(4, feeder.size());
		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(1, algorithm.sum1, 0.0);
			assertEquals(2, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(1, algorithm.sum1, 0.0);
			assertEquals(2, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(5, algorithm.sum1, 0.0);
			assertEquals(-1, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(5, algorithm.sum1, 0.0);
			assertEquals(-1, algorithm.sum2, 0.0);
		}
	}

	@Test
	public void testInstancesAreConfiguredWithRightContextsWithComposedContextRetrieval() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.<Sum>addType(() -> new Sum());
		Type<Sum> sum2 = feeder.<Sum>addType(() -> new Sum());

		Context<float[]> context1 = feeder.addContext(() -> new float[] { 1, 2 });
		Context<float[]> context2 = feeder.addContext(() -> new float[] { 5, -1 });

		Parameter<Float> p1 = feeder.createParameter(Float.class);
		Context<float[]> composedContext = Context.compose(context1, context2);
		feeder.retrieve(p1).from(composedContext).with((data) -> data[0]);
		feeder.assign(p1).to(sum1).with((algorithm, value) -> algorithm.sum1 = value);
		feeder.assign(p1).to(sum2).with((algorithm, value) -> algorithm.sum1 = value);

		Parameter<Float> p2 = feeder.createParameter(Float.class);
		feeder.retrieve(p2).from(context1).with((data) -> data[1]);
		feeder.retrieve(p2).from(context2).with((data) -> data[1]);
		feeder.assign(p2).to(sum1).with((algorithm, value) -> algorithm.sum2 = value);
		feeder.assign(p2).to(sum2).with((algorithm, value) -> algorithm.sum2 = value);

		assertEquals(4, feeder.size());
		Iterator<Sum> iterator = feeder.iterator();

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(1, algorithm.sum1, 0.0);
			assertEquals(2, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(1, algorithm.sum1, 0.0);
			assertEquals(2, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(5, algorithm.sum1, 0.0);
			assertEquals(-1, algorithm.sum2, 0.0);
		}

		{
			Sum algorithm = (Sum) iterator.next();
			assertEquals(5, algorithm.sum1, 0.0);
			assertEquals(-1, algorithm.sum2, 0.0);
		}
	}

	@Test
	public void testParameterConstraintProperlyThrowsException() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Context<float[]> context = feeder.addContext(() -> new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context).with((p) -> p[0]);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().throwException();

		try {
			feeder.iterator().next();
			fail("No exception thrown");
		} catch (Exception e) {
			// OK
		}
	}

	@Test
	public void testParameterConstraintProperlyReplacesValue() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Context<float[]> context = feeder.addContext(new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context).with((p) -> p[0]);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(35f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(35, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterReplacementsAreNotReapplied() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(35f);
		feeder.when(parameter).satisfies((v) -> v == 35).inAnyAlgorithm().replaceBy(42f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(35, iterator.next().sum1, 0);// 42 if both are applied
		assertEquals(42, iterator.next().sum1, 0);// 35 if both are applied
	}

	@Test
	public void testFirstValidParameterReplacementsIsApplied() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		for (int i = 0; i < 10; i++) {
			Context<float[]> context = feeder.addContext(() -> new float[] { 42 });
			feeder.retrieve(parameter).from(context).with((p) -> p[0]);
		}

		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(35f);
		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(8f);
		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(56f);
		feeder.when(parameter).satisfies((v) -> v == 42).inAnyAlgorithm().replaceBy(-5f);

		for (Sum algorithm : feeder) {
			assertEquals(35, algorithm.sum1, 0);
		}
	}

	@Test
	public void testParameterConstraintOnAlgorithmInstanceSelectsCorrectInstance() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).getAnyValue().in(sum1, context2).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnComposedTypeAndContextSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Type<Sum> sum3 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum3).with((a, v) -> a.sum1 = v);

		Type<Sum> composedType = Type.compose(sum1, sum3);
		feeder.when(parameter).getAnyValue().in(composedType, context2).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnTypeAndComposedContextSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });
		Context<float[]> context3 = feeder.addContext(new float[] { 28 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context3).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		Context<float[]> composedContext = Context.compose(context1, context3);
		feeder.when(parameter).getAnyValue().in(sum2, composedContext).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(28, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnTypeSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).getAnyValue().inAll(sum1).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnComposedTypeSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Type<Sum> sum3 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum3).with((a, v) -> a.sum1 = v);

		Type<Sum> composedType = Type.compose(sum1, sum3);
		feeder.when(parameter).getAnyValue().inAll(composedType).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(42, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnContextSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).getAnyValue().inAll(context1).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnComposedContextSelectsCorrectInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });
		Context<float[]> context3 = feeder.addContext(new float[] { 28 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context3).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		Context<float[]> composedContext = Context.compose(context1, context3);
		feeder.when(parameter).getAnyValue().inAll(composedContext).replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(35, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterConstraintOnAnyAlgorithmSelectsAllInstances() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(new float[] { 35 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context1).with((p) -> p[0]);
		feeder.retrieve(parameter).from(context2).with((p) -> p[0]);
		feeder.assign(parameter).to(sum1).with((a, v) -> a.sum1 = v);
		feeder.assign(parameter).to(sum2).with((a, v) -> a.sum1 = v);

		feeder.when(parameter).getAnyValue().inAnyAlgorithm().replaceBy(0f);

		Iterator<Sum> iterator = feeder.iterator();
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
		assertEquals(0, iterator.next().sum1, 0);
	}

	@Test
	public void testParameterRequiredByTypeThrowsExceptionIfNotRetrievedFromContext() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum = feeder.addType(() -> new Sum());
		feeder.addContext(() -> new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		try {
			feeder.iterator().next();
			fail("No exception thrown");
		} catch (Exception e) {
			// OK
		}
	}

	@Test
	public void testParameterRequiredByComposedTypeThrowsExceptionIfNotRetrievedFromContext() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		Type<Sum> sum1 = feeder.addType(() -> new Sum());
		Type<Sum> sum2 = feeder.addType(() -> new Sum());
		Type<Sum> sum = Type.compose(sum1, sum2);
		feeder.addContext(() -> new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.assign(parameter).to(sum).with((a, v) -> a.sum1 = v);

		try {
			feeder.iterator().next();
			fail("No exception thrown");
		} catch (Exception e) {
			// OK
		}
	}

	@Test
	public void testParameterRetrievedFromContextDoesNotThrowExceptionIfNotAppliedToType() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());
		Context<float[]> context = feeder.addContext(() -> new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		feeder.retrieve(parameter).from(context).with((p) -> p[0]);

		feeder.iterator().next();
	}

	@Test
	public void testParameterRetrievedFromComposedContextDoesNotThrowExceptionIfNotAppliedToType() {
		ContextBasedFeeder<Sum> feeder = new ContextBasedFeeder<Sum>();

		feeder.addType(() -> new Sum());
		Context<float[]> context1 = feeder.addContext(() -> new float[] { 42 });
		Context<float[]> context2 = feeder.addContext(() -> new float[] { 42 });

		Parameter<Float> parameter = feeder.createParameter(Float.class);
		Context<float[]> context = Context.compose(context1, context2);
		feeder.retrieve(parameter).from(context).with((p) -> p[0]);

		feeder.iterator().next();
	}
}
