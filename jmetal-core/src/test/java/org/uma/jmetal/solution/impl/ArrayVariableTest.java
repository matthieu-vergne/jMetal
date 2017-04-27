package org.uma.jmetal.solution.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArrayVariableTest {

	@Test
	public void testArrayVariableRetrievesProperIndex() {
		ArrayVariable<Integer> variable0 = new ArrayVariable<>(0);
		ArrayVariable<Integer> variable1 = new ArrayVariable<>(1);
		ArrayVariable<Integer> variable2 = new ArrayVariable<>(2);

		Integer[] solution = { 56, 24, 12 };
		assertEquals(56, (int) variable0.readFrom(solution));
		assertEquals(24, (int) variable1.readFrom(solution));
		assertEquals(12, (int) variable2.readFrom(solution));
	}

}
