package org.uma.jmetal.generator.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.generator.Generator;

//TODO document
//TODO test
public class GeneratorFactory {

	public Generator<Integer> createRandomIntegerGenerator(final int min,
			final int max) {
		return new Generator<Integer>() {

			private final Random rand = new Random();

			@Override
			public Integer generate() {
				return rand.nextInt(max - min) + min;
			}
		};
	}

	public Generator<Double> createRandomDoubleGenerator(final double min,
			final double max) {
		return new Generator<Double>() {

			private final Random rand = new Random();

			@Override
			public Double generate() {
				return rand.nextDouble() * (max - min) + min;
			}
		};
	}

	public Generator<Double> createRandomDoubleGenerator() {
		return new Generator<Double>() {

			private final Random rand = new Random();

			@Override
			public Double generate() {
				return rand.nextDouble();
			}
		};
	}

	public Generator<Boolean> createRandomBooleanGenerator() {
		return new Generator<Boolean>() {

			private final Random rand = new Random();

			@Override
			public Boolean generate() {
				return rand.nextBoolean();
			}
		};
	}

	public <T> Generator<T> createRandomCollectionGenerator(
			final Collection<T> collection) {
		return new Generator<T>() {

			private final Random rand = new Random();
			private final List<T> list = new LinkedList<>(collection);

			@Override
			public T generate() {
				return list.get(rand.nextInt(list.size()));
			}
		};
	}

	public <T extends Enum<T>> Generator<T> createRandomEnumGenerator(
			final Class<T> enumClass) {
		return new Generator<T>() {

			private final Random rand = new Random();
			private final T[] constants = enumClass.getEnumConstants();

			@Override
			public T generate() {
				return constants[rand.nextInt(constants.length)];
			}
		};
	}
}
