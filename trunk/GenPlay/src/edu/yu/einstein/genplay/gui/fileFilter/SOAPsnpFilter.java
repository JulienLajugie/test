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
package edu.yu.einstein.genplay.gui.fileFilter;

import javax.swing.filechooser.FileFilter;


/**
 * A {@link FileFilter} for the SOAPsnp files.
 * @author Julien Lajugie
 * @version 0.1
 */
public class SOAPsnpFilter extends ExtendedFileFilter {

	private static final long serialVersionUID = -8560834231830725642L; // generated ID
	/** Valid extensions */
	public static final String[] EXTENSIONS = {"SNP"};
	/** File type description */
	public static final String DESCRIPTION = "SOAP SNP files (*.soapsnp)";


	/**
	 * Creates an instance of {@link SOAPsnpFilter}
	 */
	public SOAPsnpFilter() {
		super(EXTENSIONS, DESCRIPTION);
	}
}
