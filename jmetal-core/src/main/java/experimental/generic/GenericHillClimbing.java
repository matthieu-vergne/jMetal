package experimental.generic;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.Operator;

import experimental.AttributeReader;

/**
 * This Hill Climbing implementation is a generic one, meaning that it
 * implements the minimal process and relies on the user to provide relevant
 * operators. Due to that, most of the code is in the {@link #run()} method. The
 * full process is the following:
 * <ol>
 * <li>create and store a random individual at the first iteration</li>
 * <li>for each next iteration:</li>
 * <ol>
 * <li>create a mutant based on the stored solution</li>
 * <li>compare both and store the best one</li>
 * </ol>
 * </ol> <br/>
 * <br/>
 * Consequently, the algorithm requires to provide a generator to create the
 * random individual and a mutator to generate the mutants. The comparison is
 * made by using a cost function also provided as parameter. Because it is a
 * cost function, the algorithm try to minimize it by selecting the best mutant
 * at each round. The number of rounds is itself provided as a parameter.<br/>
 * <br/>
 * Because all the individual-specific operations are delegated to operators
 * provided in parameters, the actual implementation of a solution is not
 * important for this {@link GenericHillClimbing}. Consequently, the type of
 * solution is let free (parameter {@link Solution}) and the user can exploit
 * what he wants depending on his own needs.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
@SuppressWarnings("serial")
public class GenericHillClimbing<Solution> implements Algorithm<Solution> {

	/**
	 * The number of iterations to execute before to stop.
	 */
	private final int rounds;
	/**
	 * The attribute which stores the cost evaluation of the individuals.
	 */
	private final AttributeReader<Solution, Double> costAttribute;
	/**
	 * The operator which generates random individuals. Void is used to type the
	 * input, meaning that no input is required, but the output should be a
	 * {@link Solution}.
	 */
	private Operator<Void, Solution> randomGenerator;
	/**
	 * The operator which generates a mutant based on an original individual.
	 * Thus, it takes a {@link Solution} and returns another {@link Solution}.
	 */
	private Operator<Solution, Solution> mutator;
	/**
	 * The best {@link Solution} found so far.
	 */
	private Solution bestFound;

	/**
	 * The constructor just receive and store the parameters to use in
	 * {@link #run()}.
	 */
	public GenericHillClimbing(int rounds,
			AttributeReader<Solution, Double> costAttribute,
			Operator<Void, Solution> randomGenerator,
			Operator<Solution, Solution> mutator) {
		this.costAttribute = costAttribute;
		this.rounds = rounds;
		this.randomGenerator = randomGenerator;
		this.mutator = mutator;
	}

	/**
	 * The execution method which implements the Hill Climbing process.
	 */
	@Override
	public void run() {
		bestFound = randomGenerator.execute(null);

		for (int i = 1; i < rounds; i++) {
			Solution mutant = mutator.execute(bestFound);

			Double costBest = costAttribute.getAttribute(bestFound);
			Double costMutant = costAttribute.getAttribute(mutant);
			bestFound = costBest > costMutant ? mutant : bestFound;
		}
	}

	/**
	 * The best individual found so far.
	 */
	@Override
	public Solution getResult() {
		return bestFound;
	}

}
