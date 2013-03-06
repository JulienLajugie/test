/*******************************************************************************
 *     GenPlay, Einstein Genome Analyzer
 *     Copyright (C) 2009, 2011 Albert Einstein College of Medicine
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *     Authors:	Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     			Nicolas Fourel <nicolas.fourel@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.core.converter.maskListConverter;

import edu.yu.einstein.genplay.core.converter.Converter;
import edu.yu.einstein.genplay.dataStructure.list.ChromosomeListOfLists;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.MaskWindowList;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;


/**
 * Creates a {@link ScoredChromosomeWindowList} from the data of the input {@link MaskWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MaskListToSCWList implements Converter {

	private final ScoredChromosomeWindowList 	list; 		// input list


	/**
	 * Creates a {@link ScoredChromosomeWindowList} from the data of the input {@link MaskWindowList}
	 * @param maskList input list
	 */
	public MaskListToSCWList(ScoredChromosomeWindowList maskList) {
		list = maskList;
	}


	@Override
	public String getDescription() {
		return "Operation: Generate Variable Window Track";
	}


	@Override
	public String getProcessingDescription() {
		return "Generating Variable Window Track";
	}


	@Override
	public void convert() throws Exception {
		// There is nothing to convert, the mask IS a variable window track with a score of 1
	}


	@Override
	public ChromosomeListOfLists<?> getList() {
		return list;
	}
}
