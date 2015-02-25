package org.uma.jmetal.parameter.space;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class ParameterSpaceFactoryTest {

	@Test
	public void testNonNullValuesNeverInEmptySpace() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();
		ParameterSpace<Integer> space = factory
				.<Integer> createEmptySpace(true);

		assertFalse(space.contains(0));
		assertFalse(space.contains(1));
		assertFalse(space.contains(3498));
		assertFalse(space.contains(-234578));
		assertFalse(space.contains(Integer.MIN_VALUE));
		assertFalse(space.contains(Integer.MAX_VALUE));
	}

	@Test
	public void testNullValuesInEmptySpaceOnlyWhenAccepted() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();
		assertTrue(factory.createEmptySpace(true).contains(null));
		assertFalse(factory.createEmptySpace(false).contains(null));
	}

	@Test
	public void testNonNullValuesAlwaysInFullSpace() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();
		ParameterSpace<Integer> space = factory.<Integer> createFullSpace(true);

		assertTrue(space.contains(0));
		assertTrue(space.contains(1));
		assertTrue(space.contains(3498));
		assertTrue(space.contains(-234578));
		assertTrue(space.contains(Integer.MIN_VALUE));
		assertTrue(space.contains(Integer.MAX_VALUE));
	}

	@Ignore("Need to check generic type, which is not feasible unless you give"
			+ " explicitely the Class. Try to find a workaround which does not"
			+ " need this Class.")
	@Test
	public void testFullSpaceRobustToHeapPollution() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();
		ParameterSpace<List<String>> space = factory
				.<List<String>> createFullSpace(true);

		// Trick the compiler to store a List<Integer> as a List<String>
		@SuppressWarnings("unchecked")
		List<String>[] bar = new List[1];
		Object[] objectArray = bar;
		objectArray[0] = Arrays.asList(new Integer(42));
		List<String> corrupted = bar[0];

		// The List<Integer> should not be part of the List<String> space
		assertFalse(space.contains(corrupted));
	}

	@Test
	public void testNullValuesInFullSpaceOnlyWhenAccepted() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();
		assertTrue(factory.createFullSpace(true).contains(null));
		assertFalse(factory.createFullSpace(false).contains(null));
	}

	@Test
	public void testExplicitSpaceOnlyContainsSpecifiedValues() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createExplicitSpace(1, 2, 3);
		assertFalse(space1.contains(null));
		assertFalse(space1.contains(0));
		assertTrue(space1.contains(1));
		assertTrue(space1.contains(2));
		assertTrue(space1.contains(3));
		assertFalse(space1.contains(4));

		ParameterSpace<String> space2 = factory.createExplicitSpace("a", null,
				"b");
		assertTrue(space2.contains(null));
		assertFalse(space2.contains(""));
		assertTrue(space2.contains("a"));
		assertTrue(space2.contains("b"));
		assertFalse(space2.contains("ab"));
	}

	@Test
	public void testComplementSpaceReverseExplicitSpace() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createComplementSpace(factory
				.createExplicitSpace(1, 2, 3));
		assertTrue(space1.contains(null));
		assertTrue(space1.contains(0));
		assertFalse(space1.contains(1));
		assertFalse(space1.contains(2));
		assertFalse(space1.contains(3));
		assertTrue(space1.contains(4));

		ParameterSpace<String> space2 = factory.createComplementSpace(factory
				.createExplicitSpace("a", null, "b"));
		assertFalse(space2.contains(null));
		assertTrue(space2.contains(""));
		assertFalse(space2.contains("a"));
		assertFalse(space2.contains("b"));
		assertTrue(space2.contains("ab"));
	}

	@Test
	public void testUnionSpaceContainsBothExplicitSpaces() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createExplicitSpace(1, 2, 3);
		ParameterSpace<Integer> space2 = factory.createExplicitSpace(3, 4, 5);
		ParameterSpace<Integer> union = factory
				.createUnionSpace(space1, space2);

		assertTrue(union.contains(1));
		assertTrue(union.contains(2));
		assertTrue(union.contains(3));
		assertTrue(union.contains(4));
		assertTrue(union.contains(5));
	}

	@Test
	public void testIntersectionSpaceContainsOnlySharedExplicitSpaces() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createExplicitSpace(1, 2, 3);
		ParameterSpace<Integer> space2 = factory.createExplicitSpace(3, 4, 5);
		ParameterSpace<Integer> inter = factory.createIntersectionSpace(space1,
				space2);

		assertFalse(inter.contains(1));
		assertFalse(inter.contains(2));
		assertTrue(inter.contains(3));
		assertFalse(inter.contains(4));
		assertFalse(inter.contains(5));
	}

	@Test
	public void testUnionCanTakeDifferentTypes() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createExplicitSpace(1, 2, 3);
		ParameterSpace<String> space2 = factory.createExplicitSpace("a", null,
				"b");
		ParameterSpace<Object> union = factory.createUnionSpace(space1, space2);
		assertNotNull(union);
	}

	@Test
	public void testIntersectionCanTakeDifferentTypes() {
		ParameterSpaceFactory factory = new ParameterSpaceFactory();

		ParameterSpace<Integer> space1 = factory.createExplicitSpace(1, 2, 3);
		ParameterSpace<String> space2 = factory.createExplicitSpace("a", null,
				"b");
		ParameterSpace<Object> inter = factory.createIntersectionSpace(space1,
				space2);
		assertNotNull(inter);
	}
}
