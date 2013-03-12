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
package edu.yu.einstein.genplay.core.operation.SCWList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;
import edu.yu.einstein.genplay.dataStructure.list.SCWList.ScoredChromosomeWindowList;
import edu.yu.einstein.genplay.dataStructure.scoredChromosomeWindow.ScoredChromosomeWindow;



/**
 * Counts the number of windows in a {@link ScoredChromosomeWindowList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class SCWLOWindowCount implements Operation<Long>{

	private final ScoredChromosomeWindowList scwList;	// input list
	private final boolean[] chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Computes the sum of the lengths (in bp) of the non-null (different from 0) windows
	 * @param scwList input list
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public SCWLOWindowCount(ScoredChromosomeWindowList scwList, boolean[] chromoList) {
		this.scwList = scwList;
		this.chromoList = chromoList;
	}


	@Override
	public Long compute() throws Exception {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Long>> threadList = new ArrayList<Callable<Long>>();
		for (int i = 0; i < scwList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (scwList.getView(i) != null) && !stopped) {
				final List<ScoredChromosomeWindow> currentList = scwList.getView(i);

				Callable<Long> currentThread = new Callable<Long>() {
					@Override
					public Long call() throws Exception {
						long count = currentList.size();

						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return count;
					}
				};

				threadList.add(currentThread);
			}
		}
		List<Long> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}

		// sum the result of each chromosome
		long total = 0;
		for (Long currentCount: result) {
			total += currentCount;
		}
		return total;
	}

	@Override
	public String getDescription() {
		return "Operation: Count Windows";
	}


	@Override
	public String getProcessingDescription() {
		return "Counting Windows";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
	}
}
