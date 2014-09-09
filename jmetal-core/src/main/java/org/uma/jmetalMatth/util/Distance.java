//  Distance.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalMatth.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetalMatth.core.Solution;
import org.uma.jmetalMatth.util.comparator.ObjectiveComparator;
import org.uma.jmetalMatth.util.encoding.CrowdingDistanceEncoding;
import org.uma.jmetalMatth.util.encoding.DecisionVariablesEncoding;
import org.uma.jmetalMatth.util.encoding.ObjectiveBasedEncoding;

/**
 * This class implements some utilities for calculating distances
 */
public class Distance {

	/**
	 * Constructor.
	 */
	public Distance() {
		// do nothing.
	}

	/**
	 * Returns a matrix with distances between solutions in a
	 * <code>SolutionSet</code>.
	 * 
	 * @param solutionSet
	 *            The <code>SolutionSet</code>.
	 * @return a matrix with distances.
	 */
	public <Encoding extends ObjectiveBasedEncoding> double[][] distanceMatrix(
			List<Solution<?, Encoding>> solutionSet) {
		Solution<?, Encoding> solutionI, solutionJ;

		// The matrix of distances
		double[][] distance = new double[solutionSet.size()][solutionSet.size()];
		// -> Calculate the distances
		for (int i = 0; i < solutionSet.size(); i++) {
			distance[i][i] = 0.0;
			solutionI = solutionSet.get(i);
			for (int j = i + 1; j < solutionSet.size(); j++) {
				solutionJ = solutionSet.get(j);
				distance[i][j] = this.distanceBetweenObjectives(solutionI,
						solutionJ);
				distance[j][i] = distance[i][j];
			}
		}

		// Return the matrix of distances
		return distance;
	}

	/**
	 * Returns the minimum distance from a <code>Solution</code> to a
	 * <code>SolutionSet according to the objective values</code>.
	 * 
	 * @param solution
	 *            The <code>Solution</code>.
	 * @param solutionSet
	 *            The <code>SolutionSet</code>.
	 * @return The minimum distance between solutiontype and the set.
	 * @throws JMetalException
	 */
	public <Encoding extends ObjectiveBasedEncoding> double distanceToSolutionSetInObjectiveSpace(
			Solution<?, Encoding> solution,
			List<Solution<?, Encoding>> solutionSet) {
		// At start point the distance is the max
		double distance = Double.MAX_VALUE;

		// found the min distance respect to population
		for (int i = 0; i < solutionSet.size(); i++) {
			double aux = this.distanceBetweenObjectives(solution,
					solutionSet.get(i));
			if (aux < distance) {
				distance = aux;
			}
		}

		// Return the best distance
		return distance;
	}

	/**
	 * Returns the minimum distance from a <code>Solution</code> to a
	 * <code>SolutionSet according to the encoding.variable values</code>.
	 * 
	 * @param solution
	 *            The <code>Solution</code>.
	 * @param solutionSet
	 *            The <code>SolutionSet</code>.
	 * @return The minimum distance between solutiontype and the set.
	 * @throws JMetalException
	 */
	public <Encoding extends DecisionVariablesEncoding> double distanceToSolutionSetInSolutionSpace(
			Solution<?, Encoding> solution,
			List<Solution<?, Encoding>> solutionSet) {
		// At start point the distance is the max
		double distance = Double.MAX_VALUE;

		// found the min distance respect to population
		for (int i = 0; i < solutionSet.size(); i++) {
			double aux = this.distanceBetweenSolutions(solution,
					solutionSet.get(i));
			if (aux < distance) {
				distance = aux;
			}
		}

		// Return the best distance
		return distance;
	}

	/**
	 * Returns the distance between two solutions in the search space.
	 * 
	 * @param solutionI
	 *            The first <code>Solution</code>.
	 * @param solutionJ
	 *            The second <code>Solution</code>.
	 * @return the distance between solutions.
	 * @throws JMetalException
	 */
	public <Encoding extends DecisionVariablesEncoding> double distanceBetweenSolutions(
			Solution<?, Encoding> solutionI, Solution<?, Encoding> solutionJ) {
		double distance = 0.0;
		double diff;
		// Calculate the Euclidean distance
		for (int i = 0; i < solutionI.getEncoding()
				.getNumberOfDecisionVariables(); i++) {
			diff = solutionI.getEncoding().getValue(i)
					- solutionJ.getEncoding().getValue(i);
			distance += Math.pow(diff, 2.0);
		}
		// Return the euclidean distance
		return Math.sqrt(distance);
	}

	/**
	 * Returns the distance between two solutions in objective space.
	 * 
	 * @param solutionI
	 *            The first <code>Solution</code>.
	 * @param solutionJ
	 *            The second <code>Solution</code>.
	 * @return the distance between solutions in objective space.
	 */
	public <Encoding extends ObjectiveBasedEncoding> double distanceBetweenObjectives(
			Solution<?, Encoding> solutionI, Solution<?, Encoding> solutionJ) {
		double diff;
		double distance = 0.0;
		// Calculate the euclidean distance
		for (int nObj = 0; nObj < solutionI.getEncoding()
				.getNumberOfObjectives(); nObj++) {
			diff = solutionI.getEncoding().getObjective(nObj)
					- solutionJ.getEncoding().getObjective(nObj);
			distance += Math.pow(diff, 2.0);
		}

		// Return the euclidean distance
		return Math.sqrt(distance);
	}

