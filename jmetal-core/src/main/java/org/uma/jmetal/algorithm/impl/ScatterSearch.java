package org.uma.jmetal.algorithm.impl;

import java.util.Collection;
import java.util.LinkedList;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.PullMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.MeasureFactory;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.measure.impl.SimplePushMeasure;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.ParameterManager;
import org.uma.jmetal.parameter.Parameterable;
import org.uma.jmetal.parameter.impl.SimpleParameterManager;
import org.uma.jmetal.solution.Solution;

@SuppressWarnings("serial")
public class ScatterSearch<S extends Solution<?>, ReferenceSet extends ScatterSearch.ReferenceSetDefinition<S>, Subset> implements Algorithm<ReferenceSet>, Parameterable, Measurable {
	
    /************************************************\
     * Methods officially required by the algorithm *
    \************************************************/
    
	private Operator<S, Collection<S>> diversificationGenerator;

	public Operator<S, Collection<S>> getDiversificationGenerator() {
		return diversificationGenerator;
	}

	public void setDiversificationGenerator(Operator<S, Collection<S>> diversificationGenerator) {
		this.diversificationGenerator = diversificationGenerator;
	}

	private Operator<S, Collection<S>> improvementMethod;

	public Operator<S, Collection<S>> getImprovementMethod() {
		return improvementMethod;
	}

	public void setImprovementMethod(Operator<S, Collection<S>> improvementMethod) {
		this.improvementMethod = improvementMethod;
	}

	public static interface ReferenceSetDefinition<S extends Solution<?>> {
		public void feed(int b, Collection<S> newSolutions);
	}

	private Operator<Integer, ReferenceSet> referenceSetBuilder;

	public Operator<Integer, ReferenceSet> getReferenceSetBuilder() {
		return referenceSetBuilder;
	}

	public void setReferenceSetBuilder(Operator<Integer, ReferenceSet> referenceSetBuilder) {
		this.referenceSetBuilder = referenceSetBuilder;
	}
    
	private Operator<ReferenceSet, Subset> subsetGenerationMethod;

	public Operator<ReferenceSet, Subset> getSubsetGenerationMethod() {
		return subsetGenerationMethod;
	}

	public void setSubsetGenerationMethod(Operator<ReferenceSet, Subset> subsetGenerationMethod) {
		this.subsetGenerationMethod = subsetGenerationMethod;
	}

	private Operator<Subset, Collection<S>> solutionCombinationMethod;

	public Operator<Subset, Collection<S>> getSolutionCombinationMethod() {
		return solutionCombinationMethod;
	}

	public void setSolutionCombinationMethod(Operator<Subset, Collection<S>> solutionCombinationMethod) {
		this.solutionCombinationMethod = solutionCombinationMethod;
	}
    
    /*****************************************************\
     * Additional methods required by the implementation *
    \*****************************************************/
    
	private Collection<S> seedSolutions = new LinkedList<>();

	public Collection<S> getSeedSolutions() {
		return seedSolutions;
	}

	public void setSeedSolutions(Collection<S> seedSolutions) {
		this.seedSolutions = seedSolutions;
	}
    
	private int referenceSetLimit = 20;

	public int getReferenceSetLimit() {
		return referenceSetLimit;
	}

	public void setReferenceSetLimit(int referenceSetLimit) {
		this.referenceSetLimit = referenceSetLimit;
	}

	private int totalNumberOfEnhancedTrialSolutions = 100;

	public int getTotalNumberOfEnhancedTrialSolutions() {
		return totalNumberOfEnhancedTrialSolutions;
	}

	public void setTotalNumberOfEnhancedTrialSolutions(int totalNumberOfEnhancedTrialSolutions) {
		this.totalNumberOfEnhancedTrialSolutions = totalNumberOfEnhancedTrialSolutions;
	}

	private Operator<Collection<Subset>, Boolean> subsetGenerationDecisionMaker;

	public Operator<Collection<Subset>, Boolean> getSubsetGenerationDecisionMaker() {
		return subsetGenerationDecisionMaker;
	}

