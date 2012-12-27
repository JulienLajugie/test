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
package edu.yu.einstein.genplay.core.multiGenome.data.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import edu.yu.einstein.genplay.core.chromosome.Chromosome;
import edu.yu.einstein.genplay.core.enums.VariantType;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFFile.VCFFile;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public abstract class Variant implements Serializable {

	/** Default serial version ID */
	private static final long serialVersionUID = 7940616840753151748L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;		// saved format version

	protected MGChromosomeContent chromosomeContent;
	protected int referencePositionIndex;
	protected int start;


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	protected void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(chromosomeContent);
		out.writeInt(referencePositionIndex);
		out.writeInt(start);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		chromosomeContent = (MGChromosomeContent) in.readObject();
		referencePositionIndex = in.readInt();
		start = in.readInt();
	}


	/**
	 * Constructor of {@link Variant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 */
	public Variant (MGChromosomeContent chromosomeContent, int referencePositionIndex) {
		this(chromosomeContent, referencePositionIndex, -1);
	}


	/**
	 * Constructor of {@link Variant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 */
	public Variant (MGChromosomeContent chromosomeContent, int referencePositionIndex, int start) {
		this.chromosomeContent = chromosomeContent;
		this.referencePositionIndex = referencePositionIndex;
		this.start = start;
	}


	/**
	 * @return the chromosomeContent
	 */
	public MGChromosomeContent getChromosomeContent() {
		return chromosomeContent;
	}


	/**
	 * @return the referencePositionIndex
	 */
	public int getReferencePositionIndex() {
		return referencePositionIndex;
	}


	/**
	 * @return the reference genome position
	 */
	public int getReferenceGenomePosition() {
		return chromosomeContent.getPositions().get(referencePositionIndex);
	}


	/**
	 * @return the score of the {@link Variant}
	 */
	public float getScore () {
		return chromosomeContent.getScore(referencePositionIndex);
	}


	/**
	 * @return the start position on the meta genome
	 */
	public int getStart () {
		return start;
	}


	/**
	 * @param start start position to set
	 */
	public void setStart (int start) {
		this.start = start;
	}


	/**
	 * @return the stop position on the meta genome
	 */
	public abstract int getStop ();


	/**
	 * @param stop stop position to set
	 */
	public abstract void setStop (int stop);


	/**
	 * @return the {@link VariantType} of the {@link Variant}
	 */
	public abstract VariantType getType ();


	/**
	 * @return a description of the {@link Variant}
	 */
	public String getDescription () {
		String description = "";
		description += "INDEX: " + referencePositionIndex + "; ";
		description += "REF: " + chromosomeContent.getPositions().get(referencePositionIndex) + "; ";
		description += "START: " + start + ";";
		return description;
	}


	/**
	 * @return the length of the variant
	 */
	public int getLength() {
		return getStop() - getStart();
	}


	/**
	 * @return the sequence of nucleotide for the variant
	 */
	public String getVariantSequence() {
		// TODO Code it!
		return ".";
	}


	/**
	 * @return the {@link VCFLine} of the variant, null if not found or for special {@link Variant} such as {@link MixVariant}
	 */
	public VCFLine getVCFLine() {
		if (chromosomeContent == null) {
			return null;
		}

		Chromosome chromosome = chromosomeContent.getChromosome();
		int referencePosition = getReferenceGenomePosition();
		VCFFile file = ProjectManager.getInstance().getMultiGenomeProject().getFileContentManager().getFile(chromosome, chromosomeContent);
		List<String> results = null;
		try {
			results = file.getReader().query(chromosome.getName(), referencePosition, referencePosition);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if ((results != null) && (results.size() > 0)) {
			return new VCFLine(results.get(0), file.getHeader());
		}

		return null;
	}


	/**
	 * @return true if the {@link Variant} is an alternative, false otherwise
	 */
	public boolean isAlternative () {
		return (this instanceof InsertionVariant) || (this instanceof DeletionVariant) || (this instanceof SNPVariant);
	}


	/**
	 * @return true if the {@link Variant} is a {@link ReferenceVariant}, false otherwise
	 */
	public boolean isReference () {
		return (this instanceof ReferenceVariant);
	}


	/**
	 * @return true if the {@link Variant} is a {@link NoCallVariant}, false otherwise
	 */
	public boolean isNoCall () {
		return (this instanceof NoCallVariant);
	}


	/**
	 * @return true if the {@link Variant} is a {@link MixVariant}, false otherwise
	 */
	public boolean isMix () {
		return (this instanceof MixVariant);
	}


	/**
	 * A variant is dominant according to the following order of importance:
	 * - is an alternative
	 * - is a reference
	 * - is a no call
	 * - is a mix of variants
	 * 
	 * If the given variant is null, the current variant is dominant.
	 * If both, current and given, variants are not at the same position, none of them is dominant.
	 * If both, current and given, variants have the same type, both of them are dominants.
	 * 
	 * @param variant the variant to compare
	 * @return	true if the current variant is dominant compare to the given variant, false otherwise
	 */
	public boolean isDominant (Variant variant) {
		Boolean result = false;

		if (variant == null) {
			result = true;
		} else if (getStart() == variant.getStart()) {
			if (	(isAlternative() && variant.isAlternative()) ||
					(isReference() && variant.isReference()) ||
					(isNoCall() && variant.isNoCall()) ||
					(isMix() && variant.isMix())){
				result = true;
			} else if (isAlternative() && (variant.isMix() || variant.isReference()  || variant.isNoCall())) {
				result = true;
			} else if (isReference() && (variant.isNoCall() || variant.isMix())) {
				result = true;
			} else if (isNoCall() && variant.isMix()) {
				result = true;
			}
		}

		return result;
	}
}
