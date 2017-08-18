package org.uma.jmetal.util.experiment.component;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING ;
import java.util.*;

/**
 * This class computes the {@link QualityIndicator}s of an experiment. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * the list of indicators in obtained from the {@link ExperimentComponent #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ComputeQualityIndicators<S extends Solution<?>, Result> implements ExperimentComponent {

  private final List<ExperimentAlgorithm<S, Result>> algorithmList;
  private final List<ExperimentProblem<S>> problemList;
  private final List<GenericIndicator<S>> indicatorList;
  private final String experimentBaseDirectory;
  private final String referenceFrontDirectory;
  private final List<String> referenceFrontFileNames;
  private final String outputParetoFrontFileName;
  private final String outputParetoSetFileName;
  private final int independentRuns;

  @Deprecated
  public ComputeQualityIndicators(org.uma.jmetal.util.experiment.Experiment<S, Result> experiment) {
    this(experiment.getAlgorithmList(), experiment.getProblemList(), experiment.getIndicatorList(), experiment.getExperimentBaseDirectory(), experiment.getReferenceFrontDirectory(), experiment.getReferenceFrontFileNames(), experiment.getOutputParetoFrontFileName(), experiment.getOutputParetoSetFileName(), experiment.getIndependentRuns());
  }

  public ComputeQualityIndicators(List<ExperimentAlgorithm<S, Result>> algorithmList, List<ExperimentProblem<S>> problemList, List<GenericIndicator<S>> indicatorList, String experimentBaseDirectory, String referenceFrontDirectory, List<String> referenceFrontFileNames, String outputParetoFrontFileName, String outputParetoSetFileName, int independentRuns) {
    this.algorithmList = algorithmList;
    this.problemList = problemList;
    this.indicatorList = indicatorList;
    this.experimentBaseDirectory = experimentBaseDirectory;
    this.referenceFrontDirectory = referenceFrontDirectory;
    this.referenceFrontFileNames = referenceFrontFileNames;
    this.independentRuns = independentRuns;
    this.outputParetoFrontFileName = outputParetoFrontFileName;
    this.outputParetoSetFileName = outputParetoSetFileName;
  }

  @Override
  public void run() throws IOException {
    for (GenericIndicator<S> indicator : indicatorList) {
      JMetalLogger.logger.info("Computing indicator: " + indicator.getName()); ;

      for (ExperimentAlgorithm<?,Result> algorithm : algorithmList) {
        String algorithmDirectory ;
        algorithmDirectory = experimentBaseDirectory + "/data/" +
            algorithm.getAlgorithmTag() ;

        for (int problemId = 0; problemId < problemList.size(); problemId++) {
          String problemDirectory = algorithmDirectory + "/" + problemList.get(problemId).getTag() ;

          String referenceFrontName = referenceFrontDirectory +
              "/" + referenceFrontFileNames.get(problemId) ;

          JMetalLogger.logger.info("RF: " + referenceFrontName); ;
          Front referenceFront = new ArrayFront(referenceFrontName) ;

          FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;
          Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;

          String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();
          resetFile(qualityIndicatorFile);

          indicator.setReferenceParetoFront(normalizedReferenceFront);
          for (int i = 0; i < independentRuns; i++) {
            String frontFileName = problemDirectory + "/" +
                outputParetoFrontFileName + i + ".tsv";

            Front front = new ArrayFront(frontFileName) ;
            Front normalizedFront = frontNormalizer.normalize(front) ;
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront) ;
            Double indicatorValue = (Double)indicator.evaluate((List<S>) normalizedPopulation) ;
            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;

            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile) ;
          }
        }
      }
    }
    findBestIndicatorFronts(algorithmList, problemList, indicatorList, experimentBaseDirectory, outputParetoFrontFileName, outputParetoSetFileName);
  }

  private void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) {
    FileWriter os;
    try {
      os = new FileWriter(qualityIndicatorFile, true);
      os.write("" + indicatorValue + "\n");
      os.close();
    } catch (IOException ex) {
      throw new JMetalException("Error writing indicator file" + ex) ;
    }
  }

  /**
   * Deletes a file or directory if it does exist
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      JMetalLogger.logger.info("File " + file + " exist.");

      if (f.isDirectory()) {
        JMetalLogger.logger.info("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          JMetalLogger.logger.info("Directory successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting directory.");
        }
      } else {
        JMetalLogger.logger.info("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          JMetalLogger.logger.info("File succesfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting file.");
        }
      }
    } else {
      JMetalLogger.logger.info("File " + file + " does NOT exist.");
    }
  }

  @Deprecated
  public <S2 extends Solution<?>> void findBestIndicatorFronts(org.uma.jmetal.util.experiment.Experiment<S2, Result> experiment) throws IOException {
    findBestIndicatorFronts(experiment.getAlgorithmList(), experiment.getProblemList(), experiment.getIndicatorList(), experiment.getExperimentBaseDirectory(), experiment.getOutputParetoFrontFileName(), experiment.getOutputParetoSetFileName());
  }

  public <S2 extends Solution<?>> void findBestIndicatorFronts(List<ExperimentAlgorithm<S2, Result>> algorithmList, List<ExperimentProblem<S2>> problemList, List<GenericIndicator<S2>> indicatorList, String experimentBaseDirectory, String outputParetoFrontFileName, String outputParetoSetFileName) throws IOException {
	for (GenericIndicator<?> indicator : indicatorList) {
	for (ExperimentAlgorithm<?, Result> algorithm : algorithmList) {
        String algorithmDirectory;
		algorithmDirectory = experimentBaseDirectory + "/data/" +
            algorithm.getAlgorithmTag();

		for (ExperimentProblem<?> problem :problemList) {
          String indicatorFileName =
              algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
          Path indicatorFile = Paths.get(indicatorFileName) ;
          if (indicatorFile == null) {
            throw new JMetalException("Indicator file " + indicator.getName() + " doesn't exist") ;
          }

          List<String> fileArray;
          fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

          List<Pair<Double, Integer>> list = new ArrayList<>() ;


          for (int i = 0; i < fileArray.size(); i++) {
            Pair<Double, Integer> pair = new ImmutablePair<>(Double.parseDouble(fileArray.get(i)), i) ;
            list.add(pair) ;
          }

          Collections.sort(list, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> pair1, Pair<Double, Integer> pair2) {
              if (Math.abs(pair1.getLeft()) > Math.abs(pair2.getLeft())){
                return 1;
              } else if (Math.abs(pair1.getLeft()) < Math.abs(pair2.getLeft())) {
                return -1;
              } else {
                return 0;
              }
            }
          });
          String bestFunFileName ;
          String bestVarFileName ;
          String medianFunFileName ;
          String medianVarFileName ;

          String outputDirectory = algorithmDirectory + "/" + problem.getTag() ;

          bestFunFileName = outputDirectory + "/BEST_" + indicator.getName() + "_FUN.tsv" ;
          bestVarFileName = outputDirectory + "/BEST_" + indicator.getName() + "_VAR.tsv" ;
          medianFunFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_FUN.tsv" ;
          medianVarFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_VAR.tsv" ;
          if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
            String bestFunFile = outputDirectory + "/" +
                outputParetoFrontFileName + list.get(0).getRight() + ".tsv";
            String bestVarFile = outputDirectory + "/" +
                outputParetoSetFileName + list.get(0).getRight() + ".tsv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING) ;
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING) ;
          } else {
            String bestFunFile = outputDirectory + "/" +
                outputParetoFrontFileName + list.get(list.size()-1).getRight() + ".tsv";
            String bestVarFile = outputDirectory + "/" +
                outputParetoSetFileName + list.get(list.size()-1).getRight() + ".tsv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING) ;
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING) ;
          }

          int medianIndex = list.size() / 2 ;
          String medianFunFile = outputDirectory + "/" +
              outputParetoFrontFileName + list.get(medianIndex).getRight() + ".tsv";
          String medianVarFile = outputDirectory + "/" +
              outputParetoSetFileName + list.get(medianIndex).getRight() + ".tsv";

          Files.copy(Paths.get(medianFunFile), Paths.get(medianFunFileName), REPLACE_EXISTING) ;
          Files.copy(Paths.get(medianVarFile), Paths.get(medianVarFileName), REPLACE_EXISTING) ;
        }
      }
    }
  }
}

