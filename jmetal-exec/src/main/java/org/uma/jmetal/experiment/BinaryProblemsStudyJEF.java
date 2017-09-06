//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.experiment ;

import java.io.File ;
import java.io.IOException ;
import java.util.Arrays ;
import java.util.LinkedList ;
import java.util.List ;

import org.uma.jmetal.algorithm.Algorithm ;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder ;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHCBuilder ;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder ;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.AlgorithmID ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.DataID ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.Generation ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.ProblemID ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.RunContext ;
import org.uma.jmetal.experiment.p3.PreparePerformProduceExperimentBuilder.When ;
import org.uma.jmetal.experiment.p3.ThreadPerformer ;
import org.uma.jmetal.operator.CrossoverOperator ;
import org.uma.jmetal.operator.MutationOperator ;
import org.uma.jmetal.operator.SelectionOperator ;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover ;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover ;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation ;
import org.uma.jmetal.operator.impl.selection.RandomSelection ;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection ;
import org.uma.jmetal.problem.BinaryProblem ;
import org.uma.jmetal.problem.multiobjective.OneZeroMax ;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5 ;
import org.uma.jmetal.qualityindicator.impl.Epsilon ;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance ;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator ;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance ;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus ;
import org.uma.jmetal.qualityindicator.impl.Spread ;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume ;
import org.uma.jmetal.solution.BinarySolution ;
import org.uma.jmetal.util.JMetalException ;
import org.uma.jmetal.util.JMetalLogger ;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator ;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators ;
import org.uma.jmetal.util.experiment.component.GenerateBoxplotsWithR ;
import org.uma.jmetal.util.experiment.component.GenerateFriedmanTestTables ;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics ;
import org.uma.jmetal.util.experiment.component.GenerateReferenceParetoFront ;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTablesWithR ;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm ;
import org.uma.jmetal.util.experiment.util.ExperimentProblem ;
import org.uma.jmetal.util.fileoutput.SolutionListOutput ;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext ;

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
public class BinaryProblemsStudyJEF {
  private static final int INDEPENDENT_RUNS = 25 ;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory") ;
    }
    String experimentBaseDirectory = args[0] ;

    PreparePerformProduceExperimentBuilder<Algorithm<List<BinarySolution>>, BinaryProblem> experimentBuilder =
        new PreparePerformProduceExperimentBuilder<>() ;

    List<ProblemID<? extends BinaryProblem>> problemIDs = new LinkedList<>() ;
    problemIDs.add(experimentBuilder.prepareProblem(() -> new ZDT5())) ;
    problemIDs.add(experimentBuilder.prepareProblem(() -> new OneZeroMax(512))) ;

    List<AlgorithmID<? extends Algorithm<List<BinarySolution>>>> algorithmIDs = new LinkedList<>() ;
    algorithmIDs.add(
        experimentBuilder.prepareAlgorithm((problem) -> new NSGAIIBuilder<BinarySolution>(problem,
            new SinglePointCrossover(1.0), new BitFlipMutation(1.0 / problem.getNumberOfBits(0)))
                .setMaxEvaluations(25000).setPopulationSize(100).build())) ;
    algorithmIDs.add(
        experimentBuilder.prepareAlgorithm((problem) -> new SPEA2Builder<BinarySolution>(problem,
            new SinglePointCrossover(1.0), new BitFlipMutation(1.0 / problem.getNumberOfBits(0)))
                .setMaxIterations(250).setPopulationSize(100).build())) ;
    algorithmIDs.add(
        experimentBuilder.prepareAlgorithm((problem) -> new MOCellBuilder<BinarySolution>(problem,
            new SinglePointCrossover(1.0), new BitFlipMutation(1.0 / problem.getNumberOfBits(0)))
                .setMaxEvaluations(25000).setPopulationSize(100).build())) ;
    algorithmIDs.add(experimentBuilder.prepareAlgorithm((problem) -> {
      CrossoverOperator<BinarySolution> crossoverOperator ;
      MutationOperator<BinarySolution> mutationOperator ;
      SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection ;
      SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection ;

      crossoverOperator = new HUXCrossover(1.0) ;
      parentsSelection = new RandomSelection<BinarySolution>() ;
      newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(100) ;
      mutationOperator = new BitFlipMutation(0.35) ;
      return new MOCHCBuilder(problem).setInitialConvergenceCount(0.25).setConvergenceValue(3)
          .setPreservedPopulation(0.05).setPopulationSize(100).setMaxEvaluations(25000)
          .setCrossover(crossoverOperator).setNewGenerationSelection(newGenerationSelection)
          .setCataclysmicMutation(mutationOperator).setParentSelection(parentsSelection)
          .setEvaluator(new SequentialSolutionListEvaluator<BinarySolution>()).build() ;
    })) ;

    experimentBuilder.prepareIndependentRuns(INDEPENDENT_RUNS) ; // Default: 1

    experimentBuilder.performWith(() -> new ThreadPerformer(8)) ; // Default: SequentialPerformer

    // ExecuteAlgorithms
    DataID<String> problemTag = experimentBuilder.defineRunData(Generation.ONCE_AND_BACKUP,
        (context) -> context.getProblem().getName()) ;
    DataID<String> algorithmTag = experimentBuilder.defineRunData(Generation.ONCE_AND_BACKUP,
        (context) -> context.getAlgorithm().getName()) ;
    DataID<String> outputDirectoryName = experimentBuilder.defineRunData(Generation.ONCE_AND_BACKUP,
        (context) -> experimentBaseDirectory + "/data/" + context.getData(algorithmTag) + "/"
            + context.getData(problemTag)) ;
    DataID<String> funFile = experimentBuilder.defineRunData(Generation.ONCE_AND_BACKUP,
        (context) -> context.getData(outputDirectoryName) + "/FUN" + context.getRun() + ".tsv") ;
    DataID<String> varFile = experimentBuilder.defineRunData(Generation.ONCE_AND_BACKUP,
        (context) -> context.getData(outputDirectoryName) + "/VAR" + context.getRun() + ".tsv") ;
    experimentBuilder.createRunEvent(When.BEFORE_RUN,
        (context) -> JMetalLogger.logger
            .info("Running algorithm: " + context.getAlgorithm().getName() + ", problem: "
                + context.getProblem().getName() + ", run: " + context.getRun() + ", funFile: "
                + context.getData(funFile) + ", varFile: " + context.getData(varFile))) ;
    experimentBuilder.createRunEvent(When.AFTER_RUN, (context) -> {
      JMetalLogger.logger.info("Finished algorithm: " + context.getAlgorithm().getName()
          + ", problem: " + context.getProblem().getName() + ", run: " + context.getRun()) ;
      new File(context.getData(outputDirectoryName)).mkdirs() ;

      List<BinarySolution> population = context.getAlgorithm().getResult() ;
      new SolutionListOutput(population).setSeparator("\t")
          .setVarFileOutputContext(new DefaultFileOutputContext(context.getData(varFile)))
          .setFunFileOutputContext(new DefaultFileOutputContext(context.getData(funFile))).print() ;
      JMetalLogger.logger.info("Generated funFile: " + context.getData(funFile) + ", varFile: "
          + context.getData(varFile)) ;
    }) ;

    experimentBuilder.produceWith((dataset) -> {
      try {
        List<ExperimentProblem<BinarySolution>> problemList = new LinkedList<>() ;
        List<ExperimentAlgorithm<BinarySolution>> algorithmList = new LinkedList<>() ;
        for (AlgorithmID<? extends Algorithm<List<BinarySolution>>> algorithmID : algorithmIDs) {
          for (ProblemID<? extends BinaryProblem> problemID : problemIDs) {
            RunContext<? extends Algorithm<List<BinarySolution>>, ? extends BinaryProblem> context =
                dataset.getRunContext(algorithmID, problemID, 0) ;
            problemList
                .add(new ExperimentProblem<>(context.getProblem(), context.getData(problemTag))) ;
            algorithmList.add(new ExperimentAlgorithm<>(context.getAlgorithm(),
                context.getData(algorithmTag), context.getData(problemTag))) ;
          }
        }

        String referenceFrontDirectory = experimentBaseDirectory + "/referenceFronts" ;
        String outputParetoFrontFileName = "FUN" ;
        List<String> referenceFrontFileNames = new LinkedList<>() ;
        new GenerateReferenceParetoFront(algorithmList, problemList, experimentBaseDirectory,
            referenceFrontDirectory, outputParetoFrontFileName, INDEPENDENT_RUNS,
            referenceFrontFileNames).run() ;

        List<GenericIndicator<BinarySolution>> indicatorList =
            Arrays.asList(new Epsilon<BinarySolution>(), new Spread<BinarySolution>(),
                new GenerationalDistance<BinarySolution>(), new PISAHypervolume<BinarySolution>(),
                new InvertedGenerationalDistance<BinarySolution>(),
                new InvertedGenerationalDistancePlus<BinarySolution>()) ;
        String outputParetoSetFileName = "VAR" ;
        new ComputeQualityIndicators<>(algorithmList, problemList, indicatorList,
            experimentBaseDirectory, referenceFrontDirectory, referenceFrontFileNames,
            outputParetoFrontFileName, outputParetoSetFileName, INDEPENDENT_RUNS).run() ;

        String experimentName = "BinaryProblemsStudy" ;
        new GenerateLatexTablesWithStatistics(algorithmList, problemList, indicatorList,
            experimentBaseDirectory, experimentName).run() ;
        new GenerateWilcoxonTestTablesWithR<>(algorithmList, problemList, indicatorList,
            experimentBaseDirectory).run() ;
        new GenerateFriedmanTestTables<>(algorithmList, problemList, indicatorList,
            experimentBaseDirectory).run() ;
        new GenerateBoxplotsWithR<>(algorithmList, problemList, indicatorList,
            experimentBaseDirectory).setRows(1).setColumns(2).setDisplayNotch().run() ;
      } catch (IOException cause) {
        throw new RuntimeException(cause) ;
      }
    }) ;

    Experiment experiment = experimentBuilder.build() ;

    experiment.run() ;
  }
}
