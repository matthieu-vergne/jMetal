package org.uma.jmetal.runner;

import java.util.Collection;
import java.util.LinkedList;

import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.PushMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.PullPushMeasure;
import org.uma.jmetal.parameter.configuration.Configuration;

/**
 * This {@link TimePerformanceAnalyser} aims at evaluating the time an algorithm
 * needs to reach a given level of quality.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Algo>
 */
public class TimePerformanceAnalyser<Algo extends Runnable, QualityValue extends Comparable<QualityValue>>
		implements Runnable {

	/**
	 * the algorithm to analyse.
	 */
	private final Algo algorithm;
	/**
	 * The quality {@link Measure} which will inform us about the quality
	 * reached so far by the algorithm.
	 */
	private final PullPushMeasure<QualityValue> qualityMeasure;
	/**
	 * The quality level to achieve before to stop the algorithm.
	 */
	private final QualityValue qualityGoal;
	/**
	 * The {@link Configuration} to apply in order to stop the algorithm.
	 */
	private final Configuration stopConfiguration;
	/**
	 * The {@link Configuration} to apply in order to run the algorithm.
	 */
	private final Configuration runConfiguration;
	/**
	 * The {@link DurationMeasure} used to measure the time spent by the
	 * algorithm to reach the quality goal.
	 */
	private final DurationMeasure durationMeasure;
	private final MeasureListener<QualityValue> qualityListener;

	/**
	 * 
	 * @param algorithm
	 *            the algorithm to analyse
	 * @param qualityMeasure
	 *            the {@link Measure} which sends a notification when the
	 *            current level of quality changes.
	 * @param qualityGoal
	 *            the quality value to reach before to stop the algorithm
	 * @param stopConfiguration
	 *            the {@link Configuration} to apply in order to stop the
	 *            algorithm
	 */
	public TimePerformanceAnalyser(final Algo algorithm,
			final PullPushMeasure<QualityValue> qualityMeasure,
			final QualityValue qualityGoal,
			final Configuration stopConfiguration,
			final Configuration runConfiguration) {
		if (algorithm == null) {
			throw new NullPointerException("No algorithm provided.");
		} else if (qualityMeasure == null) {
			throw new NullPointerException("No quality measure provided.");
		} else if (qualityGoal == null) {
			throw new NullPointerException("No quality goal provided.");
		} else if (stopConfiguration == null) {
			throw new NullPointerException("No stop configuration provided.");
		} else {
			this.algorithm = algorithm;
			this.qualityGoal = qualityGoal;
			this.qualityMeasure = qualityMeasure;
			this.stopConfiguration = stopConfiguration;
			this.runConfiguration = runConfiguration;

			durationMeasure = new DurationMeasure();
			qualityListener = new MeasureListener<QualityValue>() {

				@Override
				public void measureGenerated(QualityValue value) {
					durationMeasure.stop();
					if (value.compareTo(qualityGoal) >= 0) {
						stopConfiguration.applyAll();
					} else {
						// quality goal not reached yet
					}
					durationMeasure.start();
				}
			};
			qualityMeasure.register(qualityListener);
		}
	}

	protected void finalize() throws Throwable {
		qualityMeasure.unregister(qualityListener);
		super.finalize();
	};

	/**
	 * Equivalent to
	 * {@link #TimePerformanceAnalyser(Measurable, PullPushMeasure, Object, Configuration)}
	 * but for a quality {@link Measure} which is only a {@link PushMeasure}.
	 * The measure is transformed into a {@link PullPushMeasure} and the
	 * additional parameter providing the current value is used to initialize it
	 * (value returned until the next notification).
	 */
	public TimePerformanceAnalyser(Algo algorithm,
			PushMeasure<QualityValue> qualityMeasure,
			QualityValue currentValue, QualityValue qualityGoal,
			Configuration stopConfiguration, Configuration runConfiguration) {
		this(algorithm, new PullPushMeasure<>(qualityMeasure, currentValue),
				qualityGoal, stopConfiguration, runConfiguration);
	}

	@Override
	public void run() {
		final boolean[] isRunning = new boolean[3];
		final int analysis = 1;
		final int round = 2;
		Thread regularDisplayThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunning[analysis]) {
					try {
						wait();
						while (isRunning[round]) {
							System.out.println("So far: "
									+ qualityMeasure.get());
							wait(1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		regularDisplayThread.setDaemon(true);
		isRunning[analysis] = true;
		regularDisplayThread.start();

		Collection<Long> durations = new LinkedList<>();
		for (int i = 0; i < 100; i++) {
			runConfiguration.applyAll();

			// thread synchronization
			isRunning[round] = true;
			regularDisplayThread.notify();

			durationMeasure.reset();
			durationMeasure.start();
			algorithm.run();
			durationMeasure.stop();

			// thread synchronization
			isRunning[round] = false;
			regularDisplayThread.notify();

			Long currentDuration = durationMeasure.get();
			durations.add(currentDuration);

			double average = 0;
			for (long duration : durations) {
				average += duration;
			}
			average /= durations.size();
			System.out.println("Time spent in run " + i + ": "
					+ currentDuration + " (AVG: " + average + ")");
		}

		// thread synchronization
		isRunning[analysis] = false;
		regularDisplayThread.notify();
	}

	public Algo getAlgorithm() {
		return algorithm;
	}

	public PullPushMeasure<QualityValue> getQualityMeasure() {
		return qualityMeasure;
	}

	public QualityValue getQualityGoal() {
		return qualityGoal;
	}

	public Configuration getStopConfiguration() {
		return stopConfiguration;
	}

}
