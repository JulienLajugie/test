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
package edu.yu.einstein.genplay.core.operation.geneList;

import java.util.concurrent.ExecutionException;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;


/**
 * Counts the number of genes on the specified chromosomes
 * @author Julien Lajugie
 * @author Nicolas Fourel
 * @version 0.1
 */
public class GLOCountAllGenes implements Operation<Long> {

	private final GeneList 	geneList;		// input GeneList
	private final boolean[] chromoList;		// 1 boolean / chromosome.
	// each boolean sets to true means that the corresponding chromosome is selected
	private boolean			stopped = false;// true if the operation must be stopped


	/**
	 * Creates an instance of {@link GLOCountAllGenes}.
	 * Counts the genes on the selected chromosomes of the {@link GeneList}.
	 * @param geneList input {@link GeneList}
	 * @param chromoList list of boolean. A boolean set to true means that the
	 * chromosome with the same index is going to be used for the calculation.
	 */
	public GLOCountAllGenes(GeneList geneList, boolean[] chromoList) {
		this.geneList = geneList;
		this.chromoList = chromoList;
	}



	@Override
	public Long compute() throws InterruptedException, ExecutionException {
		long total = 0;
		for (int i = 0; (i < geneList.size()) && !stopped; i++) {
			if (((chromoList == null) || ((i < chromoList.length) && (chromoList[i]))) && (geneList.get(i) != null)) {
				total += geneList.get(i).size();
			}
		}
		if (stopped) {
			return null;
		} else {
			return total;
		}
	}


	@Override
	public String getDescription() {
		return "Operation: Count Genes";
	}


	@Override
	public String getProcessingDescription() {
		return "Counting Non Null Windows";
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
