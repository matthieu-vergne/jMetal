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

package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.SolutionAttribute;

/**
 * Generic class for implementing {@link SolutionAttribute} classes. By default, the identifier
 * of a {@link SolutionAttribute} is the class name, but it can be set to a different value
 * when constructing an instance.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GenericSolutionAttribute <S extends Solution<?>, V> extends ReaderWriterSolutionAttribute<S, V>{

  /**
   * Constructor
   */
  @SuppressWarnings("unchecked")
  public GenericSolutionAttribute() {
    super(null, null);
    setAttributeReader((s) -> (V) s.getAttribute(getAttributeIdentifier()));
    setAttributeWriter((s,v) -> s.setAttribute(getAttributeIdentifier(), v));
  }

  /**
   * Constructor
   * @param id Attribute identifier
   */
  @SuppressWarnings("unchecked")
  public GenericSolutionAttribute(Object id) {
    super((s) -> (V) s.getAttribute(id), (s,v) -> s.setAttribute(id, v));
  }
}
