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
package edu.yu.einstein.genplay.dataStructure.enums;


/**
 * Enumeration of the operations available to flatten pileups
 * @author Julien Lajugie
 * @author Nicolas Fourel
 */
public enum ScoreOperation {

	/**
	 * Addition operation
	 */
	ADDITION ("Addition"),
	/**
	 * Subtraction operation
	 */
	SUBTRACTION ("Subtraction"),
	/**
	 * Multiplication operation
	 */
	MULTIPLICATION ("Multiplication"),
	/**
	 * Division operation
	 */
	DIVISION ("Division"),
	/**
	 * Average operation
	 */
	AVERAGE ("Average"),
	/**
	 * Maximum operation
	 */
	MAXIMUM ("Maximum"),
	/**
	 * Minimum operation
	 */
	MINIMUM ("Minimum");


	/**
	 * @return The score operations available to resolve a pileup
	 */
	public static ScoreOperation[] getPileupOperations() {
		ScoreOperation[] pileupOperations = {ADDITION, AVERAGE, MAXIMUM, MINIMUM};
		return pileupOperations;
	}


	/** Name of the method of score calculation */
	private final String name;


	/**
	 * Private constructor. Creates an instance of a {@link ScoreOperation}
	 * @param name name of the method of score calculation
	 */
	private ScoreOperation(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return name;
	}
}
