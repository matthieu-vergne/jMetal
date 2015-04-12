package org.uma.jmetal.parameter.generator;

import org.uma.jmetal.parameter.generator.impl.BuilderBasedGenerator;
import org.uma.jmetal.parameter.generator.impl.FiniteDiscreteRangeGenerator;
import org.uma.jmetal.parameter.generator.impl.RandomGenerator;

/**
 * A {@link ValueGenerator} aims at creating a {@link Value} on demand from a
 * space of {@link Value}s. If {@link #canGenerate()} returns <code>true</code>,
 * it means that the {@link ValueGenerator} is ready to generate a {@link Value}
 * and that we can call {@link #generate()} to obtain it, otherwise nothing is
 * guaranteed regarding the generation: it can return a valid {@link Value},
 * return <code>null</code>, or generate an {@link Exception}.<br/>
 * <br/>
 * Basically, the {@link ValueGenerator} inspires from the <a
 * href="http://en.wikipedia.org/wiki/Builder_pattern">Builder Design
 * Pattern</a>, but it restricts the perspective to the generation phase (the
 * last call to a builder, which actually instantiates and returns the built
 * object), thus the naming as generator and not builder. This restriction is
 * due to the fact that a concrete implementation of a {@link ValueGenerator} is
 * not required to take the form of a builder:
 * <ul>
 * <li>it can be an automatic generator, which does not require any manual
 * setting before calling {@link #generate()}, like the {@link RandomGenerator};
 * </li>
 * <li>it can be a generator relying on simple inputs, like a
 * {@link FiniteDiscreteRangeGenerator} which maps a simple index to any custom
 * value;</li>
 * <li>it can be a generator which actually rely on builder-like methods, like
 * the {@link BuilderBasedGenerator};</li>
 * <li>etc.</li>
 * </ul>
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public interface ValueGenerator<Value> {

	/**
	 * 
	 * @return <code>true</code> if {@link #generate()} is guaranteed to provide
	 *         a valid {@link Value}, <code>false</code> otherwise
	 */
	public boolean canGenerate();

	/**
	 * 
	 * @return a {@link Value} ready to use
	 */
	public Value generate();
}
