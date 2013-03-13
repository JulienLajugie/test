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
package edu.yu.einstein.genplay.gui.action.project;

import java.awt.Component;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.yu.einstein.genplay.core.RNAPosToDNAPos.GeneRelativeToGenomePosition;
import edu.yu.einstein.genplay.core.manager.project.ProjectManager;
import edu.yu.einstein.genplay.dataStructure.enums.RNAToDNAResultType;
import edu.yu.einstein.genplay.gui.action.TrackListActionWorker;
import edu.yu.einstein.genplay.gui.dialog.RNAPosToDNAPosOutputFileTypeDialog;
import edu.yu.einstein.genplay.gui.fileFilter.BedFilter;
import edu.yu.einstein.genplay.gui.fileFilter.BedGraphFilter;
import edu.yu.einstein.genplay.gui.fileFilter.GdpGeneFilter;
import edu.yu.einstein.genplay.util.Utils;

/**
 * Replaces positions relative to a reference (RNA) to DNA positions
 * @author Julien Lajugie
 * @version 0.1
 */
public final class PARNAPosToDNAPos extends TrackListActionWorker<Void> {

	private static final long serialVersionUID = 8927411528919859767L; // generated ID
	private static final String DESCRIPTION = "Replace positions relative to a reference (RNA) to DNA positions"; // tooltip
	private static final String ACTION_NAME = "RNA To DNA Reference"; // action name
	private final Component 	parent; 			// parent component
	private RNAToDNAResultType	outputFileType; 	// output file type

	/**
	 * key of the action in the {@link ActionMap}
	 */
	public static final String ACTION_KEY = PARNAPosToDNAPos.class.getName();


	/**
	 * Creates an instance of {@link PARNAPosToDNAPos}
	 * 
	 * @param parent
	 *            parent component
	 */
	public PARNAPosToDNAPos(Component parent) {
		super();
		this.parent = parent;
		putValue(NAME, ACTION_NAME);
		putValue(ACTION_COMMAND_KEY, ACTION_KEY);
		putValue(SHORT_DESCRIPTION, DESCRIPTION);
	}


	@Override
	protected Void processAction() throws Exception {
		File fileRef;
		File fileData;
		File fileOutput = null;

		String defaultDirectory = ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory();
		FileNameExtensionFilter textFileFilter = new FileNameExtensionFilter("Text file (*.txt)", "txt");
		FileFilter[] fileFilters1 = { textFileFilter, new BedGraphFilter() };
		fileData = Utils.chooseFileToLoad(parent, "Select Coverage File", defaultDirectory, fileFilters1, true);
		if (fileData != null) {
			FileFilter[] fileFilters2 = { textFileFilter, new BedFilter() };
			fileRef = Utils.chooseFileToLoad(parent, "Select Reference File", defaultDirectory, fileFilters2, true);
			if (fileRef != null) {

				RNAPosToDNAPosOutputFileTypeDialog rnaToDnaDialog = new RNAPosToDNAPosOutputFileTypeDialog();
				int rtddResult = rnaToDnaDialog.showDialog(getRootPane());
				outputFileType = rnaToDnaDialog.getSelectedOutputFileType();

				if (rtddResult == RNAPosToDNAPosOutputFileTypeDialog.APPROVE_OPTION) {
					if (outputFileType == RNAToDNAResultType.GDP) {
						JFileChooser jfc = new JFileChooser(ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory());
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						jfc.setDialogTitle("Select Output GDP File");
						jfc.addChoosableFileFilter(new GdpGeneFilter());
						jfc.setAcceptAllFileFilterUsed(false);
						int returnVal = jfc.showSaveDialog(parent);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							fileOutput = Utils.addExtension(jfc.getSelectedFile(), "gdp");
						}
					} else {
						JFileChooser jfc = new JFileChooser(ProjectManager.getInstance().getProjectConfiguration().getDefaultDirectory());
						jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
						jfc.setDialogTitle("Select Output BGR File");
						jfc.addChoosableFileFilter(new BedGraphFilter());
						jfc.setAcceptAllFileFilterUsed(false);
						int returnVal = jfc.showSaveDialog(parent);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							fileOutput = Utils.addExtension(jfc.getSelectedFile(), "bgr");
						}
					}
					if (fileOutput != null) {
						final GeneRelativeToGenomePosition grtgp = new GeneRelativeToGenomePosition(fileData, fileRef, fileOutput, outputFileType);
						notifyActionStart("Generating Output Files", 1, false);
						grtgp.rePosition();
					}
				}
			}
		}
		return null;
	}
}