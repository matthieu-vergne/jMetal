package org.uma.jmetal.solution.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.uma.jmetal.solution.SolutionBuilder;
import org.uma.jmetal.solution.SolutionBuilderTest;
import org.uma.jmetal.solution.Variable;

public class ListSolutionBuilderTest extends SolutionBuilderTest<List<Integer>> {

	@Override
	public SolutionBuilder<List<Integer>> createSolutionBuilder() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		builder.assignVariable("var0").toIndex(0).forAnyValue().asMandatoryVariable();
		builder.assignVariable("var1").toIndex(1).forValues(v -> v > 0).asMandatoryVariable();
		builder.assignVariable("var2").toIndex(2).forValues(v -> v > 0 && v < 5).asMandatoryVariable();
		return builder;
	}

	@Override
	public Boolean hasUnmanagedVariables() {
		return true;
	}

	@Override
	public Map<Variable<List<Integer>, ?>, Object> getUnmanagedVariablesWithFakeValues(
			SolutionBuilder<List<Integer>> builder) {
		Map<Variable<List<Integer>, ?>, Object> map = new HashMap<>();
		map.put((s) -> s.get(0) + 1, 15);// Not builder variable
		map.put((s) -> s.size(), 4);// Not builder variable
		map.put((s) -> s.get(0) * 2, -5);// Not builder variable
		return map;
	}

	@Override
	public Boolean hasManagedVariablesWithInvalidValues() {
		return true;
	}

	@Override
	public Map<Variable<List<Integer>, ?>, Object> getManagedVariablesWithInvalidValues(
			SolutionBuilder<List<Integer>> builder) {
		ListSolutionBuilder<Integer> b = (ListSolutionBuilder<Integer>) builder;
		Map<Variable<List<Integer>, ?>, Object> map = new HashMap<>();
		map.put(b.getVariable(1), -5);// Not positive
		map.put(b.getVariable(2), 8);// Not in valid range
		return map;
	}

	@Override
	public Boolean hasInvalidSettingWithValidValues() {
		return true;
	}

	@Override
	public Map<Variable<List<Integer>, ?>, Object> getInvalidSettingWithValidValues(
			SolutionBuilder<List<Integer>> builder) {
		ListSolutionBuilder<Integer> b = (ListSolutionBuilder<Integer>) builder;
		Map<Variable<List<Integer>, ?>, Object> map = new HashMap<>();
		// Miss mandatory variable 0
		map.put(b.getVariable(1), 4);
		map.put(b.getVariable(2), 2);
		return map;
	}

	@Override
	public Map<Variable<List<Integer>, ?>, Object> getValidSetting(SolutionBuilder<List<Integer>> builder) {
		ListSolutionBuilder<Integer> b = (ListSolutionBuilder<Integer>) builder;
		Map<Variable<List<Integer>, ?>, Object> map = new HashMap<>();
		map.put(b.getVariable(0), 7);
		map.put(b.getVariable(1), 4);
		map.put(b.getVariable(2), 2);
		return map;
	}

	@Override
	public Boolean hasInvalidResetSetting() {
		return true;
	}

	@Override
	public Map<Variable<List<Integer>, ?>, Object> getResetSetting(SolutionBuilder<List<Integer>> builder) {
		return null;
	}

	@Test
	public void testBuildNotNullEmptySolutionIfNoVariable() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		List<Integer> solution = builder.build();
		assertNotNull(solution);
		assertTrue(solution.isEmpty());
	}

	@Test
	public void testBuildArrayListByDefault() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		assertTrue(builder.build() instanceof ArrayList);
	}

	@Test
	public void testBuildInstanceFromSupplier() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		assertFalse(builder.build() instanceof LinkedList);
		builder.setListSupplier(() -> new LinkedList<>());
		assertTrue(builder.build() instanceof LinkedList);
	}

	@Test
	public void testVariableNameRetrievedInStringRepresentation() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		Variable<List<Integer>, Integer> variable = builder.assignVariable("var").toIndex(0).forAnyValue()
				.asMandatoryVariable();
		assertEquals("var", variable.toString());
	}
	
	@Test
	public void testVariableAutomaticallyNamedIfNotManually() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		Variable<List<Integer>, Integer> var0 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var1 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var2 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		assertEquals("[0]", var0.toString());
		assertEquals("[1]", var1.toString());
		assertEquals("[2]", var2.toString());
	}

	@Test
	public void testBuilderHasCorrectSize() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		assertTrue(builder.getSize() == 0);
		builder.assignVariable().toIndex(0).forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 1);
		builder.assignVariable().toIndex(1).forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 2);
		builder.assignVariable().toIndex(2).forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
	}

	@Test
	public void testBuilderHasCorrectSizeWithPartialAssignment() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		builder.assignVariable().toIndex(2).forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
	}

	@Test
	public void testBuilderHasCorrectSizeWithAutoIndex() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		assertTrue(builder.getSize() == 0);
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 1);
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 2);
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
	}

	@Test
	public void testAutoIndexFeedMissingIndexesInCorrectOrder() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		builder.assignVariable().toIndex(2).forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
		assertNull(builder.getVariable(0));
		assertNull(builder.getVariable(1));
		assertNotNull(builder.getVariable(2));
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
		assertNotNull(builder.getVariable(0));
		assertNull(builder.getVariable(1));
		assertNotNull(builder.getVariable(2));
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 3);
		assertNotNull(builder.getVariable(0));
		assertNotNull(builder.getVariable(1));
		assertNotNull(builder.getVariable(2));
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertTrue(builder.getSize() == 4);
	}

	@Test
	public void testSolutionHasSameSizeThanBuilder() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		assertEquals(builder.getSize(), builder.build().size());
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertEquals(builder.getSize(), builder.build().size());
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertEquals(builder.getSize(), builder.build().size());
		builder.assignVariable().toFirstFreeIndex().forAnyValue().withDefault(0);
		assertEquals(builder.getSize(), builder.build().size());
	}

	@Test
	public void testVariableIndexProperlySet() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		Variable<List<Integer>, Integer> var0 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var1 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var2 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();

		builder.set(var0, 5);
		builder.set(var1, 10);
		builder.set(var2, 20);

		List<Integer> solution = builder.build();
		assertEquals(5, (int) solution.get(0));
		assertEquals(10, (int) solution.get(1));
		assertEquals(20, (int) solution.get(2));
	}

	@Test
	public void testVariableProperlyRetrievedFromIndex() {
		ListSolutionBuilder<Integer> builder = new ListSolutionBuilder<>();
		Variable<List<Integer>, Integer> var0 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var1 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();
		Variable<List<Integer>, Integer> var2 = builder.assignVariable().toFirstFreeIndex().forAnyValue()
				.asMandatoryVariable();

		assertEquals(var0, builder.getVariable(0));
		assertEquals(var1, builder.getVariable(1));
		assertEquals(var2, builder.getVariable(2));
	}

}
