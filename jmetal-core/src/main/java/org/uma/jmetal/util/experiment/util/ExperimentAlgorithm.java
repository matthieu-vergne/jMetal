package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class defining tasks for the execution of algorithms in parallel.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExperimentAlgorithm<S extends Solution<?>>  {
  private Algorithm<? extends List<? extends S>> algorithm;
  private String algorithmTag;
  private String problemTag;

  /**
   * Constructor
   */
  public ExperimentAlgorithm(
          Algorithm<? extends List<? extends S>> algorithm,
          String algorithmTag,
          String problemTag) {
    this.algorithm = algorithm;
    this.algorithmTag = algorithmTag;
    this.problemTag = problemTag;
  }

  public ExperimentAlgorithm(
          Algorithm<? extends List<? extends S>> algorithm,
          String problemTag) {
    this(algorithm, algorithm.getName(), problemTag) ;
  }

  @Deprecated
  public void runAlgorithm(int id, org.uma.jmetal.util.experiment.Experiment<?, ?> experimentData) {
    runAlgorithm(id, experimentData.getExperimentBaseDirectory());
  }

  public void runAlgorithm(int id, String experimentBaseDirectory) {
    String outputDirectoryName = experimentBaseDirectory
            + "/data/"
            + algorithmTag
            + "/"
            + problemTag;

    File outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdirs();
      if (result) {
        JMetalLogger.logger.info("Creating " + outputDirectoryName);
      } else {
        JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
      }
    }

    String funFile = outputDirectoryName + "/FUN" + id + ".tsv";
    String varFile = outputDirectoryName + "/VAR" + id + ".tsv";
    JMetalLogger.logger.info(
            " Running algorithm: " + algorithmTag +
                    ", problem: " + problemTag +
                    ", run: " + id +
                    ", funFile: " + funFile);


    algorithm.run();
    List<? extends S> population = algorithm.getResult();

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
            .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
            .print();
  }

  public Algorithm<? extends List<? extends S>> getAlgorithm() {
    return algorithm;
  }

  public String getAlgorithmTag() {
    return algorithmTag;
  }

  public String getProblemTag() {
    return problemTag;
  }
  
  /**
   * The list of algorithms contain an algorithm instance per problem. This is not convenient for
   * calculating statistical data, because a same algorithm will appear many times.
   * This method remove duplicated algorithms and leave only an instance of each one.
   */
  public static <E extends ExperimentAlgorithm<?>> List<E> filterTagDuplicates(List<E> algorithms) {
    List<E> algorithmList = new ArrayList<>() ;
    List<String> algorithmTagList = new ArrayList<>() ;

    for (E algorithm : algorithms) {
      if (!algorithmTagList.contains(algorithm.getAlgorithmTag())) {
        algorithmList.add(algorithm) ;
        algorithmTagList.add(algorithm.getAlgorithmTag()) ;
      }
    }
    
    return algorithmList;
  }
  
}
