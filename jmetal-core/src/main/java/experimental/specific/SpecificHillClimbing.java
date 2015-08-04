package experimental.specific;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.algorithm.Algorithm;

import experimental.generic.GenericHillClimbing;
import experimental.specific.SpecificHillClimbing.Path;

/**
 * This Hill Climbing implementation is a specific one in the sense that it is
 * intended to solve a specific kind of problem: the TSP. Thus, while its
 * {@link #run()} method is highly similar to the one of
 * {@link GenericHillClimbing}, it introduces concepts like {@link Location} and
 * {@link Path} to identify the locations to pass through, and it already
 * integrates the relevant operators to generate random solutions (
 * {@link #createRandom()} and mutants ({@link #createMutant(Path)}). These
 * assumptions allows to reduce significantly the parameters of the algorithm,
 * which needs only the number of rounds and the actual locations to use.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
@SuppressWarnings("serial")
public class SpecificHillClimbing implements Algorithm<Path> {

	/**
	 * A {@link Location} corresponds to the city or any other place through
	 * which the traveling salesman should travel. Notice that only the
	 * coordinate of the {@link Location} are relevant here, so these are the
	 * only information required by this algorithm-specific concept.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	public static interface Location {
		public double getX();

		public double getY();
	}

	/**
	 * A {@link Path} corresponds to a sequence of {@link Location}s the
	 * traveling salesman should pass through. This is the concept introduced by
	 * the algorithm for the solution: the algorithm generates {@link Path}s and
	 * returns the best found at the end depending on the cost function provided
	 * by the user.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	public class Path extends LinkedList<Location> {

		// Constructor to facilitates instantiations
		public Path(List<Location> locations) {
			addAll(locations);
		}
	}

	/**
	 * The number of iterations to execute before to stop.
	 */
	private final int rounds;
	/**
	 * The list of {@link Location}s to consider to build {@link Path}s.
	 */
	private final List<Location> locations;
	/**
	 * The best {@link Path} found so far.
	 */
	private Path bestFound;

	/**
	 * The constructor simply obtains and stores the parameters of the
	 * algorithm.
	 */
	public SpecificHillClimbing(int rounds, Collection<Location> locations) {
		this.rounds = rounds;
		this.locations = new LinkedList<>(locations);
	}

	/**
	 * The Hill Climbing process, thus it is (almost) a copy-paste of the
	 * {@link GenericHillClimbing#run()}. The only differences are that random
	 * and mutant generators as well as cost evaluations are replaced by methods
	 * which are implemented within the algorithm.
	 */
	@Override
	public void run() {
		bestFound = createRandom();

		for (int i = 1; i < rounds; i++) {
			Path mutant = createMutant(bestFound);

			Double lengthBest = evalute(bestFound);
			Double lengthMutant = evalute(mutant);
			bestFound = lengthBest > lengthMutant ? mutant : bestFound;
		}
	}

	/**
	 * The random generator.
	 */
	private Path createRandom() {
		Path randomPath = new Path(locations);
		Collections.shuffle(randomPath);
		return randomPath;
	}

	/**
	 * The mutant generator.
	 */
	private Path createMutant(Path original) {
		Path mutant = new Path(original);

		Random rand = new Random();
		int index1 = rand.nextInt(mutant.size());
		int index2 = rand.nextInt(mutant.size());
		Location removed = mutant.remove(index1);
		mutant.add(index2, removed);

		return mutant;
	}

	/**
	 * The cost evaluation.
	 */
	private Double evalute(Path path) {
		double length = 0;
		Location previous = null;
		for (Location next : path) {
			if (previous == null) {
				// nothing to compute yet
			} else {
				double dx = next.getX() - previous.getX();
				double dy = next.getY() - previous.getY();
				length += Math.sqrt(dx * dx + dy * dy);
			}
			previous = next;
		}
		return length;
	}

	/**
	 * The best {@link Path} found so far.
	 */
	@Override
	public Path getResult() {
		return bestFound;
	}

}
