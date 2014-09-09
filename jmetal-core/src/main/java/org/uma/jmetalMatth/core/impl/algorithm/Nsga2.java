package org.uma.jmetalMatth.core.impl.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.uma.jmetalMatth.core.Algorithm;
import org.uma.jmetalMatth.core.Solution;
import org.uma.jmetalMatth.core.SolutionManager;
import org.uma.jmetalMatth.core.impl.algorithm.Nsga2.Nsga2Encoding;
import org.uma.jmetalMatth.core.impl.algorithm.Nsga2.Nsga2Manager;
import org.uma.jmetalMatth.util.Distance;
import org.uma.jmetalMatth.util.Pair;
import org.uma.jmetalMatth.util.comparator.CrowdingComparator;
import org.uma.jmetalMatth.util.encoding.ConstrainedEncoding;
import org.uma.jmetalMatth.util.encoding.CrowdingDistanceEncoding;
import org.uma.jmetalMatth.util.encoding.ObjectiveBasedEncoding;
import org.uma.jmetalMatth.util.encoding.RankedEncoding;

public class Nsga2
		implements
		Algorithm<Nsga2Encoding, Nsga2Manager, Collection<Solution<?, Nsga2Encoding>>> {

	private Distance distance = new Distance();
	private long evaluations = 0;

	@Override
	public Collection<Solution<?, Nsga2Encoding>> execute(Nsga2Manager manager) {
		int populationSize = manager.getPopulationSize();

		// create initial population
		Collection<Solution<?, Nsga2Encoding>> population = createInitialPopulation(manager);

		population = evaluatePopulation(population, manager);

		// Main loop
		while (!stoppingCondition(manager)) {
			Collection<Solution<?, Nsga2Encoding>> offspringPopulation = new ArrayList<>();
			for (int i = 0; i < (populationSize / 2); i++) {
				if (!stoppingCondition(manager)) {
					Pair<Solution<?, Nsga2Encoding>> parents = new Pair<Solution<?, Nsga2Encoding>>(
							manager.selectParent(population),
							manager.selectParent(population));

					Pair<Solution<?, Nsga2Encoding>> offSpring = manager
							.cross(parents);

					manager.mutate(offSpring.getElement1());
					manager.mutate(offSpring.getElement2());

					offspringPopulation.add(offSpring.getElement1());
					offspringPopulation.add(offSpring.getElement2());
				}
			}

			offspringPopulation = evaluatePopulation(offspringPopulation,
					manager);

			List<Solution<?, Nsga2Encoding>> union = new ArrayList<>();
			union.addAll(population);
			union.addAll(offspringPopulation);
			Ranking<Nsga2Encoding> ranking = new Ranking<>(union);
			crowdingDistanceSelection(population, ranking, manager);
		}

		return getNonDominatedSolutions(population);
	}

	protected Collection<Solution<?, Nsga2Encoding>> evaluatePopulation(
			Collection<Solution<?, Nsga2Encoding>> population,
			Nsga2Manager manager) {
		evaluations += population.size();

		return manager.evaluatePopulation(population);
	}

	private Collection<Solution<?, Nsga2Encoding>> getNonDominatedSolutions(
			Collection<Solution<?, Nsga2Encoding>> solutionSet) {
		return new Ranking<Nsga2Encoding>(new ArrayList<>(solutionSet))
				.getSubfront(0);
	}

	private void crowdingDistanceSelection(
			Collection<Solution<?, Nsga2Encoding>> population,
			Ranking<Nsga2Encoding> ranking, Nsga2Manager manager) {
		population.clear();
		int rankingIndex = 0;
		while (populationIsNotFull(population, manager)) {
			if (subfrontFillsIntoThePopulation(ranking, rankingIndex, manager,
					population)) {
				addRankedSolutionsToPopulation(ranking, rankingIndex,
						population);
				rankingIndex++;
			} else {
				computeCrowdingDistance(ranking, rankingIndex, manager);
				addLastRankedSolutions(ranking, rankingIndex, manager,
						population);
			}
		}
	}

	private void addLastRankedSolutions(Ranking<Nsga2Encoding> ranking,
			int rank, Nsga2Manager manager,
			Collection<Solution<?, Nsga2Encoding>> population) {
		List<Solution<?, Nsga2Encoding>> currentRankedFront = new LinkedList<>(
				ranking.getSubfront(rank));

		Collections.sort(currentRankedFront,
				new CrowdingComparator<Nsga2Encoding>());

		int i = 0;
		while (population.size() < manager.getPopulationSize()) {
			population.add(currentRankedFront.get(i));
			i++;
		}
	}

	private void computeCrowdingDistance(Ranking<Nsga2Encoding> ranking,
			int rank, Nsga2Manager manager) {
		Collection<Solution<?, Nsga2Encoding>> currentRankedFront = ranking
				.getSubfront(rank);
		distance.crowdingDistanceAssignment(currentRankedFront);
	}

	private void addRankedSolutionsToPopulation(Ranking<Nsga2Encoding> ranking,
			int rank, Collection<Solution<?, Nsga2Encoding>> population) {
		population.addAll(ranking.getSubfront(rank));
	}

	private boolean subfrontFillsIntoThePopulation(
			Ranking<Nsga2Encoding> ranking, int rank, Nsga2Manager manager,
			Collection<Solution<?, Nsga2Encoding>> population) {
		return ranking.getSubfront(rank).size() < (manager.getPopulationSize() - population
				.size());
	}

	private boolean populationIsNotFull(
			Collection<Solution<?, Nsga2Encoding>> population,
			Nsga2Manager manager) {
		return population.size() < manager.getPopulationSize();
	}

	private Collection<Solution<?, Nsga2Encoding>> createInitialPopulation(
			Nsga2Manager manager) {
		Collection<Solution<?, Nsga2Encoding>> population = new ArrayList<>();
		for (int i = 0; i < manager.getPopulationSize(); i++) {
			population.add(manager.generateRandomSolution());
		}
		return population;
	}

	protected boolean stoppingCondition(Nsga2Manager manager) {
		return evaluations >= manager.getMaxEvaluations();
	}

	public static interface Nsga2Encoding extends ConstrainedEncoding,
			ObjectiveBasedEncoding, CrowdingDistanceEncoding, RankedEncoding {
	}

	public static interface Nsga2Manager extends
			SolutionManager<Nsga2Encoding, Solution<?, Nsga2Encoding>> {

		public int getPopulationSize();

		public long getMaxEvaluations();

		public Collection<Solution<?, Nsga2Encoding>> evaluatePopulation(
				Collection<Solution<?, Nsga2Encoding>> population);

		public void mutate(Solution<?, Nsga2Encoding> element1);

		public Pair<Solution<?, Nsga2Encoding>> cross(
				Pair<Solution<?, Nsga2Encoding>> parents);

		public Solution<?, Nsga2Encoding> selectParent(
				Collection<Solution<?, Nsga2Encoding>> population);

		public Solution<?, Nsga2Encoding> generateRandomSolution();

	}
}
