package org.uma.jmetal.util.weight;

public interface WeightingStrategy<Item, W extends Number> {
	public W weight(Item item);
}