	public void setSubsetGenerationDecisionMaker(Operator<Collection<Subset>, Boolean> subsetGenerationDecisionMaker) {
		this.subsetGenerationDecisionMaker = subsetGenerationDecisionMaker;
	}

	public boolean shouldGenerateMoreSubsets(Collection<Subset> subsets) {
		return subsetGenerationDecisionMaker.execute(subsets);
	}

	private int iterationLimit = 1000;

	public int getIterationLimit() {
		return iterationLimit;
	}

	public void setIterationLimit(int iterationLimit) {
		this.iterationLimit = iterationLimit;
	}
	
    /*************************************************\
     * Constructor: setup of parameters and measures *
    \*************************************************/
    
    private final SimpleParameterManager parameterManager = new SimpleParameterManager();
    private final SimpleMeasureManager measureManager = new SimpleMeasureManager();
    
    private final SimplePushMeasure<S> lastSeedSolutionUsed = new SimplePushMeasure<>("Seed Solution", "The last seed solution used to generate trial solutions.");
    private final SimplePushMeasure<Collection<S>> lastTrialSolutionsGenerated = new SimplePushMeasure<>("Trial Solutions", "The last trial solutions generated from a seed solution.");
    private final SimplePushMeasure<Collection<S>> lastEnhancedSolutionsGenerated = new SimplePushMeasure<>("Enhanced Solutions", "The last enhanced solutions generated from a trial solution.");
    private final SimplePushMeasure<ReferenceSet> lastReferenceSetUpdate = new SimplePushMeasure<>("Reference Set", "The last reference set update.");

    private final SimplePushMeasure<Collection<Subset>> lastSubsetsGenerated = new SimplePushMeasure<>("Subsets", "The last subsets generated from the reference set.");
    private final SimplePushMeasure<Collection<S>> lastCombinedSolutionsGenerated = new SimplePushMeasure<>("Combined Solutions", "The last combined solutions generated from a subset.");
    
    public static enum Phase {STOP, INIT, SEARCH}
    private final SimplePushMeasure<Phase> phaseMeasure = new SimplePushMeasure<>("Phase", "The current phase of the algorithm.");
    private final CountingMeasure iteration = new CountingMeasure("Iteration", "The index of the iteration started.");
    
