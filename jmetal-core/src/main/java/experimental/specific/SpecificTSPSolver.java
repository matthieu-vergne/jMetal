package experimental.specific;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.Algorithm;

import experimental.PracticalContext;
import experimental.PracticalContext.City;
import experimental.TSPSolver;
import experimental.generic.GenericTSPSolver;
import experimental.specific.SpecificHillClimbing.Location;
import experimental.specific.SpecificHillClimbing.Path;

/**
 * At the opposite of the {@link GenericTSPSolver}, this {@link TSPSolver}
 * relies on the {@link SpecificHillClimbing} algorithm to solve the TSP problem
 * of the {@link PracticalContext}. Because of that, there is significantly less
 * effort in designing parameters for the algorithm, leading to a
 * {@link #getAlgorithm()} which is significantly smaller than the one from
 * {@link GenericTSPSolver}. However, it implies to map the concepts exploited
 * by the algorithm ({@link Location}) to the concepts provided by the
 * {@link PracticalContext} ({@link City}).<br/>
 * <br/>
 * In a completely experimental context, where the algorithm designer is
 * evaluating his own algorithm, such a mapping would be unnecessary because he
 * could reuse the concepts of his algorithm to build experimental contexts.
 * However, this mapping effort makes sense for real contexts, where the
 * algorithm is introduced into a situation where concepts are already
 * implemented and exploited. In such a situation, the problem-algorithm mapping
 * effort come as an additional effort and reduces the gain introduced by the
 * simplified parameters.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class SpecificTSPSolver extends TSPSolver<Path> {

	/**
	 * Because the algorithm introduces its own {@link Location} concept, we
	 * cannot reuse the {@link City} concept provided by the
	 * {@link PracticalContext}. To make the mapping, we use a proxy pattern,
	 * which encloses a {@link City} instance into a {@link Location} instance.
	 * This is possible because {@link Location} is an interface. In the case it
	 * would have been a proper class with {@link Double} fields, we could have
	 * needed an additional {@link Map} to store the relation between each
	 * {@link City} and {@link Location}. More generally, different mapping
	 * techniques can be used depending on how the concepts are provided by the
	 * context/algorithm.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	private class CityLocation implements Location {
		private final City city;

		public CityLocation(City city) {
			this.city = city;
		}

		public City getCity() {
			return city;
		}

		@Override
		public double getX() {
			return city.getX();
		}

		@Override
		public double getY() {
			return city.getY();
		}
	}

	/**
	 * Method which setup the algorithm. Compared to {@link GenericTSPSolver},
	 * it is significantly reduced because most of the required assumptions are
	 * already implemented in the algorithm used.
	 */
	@Override
	protected Algorithm<Path> getAlgorithm() {
		Collection<Location> locations = new LinkedList<>();
		for (City city : getContext().getCities()) {
			Location location = new CityLocation(city);
			locations.add(location);
		}

		return new SpecificHillClimbing(100, locations);
	}

	/**
	 * This method is required by the {@link TSPSolver} to retrieve the path
	 * represented by the solution returned by the algorithm. Because the
	 * algorithm uses its own {@link Location} instances, we have to map them to
	 * the {@link City} instances to retrieve the context-specific paths.
	 */
	@Override
	protected List<City> retrievePath(Path solution) {
		List<City> cities = new LinkedList<>();
		for (Location location : solution) {
			City city = ((CityLocation) location).getCity();
			cities.add(city);
		}
		return cities;
	}

	/**
	 * Main method to run this solver and display the result.
	 */
	public static void main(String[] args) {
		new SpecificTSPSolver().solveProblem();
	}
}
