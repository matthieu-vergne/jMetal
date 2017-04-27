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

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.uma.jmetal.util.solutionattribute.SolutionAttribute;

/**
 * Generic class for implementing {@link SolutionAttribute} classes. By default, the identifier
 * of a {@link SolutionAttribute} is the class name, but it can be set to a different value
 * when constructing an instance.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ReaderWriterSolutionAttribute <S, V> implements SolutionAttribute<S, V>{
  private Object identifier;
  private Function<S, V> attributeReader;
  private BiConsumer<S, V> attributeWriter;

  /**
   * Constructor
   */
  public ReaderWriterSolutionAttribute(Function<S, V> attributeReader, BiConsumer<S, V> attributeWriter) {
    identifier = this.getClass() ;
    this.attributeReader = attributeReader ;
    this.attributeWriter = attributeWriter ;
  }

  /**
   * Constructor
   * @param id Attribute identifier
   */
  public ReaderWriterSolutionAttribute(Object id, Function<S, V> attributeReader, BiConsumer<S, V> attributeWriter) {
    this.identifier = id ;
    this.attributeReader = attributeReader ;
    this.attributeWriter = attributeWriter ;
  }
  
  public Function<S, V> getAttributeReader() {
    return attributeReader;
  }
  
  /**
   * Setter usable only by extensions for cases where they cannot provide the
   * reader/writer to the constructor.
   */
  protected void setAttributeReader(Function<S, V> attributeReader) {
    this.attributeReader = attributeReader;
  }
  
  public BiConsumer<S, V> getAttributeWriter() {
    return attributeWriter;
  }
  
  /**
   * Setter usable only by extensions for cases where they cannot provide the
   * reader/writer to the constructor.
   */
  protected void setAttributeWriter(BiConsumer<S, V> attributeWriter) {
    this.attributeWriter = attributeWriter;
  }

  @Override
  public V getAttribute(S solution) {
    return attributeReader.apply(solution);
  }

  @Override
  public void setAttribute(S solution, V value) {
     attributeWriter.accept(solution, value);
  }

  @Override
  public Object getAttributeIdentifier() {
    return identifier;
  }
}
