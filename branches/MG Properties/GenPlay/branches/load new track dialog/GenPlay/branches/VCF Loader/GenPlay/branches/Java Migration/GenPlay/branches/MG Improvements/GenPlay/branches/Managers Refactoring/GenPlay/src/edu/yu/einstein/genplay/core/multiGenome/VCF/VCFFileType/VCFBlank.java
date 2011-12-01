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
package edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFileType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.multiGenome.engine.Variant;
import edu.yu.einstein.genplay.core.multiGenome.engine.MGPosition;
import edu.yu.einstein.genplay.core.multiGenome.utils.FormattedMultiGenomeName;

/**
 * This class represent a VCF blank information.
 * A blank information allows genome to be synchronized with the other genomes.
 * It doesn't represent a concrete variant.
 * @author Nicolas Fourel
 * @version 0.1
 */
public class VCFBlank implements Variant {

	private static final long serialVersionUID = 1848765344167402377L;	// generated ID
	private static final int SAVED_FORMAT_VERSION_NUMBER = 0; 			// saved format version 

	private final static String DEFAULT_STRING_VALUE = "";
	
	private String 		fullGenomeName;				// The genome name
	private Chromosome 	chromosome;					// The related chromosome
	private int 		genomePosition;				// The genome position
	private int 		initialReferenceOffset;		// The offset between the genome position and the reference genome position
	private int 		initialMetaGenomeOffset;	// The offset between the genome position and the meta genome position
	private int 		extraOffset;				// Offset when multiple insertions happen at the same reference position
	private Integer 	length;
	
	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(fullGenomeName);
		out.writeObject(chromosome);
		out.writeInt(genomePosition);
		out.writeInt(initialReferenceOffset);
		out.writeInt(initialMetaGenomeOffset);
		out.writeInt(extraOffset);
		out.writeInt(length);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		fullGenomeName = (String) in.readObject();	
		chromosome = (Chromosome) in.readObject();
		genomePosition = in.readInt();
		initialReferenceOffset = in.readInt();
		initialMetaGenomeOffset = in.readInt();
		extraOffset = in.readInt();
		length = in.readInt();
	}

	
	/**
	 * Constructor of {@link VCFBlank}
	 * @param fullGenomeName 	the full genome name
	 * @param chromosome 		the chromosome
	 * @param length 			the length of the variation
	 */
	public VCFBlank (String fullGenomeName, Chromosome chromosome, int length) {
		this.fullGenomeName = fullGenomeName;
		this.chromosome = chromosome;
		this.length = length;
		initialReferenceOffset = 0;
		initialMetaGenomeOffset = 0;
		extraOffset = 0;
	}

	@Override
	public String getFullGenomeName() {
		return fullGenomeName;
	}

	@Override
	public void setFullGenomeName(String name) {
		fullGenomeName = name;
	}

	@Override
	public String getRawGenomeName() {
		try {
			return FormattedMultiGenomeName.getRawName(fullGenomeName);
		} catch (Exception e) {
			System.out.println(fullGenomeName);
			return null;
		}
	}

	@Override
	public String getUsualGenomeName() {
		return FormattedMultiGenomeName.getUsualName(fullGenomeName);
	}

	@Override
	public String getChromosomeName() {
		return chromosome.getName();
	}

	@Override
	public VariantType getType() {
		return VariantType.BLANK;
	}

	@Override
	public int getLength() {
		return length;
	}

	@Override
	public boolean isPhased() {
		return false;
	}

	@Override
	public boolean isOnFirstAllele() {
		return false;
	}

	@Override
	public boolean isOnSecondAllele() {
		return false;
	}

	@Override
	public int getGenomePosition() {
		return genomePosition;
	}

	@Override
	public int getNextGenomePosition() {
		int nextGenomePosition = genomePosition + 1;
		if (getType() == VariantType.INSERTION) {
			nextGenomePosition += getLength();
		}
		return nextGenomePosition;
	}

	@Override
	public int getReferenceGenomePosition() {
		int position = genomePosition + initialReferenceOffset;
		return position;
	}

	@Override
	public int getNextReferenceGenomePosition() {
		int position = getNextGenomePosition();
		return getNextReferenceGenomePosition(position);
	}

	@Override
	public int getNextReferenceGenomePosition(int position) {
		int current = getReferenceGenomePosition();
		int difference = position - genomePosition;
		if (getType() == VariantType.INSERTION) {
			if (difference > getLength()) {
				current += difference - getLength();
			} else {
				System.out.println("WARNING: difference < length");
			}
		} else {
			current += difference;
			if (getType() == VariantType.DELETION) {
				current += getLength();
			}
		}
		return current;
	}

	@Override
	public int getMetaGenomePosition() {
		int position = genomePosition + initialMetaGenomeOffset;
		return position;
	}

	@Override
	public int getNextMetaGenomePosition() {
		int position = getNextGenomePosition();
		return getNextMetaGenomePosition(position);
	}

	@Override
	public void setGenomePosition(int position) {
		genomePosition = position;
	}

	@Override
	public int getNextMetaGenomePosition(int position) {
		int current = getMetaGenomePosition() + (position - genomePosition);
		if (getType() != VariantType.INSERTION) {
			current += getLength();
		}
		if (position > (genomePosition + getLength())) {
			current += extraOffset;
		}
		return current;
	}

	@Override
	public int getExtraOffset() {
		return extraOffset;
	}

	@Override
	public int getInitialReferenceOffset() {
		return initialReferenceOffset;
	}

	@Override
	public int getNextReferencePositionOffset() {
		int nextGenomePosition = getNextGenomePosition();
		int nextReferencePosition = getNextReferenceGenomePosition(nextGenomePosition);
		return nextReferencePosition - nextGenomePosition;
	}

	@Override
	public int getInitialMetaGenomeOffset() {
		return initialMetaGenomeOffset;
	}

	@Override
	public int getNextMetaGenomePositionOffset() {
		int nextGenomePosition = getNextGenomePosition();
		int nextMetaGenomePosition = getNextMetaGenomePosition(nextGenomePosition);
		return nextMetaGenomePosition - nextGenomePosition;
	}

	@Override
	public void addExtraOffset(int offset) {
		this.extraOffset += extraOffset;
	}

	@Override
	public void setInitialReferenceOffset(int offset) {
		this.initialReferenceOffset = offset;
	}

	@Override
	public void setInitialMetaGenomeOffset(int offset) {
		this.initialMetaGenomeOffset = offset;
	}
	
	@Override
	public MGPosition getPositionInformation() {
		return null;
	}

	@Override
	public String getId() {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public String getReference() {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public String getAlternative() {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public Double getQuality() {
		return 100.0;
	}

	@Override
	public boolean getFilter() {
		return true;
	}

	@Override
	public String getInfo() {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public Object getInfoValue(String field) {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public String getFormat() {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public String getFormatValues () {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

	@Override
	public String getFormatValue(String field) {
		return VCFBlank.DEFAULT_STRING_VALUE;
	}

}