    public ScatterSearch() {
        /**************\
         * Parameters *
        \**************/
    	
		parameterManager.addParameter(new Parameter<Operator<S, Collection<S>>>() {

			@Override
			public void set(Operator<S, Collection<S>> value) {
				setDiversificationGenerator(value);
			}

			@Override
			public Operator<S, Collection<S>> get() {
				return getDiversificationGenerator();
			}

			@Override
			public String getName() {
				return "Diversification Generator";
			}

			@Override
			public String getDescription() {
				return "Generate a collection of diverse trial solutions, " +
						"using an arbitrary trial solution (or seed solution) " +
						"as an input.";
			}
		});
		parameterManager.addParameter(new Parameter<Operator<S, Collection<S>>>() {

			@Override
			public void set(Operator<S, Collection<S>> value) {
				setImprovementMethod(value);
			}

			@Override
			public Operator<S, Collection<S>> get() {
				return getImprovementMethod();
			}

			@Override
			public String getName() {
				return "Improvement Method";
			}

			@Override
			public String getDescription() {
				return "Transform a trial solution into one or more " +
						"enhanced trial solutions.  (Neither the input " +
						"nor output solutions are required to be feasible, " +
						"though the output solutions will more usually be " +
						"expected to be so. If no improvement of the input " +
						"trial solution results, the “enhanced” solution is " +
						"considered to be the same as the input solution.)";
			}
		});
		parameterManager.addParameter(new Parameter<Operator<Integer, ReferenceSet>>() {

			@Override
			public void set(Operator<Integer, ReferenceSet> value) {
				setReferenceSetBuilder(value);
			}

			@Override
			public Operator<Integer, ReferenceSet> get() {
				return getReferenceSetBuilder();
			}

			@Override
			public String getName() {
				return "Reference Set Builder";
			}

			@Override
			public String getDescription() {
				return "Build a Reference Set consisting of the b best solutions " +
						"found (where the value of b is typically small, e.g., " +
						"between 20 and 40), organized to provide efficient " +
						"accessing by other parts of the method.";
			}
		});
		parameterManager.addParameter(new Parameter<Operator<ReferenceSet, Subset>>() {

			@Override
			public void set(Operator<ReferenceSet, Subset> value) {
				setSubsetGenerationMethod(value);
			}

			@Override
			public Operator<ReferenceSet, Subset> get() {
				return getSubsetGenerationMethod();
			}

			@Override
			public String getName() {
				return "Subset Generation Method";
			}

			@Override
			public String getDescription() {
				return "Operate on the Reference Set, to produce a subset of its " +
						"solutions as a basis for creating combined solutions.";
			}
		});
		parameterManager.addParameter(new Parameter<Operator<Subset, Collection<S>>>() {

			@Override
			public void set(Operator<Subset, Collection<S>> value) {
				setSolutionCombinationMethod(value);
			}

			@Override
			public Operator<Subset, Collection<S>> get() {
				return getSolutionCombinationMethod();
			}

			@Override
			public String getName() {
				return "Solution Combination Method";
			}

			@Override
			public String getDescription() {
				return "Transform a given subset of solutions produced by the " +
						"Subset Generation Method into one or more combined " +
						"solution vectors.";
			}
		});
		parameterManager.addParameter(new Parameter<Collection<S>>() {

			@Override
			public void set(Collection<S> value) {
				setSeedSolutions(value);
			}

			@Override
			public Collection<S> get() {
				return getSeedSolutions();
			}

			@Override
			public String getName() {
				return "Seed Solutions";
			}

			@Override
			public String getDescription() {
				return "The initial solutions to use as seeds for the initial " +
						"phase of the Scatter Search algorithm.";
			}
		});
		parameterManager.addParameter(new Parameter<Integer>() {

			@Override
			public void set(Integer value) {
				setReferenceSetLimit(value);
			}

			@Override
			public Integer get() {
				return getReferenceSetLimit();
			}

			@Override
			public String getName() {
				return "Reference Set Limit";
			}

			@Override
			public String getDescription() {
				return "The maximal number of solutions to store in the Reference Set.";
			}
		});
		parameterManager.addParameter(new Parameter<Integer>() {

			@Override
			public void set(Integer value) {
				setTotalNumberOfEnhancedTrialSolutions(value);
			}

			@Override
			public Integer get() {
				return getTotalNumberOfEnhancedTrialSolutions();
			}

			@Override
			public String getName() {
				return "Initial Generation Limit";
			}

			@Override
			public String getDescription() {
				return "The number of solutions to generate for the initial phase.";
			}
		});
		parameterManager.addParameter(new Parameter<Integer>() {

			@Override
			public void set(Integer value) {
				setIterationLimit(value);
			}

			@Override
			public Integer get() {
				return getIterationLimit();
			}

			@Override
			public String getName() {
				return "Iteration Limit";
			}

			@Override
			public String getDescription() {
				return "The number of iteration to perform before to stop " +
						"the Scatter Search phase.";
			}
		});
		parameterManager.addParameter(new Parameter<Operator<Collection<Subset>, Boolean>>() {

			@Override
			public void set(Operator<Collection<Subset>, Boolean> value) {
				setSubsetGenerationDecisionMaker(value);
			}

			@Override
			public Operator<Collection<Subset>, Boolean> get() {
				return getSubsetGenerationDecisionMaker();
			}

			@Override
			public String getName() {
				return "Subset Limiter";
			}

			@Override
			public String getDescription() {
				return "Tells when the subsets generated are enough to pass " +
						"to the solution combination step.";
			}
		});
		
        /************\
         * Measures *
        \************/
    	
		measureManager.setPushMeasure(lastSeedSolutionUsed.getName(), lastSeedSolutionUsed);
		measureManager.setPushMeasure(lastTrialSolutionsGenerated.getName(), lastTrialSolutionsGenerated);
		measureManager.setPushMeasure(lastEnhancedSolutionsGenerated.getName(), lastEnhancedSolutionsGenerated);
		measureManager.setPushMeasure(lastReferenceSetUpdate.getName(), lastReferenceSetUpdate);
		measureManager.setPushMeasure(lastSubsetsGenerated.getName(), lastSubsetsGenerated);
		measureManager.setPushMeasure(lastCombinedSolutionsGenerated.getName(), lastCombinedSolutionsGenerated);
		measureManager.setPushMeasure(phaseMeasure.getName(), phaseMeasure);
		measureManager.setPushMeasure(iteration.getName(), iteration);
	    
		MeasureFactory factory = new MeasureFactory();
		measureManager.setPullMeasure(lastReferenceSetUpdate.getName(), factory.createPullFromPush(lastReferenceSetUpdate, null));
		measureManager.setPullMeasure(phaseMeasure.getName(), factory.createPullFromPush(phaseMeasure, Phase.STOP));
		measureManager.setPullMeasure(iteration.getName(), iteration);
		
	}
    
