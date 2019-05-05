package org.uma.jmetal.solution.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.Solution;

// TODO test
@SuppressWarnings("serial")
public class SelfContainedSolution<T> implements Solution<T> {

	private final List<T> variables;
	private final List<Double> objectives;
	private final Map<Object, Object> attributes;

	public SelfContainedSolution(List<T> variables, List<Double> objectives, Map<Object, Object> attributes) {
		this.variables = variables;
		this.objectives = objectives;
		this.attributes = attributes;
	}

	@Override
	public void setObjective(int index, double value) {
		objectives.set(index, value);
	}

	@Override
	public double getObjective(int index) {
		return objectives.get(index);
	}

	@Override
	public double[] getObjectives() {
		return objectives.stream().mapToDouble(d -> d).toArray();
	}

	@Override
	public T getVariableValue(int index) {
		return variables.get(index);
	}

	@Override
	public List<T> getVariables() {
		return variables;
	}

	@Override
	public void setVariableValue(int index, T value) {
		variables.set(index, value);
	}

	@Override
	public String getVariableValueString(int index) {
		return getVariableValue(index).toString();
	}

	@Override
	public int getNumberOfVariables() {
		return variables.size();
	}

	@Override
	public int getNumberOfObjectives() {
		return objectives.size();
	}

	@Override
	public Solution<T> copy() {
		return new SelfContainedSolution<>(new ArrayList<>(variables), new ArrayList<>(objectives),
				new HashMap<>(attributes));
	}

	@Override
	public void setAttribute(Object id, Object value) {
		attributes.put(id, value);
	}

	@Override
	public Object getAttribute(Object id) {
		return attributes.get(id);
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}

}
