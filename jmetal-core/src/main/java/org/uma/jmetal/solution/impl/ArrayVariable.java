package org.uma.jmetal.solution.impl;

import org.uma.jmetal.solution.Variable;

public class ArrayVariable<Value> implements Variable<Value[], Value> {

	private final int index;

	public ArrayVariable(int index) {
		this.index = index;
	}

	@Override
	public Value readFrom(Value[] solution) {
		return solution[index];
	}

}