	@Override
	public ParameterManager getParameterManager() {
		return parameterManager;
	}
	
	@Override
	public MeasureManager getMeasureManager() {
		return measureManager;
	}
	
	@Override
	public ReferenceSet getResult() {
		String key = lastReferenceSetUpdate.getName();
		PullMeasure<ReferenceSet> referenceSetMeasure = measureManager.<ReferenceSet> getPullMeasure(key);
		return referenceSetMeasure.get();
	}
    
    /*************\
     * Algorithm *
    \*************/

	@Override
    public void run() {
        /*****************\
         * Initial Phase *
        \*****************/
    	
        LinkedList<S> seedSolutions = new LinkedList<S>(getSeedSolutions());
        ReferenceSet referenceSet = getReferenceSetBuilder().execute(getReferenceSetLimit());
        lastReferenceSetUpdate.push(referenceSet);
        int enhancedTrialSolutionsCounter = 0;
    	phaseMeasure.push(Phase.INIT);
        do {
            S seedSolution = seedSolutions.removeFirst();
            lastSeedSolutionUsed.push(seedSolution);
            
            Collection<S> trialSolutions = diversificationGenerator.execute(seedSolution);
            lastTrialSolutionsGenerated.push(trialSolutions);
            
            for(S trialSolution : trialSolutions) {
                Collection<S> enhancedTrialSolutions = improvementMethod.execute(trialSolution);
                lastEnhancedSolutionsGenerated.push(enhancedTrialSolutions);
                
                referenceSet.feed(getReferenceSetLimit(), enhancedTrialSolutions);
                lastReferenceSetUpdate.push(referenceSet);
                
                seedSolutions.addAll(enhancedTrialSolutions);
                enhancedTrialSolutionsCounter += enhancedTrialSolutions.size();
            }
        } while(enhancedTrialSolutionsCounter < getTotalNumberOfEnhancedTrialSolutions() && !seedSolutions.isEmpty());
        
        /************************\
         * Scatter Search Phase *
        \************************/
        
    	iteration.reset();
    	phaseMeasure.push(Phase.SEARCH);
        do {
        	iteration.increment();
        	
            Collection<Subset> subsets = new LinkedList<Subset>();
            do {
                Subset subset = subsetGenerationMethod.execute(referenceSet);
                subsets.add(subset);
            } while(shouldGenerateMoreSubsets(subsets));
            lastSubsetsGenerated.push(subsets);

            Collection<S> trialSolutions = new LinkedList<S>();
            for(Subset subset : subsets) {
                Collection<S> combinedSolutions = solutionCombinationMethod.execute(subset);
                lastCombinedSolutionsGenerated.push(combinedSolutions);
                
                trialSolutions.addAll(combinedSolutions);
            }

            for(S trialSolution : trialSolutions) {
                Collection<S> enhancedTrialSolutions = improvementMethod.execute(trialSolution);
                lastEnhancedSolutionsGenerated.push(enhancedTrialSolutions);
                
                referenceSet.feed(getReferenceSetLimit(), enhancedTrialSolutions);
                lastReferenceSetUpdate.push(referenceSet);
            }
        } while(iteration.get() < getIterationLimit());
        
    	phaseMeasure.push(Phase.STOP);
    }
}
