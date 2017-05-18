package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractIntegerProblem extends AbstractGenericProblem<IntegerSolution>
  implements IntegerProblem {

  private List<Integer> lowerLimit ;
  private List<Integer> upperLimit ;
  @SuppressWarnings("deprecation")
  private BoundedRandomGenerator<Integer> variableRandomGenerator = (min, max) -> JMetalRandom.getInstance().nextInt(min, max) ;

  /* Getters */
	@Override
	public Integer getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Integer getLowerBound(int index) {
		return lowerLimit.get(index);
	}
	
	public BoundedRandomGenerator<Integer> getVariableRandomGenerator() {
		return variableRandomGenerator;
	}

  /* Setters */
  protected void setLowerLimit(List<Integer> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<Integer> upperLimit) {
    this.upperLimit = upperLimit;
  }
  
  public void setVariableRandomGenerator(BoundedRandomGenerator<Integer> variableRandomGenerator) {
    this.variableRandomGenerator = variableRandomGenerator;
  }

  @Override
  public IntegerSolution createSolution() {
    return new DefaultIntegerSolution(this, variableRandomGenerator) ;
  }

}
