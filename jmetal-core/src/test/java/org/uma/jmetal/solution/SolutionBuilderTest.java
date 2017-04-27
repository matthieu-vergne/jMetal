package org.uma.jmetal.solution;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.uma.jmetal.solution.SolutionBuilder.InvalidVariableValueException;
import org.uma.jmetal.solution.SolutionBuilder.UnableToBuildSolutionException;
import org.uma.jmetal.solution.SolutionBuilder.UnmanagedVariableException;

public abstract class SolutionBuilderTest<Solution> {

	public abstract SolutionBuilder<Solution> createSolutionBuilder();

	public abstract Boolean hasUnmanagedVariables();

	public abstract Map<Variable<Solution, ?>, Object> getUnmanagedVariablesWithFakeValues(
			SolutionBuilder<Solution> builder);

	public abstract Boolean hasManagedVariablesWithInvalidValues();

	public abstract Map<Variable<Solution, ?>, Object> getManagedVariablesWithInvalidValues(
			SolutionBuilder<Solution> builder);

	public abstract Boolean hasInvalidSettingWithValidValues();

	public abstract Map<Variable<Solution, ?>, Object> getInvalidSettingWithValidValues(
			SolutionBuilder<Solution> builder);

	public abstract Map<Variable<Solution, ?>, Object> getValidSetting(SolutionBuilder<Solution> builder);

	public abstract Boolean hasInvalidResetSetting();

	public abstract Map<Variable<Solution, ?>, Object> getResetSetting(SolutionBuilder<Solution> builder);

	@SuppressWarnings("unchecked")
	@Test
	public <Value> void testUnmanagedVariablesGeneratesVariableException() {
		if (!hasUnmanagedVariables()) {
			// No invalid variable to test
		} else {
			SolutionBuilder<Solution> builder = createSolutionBuilder();
			assertNotNull("No builder provided", builder);

			Map<Variable<Solution, ?>, ?> unmanagedVariables = getUnmanagedVariablesWithFakeValues(builder);
			assertNotNull("No invalid variable provided", unmanagedVariables);
			assertFalse("No invalid variable provided", unmanagedVariables.isEmpty());
			for (Entry<Variable<Solution, ?>, ?> entry : unmanagedVariables.entrySet()) {
				Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
				Value value = (Value) entry.getValue();
				try {
					builder.set(variable, value);
					fail("Variable set without exception");
				} catch (UnmanagedVariableException e) {
					// OK
				}
			}

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public <Value> void testInvalidValuesGeneratesVariableException() {
		if (!hasManagedVariablesWithInvalidValues()) {
			// No invalid value to test
		} else {
			SolutionBuilder<Solution> builder = createSolutionBuilder();
			assertNotNull("No builder provided", builder);

			Map<Variable<Solution, ?>, ?> invalidValues = getManagedVariablesWithInvalidValues(builder);
			assertNotNull("No invalid value provided", invalidValues);
			assertFalse("No invalid value provided", invalidValues.isEmpty());
			for (Entry<Variable<Solution, ?>, ?> entry : invalidValues.entrySet()) {
				Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
				Value value = (Value) entry.getValue();
				try {
					builder.set(variable, value);
					fail("Value set without exception");
				} catch (InvalidVariableValueException e) {
					// OK
				}
			}

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public <Value> void testInvalidSettingWithValidValuesGeneratesBuildException() {
		if (!hasInvalidSettingWithValidValues()) {
			// No invalid setting to test
		} else {
			SolutionBuilder<Solution> builder = createSolutionBuilder();
			assertNotNull("No builder provided", builder);

			Map<Variable<Solution, ?>, ?> invalidSetting = getInvalidSettingWithValidValues(builder);
			assertNotNull("No invalid setting provided", invalidSetting);
			for (Entry<Variable<Solution, ?>, ?> entry : invalidSetting.entrySet()) {
				Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
				Value value = (Value) entry.getValue();
				builder.set(variable, value);
			}

			try {
				builder.build();
				fail("Solution instantiated despite the invalid setting");
			} catch (UnableToBuildSolutionException e) {
				// OK
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public <Value> void testValidSettingGeneratesProperSolution() {
		SolutionBuilder<Solution> builder = createSolutionBuilder();
		assertNotNull("No builder provided", builder);

		Map<Variable<Solution, ?>, ?> validSetting = getValidSetting(builder);
		assertNotNull("No valid setting provided", validSetting);
		assertFalse("Empty valid setting provided", validSetting.isEmpty());
		for (Entry<Variable<Solution, ?>, ?> entry : validSetting.entrySet()) {
			Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
			Value value = (Value) entry.getValue();
			builder.set(variable, value);
		}

		Solution solution = builder.build();

		for (Entry<Variable<Solution, ?>, ?> entry : validSetting.entrySet()) {
			Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
			Value value = (Value) entry.getValue();
			assertEquals("Value " + value + " not set properly for variable " + variable, value,
					variable.readFrom(solution));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public <Value> void testResetProperlySetBuilder() {
		SolutionBuilder<Solution> builder = createSolutionBuilder();
		assertNotNull("No builder provided", builder);

		Map<Variable<Solution, ?>, ?> validSetting = getValidSetting(builder);
		assertNotNull("No valid setting provided", validSetting);
		assertFalse("Empty valid setting provided", validSetting.isEmpty());
		for (Entry<Variable<Solution, ?>, ?> entry : validSetting.entrySet()) {
			Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
			Value value = (Value) entry.getValue();
			builder.set(variable, value);
		}

		builder.reset();
		if (hasInvalidResetSetting()) {
			try {
				builder.build();
				fail("No exception after reset");
			} catch (UnableToBuildSolutionException e) {
			}
		} else {
			Solution solution = builder.build();
			Map<Variable<Solution, ?>, ?> resetSetting = getResetSetting(builder);
			assertNotNull("No reset setting provided", resetSetting);
			assertFalse("Empty reset setting provided", resetSetting.isEmpty());
			for (Entry<Variable<Solution, ?>, ?> entry : resetSetting.entrySet()) {
				Variable<Solution, Value> variable = (Variable<Solution, Value>) entry.getKey();
				Value value = (Value) entry.getValue();
				assertEquals("Value " + value + " not set properly for variable " + variable, value,
						variable.readFrom(solution));
			}
		}
	}
}
