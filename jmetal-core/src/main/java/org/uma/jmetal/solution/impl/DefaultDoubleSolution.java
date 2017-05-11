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

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Defines an implementation of a double solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDoubleSolution 
    extends AbstractGenericSolution<Double, DoubleProblem>
    implements DoubleSolution {

  /** Constructor */
  public DefaultDoubleSolution(DoubleProblem problem) {
    this(problem, (BoundedRandomGenerator<Double>) (min, max) -> JMetalRandom.getInstance().nextDouble(min, max)) ;
  }

  /** Constructor */
  public DefaultDoubleSolution(DoubleProblem problem, BoundedRandomGenerator<Double> variableRandomGenerator) {
    this(problem, (BiFunction<DefaultDoubleSolution, Integer, Double>) (s, i) -> variableRandomGenerator.getRandomValue(s.getLowerBound(i), s.getUpperBound(i)));
  }

  /** Constructor */
  public DefaultDoubleSolution(DoubleProblem problem, Function<Integer, Double> variableGenerator) {
    this(problem, (BiFunction<DefaultDoubleSolution, Integer, Double>) (s, i) -> variableGenerator.apply(i));
  }

  /** Constructor */
  private DefaultDoubleSolution(DoubleProblem problem, BiFunction<DefaultDoubleSolution, Integer, Double> variableGenerator) {
    super(problem) ;

    initializeDoubleVariables(variableGenerator);
    initializeObjectiveValues();
  }

  /** Copy constructor */
  public DefaultDoubleSolution(DefaultDoubleSolution solution) {
    super(solution.problem) ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Double getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public Double getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public DefaultDoubleSolution copy() {
    return new DefaultDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeDoubleVariables(BiFunction<DefaultDoubleSolution, Integer, Double> variableGenerator) {
    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = variableGenerator.apply(this, i) ;
      setVariableValue(i, value) ;
    }
  }
}
