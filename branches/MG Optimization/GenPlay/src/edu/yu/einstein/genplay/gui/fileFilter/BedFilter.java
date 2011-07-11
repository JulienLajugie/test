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
 *     Author: Julien Lajugie <julien.lajugie@einstein.yu.edu>
 *     Website: <http://genplay.einstein.yu.edu>
 *******************************************************************************/
package edu.yu.einstein.genplay.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A BinListAsBedGraphWriter {@link FileFilter}
 * @author Julien Lajugie
 * @version 0.1
 */
public final class BedFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -7589647687815666483L; // generated ID
	public static final String[] EXTENSIONS = {"bed"};
	public static final String DESCRIPTION = "Bed Files (*.bed)";
	
	
	/**
	 * Creates an instance of {@link BedFilter}
	 */
	public BedFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
