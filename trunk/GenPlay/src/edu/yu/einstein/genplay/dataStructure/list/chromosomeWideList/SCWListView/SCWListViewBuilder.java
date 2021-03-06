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
package edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView;

import edu.yu.einstein.genplay.dataStructure.list.listView.ListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.exceptions.ObjectAlreadyBuiltException;

/**
 * Interface defining {@link ListViewBuilder} for {@link ScoredChromosomeWindow} objects.
 * Adds an {@link #addElementToBuild(int, int, float)} method to avoid having to create a
 * temporary {@link ScoredChromosomeWindow} object to pass to the builders.
 * @author Julien Lajugie
 */
public interface SCWListViewBuilder extends ListViewBuilder<ScoredChromosomeWindow> {

	/**
	 * Adds an element to the ListView that will be built.
	 * To assure that ListView objects are immutable, this method
	 * will throw an exception if called after the getListView() has been called.
	 * Checks that the elements are added in start position order.
	 * @param start start position of the SCW to add
	 * @param stop stop position of the SCW to add
	 * @param score score value of the SCW to add
	 * @throws ObjectAlreadyBuiltException
	 */
	public void addElementToBuild(int start, int stop, float score) throws ObjectAlreadyBuiltException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SCWListViewBuilder clone();
}
