package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractDoubleProblem extends AbstractGenericProblem<DoubleSolution>
  implements DoubleProblem {

  private List<Double> lowerLimit ;
  private List<Double> upperLimit ;
  private BoundedRandomGenerator<Double> variableRandomGenerator = (min, max) -> JMetalRandom.getInstance().nextDouble(min, max) ;

  /* Getters */
	@Override
	public Double getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Double getLowerBound(int index) {
		return lowerLimit.get(index);
	}
	
	public BoundedRandomGenerator<Double> getVariableRandomGenerator() {
		return variableRandomGenerator;
	}

  /* Setters */
  protected void setLowerLimit(List<Double> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<Double> upperLimit) {
    this.upperLimit = upperLimit;
  }
  
  public void setVariableRandomGenerator(BoundedRandomGenerator<Double> variableRandomGenerator) {
    this.variableRandomGenerator = variableRandomGenerator;
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(this, variableRandomGenerator)  ;
  }
}
