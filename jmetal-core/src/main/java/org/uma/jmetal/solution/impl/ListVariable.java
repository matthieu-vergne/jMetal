package org.uma.jmetal.solution.impl;

import java.util.List;

import org.uma.jmetal.solution.Variable;

public class ListVariable<Value> implements Variable<List<Value>, Value> {

	private final int index;

	public ListVariable(int index) {
		this.index = index;
	}

	@Override
	public Value readFrom(List<Value> solution) {
		return solution.get(index);
	}

}
