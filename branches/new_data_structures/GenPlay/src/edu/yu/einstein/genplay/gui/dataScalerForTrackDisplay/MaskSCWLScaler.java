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
package edu.yu.einstein.genplay.gui.dataScalerForTrackDisplay;

import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.genomeWindow.GenomeWindow;
import edu.yu.einstein.genplay.dataStructure.list.chromosomeWideList.SCWListView.mask.MaskListViewBuilder;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.exception.ExceptionManager;
import edu.yu.einstein.genplay.exception.exceptions.InvalidChromosomeException;
import edu.yu.einstein.genplay.util.ChromosomeWindowLists;


/**
 * This class scales a {@link SCWList} of masks to be displayed on a track.
 * @author Julien Lajugie
 */
public class MaskSCWLScaler implements DataScalerForTrackDisplay<SCWList, ListView<ScoredChromosomeWindow>> {

	/** scaled chromosome */
	private Chromosome scaledChromosome;

	/** Scaled xRatio (ratio between the track width and the displayed genome window width) */
	private double scaledXRatio;

	/** The SCW list scaled for a specified chromosome and xRatio */
	private ListView<ScoredChromosomeWindow> scaledSCWList;

	/** Data to be scaled for track display */
	private final SCWList dataToScale;


	/**
	 * Creates an instance of {@link MaskSCWLScaler}
	 * @param dataToScale the data that needs to be scaled
	 */
	public MaskSCWLScaler(SCWList dataToScale) {
		this.dataToScale = dataToScale;
	}


	@Override
	public ListView<ScoredChromosomeWindow> getDataScaledForTrackDisplay() {
		GenomeWindow projectWindow = ProjectManager.getInstance().getProjectWindow().getGenomeWindow();
		double projectXRatio = ProjectManager.getInstance().getProjectWindow().getXRatio();
		// if the chromosome or the xRatio of the project window changed we need to rescale the data
		if (!projectWindow.getChromosome().equals(scaledChromosome) || (projectXRatio != scaledXRatio)) {
			scaledChromosome = projectWindow.getChromosome();
			scaledXRatio = projectXRatio;
			scaleChromosome();
		}
		if ((scaledSCWList == null) || (scaledSCWList.size() == 0)) {
			return null;
		}
		return ChromosomeWindowLists.sublist(scaledSCWList, projectWindow.getStart(), projectWindow.getStop());
	}


	@Override
	public SCWList getDataToScale() {
		return dataToScale;
	}


	/**
	 * Merges two windows together if the gap between the two windows is not visible
	 * at the current zoom level
	 */
	private void scaleChromosome() {
		ListView<ScoredChromosomeWindow> currentChromosomeList;
		scaledSCWList = null;
		try {
			currentChromosomeList = dataToScale.get(scaledChromosome);
		} catch (InvalidChromosomeException e) {
			ExceptionManager.getInstance().caughtException(e);
			scaledChromosome = null;
			return;
		}
		if ((currentChromosomeList == null) || currentChromosomeList.isEmpty()) {
			return;
		}
		if (scaledXRatio >= 1) {
			scaledSCWList = currentChromosomeList;
		} else {
			// compute the width on the genome that takes up 1 pixel on the screen
			double pixelGenomicWidth = 1 / scaledXRatio;
			MaskListViewBuilder maskLVBuilder = new MaskListViewBuilder();
			int i = 0;
			while (i < currentChromosomeList.size()) {
				int currentStart = currentChromosomeList.get(i).getStart();
				int currentStop = currentChromosomeList.get(i).getStop();
				// we merge two windows together if there is a next window
				// and if the gap between the current window and the next one is smaller than 1 pixel
				while (((i + 1) < currentChromosomeList.size())
						&& ((currentChromosomeList.get(i + 1).getStart() - currentStop) < pixelGenomicWidth)) {
					i++;
					// the new stop is the one of the next window
					currentStop = currentChromosomeList.get(i).getStop();
				}
				maskLVBuilder.addElementToBuild(currentStart, currentStop);
				i++;
			}
			scaledSCWList = maskLVBuilder.getListView();
		}
	}
}
