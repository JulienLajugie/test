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
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.SimpleScoredChromosomeWindow;


/**
 * Filters {@link ScoredChromosomeWindow} according to the length they are separated.
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public class SCWLOFilterWidth implements Operation<SCWList> {

	private final SCWList	scwList;		// input list
	private final int 		width;			// minimum width
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Adds a specified constant to the scores of each window of a {@link SimpleScoredChromosomeWindow}
	 * @param scwList input list
	 * @param width constant to add
	 */
	public SCWLOFilterWidth(SCWList scwList, int width) {
		this.scwList = scwList;
		this.width = width;
	}


	@Override
	public SCWList compute() throws Exception {
		if (width < 1) {
			return scwList;
		}

		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();
		final SCWListBuilder resultListBuilder = new SCWListBuilder(scwList);

		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(chromosome);

			Callable<Void> currentThread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					if ((currentList != null) && (currentList.size() != 0)) {
						ScoredChromosomeWindow currentWindow = currentList.get(0);
						ScoredChromosomeWindow previousWindow = currentWindow;
						List<ScoredChromosomeWindow> tmpList = new ArrayList<ScoredChromosomeWindow>();
						tmpList.add(currentWindow);
						for (int j = 1; (j < currentList.size()) && !stopped; j++) {
							currentWindow = currentList.get(j);
							int diff = currentWindow.getStart() - previousWindow.getStop();
							if (diff > width) {
								ScoredChromosomeWindow windowToAdd = createWindowFromListToList(tmpList);
								resultListBuilder.addElementToBuild(chromosome, windowToAdd);
								tmpList = new ArrayList<ScoredChromosomeWindow>();
							}
							tmpList.add(currentWindow);
							previousWindow = currentWindow;
						}
						ScoredChromosomeWindow windowToAdd = createWindowFromListToList(tmpList);
						resultListBuilder.addElementToBuild(chromosome, windowToAdd);
					}
					// tell the operation pool that a chromosome is done
					op.notifyDone();
					return null;
				}
			};

			threadList.add(currentThread);
		}
		op.startPool(threadList);
		return resultListBuilder.getSCWList();
	}


	/**
	 * Gather the windows of a list into one window.
	 * @param mainList	the main list
	 * @param tmpList	the windows to group
	 * @return			the main list
	 */
	private ScoredChromosomeWindow createWindowFromListToList (List<ScoredChromosomeWindow> tmpList) {
		ScoredChromosomeWindow newWindow;
		if (tmpList.size() == 1) {
			newWindow = tmpList.get(0);
		} else {
			int start = tmpList.get(0).getStart();
			int stop = tmpList.get(tmpList.size() - 1).getStop();
			float score = getScore(tmpList);
			newWindow = new SimpleScoredChromosomeWindow(start, stop, score);
		}
		return newWindow;
	}


	@Override
	public String getDescription() {
		return "Operation: Filter too close windows, width = " + width;
	}



	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	private float getScore (List<ScoredChromosomeWindow> list) {
		return 1f;
	}


	@Override
	public int getStepCount() {
		return 1 + scwList.getCreationStepCount();
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
