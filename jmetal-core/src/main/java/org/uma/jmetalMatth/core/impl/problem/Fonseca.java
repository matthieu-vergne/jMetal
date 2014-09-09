package org.uma.jmetalMatth.core.impl.problem;

import org.uma.jmetalMatth.core.Algorithm;
import org.uma.jmetalMatth.core.Problem;
import org.uma.jmetalMatth.core.Solution;
import org.uma.jmetalMatth.core.impl.problem.Fonseca.FonsecaRepresentation;

public class Fonseca implements Problem<FonsecaRepresentation> {

	public Fonseca() {
		// TODO unused code
		// ArrayList<Double> lowerLimit = new
		// ArrayList<>(getNumberOfVariables()) ;
		// ArrayList<Double> upperLimit = new
		// ArrayList<>(getNumberOfVariables()) ;
		// for (int i = 0; i < getNumberOfVariables(); i++) {
		// lowerLimit.add(-4.0);
		// upperLimit.add(4.0);
		// }
		// setLowerLimit(lowerLimit);
		// setUpperLimit(upperLimit);
	}

	@Override
	public Solution<FonsecaRepresentation, ?> selectBest(
			Solution<FonsecaRepresentation, ?> candidate1,
			Solution<FonsecaRepresentation, ?> candidate2) {
		// evaluate the candidates separately
		Evaluation evaluation1 = new Evaluation(candidate1.getRepresentation());
		Evaluation evaluation2 = new Evaluation(candidate2.getRepresentation());

		// use variables to shorter names
		double obj11 = evaluation1.objective1;
		double obj12 = evaluation1.objective2;
		double obj21 = evaluation2.objective1;
		double obj22 = evaluation2.objective2;

		// return the Pareto-optimal solution, null otherwise
		if (obj11 < obj21 && obj12 < obj22) {
			return candidate1;
		} else if (obj11 > obj21 && obj12 > obj22) {
			return candidate2;
		} else {
			return null;
		}
	}

	/**
	 * In {@link Fonseca}, we consider {@link Solution}s having 3 real
	 * parameters to optimize in a specific way, independently of the
	 * {@link Algorithm} used.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	public static interface FonsecaRepresentation {

		public double getParameter1();

		public double getParameter2();

		public double getParameter3();

	}

	/**
	 * To evaluate a {@link Solution} for {@link Fonseca}, we use this specific
	 * class which stores all the objective function results inferred from the
	 * {@link Solution}. We use a class rather than a a method because several
	 * values should be returned. We could also have a method to make the
	 * calculus and a class to store the final result to return, but the design
	 * choice made in this specific implementation is to merge the process and
	 * the data structure.
	 * 
	 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
	 * 
	 */
	private class Evaluation {
		private final double objective1;
		private final double objective2;

		public Evaluation(FonsecaRepresentation solution) {
			double[] x = new double[3];
			x[0] = (double) solution.getParameter1();
			x[1] = (double) solution.getParameter2();
			x[2] = (double) solution.getParameter3();

			double sum1 = 0.0;
			for (int i = 0; i < x.length; i++) {
				sum1 += StrictMath.pow(
						x[i] - (1.0 / StrictMath.sqrt((double) x.length)), 2.0);
			}
			double exp1 = StrictMath.exp((-1.0) * sum1);

			double sum2 = 0.0;
			for (int i = 0; i < x.length; i++) {
				sum2 += StrictMath.pow(
						x[i] + (1.0 / StrictMath.sqrt((double) x.length)), 2.0);
			}
			double exp2 = StrictMath.exp((-1.0) * sum2);

			this.objective1 = 1 - exp1;
			this.objective2 = 1 - exp2;
		}
	}
}
