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
package edu.yu.einstein.genplay.core.multiGenome.VCF.filtering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.yu.einstein.genplay.core.enums.InequalityOperators;
import edu.yu.einstein.genplay.core.multiGenome.VCF.VCFHeaderType.VCFHeaderType;
import edu.yu.einstein.genplay.core.multiGenome.display.variant.VariantInterface;

/**
 * @author Nicolas Fourel
 * @version 0.1
 */
public class QualFilter implements NumberIDFilterInterface {

	/** Generated default serial ID*/
	private static final long serialVersionUID = 3099777763400649421L;
	private static final int  SAVED_FORMAT_VERSION_NUMBER = 0;			// saved format version
	
	private InequalityOperators		inequation01;
	private InequalityOperators 	inequation02;
	private Float 					value01;
	private Float 					value02;

	
	/**
	 * Method used for serialization
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(SAVED_FORMAT_VERSION_NUMBER);
		out.writeObject(inequation01);
		out.writeObject(inequation02);
		out.writeFloat(value01);
		out.writeFloat(value02);
	}


	/**
	 * Method used for unserialization
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.readInt();
		inequation01 = (InequalityOperators) in.readObject();
		inequation02 = (InequalityOperators) in.readObject();
		value01 = in.readFloat();
		value02 = in.readFloat();
	}


	@Override
	public VCFHeaderType getID() {
		return null;
	}


	@Override
	public void setID(VCFHeaderType id) {}


	@Override
	public InequalityOperators getInequation01() {
		return inequation01;
	}


	@Override
	public void setInequation01(InequalityOperators inequation01) {
		this.inequation01 = inequation01;
	}


	@Override
	public InequalityOperators getInequation02() {
		return inequation02;
	}


	@Override
	public void setInequation02(InequalityOperators inequation02) {
		this.inequation02 = inequation02;
	}


	@Override
	public Float getValue01() {
		return value01;
	}


	@Override
	public void setValue01(Float value01) {
		this.value01 = value01;
	}


	@Override
	public Float getValue02() {
		return value02;
	}


	@Override
	public void setValue02(Float value02) {
		this.value02 = value02;
	}


	@Override
	public String toStringForDisplay() {
		String text = "";

		text += "x " + inequation01 + " " + value01;
		if (inequation02 != null && value02 != null) {
			text += " and ";
			text += "x " + inequation02 + " " + value02;
		}
		
		return text;
	}


	@Override
	public String getErrors() {
		String error = "";

		if (inequation01 == null || inequation01.equals(" ")) {
			error += "First inequation invalid;";
		}

		if (value01 == null) {
			error += "First value invalid;";
		}

		if (inequation02 != null && inequation02.equals(" ") && value02 != null) {
			error += "Second sign inequation missing";
		}

		if (inequation02 != null && !inequation02.equals(" ") && value02 == null) {
			error += "Second value missing";
		}

		if (error.equals("")) {
			return null;
		} else {
			return error;
		}
	}

	
	/*/**
	 * Compare to float in order to define if they correlate the inequation.
	 * @param inequation		an inequation
	 * @param referenceValue	a first value
	 * @param valueToCompare	a second value
	 * @return					true if both values correlate the inequation, false otherwise.
	 */
	/*private boolean isValid (InequalityOperators inequation, Float referenceValue, Float valueToCompare) {
		boolean valid = false;
		
		if (valueToCompare < 0) {
			valueToCompare = valueToCompare * -1;
		}
		
		int result = valueToCompare.compareTo(referenceValue);
		
		if (inequation == InequalityOperators.EQUAL) {
			if (result == 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR) {
			if (result > 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.SUPERIOR_OR_EQUAL) {
			if (result >= 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR) {
			if (result < 0) {
				valid = true;
			}
		} else if (inequation == InequalityOperators.INFERIOR_OR_EQUAL) {
			if (result <= 0) {
				valid = true;
			}
		}
		
		return valid;
	}*/
	
	
	@Override
	public void setCategory(String category) {}
	
	
	@Override
	public String getCategory () {
		return null;
	}

	
	@Override
	public boolean isValid(VariantInterface variant) {
		return false;
	}


	@Override
	public boolean isValid(Object value) {
		return false;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj){
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		
		// object must be Test at this point
		QualFilter test = (QualFilter)obj;
		return
		inequation01.toString().equals(test.getInequation01().toString()) &&
		inequation02.toString().equals(test.getInequation02().toString()) &&
		value01 == test.getValue01() &&
		value02 == test.getValue02();
	}
}