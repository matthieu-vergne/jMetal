package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class generates a R script that generates an eps file containing boxplots
 *
 * The results are a set of R files that are written in the directory
 * {@link Experiment #getExperimentBaseDirectory()}/R. Each file is called as
 * indicatorName.Wilcoxon.R
 *
 * To run the R script: Rscript indicatorName.Wilcoxon.R
 * To generate the resulting Latex file: pdflatex indicatorName.Wilcoxon.tex
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerateBoxplotsWithR<Result> implements ExperimentComponent {
  private static final String DEFAULT_R_DIRECTORY = "R";

  private final List<? extends ExperimentAlgorithm<?>> algorithmList;
  private final List<? extends ExperimentProblem<?>> problemList;
  private final List<? extends GenericIndicator<?>> indicatorList;
  private final String experimentBaseDirectory;
  private int numberOfRows ;
  private int numberOfColumns ;
  private boolean displayNotch ;

  @Deprecated
  public GenerateBoxplotsWithR(org.uma.jmetal.util.experiment.Experiment<?, Result> experimentConfiguration) {
    this(experimentConfiguration.getAlgorithmList(), experimentConfiguration.getProblemList(), experimentConfiguration.getIndicatorList(), experimentConfiguration.getExperimentBaseDirectory());
  }

  public GenerateBoxplotsWithR(List<? extends ExperimentAlgorithm<?>> algorithmList, List<? extends ExperimentProblem<?>> problemList, List<? extends GenericIndicator<?>> indicatorList, String experimentBaseDirectory) {
    this.displayNotch = false ;
    this.numberOfRows = 3 ;
    this.numberOfColumns = 3 ;

    this.algorithmList = ExperimentAlgorithm.filterTagDuplicates(algorithmList);
    this.problemList = problemList;
    this.indicatorList = indicatorList;
    this.experimentBaseDirectory = experimentBaseDirectory;
  }

  public GenerateBoxplotsWithR<Result> setRows(int rows) {
    numberOfRows = rows ;

    return this ;
  }

  public GenerateBoxplotsWithR<Result> setColumns(int columns) {
    numberOfColumns = columns ;

    return this ;
  }

  public GenerateBoxplotsWithR<Result> setDisplayNotch() {
    displayNotch = true ;

    return this ;
  }

  @Override
  public void run() throws IOException {
    String rDirectoryName = experimentBaseDirectory + "/" + DEFAULT_R_DIRECTORY;
    File rOutput;
    rOutput = new File(rDirectoryName);
    if (!rOutput.exists()) {
      new File(rDirectoryName).mkdirs();
      System.out.println("Creating " + rDirectoryName + " directory");
    }
    for (GenericIndicator<?> indicator : indicatorList) {
      String rFileName = rDirectoryName + "/" + indicator.getName() + ".Boxplot" + ".R";

      FileWriter os = new FileWriter(rFileName, false);
      os.write("postscript(\"" +
               indicator.getName() +
              ".Boxplot.eps\", horizontal=FALSE, onefile=FALSE, height=8, width=12, pointsize=10)" +
              "\n");

      os.write("resultDirectory<-\"../data" + "\"" + "\n");
      os.write("qIndicator <- function(indicator, problem)" + "\n");
      os.write("{" + "\n");

      for (int i = 0; i <  algorithmList.size(); i++) {
        String algorithmName = algorithmList.get(i).getAlgorithmTag();
        os.write("file" +  algorithmName + "<-paste(resultDirectory, \"" + algorithmName + "\", sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " +  "problem, sep=\"/\")" + "\n");
        os.write("file" +  algorithmName + "<-paste(file" +  algorithmName + ", " + "indicator, sep=\"/\")" + "\n");
        os.write( algorithmName + "<-scan(" + "file" +  algorithmName + ")" + "\n");
        os.write("\n");
      }

      os.write("algs<-c(");
      for (int i = 0; i <  algorithmList.size() - 1; i++) {
        os.write("\"" +  algorithmList.get(i).getAlgorithmTag() + "\",");
      } // for
      os.write("\"" +  algorithmList.get(algorithmList.size() - 1).getAlgorithmTag() + "\")" + "\n");

      os.write("boxplot(");
      for (int i = 0; i <  algorithmList.size(); i++) {
        os.write(algorithmList.get(i).getAlgorithmTag() + ",");
      } // for
      if (displayNotch) {
        os.write("names=algs, notch = TRUE)" + "\n");
      } else {
        os.write("names=algs, notch = FALSE)" + "\n");
      }
      os.write("titulo <-paste(indicator, problem, sep=\":\")" + "\n");
      os.write("title(main=titulo)" + "\n");

      os.write("}" + "\n");

      os.write("par(mfrow=c(" + numberOfRows + "," + numberOfColumns + "))" + "\n");

      os.write("indicator<-\"" +  indicator.getName() + "\"" + "\n");

      for (ExperimentProblem<?> problem : problemList) {
        os.write("qIndicator(indicator, \"" + problem.getTag() + "\")" + "\n");
      }

      os.close();
    }
  }
}