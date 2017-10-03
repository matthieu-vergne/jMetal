package org.uma.jmetal.util.experiment.util;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Class used to add a tag field to a problem.
 *
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public class ExperimentProblem<S extends Solution<?>> {
  private Problem<S> problem ;
  private String tag ;

  public ExperimentProblem(Problem<S> problem, String tag) {
    this.problem = problem;
    this.tag = tag;
  }

  public ExperimentProblem(Problem<S> problem) {
    this.problem = problem;
    this.tag = problem.getName() ;
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public String getTag() {
    return tag ;
  }

  /**
   * The list of problems contain a problem instance per algorithm. This is not convenient for
   * calculating statistical data, because a same problem will appear many times.
   * This method remove duplicated problems and leave only an instance of each one.
   */
  public static <E extends ExperimentProblem<?>> List<E> filterTagDuplicates(List<E> problems) {
    List<E> problemList = new ArrayList<>() ;
    List<String> problemTagList = new ArrayList<>() ;

    for (E problem : problems) {
      if (!problemTagList.contains(problem.getTag())) {
        problemList.add(problem) ;
        problemTagList.add(problem.getTag()) ;
      }
    }
    
    return problemList;
  }
}
