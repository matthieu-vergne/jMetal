package experimental.generic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.Operator;

import experimental.AttributeReader;
import experimental.ComputedAttributeReader;
import experimental.PracticalContext;
import experimental.PracticalContext.City;
import experimental.SolutionStore;
import experimental.SolutionStore.SolutionAttributeControler;
import experimental.TSPSolver;

/**
 * This {@link TSPSolver} exploits the {@link GenericHillClimbing} algorithm to
 * solve the TSP problem of the {@link PracticalContext}. Because this algorithm
 * is generic, it is highly adaptable to our own needs, including the type of
 * solution to use. However, we have to tell it how to do the different
 * operations on the solutions.<br/>
 * <br/>
 * Because we can choose which type of solution to use, we exploit the
 * {@link SolutionStore} to store the attributes of each solution and the
 * {@link SolutionAttributeControler} to control these attributes. In
 * particular, we design our solutions so that each of them correspond to a path
 * (path attribute) and has a given length (length attribute).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class GenericTSPSolver extends TSPSolver<SolutionStore> {

	/**
	 * We store the path attribute as a field so we can use it on
	 * {@link #retrievePath(SolutionStore)}.
	 */
	private SolutionAttributeControler<List<City>> pathAttribute;

	/**
	 * This method setup the algorithm such that it can be run in the
	 * {@link #solveProblem()} method. Because we use the generic algorithm
	 * {@link GenericHillClimbing}, we can adapt the algorithm to our own needs
	 * but it requires to provide advanced knowledge on the problem, in
	 * particular regarding the random generator and the mutator.
	 */
	@Override
	protected Algorithm<SolutionStore> getAlgorithm() {
		/*
		 * Setup the number of rounds to execute. The value is decided
		 * empirically to have a high probability to get a correct result. A
		 * more advanced way would be to use Measures to get the best solution
		 * found at each round and stop when a threshold has been met or when no
		 * improvements has been made after a given number of rounds.
		 */
		final int rounds = 100;

		/*
		 * Setup the attributes to exploit. Because we can choose the solution
		 * we want, we exploit the SolutionStore, which allows us to use the
		 * SolutionAttributeControler for the parameters of the solution. This
		 * way, we can just use the concepts already provided by the problem,
		 * like the City organized as a List to have a path.
		 */
		pathAttribute = new SolutionAttributeControler<List<City>>();

		/*
		 * Another type of attribute is related to the evaluation of the
		 * solution. Such values are by definition read-only attributes, so we
		 * implement an AttributeReader, and more precisely a
		 * ComputedAttributeReader which allow us to focus on the value
		 * computation while it provides by default the necessary stuff to store
		 * the value. Notice that this AttributeReader does not exploit the
		 * SolutionStore, at the opposite of the SolutionAttributeControler, but
		 * such an implementation could be made easily. We use a different one
		 * here to show that exploiting the SolutionStore is not the only way to
		 * deal with the storage of attributes.
		 */
		AttributeReader<SolutionStore, Double> lengthAttribute = new ComputedAttributeReader<SolutionStore, Double>() {

			@Override
			public Double compute(SolutionStore solution) {
				List<City> path = pathAttribute.getAttribute(solution);
				return getContext().calculatePathLength(path);
			}

		};

		/*
		 * The random generator allows to generate a random individual, thus a
		 * random path between the cities. We need to make this operator ourself
		 * because the generic algorithm does not make any assumption on the
		 * attributes of the solutions (it is oblivious to the type of solution
		 * and consider only a cost attribute), so it cannot know how to access
		 * nor set them. This is a typical limitation of generic algorithms:
		 * being generic implies to make less assumptions, thus requiring more
		 * information from the user. The corresponding advantage is the degree
		 * of flexibility it provides to the user.
		 */
		final Operator<Void, SolutionStore> randomGenerator = new Operator<Void, SolutionStore>() {

			@Override
			public SolutionStore execute(Void source) {
				SolutionStore solution = new SolutionStore();
				List<City> randomPath = new LinkedList<>(getContext()
						.getCities());
				Collections.shuffle(randomPath);
				pathAttribute.setAttribute(solution, randomPath);
				return solution;
			}
		};

		/*
		 * Similarly to the random generator, the mutator must be provided by
		 * the user because the algorithm does not have the required information
		 * to know which attribute to access nor how to change it. The mutation
		 * implemented here takes a random city in the path and re-place it in a
		 * random place.
		 */
		final Operator<SolutionStore, SolutionStore> mutator = new Operator<SolutionStore, SolutionStore>() {

			private final Random rand = new Random();

			@Override
			public SolutionStore execute(SolutionStore original) {
				List<City> mutantPath = new LinkedList<>(
						pathAttribute.getAttribute(original));
				int index1 = rand.nextInt(mutantPath.size());
				int index2 = rand.nextInt(mutantPath.size());
				City removed = mutantPath.remove(index1);
				mutantPath.add(index2, removed);

				SolutionStore mutant = new SolutionStore();
				pathAttribute.setAttribute(mutant, mutantPath);
				return mutant;
			}
		};

		/*
		 * Finally, the algorithm can be instantiated with the corresponding
		 * parameters.
		 */
		return new GenericHillClimbing<SolutionStore>(rounds, lengthAttribute,
				randomGenerator, mutator);
	}

	/**
	 * This method is required by the {@link TSPSolver} to retrieve the path
	 * represented by the solution returned by the algorithm. Because we
	 * implemented a path attribute, we can directly use it to retrieve the path
	 * of the solution given in argument.
	 */
	@Override
	protected List<City> retrievePath(SolutionStore solution) {
		return pathAttribute.getAttribute(solution);
	}

	/**
	 * Main method to run this solver and display the result.
	 */
	public static void main(String[] args) {
		new GenericTSPSolver().solveProblem();
	}
}
