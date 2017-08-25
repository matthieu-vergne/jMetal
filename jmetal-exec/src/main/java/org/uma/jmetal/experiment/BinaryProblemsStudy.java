



//




//



package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHCBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.OneZeroMax;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoFront;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList ;
import java.util.List;

/**
 * Example of experimental study based on solving two binary problems with four algorithms: NSGAII,
 * SPEA2, MOCell, and MOCHC
 *
 * This experiment assumes that the reference Pareto front are not known, so the names of files
 * containing them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are: 1. Configure the experiment 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts 4. Compute que quality indicators 5. Generate Latex
 * tables reporting means and medians 6. Generate Latex tables with the result of applying the
 * Wilcoxon Rank Sum Test 7. Generate Latex tables with the ranking obtained by applying the
 * Friedman test 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class BinaryProblemsStudy {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<BinarySolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZDT5()));
    problemList.add(new ExperimentProblem<>(new OneZeroMax(512)));

    List<ExperimentAlgorithm<BinarySolution>> algorithmList =
            configureAlgorithmList(problemList);

    int numberOfCores = 8;
    List<GenericIndicator<BinarySolution>> indicatorList = Arrays.asList(
            new Epsilon<BinarySolution>(), new Spread<BinarySolution>(), new GenerationalDistance<BinarySolution>(),
            new PISAHypervolume<BinarySolution>(),
            new InvertedGenerationalDistance<BinarySolution>(),
            new InvertedGenerationalDistancePlus<BinarySolution>()) ;
    String referenceFrontDirectory = experimentBaseDirectory+"/referenceFronts" ;
    String outputParetoFrontFileName = "FUN" ;
    String outputParetoSetFileName = "VAR" ;
    String experimentName = "BinaryProblemsStudy" ;
    
    new ExecuteAlgorithms<>(algorithmList, INDEPENDENT_RUNS, numberOfCores, experimentBaseDirectory).run();
    List<String> referenceFrontFileNames = new LinkedList<>();
    new GenerateReferenceParetoFront(algorithmList, problemList, experimentBaseDirectory, referenceFrontDirectory, outputParetoFrontFileName, INDEPENDENT_RUNS, referenceFrontFileNames).run();
    new ComputeQualityIndicators<>(algorithmList, problemList, indicatorList, experimentBaseDirectory, referenceFrontDirectory, referenceFrontFileNames, outputParetoFrontFileName, outputParetoSetFileName, INDEPENDENT_RUNS).run();
    new GenerateLatexTablesWithStatistics(algorithmList, problemList, indicatorList, experimentBaseDirectory, experimentName).run();
    new GenerateWilcoxonTestTablesWithR<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).run();
    new GenerateFriedmanTestTables<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).run();
    new GenerateBoxplotsWithR<>(algorithmList, problemList, indicatorList, experimentBaseDirectory).setRows(1).setColumns(2).setDisplayNotch().run();

  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */

  static List<ExperimentAlgorithm<BinarySolution>> configureAlgorithmList(
          List<ExperimentProblem<BinarySolution>> problemList) {
    List<ExperimentAlgorithm<BinarySolution>> algorithms = new ArrayList<>();

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<BinarySolution>(
              problemList.get(i).getProblem(),
              new SinglePointCrossover(1.0),
              new BitFlipMutation(1.0 / ((BinaryProblem) problemList.get(i).getProblem()).getNumberOfBits(0)))
              .setMaxEvaluations(25000)
              .setPopulationSize(100)
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<BinarySolution>> algorithm = new SPEA2Builder<BinarySolution>(
              problemList.get(i).getProblem(),
              new SinglePointCrossover(1.0),
              new BitFlipMutation(1.0 / ((BinaryProblem) problemList.get(i).getProblem()).getNumberOfBits(0)))
              .setMaxIterations(250)
              .setPopulationSize(100)
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<BinarySolution>> algorithm = new MOCellBuilder<BinarySolution>(
              problemList.get(i).getProblem(),
              new SinglePointCrossover(1.0),
              new BitFlipMutation(1.0 / ((BinaryProblem) problemList.get(i).getProblem()).getNumberOfBits(0)))
              .setMaxEvaluations(25000)
              .setPopulationSize(100)
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    for (int i = 0; i < problemList.size(); i++) {
      CrossoverOperator<BinarySolution> crossoverOperator;
      MutationOperator<BinarySolution> mutationOperator;
      SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection;
      SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;

      crossoverOperator = new HUXCrossover(1.0);
      parentsSelection = new RandomSelection<BinarySolution>();
      newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(100);
      mutationOperator = new BitFlipMutation(0.35);
      Algorithm<List<BinarySolution>> algorithm = new MOCHCBuilder(
              (BinaryProblem) problemList.get(i).getProblem())
              .setInitialConvergenceCount(0.25)
              .setConvergenceValue(3)
              .setPreservedPopulation(0.05)
              .setPopulationSize(100)
              .setMaxEvaluations(25000)
              .setCrossover(crossoverOperator)
              .setNewGenerationSelection(newGenerationSelection)
              .setCataclysmicMutation(mutationOperator)
              .setParentSelection(parentsSelection)
              .setEvaluator(new SequentialSolutionListEvaluator<BinarySolution>())
              .build();
      algorithms.add(new ExperimentAlgorithm<>(algorithm, problemList.get(i).getTag()));
    }

    return algorithms;
  }
}
