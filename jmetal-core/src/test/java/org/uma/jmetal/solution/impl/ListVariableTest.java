package org.uma.jmetal.solution.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ListVariableTest {

	@Test
	public void testListVariableRetrievesProperIndex() {
		ListVariable<Integer> variable0 = new ListVariable<>(0);
		ListVariable<Integer> variable1 = new ListVariable<>(1);
		ListVariable<Integer> variable2 = new ListVariable<>(2);

		List<Integer> solution = Arrays.asList(56, 24, 12);
		assertEquals(56, (int) variable0.readFrom(solution));
		assertEquals(24, (int) variable1.readFrom(solution));
		assertEquals(12, (int) variable2.readFrom(solution));
	}

}
