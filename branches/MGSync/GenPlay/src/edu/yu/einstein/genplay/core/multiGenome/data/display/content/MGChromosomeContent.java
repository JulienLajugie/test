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
package edu.yu.einstein.genplay.core.multiGenome.data.display.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGByteArray;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGFloatArray;
import edu.yu.einstein.genplay.core.multiGenome.data.display.array.MGIntegerArray;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class MGChromosomeContent implements Iterable<MGLineContent> {

	private final Chromosome chromosome;
	private final MGIntegerArray positions;
	private final MGFloatArray scores;
	private final List<MGIntegerArray> alternatives;
	private final Map<String, List<MGByteArray>> genotypes;
	private MGChromosomeVariants variants;


	/**
	 * Constructor of {@link MGChromosomeContent}
	 * @param chromosome
	 * @param genomeNames
	 */
	public MGChromosomeContent (Chromosome chromosome, List<String> genomeNames) {
		this.chromosome = chromosome;
		positions = new MGIntegerArray();
		scores = new MGFloatArray();
		alternatives = new ArrayList<MGIntegerArray>();
		alternatives.add(new MGIntegerArray());
		genotypes = new HashMap<String, List<MGByteArray>>();
		for (String genomeName: genomeNames) {
			genotypes.put(genomeName, new ArrayList<MGByteArray>());
			genotypes.get(genomeName).add(new MGByteArray());
		}
		variants = null;
	}



	/**
	 * Add a {@link MGLineContent} into the file content structure
	 * @param index		index to insert the {@link MGLineContent}
	 * @param position	the {@link MGLineContent} to insert
	 */
	public void addPosition (int index, MGLineContent position) {
		positions.set(index, position.getReferenceGenomePosition());
		scores.set(index, position.getScore());
		for (int i = 0; i < position.getAlternatives().length; i++) {
			addAlternative(i, index, position.getAlternatives()[i]);
		}

		List<String> genomes = new ArrayList<String>(position.getGenotypes().keySet());
		for (String genome: genomes) {
			addGenotype(index, genome, position.getGenotypes().get(genome));
		}
	}


	/**
	 * Add the length of an alternative into the file content structure
	 * @param alternativeIndex	the index of the alternative
	 * @param positionIndex		the index of position where to set the alternative
	 * @param alternative		the length of the alternative
	 */
	private void addAlternative (int alternativeIndex, int positionIndex, int alternative) {
		int add = (alternativeIndex - alternatives.size()) + 1;
		for (int i = 0; i < add; i++) {
			alternatives.add(new MGIntegerArray());
		}
		alternatives.get(alternativeIndex).set(positionIndex, alternative);
	}


	/**
	 * Add a genotype into the file content structure
	 * @param positionIndex	the index of position where to set the genotype
	 * @param genomeName	the name of the genome the genotype belongs to
	 * @param genotype		the genotype
	 */
	private void addGenotype (int positionIndex, String genomeName, byte[] genotype) {
		for (int i = 0; i < genotype.length; i++) {
			int add = (i - genotypes.get(genomeName).size()) + 1;
			for (int j = 0; j < add; j++) {
				genotypes.get(genomeName).add(new MGByteArray());
			}
			genotypes.get(genomeName).get(i).set(positionIndex, genotype[i]);
		}
	}


	/**
	 * @param index index of the {@link MGLineContent}
	 * @return the {@link MGLineContent} for the given index
	 */
	public MGLineContent getPosition (int index) {
		MGLineContent position = new MGLineContent();
		return getPosition(position, index);
	}


	/**
	 * @param position a {@link MGLineContent} to update
	 * @param index index of the {@link MGLineContent}
	 * @return the {@link MGLineContent} for the given index
	 */
	public MGLineContent getPosition (MGLineContent position, int index) {
		position.setReferenceGenomePosition(positions.get(index));
		position.setScore(scores.get(index));
		position.setAlternatives(getAlternatives(index));
		position.setGenotypes(getGenotypes(index));
		return position;
	}


	/**
	 * @param index index of the alternatives
	 * @return an array of alternatives length
	 */
	private int[] getAlternatives (int index) {
		List<Integer> result = new ArrayList<Integer>();
		for (MGIntegerArray alternative: alternatives) {
			if (alternative.get(index) != MGLineContent.NO_ALTERNATIVE) {
				result.add(alternative.get(index));
			}
		}

		int[] array = new int[result.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = result.get(i);
		}

		return array;
	}


	/**
	 * @param index index of the genotypes
	 * @return the map of genotypes
	 */
	private Map<String, byte[]> getGenotypes (int index) {
		Map<String, byte[]> genotypes = new HashMap<String, byte[]>();
		for (String genomeName: this.genotypes.keySet()) {
			List<MGByteArray> byteArrays = this.genotypes.get(genomeName);
			byte[] array = new byte[byteArrays.size()];
			for (int i = 0; i < array.length; i++) {
				array[i] = byteArrays.get(i).getByte(index);
			}
			genotypes.put(genomeName, array);
		}
		return genotypes;
	}


	/**
	 * Compact all lists resizing arrays for better memory usage
	 */
	public void compact () {
		positions.compact();
		int size = positions.size();
		scores.resize(size);
		for (MGIntegerArray alternative: alternatives) {
			alternative.resize(size);
		}
		for (String genomeName: this.genotypes.keySet()) {
			for (MGByteArray genotype: genotypes.get(genomeName)) {
				genotype.resize(size);
			}
		}
	}


	/**
	 * Generates the variants based on {@link MGChromosomeContent} information.
	 */
	public void generateVariants () {
		variants = new MGChromosomeVariants();
		variants.generateVariants(this);
	}


	/**
	 * Removes the variants
	 */
	public void removeVariants () {
		variants = null;
	}



	/**
	 * @return file content size (number of position)
	 */
	public int getSize () {
		return positions.size();
	}


	/**
	 * @param index	position index on the list
	 * @return the score for the given index, -1 otherwise
	 */
	public float getScore (int index) {
		if (index < getSize()) {
			return scores.get(index);
		}
		return -1;
	}


	/**
	 * @return the maximum number of alternatives found in a line
	 */
	public int getMaxAlternativeNumber () {
		return alternatives.size();
	}


	/**
	 * Shows file content
	 */
	public void show () {
		String info = "";

		int size = positions.size();
		for (int i = 0; i < size; i++) {
			info += getPosition(i).toString() + "\n";
		}

		System.out.println(info);

		if (variants != null) {
			variants.show();
		}
	}


	/**
	 * @return the chromosome
	 */
	public Chromosome getChromosome() {
		return chromosome;
	}


	/**
	 * @return the positions
	 */
	public MGIntegerArray getPositions() {
		return positions;
	}


	/**
	 * @return the scores
	 */
	public MGFloatArray getScores() {
		return scores;
	}


	/**
	 * @return the alternatives
	 */
	public List<MGIntegerArray> getAlternatives() {
		return alternatives;
	}


	@Override
	public Iterator<MGLineContent> iterator() {
		return new ChromosomeContentIterator(this);
	}


	/**
	 * @return the variants
	 */
	public MGChromosomeVariants getVariants() {
		return variants;
	}

}