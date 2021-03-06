/*******************************************************************************
 * GenPlay, Einstein Genome Analyzer
 * Copyright (C) 2009, 2014 Albert Einstein College of Medicine
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * Authors: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *          Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *          Eric Bouhassira <eric.bouhassira@einstein.yu.edu>
 * 
 * Website: <http://genplay.einstein.yu.edu>
 ******************************************************************************/
package edu.yu.einstein.genplay.dataStructure.chromosomeWindow;

import java.io.Serializable;


/**
 * A window on a chromosome or a scaffold with a start and stop position (in bp).
 * The window interval is left closed, right open: [start, stop[
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public interface ChromosomeWindow extends Serializable, Comparable<ChromosomeWindow> {


	/**
	 * Checks if the window contains the given position.
	 * If the position is located before the window, -1 is returned.
	 * If the position is located after the window, 1 is returned.
	 * if the position is included in the window, 0 is returned.
	 * @param position the position to check
	 * @return 0 is the position is in the window, -1 if lower, 1 if higher.
	 */
	public int containsPosition(int position);


	/**
	 * @return the position of the middle of the window
	 */
	public double getMiddlePosition();


	/**
	 * @return the size of the window in base pair (ie: stop - start)
	 */
	public int getSize();


	/**
	 * @return the start position of the window
	 */
	public int getStart ();


	/**
	 * @return the stop position of the window
	 */
	public int getStop ();
}
