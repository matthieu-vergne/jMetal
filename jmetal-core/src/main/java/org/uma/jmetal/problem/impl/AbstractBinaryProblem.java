package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem extends AbstractGenericProblem<BinarySolution>
  implements BinaryProblem {

  private RandomGenerator<Boolean> randomBitGenerator = () -> JMetalRandom.getInstance().nextDouble() > 0.5;

  protected abstract int getBitsPerVariable(int index) ;

  @Override
  public int getNumberOfBits(int index) {
    return getBitsPerVariable(index) ;
  }
  
  @Override
  public int getTotalNumberOfBits() {
  	int count = 0 ;
  	for (int i = 0; i < this.getNumberOfVariables(); i++) {
  		count += this.getBitsPerVariable(i) ;
  	}
  	
  	return count ;
  }
  
  public RandomGenerator<Boolean> getRandomBitGenerator() {
    return randomBitGenerator;
  }
  
  public void setRandomBitGenerator(RandomGenerator<Boolean> randomBitGenerator) {
    this.randomBitGenerator = randomBitGenerator;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(this, randomBitGenerator)  ;
  }
}
