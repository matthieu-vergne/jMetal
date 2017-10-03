Critics of jMetal 5.2 Experiments
===========================

# Modules: Mix Experiment with Implementation

Experiments are currently organized in two locations:
- the module `core` (package `org.uma.jmetal.util.experiment`) provides support for storing experiment data and executing various operations on them ;
- the module `exec` (package `org.uma.jmetal.experiment`) provides examples of experiment implementations which reuse core elements.

Such a dispatch is arguable, because experiments are not used to implement or execute algorithms. Thus, even as utilitary features, it appears that **experiments are out of scope for the `core` module**. Indeed, this module is always required for jMetal users, and someone who only wants to execute specific implementations of an algorithm would need the modules `core` and `algorithm`. However, it would come with all the tools designed for running experiments, which only apply to experimental (and more generally evaluation) settings, not in the mere use of algorithms. Moreover, considering the research focus of jMetal, one should not forget that experiments are a fundamental part of research activities, and one which requires a high level of rigor to ensure reliability. In such a context, supporting experiments design and execution **should lead to a thorough analysis and to the implementation of a significant amount of tools** to cope with various relevant contexts. These two observations lead to think that a dedicated module seems more adapted.

# Architecture: Few Factored Pieces and Manual Implementation

The current architecture has clearly emerged from a bottom-up factoring, in the sense that **an experiment is designed through a specific implementation** in the `exec` module, while the `core` classes only provide structures and operations to reuse within these implementations. This is rather limited because their is **no concrete support for full experiments**, which means that any experiment, even a trivial one, requires a complete process to be written again. Reproducing experiments with few changes (e.g. a different algorithm, a different problem, an additional quality indicator) thus usually passes through [copy-pasting](https://en.wikipedia.org/wiki/Copy_and_paste_programming) an existing implementation, which shows a lack of code factoring. The fact that each implementation describes a rather similar procedure is a hint that more factoring can be done and a proper framework can be implemented.

# Design: Few, Confusing Concepts and Relationships

The code factored in the `core` module splits into several elements:
- `Experiment` which describes an experiment configuration and depends on an `ExperimentBuilder` to be instantiated ;
- `ExperimentBuilder` which helps building `Experiment` instances by introducing the setters that `Experiment` does not ;
- `ExperimentComponent` which defines an interface for runnable components, which are assumed to be used in conjunction with `Experiment` instances ;
- implementations of `ExperimentComponent` in the sub-package `component`:
	- to compute quality indicators
	- to execute algorithms
	- to generate Pareto fronts, LaTeX tables, and R scripts
	- which require `Experiment` through their constructors
- utilitary classes in the sub-package `util` to tag algorithms and problems and manage directories correspondingly.

The first confusing point comes from `Experiment`, because this term is usually used, even in the documentation of jMetal, to describe a procedure that evaluates one or several algorithms, while this class is only a data container for configuration. The documentation is explicit: an `Experiment` is a class "for describing the configuration of a jMetal experiment". If we consider a jMetal experiment to be a full evaluation procedure, then `Experiment` clearly implements a different concept. An `ExperimentConfiguration` class (or a `Configuration` class in the experiment package) may be clearer.

The second confusion comes from the corresponding builder, which in fact builds a configuration instance. However, not only the naming may be improved, but the very purpose of the builder is arguable: since `Experiment` is already a class which only contains data, one can already design setters and getters for each required property, making a builder useless. The fundamental difference is that `Experiment` mainly provides getters, not setters, and thus acts as an immutable configuration. Such a perspective can be motivated by an experiment execution having no authority in changing its own configuration, thus using an immutable instance internally. Such design would justify the use of a builder or factory to create the configuration before to provide it to an executor. However, `Experiment` also provides some setters to change reference Pareto fronts and algorithms, which makes no sense for a mere executor and lead to wonder about the actual responsibility of `Experiment`. This point has been more thoroughly discussed in issue #221, so we don't come back on that. At the end, `Experiment` and `ExperimentBuilder` should be removed or significantly redesigned, for example as a single configuration structure, or as a configuration abstraction offering getters (used in concrete experiments) and a builder to instantiate them.

A third confusing point is the presence of `ExperimentComponent`, which is merely equivalent to a `Runnable`, in the sense that it only requires a `run()` method, with no dependency on any experiment-related concept. The main difference with `Runnable` is its ability to throw an `IOException`, which otherwise would need to be catched inside. But because of the genericness of the interface, it is unclear why such an exception should be expected: `IOException` assumes interactions with external resources, like files, but having a `run()` method does not implies necessarily such interactions. On the sole purpose that it *might* be the case, why not throw any kind of exception? Who knows what specific experiments may need? However, whatever the exception here, using such an interface means that we have no clue about the underlying component, and thus we have no reason to catch such an exception, because we would not know what to do with it. In other words, this class seems to mix at the same time too much genericness (lack links with experiments) and too much specificness (requires an exception which might break the [interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle)). In fact, the sub-package `component` provides implementations of this interface which act as evidences to these arguments. First, each implementation requests an `Experiment` instance in its constructor, and such a systematicity makes arguable the lack of this concept in the common interface. Second, some implementations redefine the `run()` method without the exception, showing it is not a requirement. In short, this set of interface and classes may find more clarity and interest with a proper redesign.

To close on the `core` module, the presence of the two utilitary classes `ExperimentAlgorithm` and `ExperimentProblem` seem to highlight a lack of design again. Indeed, these two classes add a tag management, respectively for an `Algorithm` and a `Problem`, and thus would make sense as child classes or [decorators](https://en.wikipedia.org/wiki/Decorator_pattern). However, they would need to implement, respectively, `Algorithm` and `Problem` for that, but it is not the case. Such an extension might seem useless if they are dedicated to experiments, but if they are, then it seems heavy to implement full classes just to manage tags. Why not use a `Map` internally to associate a tag to each instance? Moreover, it is not that economical: actually, several core classes are impacted, leading them to require these tagged algorithms which are not `Algorithm` instances, and thus making them unable to deal with untagged instances even if it would be enough. Such a design challenges the original purpose of experiments, which is to evaluate `Algorithm` instances, while tags remain low level requirements for specific experiments. With such a design, extra computation is imposed on all the experiments, which breaks the [interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle) and the [open/closed principle](https://en.wikipedia.org/wiki/Open/closed_principle). Rather, these classes may disappear in order to delegate tag management to a more specific, experiment-related entity.

# Implementation: Small Support Based On Anti-Patterns

Finally, we can speak about concrete experiments, which are specific implementations in the `exec` module:
- `BinaryProblemsStudy` runs some algorithms on some binary problems, assesses their performance through 6 quality indicators, and generates LaTeX tables and R scripts ;
- `NSGAIIStudy` and `NSGAIIStudy2` run NSGA-II variants (different parameters) on some ZDT problems, assess their performance through 6 quality indicators, and generate LaTeX tables and R scripts ;
- `ZDTScalabilityIStudy` runs some algorithms on variants of the ZDT1 problem (increasing number of variables), assesses their performance through 6 quality indicators, and generates LaTeX tables and R scripts ;
- `ZDTStudy` and `ZDTStudy2` run some algorithms on some ZDT problems, assess their performance through 6 quality indicators, and generate LaTeX tables and R scripts.

The main issue with these experiments is their broken data flow. Each of them starts by providing the problems to solve and the algorithms to use to solve them. After that, a configuration instance (i.e. an `Experiment` instance) is generated, before to instantiate and run one after the other several `ExperimentComponent` implementations. At least two anti-patterns should be spotted here: one is the [God object](https://en.wikipedia.org/wiki/God_object) storing all the configuration data, and the other is the [sequential coupling](https://en.wikipedia.org/wiki/Sequential_coupling) between each component run. The first one has been already discussed in issue #221, so let's focus here on the second one.

The sequential coupling anti-pattern is due to the dependency between the calls of each `ExperimentComponent`: it makes no sense to run `ComputeQualityIndicators` if we did not run `ExecuteAlgorithms` or `GenerateReferenceParetoFront` yet. Such a process should be guaranteed rather than re-implemented every time we want to create an experiment, to avoid mistakes like swapping lines or missing one of them, which can be rather hard to detect if we did not implement the right exceptions. One way to go is to simply get rid of the `Experiment` container and directly request the relevant data as parameters of the component, while ensuring that each component returns something to exploit in the next steps instead of simply calling an independent `run()` method. This way, we cannot execute the quality indicators before to have the results of the algorithm. Another solution is to create a [template method pattern](https://en.wikipedia.org/wiki/Template_method_pattern), which would constrain the order of the steps and only ask for which component to run. This is the main argument towards implementing a framework rather than using factored pieces of code only. We may also think about a [pipeline](https://en.wikipedia.org/wiki/Pipeline_(software)), although not a linear one because some outputs should be provided to several components.

Another arguable design choice is the creation, from the start, of every instances of algorithms for each problem instance. Such a procedure seems rather primitive, because each algorithm is applied to each problem, and thus the redundant instantiation can be managed automatically and on demand, to ensure that only the required instances are generated. We may even think about reusing existing instances which have finished to solve their own problem, but such a feature would need further revisions which are out of scope for now (see issue #55, when speaking about replacing `Algorithm.run()` by `Algorithm.solve(problem)`).

# Summary

As a summary, the current experiment management has many flaws, and some significant redesign may be required. For evolution, the main decision is either to go for incremental fixes, or to restart from scratch. In this case, [it has been agreed](https://github.com/jMetal/jMetal/issues/221#issuecomment-323789371) that restarting from scratch would be a good idea, so let's go for that.

The jMetal Experiment Framework: jEF
======================================

# Concrete Implementations

## Current Procedures

Based on each implementation provided in the `exec` module, we can identify a common experiment procedure:
1. configure algorithms with problems
2. execute algorithms
3. compute quality indicators
4. generate Latex tables and R scripts

You already said that each step [must be executed in order](https://github.com/jMetal/jMetal/issues/221#issuecomment-323702768), but the actual need seems to me more to ensure something way more trivial: just that each component receives the relevant data, which means that this data should exist in the first place. This is normally the case in practice, because methods request parameters and thus cannot be called before you have all of them, but now the `Experiment` object centralizes everything and thus hide these dependencies, thus breaking the control at compile time, as presented in issue #221. To solve that, we only need to explicit the parameters to ensure that they are produced before the call. As long as components are executed to satisfy dependencies, there is no problem, and thus the order has some flexibility.

Once this point is clarified, this procedure still has its flaws in terms of continuous monitoring, as mentioned above, but it also has one clear advantage: the computation time during the execution phase is focused on the execution of the algorithms. When running a complete experiment on a single machine, we can minimize the impact of configuration/monitoring on the execution, and thus offer a more reliable evaluation of the performance of the algorithms. So although I consider that, *in general*, we don't need a strict order for these steps, I consider that it still has its utility *in some contexts*. So although the framework should not be bound to such an order, a specific support can be implemented for such a process.

## jEF Procedure: P<sup>3</sup> Experiment

Because I focus on isolating the execution of the algorithm, it means that I should consider only 3 steps:
1. *prepare* the execution
2. *perform* the execution to get raw data
3. *produce* the exploitable results from the raw data

Both computing intermediary results, like quality indicators, and terminal results, like tables and scripts, are included in the last step.

Then, based on what has been said above and on other comments in the issues, this procedure has several requirements:
1. guarantee that the execution step is executed alone, and thus that the 3 steps are executed in order
2. guarantee that each component of the third step (quality indicators, table generators, etc.) is executed only when all its relevant data is available
3. generate algorithms on demand to [ensure thread safety](https://github.com/jMetal/jMetal/issues/135#issuecomment-201230569)
4. work the same whether the execution is done internally (e.g. sequential, parallel with threads) or [externally](https://github.com/jMetal/jMetal/issues/135#issuecomment-202160624)

We will see in the following how we satisfy them in jEF.

### Requirement 1: Guarantee the order of the 3 steps

To satisfy this requirement, one may think about a [pipeline](https://en.wikipedia.org/wiki/Pipeline_(software)) of 3 units, one for each step. Such a structure is used for instance by [collection streams](https://docs.oracle.com/javase/tutorial/collections/streams/) introduced in Java 8. This is also a structure that I have seen in [GATE](https://gate.ac.uk/), which processes a corpus of texts to annotate them automatically in several phases. However, although a pipeline is well adapted to deal with successive, ordered processing, it processes a single piece of data through successive transformations, like successive collections for streams or successive annotated texts for GATE. Rather than transformations, one may speak more generally about successive operations: successive refinements of the data, like streams and GATE, or successive additions of data, like a [builder](https://en.wikipedia.org/wiki/Builder_pattern) creating a complex object, or successive extractions of data, which might be a quite peculiar use, or a composite. As a terminal operation, we can also consume the data for a specific purpose, without returning anything. Nevertheless, atomic steps are executed one after the other, and coming back to repeat the process in another way is not to be expected with a pipeline.

In this case, we may indeed design a pipeline in 3 steps, such that the first step *adds* algorithms to be executed, the second step *transforms* the list of algorithm into a dataset, and the last step *consumes* the dataset in various ways, but the support would be rather poor: the pipeline is short and we still remain with all the effort to do for designing each step, especially produce all the relevant algorithm instances of the first step and manage the execution of all the components of the third step. If these steps cannot be provided in one line, by using a predefined class or a Lambda expression, it does not seem really useful, but the point is that it seems hard to tell in such a restricted space all the algorithms to instantiate and how to apply each of them on a problem. It also seems hard to tell in one line which components to execute and their dependencies. Clearly it seems that each step needs an additional support, and thus the pipeline might be too limited.

Rather, we may use the [template method pattern](https://en.wikipedia.org/wiki/Template_method_pattern). Like the pipeline, it specifies a procedure to follow, but such a structure is intentionally "abstract", in the sense that it expects its various steps to be defined. Usually, one implements such a pattern through an abstract class. In our case, it looks like this:
```java
public abstract class PreparePerformProduceExperiment<Algorithms, Dataset> {
	protected Algorithms prepare();
	protected Dataset perform(Algorithms algorithms);
	protected void produce(Dataset dataset);
	
	public void run() {
		Algorithms algorithms = prepare();
		Dataset dataset = perform(algorithms);
		produce(dataset);
	}
}
```

The generics are not bounded to remain free of choosing whatever representation may fit. We may bound for example `Algorithms` to `Collection<? extends Algorithm<?>>`, but it is not required here, so we omit it and let child classes make such a bounding if needed. Indeed, one may prefer, instead of providing a collection of algorithm instances, provide a collection of instantiators, such that the algorithms are produced on demand. This is a way to produce an independent instance for each thread, and thus ensure thread safety, but we will come back on that later.

We may argue against this design the fact that we need to implement another class to define each of the 3 methods instead of using normal code, which has its impact on readability... and still does not support the details of each step. To fix the readability issue, we can instead use functional interfaces, which consists in defining dedicated interfaces to 

[standard functional interfaces](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html), introduced in Java 8.

TODO [builder](https://en.wikipedia.org/wiki/Builder_pattern) for building each step

Some experiments add an additional step, between 2 and 3, to generate some reference Pareto fronts. Because it is required only by some of them, we may consider 

we consider it to be a step of a lower level of abstraction, which should then be integrated within one of the 4 steps above, like a preparation of the computation of quality indicators. Indeed, adding a method for generating reference Pareto fronts here and let lower level implementations decide whether or not to implement it would increase the complexity of the high level procedure and impose useless programming and computation to implementations not needing them. In other words, it breaks the [interface segregation principle](https://en.wikipedia.org/wiki/Interface_segregation_principle). However, this 

Thus, we start from the 4-steps procedure above to identify the first procedure to support in our framework.



Reading this procedure, we can already wonder about practical issues: what happens when an algorithm is long to run? Should we wait to execute all of them before to start generating results? should we accept that crashing at step 2 after several hours of computation justifies the total absence of results? Wouldn't an experimenter appreciate to monitor quality indicators during execution, to stop it because of early hints that it is going wrong? These questions highlight that a sequential procedure is only an example of experiment design. Other experiments might exploit parallelism, where quality indicators are computed during the execution of the algorithms, and results generated on the fly. In particular, results may not be limited to LaTeX tables and R scripts, but also involve log files and GUI updates, which are typically filled on the fly and thus motivate online evaluations. Consequently, we consider the sequential procedure as a specific implementation of a more generic experiment workflow and we will try to identify such a workflow to cover also online evaluations.

Although this procedure describes the different steps to follow, it does not tell how they depend on each other, a required information to know where parallelism can be introduced.
(TODO)
1. configure the experiment
2. execute algorithms
3. compute quality indicators
4. generate Latex tables and R scripts

# P3 Experiments: Prepare-Perform-Produce



jMetal Experiment Framework
===========================

<!-- TODO: Summary -->

# Context of Use

<!-- TODO: Context -->
The jMetal Experiment Framework (jEF)

# Goal of the Framework

<!-- TODO: Goal -->

# How to Use?

<!-- TODO: Examples -->
