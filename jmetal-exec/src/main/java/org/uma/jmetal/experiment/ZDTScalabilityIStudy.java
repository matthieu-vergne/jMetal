package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT1 problem but using five different
 * number of variables. This can be interesting to study the behaviour of the algorithms when solving
 * an scalable problem (in the number of variables). The used algorithms are NSGA-II, SPEA2 and
 * SMPSO.
 *
 * This experiment assumes that the reference Pareto front is of problem ZDT1 is known,
 * so the name of file containing it and the directory where it are located must be specified. Note
 * that the name of the file must be replicated to be equal to the number of problem variants.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts 4. Compute the quality indicators 5. Generate Latex
 * tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
 * Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTScalabilityIStudy {

  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT1(10), "ZDT110"));
    problemList.add(new ExperimentProblem<>(new ZDT1(20), "ZDT120"));
    problemList.add(new ExperimentProblem<>(new ZDT1(30), "ZDT130"));
    problemList.add(new ExperimentProblem<>(new ZDT1(40), "ZDT140"));
    problemList.add(new ExperimentProblem<>(new ZDT1(50), "ZDT150"));

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf", "ZDT1.pf", "ZDT1.pf", "ZDT1.pf", "ZDT1.pf");

    int numberOfCores = 8;
    List<GenericIndicator<DoubleSolution>> indicatorList = Arrays.asList(
            new Epsilon<DoubleSolution>(),
            new Spread<DoubleSolution>(),
            new GenerationalDistance<DoubleSolution>(),
            new PISAHypervolume<DoubleSolution>(),
            new InvertedGenerationalDistance<DoubleSolution>(),
            new InvertedGenerationalDistancePlus<DoubleSolution>()) ;
    String referenceFrontDirectory = "/pareto_fronts" ;
    String outputParetoFrontFileName = "FUN" ;
    String outputParetoSetFileName = "VAR" ;
    String experimentName = "ZDTScalabilityStudy" ;
    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>(experimentName)
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setOutputParetoFrontFileName(outputParetoFrontFileName)
                    .setOutputParetoSetFileName(outputParetoSetFileName)
                    .setReferenceFrontDirectory(referenceFrontDirectory)
                    .setReferenceFrontFileNames(referenceFrontFileNames)
                    .setIndicatorList(indicatorList)
                    .setIndependentRuns(INDEPENDENT_RUNS)
                    .setNumberOfCores(numberOfCores)
                    .build();

    new ExecuteAlgorithms<>(algorithmList, INDEPENDENT_RUNS, numberOfCores, experimentBaseDirectory).run();
    new ComputeQualityIndicators<>(algorithmList, problemList, indicatorList, experimentBaseDirectory, referenceFrontDirectory, referenceFrontFileNames, outputParetoFrontFileName, outputParetoSetFileName, INDEPENDENT_RUNS).run();
    new GenerateLatexTablesWithStatistics(algorithmList, problemList, indicatorList, experimentBaseDirectory, experimentName).run();
    new GenerateWilcoxonTestTablesWithR<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).run();
    new GenerateFriedmanTestTables<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).run();
    new GenerateBoxplotsWithR<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).setRows(3).setColumns(3).run();
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link
   * ExperimentAlgorithm} has an optional tag component, that can be set as it is shown in this example,
   * where four variants of a same algorithm are defined.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
          List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

    for (int i = 0; i < problemList.size(); i++) {
      double mutationProbability = 1.0 / problemList.get(i).getProblem().getNumberOfVariables();
      double mutationDistributionIndex = 20.0;
      Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder((DoubleProblem) problemList.get(i).getProblem(),
              new CrowdingDistanceArchive<DoubleSolution>(100))
              .setMutation(new PolynomialMutation(mutationProbability, mutationDistributionIndex))
              .setMaxIterations(250)
              .setSwarmSize(100)
              .setSolutionListEvaluator(new SequentialSolutionListEvaluator<DoubleSolution>())
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(
              problemList.get(i).getProblem(),
              new SBXCrossover(1.0, 20.0),
              new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(
              problemList.get(i).getProblem(),
              new SBXCrossover(1.0, 10.0),
              new PolynomialMutation(1.0 / problemList.get(i).getProblem().getNumberOfVariables(), 20.0))
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    return algorithms ;
  }
}