	/**
	 * Return the index of the nearest solutiontype in the solutiontype set to a
	 * given solutiontype
	 * 
	 * @param solution
	 * @param solutionSet
	 * @return The index of the nearest solutiontype; -1 if the solutionSet is
	 *         empty
	 */
	public <Encoding extends DecisionVariablesEncoding> int indexToNearestSolutionInSolutionSpace(
			Solution<?, Encoding> solution,
			List<Solution<?, Encoding>> solutionSet) {
		int index = -1;
		double minimumDistance = Double.MAX_VALUE;
		try {
			for (int i = 0; i < solutionSet.size(); i++) {
				double distance = 0;
				distance = this.distanceBetweenSolutions(solution,
						solutionSet.get(i));

				if (distance < minimumDistance) {
					minimumDistance = distance;
					index = i;
				}
			}
		} catch (Exception e) {
			JMetalLogger.logger.log(Level.SEVERE, "Error", e);
		}
		return index;
	}

	/**
	 * Assigns crowding distances to all solutions in a <code>SolutionSet</code>
	 * .
	 * 
	 * @param solutionSet
	 *            The <code>SolutionSet</code>.
	 * @throws JMetalException
	 */

	public <Encoding extends CrowdingDistanceEncoding & ObjectiveBasedEncoding> void crowdingDistanceAssignment(
			Collection<Solution<?, Encoding>> solutionSet) {
		int size = solutionSet.size();

		if (size == 0) {
			return;
		}

		if (size == 1 || size == 2) {
			for (Solution<?, Encoding> solution : solutionSet) {
				solution.getEncoding().setCrowdingDistance(
						Double.POSITIVE_INFINITY);
			}
			return;
		}

		// Use a new SolutionSet to avoid altering the original solutionSet
		List<Solution<?, Encoding>> front = new ArrayList<>(solutionSet);

		for (int i = 0; i < size; i++) {
			front.get(i).getEncoding().setCrowdingDistance(0.0);
		}

		double objetiveMaxn;
		double objetiveMinn;
		double distance;

		int numberOfObjectives = solutionSet.iterator().next().getEncoding()
				.getNumberOfObjectives();

		for (int i = 0; i < numberOfObjectives; i++) {
			// Sort the population by Obj n
			Collections.sort(front, new ObjectiveComparator<Encoding>(i));
			objetiveMinn = front.get(0).getEncoding().getObjective(i);
			objetiveMaxn = front.get(front.size() - 1).getEncoding()
					.getObjective(i);

			// Set de crowding distance
			front.get(0).getEncoding()
					.setCrowdingDistance(Double.POSITIVE_INFINITY);
			front.get(size - 1).getEncoding()
					.setCrowdingDistance(Double.POSITIVE_INFINITY);

			for (int j = 1; j < size - 1; j++) {
				distance = front.get(j + 1).getEncoding().getObjective(i)
						- front.get(j - 1).getEncoding().getObjective(i);
				distance = distance / (objetiveMaxn - objetiveMinn);
				distance += front.get(j).getEncoding().getCrowdingDistance();
				front.get(j).getEncoding().setCrowdingDistance(distance);
			}
		}
	}

	public static <Encoding extends CrowdingDistanceEncoding & ObjectiveBasedEncoding> void crowdingDistance(
			Collection<Solution<?, Encoding>> solutionSet) {
		int size = solutionSet.size();

		if (size == 0) {
			return;
		}

		if (size == 1 || size == 2) {
			for (Solution<?, Encoding> solution : solutionSet) {
				solution.getEncoding().setCrowdingDistance(
						Double.POSITIVE_INFINITY);
			}
			return;
		}

		// Use a new SolutionSet to avoid altering the original solutionSet
		List<Solution<?, Encoding>> front = new ArrayList<>(solutionSet);

		for (int i = 0; i < size; i++) {
			front.get(i).getEncoding().setCrowdingDistance(0.0);
		}

		double objetiveMaxn;
		double objetiveMinn;
		double distance;

		int numberOfObjectives = solutionSet.iterator().next().getEncoding()
				.getNumberOfObjectives();

		for (int i = 0; i < numberOfObjectives; i++) {
			// Sort the population by Obj n
			Collections.sort(front, new ObjectiveComparator<Encoding>(i));
			objetiveMinn = front.get(0).getEncoding().getObjective(i);
			objetiveMaxn = front.get(front.size() - 1).getEncoding()
					.getObjective(i);

			// Set de crowding distance
			front.get(0).getEncoding()
					.setCrowdingDistance(Double.POSITIVE_INFINITY);
			front.get(size - 1).getEncoding()
					.setCrowdingDistance(Double.POSITIVE_INFINITY);

			for (int j = 1; j < size - 1; j++) {
				distance = front.get(j + 1).getEncoding().getObjective(i)
						- front.get(j - 1).getEncoding().getObjective(i);
				distance = distance / (objetiveMaxn - objetiveMinn);
				distance += front.get(j).getEncoding().getCrowdingDistance();
				front.get(j).getEncoding().setCrowdingDistance(distance);
			}
		}
	}
}
