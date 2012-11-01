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
package edu.yu.einstein.genplay.core.list.geneList.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.Gene;
import edu.yu.einstein.genplay.core.list.geneList.GeneList;
import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.core.operationPool.OperationPool;



/**
 * Computes the sum of the scores of a {@link GeneList}
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GLOSumScore implements Operation<Double> {

	private final GeneList 	geneList;		// input GeneList
	private final boolean[] chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean				stopped = false;// true if the operation must be stopped


	/**
	 * Computes the sum of the scores of a specified {@link GeneList}
	 * @param geneList input {@link GeneList}
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public GLOSumScore(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}


	@Override
	public Double compute() throws InterruptedException, ExecutionException {
		final OperationPool op = OperationPool.getInstance();
		final Collection<Callable<Double>> threadList = new ArrayList<Callable<Double>>();
		for (int i = 0; i < geneList.size(); i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (geneList.get(i) != null)) {
				final List<Gene> currentList = geneList.get(i);

				Callable<Double> currentThread = new Callable<Double>() {
					@Override
					public Double call() throws Exception {
						// we set the max to the smallest double value
						double sum = 0;
						for (int i = 0; (i < currentList.size()) && !stopped; i++) {
							Gene currentGene = currentList.get(i);
							if ((currentGene != null) && (currentGene.getExonScores() != null)) {
								for (Double currentScore: currentGene.getExonScores()) {
									if (currentScore != 0) {
										sum += currentScore;
									}
								}
							}
						}
						// tell the operation pool that a chromosome is done
						op.notifyDone();
						return sum;
					}
				};

				threadList.add(currentThread);
			}
		}

		List<Double> result = op.startPool(threadList);
		if (result == null) {
			return null;
		}
		// sum the result of each chromosome
		double total = 0;
		for (Double currentTotal: result) {
			total += currentTotal;
		}
		return total;
	}


	@Override
	public String getDescription() {
		return "Operation: Sum Score";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public String getProcessingDescription() {
		return "Computing Score Count";
	}


	@Override
	public void stop() {
		this.stopped = true;
	}
}
