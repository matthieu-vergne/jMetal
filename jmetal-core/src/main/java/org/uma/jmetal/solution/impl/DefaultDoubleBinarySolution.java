//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleBinaryProblem;
import org.uma.jmetal.solution.DoubleBinarySolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.BitSet;
import java.util.HashMap;

/**
 * Description:
 *  - this solution contains an array of double value + a binary string
 *  - getNumberOfVariables() returns the number of double values + 1 (the string)
 *  - getNumberOfDoubleVariables() returns the number of double values
 *  - getNumberOfVariables() = getNumberOfDoubleVariables() + 1
 *  - the bitset is the last variable
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleBinarySolution
    extends AbstractGenericSolution<Object, DoubleBinaryProblem<?>>
    implements DoubleBinarySolution {
  private int numberOfDoubleVariables ;

  /** Constructor */
  public DefaultDoubleBinarySolution(DoubleBinaryProblem<?> problem) {
	  this(problem, (min, max) -> JMetalRandom.getInstance().nextDouble(min, max), () -> JMetalRandom.getInstance().nextDouble() > 0.5);
  }

  /** Constructor */
  public DefaultDoubleBinarySolution(DoubleBinaryProblem<?> problem, RandomGenerator<Double> randomGenerator) {
	  this(problem, BoundedRandomGenerator.bound(randomGenerator), () -> randomGenerator.getRandomValue() > 0.5);
  }

  /** Constructor */
  public DefaultDoubleBinarySolution(DoubleBinaryProblem<?> problem, BoundedRandomGenerator<Double> randomDoubleGenerator, RandomGenerator<Boolean> randomBitGenerator) {
    super(problem) ;

    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;

    initializeDoubleVariables(randomDoubleGenerator);
    initializeBitSet(randomBitGenerator) ;
    initializeObjectiveValues();
  }

  /** Copy constructor */
  public DefaultDoubleBinarySolution(DefaultDoubleBinarySolution solution) {
    super(solution.problem) ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    copyDoubleVariables(solution);
    copyBitSet(solution);

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  private void initializeDoubleVariables(BoundedRandomGenerator<Double> randomDoubleGenerator) {
    for (int i = 0 ; i < numberOfDoubleVariables; i++) {
      Double value = randomDoubleGenerator.getRandomValue(getLowerBound(i), getUpperBound(i)) ;
      //variables.add(value) ;
      setVariableValue(i, value);
    }
  }

  private void initializeBitSet(RandomGenerator<Boolean> randomBitGenerator) {
    BitSet bitset = createNewBitSet(problem.getNumberOfBits(), randomBitGenerator) ;
    setVariableValue(numberOfDoubleVariables, bitset);
  }

  private void copyDoubleVariables(DefaultDoubleBinarySolution solution) {
    for (int i = 0 ; i < numberOfDoubleVariables; i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }
  }

  private void copyBitSet(DefaultDoubleBinarySolution solution) {
    BitSet bitset = (BitSet)solution.getVariableValue(solution.getNumberOfVariables()-1) ;
    setVariableValue(numberOfDoubleVariables, bitset);
  }

  @Override
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  @Override
  public Double getUpperBound(int index) {
    return (Double)problem.getUpperBound(index);
  }

  @Override
  public int getNumberOfBits() {
    return problem.getNumberOfBits();
  }

  @Override
  public Double getLowerBound(int index) {
    return (Double)problem.getLowerBound(index) ;
  }

  @Override
  public DefaultDoubleBinarySolution copy() {
    return new DefaultDoubleBinarySolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }

  private BitSet createNewBitSet(int numberOfBits, RandomGenerator<Boolean> randomBitGenerator) {
    BitSet bitSet = new BitSet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      bitSet.set(i, randomBitGenerator.getRandomValue());
    }
    return bitSet ;
  }
}
