//  Ranking.java
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

package org.uma.jmetalMatth.core.impl.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.uma.jmetalMatth.core.Solution;
import org.uma.jmetalMatth.util.comparator.DominanceComparator;
import org.uma.jmetalMatth.util.comparator.OverallConstraintViolationComparator;
import org.uma.jmetalMatth.util.encoding.ConstrainedEncoding;
import org.uma.jmetalMatth.util.encoding.ObjectiveBasedEncoding;

/**
 * This class implements some facilities for ranking set of solutions. Given a
 * collection of solutions, they are ranked according to scheme proposed in
 * NSGA-II; as an output, a set of subsets are obtained. The subsets are
 * numbered starting from 0 (in NSGA-II, the numbering starts from 1); thus,
 * subset 0 contains the non-dominated solutions, subset 1 contains the
 * non-dominated solutions after removing those belonging to subset 0, and so
 * on.
 */
public class Ranking<Encoding extends ConstrainedEncoding & ObjectiveBasedEncoding> {
	private final Comparator<Solution<?, Encoding>> DOMINANCE_COMPARATOR = new DominanceComparator<Encoding>();
	private final Comparator<Solution<?, Encoding>> CONSTRAINT_VIOLATION_COMPARATOR = new OverallConstraintViolationComparator<Encoding>();

	private final Collection<Solution<?, Encoding>> population;
	private final List<Collection<Solution<?, Encoding>>> rankedSubpopulations;
	private int[] solutionRanking;

	/**
	 * Constructor.
	 * 
	 * @param solutionSet
	 *            The <code>SolutionSet</code> to be ranked.
	 * @throws org.uma.jmetal.util.JMetalException
	 */
	public Ranking(List<Solution<?, Encoding>> solutionSet) {
		this.population = new ArrayList<>(solutionSet);

		// dominateMe[i] contains the number of solutions dominating i
		int[] dominateMe = new int[this.population.size()];

		// solutionRanking stores the ranking of each solution in the solution
		// set
		solutionRanking = new int[solutionSet.size()];

		// front[i] contains the list of individuals belonging to the front i
		// Initialize the fronts
		List<Collection<Integer>> front = new ArrayList<>();
		for (int i = 0; i < this.population.size() + 1; i++) {
			front.add(new LinkedList<Integer>());
		}

		// iDominate[k] contains the list of solutions dominated by k
		// Fast non dominated sorting algorithm
		// Contribution of Guillaume Jacquenot
		List<Collection<Integer>> iDominate = new ArrayList<>();
		for (int i = 0; i < this.population.size(); i++) {
			// Initialize the list of individuals that i dominate and the number
			// of individuals that dominate me
			iDominate.add(new LinkedList<Integer>());
			dominateMe[i] = 0;
		}

		int flagDominate;
		for (int i = 0; i < (this.population.size() - 1); i++) {
			// For all q individuals , calculate if p dominates q or vice versa
			for (int q = i + 1; q < this.population.size(); q++) {
				flagDominate = CONSTRAINT_VIOLATION_COMPARATOR.compare(
						solutionSet.get(i), solutionSet.get(q));
				if (flagDominate == 0) {
					flagDominate = DOMINANCE_COMPARATOR.compare(
							solutionSet.get(i), solutionSet.get(q));
				}
				if (flagDominate == -1) {
					iDominate.get(i).add(q);
					dominateMe[q]++;
				} else if (flagDominate == 1) {
					iDominate.get(q).add(i);
					dominateMe[i]++;
				}
			}
			// If nobody dominates p, p belongs to the first front
		}
		for (int i = 0; i < this.population.size(); i++) {
			if (dominateMe[i] == 0) {
				front.get(0).add(i);
				// solutionSet.get(i).setRank(0);
				solutionRanking[i] = 0;
			}
		}

		// Obtain the rest of fronts
		int i = 0;
		Iterator<Integer> it1, it2; // Iterators
		while (front.get(i).size() != 0) {
			i++;
			it1 = front.get(i - 1).iterator();
			while (it1.hasNext()) {
				it2 = iDominate.get(it1.next()).iterator();
				while (it2.hasNext()) {
					int index = it2.next();
					dominateMe[index]--;
					if (dominateMe[index] == 0) {
						front.get(i).add(index);
						// this.population.get(index).setRank(i);
						solutionRanking[index] = i;
					}
				}
			}
		}

		// rankedSubpopulations = new SolutionSet[i];
		rankedSubpopulations = new ArrayList<Collection<Solution<?, Encoding>>>();
		// 0,1,2,....,i-1 are fronts, then i fronts
		for (int j = 0; j < i; j++) {
			// rankedSubpopulations[j] = new SolutionSet(front[j].size());
			rankedSubpopulations.set(j, new ArrayList<Solution<?, Encoding>>(
					front.get(j).size()));
			it1 = front.get(j).iterator();
			while (it1.hasNext()) {
				// rankedSubpopulations[j].add(solutionSet.get(it1.next()));
				rankedSubpopulations.get(j).add(solutionSet.get(it1.next()));
			}
		}

	}

	/**
	 * Returns a <code>SolutionSet</code> containing the solutions of a given
	 * rank.
	 * 
	 * @param rank
	 *            The rank
	 * @return Object representing the <code>SolutionSet</code>.
	 */
	public Collection<Solution<?, Encoding>> getSubfront(int rank) {
		return rankedSubpopulations.get(rank);
	}

	public int getNumberOfSubfronts() {
		return rankedSubpopulations.size();
	}

	public int getSolutionRanking(int index) {
		return solutionRanking[index];
	}
}
