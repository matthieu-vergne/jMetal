package experimental;

import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;

import experimental.PracticalContext.City;
import experimental.generic.GenericHillClimbing;
import experimental.generic.GenericTSPSolver;
import experimental.specific.SpecificHillClimbing;
import experimental.specific.SpecificTSPSolver;

/**
 * This class focuses on the problem to solve for the {@link PracticalContext}
 * (TSP) and provides an abstract resolution process. Consequently, we give an
 * access to the context through {@link #getContext()}, so that an algorithm can
 * be set up in {@link #getAlgorithm()} by child classes. We additionally
 * require a way to retrieve the path of a solution (
 * {@link #retrievePath(Object)}) so that we can provide the path of the
 * solution returned by the algorithm to the context for display.<br/>
 * <br/>
 * The only aim of this class is to centralize the algorithm-independent stuff
 * from {@link GenericTSPSolver} and {@link SpecificTSPSolver}, which both aims
 * at solving the same problem (TSP) of the {@link PracticalContext}. While
 * {@link GenericTSPSolver} uses a {@link GenericHillClimbing} algorithm, which
 * does not make much assumption and consequently need to get much information
 * but is highly flexible, {@link SpecificTSPSolver} uses a
 * {@link SpecificHillClimbing}, which is dedicated to solving TSP problems and
 * consequently need few information but a significant effort to map the problem
 * concepts to the algorithm concepts.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <S>
 *            the type of solution returned by the algorithm used to solve this
 *            problem. No particular use is made at this level because we focus
 *            on high level functionalities (nothing algorithm-dependent), thus
 *            we don't care about which kind of solution is managed and this
 *            parameter is only there to maintain the type correspondence
 *            between the algorithm, the returned solution and the path
 *            retrieval method {@link #retrievePath(Object)}.
 */
public abstract class TSPSolver<S> {

	/**
	 * The context relating to the problem we want to solve.
	 */
	private final PracticalContext context = new PracticalContext();

	/**
	 * This method just provide an access to the context for child classes.
	 */
	protected PracticalContext getContext() {
		return context;
	}

	/**
	 * This method should be implemented by a concrete solver to return the
	 * configured algorithm, ready to run.
	 */
	protected abstract Algorithm<S> getAlgorithm();

	/**
	 * This method should be implemented by a concrete solver to return the
	 * (problem-specific) path corresponding to the (algorithm-specific)
	 * solution provided in argument. This is mainly used to retrieve the path
	 * of the solution returned by the algorithm, but it could be used to get
	 * the path of the best solution after each round.
	 */
	protected abstract List<City> retrievePath(S solution);

	/**
	 * This is the solving method, where the algorithm is run and the result
	 * displayed.
	 */
	public void solveProblem() {
		Algorithm<S> algorithm = getAlgorithm();
		algorithm.run();
		S result = algorithm.getResult();

		List<City> path = retrievePath(result);
		context.displayResult(path);
	}

}
