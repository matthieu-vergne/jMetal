package org.uma.jmetal.util.experiment.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.*;
import java.util.*;

/**
 * This class computes a number of statistical values (mean, median, standard deviation, interquartile range)
 * from the indicator files generated after executing {@link ExecuteAlgorithms} and {@link ComputeQualityIndicators}.
 * After reading the data files and calculating the values, a Latex file is created containing an script
 * that generates tables with the best and second best values per indicator. The name of the file is
 * {@link Experiment #getExperimentName()}.tex, which is located by default in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/latex
 *
 * Although the maximum, minimum, and total number of items are also computed, no tables are generated
 * with them (this is a pending work).
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateLatexTablesWithStatistics implements ExperimentComponent {
  private static final String DEFAULT_LATEX_DIRECTORY = "latex" ;

  private final List<? extends ExperimentProblem<?>> problemList;
  private final List<? extends ExperimentAlgorithm<?>> algorithmList;
  private final List<? extends GenericIndicator<?>> indicatorList;
  private final String experimentBaseDirectory;
  private final String experimentName;

  private double[][][] mean;
  private double[][][] median;
  private double[][][] stdDeviation;
  private double[][][] iqr;
  private double[][][] max;
  private double[][][] min;
  private double[][][] numberOfValues;

  @Deprecated
  public GenerateLatexTablesWithStatistics(org.uma.jmetal.util.experiment.Experiment<?, ?> configuration) {
    this(configuration.getAlgorithmList(), configuration.getProblemList(), configuration.getIndicatorList(), configuration.getExperimentBaseDirectory(), configuration.getExperimentName());
  }

  public GenerateLatexTablesWithStatistics(List<? extends ExperimentAlgorithm<?>> algorithmList, List<? extends ExperimentProblem<?>> problemList, List<? extends GenericIndicator<?>> indicatorList, String experimentBaseDirectory, String experimentName) {
    this.algorithmList = ExperimentAlgorithm.filterTagDuplicates(algorithmList);
    this.problemList = problemList;
    this.indicatorList = indicatorList;
    this.experimentBaseDirectory = experimentBaseDirectory;
    this.experimentName = experimentName;
  }

  @Override
  public void run() throws IOException {
    List<List<List<List<Double>>>> data = readDataFromFiles() ;
    computeDataStatistics(data) ;
    generateLatexScript(data) ;
  }

  private List<List<List<List<Double>>>> readDataFromFiles() throws IOException {
    List<List<List<List<Double>>>> data = new ArrayList<List<List<List<Double>>>>(indicatorList.size()) ;

    for (int indicator = 0; indicator < indicatorList.size(); indicator++ ) {
      // A data vector per problem
      data.add(indicator, new ArrayList<List<List<Double>>>()) ;
      for (int problem = 0; problem < problemList.size(); problem++) {
        data.get(indicator).add(problem, new ArrayList<List<Double>>());

        for (int algorithm = 0; algorithm < algorithmList.size(); algorithm++) {
          data.get(indicator).get(problem).add(algorithm, new ArrayList<Double>());

          String directory = experimentBaseDirectory;
          directory += "/data/";
          directory += "/" + algorithmList.get(algorithm).getAlgorithmTag();
          directory += "/" + problemList.get(problem).getTag();
          directory += "/" + indicatorList.get(indicator).getName();
          // Read values from data files
          FileInputStream fis = new FileInputStream(directory);
          InputStreamReader isr = new InputStreamReader(fis);
          BufferedReader br = new BufferedReader(isr);
          String aux = br.readLine();
          while (aux != null) {
            data.get(indicator).get(problem).get(algorithm).add(Double.parseDouble(aux));
            aux = br.readLine();
          }
          br.close();
        }
      }
    }

    return data ;
  }

  private void computeDataStatistics(List<List<List<List<Double>>>> data) {
    int indicatorListSize = indicatorList.size() ;
    mean = new double[indicatorListSize][][];
    median = new double[indicatorListSize][][];
    stdDeviation = new double[indicatorListSize][][];
    iqr = new double[indicatorListSize][][];
    min = new double[indicatorListSize][][];
    max = new double[indicatorListSize][][];
    numberOfValues = new double[indicatorListSize][][];

    int problemListSize = problemList.size() ;
    for (int indicator = 0; indicator < indicatorListSize; indicator++) {
      // A data vector per problem
      mean[indicator] = new double[problemListSize][];
      median[indicator] = new double[problemListSize][];
      stdDeviation[indicator] = new double[problemListSize][];
      iqr[indicator] = new double[problemListSize][];
      min[indicator] = new double[problemListSize][];
      max[indicator] = new double[problemListSize][];
      numberOfValues[indicator] = new double[problemListSize][];

      int algorithmListSize = algorithmList.size() ;
      for (int problem = 0; problem < problemListSize; problem++) {
        mean[indicator][problem] = new double[algorithmListSize];
        median[indicator][problem] = new double[algorithmListSize];
        stdDeviation[indicator][problem] = new double[algorithmListSize];
        iqr[indicator][problem] = new double[algorithmListSize];
        min[indicator][problem] = new double[algorithmListSize];
        max[indicator][problem] = new double[algorithmListSize];
        numberOfValues[indicator][problem] = new double[algorithmListSize];

        for (int algorithm = 0; algorithm < algorithmListSize; algorithm++) {
          Collections.sort(data.get(indicator).get(problem).get(algorithm));

          Map<String, Double> statValues = computeStatistics(data.get(indicator).get(problem).get(algorithm)) ;

          mean[indicator][problem][algorithm] = statValues.get("mean");
          median[indicator][problem][algorithm] = statValues.get("median");
          stdDeviation[indicator][problem][algorithm] = statValues.get("stdDeviation");
          iqr[indicator][problem][algorithm] = statValues.get("iqr");
          min[indicator][problem][algorithm] = statValues.get("min");
          max[indicator][problem][algorithm] = statValues.get("max");
          numberOfValues[indicator][problem][algorithm] = statValues.get("numberOfElements").intValue();
        }
      }
    }
  }

  private void generateLatexScript(List<List<List<List<Double>>>> data) throws IOException {
    String latexDirectoryName = experimentBaseDirectory + "/" + DEFAULT_LATEX_DIRECTORY ;
    File latexOutput;
    latexOutput = new File(latexDirectoryName);
    if (!latexOutput.exists()) {
      new File(latexDirectoryName).mkdirs();
      JMetalLogger.logger.info("Creating " + latexDirectoryName + " directory");
    }
    //System.out.println("Experiment name: " + experimentName_);
    String latexFile = latexDirectoryName + "/" + experimentName + ".tex";
    printHeaderLatexCommands(latexFile);
    for (int i = 0; i < indicatorList.size(); i++) {
      printData(latexFile, i, mean, stdDeviation, "Mean and Standard Deviation");
      printData(latexFile, i, median, iqr, "Median and Interquartile Range");
    }
    printEndLatexCommands(latexFile);
  }

  /**
   * Computes the statistical values
   * @param values
   * @return
   */
  private Map<String, Double> computeStatistics(List<Double> values) {
    Map<String, Double> results = new HashMap<>() ;

    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (Double value : values) {
      stats.addValue(value);
    }

    results.put("mean", stats.getMean()) ;
    results.put("median", stats.getPercentile(50.0)) ;
    results.put("stdDeviation", stats.getStandardDeviation());
    results.put("iqr", stats.getPercentile(75) - stats.getPercentile(25));
    results.put("max", stats.getMax());
    results.put("min", stats.getMean());
    results.put("numberOfElements", (double)values.size()) ;

    return results ;
  }

  void printHeaderLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, false);
    os.write("\\documentclass{article}" + "\n");
    os.write("\\title{" + experimentName + "}" + "\n");
    os.write("\\usepackage{colortbl}" + "\n");
    os.write("\\usepackage[table*]{xcolor}" + "\n");
    os.write("\\xdefinecolor{gray95}{gray}{0.65}" + "\n");
    os.write("\\xdefinecolor{gray25}{gray}{0.8}" + "\n");
    os.write("\\author{A.J. Nebro}" + "\n");
    os.write("\\begin{document}" + "\n");
    os.write("\\maketitle" + "\n");
    os.write("\\section{Tables}" + "\n");

    os.close();
  }

  void printEndLatexCommands(String fileName) throws IOException {
    FileWriter os = new FileWriter(fileName, true);
    os.write("\\end{document}" + "\n");
    os.close();
  }

  private void printData(String latexFile, int indicatorIndex, double[][][] centralTendency, double[][][] dispersion, String caption) throws IOException {
    // Generate header of the table
    FileWriter os = new FileWriter(latexFile, true);
    os.write("\n");
    os.write("\\begin{table}" + "\n");
    os.write("\\caption{" + indicatorList.get(indicatorIndex).getName() + ". " + caption + "}" + "\n");
    os.write("\\label{table: " + indicatorList.get(indicatorIndex).getName() + "}" + "\n");
    os.write("\\centering" + "\n");
    os.write("\\begin{scriptsize}" + "\n");
    os.write("\\begin{tabular}{l");

    // calculate the number of columns
    os.write(StringUtils.repeat("l", algorithmList.size()));
    os.write("}\n");
    os.write("\\hline");

    // write table head
    for (int i = -1; i < algorithmList.size(); i++) {
      if (i == -1) {
        os.write(" & ");
      } else if (i == (algorithmList.size() - 1)) {
        os.write(" " + algorithmList.get(i).getAlgorithmTag() + "\\\\" + "\n");
      } else {
        os.write("" + algorithmList.get(i).getAlgorithmTag() + " & ");
      }
    }
    os.write("\\hline \n");

    // write lines
    for (int i = 0; i < problemList.size(); i++) {
      // find the best value and second best value
      double bestCentralTendencyValue;
      double bestDispersionValue;
      double secondBestCentralTendencyValue;
      double secondBestDispersionValue;
      int bestIndex = -1;
      int secondBestIndex = -1;

      if (indicatorList.get(indicatorIndex).isTheLowerTheIndicatorValueTheBetter()) {
        bestCentralTendencyValue = Double.MAX_VALUE;
        bestDispersionValue = Double.MAX_VALUE;
        secondBestCentralTendencyValue = Double.MAX_VALUE;
        secondBestDispersionValue = Double.MAX_VALUE;
        for (int j = 0; j < (algorithmList.size()); j++) {
          if ((centralTendency[indicatorIndex][i][j] < bestCentralTendencyValue) ||
              ((centralTendency[indicatorIndex][i][j] ==
                  bestCentralTendencyValue) && (dispersion[indicatorIndex][i][j] < bestDispersionValue))) {
            secondBestIndex = bestIndex;
            secondBestCentralTendencyValue = bestCentralTendencyValue;
            secondBestDispersionValue = bestDispersionValue;
            bestCentralTendencyValue = centralTendency[indicatorIndex][i][j];
            bestDispersionValue = dispersion[indicatorIndex][i][j];
            bestIndex = j;
          } else if ((centralTendency[indicatorIndex][i][j] < secondBestCentralTendencyValue) ||
              ((centralTendency[indicatorIndex][i][j] ==
                  secondBestCentralTendencyValue) && (dispersion[indicatorIndex][i][j] < secondBestDispersionValue))) {
            secondBestIndex = j;
            secondBestCentralTendencyValue = centralTendency[indicatorIndex][i][j];
            secondBestDispersionValue = dispersion[indicatorIndex][i][j];
          }
        }
      } else {
        bestCentralTendencyValue = Double.MIN_VALUE;
        bestDispersionValue = Double.MIN_VALUE;
        secondBestCentralTendencyValue = Double.MIN_VALUE;
        secondBestDispersionValue = Double.MIN_VALUE;
        for (int j = 0; j < (algorithmList.size()); j++) {
          if ((centralTendency[indicatorIndex][i][j] > bestCentralTendencyValue) ||
              ((centralTendency[indicatorIndex][i][j] ==
                  bestCentralTendencyValue) && (dispersion[indicatorIndex][i][j] < bestDispersionValue))) {
            secondBestIndex = bestIndex;
            secondBestCentralTendencyValue = bestCentralTendencyValue;
            secondBestDispersionValue = bestDispersionValue;
            bestCentralTendencyValue = centralTendency[indicatorIndex][i][j];
            bestDispersionValue = dispersion[indicatorIndex][i][j];
            bestIndex = j;
          } else if ((centralTendency[indicatorIndex][i][j] > secondBestCentralTendencyValue) ||
              ((centralTendency[indicatorIndex][i][j] ==
                  secondBestCentralTendencyValue) && (dispersion[indicatorIndex][i][j] < secondBestDispersionValue))) {
            secondBestIndex = j;
            secondBestCentralTendencyValue = centralTendency[indicatorIndex][i][j];
            secondBestDispersionValue = dispersion[indicatorIndex][i][j];
          }
        }
      }

      os.write(problemList.get(i).getTag().replace("_", "\\_") + " & ");
      for (int j = 0; j < (algorithmList.size() - 1); j++) {
        if (j == bestIndex) {
          os.write("\\cellcolor{gray95}");
        }
        if (j == secondBestIndex) {
          os.write("\\cellcolor{gray25}");
        }

        String m = String.format(Locale.ENGLISH, "%10.2e", centralTendency[indicatorIndex][i][j]);
        String s = String.format(Locale.ENGLISH, "%8.1e", dispersion[indicatorIndex][i][j]);
        os.write("$" + m + "_{" + s + "}$ & ");
      }
      if (bestIndex == (algorithmList.size()- 1)) {
        os.write("\\cellcolor{gray95}");
      }
      if (secondBestIndex == (algorithmList.size()- 1)) {
        os.write("\\cellcolor{gray25}");
      }
      String m = String.format(Locale.ENGLISH, "%10.2e",
          centralTendency[indicatorIndex][i][algorithmList.size() - 1]);
      String s = String.format(Locale.ENGLISH, "%8.1e",
          dispersion[indicatorIndex][i][algorithmList.size() - 1]);
      os.write("$" + m + "_{" + s + "}$ \\\\" + "\n");
    }

    // close table
    os.write("\\hline" + "\n");
    os.write("\\end{tabular}" + "\n");
    os.write("\\end{scriptsize}" + "\n");
    os.write("\\end{table}" + "\n");
    os.close();
  }

}
