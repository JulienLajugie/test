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

import java.util.Arrays;

import edu.yu.einstein.genplay.core.operation.Operation;
import edu.yu.einstein.genplay.dataStructure.gene.Gene;
import edu.yu.einstein.genplay.dataStructure.list.genomeWideList.geneList.GeneList;
import edu.yu.einstein.genplay.dataStructure.list.listView.ListView;


/**
 * Removes a percentage of genes with the greatest and smallest overall RPKM
 * @author Julien Lajugie
 */
public class GLOFilterPercentage implements Operation<GeneList> {

	private final GeneList 		geneList;			// {@link GeneList} to filter
	private final float 		lowPercentage;		// percentage of low values to filter
	private final float 		highPercentage;		// percentage of high values to filter
	private final boolean		isSaturation;		// true if we saturate, false if we remove the filtered values
	private boolean				stopped = false;	// true if the operation must be stopped
	private Operation<GeneList>	gloFilterThreshold;	// threshold filter that does the real fitering operation


	/**
	 * Creates an instance of {@link GLOFilterPercentage}
	 * @param geneList {@link GeneList} to filter
	 * @param lowPercentage percentage of low values to filter
	 * @param highPercentage percentage of high values to filter
	 * @param isSaturation true to saturate, false to remove the filtered values
	 */
	public GLOFilterPercentage(GeneList geneList, float lowPercentage, float highPercentage, boolean isSaturation) {
		this.geneList = geneList;
		this.lowPercentage = lowPercentage;
		this.highPercentage = highPercentage;
		this.isSaturation = isSaturation;
	}


	@Override
	public GeneList compute() throws Exception {
		if ((highPercentage < 0) || (highPercentage > 1) || (lowPercentage < 0) ||(lowPercentage > 1)) {
			throw new IllegalArgumentException("The percentage value must be between 0 and 1");
		}
		if ((lowPercentage + highPercentage) > 1) {
			throw new IllegalArgumentException("The sum of the low and high percentages value must be between 0 and 1");
		}
		boolean[] selectedChromo = new boolean[geneList.size()];
		Arrays.fill(selectedChromo, true);
		int totalLenght = new GLOCountNonNullGenes(geneList, selectedChromo).compute().intValue();
		if (totalLenght == 0) {
			return geneList;
		}
		float[] allScores = new float[totalLenght];
		int i = 0;
		for (ListView<Gene> currentList: geneList) {
			if (currentList != null) {
				for (int j = 0; (j < currentList.size()) && !stopped; j++) {
					float currentScore = currentList.get(j).getScore();
					if ((currentScore != Float.NaN) && (currentScore != 0)) {
						allScores[i] = currentScore;
						i++;
					}
				}
			}
		}
		int lowValuesCount = (int)(lowPercentage * allScores.length);
		int highValuesCount = (int)(highPercentage * allScores.length);
		Arrays.sort(allScores);

		float minValue = lowValuesCount == 0 ? Float.NEGATIVE_INFINITY : allScores[lowValuesCount - 1];
		float maxValue = highValuesCount == 0 ? Float.POSITIVE_INFINITY : allScores[allScores.length - highValuesCount];
		gloFilterThreshold = new GLOFilterThreshold(geneList, minValue, maxValue, isSaturation);
		return gloFilterThreshold.compute();
	}


	@Override
	public String getDescription() {
		String optionStr;
		if (isSaturation) {
			optionStr = ", option = saturation";
		} else {
			optionStr = ", option = remove";
		}
		return "Operation: Filter, " + (lowPercentage * 100) + "% smallest values, " + (highPercentage * 100) + "% greatest values" + optionStr;
	}


	@Override
	public String getProcessingDescription() {
		return "Filtering";
	}


	@Override
	public int getStepCount() {
		return 1;
	}


	@Override
	public void stop() {
		stopped = true;
		if (gloFilterThreshold != null) {
			gloFilterThreshold.stop();
		}
	}
}
