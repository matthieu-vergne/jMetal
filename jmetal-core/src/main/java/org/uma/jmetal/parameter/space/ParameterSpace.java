package org.uma.jmetal.parameter.space;

/**
 * A {@link ParameterSpace} aims at describing a space of {@link Value}s. As
 * such, depending on whether a {@link Value} pertains to the
 * {@link ParameterSpace} or not, the method {@link #contains(Object)} should
 * return <code>true</code> or <code>false</code>.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public interface ParameterSpace<Value> {
	/**
	 * 
	 * @param value
	 *            the {@link Value} to check
	 * @return <code>true</code> if the {@link Value} pertains to this
	 *         {@link ParameterSpace}, <code>false</code> otherwise
	 */
	public boolean contains(Value value);

	public ParameterSpaceBrowser<Value> getBrowser();
}
