package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.io.PrintStream;
import java.util.*;

/**
 * This class implements some facilities for ranking set of solutions. Given a
 * collection of solutions, they are ranked according to scheme proposed in
 * NSGA-II; as an output, a set of subsets are obtained. The subsets are
 * numbered starting from 0 (in NSGA-II, the numbering starts from 1); thus,
 * subset 0 contains the non-dominated solutions, subset 1 contains the
 * non-dominated solutions after removing those belonging to subset 0, and so
 * on.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class DominanceRanking<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
    implements Ranking<S> {

  private static final Comparator<Solution<?>> DOMINANCE_COMPARATOR = new DominanceComparator<Solution<?>>();
  private static final Comparator<Solution<?>> CONSTRAINT_VIOLATION_COMPARATOR = new OverallConstraintViolationComparator<Solution<?>>();

  private List<List<S>> rankedSubPopulations;

  /**
   * Constructor
   */
  public DominanceRanking() {
    rankedSubPopulations = new ArrayList<>();
  }

  public DominanceRanking(Object id) {
    super(id);
    rankedSubPopulations = new ArrayList<>();
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionSet) {
    if (solutionSet.isEmpty()) {
      rankedSubPopulations = new LinkedList<>();
    } else {
      Comparator<S> fullComparator = (s1, s2) -> {
        int flagDominate = CONSTRAINT_VIOLATION_COMPARATOR.compare(s1, s2);
        if (flagDominate == 0) {
          return DOMINANCE_COMPARATOR.compare(s1, s2);
        } else {
          return flagDominate;
        }
      };

      Map<S, Integer> arbitrarySort = new HashMap<>(solutionSet.size());
      solutionSet.forEach(s -> arbitrarySort.put(s, arbitrarySort.size()));
      Comparator<S> totalComparator = (s1, s2) -> {
        int flagDominate = fullComparator.compare(s1, s2);
        if (flagDominate == 0) {
          flagDominate = Integer.compare(arbitrarySort.get(s1), arbitrarySort.get(s2));
        }
        String operator = flagDominate == 1 ? ">" : flagDominate == -1 ? "<" : "=";
        PrintStream writer = (PrintStream) s1.getAttribute("writer");
        writer.println("REL:"+s1.getAttribute("name") + operator + s2.getAttribute("name"));
        return flagDominate;
      };

      List<S> sortedPopulation = new LinkedList<>(solutionSet);// TODO ArrayList?
      sortedPopulation.sort(totalComparator);
      List<List<S>> fronts = splitIntoFronts(sortedPopulation, fullComparator);
      setSolutionAttributes(fronts);

      rankedSubPopulations = fronts;
    }

    return this;
  }

  private List<List<S>> splitIntoFronts(List<S> sortedPopulation, Comparator<S> fullComparator) {
    List<List<S>> fronts = new LinkedList<>();
    int referenceIndex = 0;
    for (int solutionIndex = 0; solutionIndex < sortedPopulation.size(); solutionIndex++) {
      S ref = sortedPopulation.get(referenceIndex);
      S sol = sortedPopulation.get(solutionIndex);
      if (fullComparator.compare(ref, sol) == 0) {
        // Still in the same front
      } else {
        List<S> front = sortedPopulation.subList(referenceIndex, solutionIndex);
        fronts.add(front);
        referenceIndex = solutionIndex;
      }
    }
    fronts.add(sortedPopulation.subList(referenceIndex, sortedPopulation.size()));
    return fronts;
  }

  private void setSolutionAttributes(List<List<S>> fronts) {
    Object id = getAttributeIdentifier();
    for (int frontIndex = 0; frontIndex < fronts.size(); frontIndex++) {
      List<S> front = fronts.get(frontIndex);
      for (S solution : front) {
        solution.setAttribute(id, frontIndex);
      }
    }
  }

  @Override
  public List<S> getSubfront(int rank) {
    if (rank >= rankedSubPopulations.size()) {
      throw new JMetalException("Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() - 1));
    }
    return rankedSubPopulations.get(rank);
  }

  @Override
  public int getNumberOfSubfronts() {
    return rankedSubPopulations.size();
  }
}
