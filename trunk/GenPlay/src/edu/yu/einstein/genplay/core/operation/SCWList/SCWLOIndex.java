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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.manager.project.ProjectChromosomes;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.chromosome.Chromosome;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWList;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.SCWList.SCWListBuilder;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;


/**
 * Indexes the scores of a {@link SCWList} based on
 * the greatest and the smallest value of the whole genome
 * @author Julien Lajugie
 */
public class SCWLOIndex implements Operation<SCWList> {

	private final SCWList 	scwList;			// list to index
	private final float 	newMin;				// new min after index
	private final float 	newMax;				// new max after index
	private boolean			stopped = false;	// true if the operation must be stopped


	/**
	 * Creates an instance of {@link SCWLOIndex}
	 * Indexes the scores between the specified minimum and maximum
	 * based on the greatest and the smallest value of the whole genome.
	 * @param scwList {@link SCWList} to index
	 * @param newMin minimum value after index
	 * @param newMax maximum value after index
	 */
	public SCWLOIndex(SCWList scwList, float newMin, float newMax) {
		this.scwList = scwList;
		this.newMin = newMin;
		this.newMax = newMax;
	}


	@Override
	public SCWList compute() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		ProjectChromosomes projectChromosomes = ProjectManager.getInstance().getProjectChromosomes();
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Void>> threadList = new ArrayList<Callable<Void>>();

		final float oldMin = scwList.getStatistics().getMinimum();
		final float oldMax = scwList.getStatistics().getMaximum();
		// We calculate the difference between the highest and the lowest value
		final float oldDistance = oldMax - oldMin;
		if (oldDistance == 0) {
			return null;
		}
		final float newDistance = newMax - newMin;
		final SCWListBuilder resultListBuilder = new SCWListBuilder(scwList);
		for (final Chromosome chromosome: projectChromosomes) {
			final ListView<ScoredChromosomeWindow> currentList = scwList.get(chromosome);
			Callable<Void> currentThread = new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					if (currentList != null) {
						// We index the intensities
						for (int j = 0; (j < currentList.size()) && !stopped; j++) {
							int start = currentList.get(j).getStart();
							int stop = currentList.get(j).getStop();
							float score = currentList.get(j).getScore();
							if (score != 0) {
								score = ((newDistance * (score - oldMin)) / oldDistance) + newMin;
							}
							resultListBuilder.addElementToBuild(chromosome, start, stop, score);
						}
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


	@Override
	public String getDescription() {
		return "Operation: Index Between " +  newMin + " and " + newMax;
	}


	@Override
	public String getProcessingDescription() {
		return "Indexing";
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
