package org.uma.jmetal.parameter.generator.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.ParameterManager;
import org.uma.jmetal.parameter.generator.GenerableParameter;
import org.uma.jmetal.parameter.generator.ParameterableValueGenerator;
import org.uma.jmetal.parameter.generator.ValueGenerator;

public class RandomGenerator<Value> implements ValueGenerator<Value> {

	private final Map<ValueGenerator<?>, Parameter<?>> subGenerators;
	private final ParameterableValueGenerator<Value> rootGenerator;

	public RandomGenerator(ParameterableValueGenerator<Value> generator,
			Random random) {
		ParameterManager rootManager = generator.getParameterManager();
		if (!rootManager.iterator().hasNext()) {
			throw new IllegalArgumentException(
					"Although the generator is parameterable, no parameter is actually provided, so no random value can be set.");
		} else {
			this.rootGenerator = generator;
			this.subGenerators = new HashMap<>();
			ValueGeneratorFactory factory = new ValueGeneratorFactory();
			for (Entry<Object, Parameter<?>> entry : rootManager) {
				Parameter<?> parameter = entry.getValue();
				if (parameter instanceof GenerableParameter) {
					ValueGenerator<?> subGenerator = ((GenerableParameter<?>) parameter)
							.getValueGenerator();
					if (subGenerator instanceof FiniteDiscreteRangeGenerator) {
						ValueGenerator<?> wrapper = factory
								.createRandomGenerator(
										(FiniteDiscreteRangeGenerator<?>) subGenerator,
										random);
						subGenerators.put(wrapper, parameter);
					} else if (subGenerator instanceof FiniteContinuousRangeGenerator) {
						ValueGenerator<?> wrapper = factory
								.createRandomGenerator(
										(FiniteContinuousRangeGenerator<?>) subGenerator,
										random);
						subGenerators.put(wrapper, parameter);
					} else if (subGenerator instanceof ParameterableValueGenerator) {
						ValueGenerator<?> wrapper = factory
								.createRandomGenerator(
										(ParameterableValueGenerator<?>) subGenerator,
										random);
						subGenerators.put(wrapper, parameter);
					} else {
						throw new IllegalArgumentException("The generator "
								+ generator + " lies on a generable parameter "
								+ parameter
								+ " with an unmanageable generator "
								+ subGenerator);
					}
				} else {
					throw new IllegalArgumentException("The generator "
							+ generator + " lies on a parameter " + parameter
							+ " that we cannot generate.");
				}
			}
		}
	}

	@Override
	public boolean canGenerate() {
		Collection<ValueGenerator<?>> remaining = new LinkedList<>();
		remaining.addAll(subGenerators.keySet());
		boolean isProgressing = false;
		do {
			Collection<ValueGenerator<?>> redo = new LinkedList<>();
			for (ValueGenerator<?> subGenerator : remaining) {
				if (subGenerator.canGenerate()) {
					apply(subGenerator);
					isProgressing = true;
				} else {
					redo.add(subGenerator);
				}
			}
			remaining = redo;
		} while (isProgressing && !remaining.isEmpty());

		return remaining.isEmpty();
	}

	@SuppressWarnings("unchecked")
	private <V> void apply(ValueGenerator<V> subGenerator) {
		Parameter<V> subParameter = (Parameter<V>) subGenerators
				.get(subGenerator);
		V subValue = subGenerator.generate();
		subParameter.set(subValue);
	}

	@Override
	public Value generate() {
		return rootGenerator.generate();
	}

}
