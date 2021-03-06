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
package edu.yu.einstein.genplay.core.multiGenome.data.display.variant;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFLine;
import edu.yu.einstein.genplay.core.multiGenome.data.display.content.MGChromosomeContent;
import edu.yu.einstein.genplay.dataStructure.enums.VariantType;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class ReferenceVariant extends MultiNucleotideVariant {

	/** Default serial version ID */
	private static final long serialVersionUID = 2296711907593637593L;
	private VariantType type;


	/**
	 * Constructor of {@link ReferenceVariant}
	 * @param chromosomeContent the {@link MGChromosomeContent}
	 * @param referencePositionIndex the index position on the reference genome
	 * @param start start position
	 * @param stop stop position
	 * @param type the type of the reference variant
	 */
	public ReferenceVariant(MGChromosomeContent chromosomeContent, int referencePositionIndex, int start, int stop, VariantType type) {
		super(chromosomeContent, referencePositionIndex, start, stop);
		this.type = type;
	}


	/**
	 * @return a description of the {@link Variant}
	 */
	@Override
	public String getDescription () {
		String description = super.getDescription();
		description += " TYPE: " + type.name() + ";";
		return description;
	}


	@Override
	public VariantType getType() {
		return type;
	}


	@Override
	public String getVariantSequence() {
		VCFLine line = getVCFLine();
		if (line != null) {
			String chain = "-";
			String ref = line.getREF();
			if (type == VariantType.REFERENCE_SNP) {
				chain = ref.substring(0, 1);
			} else if (ref.length() > 1) {
				chain = ref.substring(1);
			}
			return chain;
		}
		return super.getVariantSequence();
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Override
	protected void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		super.readObject(in);
		type = (VariantType) in.readObject();
	}


	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	@Override
	protected void writeObject(ObjectOutputStream out) throws IOException {
		super.writeObject(out);
		out.writeObject(type);
	}
}
