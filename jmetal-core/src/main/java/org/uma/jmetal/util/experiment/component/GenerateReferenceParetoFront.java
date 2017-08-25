package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class computes a reference Pareto front from a set of files. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * all the obtained fronts of all the algorithms are gathered per problem; then, the dominated solutions
 * are removed and the final result is a file per problem containing the reference Pareto front.
 *
 * By default, the files are stored in a directory called "referenceFront", which is located in the
 * experiment base directory. Each front is named following the scheme "problemName.rf".
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateReferenceParetoFront implements ExperimentComponent{
  private final List<? extends ExperimentAlgorithm<?>> algorithmList;
  private final List<? extends ExperimentProblem<?>> problemList;
  private final String experimentBaseDirectory;
  private final String outputDirectoryName;
  private final String outputParetoFrontFileName;
  private final int independentRuns;
  private final List<String> referenceFrontFileNames;
  
  @Deprecated
  public GenerateReferenceParetoFront(org.uma.jmetal.util.experiment.Experiment<?, ?> experimentConfiguration) {
    this(experimentConfiguration.getAlgorithmList(), experimentConfiguration.getProblemList(), experimentConfiguration.getExperimentBaseDirectory(), experimentConfiguration.getReferenceFrontDirectory(), experimentConfiguration.getOutputParetoFrontFileName(), experimentConfiguration.getIndependentRuns(), cleanRFFN(experimentConfiguration));
  }

  @Deprecated
  private static List<String> cleanRFFN(org.uma.jmetal.util.experiment.Experiment<?, ?> experimentConfiguration) {
    experimentConfiguration.setReferenceFrontFileNames(new LinkedList<>());
    return experimentConfiguration.getReferenceFrontFileNames();
  }

  public GenerateReferenceParetoFront(List<? extends ExperimentAlgorithm<?>> algorithmList, List<? extends ExperimentProblem<?>> problemList, String experimentBaseDirectory, String referenceFrontDirectory, String outputParetoFrontFileName, int independentRuns, List<String> referenceFrontFileNames) {
    this.algorithmList = ExperimentAlgorithm.filterTagDuplicates(algorithmList);
    this.problemList = problemList;
    this.experimentBaseDirectory = experimentBaseDirectory;
    this.outputDirectoryName = referenceFrontDirectory;
    this.outputParetoFrontFileName = outputParetoFrontFileName;
    this.independentRuns = independentRuns;
    this.referenceFrontFileNames = referenceFrontFileNames;
  }

  /**
   * The run() method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    createOutputDirectory(outputDirectoryName) ;

    for (ExperimentProblem<?> problem : problemList) {
      NonDominatedSolutionListArchive<PointSolution> nonDominatedSolutionArchive =
          new NonDominatedSolutionListArchive<PointSolution>() ;

      for (ExperimentAlgorithm<?> algorithm : algorithmList) {
        String problemDirectory = experimentBaseDirectory + "/data/" +
            algorithm.getAlgorithmTag() + "/" + problem.getTag() ;

        for (int i = 0; i < independentRuns; i++) {
          String frontFileName = problemDirectory + "/" + outputParetoFrontFileName +
              i + ".tsv";
          Front front = new ArrayFront(frontFileName) ;
          List<PointSolution> solutionList = FrontUtils.convertFrontToSolutionList(front) ;
          GenericSolutionAttribute<PointSolution, String> solutionAttribute = new GenericSolutionAttribute<PointSolution, String>()  ;

          for (PointSolution solution : solutionList) {
            solutionAttribute.setAttribute(solution, algorithm.getAlgorithmTag());
            nonDominatedSolutionArchive.add(solution) ;
          }
        }
      }
      String referenceSetFileName = outputDirectoryName + "/" + problem.getTag() + ".rf" ;
      referenceFrontFileNames.add(problem.getTag() + ".rf");
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printObjectivesToFile(referenceSetFileName);

      writeFilesWithTheSolutionsContributedByEachAlgorithm(outputDirectoryName, problem.getProblem(),
          nonDominatedSolutionArchive.getSolutionList()) ;
    }
  }

  private File createOutputDirectory(String outputDirectoryName) {
    File outputDirectory ;
    outputDirectory = new File(outputDirectoryName) ;
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdir() ;
      JMetalLogger.logger.info("Creating " + outputDirectoryName + ". Status = " + result);
    }

    return outputDirectory ;
  }

  private void writeFilesWithTheSolutionsContributedByEachAlgorithm(
      String outputDirectoryName, Problem<?> problem,
      List<PointSolution> nonDominatedSolutions) throws IOException {
    GenericSolutionAttribute<PointSolution, String> solutionAttribute = new GenericSolutionAttribute<PointSolution, String>()  ;

    for (ExperimentAlgorithm<?> algorithm : algorithmList) {
      List<PointSolution> solutionsPerAlgorithm = new ArrayList<>() ;
      for (PointSolution solution : nonDominatedSolutions) {
        if (algorithm.getAlgorithmTag().equals(solutionAttribute.getAttribute(solution))) {
          solutionsPerAlgorithm.add(solution) ;
        }
      }

      new SolutionListOutput(solutionsPerAlgorithm)
          .printObjectivesToFile(
              outputDirectoryName + "/" + problem.getName() + "." +
                  algorithm.getAlgorithmTag() + ".rf"
          );
    }
  }
}
