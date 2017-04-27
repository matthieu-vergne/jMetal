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

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Objective;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ObjectiveDominanceComparator<S> implements Comparator<S>, Serializable {
  private ConstraintViolationComparator<S> constraintViolationComparator;
  private Collection<Objective<S, Double>> objectives;

  /** Constructor */
  public ObjectiveDominanceComparator(Collection<Objective<S, Double>> objectives) {
    this(new OverallConstraintViolationComparator<S>(), objectives) ;
  }

  /** Constructor */
  public ObjectiveDominanceComparator(ConstraintViolationComparator<S> constraintComparator, Collection<Objective<S, Double>> objectives) {
    this.constraintViolationComparator = constraintComparator ;
    this.objectives = new LinkedList<>(objectives);
  }
  
  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are
   * non-dominated, or solution1  is dominated by solution2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    if (solution1 == null) {
      throw new JMetalException("Solution1 is null") ;
    } else if (solution2 == null) {
      throw new JMetalException("Solution2 is null") ;
    }
    int result ;
    result = constraintViolationComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = dominanceTest(solution1, solution2) ;
    }

    return result ;
  }

  private int dominanceTest(S solution1, S solution2) {
    int bestIsOne = 0 ;
    int bestIsTwo = 0 ;
    int result ;
    for (Objective<S, Double> objective : objectives) {
      double value1 = objective.readFrom(solution1);
      double value2 = objective.readFrom(solution2);
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    if (bestIsOne > bestIsTwo) {
      result = -1;
    } else if (bestIsTwo > bestIsOne) {
      result = 1;
    } else {
      result = 0;
    }
    return result ;
  }
}
