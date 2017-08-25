package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;

import java.io.File;
import java.util.List;

/**
 * This class executes the algorithms the have been configured with a instance of class
 * {@link Experiment}. Java 8 parallel streams are used to run the algorithms in parallel.
 *
 * The result of the execution is a pair of files FUNrunId.tsv and VARrunID.tsv per experiment,
 * which are stored in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/algorithmName/problemName.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExecuteAlgorithms<S extends Solution<?>, Result> implements ExperimentComponent {
  private final List<ExperimentAlgorithm<S>> algorithmList;
  private final int independentRuns;
  private final int numberOfCores;
  private final String experimentBaseDirectory;

  /** Constructor */
  @Deprecated
  public ExecuteAlgorithms(org.uma.jmetal.util.experiment.Experiment<S, Result> configuration) {
    this(configuration.getAlgorithmList(), configuration.getIndependentRuns(), configuration.getNumberOfCores(), configuration.getExperimentBaseDirectory());
  }

  public ExecuteAlgorithms(List<ExperimentAlgorithm<S>> algorithmList, int independentRuns, int numberOfCores, String experimentBaseDirectory) {
    this.algorithmList = algorithmList;
    this.independentRuns = independentRuns;
    this.numberOfCores = numberOfCores;
    this.experimentBaseDirectory = experimentBaseDirectory;
  }

  @Override
  public void run() {
    JMetalLogger.logger.info("ExecuteAlgorithms: Preparing output directory");
    prepareOutputDirectory() ;

    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism",
            "" + numberOfCores);

    for (int i = 0; i < independentRuns; i++) {
      final int id = i ;

      algorithmList.parallelStream()
              .forEach(algorithm -> algorithm.runAlgorithm(id, experimentBaseDirectory)) ;
    }
  }



  private void prepareOutputDirectory() {
    if (experimentDirectoryDoesNotExist()) {
      createExperimentDirectory() ;
    }
  }

  private boolean experimentDirectoryDoesNotExist() {
    boolean result;
    File experimentDirectory;

    experimentDirectory = new File(experimentBaseDirectory);
    if (experimentDirectory.exists() && experimentDirectory.isDirectory()) {
      result = false;
    } else {
      result = true;
    }

    return result;
  }

  private void createExperimentDirectory() {
    File experimentDirectory;
    experimentDirectory = new File(experimentBaseDirectory);

    if (experimentDirectory.exists()) {
      experimentDirectory.delete() ;
    }

    boolean result ;
    result = new File(experimentBaseDirectory).mkdirs() ;
    if (!result) {
      throw new JMetalException("Error creating experiment directory: " +
          experimentBaseDirectory) ;
    }
  }
}
