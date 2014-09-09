//  ObjectiveComparator.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetalMatth.util.comparator;

import java.util.Comparator;

import org.uma.jmetalMatth.core.Solution;
import org.uma.jmetalMatth.util.encoding.ObjectiveBasedEncoding;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based on a objective values.
 */
public class ObjectiveComparator<Encoding extends ObjectiveBasedEncoding>
		implements Comparator<Solution<?, Encoding>> {

	/**
	 * Stores the index of the objective to compare
	 */
	private int nObj;
	private boolean ascendingOrder;

	/**
	 * Constructor.
	 * 
	 * @param nObj
	 *            The index of the objective to compare
	 */
	public ObjectiveComparator(int nObj) {
		this.nObj = nObj;
		ascendingOrder = true;
	}

	public ObjectiveComparator(int nObj, boolean descendingOrder) {
		this.nObj = nObj;
		ascendingOrder = !descendingOrder;
	}

	/**
	 * Compares two solutions.
	 * 
	 * @param o1
	 *            Object representing the first <code>Solution</code>.
	 * @param o2
	 *            Object representing the second <code>Solution</code>.
	 * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
	 *         respectively.
	 */
	@Override
	public int compare(Solution<?, Encoding> o1, Solution<?, Encoding> o2) {
		if (o1 == null) {
			return 1;
		} else if (o2 == null) {
			return -1;
		}

		double objetive1 = o1.getEncoding().getObjective(this.nObj);
		double objetive2 = o2.getEncoding().getObjective(this.nObj);
		if (ascendingOrder) {
			if (objetive1 < objetive2) {
				return -1;
			} else if (objetive1 > objetive2) {
				return 1;
			} else {
				return 0;
			}
		} else {
			if (objetive1 < objetive2) {
				return 1;
			} else if (objetive1 > objetive2) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}
