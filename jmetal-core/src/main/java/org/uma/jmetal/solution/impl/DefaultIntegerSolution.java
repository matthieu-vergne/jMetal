package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of an integer solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerSolution
    extends AbstractGenericSolution<Integer, IntegerProblem>
    implements IntegerSolution {

  /** Constructor */
  public DefaultIntegerSolution(IntegerProblem problem) {
    super(problem) ;

    initializeIntegerVariables(JMetalRandom.getInstance());
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.problem, solution) ;
  }

  @Override
  public Integer getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public Integer getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeIntegerVariables(JMetalRandom randomGenerator) {
    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = randomGenerator.nextInt(getLowerBound(i), getUpperBound(i));
      setVariableValue(i, value) ;
    }
  }
}
